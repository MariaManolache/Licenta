package eu.ase.ro.grupa1086.licentamanolachemariacatalina;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import eu.ase.ro.grupa1086.licentamanolachemariacatalina.cart.ItemClickListener;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.cart.SwipeHelper;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.Food;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.Restaurant;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.food.FoodInfo;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.viewHolder.CartViewHolder;

public class ShoppingCart extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference cart;
    DatabaseReference restaurants;

    FirebaseAuth firebaseAuth;
    FirebaseUser user;

    TextView tvTotalPrice;
    Button btnPlaceOrder;
    String id;
    Float total = 0.0f;

    ImageView emptyCart;
    TextView tvEmptyCart;
    Button btnStartShopping;

    List<Food> cartList = new ArrayList<Food>();

    FirebaseRecyclerAdapter<Food, CartViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);

        //Firebase
        database = FirebaseDatabase.getInstance();

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        id = user.getUid();
        cart = database.getInstance().getReference("carts").child(id).child("foodList");

        emptyCart = findViewById(R.id.emptyCart);
        tvEmptyCart = findViewById(R.id.tvEmptyCart);
        btnStartShopping = findViewById(R.id.btnStartShopping);

        if(cart.equals(null)) {
            emptyCart.setVisibility(View.VISIBLE);
            tvEmptyCart.setVisibility(View.VISIBLE);
        }

        //RecyclerView
        recyclerView = findViewById(R.id.cartList);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        SwipeHelper swipeHelper = new SwipeHelper(getApplicationContext(), recyclerView, 200) {
            @Override
            public void instantiateMyButton(RecyclerView.ViewHolder viewHolder, List<MyButton> buffer) {
                buffer.add(new MyButton(getApplicationContext(), "Stergere", 30, 0, Color.parseColor("#FF3C30"),
                        position -> {
                            Toast.makeText(getApplicationContext(), "Produsul a fost eliminat din cos", Toast.LENGTH_SHORT).show();
                            Food deletedFood = adapter.getItem(position);
                            total -= deletedFood.getPrice() * deletedFood.getQuantity();
                            DatabaseReference cartItem = cart.child(deletedFood.getId());
                            cartItem.removeValue();
                            cartList.remove(deletedFood);
                            recyclerView.setAdapter(adapter);

                            if(cartList.size() == 0) {
                                emptyCart.setVisibility(View.VISIBLE);
                                tvEmptyCart.setVisibility(View.VISIBLE);
                                btnPlaceOrder.setEnabled(false);
                            }
                }));
            }
        };

        tvTotalPrice = findViewById(R.id.total);
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder);


        Set<String> restaurantsId = new HashSet<>();

//        cartList = (List<Food>) cart;
//        for (Food f: cartList) {
//            Log.i("info2", f.toString());
//        }

//       loadListFood();
//
        loadFood();


    }


    public List<Food> loadFood() {

        cart.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                //String value = dataSnapshot.getValue(String.class);
                //Log.d("hello", "Value is: " + value);
                List<Food> foods = new ArrayList<>();

                total = 0.0f;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Food food = snapshot.getValue(Food.class);
                    Log.i("hello", food.toString());
                    cartList.add(food);
                    total += food.getPrice() * food.getQuantity();
                }

