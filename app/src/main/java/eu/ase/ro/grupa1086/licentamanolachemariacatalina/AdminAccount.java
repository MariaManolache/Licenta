package eu.ase.ro.grupa1086.licentamanolachemariacatalina;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import eu.ase.ro.grupa1086.licentamanolachemariacatalina.admin.DriverAccountsList;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.User;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.order.RestaurantOrders;

public class AdminAccount extends AppCompatActivity {

    private EditText etAdminEmail;
    private EditText etAdminPassword;
    private Button btnConnect;


    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;
    FirebaseDatabase database;
    DatabaseReference admin;
    LayoutInflater inflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_account);

        initialiseComponents();
    }

    private void initialiseComponents() {

        etAdminEmail = findViewById(R.id.etAdminEmail);
        etAdminPassword = findViewById(R.id.etAdminPassword);

        btnConnect = findViewById(R.id.btnConnect);

        progressBar = findViewById(R.id.progressBarSignIn);

        firebaseAuth = FirebaseAuth.getInstance();

        inflater = this.getLayoutInflater();

        database = FirebaseDatabase.getInstance();

        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etAdminEmail.getText().toString();
                String password = etAdminPassword.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    etAdminEmail.setError("Email-ul administratorului este necesar pentru autentificare");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    etAdminPassword.setError("O parolă este necesară pentru autentificare");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);


                firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            onAuthSuccess(task.getResult().getUser());
                            Toast.makeText(AdminAccount.this, "Autentificare realizată cu succes", Toast.LENGTH_LONG).show();
//                            startActivity(new Intent(getApplicationContext(), MainMenu.class));


                        } else {
                            Toast.makeText(AdminAccount.this, "Eroare la autentificare" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });

    }

    private void onAuthSuccess(FirebaseUser user) {
        if (user != null) {
            admin = database.getReference("admin").child(user.getUid());
            startActivity(new Intent(getApplicationContext(), DriverAccountsList.class));
            finish();
        }


    }

}