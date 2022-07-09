package eu.ase.ro.grupa1086.licentamanolachemariacatalina.account;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import eu.ase.ro.grupa1086.licentamanolachemariacatalina.principalmenus.DriverMenu;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.principalmenus.PrincipalMenu;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.R;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.User;

public class SignIn extends AppCompatActivity {

    private EditText etEmail;
    private EditText etPassword;
    private Button btnConnect;


    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;
    FirebaseDatabase database;
    DatabaseReference users;

    private TextView signUp;
    private TextView resetPassword;

    AlertDialog.Builder resetAlert;

    LayoutInflater inflater;
    User userFromDatabase;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        initialiseComponents();
//        firebaseService = FirebaseService.getInstance();
//        firebaseService.addUserListener(dataChangeCallback());

    }

    private void initialiseComponents() {

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);

        btnConnect = findViewById(R.id.btnConnect);

        progressBar = findViewById(R.id.progressBarSignIn);

        firebaseAuth = FirebaseAuth.getInstance();

        signUp = findViewById(R.id.tvSignUp);

        resetPassword = findViewById(R.id.tvResetPassword);

        inflater = this.getLayoutInflater();

        database = FirebaseDatabase.getInstance();

        resetAlert = new AlertDialog.Builder(this);

        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = inflater.inflate(R.layout.reset_password_pop_up, null);
                resetAlert.setTitle("Resetare parolă").setMessage("Introdu adresa de email pentru resetarea parolei")
                        .setPositiveButton("Resetare", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                //validarea adresei de email
                                EditText email = view.findViewById(R.id.etResetEmailPasswordPopUp);
                                if (email.getText().toString().isEmpty()) {
                                    email.setError("Câmp necesar pentru resetarea parolei");
                                    return;
                                }
//                                else {
//                                    user = firebaseAuth.getCurrentUser();
//                                    String id = user.getUid();
//                                    databaseReference = FirebaseDatabase.getInstance().getReference("users").child(id);
//
//                                    databaseReference.addValueEventListener(new ValueEventListener() {
//                                        @Override
//                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                            User user2 = snapshot.getValue(User.class);
//
//                                            assert user2 != null;
//                                            if(user2.getId().equals(user.getUid())) {
//                                                HashMap<String, String> hashMap = new HashMap<>();
//                                                hashMap.put("id", user2.getId());
//                                                hashMap.put("name", user2.getName());
//                                                hashMap.put("email", user2.getEmail());
//                                                hashMap.put("password", newPassword.getText().toString());
//
//                                                databaseReference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                    @Override
//                                                    public void onComplete(@NonNull Task<Void> task) {
//                                                        if(task.isSuccessful()) {
//                                                            Toast.makeText(ResetPassword.this, "Parola a fost resetata", Toast.LENGTH_LONG).show();
////                                                startActivity(new Intent(getApplicationContext(), MainMenu.class));
////                                                finish();
//                                                        } else {
//                                                            Toast.makeText(ResetPassword.this, "Eroare la modificarea parolei" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
//                                                        }
//                                                    }
//                                                });
//                                            }
//                                        }
//
//                                        @Override
//                                        public void onCancelled(@NonNull DatabaseError error) {
//
//                                        }
//                                    });
//                                }
                                firebaseAuth.fetchSignInMethodsForEmail(email.getText().toString()).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                                        if (task.getResult().getSignInMethods().isEmpty()) {
                                            Toast.makeText(SignIn.this, "Acestui email nu îi este asociat un cont", Toast.LENGTH_LONG).show();
                                        } else {
                                            //trimiterea linkului de resetare
                                            firebaseAuth.sendPasswordResetEmail(email.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Toast.makeText(SignIn.this, "Email trimis pentru resetarea parolei", Toast.LENGTH_LONG).show();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(SignIn.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        }
                                    }
                                });

                            }
                        }).setNegativeButton("Anuleaza", null)
                        .setView(view)
                        .create().show();
            }
        });

//        btnConnect.setOnClickListener(connectUser());
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


//                users = database.getReference("users");
//                users.addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                            User user = dataSnapshot.getValue(User.class);
//                            if (user != null && email.equals(user.getEmail())) {
                                firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            onAuthSuccess(task.getResult().getUser());
//                            startActivity(new Intent(getApplicationContext(), MainMenu.class));


                                        } else {
                                            Toast.makeText(SignIn.this, "Eroare la autentificare", Toast.LENGTH_LONG).show();
                                            progressBar.setVisibility(View.GONE);
                                        }
                                    }
                                });
//                            } else {
//                                Toast.makeText(SignIn.this, "Eroare la autentificare", Toast.LENGTH_LONG).show();
//                                progressBar.setVisibility(View.GONE);
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });

            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SignUp.class));
                overridePendingTransition(R.anim.slide_up, R.anim.slide_nothing);
                finish();
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
                        if (isDriver == 0) {
                            Toast.makeText(SignIn.this, "Autentificare realizată cu succes", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(getApplicationContext(), PrincipalMenu.class));
                            overridePendingTransition(R.anim.slide_up, R.anim.slide_nothing);
                            finish();
                        } else {
                           Toast.makeText(getApplicationContext(), "Utilizatorul introdus nu se află în baza de date", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                            FirebaseAuth.getInstance().signOut();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Utilizatorul introdus nu se află în baza de date", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                        FirebaseAuth.getInstance().signOut();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

//    private Callback<List<User>> dataChangeCallback() {
//        return new Callback<List<User>>() {
//            @Override
//            public void runResultOnUI(List<User> rezultat) {
//                userList.clear();
//                userList.addAll(rezultat);
//            }
//        };
//    }
//
//
//    private View.OnClickListener connectUser() {
//        return new View.OnClickListener(){
//            @Override
//            public void onClick(View v) {
//                isValid();
//                }
//            };
//        };
//
//
//    private boolean isValid() {
//        int k = 0;
//        if(etEmail.getText() == null || etEmail.getText().toString().trim().isEmpty() || etEmail.getText().length() < 3 ||
//                etPassword.getText() == null || etPassword.getText().toString().trim().isEmpty() || etPassword.getText().length() < 3) {
//
//            Toast.makeText(getApplicationContext(),"Validarea nu a trecut",
//                    Toast.LENGTH_LONG).show();
//            return false;
//        } else {
//            for(User u : userList) {
//                if(u.getEmail().equals(etEmail.getText())) {
//                    if(u.getPassword().equals(etPassword.getText())) {
//                        k = 1;
//                        Toast.makeText(getApplicationContext(),"Autentificarea s-a realizat cu succes",
//                                Toast.LENGTH_LONG).show();
//                    }
//                }
//            }
//
//            if(k == 0) {
//                Toast.makeText(getApplicationContext(),"Acest email nu este inregistrat in aplicatie",
//                        Toast.LENGTH_LONG).show();
//            }
//        }
//        return true;
//    }
}
