package eu.ase.ro.grupa1086.licentamanolachemariacatalina.account;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

import eu.ase.ro.grupa1086.licentamanolachemariacatalina.MainActivity;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.R;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.User;

public class ResetPassword extends AppCompatActivity {
    private EditText newPassword;
    private EditText newConfirmedPassword;
    private EditText oldPassword;
    private Button btnResetPassword;
    private FirebaseUser user;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private List<User> usersList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        oldPassword = findViewById(R.id.etOldPassword);
        newPassword = findViewById(R.id.etResetPassword);
        newConfirmedPassword = findViewById(R.id.etConfirmPassword);
        btnResetPassword = findViewById(R.id.btnResetPassword);

        user = FirebaseAuth.getInstance().getCurrentUser();
        firebaseAuth = FirebaseAuth.getInstance();

        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(oldPassword.getText().toString().isEmpty()) {
                    oldPassword.setError("Camp necesar pentru schimbarea parolei");
                }
                if(newPassword.getText().toString().isEmpty()) {
                    newPassword.setError("Camp necesar pentru schimbarea parolei");
                    return;
                }
                if(newPassword.length() < 6) {
                    newPassword.setError("Parola trebuie sa aiba cel putin 6 caractere");
                    return;
                }
                if(newConfirmedPassword.getText().toString().isEmpty()) {
                    newConfirmedPassword.setError("Camp necesar pentru schimbarea parolei");
                    return;
                }
                if(!newPassword.getText().toString().equals(newConfirmedPassword.getText().toString())) {
                    newConfirmedPassword.setError("Parolele nu sunt identice");
                    return;
                }

                AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), oldPassword.getText().toString());
                user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            user.updatePassword(newPassword.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {

                                    String id = user.getUid();
                                    String email = user.getEmail();
                                    databaseReference = FirebaseDatabase.getInstance().getReference("users").child(id);

                                    databaseReference.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            User user2 = snapshot.getValue(User.class);

                                            assert user2 != null;
                                            if(user2.getId().equals(user.getUid())) {
                                                User newUserPassword = new User(user2.getId(), user2.getEmail(), user2.getName(), newPassword.getText().toString(), user2.getPhoneNumber(), 0);

//                                                HashMap<String, String> hashMap = new HashMap<>();
//                                                hashMap.put("id", user2.getId());
//                                                hashMap.put("name", user2.getName());
//                                                hashMap.put("email", user2.getEmail());
//                                                hashMap.put("phoneNumber", user2.getPhoneNumber());
//                                                hashMap.put("password", newPassword.getText().toString());

                                                databaseReference.setValue(newUserPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful()) {
                                                            Toast.makeText(ResetPassword.this, "Parola a fost resetata", Toast.LENGTH_LONG).show();
                                                            firebaseAuth.signOut();
                                                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                                            finish();
//                                                startActivity(new Intent(getApplicationContext(), MainMenu.class));
//                                                finish();
                                                        } else {
                                                            //Toast.makeText(ResetPassword.this, "Eroare la modificarea parolei" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                                        }
                                                    }
                                                });
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                   // User user2 = ;
                                   // String name = FirebaseDatabase.getInstance().getReference("users").child(id).get().
//                                    HashMap<String, String> hashMap = new HashMap<>();
//                                    hashMap.put("id", id);
//                                    hashMap.put("name", name);
//                                    hashMap.put("email", email);

                                    Toast.makeText(ResetPassword.this, "Parola a fost resetata", Toast.LENGTH_LONG).show();

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    //Toast.makeText(ResetPassword.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                        }  else {
                            //Toast.makeText(ResetPassword.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
    }
}