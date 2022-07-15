package eu.ase.ro.grupa1086.licentamanolachemariacatalina.order;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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

import eu.ase.ro.grupa1086.licentamanolachemariacatalina.R;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.cart.ItemClickListener;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.Food;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.Restaurant;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.viewHolder.OrderDetailsViewHolder;

public class OrderDetails extends AppCompatActivity {

    TextView restaurantName;
    TextView orderStatus;
    TextView orderAddress;
    TextView orderTotal;
    ImageView restaurantImage;

    String orderId;
    String restaurantNameString;
    String restaurantImageString;
    String status;
    String total;
    String address;

    FirebaseRecyclerAdapter<Food, OrderDetailsViewHolder> adapter;

    DatabaseReference cart;
    DatabaseReference restaurants;
    DatabaseReference restaurantAddresses;
    FirebaseDatabase database;
    FirebaseUser user;

    List<Food> foods = new ArrayList<Food>();

    RecyclerView foodList;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        restaurantName = findViewById(R.id.restaurantName);
        orderStatus = findViewById(R.id.orderStatus);
        orderAddress = findViewById(R.id.orderAddress);
        orderTotal = findViewById(R.id.orderPriceTotal);
        restaurantImage = findViewById(R.id.restaurantImage);

        foodList = findViewById(R.id.orderDetails);
        foodList.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        foodList.setLayoutManager(layoutManager);

        if (getIntent() != null) {
            orderId = getIntent().getStringExtra("orderId");
            restaurantNameString = getIntent().getStringExtra("restaurantName");
            restaurantImageString = getIntent().getStringExtra("restaurantImage");
            address = getIntent().getStringExtra("address");
            status = getIntent().getStringExtra("orderStatus");
            total = getIntent().getStringExtra("total");

            restaurantName.setText(restaurantNameString);
            //restaurantImage.setImageURI(Uri.parse(restaurantImageString));
            orderStatus.setText(status);
            orderTotal.setText(total + " lei");
            orderAddress.setText(address);

            Picasso.with(getBaseContext()).load(restaurantImageString)
                    .into(restaurantImage);

        }

        database = FirebaseDatabase.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        cart = database.getReference().child("orders").child(user.getUid()).child(orderId).child("cart");
        restaurants = database.getReference("restaurants");
        restaurantAddresses = database.getReference("orders").child(user.getUid()).child(orderId).child("restaurantAddress");


        loadOrderDetails();
//        cart.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                    Food food = dataSnapshot.getValue(Food.class);
//                    Log.i("foods", food.toString());
//                    foods.add(food);
//                }
//
//                loadOrderDetails();
//
////                OrderDetailsAdapter adapter = new OrderDetailsAdapter(foods);
////                adapter.notifyDataSetChanged();
////                foodList.setAdapter(adapter);
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });


    }

    private void loadOrderDetails() {

        Query query = cart
                .orderByChild("id")
                .limitToLast(50);

        FirebaseRecyclerOptions<Food> options =
                new FirebaseRecyclerOptions.Builder<Food>()
                        .setQuery(query, Food.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<Food, OrderDetailsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull OrderDetailsViewHolder holder, int position, @NonNull Food model) {

                holder.foodName.setText(model.getName());

                restaurantAddresses.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Restaurant restaurant = dataSnapshot.getValue(Restaurant.class);
                            if (restaurant != null && model.getRestaurantId().equals(restaurant.getId())) {
                                holder.restaurantName.setText(restaurant.getName() + " : ");
                            }
                        }

                        holder.foodPrice.setText(String.valueOf(model.getPrice()));
                        holder.foodQuantity.setText(String.valueOf(model.getQuantity()));
                        holder.foodTotal.setText(model.getPrice() * model.getQuantity() + " lei");
                        Picasso.with(getBaseContext()).load(model.getImage()).placeholder(R.drawable.loading)
                                .into(holder.foodImage);

                        final Food local = model;
                        holder.setItemClickListener(new ItemClickListener() {
                            @Override
                            public void onClick(View view, int position, boolean isLongClick) {

                                Toast.makeText(OrderDetails.this, model.getName(), Toast.LENGTH_LONG).show();

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

        foodList.setAdapter(adapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }
}


