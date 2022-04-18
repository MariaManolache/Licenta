package eu.ase.ro.grupa1086.licentamanolachemariacatalina;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.Address;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.AddressList;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.Cart;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.Food;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.User;

public class PhoneNumberValidation extends AppCompatActivity {

    private Button btnValidate;
    private EditText etValidationCode;
    private DatabaseReference databaseReference;
    private DatabaseReference databaseReferenceShoppingCart;
    private DatabaseReference databaseReferenceAddresses;
    private FirebaseAuth firebaseAuth;
    private ProgressBar progressBar;
    private List<Food> foodList;
    private List<Address> addressesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_number_validation);

        btnValidate = findViewById(R.id.btnValidate);
        etValidationCode = findViewById(R.id.etValidationCode);
        progressBar = findViewById(R.id.progressBarPhoneValidation);
        firebaseAuth = FirebaseAuth.getInstance();
        foodList = new ArrayList<>();
        addressesList = new ArrayList<>();

        String name = getIntent().getStringExtra("name");
        String email = getIntent().getStringExtra("email");
        String phoneNumber = getIntent().getStringExtra("phoneNumber");
        String password = getIntent().getStringExtra("password");
        String authVerification = getIntent().getStringExtra("authCredential");

        btnValidate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String validationCode = etValidationCode.getText().toString();

                if (validationCode.isEmpty()) {
                    etValidationCode.setError(getString(R.string.necesity_validation_code));

                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(authVerification, validationCode);
//                    signInWithPhoneAuthCredential(credential);
//                }


                    firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                int isDriver = 0;
                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                String id = user.getUid();
                                databaseReference = FirebaseDatabase.getInstance().getReference("users").child(id);
                                HashMap<String, String> hashMap = new HashMap<>();
                                hashMap.put("id", id);
                                hashMap.put("name", name);
                                hashMap.put("email", email);
                                hashMap.put("phoneNumber", phoneNumber);
                                hashMap.put("password", password);
                                hashMap.put("isDriver", String.valueOf(isDriver));


                                databaseReference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(PhoneNumberValidation.this, "Cont creat", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(getApplicationContext(), PrincipalMenu.class));
                                            finish();
                                        } else {
                                            Toast.makeText(PhoneNumberValidation.this, "Eroare la crearea contului" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });

                                databaseReferenceShoppingCart = FirebaseDatabase.getInstance().getReference("carts").child(id);
                                Cart cart = new Cart(id, foodList);
                                HashMap<String, String> hashMapCarts = new HashMap<>();
                                hashMapCarts.put("id", id);
                                hashMapCarts.put("food", foodList.toString());
                                databaseReferenceShoppingCart.setValue(cart).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(PhoneNumberValidation.this, "Cos de cumparaturi creat", Toast.LENGTH_LONG).show();
                                        } else {
                                            Toast.makeText(PhoneNumberValidation.this, "Eroare la crearea contului" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });

                                databaseReferenceAddresses = FirebaseDatabase.getInstance().getReference("addresses").child(id);
                                AddressList addressList = new AddressList(id, addressesList);
                                databaseReferenceAddresses.setValue(addressList).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(PhoneNumberValidation.this, "Lista de adrese creata", Toast.LENGTH_LONG).show();
                                        } else {
                                            Toast.makeText(PhoneNumberValidation.this, "Eroare la crearea listei de adrese" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });

                            } else {
                                Toast.makeText(PhoneNumberValidation.this, "Eroare la crearea contului" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }



                        }
                    });
                }
            }
        });
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(PhoneNumberValidation.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(PhoneNumberValidation.this, "Numar de telefon validat", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), Home.class));
                            finish();

                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(PhoneNumberValidation.this, "Eroare. Numarul de telefon nu a fost validat", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    }
                });
    }
}