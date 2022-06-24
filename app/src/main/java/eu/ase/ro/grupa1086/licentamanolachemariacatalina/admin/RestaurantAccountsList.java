package eu.ase.ro.grupa1086.licentamanolachemariacatalina.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Locale;

import eu.ase.ro.grupa1086.licentamanolachemariacatalina.MainActivity;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.R;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.RestaurantsList;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.account.DriverAccount;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.account.ResetPassword;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.account.SignIn;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.cart.ItemClickListener;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.Food;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.Order;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.Restaurant;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.RestaurantAccount;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.RestaurantOrder;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.User;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.food.FoodInfo;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.food.FoodList;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.order.OrdersList;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.order.RestaurantOrders;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.viewHolder.DriverViewHolder;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.viewHolder.OrderDetailsViewHolder;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.viewHolder.RestaurantAccountViewHolder;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.viewHolder.RestaurantOrderViewHolder;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.viewHolder.RestaurantViewHolder;

public class RestaurantAccountsList extends AppCompatActivity {

    public RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;
    public RecyclerView.LayoutManager secondLayoutManager;
    public RecyclerView.LayoutManager thirdLayoutManager;

    FirebaseRecyclerAdapter<RestaurantAccount, RestaurantAccountViewHolder> adapter;
    FirebaseRecyclerAdapter<RestaurantOrder, RestaurantOrderViewHolder> secondAdapter;
    FirebaseRecyclerAdapter<Food, OrderDetailsViewHolder> thirdAdapter;

    FirebaseDatabase database;
    FirebaseUser user;
    DatabaseReference restaurantOrdersHistory;
    DatabaseReference users;
    DatabaseReference restaurantAddresses;
    DatabaseReference cart;
    DatabaseReference restaurants;

    BottomNavigationView bottomNavigationView;
    FloatingActionButton fabAddNewRestaurant;

    int restaurantOrders = 0;
    AlertDialog.Builder signOutAlert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(eu.ase.ro.grupa1086.licentamanolachemariacatalina.R.layout.activity_restaurant_accounts_list);

        database = FirebaseDatabase.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        restaurantOrdersHistory = database.getReference("restaurantOrdersHistory");
        users = database.getReference("users");
        restaurants = database.getReference("restaurants");

