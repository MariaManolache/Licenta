package eu.ase.ro.grupa1086.licentamanolachemariacatalina.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
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
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.viewHolder.OrderDetailsViewHolder;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.viewHolder.OrderViewHolder;

public class DriverAccountsList extends AppCompatActivity {

    public RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;
    public RecyclerView.LayoutManager secondLayoutManager;

    FirebaseRecyclerAdapter<User, DriverViewHolder> adapter;
    FirebaseRecyclerAdapter<Order, OrderViewHolder> secondAdapter;

    FirebaseDatabase database;
    FirebaseUser user;
    DatabaseReference driverOrdersHistory;
    int driverOrders = 0;
    Button btnLogout;

    String restaurantName;
    String restaurantImage;
    String orderId;
    List<Order> orderList = new ArrayList<Order>();

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.driver_accounts_list);

        database = FirebaseDatabase.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        driverOrdersHistory = database.getReference("driverOrdersHistory");

        recyclerView = findViewById(R.id.driversAccountsList);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        driverOrders = 0;
        loadDriverAccounts();

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.orders);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.restaurantAccounts:
//                        startActivity(new Intent(getApplicationContext(), DriverAccount.class));
//                        finish();
//                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.driverAccounts:
                        return true;
                    case R.id.topOrders:
//                        startActivity(new Intent(getApplicationContext(), PersonalDriverOrders.class));
//                        finish();
//                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }
        });

        btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        });
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
                holder.driverName.setText(model.getName());
                holder.driverEmail.setText("Email: " + model.getEmail());
                holder.driverPhoneNumber.setText("Telefon: " + model.getPhoneNumber());
                driverOrdersHistory.child(model.getId()).child("orders").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            driverOrders++;
                        }
                        holder.driverOrderNumbers.setText("Comenzi livrate: " + String.valueOf(driverOrders));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                final User local = model;
                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                        Toast.makeText(DriverAccountsList.this, model.getId(), Toast.LENGTH_LONG).show();
                        if(!isLongClick) {
                            if(holder.downArrow.getVisibility() == View.VISIBLE) {
                                holder.downArrow.setVisibility(View.GONE);
                            } else {
                                holder.downArrow.setVisibility(View.VISIBLE);
                            }

                            if(holder.upArrow.getVisibility() == View.VISIBLE) {
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

                            secondAdapter = new FirebaseRecyclerAdapter<Order, OrderViewHolder>(options) {
                                @Override
                                protected void onBindViewHolder(@NonNull OrderViewHolder holder, int position, @NonNull Order model) {

                                    Log.i("ceva", model.toString());
                                    restaurantName = null;
                                    restaurantImage = null;
                                    orderList.add(model);
                                    Log.i("ceva", orderList.toString());
                                    orderId = model.getId();

                                    driverOrdersHistory.child(local.getId()).child("orders").child(model.getId()).child("restaurantAddress").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                                Log.i("ceva", dataSnapshot.toString());
                                                Restaurant restaurant = dataSnapshot.getValue(Restaurant.class);
                                                if(restaurantName == null) {
                                                    restaurantName = restaurant.getName();
                                                    restaurantImage = restaurant.getImage();
                                                } else {
                                                    restaurantName += ", " + restaurant.getName();
                                                }
                                            }

                                            holder.restaurantsName.setText(restaurantName);
                                            holder.orderStatus.setText(getString(R.string.order_status) + " " + String.valueOf(model.getStatus()).substring(0, 1).toUpperCase(Locale.ROOT) + String.valueOf(model.getStatus()).replace("_", " ").substring(1));
                                            holder.orderAddress.setText(getString(R.string.address) + " " + model.getAddress().getMapsAddress());
                                            holder.orderDateAndTime.setText("Data: " + model.getCurrentDateAndTime());
                                            holder.orderPriceTotal.setText(getString(R.string.total) + " " + (double)Math.round(model.getTotal() * 100d) / 100d + " " + getString(R.string.lei));
                                            Picasso.with(getBaseContext()).load(restaurantImage)
                                                    .into(holder.restaurantImage);

                                            String restaurantName2 = restaurantName;
                                            String restaurantImage2 = restaurantImage;
                                            restaurantName = null;
                                            restaurantImage = null;

                                            final Order local = model;
                                            holder.setItemClickListener(new ItemClickListener() {
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
                                public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                                    View view = LayoutInflater.from(parent.getContext())
                                            .inflate(R.layout.order_layout, parent, false);
                                    view.setMinimumWidth(parent.getMeasuredWidth());

                                    return new OrderViewHolder(view);
                                }
                            };

                            if(holder.secondRecyclerView.getVisibility() == View.GONE) {
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
}