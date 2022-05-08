package eu.ase.ro.grupa1086.licentamanolachemariacatalina.order;

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
import android.widget.ImageView;
import android.widget.TextView;
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

import eu.ase.ro.grupa1086.licentamanolachemariacatalina.principalmenus.PrincipalMenu;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.R;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.account.Account;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.cart.ItemClickListener;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.Food;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.Order;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.Restaurant;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.food.FoodInfo;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.viewHolder.OrderDetailsViewHolder;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.viewHolder.OrderViewHolder;

public class OrdersList extends AppCompatActivity {

    public RecyclerView recyclerView;
    //public RecyclerView secondRecyclerView;
    public RecyclerView.LayoutManager layoutManager;
    public RecyclerView.LayoutManager secondLayoutManager;

    FirebaseRecyclerAdapter<Order, OrderViewHolder> adapter;
    FirebaseRecyclerAdapter<Food, OrderDetailsViewHolder> secondAdapter;


    FirebaseDatabase database;
    DatabaseReference orders;
    DatabaseReference restaurants;
    DatabaseReference restaurantAddresses;
    DatabaseReference cart;
    DatabaseReference ratings;
    DatabaseReference food;
    FirebaseUser user;

    String restaurantName;
    String restaurantImage;
    String orderId;

    BottomNavigationView bottomNavigationView;

    ImageView imgNoOrderFound;
    TextView tvNoOrderFound;

    List<Order> orderList = new ArrayList<Order>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders_list);

        database = FirebaseDatabase.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        orders = database.getReference("orders").child(user.getUid());
        restaurants = database.getReference("restaurants");
        restaurantAddresses = database.getReference("orders").child(user.getUid());
        cart = database.getReference().child("orders").child(user.getUid());
        food = database.getReference().child("food");

        recyclerView = (RecyclerView) findViewById(R.id.ordersList);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        imgNoOrderFound = findViewById(R.id.imgNoOrderFound);
        tvNoOrderFound = findViewById(R.id.tvNoOrderFound);

//        secondRecyclerView = findViewById(R.id.orderDetails);
//        //secondRecyclerView.setHasFixedSize(true);
//        secondLayoutManager = new LinearLayoutManager(this);
//        secondRecyclerView.setLayoutManager(secondLayoutManager);


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
                        finish();
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.restaurantsMenu:
                        startActivity(new Intent(getApplicationContext(), PrincipalMenu.class));
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
                .child("orders")
                .child(user.getUid())
                .orderByChild("id")
                .limitToLast(50);

        if(query == null) {
            imgNoOrderFound.setVisibility(View.VISIBLE);
            tvNoOrderFound.setVisibility(View.VISIBLE);
        } else {
            imgNoOrderFound.setVisibility(View.GONE);
            tvNoOrderFound.setVisibility(View.GONE);
        }

        FirebaseRecyclerOptions<Order> options =
                new FirebaseRecyclerOptions.Builder<Order>()
                        .setQuery(query, Order.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<Order, OrderViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull OrderViewHolder holder, int position, @NonNull Order model) {

                restaurantName = null;
                restaurantImage = null;
                orderList.add(model);
                orderId = model.getId();

                orders.child(model.getId()).child("restaurantAddress").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Restaurant restaurant = dataSnapshot.getValue(Restaurant.class);
                            if(restaurantName == null) {
                                restaurantName = restaurant.getName();
                                restaurantImage = restaurant.getImage();
                            } else {
                                restaurantName += ", " + restaurant.getName();
                            }
                        }

                        holder.restaurantsName.setText(restaurantName);
                        holder.orderStatus.setText(String.valueOf(model.getStatus()));
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

                                if(!isLongClick) {
                                    //Toast.makeText(OrdersList.this, model.getCart().toString(), Toast.LENGTH_LONG).show();
//                                    loadOrderDetails(model.getId());

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

                                    cart = database.getReference().child("orders").child(user.getUid()).child(model.getId()).child("cart");
                                    restaurantAddresses = database.getReference("orders").child(user.getUid()).child(model.getId()).child("restaurantAddress");

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
                                                        if(model.getRestaurantId().equals(restaurant.getId())) {
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

                                                            Toast.makeText(OrdersList.this, model.getName(), Toast.LENGTH_LONG).show();
//                                                            showRatingDialog(model.getId());

                                                                Intent foodInfo = new Intent(OrdersList.this, FoodInfo.class);
                                                                foodInfo.putExtra("origin", "ordersList");
                                                                foodInfo.putExtra("orderId", local.getId());
                                                                foodInfo.putExtra("quantity", local2.getQuantity());
                                                                foodInfo.putExtra("foodId", model.getId());
                                                                startActivity(foodInfo);

                                                        }
                                                    });
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });

