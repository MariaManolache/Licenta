package eu.ase.ro.grupa1086.licentamanolachemariacatalina;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.Category;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.Restaurant;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.databinding.ActivityHomeBinding;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.viewHolder.MenuViewHolder;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.viewHolder.RestaurantViewHolder;

public class RestaurantsList extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference restaurantsList;

    String categoryId = "";

    FirebaseRecyclerAdapter<Restaurant, RestaurantViewHolder> adapter;

    SearchView searchView;

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

        searchView = findViewById(R.id.searchView);

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
                    holder.restaurantName.setText(model.getName());
                    Picasso.with(getBaseContext()).load(model.getImage())
                            .into(holder.imageView);

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

            recyclerView.setAdapter(adapter);
        }



    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.search_menu, menu);
//
//        MenuItem menuItem = menu.findItem(R.id.actionSearch);
//
//        SearchView searchView = (SearchView) menuItem.getActionView();
//        searchView.setQueryHint(getString(R.string.search_favourite_restaurant));
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//
//                search(newText);
//                return true;
//            }
//        });
//
//        return false;
//    }
//
//    private void search(String text) {
//        Query query = FirebaseDatabase.getInstance()
//                .getReference()
//                .child("restaurants")
//                .orderByChild("categoryId").equalTo(categoryId)
//                .orderByChild("name").startAt(text)
//                .limitToLast(50);
//
//        FirebaseRecyclerOptions<Restaurant> options =
//                new FirebaseRecyclerOptions.Builder<Restaurant>()
//                        .setQuery(query, Restaurant.class)
//                        .build();
//
//        adapter = new FirebaseRecyclerAdapter<Restaurant, RestaurantViewHolder>(options) {
//            @Override
//            protected void onBindViewHolder(@NonNull RestaurantViewHolder holder, int position, @NonNull Restaurant model) {
//                holder.restaurantName.setText(model.getName());
//                Picasso.with(getBaseContext()).load(model.getImage())
//                        .into(holder.imageView);
//
//                final Restaurant local = model;
//                holder.setItemClickListener(new ItemClickListener() {
//                    @Override
//                    public void onClick(View view, int position, boolean isLongClick) {
//
//                        //restaurantId
//                        Intent foodList = new Intent(RestaurantsList.this, FoodList.class);
//                        foodList.putExtra("restaurantId", adapter.getRef(position).getKey());
//                        startActivity(foodList);
//
//                    }
//                });
//            }
//
//            @NonNull
//            @Override
//            public RestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//                View view = LayoutInflater.from(parent.getContext())
//                        .inflate(R.layout.restaurant_item, parent, false);
//                view.setMinimumWidth(parent.getMeasuredWidth());
//
//                return new RestaurantViewHolder(view);
//            }
//        };
//
//        recyclerView.setAdapter(adapter);
//    }
}