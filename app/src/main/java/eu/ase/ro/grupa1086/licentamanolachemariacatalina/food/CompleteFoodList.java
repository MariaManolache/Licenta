package eu.ase.ro.grupa1086.licentamanolachemariacatalina.food;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import eu.ase.ro.grupa1086.licentamanolachemariacatalina.R;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.cart.ItemClickListener;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.Food;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.Restaurant;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.viewHolder.AllFoodViewHolder;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.viewHolder.AllRestaurantNameViewHolder;

public class CompleteFoodList extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    RecyclerView recyclerViewString;
    RecyclerView.LayoutManager layoutManagerString;

    FirebaseDatabase database;
    DatabaseReference foodList;
    DatabaseReference restaurantsList;

    String restaurantId = "";
    String restaurantName = "";

    FirebaseRecyclerAdapter<DataSnapshot, AllRestaurantNameViewHolder> stringAdapter;
    FirebaseRecyclerAdapter<Food, AllFoodViewHolder> adapter;

    List<String> restaurantsName = new ArrayList<String>();
    int same = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_food_list);

        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24);// set drawable icon
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //Firebase
        database = FirebaseDatabase.getInstance();
        foodList = database.getReference("food");
        restaurantsList = database.getReference("restaurants");

        recyclerViewString = (RecyclerView) findViewById(R.id.recyclerRestaurantName);
        recyclerViewString.setHasFixedSize(true);
        layoutManagerString = new LinearLayoutManager(this);
        recyclerViewString.setLayoutManager(layoutManagerString);

//        recyclerView = (RecyclerView) findViewById(R.id.recyclerAllFood);
//        recyclerView.setHasFixedSize(true);
//        layoutManager = new LinearLayoutManager(this);
//        recyclerView.setLayoutManager(layoutManager);

        String emptyString = null;
        loadAllRestaurantsName();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private void loadAllRestaurantsName() {
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("food")
                .orderByKey()
                .limitToLast(50);

        Log.i("queryRestaurant", String.valueOf(query.getRef()));

        FirebaseRecyclerOptions<DataSnapshot> options =
                new FirebaseRecyclerOptions.Builder<DataSnapshot>()
                        .setQuery(query, new SnapshotParser<DataSnapshot>() {
                            public DataSnapshot parseSnapshot(@NonNull DataSnapshot snapshot) {
                                return snapshot;
                            }
                        })
                        .build();

        stringAdapter = new FirebaseRecyclerAdapter<DataSnapshot, AllRestaurantNameViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull AllRestaurantNameViewHolder holder, int position, @NonNull DataSnapshot model) {

                String entry = model.getKey();
                Log.i("queryRestaurant", entry);
                restaurantsList.child(entry).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Restaurant restaurant = snapshot.getValue(Restaurant.class);
                        holder.restaurantName.setText(restaurant.getName());

                        loadAllFoodList(entry, holder);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                final String local = entry;

                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                    }
                });
            }

            @NonNull
            @Override
            public AllRestaurantNameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.all_food_restaurant_name, parent, false);
                view.setMinimumWidth(parent.getMeasuredWidth());

                return new AllRestaurantNameViewHolder(view);
            }
        };

        recyclerViewString.setAdapter(stringAdapter);
        //restaurantsName = new ArrayList<String>();
    }

    private void loadAllFoodList(String id, @NonNull AllRestaurantNameViewHolder holder) {

        Query query = FirebaseDatabase.getInstance()
                    .getReference()
                    .child("food")
                    .child(id)
                    .orderByChild("id")
                    .limitToLast(50);

        FirebaseRecyclerOptions<Food> options =
                new FirebaseRecyclerOptions.Builder<Food>()
                        .setQuery(query, Food.class)
                        .build();

//        restaurantsName = new ArrayList<String>();
//        restaurantsName = null;

        adapter = new FirebaseRecyclerAdapter<Food, AllFoodViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull AllFoodViewHolder holder, int position, @NonNull Food model) {
                holder.foodName.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage()).placeholder(R.drawable.loading)
                        .into(holder.imageView);


                // same = 0;
//                restaurantsList.child(model.getRestaurantId()).child("name").addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        restaurantName = snapshot.getValue(String.class);
//                        restaurantsName.add(restaurantName);
//
//                        if (restaurantsName.size() == 1) {
//                            holder.restaurantName.setVisibility(View.VISIBLE);
//                            holder.restaurantName.setText(restaurantName);
//                        } else {
//                            if (restaurantsName.get(restaurantsName.size() - 1).equals(restaurantsName.get(restaurantsName.size() - 2))) {
//                                holder.restaurantName.setVisibility(View.GONE);
////                                if(model.getName().equals("Chicken and corn")) {
////                                    holder.restaurantName.setVisibility(View.VISIBLE);
////                                    holder.restaurantName.setText(restaurantName);
////                                }
//                            } else {
//                                holder.restaurantName.setVisibility(View.VISIBLE);
//                                holder.restaurantName.setText(restaurantName);
//                            }
//                        }
//
//                        Log.i("restaurantsName", restaurantsName.toString());
//                        if (restaurantsName.size() == 2) {
//                            restaurantsName.remove(restaurantsName.get(0));
//                        }
//
//
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });

                //pana aici

