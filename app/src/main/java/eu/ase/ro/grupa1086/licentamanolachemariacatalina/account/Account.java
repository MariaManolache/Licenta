package eu.ase.ro.grupa1086.licentamanolachemariacatalina.account;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.appcompat.app.AppCompatActivity;

import eu.ase.ro.grupa1086.licentamanolachemariacatalina.AccountDetails;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.AddressesList;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.OrdersList;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.PrincipalMenu;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.R;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.User;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.databinding.ActivityAccountBinding;

public class Account extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityAccountBinding binding;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    TextView name;
    Button btnLogout;
    TextView verifyMessage;
    Button btnVerifyEmail;
    AlertDialog.Builder resetAlert;
    private DatabaseReference databaseReferenceCart;
    LayoutInflater inflater;
    BottomNavigationView bottomNavigationView;

    FrameLayout firstFrameLayout;
    FrameLayout secondFrameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);


        //Firebase
        firebaseAuth = FirebaseAuth.getInstance();


        firebaseUser = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
//                assert user != null;

                if(user != null)
                    name.setText("Bine ai venit, " + user.getName() + "!");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Account.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        verifyMessage = findViewById(R.id.tvVerifyEmail);
        btnVerifyEmail = findViewById(R.id.btnVerify);
        name = findViewById(R.id.tvName);

        firstFrameLayout = findViewById(R.id.firstFrameLayout);
        secondFrameLayout = findViewById(R.id.secondFrameLayout);

        inflater = this.getLayoutInflater();

        resetAlert = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogStyle));

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
                        Toast.makeText(Account.this, "Email de verificare trimis", Toast.LENGTH_LONG).show();
                        verifyMessage.setVisibility(View.GONE);
                        btnVerifyEmail.setVisibility(View.GONE);
                    }
                });
            }
        });

        btnLogout = findViewById(R.id.btnSignOut);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(), SignIn.class));
                finish();
            }
        });

        firstFrameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myProfile = new Intent(Account.this, AccountDetails.class);
                startActivity(myProfile);
            }
        });

        secondFrameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myAddresses = new Intent(Account.this, AddressesList.class);
                myAddresses.putExtra("origin", "account");
                startActivity(myAddresses);
            }
        });

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.account);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (item.getItemId()) {
                    case R.id.restaurantsMenu:
                        startActivity(new Intent(getApplicationContext(), PrincipalMenu.class));
                        //finish();
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.account:
                        return true;
                    case R.id.orders:
                        startActivity(new Intent(getApplicationContext(), OrdersList.class));
                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
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
                                    Toast.makeText(Account.this, "Cont sters", Toast.LENGTH_LONG).show();
                                    databaseReference.removeValue();
                                    databaseReferenceCart.removeValue();
                                    FirebaseAuth.getInstance().signOut();
                                    startActivity(new Intent(getApplicationContext(), SignIn.class));
                                    finish();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(Account.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                    finish();
                                }
                            });

                        }
                    }).setNegativeButton("Nu", null)
                    .create().show();
        }
        return false;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_account);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


}