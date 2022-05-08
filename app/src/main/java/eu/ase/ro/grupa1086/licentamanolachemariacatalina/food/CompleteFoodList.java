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

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.viewHolder.AllFoodViewHolder;

public class CompleteFoodList extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference foodList;
    DatabaseReference restaurantsList;

    String restaurantId = "";
    String restaurantName = "";

    FirebaseRecyclerAdapter<Food, AllFoodViewHolder> adapter;

    List<String> restaurantsName = new ArrayList<String>();
    int same = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_food_list);

        //Firebase
        database = FirebaseDatabase.getInstance();
        foodList = database.getReference("food");
        restaurantsList = database.getReference("restaurants");

        recyclerView = (RecyclerView) findViewById(R.id.recyclerAllFood);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        loadAllFoodList();

    }

    private void loadAllFoodList() {

        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("food")
                .orderByChild("restaurantId")
                .limitToLast(50);

        FirebaseRecyclerOptions<Food> options =
                new FirebaseRecyclerOptions.Builder<Food>()
                        .setQuery(query, Food.class)
                        .build();

        restaurantsName = new ArrayList<String>();
        restaurantsName = null;

        adapter = new FirebaseRecyclerAdapter<Food, AllFoodViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull AllFoodViewHolder holder, int position, @NonNull Food model) {
                holder.foodName.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage())
                        .into(holder.imageView);


                // same = 0;
                restaurantsList.child(model.getRestaurantId()).child("name").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        restaurantName = snapshot.getValue(String.class);
                        restaurantsName.add(restaurantName);

                        if (restaurantsName.size() == 1) {
                            holder.restaurantName.setVisibility(View.VISIBLE);
                            holder.restaurantName.setText(restaurantName);
                        } else {
                            if (restaurantsName.get(restaurantsName.size() - 1).equals(restaurantsName.get(restaurantsName.size() - 2))) {
                                holder.restaurantName.setVisibility(View.GONE);
//                                if(model.getName().equals("Chicken and corn")) {
//                                    holder.restaurantName.setVisibility(View.VISIBLE);
//                                    holder.restaurantName.setText(restaurantName);
//                                }
                            } else {
                                holder.restaurantName.setVisibility(View.VISIBLE);
                                holder.restaurantName.setText(restaurantName);
                            }
                        }

                        Log.i("restaurantsName", restaurantsName.toString());
                        if (restaurantsName.size() == 2) {
                            restaurantsName.remove(restaurantsName.get(0));
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

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
                        foodInfo.putExtra("id", adapter.getRef(position).getKey());
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

        recyclerView.setAdapter(adapter);
        restaurantsName = new ArrayList<String>();
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

        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("food")
                .orderByChild("name").startAt(s).endAt(s + "\uf8ff")
                .limitToLast(50);

        FirebaseRecyclerOptions<Food> options =
                new FirebaseRecyclerOptions.Builder<Food>()
                        .setQuery(query, Food.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<Food, AllFoodViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull AllFoodViewHolder holder, int position, @NonNull Food model) {
                holder.foodName.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage())
                        .into(holder.imageView);

                restaurantsList.child(model.getRestaurantId()).child("name").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String restaurantName = snapshot.getValue(String.class);
                        holder.restaurantName.setText(restaurantName);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                final Food local = model;
                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                        //newActivity
                        Intent foodInfo = new Intent(CompleteFoodList.this, FoodInfo.class);
                        foodInfo.putExtra("id", adapter.getRef(position).getKey());
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

        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }

}