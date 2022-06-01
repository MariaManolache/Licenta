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

import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.User;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.principalmenus.DriverMenu;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.principalmenus.PrincipalMenu;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.principalmenus.RestaurantAccount;

public class DriverLogIn extends AppCompatActivity {
    private EditText etEmail;
    private EditText etPassword;
    private Button btnConnect;


    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;
    FirebaseDatabase database;
    DatabaseReference users;
    DatabaseReference restaurants;
    LayoutInflater inflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_log_in);

        initialiseComponents();
    }

    private void initialiseComponents() {

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);

        btnConnect = findViewById(R.id.btnConnect);

        progressBar = findViewById(R.id.progressBarSignIn);

        firebaseAuth = FirebaseAuth.getInstance();

        inflater = this.getLayoutInflater();

        database = FirebaseDatabase.getInstance();

        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    etEmail.setError("Email-ul utilizatorului este necesar pentru autentificare");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    etPassword.setError("O parola este necesara pentru autentificare");
                    return;
                }

                if (password.length() < 6) {
                    etPassword.setError("Parola trebuie sa aiba cel putin 4 caractere");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);


                firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            onAuthSuccess(task.getResult().getUser());
//                            startActivity(new Intent(getApplicationContext(), MainMenu.class));


                        } else {
                            Toast.makeText(DriverLogIn.this, "Eroare la autentificare" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });

    }

    private void onAuthSuccess(FirebaseUser user) {
        if (user != null) {
            users = database.getReference("users").child(user.getUid()).child("isDriver");
            users.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        int isDriver = snapshot.getValue(Integer.class);
                        if (isDriver == 1) {
                            Toast.makeText(DriverLogIn.this, "Autentificare realizata cu succes", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(getApplicationContext(), DriverMenu.class));
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), "Utilizatorul introdus nu se afla in baza de date", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
}