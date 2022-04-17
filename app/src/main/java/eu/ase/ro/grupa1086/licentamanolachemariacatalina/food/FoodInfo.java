package eu.ase.ro.grupa1086.licentamanolachemariacatalina.food;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.service.controls.actions.CommandAction;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
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
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.ase.ro.grupa1086.licentamanolachemariacatalina.CommentsList;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.R;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.Rating;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.Cart;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.Food;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.User;

public class FoodInfo extends FragmentActivity {

    TextView name, price, description;
    ImageView image;
    CollapsingToolbarLayout collapsingToolbarLayout;
    FloatingActionButton btnShoppingCart;
    ImageButton btnAdd;
    ImageButton btnRemove;
    TextView quantity;
    RatingBar ratingBar;
    FloatingActionButton btnRating;
    float ratingValue = 0.0f;
    int nbOfRatings = 0;

    String foodId = "";

    FirebaseDatabase database;
    DatabaseReference foodList;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    List<Food> cartFood;

    DatabaseReference cart;
    DatabaseReference cartItem;
    DatabaseReference users;
    String userName;

    String quantityFromCart;
    String orderId;
    TextView tvQuantity;
    int quantityFromOrdersList;

    Food food;
    TextView comments;
    LinearLayout linearLayoutComments;

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
        users = database.getReference("users");
        users.child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                userName = user.getName();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //View
        btnAdd = findViewById(R.id.btnPlus);
        btnRemove = findViewById(R.id.btnMinus);
        quantity = findViewById(R.id.tvQuantity);

        btnShoppingCart = findViewById(R.id.btnShoppingCart);

        description = findViewById(R.id.foodDescription);
        name = findViewById(R.id.foodName);
        price = findViewById(R.id.foodPrice);
        image = findViewById(R.id.foodImage);
        ratingBar = findViewById(R.id.ratingBar);
        tvQuantity = findViewById(R.id.quantity);

        btnRating = findViewById(R.id.btnRating);
        comments = findViewById(R.id.comments);
        linearLayoutComments = findViewById(R.id.linearLayoutComments);

        collapsingToolbarLayout = findViewById(R.id.collapsing);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExtendedAppbar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppbar);

        //foodId from Intent
        if (getIntent() != null && getIntent().getExtras() != null) {
            String origin = getIntent().getExtras().getString("origin");
            if (origin != null && origin.equals("activityFoodList")) {
                foodId = getIntent().getStringExtra("id");
                if (!foodId.isEmpty()) {
                    getFoodInfo(foodId);
                }
            }
            if(origin != null && origin.equals("banner")) {
                foodId = getIntent().getStringExtra("foodId");
                if (!foodId.isEmpty()) {
                    getFoodInfo(foodId);
                }
            }
            if (origin != null && origin.equals("activityShoppingCart")) {
                foodId = getIntent().getStringExtra("idCartItem");
                Log.i("idCart", foodId);
                if (!foodId.isEmpty()) {
                    quantityFromCart = getIntent().getStringExtra("quantity");
                    Log.i("idCart", quantityFromCart);
                    getFoodInfoFromCart(foodId);
                }
            }
            if (origin != null && origin.equals("ordersList")) {

                orderId = getIntent().getStringExtra("orderId");
                foodId = getIntent().getStringExtra("foodId");
                quantityFromOrdersList = getIntent().getIntExtra("quantity", 1);
                quantity.setText(String.valueOf(quantityFromOrdersList));

                foodList.child(foodId).child("ratings").child(orderId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.exists()) {
                            btnRating.setVisibility(View.GONE);
                        } else {
                            btnRating.setVisibility(View.VISIBLE);
                        }
                        btnAdd.setVisibility(View.GONE);
                        btnRemove.setVisibility(View.GONE);
                        tvQuantity.setVisibility(View.VISIBLE);
                        btnShoppingCart.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


                getFoodInfo(foodId);

                ratingValue = 0.0f;
                nbOfRatings = 0;
                foodList.child(foodId).child("ratings").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Rating rating = dataSnapshot.getValue(Rating.class);
                            ratingValue += rating.getRateValue();
                            nbOfRatings++;
                        }

                        if (nbOfRatings >= 1) {
                            ratingValue /= nbOfRatings;
                        }

                        ratingBar.setRating(ratingValue);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
            linearLayoutComments.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent commentsList = new Intent(FoodInfo.this, CommentsList.class);
                    commentsList.putExtra("foodId", foodId);
                    commentsList.putExtra("userName", userName);
                    startActivity(commentsList);
                }
            });

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


            }
        });

