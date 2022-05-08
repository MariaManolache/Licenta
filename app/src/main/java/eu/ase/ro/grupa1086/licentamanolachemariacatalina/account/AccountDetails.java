package eu.ase.ro.grupa1086.licentamanolachemariacatalina.account;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import eu.ase.ro.grupa1086.licentamanolachemariacatalina.R;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.User;

public class AccountDetails extends AppCompatActivity {

    TextView tvName;
    TextView tvEmail;
    TextView tvPhoneNumber;

    FirebaseDatabase database;
    DatabaseReference users;
    FirebaseUser user;

    ImageView changeName;
    ImageView changePhoneNumber;
    ImageView changeEmail;

    AlertDialog.Builder resetName;
    AlertDialog.Builder resetPhoneNumber;
    AlertDialog.Builder resetEmail;
    LayoutInflater inflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_details);

        tvName = findViewById(R.id.tvName);
        tvPhoneNumber = findViewById(R.id.phoneNumber);
        tvEmail = findViewById(R.id.email);

        database = FirebaseDatabase.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        users = database.getReference().child("users");

        changeName = findViewById(R.id.changeName);
        changePhoneNumber = findViewById(R.id.changePhoneNumber);
        changeEmail = findViewById(R.id.changeEmail);

        resetName = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogStyle));
        resetPhoneNumber = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogStyle));
        resetEmail = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogStyle));

        users.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User currentUser = snapshot.getValue(User.class);
                tvName.setText(currentUser.getName());
                tvEmail.setText(currentUser.getEmail());
                tvPhoneNumber.setText(currentUser.getPhoneNumber());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        inflater = this.getLayoutInflater();

        changeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = inflater.inflate(R.layout.reset_name_pop_up, null);
                resetName.setTitle("Modificarea numelui de utilizator")
                        .setPositiveButton("Confirmare", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                EditText name = view.findViewById(R.id.etName);
                                if(name.getText().toString().isEmpty()) {
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

        changePhoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = inflater.inflate(R.layout.reset_phone_number_pop_up, null);
                resetPhoneNumber.setTitle("Modificarea numarului de telefon")
                        .setPositiveButton("Confirmare", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                EditText phoneNumber = view.findViewById(R.id.etPhoneNumber);
                                if(phoneNumber.getText().toString().isEmpty()) {
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

        changeEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = inflater.inflate(R.layout.reset_email_pop_up, null);
                resetEmail.setTitle("Modificarea adresei de email")
                        .setPositiveButton("Confirmare", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                EditText email = view.findViewById(R.id.etEmail);
                                if(email.getText().toString().isEmpty()) {
                                    email.setError("Campul este necesar pentru modificarea adresei de email");
                                    return;
                                }

                                user.updateEmail(email.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(AccountDetails.this, "Adresa de email a fost modificata", Toast.LENGTH_LONG).show();
                                        users.child(user.getUid()).child("email").setValue(email.getText().toString());

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(AccountDetails.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });

                            }
                        }).setNegativeButton("Anuleaza", null)
                        .setView(view)
                        .create().show();
            }
        });
    }
}