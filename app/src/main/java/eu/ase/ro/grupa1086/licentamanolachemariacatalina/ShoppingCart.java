package eu.ase.ro.grupa1086.licentamanolachemariacatalina;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import java.util.List;

import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.Cart;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.Food;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.Restaurant;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.viewHolder.CartViewHolder;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.viewHolder.RestaurantViewHolder;

public class ShoppingCart extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference cart;

    FirebaseAuth firebaseAuth;
    FirebaseUser user;

    TextView tvTotalPrice;
    Button btnPlaceOrder;
    String id;
    Float total = 0.0f;

    FirebaseRecyclerAdapter<Food, CartViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);

        //Firebase
        database = FirebaseDatabase.getInstance();

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        id = user.getUid();
        cart = database.getInstance().getReference("carts").child(id).child("foodList");

        //RecyclerView
        recyclerView = findViewById(R.id.cartList);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        tvTotalPrice = findViewById(R.id.total);
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder);

        loadListFood();

    }

    private void loadListFood() {

        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("carts")
                .child(id)
                .child("foodList")
                .orderByChild("id")
                .limitToLast(50);


        FirebaseRecyclerOptions<Food> options =
                new FirebaseRecyclerOptions.Builder<Food>()
                        .setQuery(query, Food.class)
                        .build();


        adapter = new FirebaseRecyclerAdapter<Food, CartViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull Food model) {

                holder.tvCartName.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage())
                        .into(holder.imgCartCount);

                holder.tvCartPrice.setText(String.valueOf(model.getPrice()));
                holder.tvCartQuantity.setText(String.valueOf(model.getQuantity()));
                holder.tvCartValue.setText(String.valueOf(model.getPrice() * model.getQuantity()));

                total += model.getPrice() * model.getQuantity();

                if(total > 0) {
                    tvTotalPrice.setText(total + " LEI");
                } else
                {
                    tvTotalPrice.setText("0 LEI");
                }

                final Food local = model;
                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                        Toast.makeText(getApplicationContext(), local.getName(), Toast.LENGTH_LONG).show();
                        Intent foodInfo = new Intent(ShoppingCart.this, FoodInfo.class);
                        foodInfo.putExtra("idCartItem", adapter.getRef(position).getKey());
                        foodInfo.putExtra("quantity", String.valueOf(local.getQuantity()));
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

        recyclerView.setAdapter(adapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }
}