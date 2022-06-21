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
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import eu.ase.ro.grupa1086.licentamanolachemariacatalina.MainActivity;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.PersonalDriverOrders;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.R;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.account.DriverAccount;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.cart.ItemClickListener;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.Food;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.Order;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.Restaurant;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.User;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.food.FoodInfo;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.order.OrderDetails;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.order.OrdersList;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.principalmenus.PrincipalMenu;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.rating.CommentsList;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.rating.Rating;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.viewHolder.CommentViewHolder;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.viewHolder.DriverOrderViewHolder;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.viewHolder.DriverViewHolder;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.viewHolder.OrderAdminViewHolder;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.viewHolder.OrderDetailsViewHolder;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.viewHolder.OrderViewHolder;

public class DriverAccountsList extends AppCompatActivity {

    public RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;
    public RecyclerView.LayoutManager secondLayoutManager;
    public RecyclerView.LayoutManager thirdLayoutManager;

    FirebaseRecyclerAdapter<User, DriverViewHolder> adapter;
    FirebaseRecyclerAdapter<Order, OrderAdminViewHolder> secondAdapter;
    FirebaseRecyclerAdapter<Food, OrderDetailsViewHolder> thirdAdapter;


    FirebaseDatabase database;
    FirebaseUser user;
    DatabaseReference driverOrdersHistory;
    DatabaseReference restaurantAddresses;
    DatabaseReference cart;
    DatabaseReference users;
    int driverOrders = 0;
//    Button btnLogout;

    String restaurantName;
    String restaurantImage;
    String orderId;
    List<Order> orderList = new ArrayList<Order>();

    BottomNavigationView bottomNavigationView;
    FloatingActionButton fabAddNewDriver;

    AlertDialog.Builder signOutAlert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.driver_accounts_list);

        database = FirebaseDatabase.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        driverOrdersHistory = database.getReference("driverOrdersHistory");
        users = database.getReference("users");

        recyclerView = findViewById(R.id.driversAccountsList);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        signOutAlert = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogStyle));

        driverOrders = 0;
        loadDriverAccounts();

        fabAddNewDriver = findViewById(R.id.fabAddNewDriver);
        fabAddNewDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), NewDriverAccount.class));
            }
        });

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.driverAccounts);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.restaurantAccounts:
                        startActivity(new Intent(getApplicationContext(), RestaurantAccountsList.class));
                        finish();
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.driverAccounts:
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

