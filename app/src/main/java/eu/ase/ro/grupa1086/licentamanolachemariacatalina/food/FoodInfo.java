package eu.ase.ro.grupa1086.licentamanolachemariacatalina.food;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
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

import java.util.HashMap;
import java.util.List;

import eu.ase.ro.grupa1086.licentamanolachemariacatalina.R;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.Cart;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.Food;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.User;

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
    DatabaseReference cartItem;

    String quantityFromCart;

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
        cart = database.getReference("carts").child(id).child("foodList");

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
        if (getIntent() != null && getIntent().getExtras() != null) {
            String origin = getIntent().getExtras().getString("origin");
            if(origin != null && origin.equals("activityFoodList")){
                foodId = getIntent().getStringExtra("id");
                if (!foodId.isEmpty()) {
                    getFoodInfo(foodId);
                }
            }
            if(origin != null && origin.equals("activityShoppingCart")) {
                foodId = getIntent().getStringExtra("idCartItem");
                Log.i("idCart", foodId);
                if (!foodId.isEmpty()) {
                    quantityFromCart = getIntent().getStringExtra("quantity");
                    Log.i("idCart", quantityFromCart);
                    getFoodInfoFromCart(foodId);
                }
            }
        }


        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int newQuantity = food.getQuantity() + 1;
                food.setQuantity(newQuantity);
                quantity.setText(String.valueOf(food.getQuantity()));
                quantityFromCart = String.valueOf(newQuantity);
            }
        });

        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int newQuantity = food.getQuantity() - 1;
                if (newQuantity >= 1) {
                    food.setQuantity(newQuantity);
                    quantity.setText(String.valueOf(food.getQuantity()));
                    quantityFromCart = String.valueOf(newQuantity);
                }
            }
        });

        btnShoppingCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String id = user.getUid();
                cartItem = cart.child(food.getId());
//                Log.i("cartItem", String.valueOf(cart.child("quantity")));
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("id", food.getId());
                hashMap.put("name", food.getName());
                hashMap.put("description", food.getDescription());
                hashMap.put("image", food.getImage());
                hashMap.put("price", String.valueOf(food.getPrice()));
                hashMap.put("quantity", quantity.getText().toString());
                hashMap.put("restaurantId", food.getRestaurantId());


//                cartItem.addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        Food oldItem = snapshot.getValue(Food.class);
//                        Log.i("oldItem", oldItem.toString());

//                        if (oldItem != null) {
////                            String oldQuantity = cartItem.child("quantity").toString();
////                            Log.i("cantitate", String.valueOf(oldQuantity));
//                            cartItem.child("quantity").setValue(oldItem.getQuantity() + food.getQuantity()).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//                                    if (task.isSuccessful()) {
//                                        Toast.makeText(getApplicationContext(), "Cantitatea de " + food.getName() + " a fost actualizata", Toast.LENGTH_SHORT).show();
//                                    } else {
//                                        Toast.makeText(getApplicationContext(), "Eroare la actualizarea cantitatii", Toast.LENGTH_SHORT).show();
//                                    }
//                                }
//                            });
//                        }
//                        else {
//                        if (oldItem.getQuantity() > 0) {
//                            int oldQuantity = Integer.parseInt(String.valueOf(cartItem.child("quantity")));
//                            cartItem.child("quantity").setValue(oldQuantity + food.getQuantity()).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//                                    if (task.isSuccessful()) {
//                                        Toast.makeText(getApplicationContext(), "Cantitatea de " + food.getName() + " a fost actualizata", Toast.LENGTH_SHORT).show();
//                                    } else {
//                                        Toast.makeText(getApplicationContext(), "Eroare la actualizarea cantitatii", Toast.LENGTH_SHORT).show();
//                                    }
//                                }
//                            });
//
//                        } else {

                            cartItem.setValue(food).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getApplicationContext(), "Produsul a fost adaugat in cos", Toast.LENGTH_SHORT).show();
                                        quantity.setText(String.valueOf(food.getQuantity()));
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Eroare la adaugarea produsului in cos", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                       // }

//                    }

//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//                        Toast.makeText(FoodInfo.this, error.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                });


            }
        });

    }

    private void getFoodInfoFromCart(String foodId) {
        cart.child(foodId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    food = snapshot.getValue(Food.class);
//
//                for(Food cartItem : cartFood) {
//                    if(cartItem.getId().equals(foodId)) {
//                        food = cartItem;
//                    }
//                }

                    //image
                    Picasso.with(getBaseContext()).load(food.getImage())
                            .into(image);

                    collapsingToolbarLayout.setTitle(food.getName());

                    price.setText(String.valueOf(food.getPrice()));
                    name.setText(food.getName());
                    description.setText(food.getDescription());
                    quantity.setText(quantityFromCart);
                    food.setQuantity(Integer.parseInt(String.valueOf(quantity.getText())));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                food.setQuantity(1);
               // quantity.setText(food.getQuantity());
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
                food.setQuantity(1);
                quantity.setText(String.valueOf(food.getQuantity()));
            }
        });
    }
}