//                for(int i = 0; i < restaurantsName.size(); i++) {
//                    if(restaurantName.equals(restaurantsName.get(i))) {
//                        same = 1;
//                    }
//                }
//                if(same == 0) {
//                    restaurantsName.add(restaurantName);
//                    holder.restaurantName.setVisibility(View.VISIBLE);
//                    holder.restaurantName.setText(restaurantName);
//                } else if (same == 1){
//                    holder.restaurantName.setVisibility(View.GONE);
//                }
//
//                same = 0;

//                restaurantsName = new ArrayList<String>();

                final Food local = model;
                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                        //newActivity
                        Intent foodInfo = new Intent(CompleteFoodList.this, FoodInfo.class);
                        foodInfo.putExtra("id", model.getId());
                        foodInfo.putExtra("restaurantId", model.getRestaurantId());
                        foodInfo.putExtra("origin", "activityFoodList");
                        //Log.i("foodid", adapter.getRef(position).getKey());
                        startActivity(foodInfo);
                    }
                });
            }

            @NonNull
            @Override
            public AllFoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.all_food_item, parent, false);
                view.setMinimumWidth(parent.getMeasuredWidth());

                return new AllFoodViewHolder(view);
            }
        };

        layoutManager = new LinearLayoutManager(getApplicationContext());
        //holder.secondRecyclerView.setHasFixedSize(true);
        holder.secondRecyclerView.setLayoutManager(layoutManager);
        holder.secondRecyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
        stringAdapter.startListening();
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
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("food")
                .orderByKey()
                .limitToLast(50);

        Log.i("queryRestaurant", String.valueOf(query.getRef()));

        FirebaseRecyclerOptions<DataSnapshot> options =
                new FirebaseRecyclerOptions.Builder<DataSnapshot>()
                        .setQuery(query, new SnapshotParser<DataSnapshot>() {
                            public DataSnapshot parseSnapshot(@NonNull DataSnapshot snapshot) {
                                return snapshot;
                            }
                        })
                        .build();

        stringAdapter = new FirebaseRecyclerAdapter<DataSnapshot, AllRestaurantNameViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull AllRestaurantNameViewHolder holder, int position, @NonNull DataSnapshot model) {

                String entry = model.getKey();
                Log.i("queryRestaurant", entry);
                restaurantsList.child(entry).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Restaurant restaurant = snapshot.getValue(Restaurant.class);
                        holder.restaurantName.setText(restaurant.getName());

                        Query query2 = FirebaseDatabase.getInstance()
                                .getReference()
                                .child("food")
                                .child(entry)
                                .orderByChild("name").startAt(s).endAt(s + "\uf8ff")
                                .limitToLast(50);

                        query2.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                if(dataSnapshot.exists()) {

                                    holder.restaurantName.setVisibility(View.VISIBLE);
                                    Log.i("queryAllFood", String.valueOf(query.getRef()));

                                    FirebaseRecyclerOptions<Food> options =
                                            new FirebaseRecyclerOptions.Builder<Food>()
                                                    .setQuery(query2, Food.class)
                                                    .build();

                                    adapter = new FirebaseRecyclerAdapter<Food, AllFoodViewHolder>(options) {
                                        @Override
                                        protected void onBindViewHolder(@NonNull AllFoodViewHolder holder, int position, @NonNull Food model) {
                                            holder.foodName.setText(model.getName());
                                            Picasso.with(getBaseContext()).load(model.getImage()).placeholder(R.drawable.loading)
                                                    .into(holder.imageView);


                                            final Food local = model;
                                            holder.setItemClickListener(new ItemClickListener() {
                                                @Override
                                                public void onClick(View view, int position, boolean isLongClick) {

                                                    //newActivity
                                                    Intent foodInfo = new Intent(CompleteFoodList.this, FoodInfo.class);
                                                    foodInfo.putExtra("id", model.getId());
                                                    foodInfo.putExtra("restaurantId", model.getRestaurantId());
                                                    foodInfo.putExtra("origin", "activityFoodList");
                                                    //Log.i("foodid", adapter.getRef(position).getKey());
                                                    startActivity(foodInfo);
                                                }
                                            });
                                        }

                                        @NonNull
                                        @Override
                                        public AllFoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                                            View view = LayoutInflater.from(parent.getContext())
                                                    .inflate(R.layout.all_food_item, parent, false);
                                            view.setMinimumWidth(parent.getMeasuredWidth());

                                            return new AllFoodViewHolder(view);
                                        }
                                    };

                                    layoutManager = new LinearLayoutManager(getApplicationContext());
                                    //holder.secondRecyclerView.setHasFixedSize(true);
                                    holder.secondRecyclerView.setLayoutManager(layoutManager);
                                    holder.secondRecyclerView.setAdapter(adapter);
                                    adapter.startListening();
                                }
                                else {

                                    holder.restaurantName.setVisibility(View.GONE);

                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                throw databaseError.toException();
                            }
                        });

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                final String local = entry;

                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                    }
                });
            }

            @NonNull
            @Override
            public AllRestaurantNameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.all_food_restaurant_name, parent, false);
                view.setMinimumWidth(parent.getMeasuredWidth());

                return new AllRestaurantNameViewHolder(view);
            }
        };

        stringAdapter.startListening();
        recyclerViewString.setAdapter(stringAdapter);

    }

}