//        btnLogout = findViewById(R.id.btnLogout);
//        btnLogout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FirebaseAuth.getInstance().signOut();
//                startActivity(new Intent(getApplicationContext(), MainActivity.class));
//                finish();
//            }
//        });
    }

    private void loadDriverAccounts() {
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("users")
                .orderByChild("isDriver").equalTo(1)
                .limitToLast(50);


        FirebaseRecyclerOptions<User> options =
                new FirebaseRecyclerOptions.Builder<User>()
                        .setQuery(query, User.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<User, DriverViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull DriverViewHolder holder, int position, @NonNull User model) {

                driverOrders = 0;
                holder.driverName.setText(model.getName());
                holder.driverEmail.setText("Email: " + model.getEmail());
                holder.driverPhoneNumber.setText("Telefon: " + model.getPhoneNumber());
                driverOrdersHistory.child(model.getId()).child("orders").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            driverOrders++;
                        }
                        holder.driverOrderNumbers.setText("Comenzi livrate: " + String.valueOf(driverOrders));
                        driverOrders = 0;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                final User local = model;
                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                        //Toast.makeText(DriverAccountsList.this, model.getId(), Toast.LENGTH_LONG).show();
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
                                    .child("driverOrdersHistory")
                                    .child(local.getId())
                                    .child("orders")
                                    .orderByChild("currentDateAndTime")
                                    .limitToLast(50);

                            FirebaseRecyclerOptions<Order> options =
                                    new FirebaseRecyclerOptions.Builder<Order>()
                                            .setQuery(query, Order.class)
                                            .build();

                            secondAdapter = new FirebaseRecyclerAdapter<Order, OrderAdminViewHolder>(options) {
                                @Override
                                protected void onBindViewHolder(@NonNull OrderAdminViewHolder holder, int position, @NonNull Order model2) {

                                    //Log.i("ceva", model.toString());
                                    restaurantName = null;
                                    restaurantImage = null;
                                    orderList.add(model2);
                                    Log.i("ceva", orderList.toString());
                                    orderId = model2.getId();

                                    driverOrdersHistory.child(local.getId()).child("orders").child(model2.getId()).child("restaurantAddress").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                                Log.i("ceva", dataSnapshot.toString());
                                                Restaurant restaurant = dataSnapshot.getValue(Restaurant.class);
                                                if (restaurantName == null) {
                                                    restaurantName = restaurant.getName();
                                                    restaurantImage = restaurant.getImage();
                                                } else {
                                                    restaurantName += ", " + restaurant.getName();
                                                }
                                            }

                                            users.child(model2.getUserId()).addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    User user = snapshot.getValue(User.class);
                                                    holder.clientName.setText("#" + user.getName());
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });

                                            holder.restaurantsName.setText(restaurantName);
                                            holder.orderStatus.setText(getString(R.string.order_status) + " " + String.valueOf(model2.getStatus()).substring(0, 1).toUpperCase(Locale.ROOT) + String.valueOf(model2.getStatus()).replace("_", " ").substring(1));
                                            holder.orderAddress.setText(getString(R.string.address) + " " + model2.getAddress().getMapsAddress());
                                            holder.orderDateAndTime.setText("Data: " + model2.getCurrentDateAndTime());
                                            holder.orderPriceTotal.setText(getString(R.string.total) + " " + (double) Math.round(model2.getTotal() * 100d) / 100d + " " + getString(R.string.lei));
                                            Picasso.with(getBaseContext()).load(restaurantImage).placeholder(R.drawable.loading)
                                                    .into(holder.restaurantImage);

                                            String restaurantName2 = restaurantName;
                                            String restaurantImage2 = restaurantImage;
                                            restaurantName = null;
                                            restaurantImage = null;

                                            //final Order local = model;
                                            holder.setItemClickListener(new ItemClickListener() {
                                                @Override
                                                public void onClick(View view, int position, boolean isLongClick) {

                                                    if (!isLongClick) {
                                                        //Toast.makeText(OrdersList.this, model.getCart().toString(), Toast.LENGTH_LONG).show();
//                                    loadOrderDetails(model.getId());

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

                                                        cart = database.getReference().child("driverOrdersHistory").child(local.getId()).child("orders").child(model2.getId()).child("cart");
                                                        restaurantAddresses = database.getReference().child("driverOrdersHistory").child(local.getId()).child("orders").child(model2.getId()).child("restaurantAddress");

                                                        Query query = cart
                                                                .orderByChild("id")
                                                                .limitToLast(50);

                                                        FirebaseRecyclerOptions<Food> options =
                                                                new FirebaseRecyclerOptions.Builder<Food>()
                                                                        .setQuery(query, Food.class)
                                                                        .build();

                                                        thirdAdapter = new FirebaseRecyclerAdapter<Food, OrderDetailsViewHolder>(options) {
                                                            @Override
                                                            protected void onBindViewHolder(@NonNull OrderDetailsViewHolder holder2, int position, @NonNull Food model) {

                                                                holder2.foodName.setText(model.getName());

                                                                restaurantAddresses.addValueEventListener(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                                                            Restaurant restaurant = dataSnapshot.getValue(Restaurant.class);
                                                                            if (model.getRestaurantId().equals(restaurant.getId())) {
                                                                                holder2.restaurantName.setText(restaurant.getName() + " : ");
                                                                            }
                                                                        }

                                                                        holder2.foodPrice.setText(String.valueOf(model.getPrice()));
                                                                        holder2.foodQuantity.setText(String.valueOf(model.getQuantity()));
                                                                        holder2.foodTotal.setText((double) Math.round(model.getPrice() * model.getQuantity() * 100d) / 100d + " lei");
                                                                        Picasso.with(getBaseContext()).load(model.getImage()).placeholder(R.drawable.loading)
                                                                                .into(holder2.foodImage);

                                                                        final Food local2 = model;
                                                                        holder2.setItemClickListener(new ItemClickListener() {
                                                                            @Override
                                                                            public void onClick(View view, int position, boolean isLongClick) {

                                                                                Toast.makeText(DriverAccountsList.this, model.getName(), Toast.LENGTH_LONG).show();
//                                                            showRatingDialog(model.getId());

                                                                                Intent foodInfo = new Intent(DriverAccountsList.this, FoodInfo.class);
                                                                                foodInfo.putExtra("origin", "ordersList");
                                                                                foodInfo.putExtra("orderId", model.getId());
                                                                                foodInfo.putExtra("quantity", local2.getQuantity());
                                                                                foodInfo.putExtra("foodId", local2.getId());
                                                                                foodInfo.putExtra("restaurantId", model.getRestaurantId());
                                                                                startActivity(foodInfo);

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
                                                        if (holder.secondRecyclerView.getVisibility() == View.GONE) {
                                                            holder.secondRecyclerView.setVisibility(View.VISIBLE);
                                                        } else {
                                                            holder.secondRecyclerView.setVisibility(View.GONE);
                                                        }
                                                        thirdLayoutManager = new LinearLayoutManager(getApplicationContext());
                                                        //holder.secondRecyclerView.setHasFixedSize(true);
                                                        holder.secondRecyclerView.setLayoutManager(thirdLayoutManager);
                                                        holder.secondRecyclerView.setAdapter(thirdAdapter);
                                                        thirdAdapter.startListening();

                                                    }

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
                                public OrderAdminViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                                    View view = LayoutInflater.from(parent.getContext())
                                            .inflate(R.layout.order_admin_layout, parent, false);
                                    view.setMinimumWidth(parent.getMeasuredWidth());

                                    return new OrderAdminViewHolder(view);
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


                driverOrders = 0;
            }

            @NonNull
            @Override
            public DriverViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.driver_account_layout, parent, false);
                view.setMinimumWidth(parent.getMeasuredWidth());

                return new DriverViewHolder(view);
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
}