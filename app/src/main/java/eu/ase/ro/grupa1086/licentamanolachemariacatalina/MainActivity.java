package eu.ase.ro.grupa1086.licentamanolachemariacatalina;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import eu.ase.ro.grupa1086.licentamanolachemariacatalina.account.SignIn;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.account.SignUp;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.User;

public class MainActivity extends AppCompatActivity {

    Button btnSignIn;
    Button btnSignUp;
    TextView slogan;
    FirebaseDatabase database;
    DatabaseReference users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Toast.makeText(getApplicationContext(), "S-a conectat la firebase", Toast.LENGTH_LONG).show();
//        initializareComponente();
//        firebaseService = FirebaseService.getInstance();
//        firebaseService.addFoodListener(dataChangeCallback());

        btnSignIn = findViewById(R.id.btnSignIn);
        btnSignUp = findViewById(R.id.btnSignUp);
        database = FirebaseDatabase.getInstance();

        slogan = findViewById(R.id.tvSlogan);
//        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/RobotoSerif-Black.ttf");
//        slogan.setTypeface(face);


        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignUp.class);
                startActivity(intent);
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignIn.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {

            Dexter.withActivity(this)
                    .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    .withListener(new PermissionListener() {
                        @Override
                        public void onPermissionGranted(PermissionGrantedResponse response) {
                            users = database.getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                            users.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    User user1 = snapshot.getValue(User.class);
                                    if (user1.getIsDriver() == 1) {
                                        startActivity(new Intent(getApplicationContext(), DriverMenu.class));
                                    } else {
                                        startActivity(new Intent(getApplicationContext(), PrincipalMenu.class));
                                    }
                                    Log.i("userFromDatabase", String.valueOf(user1.getIsDriver()));
                                    finish();
                                }


                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }

                        @Override
                        public void onPermissionDenied(PermissionDeniedResponse response) {
                            Toast.makeText(MainActivity.this, "Trebuie acordata permisiunea pentru a putea folosi aplicatia", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                        }
                    }).check();

        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(PrincipalMenu menu) {
//        getMenuInflater().inflate(R.menu.main_menu, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.optiune1:
//                Intent intentFood = new Intent(this, AddFood.class);
//                startActivity(intentFood);
//                break;
//            case R.id.optiune2:
//                Intent intentCategory = new Intent(this, AddCategory.class);
//                startActivity(intentCategory);
//                break;
//            case R.id.optiune3:
//                Toast.makeText(this, "Ai selectat opt3", Toast.LENGTH_LONG).show();
//                return true;
//        }
//        return false;
//    }


}