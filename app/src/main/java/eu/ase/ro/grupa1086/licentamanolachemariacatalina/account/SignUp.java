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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import eu.ase.ro.grupa1086.licentamanolachemariacatalina.Home;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.ShoppingCart;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.Cart;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.Food;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.Order;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.firebase.FirebaseService;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.MainMenu;
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

    private List<User> usersList;
    private List<String> user_list;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        initializareComponente();


        //firebaseService = FirebaseService.getInstance();
        //firebaseService.addFoodListener(dataChangeCallback());

    }

    private void initializareComponente() {
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

        Food food = new Food("01", "01");
        foodList.add(food);

        if(firebaseAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), MainMenu.class));
            finish();
        }

        //btnRegister.setOnClickListener(addUser());
        btnRegister.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = etName.getText().toString().trim();
                final String email = etEmail.getText().toString().trim();
                final String phoneNumber = etPhoneNumber.getText().toString().trim();
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
                    etPassword.setError("O parola este necesara pentru crearea contului");
                    return;
                }

                if(password.length() < 4) {
                    etPassword.setError("Parola trebuie sa aiba cel putin 4 caractere");
                    return;
                }

                if(TextUtils.isEmpty(name)) {
                    etPhoneNumber.setError("Numarul de telefon al utilizatorului este necesar pentru crearea contului");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                // inregistrarea user-ului in firebase

                firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {

                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            String id = user.getUid();
                            databaseReference = FirebaseDatabase.getInstance().getReference("users").child(id);
                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("id", id);
                            hashMap.put("name", name);
                            hashMap.put("email", email);
                            hashMap.put("phoneNumber", phoneNumber);
                            hashMap.put("password", password);
                            databaseReference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()) {
                                        Toast.makeText(SignUp.this, "Cont creat", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(getApplicationContext(), Home.class));
                                        finish();
                                    } else {
                                        Toast.makeText(SignUp.this, "Eroare la crearea contului" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);
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
                                    if(task.isSuccessful()) {
                                        Toast.makeText(SignUp.this, "Cos de cumparaturi creat", Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(SignUp.this, "Eroare la crearea contului" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                        } else {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(SignUp.this, "Eroare la crearea contului" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }));

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SignIn.class));
            }
        });
    }

    private View.OnClickListener addUser() {
        return new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(isValid()) {
                    User user = updateUserFromView(null);
                    firebaseService.insertUser(user);
                }
            }
        };
    }

    private User updateUserFromView(String id) {
        User user  = new User();
        user.setId(id);
        user.setName(etName.getText().toString());
        user.setEmail(etEmail.getText().toString());
        user.setPassword(etPassword.getText().toString());
        return user;
    }

    private boolean isValid() {
        if(etName.getText() == null || etName.getText().toString().trim().isEmpty() ||
                etEmail.getText() == null || etEmail.getText().toString().trim().isEmpty() || etEmail.getText().length() < 3 ||
                etPassword.getText() == null || etPassword.getText().toString().trim().isEmpty() || etPassword.getText().length() < 3) {


            Toast.makeText(getApplicationContext(),"Validarea nu a trecut",
                    Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

}
