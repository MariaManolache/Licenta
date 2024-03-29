package eu.ase.ro.grupa1086.licentamanolachemariacatalina.restaurant;

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

import eu.ase.ro.grupa1086.licentamanolachemariacatalina.R;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.User;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.order.RestaurantOrders;

public class RestaurantLogin extends AppCompatActivity {

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
        setContentView(R.layout.activity_restaurant_login);

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
                    etPassword.setError("O parolă este necesară pentru autentificare");
                    return;
                }

                if (password.length() < 6) {
                    etPassword.setError("Parola trebuie să aibă cel puțin 4 caractere");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);


                firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            onAuthSuccess(task.getResult().getUser());
//                            Toast.makeText(RestaurantLogin.this, "Autentificare realizata cu succes", Toast.LENGTH_LONG).show();
//                            startActivity(new Intent(getApplicationContext(), MainMenu.class));


                        } else {
                            Toast.makeText(RestaurantLogin.this, "Eroare la autentificare", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });

    }

    private void onAuthSuccess(FirebaseUser user) {
        if (user != null) {
            users = database.getReference("restaurantAccounts").child(user.getUid());
            restaurants = database.getReference("restaurants");
            users.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User connectedUser = snapshot.getValue(User.class);

                    if(!snapshot.exists()) {
                        Toast.makeText(getApplicationContext(), "Utilizatorul introdus nu se află în baza de date", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                        FirebaseAuth.getInstance().signOut();
                    } else {
                        Query query = null;
                        if (connectedUser != null) {
                            query = restaurants.orderByChild("id").equalTo(connectedUser.getId());
                            query.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(!snapshot.exists()) {
                                        restaurants.child(user.getUid()).child("name").setValue(connectedUser.getName()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Toast.makeText(RestaurantLogin.this, "Autentificare realizată cu success", Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }

                        //Toast.makeText(RestaurantLogin.this, "Autentificare realizată cu succes", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(getApplicationContext(), RestaurantOrders.class));
                        overridePendingTransition(R.anim.slide_up, R.anim.slide_nothing);
                        finish();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
}