//                restaurants.child(model.getRestaurantId()).addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        Restaurant restaurant = snapshot.getValue(Restaurant.class);
//                        holder.restaurantName.setText(restaurant.getName() + " : ");
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });


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

                                } else {
                                    Intent orderDetails = new Intent(OrdersList.this, OrderDetails.class);
                                    orderDetails.putExtra("orderId", model.getId());
                                    orderDetails.putExtra("restaurantName", restaurantName2);
                                    orderDetails.putExtra("restaurantImage", restaurantImage2);
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

//                if (model.getCart().size() == 1) {
//                    restaurants.child(model.getCart().get(0).getRestaurantId()).addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot snapshot) {
//                            Restaurant restaurant = snapshot.getValue(Restaurant.class);
//                            //Log.i("restaurant", restaurant.toString());
//
//                            restaurantName = restaurant.getName();
//                            restaurantImage = restaurant.getImage();
//
//                            holder.restaurantsName.setText(restaurantName);
//                            holder.orderStatus.setText(String.valueOf(model.getStatus()));
//                            holder.orderAddress.setText(String.valueOf(model.getAddress().getMapsAddress()));
//                            holder.orderPriceTotal.setText("Total: " + model.getTotal() + " lei");
//                            Picasso.with(getBaseContext()).load(restaurantImage)
//                                    .into(holder.restaurantImage);
//
//                            restaurantName = null;
//
//                            final Order local = model;
//                            holder.setItemClickListener(new ItemClickListener() {
//                                @Override
//                                public void onClick(View view, int position, boolean isLongClick) {
//
//                                    if(!isLongClick) {
//                                        Toast.makeText(OrdersList.this, model.getCart().toString(), Toast.LENGTH_LONG).show();
//                                    } else {
//                                        Intent orderDetails = new Intent(OrdersList.this, OrderDetails.class);
//                                        orderDetails.putExtra("orderId", model.getId());
//                                        orderDetails.putExtra("restaurantName", restaurant.getName());
//                                        orderDetails.putExtra("restaurantImage", restaurant.getImage());
//                                        orderDetails.putExtra("orderStatus", String.valueOf(local.getStatus()));
//                                        orderDetails.putExtra("address", model.getAddress().getMapsAddress());
//                                        orderDetails.putExtra("total", String.valueOf(local.getTotal()));
//                                        startActivity(orderDetails);
//                                    }
//
//                                }
//                            });
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError error) {
//
//                        }
//                    });
//
//                } else {
//
//                    for (int i = 0; i < model.getCart().size(); i++) {
//                        Log.i("restaurants", String.valueOf(model.getCart().size()));
////                        restaurantName = null;
////                        restaurantImage = null;
//
//                        restaurants.child(model.getCart().get(i).getRestaurantId()).addValueEventListener(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//
//                                    Restaurant restaurant = snapshot.getValue(Restaurant.class);
//                                    Log.i("restaurant", restaurant.toString());
//
//                                    if (restaurantName == null) {
//                                        restaurantName = restaurant.getName();
//                                    } else {
//                                        if (!restaurantName.contains(restaurant.getName())) {
//                                            restaurantName += ", " + restaurant.getName();
//                                        }
//                                    }
//                                    restaurantImage = restaurant.getImage();
//
////                            if(restaurantName == null) {
////                                restaurantName = restaurant.getName();
////                            } else {
////                                restaurantName += ", " + restaurant.getName();
////                            }
//////                            if(restaurantImage == null) {
////                                restaurantImage = restaurant.getImage();
//////                            }
//
//                                    holder.restaurantsName.setText(restaurantName);
//                                    holder.orderStatus.setText(String.valueOf(model.getStatus()));
//                                    holder.orderAddress.setText(String.valueOf(model.getAddress().getMapsAddress()));
//                                    holder.orderPriceTotal.setText("Total: " + model.getTotal() + " lei");
//                                    Picasso.with(getBaseContext()).load(restaurantImage)
//                                            .into(holder.restaurantImage);
//
//                                    final Order local = model;
//                                    holder.setItemClickListener(new ItemClickListener() {
//                                        @Override
//                                        public void onClick(View view, int position, boolean isLongClick) {
//
//                                            if (!isLongClick) {
//                                                Toast.makeText(OrdersList.this, model.getCart().toString(), Toast.LENGTH_LONG).show();
//                                            } else {
//                                                Intent orderDetails = new Intent(OrdersList.this, OrderDetails.class);
//                                                orderDetails.putExtra("orderId", model.getId());
//                                                orderDetails.putExtra("restaurantName", restaurantName);
//                                                orderDetails.putExtra("restaurantImage", restaurantImage);
//                                                orderDetails.putExtra("orderStatus", String.valueOf(local.getStatus()));
//                                                orderDetails.putExtra("address", model.getAddress().getMapsAddress());
//                                                orderDetails.putExtra("total", String.valueOf(local.getTotal()));
//                                                startActivity(orderDetails);
//                                            }
//
//                                        }
//                                    });
//
//                                }
//                            }
//
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError error) {
//
//                            }
//                        });
//                    }
//
//                }

                if(orderList.size() == 0) {
                    Log.i("ordersList", String.valueOf(orderList.size()));
                    imgNoOrderFound.setVisibility(View.VISIBLE);
                    tvNoOrderFound.setVisibility(View.VISIBLE);

                    tvNoOrderFound.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent principalMenu = new Intent(OrdersList.this, PrincipalMenu.class);
                            startActivity(principalMenu);
                            finish();
                        }
                    });
                } else {
                    imgNoOrderFound.setVisibility(View.GONE);
                    tvNoOrderFound.setVisibility(View.GONE);
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

    private void loadOrderDetails(String orderId) {
        cart = database.getReference().child("orders").child(user.getUid()).child(orderId).child("cart");
        restaurantAddresses = database.getReference("orders").child(user.getUid()).child(orderId).child("restaurantAddress");

        Query query = cart
                .orderByChild("id")
                .limitToLast(50);

        FirebaseRecyclerOptions<Food> options =
                new FirebaseRecyclerOptions.Builder<Food>()
                        .setQuery(query, Food.class)
                        .build();

        secondAdapter = new FirebaseRecyclerAdapter<Food, OrderDetailsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull OrderDetailsViewHolder holder, int position, @NonNull Food model) {

                holder.foodName.setText(model.getName());

                restaurantAddresses.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Restaurant restaurant = dataSnapshot.getValue(Restaurant.class);
                            if(model.getRestaurantId().equals(restaurant.getId())) {
                                holder.restaurantName.setText(restaurant.getName() + " : ");
                            }
                        }

                        holder.foodPrice.setText(String.valueOf(model.getPrice()));
                        holder.foodQuantity.setText(String.valueOf(model.getQuantity()));
                        holder.foodTotal.setText(model.getPrice() * model.getQuantity() + " lei");
                        Picasso.with(getBaseContext()).load(model.getImage())
                                .into(holder.foodImage);

                        final Food local = model;
                        holder.setItemClickListener(new ItemClickListener() {
                            @Override
                            public void onClick(View view, int position, boolean isLongClick) {

                                Toast.makeText(OrdersList.this, model.getName(), Toast.LENGTH_LONG).show();

                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

//                restaurants.child(model.getRestaurantId()).addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        Restaurant restaurant = snapshot.getValue(Restaurant.class);
//                        holder.restaurantName.setText(restaurant.getName() + " : ");
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });


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

        //secondRecyclerView.setAdapter(secondAdapter);
        secondAdapter.startListening();

    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
        //secondAdapter.startListening();
    }
}

class ParentViewHolder
        extends RecyclerView.ViewHolder {

    private TextView ParentItemTitle;
    private RecyclerView ChildRecyclerView;

    ParentViewHolder(final View itemView) {
        super(itemView);

        ParentItemTitle
                = itemView
                .findViewById(
                        R.id.ordersList);
        ChildRecyclerView
                = itemView
                .findViewById(
                        R.id.orderDetails);
    }
}