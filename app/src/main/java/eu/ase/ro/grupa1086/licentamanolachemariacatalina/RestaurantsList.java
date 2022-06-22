package eu.ase.ro.grupa1086.licentamanolachemariacatalina;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.util.Log;
import android.widget.SearchView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import eu.ase.ro.grupa1086.licentamanolachemariacatalina.cart.ItemClickListener;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.Restaurant;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.food.FoodList;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.viewHolder.RestaurantViewHolder;

public class RestaurantsList extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference restaurantsList;

    String categoryId = "";

    FirebaseRecyclerAdapter<Restaurant, RestaurantViewHolder> adapter;

    //ProgressDialog loader;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurants_list);


        //Firebase
        database = FirebaseDatabase.getInstance();
        restaurantsList = database.getReference("restaurants");

        recyclerView = (RecyclerView)findViewById(R.id.recyclerRestaurants);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

//        searchView = findViewById(R.id.searchView);

       // loader = new ProgressDialog(RestaurantsList.this);

        //Intent
        if(getIntent() != null) {
            categoryId = getIntent().getStringExtra("categoryId");
        }
        if(!categoryId.isEmpty() && categoryId != null) {
            loadRestaurantsList(categoryId);
        }


    }

    private void loadRestaurantsList(String categoryId) {

            Query query = FirebaseDatabase.getInstance()
                    .getReference()
                    .child("restaurants")
                    .orderByChild("categoryId").equalTo(categoryId)
                    .limitToLast(50);


            FirebaseRecyclerOptions<Restaurant> options =
                    new FirebaseRecyclerOptions.Builder<Restaurant>()
                            .setQuery(query, Restaurant.class)
                            .build();

            adapter = new FirebaseRecyclerAdapter<Restaurant, RestaurantViewHolder>(options) {
                @Override
                protected void onBindViewHolder(@NonNull RestaurantViewHolder holder, int position, @NonNull Restaurant model) {


//                    loader.setMessage("Adding the attraction to your trip");
//                    loader.setCanceledOnTouchOutside(false);
//                    loader.show();

                    holder.restaurantName.setText(model.getName());
                    //holder.setIcon(model.getImage());
//                    Glide.with(getBaseContext()).load(model.getImage()).into(holder.imageView);
                    Log.i("picasso", model.getImage());
                    Picasso.with(getBaseContext()).load(model.getImage()).placeholder(R.drawable.loading)
                            .into(holder.imageView);
                    //holder.deliveryTime.setText(model.getDeliveryTime());

//                    loader.dismiss();

                    final Restaurant local = model;
                    holder.setItemClickListener(new ItemClickListener() {
                        @Override
                        public void onClick(View view, int position, boolean isLongClick) {

                            //restaurantId
                            Intent foodList = new Intent(RestaurantsList.this, FoodList.class);
                            foodList.putExtra("restaurantId", model.getId());
                            startActivity(foodList);

                        }
                    });
                }

                @NonNull
                @Override
                public RestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.restaurant_item, parent, false);
                    view.setMinimumWidth(parent.getMeasuredWidth());

                    return new RestaurantViewHolder(view);
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

        getMenuInflater().inflate(R.menu.search_menu, menu);

        MenuItem item = menu.findItem(R.id.actionSearch);

        SearchView searchView = (SearchView) item.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchProcess(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchProcess(newText);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    private void searchProcess(String s) {
        String categoryIdName = categoryId + s;
        Log.i("category", categoryIdName);

        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("restaurants")
                .orderByChild("categoryIdName").startAt(categoryIdName).endAt(categoryIdName+"\uf8ff")
                .limitToLast(50);


        FirebaseRecyclerOptions<Restaurant> options =
                new FirebaseRecyclerOptions.Builder<Restaurant>()
                        .setQuery(query, Restaurant.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<Restaurant, RestaurantViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull RestaurantViewHolder holder, int position, @NonNull Restaurant model) {
                holder.restaurantName.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage())
                        .into(holder.imageView);
                //holder.deliveryTime.setText(model.getDeliveryTime());

                final Restaurant local = model;
                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                        //restaurantId
                        Intent foodList = new Intent(RestaurantsList.this, FoodList.class);
                        foodList.putExtra("restaurantId", adapter.getRef(position).getKey());
                        startActivity(foodList);

                    }
                });
            }

            @NonNull
            @Override
            public RestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.restaurant_item, parent, false);
                view.setMinimumWidth(parent.getMeasuredWidth());

                return new RestaurantViewHolder(view);
            }
        };

        adapter.startListening();
        recyclerView.setAdapter(adapter);

    }

}