//                for (Food f: foods) {
//                    cartList.add(f);
//                }
//                cartList = foods;

                if(cartList.size() == 0) {
                    emptyCart.setVisibility(View.VISIBLE);
                    tvEmptyCart.setVisibility(View.VISIBLE);
                    btnStartShopping.setVisibility(View.VISIBLE);
                    btnStartShopping.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent principalMenu = new Intent(ShoppingCart.this, PrincipalMenu.class);
                            startActivity(principalMenu);
                            finish();
                        }
                    });
                    btnPlaceOrder.setEnabled(false);
                } else {
                    emptyCart.setVisibility(View.GONE);
                    tvEmptyCart.setVisibility(View.GONE);
                    btnStartShopping.setVisibility(View.GONE);
                    btnPlaceOrder.setEnabled(true);
                }

                for (int i = 0; i < cartList.size(); i++) {
                    loadListFood(cartList.get(i).getRestaurantId());
                    Log.i("hello", cartList.get(i).toString());
                }

                btnPlaceOrder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent placeOrder = new Intent(ShoppingCart.this, PlaceOrder.class);
                        placeOrder.putExtra("total", total);
                        startActivity(placeOrder);
                    }
                });
                // cart.removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.i("hello", "Failed to read value.", error.toException());
            }
        });
        return cartList;
    }

    private void loadListFood(String restaurantId) {

        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("carts")
                .child(id)
                .child("foodList")
                //.orderByChild("restaurantId").equalTo(restaurantId)
                .limitToLast(50);

        Log.i("hello", query.toString());

        FirebaseRecyclerOptions<Food> options =
                new FirebaseRecyclerOptions.Builder<Food>()
                        .setQuery(query, Food.class)
                        .build();


        adapter = new FirebaseRecyclerAdapter<Food, CartViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull Food model) {

                int quantity = 0;
                float price = 0.0f;

                //holder.restaurantName.setText(model.getRestaurantId());
                holder.tvCartName.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage())
                        .into(holder.imgCartCount);

                holder.tvCartPrice.setText(String.valueOf(model.getPrice()));
                holder.tvCartQuantity.setText(String.valueOf(model.getQuantity()));
                holder.tvCartValue.setText(String.valueOf(model.getPrice() * model.getQuantity()));
                holder.tvQuantityDisplay.setText(String.valueOf(model.getQuantity()));

                restaurants = database.getInstance().getReference("restaurants").child(model.getRestaurantId());

                restaurants.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        Restaurant restaurant = dataSnapshot.getValue(Restaurant.class);
                        holder.restaurantName.setText(restaurant.getName());
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                        Log.i("hello", "Failed to read value.", error.toException());
                    }
                });

                //total += Float.parseFloat(String.valueOf(holder.tvCartValue.getText()));


                holder.btnAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int quantity = Integer.parseInt(holder.tvQuantityDisplay.getText().toString()) + 1;
                        holder.tvQuantityDisplay.setText(String.valueOf(quantity));
                        model.setQuantity(quantity);

                        Food updatedFood = new Food(model.getId(), model.getName(), model.getPrice(), model.getDescription(),
                                model.getQuantity(), model.getImage(), model.getRestaurantId());

                        cart.child(model.getId()).setValue(updatedFood).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    total += model.getPrice();
                                    Toast.makeText(ShoppingCart.this, "Cantitate modificata", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(ShoppingCart.this, "Eroare la modificarea cantitatii" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                });

                holder.btnRemove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int quantity = Integer.parseInt(holder.tvQuantityDisplay.getText().toString()) - 1;
                        if (quantity >= 1) {
                            holder.tvQuantityDisplay.setText(String.valueOf(quantity));
                            model.setQuantity(quantity);

                            Food updatedFood = new Food(model.getId(), model.getName(), model.getPrice(), model.getDescription(),
                                    model.getQuantity(), model.getImage(), model.getRestaurantId());

                            cart.child(model.getId()).setValue(updatedFood).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        total -= model.getPrice();
                                        Toast.makeText(ShoppingCart.this, "Cantitate modificata", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(ShoppingCart.this, "Eroare la modificarea cantitatii" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }
                    }
                });

                if (total > 0) {
                    tvTotalPrice.setText(total + " LEI");
                } else {
                    tvTotalPrice.setText("0 LEI");
                }

                final Food local = model;
                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                        Toast.makeText(getApplicationContext(), local.getName(), Toast.LENGTH_LONG).show();
                        Intent foodInfo = new Intent(ShoppingCart.this, FoodInfo.class);
                        foodInfo.putExtra("idCartItem", local.getId());
                        foodInfo.putExtra("quantity", String.valueOf(local.getQuantity()));
                        foodInfo.putExtra("origin", "activityShoppingCart");
                        startActivity(foodInfo);

                    }
                });

            }

            @NonNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.cart_layout, parent, false);
                view.setMinimumWidth(parent.getMeasuredWidth());

                return new CartViewHolder(view);
            }
        };

        adapter.startListening();
        recyclerView.setAdapter(adapter);

    }



    @Override
    protected void onStart() {
        super.onStart();
    }
}