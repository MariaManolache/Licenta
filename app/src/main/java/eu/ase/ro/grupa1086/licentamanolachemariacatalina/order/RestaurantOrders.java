package eu.ase.ro.grupa1086.licentamanolachemariacatalina.order;

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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import eu.ase.ro.grupa1086.licentamanolachemariacatalina.R;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.restaurant.RestaurantProducts;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.cart.ItemClickListener;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.Food;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.Order;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.Restaurant;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.RestaurantOrder;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.Status;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.principalmenus.RestaurantAccount;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.viewHolder.RestaurantOrderDetailsViewHolder;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.viewHolder.RestaurantOrderViewHolder;

public class RestaurantOrders extends AppCompatActivity {

    public RecyclerView recyclerView;
    //public RecyclerView secondRecyclerView;
    public RecyclerView.LayoutManager layoutManager;
    public RecyclerView.LayoutManager secondLayoutManager;

    FirebaseRecyclerAdapter<RestaurantOrder, RestaurantOrderViewHolder> adapter;
    FirebaseRecyclerAdapter<Food, RestaurantOrderDetailsViewHolder> secondAdapter;
    FirebaseDatabase database;
    DatabaseReference orders;
    DatabaseReference cart;
    DatabaseReference restaurantAddresses;
    DatabaseReference restaurants;
    DatabaseReference driverOrders;
    DatabaseReference restaurantOrders;
    DatabaseReference users;
    DatabaseReference restaurantOrdersHistory;
    DatabaseReference ordersHistory;
    FirebaseUser user;

    AlertDialog.Builder acceptOrder;

    List<RestaurantOrder> orderList = new ArrayList<RestaurantOrder>();

    String restaurantName;
    String restaurantImage;
    String orderId;
    //ImageView callButton;

    BottomNavigationView bottomNavigationView;
    boolean allRestaurantsConfirmed;
    ImageView nothingFound;
    TextView tvNoRestaurant;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_orders);

        database = FirebaseDatabase.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        //Log.i("restaurantOrder", user.getUid());
        restaurants = database.getReference("restaurants");
        driverOrders = database.getReference().child("driverOrders");
        restaurantOrders = database.getReference().child("restaurantOrders");
        orders = database.getReference().child("orders");
        users = database.getReference("users");
        restaurantOrdersHistory = database.getReference("restaurantOrdersHistory");
        ordersHistory = database.getReference("ordersHistory");

        recyclerView = (RecyclerView) findViewById(R.id.restaurantOrdersList);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //callButton = findViewById(R.id.callButton);

        acceptOrder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogStyle));

        nothingFound = findViewById(R.id.noRestaurants);
        tvNoRestaurant = findViewById(R.id.tvNoRestaurants);

        loadOrders();

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.orders);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if(id == R.id.account) {
                    startActivity(new Intent(getApplicationContext(), RestaurantAccount.class));
                    overridePendingTransition(R.anim.slide_left2, R.anim.slide_right2);
                    finish();
                    return true;
                } else if(id == R.id.products) {
                    startActivity(new Intent(getApplicationContext(), RestaurantProducts.class));
                    overridePendingTransition(R.anim.slide_right, R.anim.slide_left);
                    finish();
                    return true;
                } else if(id == R.id.orders) {
                    return true;
                }
