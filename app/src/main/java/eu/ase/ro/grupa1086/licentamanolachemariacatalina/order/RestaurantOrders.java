package eu.ase.ro.grupa1086.licentamanolachemariacatalina.order;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import com.google.maps.android.SphericalUtil;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import eu.ase.ro.grupa1086.licentamanolachemariacatalina.R;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.account.DriverAccount;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.cart.ItemClickListener;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.Food;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.Order;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.Restaurant;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.Status;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.principalmenus.DriverMenu;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.principalmenus.RestaurantMenu;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.viewHolder.DriverOrderViewHolder;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.viewHolder.OrderDetailsViewHolder;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.viewHolder.OrderViewHolder;

public class RestaurantOrders extends AppCompatActivity {

    public RecyclerView recyclerView;
    //public RecyclerView secondRecyclerView;
    public RecyclerView.LayoutManager layoutManager;
    public RecyclerView.LayoutManager secondLayoutManager;

    FirebaseRecyclerAdapter<Order, DriverOrderViewHolder> adapter;
    FirebaseRecyclerAdapter<Food, OrderDetailsViewHolder> secondAdapter;
    FirebaseDatabase database;
    DatabaseReference orders;
    DatabaseReference cart;
    DatabaseReference restaurantAddresses;
    FirebaseUser user;

    List<Order> orderList = new ArrayList<Order>();

    String restaurantName;
    String restaurantImage;
    String orderId;

    BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_orders);

        database = FirebaseDatabase.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        orders = database.getReference("driverOrders");

        recyclerView = (RecyclerView) findViewById(R.id.ordersList);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        loadOrders();

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.orders);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.products:
                        startActivity(new Intent(getApplicationContext(), RestaurantMenu.class));
                        finish();
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.orders:
                        return true;
                }
                return false;
            }
        });
    }

    private void loadOrders() {

        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("driverOrders")
                .orderByChild("id")
                .limitToLast(50);

        FirebaseRecyclerOptions<Order> options =
                new FirebaseRecyclerOptions.Builder<Order>()
                        .setQuery(query, Order.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<Order, DriverOrderViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull DriverOrderViewHolder holder, int position, @NonNull Order model) {

                restaurantName = null;
                restaurantImage = null;
                orderList.add(model);
                orderId = model.getId();

                if (!model.getStatus().equals(Status.finalizata)) {
//                    for (int i = 0; i < model.getRestaurantAddress().size(); i++) {
//                        if (model.getRestaurantAddress().get(i).getId().equals(user.getUid())) {

                            orders.child(model.getId()).child("restaurantAddress").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                        Restaurant restaurant = dataSnapshot.getValue(Restaurant.class);
                                        if (restaurantName == null) {
                                            restaurantName = restaurant.getName();
                                            restaurantImage = restaurant.getImage();
                                        } else {
                                            restaurantName += ", " + restaurant.getName();
                                        }
                                    }

                                    holder.restaurantsName.setText(restaurantName);
                                    holder.orderStatus.setText(String.valueOf(model.getStatus()).replace("_", " "));
                                    holder.orderAddress.setText(String.valueOf(model.getAddress().getMapsAddress()));
                                    holder.orderPriceTotal.setText("Total: " + model.getTotal() + " lei");
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

                                                cart = database.getReference().child("driverOrders").child(model.getId()).child("cart");
                                                restaurantAddresses = database.getReference("driverOrders").child(model.getId()).child("restaurantAddress");

                                                Query query = cart
                                                        .orderByChild("id")
                                                        .limitToLast(50);

                                                FirebaseRecyclerOptions<Food> options =
                                                        new FirebaseRecyclerOptions.Builder<Food>()
                                                                .setQuery(query, Food.class)
                                                                .build();

                                                secondAdapter = new FirebaseRecyclerAdapter<Food, OrderDetailsViewHolder>(options) {
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
                                                                holder2.foodTotal.setText(model.getPrice() * model.getQuantity() + " lei");
                                                                Picasso.with(getBaseContext()).load(model.getImage())
                                                                        .into(holder2.foodImage);

                                                                final Food local2 = model;
                                                                holder2.setItemClickListener(new ItemClickListener() {
                                                                    @Override
                                                                    public void onClick(View view, int position, boolean isLongClick) {

                                                                        Toast.makeText(RestaurantOrders.this, model.getName(), Toast.LENGTH_LONG).show();

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
//                                            } else if (isLongClick) {
//                                                if (model.getStatus().equals(Status.plasata)) {
////                                    View viewPopUp = inflater.inflate(R.layout.reset_name_pop_up, null);
//                                                    acceptOrder.setTitle("Doresti sa livrezi comanda selectata?")
//                                                            .setPositiveButton("Confirmare", new DialogInterface.OnClickListener() {
//                                                                @Override
//                                                                public void onClick(DialogInterface dialog, int which) {
//
//                                                                    model.setStatus(Status.in_curs_de_livrare);
//                                                                    driverOrders.child(model.getId()).child("status").setValue(Status.in_curs_de_livrare);
//
//                                                                    driverOrders.child(model.getId()).child("userId").addValueEventListener(new ValueEventListener() {
//                                                                        @Override
//                                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                                                            String userId = snapshot.getValue(String.class);
//                                                                            if (userId != null)
//                                                                                orders.child(userId).child(model.getId()).child("status").setValue(Status.in_curs_de_livrare);
//                                                                        }
//
//                                                                        @Override
//                                                                        public void onCancelled(@NonNull DatabaseError error) {
//
//                                                                        }
//                                                                    });
//
//
////                                                            orders.child(id).child(model.getId()).child("status").setValue(Status.in_curs_de_livrare);
//
//
//                                                                }
//                                                            }).setNegativeButton("Anuleaza", null)
//                                                            .create().show();
//
//                                                } else if (model.getStatus().equals(Status.in_curs_de_livrare)) {
//                                                    acceptOrder.setTitle("Ai terminat de livrat aceasta comanda?")
//                                                            .setPositiveButton("Confirmare", new DialogInterface.OnClickListener() {
//                                                                @Override
//                                                                public void onClick(DialogInterface dialog, int which) {
//
//                                                                    model.setStatus(Status.finalizata);
//
//                                                                    driverOrders.child(model.getId()).child("status").setValue(Status.finalizata);
//
//
//                                                                    driverOrders.child(model.getId()).child("userId").addValueEventListener(new ValueEventListener() {
//                                                                        @Override
//                                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                                                            String userId = snapshot.getValue(String.class);
//                                                                            if (userId != null)
//                                                                                orders.child(userId).child(model.getId()).child("status").setValue(Status.finalizata).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                                                    @Override
//                                                                                    public void onComplete(@NonNull Task<Void> task) {
//                                                                                        driverOrders.child(model.getId()).removeValue();
//                                                                                    }
//                                                                                });
//                                                                        }
//
//                                                                        @Override
//                                                                        public void onCancelled(@NonNull DatabaseError error) {
//
//                                                                        }
//                                                                    });
//
//                                                                }
//                                                            }).setNegativeButton("Anuleaza", null)
//                                                            .create().show();
//                                                }
//                                            }

                                        }
                                    });


                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }

//                    }
//                }

            }

            @NonNull
            @Override
            public DriverOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.driver_order_layout, parent, false);
                view.setMinimumWidth(parent.getMeasuredWidth());

                return new DriverOrderViewHolder(view);
            }
        };


        recyclerView.setAdapter(adapter);
        adapter.startListening();


    }

    @Override
    protected void onStart() {
        super.onStart();
        //adapter.startListening();
    }
}