        recyclerView = findViewById(R.id.restaurantsAccountsList);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        signOutAlert = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogStyle));

        loadRestaurantAccounts();


        fabAddNewRestaurant = findViewById(R.id.fabAddNewRestaurant);
        fabAddNewRestaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), NewRestaurantAccount.class));
            }
        });

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.restaurantAccounts);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.restaurantAccounts:
                        return true;
                    case R.id.driverAccounts:
                        startActivity(new Intent(getApplicationContext(), DriverAccountsList.class));
                        finish();
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.topOrders:
                        startActivity(new Intent(getApplicationContext(), AdminOrders.class));
                        finish();
                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }
        });
    }

    private void loadRestaurantAccounts() {
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("restaurantAccounts")
                .orderByChild("id")
                .limitToLast(50);


        FirebaseRecyclerOptions<RestaurantAccount> options =
                new FirebaseRecyclerOptions.Builder<RestaurantAccount>()
                        .setQuery(query, RestaurantAccount.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<RestaurantAccount, RestaurantAccountViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull RestaurantAccountViewHolder holder, int position, @NonNull RestaurantAccount model) {
                restaurantOrders = 0;

                restaurants.child(model.getId()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Restaurant restaurant = snapshot.getValue(Restaurant.class);

                        if (restaurant != null) {
                            Picasso.with(getBaseContext()).load(restaurant.getImage()).placeholder(R.drawable.loading)
                                    .into(holder.restaurantImage);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                holder.name.setText(model.getName());
                holder.email.setText("Email: " + model.getEmail());
                holder.address.setText("Adresa: " + model.getAddress());
                holder.phoneNumber.setText("Telefon: " + model.getPhoneNumber());

                restaurantOrdersHistory.child(model.getId()).child("orders").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            restaurantOrders++;
                        }
                        holder.restaurantOrdersNumber.setText("Comenzi preparate: " + String.valueOf(restaurantOrders));
                        restaurantOrders = 0;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


                final RestaurantAccount local = model;
                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {


                        if (!isLongClick) {
                            if (holder.downArrow.getVisibility() == View.VISIBLE) {
                                holder.downArrow.setVisibility(View.GONE);
                            } else {
                                holder.downArrow.setVisibility(View.VISIBLE);
                            }

                            if (holder.upArrow.getVisibility() == View.VISIBLE) {
                                holder.upArrow.setVisibility(View.GONE);
                            } else {
                                holder.upArrow.setVisibility(View.VISIBLE);
                            }


                            Query query = FirebaseDatabase.getInstance()
                                    .getReference()
                                    .child("restaurantOrdersHistory")
                                    .child(local.getId())
                                    .child("orders")
                                    .orderByChild("currentDateAndTime")
                                    .limitToLast(50);

                            FirebaseRecyclerOptions<RestaurantOrder> options =
                                    new FirebaseRecyclerOptions.Builder<RestaurantOrder>()
                                            .setQuery(query, RestaurantOrder.class)
                                            .build();

                            secondAdapter = new FirebaseRecyclerAdapter<RestaurantOrder, RestaurantOrderViewHolder>(options) {
                                @Override
                                protected void onBindViewHolder(@NonNull RestaurantOrderViewHolder holder2, int position, @NonNull RestaurantOrder model) {

                                    users.child(model.getUserId()).child("name").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            String userName = snapshot.getValue(String.class);
                                            holder2.orderName.setText("Comanda #" + userName);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });

                                    holder2.orderStatus.setText("Status: " + String.valueOf(model.getStatus()).substring(0, 1).toUpperCase(Locale.ROOT) + String.valueOf(model.getStatus()).replace("_", " ").substring(1));
                                    holder2.orderAddress.setText("Adresa: " + model.getAddress().getMapsAddress());
                                    holder2.orderDateAndTime.setText("Data: " + model.getCurrentDateAndTime());
                                    holder2.orderPriceTotal.setText("Total: " + Math.round(model.getTotal() * 100.0) / 100.0 + " lei");

                                    final RestaurantOrder local2 = model;
                                    holder2.setItemClickListener(new ItemClickListener() {
                                        @Override
                                        public void onClick(View view, int position, boolean isLongClick) {

                                            if (!isLongClick) {
                                                //Toast.makeText(OrdersList.this, model.getCart().toString(), Toast.LENGTH_LONG).show();
//                                    loadOrderDetails(model.getId());

                                                if (holder2.downArrow.getVisibility() == View.VISIBLE) {
                                                    holder2.downArrow.setVisibility(View.GONE);
                                                } else {
                                                    holder2.downArrow.setVisibility(View.VISIBLE);
                                                }

                                                if (holder2.upArrow.getVisibility() == View.VISIBLE) {
                                                    holder2.upArrow.setVisibility(View.GONE);
                                                } else {
                                                    holder2.upArrow.setVisibility(View.VISIBLE);
                                                }

                                                cart = database.getReference().child("restaurantOrdersHistory").child(local.getId()).child("orders").child(local2.getOrderId()).child("foodList");
                                                restaurantAddresses = database.getReference("restaurantOrdersHistory").child(local.getId()).child("orders").child(local2.getOrderId()).child("restaurant");

                                                Query query = cart
                                                        .orderByChild("id")
                                                        .limitToLast(50);

                                                FirebaseRecyclerOptions<Food> options =
                                                        new FirebaseRecyclerOptions.Builder<Food>()
                                                                .setQuery(query, Food.class)
                                                                .build();

                                                thirdAdapter = new FirebaseRecyclerAdapter<Food, OrderDetailsViewHolder>(options) {
                                                    @Override
                                                    protected void onBindViewHolder(@NonNull OrderDetailsViewHolder holder3, int position, @NonNull Food model) {

                                                        holder3.foodName.setText(model.getName());

                                                        restaurantAddresses.addValueEventListener(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                Restaurant restaurant = snapshot.getValue(Restaurant.class);
                                                                holder3.restaurantName.setText(restaurant.getName() + " : ");

                                                                holder3.foodPrice.setText(String.valueOf(model.getPrice()));
                                                                holder3.foodQuantity.setText(String.valueOf(model.getQuantity()));
                                                                holder3.foodTotal.setText((double) Math.round(model.getPrice() * model.getQuantity() * 100d) / 100d + " lei");
                                                                Picasso.with(getBaseContext()).load(model.getImage()).placeholder(R.drawable.loading)
                                                                        .into(holder3.foodImage);

                                                                final Food local2 = model;
                                                                holder3.setItemClickListener(new ItemClickListener() {
                                                                    @Override
                                                                    public void onClick(View view, int position, boolean isLongClick) {


                                                                    }
                                                                });
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError error) {

                                                            }
                                                        });


                                                    }


                                                    @NonNull
                                                    @Override
                                                    public OrderDetailsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                                                        View view = LayoutInflater.from(parent.getContext())
                                                                .inflate(R.layout.order_detail_layout, parent, false);
                                                        view.setMinimumWidth(parent.getMeasuredWidth());

                                                        return new OrderDetailsViewHolder(view);
                                                    }
                                                };

//                                    secondRecyclerView.setAdapter(secondAdapter);
//                                    secondAdapter.startListening();
                                                if (holder2.secondRecyclerView.getVisibility() == View.GONE) {
                                                    holder2.secondRecyclerView.setVisibility(View.VISIBLE);
                                                } else {
                                                    holder2.secondRecyclerView.setVisibility(View.GONE);
                                                }
                                                thirdLayoutManager = new LinearLayoutManager(getApplicationContext());
                                                //holder.secondRecyclerView.setHasFixedSize(true);
                                                holder2.secondRecyclerView.setLayoutManager(thirdLayoutManager);
                                                holder2.secondRecyclerView.setAdapter(thirdAdapter);
                                                thirdAdapter.startListening();
                                            }
                                        }
                                    });

                                }


                                @NonNull
                                @Override
                                public RestaurantOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                                    View view = LayoutInflater.from(parent.getContext())
                                            .inflate(R.layout.restaurant_order_layout, parent, false);
                                    view.setMinimumWidth(parent.getMeasuredWidth());

                                    return new RestaurantOrderViewHolder(view);
                                }
                            };

                            if (holder.secondRecyclerView.getVisibility() == View.GONE) {
                                holder.secondRecyclerView.setVisibility(View.VISIBLE);
                            } else {
                                holder.secondRecyclerView.setVisibility(View.GONE);
                            }
                            secondLayoutManager = new LinearLayoutManager(getApplicationContext());
                            //holder.secondRecyclerView.setHasFixedSize(true);
                            holder.secondRecyclerView.setLayoutManager(secondLayoutManager);
                            holder.secondRecyclerView.setAdapter(secondAdapter);
                            secondAdapter.startListening();

                        }

                    }
                });
            }

            ;

            @NonNull
            @Override
            public RestaurantAccountViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.restaurant_account_layout, parent, false);
                view.setMinimumWidth(parent.getMeasuredWidth());

                return new RestaurantAccountViewHolder(view);
            }

        };


        recyclerView.setAdapter(adapter);
    }


    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.sign_out_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logout) {
            signOutAlert.setTitle("Ieșire din cont")
                    .setMessage("Ești sigur că dorești să ieși din cont?")
                    .setPositiveButton("Da", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            FirebaseAuth.getInstance().signOut();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            finish();

                        }
                    }).setNegativeButton("Nu", null)
                    .create().show();
        }
        return false;
    }
};