//                switch (item.getItemId()) {
//                    case R.id.account:
//                        startActivity(new Intent(getApplicationContext(), RestaurantAccount.class));
//                        finish();
//                        overridePendingTransition(0, 0);
//                        return true;
//                    case R.id.products:
//                        startActivity(new Intent(getApplicationContext(), RestaurantProducts.class));
//                        finish();
//                        overridePendingTransition(0, 0);
//                        return true;
//                    case R.id.orders:
//                        return true;
//                }
                return false;
            }
        });
    }

    private void loadOrders() {

        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("restaurantOrders")
                .child(user.getUid())
                .child("orders")
                .orderByChild("id")
                .limitToLast(50);

        FirebaseRecyclerOptions<RestaurantOrder> options =
                new FirebaseRecyclerOptions.Builder<RestaurantOrder>()
                        .setQuery(query, RestaurantOrder.class)
                        .build();

        restaurantOrders.child(user.getUid()).child("orders").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() == null) {
                    nothingFound.setVisibility(View.VISIBLE);
                    tvNoRestaurant.setVisibility(View.VISIBLE);
                } else {
                    nothingFound.setVisibility(View.GONE);
                    tvNoRestaurant.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        adapter = new FirebaseRecyclerAdapter<RestaurantOrder, RestaurantOrderViewHolder>(options) {

            @NonNull
            @Override
            public RestaurantOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.restaurant_order_layout, parent, false);
                view.setMinimumWidth(parent.getMeasuredWidth());

                return new RestaurantOrderViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull RestaurantOrderViewHolder holder, int position, @NonNull RestaurantOrder model) {

                orderList.add(model);
//                int orderNumber = 0;
//                for (int i = 0; i < orderList.size(); i++) {
//                    if (orderList.get(i).equals(model)) {
//                        orderNumber = i + 1;
//                    }
//                }
                orderId = model.getOrderId();
                Log.i("restaurantOrder", orderId);

                if (!model.getStatus().equals(Status.finalizata)) {

                    users.child(model.getUserId()).child("name").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String userName = snapshot.getValue(String.class);
                            holder.orderName.setText("Comanda #" + userName);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    holder.orderStatus.setText("Status: " + String.valueOf(model.getStatus()).substring(0, 1).toUpperCase(Locale.ROOT) + String.valueOf(model.getStatus()).replace("_", " ").substring(1));
                    holder.orderPaymentType.setText("Metoda de plată: " + String.valueOf(model.getPaymentMethod()).toUpperCase(Locale.ROOT).replace("_", " "));
                    holder.orderAddress.setText("Adresa: " + model.getAddress().getMapsAddress());
                    holder.orderDateAndTime.setText("Data: " + model.getCurrentDateAndTime());
                    holder.orderPriceTotal.setText("Total: " + Math.round(model.getTotal() * 100.0) / 100.0 + " lei");


                    final RestaurantOrder local = model;
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

                                cart = database.getReference().child("restaurantOrders").child(model.getRestaurantId()).child("orders").child(model.getOrderId()).child("foodList");
                                restaurantAddresses = database.getReference("restaurantOrders").child(model.getRestaurantId()).child("orders").child(model.getOrderId()).child("restaurant");

                                Query query = cart
                                        .orderByChild("id")
                                        .limitToLast(50);

                                FirebaseRecyclerOptions<Food> options =
                                        new FirebaseRecyclerOptions.Builder<Food>()
                                                .setQuery(query, Food.class)
                                                .build();

                                secondAdapter = new FirebaseRecyclerAdapter<Food, RestaurantOrderDetailsViewHolder>(options) {
                                    @Override
                                    protected void onBindViewHolder(@NonNull RestaurantOrderDetailsViewHolder holder2, int position, @NonNull Food model) {

                                        holder2.foodName.setText(model.getName());
                                        holder2.foodPrice.setText(String.valueOf(model.getPrice()));
                                        holder2.foodQuantity.setText(String.valueOf(model.getQuantity()));
                                        holder2.foodTotal.setText((double)Math.round(model.getPrice() * model.getQuantity() * 100) / 100 + " lei");
                                        Picasso.with(getBaseContext()).load(model.getImage()).placeholder(R.drawable.loading)
                                                .into(holder2.foodImage);

                                        final Food local2 = model;
                                        holder2.setItemClickListener(new ItemClickListener() {
                                            @Override
                                            public void onClick(View view, int position, boolean isLongClick) {

                                                Toast.makeText(RestaurantOrders.this, model.getName(), Toast.LENGTH_LONG).show();

                                            }
                                        });


                                    }


                                    @NonNull
                                    @Override
                                    public RestaurantOrderDetailsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                                        View view = LayoutInflater.from(parent.getContext())
                                                .inflate(R.layout.restaurant_order_detail_layout, parent, false);
                                        view.setMinimumWidth(parent.getMeasuredWidth());

                                        return new RestaurantOrderDetailsViewHolder(view);
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

                            } else if (isLongClick) {
                                if (model.getStatus().equals(Status.plasata)) {
//                                    View viewPopUp = inflater.inflate(R.layout.reset_name_pop_up, null);
                                    acceptOrder.setTitle("Acceptați această comandă?")
                                            .setPositiveButton("Da", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                    model.setStatus(Status.confirmata);
                                                    //driverOrders.child(model.getOrderId()).setValue(model);
                                                    restaurantOrders.child(model.getRestaurantId()).child("orders").child(model.getOrderId()).child("status").setValue(Status.confirmata);
                                                    restaurantOrders.child(model.getRestaurantId()).child("orders").child(model.getOrderId()).child("confirmed").setValue(true);

                                                    // orders.child(model.getUserId()).child(model.getOrderId()).child("status").setValue(Status.confirmata);
                                                    orders.child(model.getUserId()).child(model.getOrderId()).child("restaurants").addValueEventListener(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                                                Restaurant confirmedRestaurant = dataSnapshot.getValue(Restaurant.class);
                                                                if (confirmedRestaurant.getId().equals(model.getRestaurantId())) {
                                                                    confirmedRestaurant.setConfirmed(true);
                                                                    allRestaurantsConfirmed = true;
                                                                    orders.child(model.getUserId()).child(model.getOrderId()).child("restaurants").child(confirmedRestaurant.getId()).child("confirmed").setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            orders.child(model.getUserId()).child(model.getOrderId()).child("restaurants").addValueEventListener(new ValueEventListener() {
                                                                                @Override
                                                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                                                                        Restaurant confirmedRestaurant2 = dataSnapshot.getValue(Restaurant.class);
                                                                                        if (!confirmedRestaurant2.isConfirmed()) {
                                                                                            allRestaurantsConfirmed = false;
                                                                                        }
                                                                                    }

                                                                                    if (allRestaurantsConfirmed == true) {

                                                                                        orders.child(model.getUserId()).child(model.getOrderId()).child("restaurants").addValueEventListener(new ValueEventListener() {
                                                                                            @Override
                                                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                                                                                    Restaurant confirmedRestaurant3 = dataSnapshot.getValue(Restaurant.class);

                                                                                                    ordersHistory.child(model.getOrderId()).child("status").setValue(Status.confirmata);
                                                                                                    if (confirmedRestaurant3 != null) {
                                                                                                        restaurantOrders.child(confirmedRestaurant3.getId()).child("orders").child(model.getOrderId()).addValueEventListener(new ValueEventListener() {
                                                                                                            @Override
                                                                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                                                RestaurantOrder restaurantOrder = snapshot.getValue(RestaurantOrder.class);
                                                                                                                restaurantOrdersHistory.child(confirmedRestaurant3.getId()).child("orders").child(model.getOrderId()).setValue(restaurantOrder);
                                                                                                            }

                                                                                                            @Override
                                                                                                            public void onCancelled(@NonNull DatabaseError error) {

                                                                                                            }
                                                                                                        });
                                                                                                    }


                                                                                                }
                                                                                            }

                                                                                            @Override
                                                                                            public void onCancelled(@NonNull DatabaseError error) {

                                                                                            }
                                                                                        });

                                                                                        orders.child(model.getUserId()).child(model.getOrderId()).child("status").setValue(Status.confirmata).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                            @Override
                                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                                orders.child(model.getUserId()).child(model.getOrderId()).addValueEventListener(new ValueEventListener() {
                                                                                                    @Override
                                                                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                                        Order orderToBeAdded = snapshot.getValue(Order.class);
                                                                                                        driverOrders.child("orders").child(model.getOrderId()).setValue(orderToBeAdded).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                            @Override
                                                                                                            public void onSuccess(Void unused) {
//                                                                                                                String message = "Comanda a fost acceptata! Restaurantele au inceput sa o prepare";
//                                                                                                                NotificationCompat.Builder builder = new NotificationCompat.Builder(RestaurantOrders.this, "my_channel_id_01")
//                                                                                                                        .setSmallIcon(R.drawable.ic_baseline_restaurant_menu_24)
//                                                                                                                        .setContentTitle("Notificare noua")
//                                                                                                                        .setContentText(message)
//                                                                                                                        .setAutoCancel(true)
//                                                                                                                        .setDefaults(Notification.DEFAULT_ALL)
//                                                                                                                        .setPriority(Notification.PRIORITY_MAX);
//
//
//                                                                                                                NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
//                                                                                                                notificationManager.notify(1, builder.build());
                                                                                                            }
                                                                                                        });
                                                                                                    }

                                                                                                    @Override
                                                                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                                                                    }
                                                                                                });
                                                                                            }
                                                                                        });

                                                                                    }
                                                                                }

                                                                                @Override
                                                                                public void onCancelled(@NonNull DatabaseError error) {

                                                                                }
                                                                            });
                                                                        }
                                                                    });
                                                                }

                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                        }
                                                    });

                                                }
                                            }).setNegativeButton("Nu", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            model.setStatus(Status.anulata);
                                            //driverOrders.child(model.getOrderId()).setValue(model);
                                            restaurantOrders.child(model.getRestaurantId()).child("orders").child(model.getOrderId()).child("status").setValue(Status.anulata);
                                            orders.child(model.getUserId()).child(model.getOrderId()).child("status").setValue(Status.anulata);

                                            orders.child(model.getUserId()).child(model.getOrderId()).child("restaurants").addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                                        Restaurant cancelledRestaurant = dataSnapshot.getValue(Restaurant.class);
                                                        restaurantOrders.child(cancelledRestaurant.getId()).child("orders").child(model.getOrderId()).child("status").setValue(Status.anulata);
                                                        restaurantOrders.child(cancelledRestaurant.getId()).child("orders").child(model.getOrderId()).removeValue();


                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                        }
                                    })
                                            .setNeutralButton("Anulează", null)
                                            .create().show();

                                }
                            }

                        }

                    });

                }
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