//        ratingValue = 0.0f;
//        nbOfRatings = 0;
//        foodList.child(foodId).child("ratings").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                    Rating rating = dataSnapshot.getValue(Rating.class);
//                    ratingValue += rating.getRateValue();
//                    Log.i("ratingValue", String.valueOf(ratingValue));
//                    nbOfRatings++;
//                    Log.i("nbOfRatings", String.valueOf(nbOfRatings));
//                }
//
//                if (nbOfRatings >= 1) {
//                    ratingValue /= nbOfRatings;
//                    Log.i("ratingValue", String.valueOf(ratingValue));
//                }
//
//                ratingBar.setRating(ratingValue);
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

    }

    private void getFoodInfoFromCart(String foodId) {
        cart.child(foodId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                food = snapshot.getValue(Food.class);

                //image
                Picasso.with(getBaseContext()).load(food.getImage())
                        .into(image);

                collapsingToolbarLayout.setTitle(food.getName());

                price.setText(food.getPrice() + " lei");
                name.setText(food.getName());
                description.setText(food.getDescription());
                quantity.setText(quantityFromCart);
                food.setQuantity(Integer.parseInt(String.valueOf(quantity.getText())));

                ratingValue = 0.0f;
                nbOfRatings = 0;
                foodList.child(foodId).child("ratings").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Rating rating = dataSnapshot.getValue(Rating.class);
                            ratingValue += rating.getRateValue();
                            nbOfRatings++;
                        }

                        if (nbOfRatings >= 1) {
                            ratingValue /= nbOfRatings;
                        }

                        ratingBar.setRating(ratingValue);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                if (btnRating.getVisibility() == View.VISIBLE) {
                    btnRating.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showRatingDialog();
                        }
                    });
                }

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

                price.setText(food.getPrice() + " lei");
                name.setText(food.getName());
                description.setText(food.getDescription());
                food.setQuantity(Integer.parseInt(quantity.getText().toString()));

                ratingValue = 0.0f;
                nbOfRatings = 0;
                foodList.child(foodId).child("ratings").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Rating rating = dataSnapshot.getValue(Rating.class);
                            ratingValue += rating.getRateValue();
                            nbOfRatings++;
                        }

                        if (nbOfRatings >= 1) {
                            ratingValue /= nbOfRatings;
                        }

                        ratingBar.setRating(ratingValue);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                if (btnRating.getVisibility() == View.VISIBLE) {
                    btnRating.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showRatingDialog();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                food.setQuantity(1);
                quantity.setText(String.valueOf(food.getQuantity()));
            }
        });
    }

    private void showRatingDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogStyle));
        builder.setTitle("Rating produs");
        builder.setMessage("Lasa un rating pentru acest produs");

        View itemView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.rating, null);

        RatingBar ratingBar = itemView.findViewById(R.id.ratingBarPopUp);
        EditText etComment = itemView.findViewById(R.id.etComment);

        builder.setView(itemView);

        builder.setNegativeButton("ANULARE", ((dialog, which) -> {
            dialog.dismiss();
        }));

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Rating rating = new Rating();
                rating.setName(userName);
                rating.setOrderId(orderId);
                rating.setComment(etComment.getText().toString());
                rating.setRateValue(ratingBar.getRating());
                Map<String, Object> serverTimeStamp = new HashMap<>();
                serverTimeStamp.put("timeStamp", ServerValue.TIMESTAMP);
                rating.setCommentTimeStamp(serverTimeStamp);

//                List<Rating> ratings = new ArrayList<>();
//                ratings.add(rating);

                ratingValue = 0.0f;
                nbOfRatings = 0;
                foodList.child(foodId).child("ratings").child(orderId).setValue(rating);
                foodList.child(foodId).child("ratings").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Rating rating = dataSnapshot.getValue(Rating.class);
                            ratingValue += rating.getRateValue();
                            nbOfRatings++;
                        }

                        if (nbOfRatings >= 1) {
                            ratingValue /= nbOfRatings;
                        }

                        ratingBar.setRating(ratingValue);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}