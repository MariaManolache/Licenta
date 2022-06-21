package eu.ase.ro.grupa1086.licentamanolachemariacatalina.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import eu.ase.ro.grupa1086.licentamanolachemariacatalina.R;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.account.PhoneNumberValidation;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.AddressList;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.Cart;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.User;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.principalmenus.PrincipalMenu;

public class NewDriverAccount extends AppCompatActivity {

    private EditText etDriverName;
    private EditText etDriverEmail;
    private EditText etDriverPhoneNumber;
    private Button btnAddNewDriver;
    private DatabaseReference databaseReference;

    private FirebaseAuth firebaseAuth;
    private ProgressBar progressBar;

    private String subject = "Vi s-a creat un cont de livrator în aplicatia Deliver It Right!";
    private String message = "";
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_driver_account);

        initializeComponents();
    }

    private void initializeComponents() {

        etDriverName = findViewById(R.id.etDriverName);
        etDriverEmail = findViewById(R.id.etDriverEmail);
        etDriverPhoneNumber = findViewById(R.id.etDriverPhoneNumber);

        btnAddNewDriver = findViewById(R.id.btnAddNewDriver);

        firebaseAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBar);

        btnAddNewDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = etDriverName.getText().toString().trim();
                final String email = etDriverEmail.getText().toString().trim();
                final String phoneNumber = "+40" + etDriverPhoneNumber.getText().toString().trim();

                if (TextUtils.isEmpty(name)) {
                    etDriverName.setError("Numele livratorului este necesar pentru crearea contului");
                    return;
                }

                if (TextUtils.isEmpty(email)) {
                    etDriverEmail.setError("Email-ul livratorului este necesar pentru crearea contului");
                    return;
                }

                if (TextUtils.isEmpty(name)) {
                    etDriverPhoneNumber.setError("Numarul de telefon al livratorului este necesar pentru crearea contului");
                    return;
                }

                ThreadLocalRandom random = ThreadLocalRandom.current();
                int rand = random.nextInt(8, 12);
                password = PasswordGenerator.process(rand);

                progressBar.setVisibility(View.VISIBLE);

                firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            int isDriver = 1;
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            String id = user.getUid();
                            databaseReference = FirebaseDatabase.getInstance().getReference("users").child(id);
//                                HashMap<String, String> hashMap = new HashMap<>();
//                                hashMap.put("id", id);
//                                hashMap.put("name", name);
//                                hashMap.put("email", email);
//                                hashMap.put("phoneNumber", phoneNumber);
//                                hashMap.put("password", password);
//                                hashMap.put("isDriver", isDriver);


                            User newUser = new User(id, email, name, password, phoneNumber, isDriver);

                            message = "Bine ai venit în echipa Deliver It Right, " + name + "!" + '\n' + '\n' + "Folosește următoarele date pentru a te conecta în aplicație:" + '\n' + '\n' +
                                    "Adresa de email: " + email + '\n' + "Parola: " + password;

                            databaseReference.setValue(newUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(NewDriverAccount.this, "Livrator adăugat", Toast.LENGTH_SHORT).show();
                                        Intent sendEmail = new Intent(Intent.ACTION_SEND);
                                        sendEmail.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
                                        sendEmail.putExtra(Intent.EXTRA_SUBJECT, subject);
                                        sendEmail.putExtra(Intent.EXTRA_TEXT, message);

                                        sendEmail.setType("message/rfc822");

                                        startActivity(Intent.createChooser(sendEmail, "Alege email-ul:"));

                                        finish();
                                    } else {
                                        Toast.makeText(NewDriverAccount.this, "Eroare la crearea contului" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });


                        } else {
                            Toast.makeText(NewDriverAccount.this, "Eroare la crearea contului" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }


                    }
                });


            }
        });
    }
}