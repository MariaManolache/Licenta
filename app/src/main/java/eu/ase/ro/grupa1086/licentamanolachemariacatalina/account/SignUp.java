package eu.ase.ro.grupa1086.licentamanolachemariacatalina.account;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import eu.ase.ro.grupa1086.licentamanolachemariacatalina.old.Home;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.Food;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.firebase.FirebaseService;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.R;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.User;

public class SignUp extends AppCompatActivity {

    private EditText etName;
    private EditText etEmail;
    private EditText etPhoneNumber;
    private EditText etPassword;
    private Button btnRegister;
    private FirebaseService firebaseService;
    private List<Food> foodList;

    private FirebaseAuth firebaseAuth;
    private ProgressBar progressBar;

    private DatabaseReference databaseReference;
    private DatabaseReference databaseReferenceShoppingCart;

    private TextView signIn;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;

    private List<User> usersList;
    private List<String> user_list;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        initializeComponents();


        //firebaseService = FirebaseService.getInstance();
        //firebaseService.addFoodListener(dataChangeCallback());

    }

    private void initializeComponents() {
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);

        btnRegister = findViewById(R.id.btnRegister);

        firebaseAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBar);

        signIn = findViewById(R.id.tvSignIn);

        user_list = new ArrayList<>();
        foodList = new ArrayList<>();


        if(firebaseAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), Home.class));
            finish();
            overridePendingTransition(R.anim.slide_up, R.anim.slide_nothing);
        }

        //btnRegister.setOnClickListener(addUser());
        btnRegister.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = etName.getText().toString().trim();
                final String email = etEmail.getText().toString().trim();
                final String phoneNumber = "+40" + etPhoneNumber.getText().toString().trim();
                final String password = etPassword.getText().toString().trim();

                if(TextUtils.isEmpty(name)) {
                    etName.setError("Numele utilizatorului este necesar pentru crearea contului");
                    return;
                }

                if(TextUtils.isEmpty(email)) {
                    etEmail.setError("Email-ul utilizatorului este necesar pentru crearea contului");
                    return;
                }

                if(TextUtils.isEmpty(password)) {
                    etPassword.setError("O parolă este necesară pentru crearea contului");
                    return;
                }

                if(password.length() < 4) {
                    etPassword.setError("Parola trebuie sa aibă cel puțin 4 caractere");
                    return;
                }

                if(TextUtils.isEmpty(phoneNumber)) {
                    etPhoneNumber.setError("Numărul de telefon al utilizatorului este necesar pentru crearea contului");
                    return;
                }

                if (phoneNumber.length() != 12) {
                    etPhoneNumber.setError("Numărul de telefon trebuie să aibă formatul corect");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);


//                firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if(task.isSuccessful()) {
//
//                            FirebaseUser user = firebaseAuth.getCurrentUser();
//                            String id = user.getUid();
//                            databaseReference = FirebaseDatabase.getInstance().getReference("users").child(id);
//                            HashMap<String, String> hashMap = new HashMap<>();
//                            hashMap.put("id", id);
//                            hashMap.put("name", name);
//                            hashMap.put("email", email);
//                            hashMap.put("phoneNumber", phoneNumber);
//                            hashMap.put("password", password);

                            callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                @Override
                                public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                                }

                                @Override
                                public void onVerificationFailed(@NonNull FirebaseException e) {
                                    Toast.makeText(SignUp.this, "Eroare la validarea numarului de telefon", Toast.LENGTH_LONG).show();
                                    progressBar.setVisibility(View.GONE);
                                }

                                @Override
                                public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                    super.onCodeSent(s, forceResendingToken);
                                    firebaseAuth.getFirebaseAuthSettings().setAppVerificationDisabledForTesting(true);
                                    Intent phoneValidationIntent = new Intent(SignUp.this, PhoneNumberValidation.class);
//                                    phoneValidationIntent.putExtra("id", id);
                                    phoneValidationIntent.putExtra("name", name);
                                    phoneValidationIntent.putExtra("email", email);
                                    phoneValidationIntent.putExtra("phoneNumber", phoneNumber);
                                    phoneValidationIntent.putExtra("password", password);
                                    phoneValidationIntent.putExtra("authCredential", s);
                                    startActivity(phoneValidationIntent);
                                    overridePendingTransition(R.anim.slide_up, R.anim.slide_nothing);
                                }
                            };


//                            databaseReference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//                                    if(task.isSuccessful()) {
//                                        Toast.makeText(SignUp.this, "Cont creat", Toast.LENGTH_SHORT).show();

                                        PhoneAuthOptions options =
                                                PhoneAuthOptions.newBuilder(firebaseAuth)
                                                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                                                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                                                        .setActivity(SignUp.this)                 // Activity (for callback binding)
                                                        .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
                                                        .build();
                                        PhoneAuthProvider.verifyPhoneNumber(options);

//
//                                    } else {
//                                        Toast.makeText(SignUp.this, "Eroare la crearea contului" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
//                                        progressBar.setVisibility(View.GONE);
//                                    }
//                                }
//                            });
//
//                            databaseReferenceShoppingCart = FirebaseDatabase.getInstance().getReference("carts").child(id);
//                            Cart cart = new Cart(id, foodList);
//                            HashMap<String, String> hashMapCarts = new HashMap<>();
//                            hashMapCarts.put("id", id);
//                            hashMapCarts.put("food", foodList.toString());
//                            databaseReferenceShoppingCart.setValue(cart).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//                                    if(task.isSuccessful()) {
//                                        Toast.makeText(SignUp.this, "Cos de cumparaturi creat", Toast.LENGTH_LONG).show();
//                                    } else {
//                                        Toast.makeText(SignUp.this, "Eroare la crearea cosului de cumparaturi" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
//                                        progressBar.setVisibility(View.VISIBLE);
//                                    }
//                                }
//                            });
//
//                        } else {
//                            progressBar.setVisibility(View.GONE);
//                            Toast.makeText(SignUp.this, "Eroare la crearea contului" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
//                        }
//
//                    }
//                });
            }
        }));


        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SignIn.class));
                overridePendingTransition(R.anim.slide_up, R.anim.slide_nothing);
                finish();
            }
        });
    }


}
