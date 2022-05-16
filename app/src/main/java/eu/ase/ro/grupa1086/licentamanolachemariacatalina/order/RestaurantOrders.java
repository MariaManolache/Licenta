package eu.ase.ro.grupa1086.licentamanolachemariacatalina.order;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
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
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.RestaurantOrder;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.Status;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.principalmenus.DriverMenu;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.principalmenus.RestaurantMenu;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.viewHolder.DriverOrderViewHolder;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.viewHolder.OrderDetailsViewHolder;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.viewHolder.OrderViewHolder;
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
    FirebaseUser user;

    List<RestaurantOrder> orderList = new ArrayList<RestaurantOrder>();

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
        Log.i("restaurantOrder", user.getUid());
        restaurants = database.getReference("restaurants");

        recyclerView = (RecyclerView) findViewById(R.id.restaurantOrdersList);
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
                .child("restaurantOrders")
                .child(user.getUid())
                .child("orders")
                .orderByChild("id")
                .limitToLast(50);

        FirebaseRecyclerOptions<RestaurantOrder> options =
                new FirebaseRecyclerOptions.Builder<RestaurantOrder>()
                        .setQuery(query, RestaurantOrder.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<RestaurantOrder, RestaurantOrderViewHolder>(options) {

            @NonNull
            @Override
            public RestaurantOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.order_layout, parent, false);
                view.setMinimumWidth(parent.getMeasuredWidth());

                return new RestaurantOrderViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull RestaurantOrderViewHolder holder, int position, @NonNull RestaurantOrder model) {

                orderList.add(model);
                orderId = model.getOrderId();
                Log.i("restaurantOrder", orderId);

                if (!model.getStatus().equals(Status.finalizata)) {

                    holder.restaurantsName.setText(model.getRestaurant().getName());
                    holder.orderStatus.setText(String.valueOf(model.getStatus()).substring(0, 1).toUpperCase(Locale.ROOT) + String.valueOf(model.getStatus()).replace("_", " ").substring(1));
                    holder.orderAddress.setText(String.valueOf(model.getAddress().getMapsAddress()));
                    holder.orderPriceTotal.setText("Total: " + model.getTotal() + " lei");
                    Picasso.with(getBaseContext()).load(model.getRestaurant().getImage())
                            .into(holder.restaurantImage);


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
