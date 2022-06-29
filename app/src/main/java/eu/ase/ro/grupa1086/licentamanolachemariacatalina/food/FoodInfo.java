package eu.ase.ro.grupa1086.licentamanolachemariacatalina.food;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.Status;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.rating.CommentsList;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.R;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.rating.Rating;
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
    String restaurantId = "";
    String userId = "";

    FirebaseDatabase database;
    DatabaseReference foodList;
    DatabaseReference orders;
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
    String status;

    Food food;
    TextView comments;
    LinearLayout linearLayoutComments;

    ImageView close;

    String origin = null;
    boolean exists = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_info);
        setTitle("Felul de mancare dorit");

        //Firebase
        database = FirebaseDatabase.getInstance();
        foodList = database.getReference("food");
        orders = database.getReference("orders");

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        String id = null;
        if (user != null) {
            id = user.getUid();
        }
        cart = database.getReference("carts").child(id).child("foodList");
        users = database.getReference("users");
        users.child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user != null) {
                    userName = user.getName();
                }
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

        close = findViewById(R.id.closeActivity);
        //foodId from Intent
        if (getIntent() != null && getIntent().getExtras() != null) {

            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                    overridePendingTransition(R.anim.slide_nothing, R.anim.slide_down);
                }
            });

            origin = getIntent().getExtras().getString("origin");
            if (origin != null && origin.equals("activityFoodList")) {
                foodId = getIntent().getStringExtra("id");
                restaurantId = getIntent().getStringExtra("restaurantId");
                if (!foodId.isEmpty()) {
                    getFoodInfo(foodId, restaurantId);
                }
            }
            if (origin != null && origin.equals("banner")) {
                foodId = getIntent().getStringExtra("foodId");
                restaurantId = getIntent().getStringExtra("restaurantId");
                if (!foodId.isEmpty()) {
                    getFoodInfo(foodId, restaurantId);
                }
            }
            if (origin != null && origin.equals("activityShoppingCart")) {
                foodId = getIntent().getStringExtra("idCartItem");
                restaurantId = getIntent().getStringExtra("restaurantId");
                Log.i("idCart", foodId);
                if (!foodId.isEmpty()) {
                    quantityFromCart = getIntent().getStringExtra("quantity");
                    Log.i("idCart", quantityFromCart);
                    getFoodInfoFromCart(foodId, restaurantId);
                }
            }
            if (origin != null && origin.equals("ordersList")) {

                orderId = getIntent().getStringExtra("orderId");
                foodId = getIntent().getStringExtra("foodId");
                restaurantId = getIntent().getStringExtra("restaurantId");
                userId = getIntent().getStringExtra("userId");
                quantityFromOrdersList = getIntent().getIntExtra("quantity", 1);
                status = getIntent().getStringExtra("status");
                quantity.setText(String.valueOf(quantityFromOrdersList));

                foodList.child(restaurantId).child(foodId).child("ratings").child(orderId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.exists() || !status.equals(Status.finalizata.toString())) {
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


                getFoodInfo(foodId, restaurantId);

                ratingValue = 0.0f;
                nbOfRatings = 0;
                foodList.child(foodId).child("ratings").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Rating rating = dataSnapshot.getValue(Rating.class);
                            if (rating != null) {
                                ratingValue += rating.getRateValue();
                            }
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
                    commentsList.putExtra("origin", "foodInfo");
                    commentsList.putExtra("foodId", foodId);
                    commentsList.putExtra("restaurantId", restaurantId);
                    commentsList.putExtra("userName", userName);
                    startActivity(commentsList);
                    overridePendingTransition(R.anim.slide_up, R.anim.slide_nothing);
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
                if(origin != null && origin.equals("activityShoppingCart")) {
                    cart.child(food.getId()).child("quantity").setValue(newQuantity);
                }

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
                    if(origin != null && origin.equals("activityShoppingCart")) {
                        cart.child(food.getId()).child("quantity").setValue(newQuantity);
                    }
                }
            }
        });

        btnShoppingCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                exists = false;
                String id = user.getUid();
                cartItem = cart.child(food.getId());

