package eu.ase.ro.grupa1086.licentamanolachemariacatalina;

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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
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

import eu.ase.ro.grupa1086.licentamanolachemariacatalina.cart.ItemClickListener;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.Food;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.food.FoodInfo;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.food.FoodList;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.order.RestaurantOrders;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.principalmenus.RestaurantAccount;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.viewHolder.FoodViewHolder;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.viewHolder.RestaurantFoodViewHolder;

public class RestaurantProducts extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    RecyclerView recyclerView;
    //ImageView noProductsFound;

    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference foodList;
    FirebaseUser user;
    List<Food> listOfFoods = new ArrayList<>();

    ImageView nothingFound;
    TextView tvNoRestaurant;


    FirebaseRecyclerAdapter<Food, RestaurantFoodViewHolder> adapter;

    FloatingActionButton fabAddProduct;
    AlertDialog.Builder deleteAlert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_products);

        database = FirebaseDatabase.getInstance();

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Log.i("model", user.getUid());
        }
        foodList = database.getReference("food");


        recyclerView = (RecyclerView)findViewById(R.id.recyclerFood);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        deleteAlert = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogStyle));

        nothingFound = findViewById(R.id.noRestaurants);
        tvNoRestaurant = findViewById(R.id.tvNoRestaurants);

        loadFoodList(user.getUid());

        fabAddProduct = findViewById(R.id.fabAddProduct);

        fabAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addNewProduct = new Intent(getApplicationContext(), AddRestaurantProduct.class);
                startActivity(addNewProduct);
                overridePendingTransition(R.anim.slide_up, R.anim.slide_nothing);
            }
        });

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.products);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if(id == R.id.products) {
                    return true;
                } else if(id == R.id.orders) {
                    startActivity(new Intent(getApplicationContext(), RestaurantOrders.class));
                    overridePendingTransition(R.anim.slide_left2, R.anim.slide_right2);
                    finish();
                    return true;
                } else if(id == R.id.account) {
                    startActivity(new Intent(getApplicationContext(), RestaurantAccount.class));
                    overridePendingTransition(R.anim.slide_left2, R.anim.slide_right2);
                    finish();
                    return true;
                }
//                switch (item.getItemId()) {
//                    case R.id.products:
//                        return true;
//                    case R.id.orders:
//                        startActivity(new Intent(getApplicationContext(), RestaurantOrders.class));
//                        finish();
//                        overridePendingTransition(0, 0);
//                        return true;
//                    case R.id.account:
//                        startActivity(new Intent(getApplicationContext(), RestaurantAccount.class));
//                        finish();
//                        overridePendingTransition(0, 0);
//                        return true;
//                }
                return false;
            }
        });

    }

    private void loadFoodList(String userId) {
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("food")
                .child(userId)
                .orderByChild("id")
                .limitToLast(50);

        Log.i("queryyy", String.valueOf(query.getRef()));

        database.getReference("food").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()) {
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

        FirebaseRecyclerOptions<Food> options =
                new FirebaseRecyclerOptions.Builder<Food>()
                        .setQuery(query, Food.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<Food, RestaurantFoodViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull RestaurantFoodViewHolder holder, int position, @NonNull Food model) {
                holder.foodName.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage()).placeholder(R.drawable.loading)
                        .into(holder.imageView);

                listOfFoods.add(model);
                Log.i("model", listOfFoods.toString());

                holder.fabRemoveProduct.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                       deleteAlert.setTitle("Ștergere produs")
                                .setMessage("Ești sigur că dorești să ștergi produsul din lista de produse?")
                                .setPositiveButton("Da", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        database.getReference("food").child(userId).child(model.getId()).removeValue();
                                        Toast.makeText(getApplicationContext(), "Produsul a fost eliminat", Toast.LENGTH_LONG).show();

                                    }
                                }).setNegativeButton("Nu", null)
                                .create().show();


                    }
                });

                final Food local = model;
                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                        Intent productInfo = new Intent(RestaurantProducts.this, ProductInfo.class);
                        productInfo.putExtra("id", model.getId());
                        productInfo.putExtra("origin", "activityRestaurantProducts");
                        //Log.i("foodid", adapter.getRef(position).getKey());
                        startActivity(productInfo);
                        overridePendingTransition(R.anim.slide_up, R.anim.slide_nothing);
                    }
                });
            }

            @NonNull
            @Override
            public RestaurantFoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.restaurant_food_item, parent, false);
                view.setMinimumWidth(parent.getMeasuredWidth());

                return new RestaurantFoodViewHolder(view);
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }
}

