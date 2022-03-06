package eu.ase.ro.grupa1086.licentamanolachemariacatalina;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.Food;

public class FoodInfo extends AppCompatActivity {

    TextView name, price, description;
    ImageView image;
    CollapsingToolbarLayout collapsingToolbarLayout;
    FloatingActionButton btnShoppingCart;
    ImageButton btnAdd;
    ImageButton btnRemove;
    TextView quantity;

    String foodId = "";

    FirebaseDatabase database;
    DatabaseReference foodList;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    List<Food> cartFood;

    DatabaseReference cart;

    Food food;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_info);
        setTitle("Felul de mancare dorit");

        //Firebase
        database = FirebaseDatabase.getInstance();
        foodList = database.getReference("food");

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        String id = user.getUid();
        cart = database.getInstance().getReference("carts").child(id).child("foodList");

        //View
        btnAdd = findViewById(R.id.btnPlus);
        btnRemove = findViewById(R.id.btnMinus);
        quantity = findViewById(R.id.tvQuantity);

        btnShoppingCart = findViewById(R.id.btnShoppingCart);

        description = findViewById(R.id.foodDescription);
        name = findViewById(R.id.foodName);
        price = findViewById(R.id.foodPrice);
        image = findViewById(R.id.foodImage);

        collapsingToolbarLayout = findViewById(R.id.collapsing);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExtendedAppbar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppbar);

        //foodId from Intent
        if(getIntent() != null) {
            foodId = getIntent().getStringExtra("id");
        }
        if(!foodId.isEmpty()) {
            getFoodInfo(foodId);
        }

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int newQuantity = food.getQuantity() + 1;
                food.setQuantity(newQuantity);
                quantity.setText(String.valueOf(food.getQuantity()));
            }
        });

        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int newQuantity = food.getQuantity() - 1;
                if(newQuantity >= 1)
                { food.setQuantity(newQuantity);
                quantity.setText(String.valueOf(food.getQuantity()));}
            }
        });

        btnShoppingCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cart.setValue(food).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Produsul a fost adaugat in cos", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Eroare la adaugarea produsului in cos", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }

    private void getFoodInfo(String foodId) {
        foodList.child(foodId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                food = snapshot.getValue(Food.class);

                //image
                Picasso.with(getBaseContext()).load(food.getImage())
                        .into(image);

                collapsingToolbarLayout.setTitle(food.getName());

                price.setText(String.valueOf(food.getPrice()));
                name.setText(food.getName());
                description.setText(food.getDescription());
                food.setQuantity(Integer.parseInt(quantity.getText().toString()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                food.setQuantity(0);
                quantity.setText(food.getQuantity());
            }
        });
    }
}