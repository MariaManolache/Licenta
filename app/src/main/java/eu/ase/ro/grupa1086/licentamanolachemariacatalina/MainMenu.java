package eu.ase.ro.grupa1086.licentamanolachemariacatalina;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import eu.ase.ro.grupa1086.licentamanolachemariacatalina.account.ResetPassword;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.account.SignIn;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.User;

public class MainMenu extends AppCompatActivity {

    private Button btnLogout;
    private TextView verifyMessage;
    private Button btnVerifyEmail;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private DatabaseReference databaseReferenceCart;

    private TextView name;
    AlertDialog.Builder resetAlert;

    LayoutInflater inflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
//                assert user != null;

                if(user != null)
                name.setText(user.getName());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainMenu.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        btnLogout = findViewById(R.id.btnLogout);

        verifyMessage = findViewById(R.id.tvVerifyEmail);
        btnVerifyEmail = findViewById(R.id.btnVerify);
        name = findViewById(R.id.tvName);

        inflater = this.getLayoutInflater();

        resetAlert = new AlertDialog.Builder(this);

        if(!firebaseAuth.getCurrentUser().isEmailVerified()) {
            btnVerifyEmail.setVisibility(View.VISIBLE);
            verifyMessage.setVisibility(View.VISIBLE);
        }

        btnVerifyEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // trimitere email de verificare
                firebaseAuth.getCurrentUser().sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(MainMenu.this, "Email de verificare trimis", Toast.LENGTH_LONG).show();
                        verifyMessage.setVisibility(View.GONE);
                        btnVerifyEmail.setVisibility(View.GONE);
                    }
                });
            }
        });

        btnLogout.setOnClickListener(logout());
    }

    public View.OnClickListener logout() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(), SignIn.class));
                finish();
            }
        };

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.reset_password_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
       if(item.getItemId() == R.id.resetPassword) {
           startActivity(new Intent(getApplicationContext(), ResetPassword.class));
       }
       if(item.getItemId() == R.id.deleteAccount) {
            resetAlert.setTitle("Doresti sa iti stergi contul?")
                    .setMessage("Esti sigur? Actiunea nu este reversibila.")
                    .setPositiveButton("Da", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {


                            FirebaseUser user = firebaseAuth.getCurrentUser();

                            String id = user.getUid();
                            String email = user.getEmail();
                            databaseReference = FirebaseDatabase.getInstance().getReference("users").child(id);

                            databaseReferenceCart = FirebaseDatabase.getInstance().getReference("carts").child(id);

//                            databaseReference.addValueEventListener(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                    User user2 = snapshot.getValue(User.class);
//
//                                    assert user2 != null;
//
//                                    if(user2.getId().equals(user.getUid())) {
////                                        HashMap<String, String> hashMap = new HashMap<>();
////                                        hashMap.put("id", user2.getId());
////                                        hashMap.put("name", user2.getName());
////                                        hashMap.put("email", user2.getEmail());
////                                        hashMap.put("password", user2.getPassword());
//
//                                        databaseReference.removeValue();
//                                        FirebaseAuth.getInstance().signOut();
//
//                                    }
//                                }
//
//                                @Override
//                                public void onCancelled(@NonNull DatabaseError error) {
//                                    Toast.makeText(MainMenu.this, error.getMessage(), Toast.LENGTH_SHORT).show();
//                                }
//                            });

                            user.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(MainMenu.this, "Cont sters", Toast.LENGTH_LONG).show();
                                    databaseReference.removeValue();
                                    databaseReferenceCart.removeValue();
                                    FirebaseAuth.getInstance().signOut();
                                    startActivity(new Intent(getApplicationContext(), SignIn.class));
                                    finish();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(MainMenu.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });

                        }
                    }).setNegativeButton("Nu", null)
                    .create().show();
       }
        return false;
    }
}