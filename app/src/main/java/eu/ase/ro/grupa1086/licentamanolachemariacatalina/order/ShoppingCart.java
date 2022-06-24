package eu.ase.ro.grupa1086.licentamanolachemariacatalina.order;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.maps.model.LatLng;
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
import com.google.maps.android.SphericalUtil;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import eu.ase.ro.grupa1086.licentamanolachemariacatalina.R;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.cart.ItemClickListener;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.cart.SwipeHelper;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.Food;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.Restaurant;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.food.FoodInfo;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.principalmenus.PrincipalMenu;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.principalmenus.UserComparator;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.viewHolder.CartViewHolder;

public class ShoppingCart extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    CardView cardView;

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

        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24);// set drawable icon
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Firebase
        database = FirebaseDatabase.getInstance();

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        id = user.getUid();
        cart = database.getInstance().getReference("carts").child(id).child("foodList");


        emptyCart = findViewById(R.id.emptyCart);
        tvEmptyCart = findViewById(R.id.tvEmptyCart);
        btnStartShopping = findViewById(R.id.btnStartShopping);
        cardView = findViewById(R.id.cardView);

        if (cart.equals(null)) {
            emptyCart.setVisibility(View.VISIBLE);
            tvEmptyCart.setVisibility(View.VISIBLE);
            cardView.setVisibility(View.GONE);
        } else {
            cardView.setVisibility(View.VISIBLE);
        }

        //RecyclerView
        recyclerView = findViewById(R.id.cartList);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);



        SwipeHelper swipeHelper = new SwipeHelper(getApplicationContext(), recyclerView, 200) {
            @Override
            public void instantiateMyButton(RecyclerView.ViewHolder viewHolder, List<MyButton> buffer) {
                buffer.add(new MyButton(getApplicationContext(), "Ștergere", 30, 0, Color.parseColor("#FF3C30"),
                        position -> {
                            Toast.makeText(getApplicationContext(), "Produsul a fost eliminat din coș", Toast.LENGTH_SHORT).show();
                            Food deletedFood = adapter.getItem(position);
                            total -= deletedFood.getPrice() * deletedFood.getQuantity();
                            DatabaseReference cartItem = cart.child(deletedFood.getId());
                            cartItem.removeValue();
                            cartList.remove(deletedFood);

                            Log.i("cartList", cartList.toString());
                            Log.i("cartList", cartItem.toString());

                            cart.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (cartList.size() == 0) {
                                        emptyCart.setVisibility(View.VISIBLE);
                                        tvEmptyCart.setVisibility(View.VISIBLE);
                                        cardView.setVisibility(View.GONE);
                                        btnPlaceOrder.setEnabled(false);
                                        adapter.onDataChanged();
                                        adapter.notifyItemChanged(0);
                                        recyclerView.setAdapter(adapter);

//                                Intent emptyCart = new Intent(ShoppingCart.this, ShoppingCart.class);
//                                startActivity(emptyCart);
//                                finish();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                            recyclerView.setAdapter(adapter);

                        }));
            }
        };

        tvTotalPrice = findViewById(R.id.total);
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder);

        loadFood();


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    public List<Food> loadFood() {

        cart.addListenerForSingleValueEvent(new ValueEventListener() {
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

                tvTotalPrice.setText((double)Math.round(total * 100d) / 100d + " LEI");

                if (cartList.size() == 0 || total == 0.0f) {
                    emptyCart.setVisibility(View.VISIBLE);
                    tvEmptyCart.setVisibility(View.VISIBLE);
                    btnStartShopping.setVisibility(View.VISIBLE);
                    cardView.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);
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
                    cardView.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.VISIBLE);
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
                        placeOrder.putExtra("total", (double) Math.round(total * 100d) / 100d);
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
                .orderByChild("restaurantId")
                .limitToLast(50);

        Log.i("hello", query.toString());

        cart.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()) {
                    emptyCart.setVisibility(View.VISIBLE);
                    tvEmptyCart.setVisibility(View.VISIBLE);
                    btnStartShopping.setVisibility(View.VISIBLE);
                    cardView.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);
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
                    cardView.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.VISIBLE);
                    btnPlaceOrder.setEnabled(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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
                Picasso.with(getBaseContext()).load(model.getImage()).placeholder(R.drawable.loading)
                        .into(holder.imgCartCount);

                holder.tvCartPrice.setText(String.valueOf(model.getPrice()));
                holder.tvCartQuantity.setText(String.valueOf(model.getQuantity()));
                holder.tvCartValue.setText((double)Math.round(model.getPrice() * model.getQuantity() * 100d) / 100d + " lei");
                holder.tvQuantityDisplay.setText(String.valueOf(model.getQuantity()));

                restaurants =  FirebaseDatabase.getInstance().getReference("restaurants").child(model.getRestaurantId());

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
                                model.getQuantity(), model.getImage(), model.getRestaurantId(), model.getPreparationTime());

                        cart.child(model.getId()).setValue(updatedFood).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    total += model.getPrice();
                                    tvTotalPrice.setText((double)Math.round(total * 100d) / 100d + " LEI");
                                    Toast.makeText(ShoppingCart.this, "Cantitate modificată", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(ShoppingCart.this, "Eroare la modificarea cantității", Toast.LENGTH_LONG).show();
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
                                    model.getQuantity(), model.getImage(), model.getRestaurantId(), model.getPreparationTime());

                            cart.child(model.getId()).setValue(updatedFood).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        total -= model.getPrice();
                                        tvTotalPrice.setText((double)Math.round(total * 100d) / 100d + " LEI");
                                        Toast.makeText(ShoppingCart.this, "Cantitate modificată", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(ShoppingCart.this, "Eroare la modificarea cantității", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(ShoppingCart.this, "Cantitatea nu poate fi mai mică de 1!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                if (total > 0) {
                    tvTotalPrice.setText((double)Math.round(total * 100d) / 100d + " LEI");
                } else {
                    tvTotalPrice.setText("0 LEI");

//                    emptyCart.setVisibility(View.VISIBLE);
//                    tvEmptyCart.setVisibility(View.VISIBLE);
//                    btnStartShopping.setVisibility(View.VISIBLE);
//                    cardView.setVisibility(View.GONE);
//                    recyclerView.setVisibility(View.GONE);
//                    btnStartShopping.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            Intent principalMenu = new Intent(ShoppingCart.this, PrincipalMenu.class);
//                            startActivity(principalMenu);
//                            finish();
//                        }
//                    });
//                    btnPlaceOrder.setEnabled(false);
                }

                final Food local = model;
                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                        Toast.makeText(getApplicationContext(), model.getName(), Toast.LENGTH_LONG).show();
                        Intent foodInfo = new Intent(ShoppingCart.this, FoodInfo.class);
                        foodInfo.putExtra("idCartItem", model.getId());
                        foodInfo.putExtra("restaurantId", model.getRestaurantId());
                        foodInfo.putExtra("quantity", String.valueOf(model.getQuantity()));
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
        loadFood();
    }

    public LatLng getLocationFromAddress(String strAddress) {

        Geocoder coder = new Geocoder(getApplicationContext());
        List<android.location.Address> address;
        LatLng coordinates = null;

        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }

            android.location.Address location = address.get(0);
            coordinates = new LatLng(location.getLatitude(), location.getLongitude());

        } catch (IOException ex) {

            ex.printStackTrace();
        }

        return coordinates;
    }
}