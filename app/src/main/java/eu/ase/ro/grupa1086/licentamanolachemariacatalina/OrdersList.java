package eu.ase.ro.grupa1086.licentamanolachemariacatalina;

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

import eu.ase.ro.grupa1086.licentamanolachemariacatalina.account.Account;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.cart.ItemClickListener;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.Order;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.Restaurant;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.food.FoodList;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.viewHolder.OrderViewHolder;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.viewHolder.RestaurantViewHolder;

public class OrdersList extends AppCompatActivity {

    public RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;

    FirebaseRecyclerAdapter<Order, OrderViewHolder> adapter;

    FirebaseDatabase database;
    DatabaseReference orders;
    DatabaseReference restaurants;
    FirebaseUser user;

    String restaurantName;
    String restaurantImage;

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders_list);

        database = FirebaseDatabase.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        orders = database.getReference("orders").child(user.getUid());
        restaurants = database.getReference("restaurants");

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
                int id = item.getItemId();
                switch (item.getItemId()) {
                    case R.id.account:
                        startActivity(new Intent(getApplicationContext(), Account.class));
                        //finish();
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.restaurantsMenu:
                        startActivity(new Intent(getApplicationContext(), PrincipalMenu.class));
                        //finish();
                        overridePendingTransition(0, 0);
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
                .child("orders")
                .child(user.getUid())
                .orderByChild("id")
                .limitToLast(50);

        FirebaseRecyclerOptions<Order> options =
                new FirebaseRecyclerOptions.Builder<Order>()
                        .setQuery(query, Order.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<Order, OrderViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull OrderViewHolder holder, int position, @NonNull Order model) {

                restaurantName = null;
                restaurantImage = null;

                if (model.getCart().size() == 1) {
                    restaurants.child(model.getCart().get(0).getRestaurantId()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Restaurant restaurant = snapshot.getValue(Restaurant.class);
                            //Log.i("restaurant", restaurant.toString());

                            restaurantName = restaurant.getName();
                            restaurantImage = restaurant.getImage();

                            holder.restaurantsName.setText(restaurantName);
                            holder.orderStatus.setText(String.valueOf(model.getStatus()));
                            holder.orderAddress.setText(String.valueOf(model.getAddress().getMapsAddress()));
                            holder.orderPriceTotal.setText("Total: " + model.getTotal() + " lei");
                            Picasso.with(getBaseContext()).load(restaurantImage)
                                    .into(holder.restaurantImage);

                            restaurantName = null;

                            final Order local = model;
                            holder.setItemClickListener(new ItemClickListener() {
                                @Override
                                public void onClick(View view, int position, boolean isLongClick) {

                                    if(!isLongClick) {
                                        Toast.makeText(OrdersList.this, model.getCart().toString(), Toast.LENGTH_LONG).show();
                                    } else {
                                        Intent orderDetails = new Intent(OrdersList.this, OrderDetails.class);
                                        orderDetails.putExtra("orderId", adapter.getRef(position).getKey());
                                        orderDetails.putExtra("restaurantName", restaurant.getName());
                                        orderDetails.putExtra("restaurantImage", restaurant.getImage());
                                        orderDetails.putExtra("orderStatus", String.valueOf(local.getStatus()));
                                        orderDetails.putExtra("address", model.getAddress().getMapsAddress());
                                        orderDetails.putExtra("total", String.valueOf(local.getTotal()));
                                        startActivity(orderDetails);
                                    }

                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                } else {
                    for (int i = 0; i < model.getCart().size(); i++) {
                        Log.i("restaurants", String.valueOf(model.getCart().size()));
//                        restaurantName = null;
//                        restaurantImage = null;

                        restaurants.child(model.getCart().get(i).getRestaurantId()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                                    Restaurant restaurant = snapshot.getValue(Restaurant.class);
                                    Log.i("restaurant", restaurant.toString());

                                    if (restaurantName == null) {
                                        restaurantName = restaurant.getName();
                                    } else {
                                        if (!restaurantName.contains(restaurant.getName())) {
                                            restaurantName += ", " + restaurant.getName();
                                        }
                                    }
                                    restaurantImage = restaurant.getImage();

//                            if(restaurantName == null) {
//                                restaurantName = restaurant.getName();
//                            } else {
//                                restaurantName += ", " + restaurant.getName();
//                            }
////                            if(restaurantImage == null) {
//                                restaurantImage = restaurant.getImage();
////                            }

                                    holder.restaurantsName.setText(restaurantName);
                                    holder.orderStatus.setText(String.valueOf(model.getStatus()));
                                    holder.orderAddress.setText(String.valueOf(model.getAddress().getMapsAddress()));
                                    holder.orderPriceTotal.setText("Total: " + model.getTotal() + " lei");
                                    Picasso.with(getBaseContext()).load(restaurantImage)
                                            .into(holder.restaurantImage);

                                    final Order local = model;
                                    holder.setItemClickListener(new ItemClickListener() {
                                        @Override
                                        public void onClick(View view, int position, boolean isLongClick) {

                                            if (!isLongClick) {
                                                Toast.makeText(OrdersList.this, model.getCart().toString(), Toast.LENGTH_LONG).show();
                                            } else {
                                                Intent orderDetails = new Intent(OrdersList.this, OrderDetails.class);
                                                orderDetails.putExtra("orderId", adapter.getRef(position).getKey());
                                                orderDetails.putExtra("restaurantName", restaurantName);
                                                orderDetails.putExtra("restaurantImage", restaurantImage);
                                                orderDetails.putExtra("orderStatus", String.valueOf(local.getStatus()));
                                                orderDetails.putExtra("address", model.getAddress().getMapsAddress());
                                                orderDetails.putExtra("total", String.valueOf(local.getTotal()));
                                                startActivity(orderDetails);
                                            }

                                        }
                                    });

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                }
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

        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }
}