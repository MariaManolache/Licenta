package eu.ase.ro.grupa1086.licentamanolachemariacatalina;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import eu.ase.ro.grupa1086.licentamanolachemariacatalina.account.SignIn;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.account.SignUp;

public class MainActivity extends AppCompatActivity {

   Button btnSignIn;
   Button btnSignUp;
   TextView slogan;

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
        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), Home.class));
            finish();
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
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