//                Log.i("cartItem", String.valueOf(cart.child("quantity")));

//                cart.addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                            Food newAddedFood = dataSnapshot.getValue(Food.class);
//                            if (newAddedFood != null && food.getId().equals(newAddedFood.getId())) {
//                                int previousQuantity = newAddedFood.getQuantity();
//                                int newQuantity = food.getQuantity();
//                                int totalQuantity = previousQuantity + newQuantity;
//                                food.setQuantity(totalQuantity);
//                                cart.child(food.getId()).setValue(food);
//                                exists = true;
//                                quantity.setText(String.valueOf(totalQuantity));
//                            }
//                        }
//
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });

//                HashMap<String, String> hashMap = new HashMap<>();
//                hashMap.put("id", food.getId());
//                hashMap.put("name", food.getName());
//                hashMap.put("description", food.getDescription());
//                hashMap.put("image", food.getImage());
//                hashMap.put("price", String.valueOf(food.getPrice()));
//                hashMap.put("quantity", quantity.getText().toString());
//                hashMap.put("restaurantId", food.getRestaurantId());

                //if(exists == false) {
                    cartItem.setValue(food).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Produsul a fost adăugat în coș", Toast.LENGTH_SHORT).show();
                                quantity.setText(String.valueOf(food.getQuantity()));
                            } else {
                                Toast.makeText(getApplicationContext(), "Eroare la adăugarea produsului în coș", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                //}


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

    private void getFoodInfoFromCart(String foodId, String restaurantId) {
        cart = database.getReference("carts").child(user.getUid()).child("foodList");
        cart.child(foodId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //if (food != null) {
                Food selectedFood = snapshot.getValue(Food.class);
                //food = snapshot.getValue(Food.class);

                //image
                if (selectedFood != null) {


                    Picasso.with(getBaseContext()).load(selectedFood.getImage()).placeholder(R.drawable.loading)
                            .into(image);

                    collapsingToolbarLayout.setTitle(selectedFood.getName());

                    price.setText(selectedFood.getPrice() + " lei");
                    name.setText(selectedFood.getName());
                    description.setText(selectedFood.getDescription());
                    quantity.setText(quantityFromCart);
                    selectedFood.setQuantity(Integer.parseInt(String.valueOf(quantity.getText())));

                    food = selectedFood;
                    food.setPreparationTime(selectedFood.getPreparationTime());

                    ratingValue = 0.0f;
                    nbOfRatings = 0;
                    foodList.child(restaurantId).child(foodId).child("ratings").addValueEventListener(new ValueEventListener() {
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
            }

            //}

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                food.setQuantity(1);
                // quantity.setText(food.getQuantity());
            }


        });
    }

    private void getFoodInfo(String foodId, String restaurantId) {
        foodList.child(restaurantId).child(foodId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                food = snapshot.getValue(Food.class);

                //image
                Picasso.with(getBaseContext()).load(food.getImage()).placeholder(R.drawable.loading)
                        .into(image);

                collapsingToolbarLayout.setTitle(food.getName());

                price.setText(food.getPrice() + " lei");
                name.setText(food.getName());
                description.setText(food.getDescription());
                food.setQuantity(Integer.parseInt(quantity.getText().toString()));

                ratingValue = 0.0f;
                nbOfRatings = 0;
                foodList.child(restaurantId).child(foodId).child("ratings").addValueEventListener(new ValueEventListener() {
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
        builder.setMessage("Lasă un rating pentru acest produs");

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
                foodList.child(restaurantId).child(foodId).child("ratings").child(orderId).setValue(rating);
                foodList.child(restaurantId).child(foodId).child("ratings").addValueEventListener(new ValueEventListener() {
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