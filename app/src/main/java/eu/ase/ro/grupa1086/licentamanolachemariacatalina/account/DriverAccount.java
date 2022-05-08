package eu.ase.ro.grupa1086.licentamanolachemariacatalina.account;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

import eu.ase.ro.grupa1086.licentamanolachemariacatalina.principalmenus.DriverMenu;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.R;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.User;

public class DriverAccount extends AppCompatActivity {

    TextView tvDriverName;
    TextView tvDriverEmail;
    TextView tvDriverPhoneNumber;

    FirebaseDatabase database;
    DatabaseReference users;
    FirebaseUser user;

    ImageView changeDriverName;
    ImageView changeDriverPhoneNumber;
    ImageView changeDriverEmail;

    AlertDialog.Builder resetDriverName;
    AlertDialog.Builder resetDriverPhoneNumber;
    AlertDialog.Builder resetDriverEmail;
    LayoutInflater inflater;

    BottomNavigationView bottomNavigationView;
    Button btnLogout;
    DatabaseReference databaseReference;
    AlertDialog.Builder deleteAlert;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_account);

        tvDriverName = findViewById(R.id.tvDriverName);
        tvDriverPhoneNumber = findViewById(R.id.driverPhoneNumber);
        tvDriverEmail = findViewById(R.id.driverEmail);

        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        users = database.getReference().child("users");

        changeDriverName = findViewById(R.id.changeDriverName);
        changeDriverPhoneNumber = findViewById(R.id.changeDriverPhoneNumber);
        changeDriverEmail = findViewById(R.id.changeDriverEmail);

        resetDriverName = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogStyle));
        resetDriverPhoneNumber = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogStyle));
        resetDriverEmail = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogStyle));

        deleteAlert = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogStyle));

        btnLogout = findViewById(R.id.btnSignOut);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(), SignIn.class));
                finish();
            }
        });


        users.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User currentUser = snapshot.getValue(User.class);
                tvDriverName.setText(currentUser.getName());
                tvDriverEmail.setText(currentUser.getEmail());
                tvDriverPhoneNumber.setText(currentUser.getPhoneNumber());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        inflater = this.getLayoutInflater();

        changeDriverName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = inflater.inflate(R.layout.reset_name_pop_up, null);
                resetDriverName.setTitle("Modificarea numelui de utilizator")
                        .setPositiveButton("Confirmare", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                EditText name = view.findViewById(R.id.etName);
                                if (name.getText().toString().isEmpty()) {
                                    name.setError("Campul este necesar pentru modificarea numelui");
                                    return;
                                }

                                users.child(user.getUid()).child("name").setValue(name.getText().toString());

                            }
                        }).setNegativeButton("Anuleaza", null)
                        .setView(view)
                        .create().show();
            }
        });

        changeDriverPhoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = inflater.inflate(R.layout.reset_phone_number_pop_up, null);
                resetDriverPhoneNumber.setTitle("Modificarea numarului de telefon")
                        .setPositiveButton("Confirmare", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                EditText phoneNumber = view.findViewById(R.id.etPhoneNumber);
                                if (phoneNumber.getText().toString().isEmpty()) {
                                    phoneNumber.setError("Campul este necesar pentru modificarea numarului de telefon");
                                    return;
                                }

                                users.child(user.getUid()).child("phoneNumber").setValue(phoneNumber.getText().toString());

                            }
                        }).setNegativeButton("Anuleaza", null)
                        .setView(view)
                        .create().show();
            }
        });

        changeDriverEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = inflater.inflate(R.layout.reset_email_pop_up, null);
                resetDriverEmail.setTitle("Modificarea adresei de email")
                        .setPositiveButton("Confirmare", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                EditText email = view.findViewById(R.id.etEmail);
                                if (email.getText().toString().isEmpty()) {
                                    email.setError("Campul este necesar pentru modificarea adresei de email");
                                    return;
                                }

                                user.updateEmail(email.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(DriverAccount.this, "Adresa de email a fost modificata", Toast.LENGTH_LONG).show();
                                        users.child(user.getUid()).child("email").setValue(email.getText().toString());

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(DriverAccount.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });

                            }
                        }).setNegativeButton("Anuleaza", null)
                        .setView(view)
                        .create().show();
            }
        });

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.account);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.account:
                        return true;
                    case R.id.orders:
                        startActivity(new Intent(getApplicationContext(), DriverMenu.class));
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
        if (item.getItemId() == R.id.resetPassword) {
            startActivity(new Intent(getApplicationContext(), ResetPassword.class));
        }
        if (item.getItemId() == R.id.deleteAccount) {
            deleteAlert.setTitle("Doresti sa iti stergi contul?")
                    .setMessage("Esti sigur? Actiunea nu este reversibila.")
                    .setPositiveButton("Da", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {


                            FirebaseUser user = firebaseAuth.getCurrentUser();

                            String id = user.getUid();
                            String email = user.getEmail();
                            databaseReference = FirebaseDatabase.getInstance().getReference("users").child(id);

                            user.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(DriverAccount.this, "Cont sters", Toast.LENGTH_LONG).show();
                                    databaseReference.removeValue();
                                    FirebaseAuth.getInstance().signOut();
                                    startActivity(new Intent(getApplicationContext(), SignIn.class));
                                    finish();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(DriverAccount.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                    finish();
                                }
                            });

                        }
                    }).setNegativeButton("Nu", null)
                    .create().show();
        }
        return false;
    }

}