package eu.ase.ro.grupa1086.licentamanolachemariacatalina.food;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import eu.ase.ro.grupa1086.licentamanolachemariacatalina.cart.ItemClickListener;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.R;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.Food;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.viewHolder.FoodViewHolder;

public class FoodList extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference foodList;

    String restaurantId = "";

    FirebaseRecyclerAdapter<Food, FoodViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);

        //Firebase
        database = FirebaseDatabase.getInstance();
        foodList = database.getReference("food");

        recyclerView = (RecyclerView)findViewById(R.id.recyclerFood);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //Intent
        if(getIntent() != null) {
            restaurantId = getIntent().getStringExtra("restaurantId");
        }
        if(!restaurantId.isEmpty() && restaurantId != null) {
            loadFoodList(restaurantId);
        }

    }

    private void loadFoodList(String restaurantId) {

        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("food")
                .child(restaurantId)
                .orderByChild("restaurantId").equalTo(restaurantId)
                .limitToLast(50);

        FirebaseRecyclerOptions<Food> options =
                new FirebaseRecyclerOptions.Builder<Food>()
                        .setQuery(query, Food.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FoodViewHolder holder, int position, @NonNull Food model) {
                holder.foodName.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage())
                        .into(holder.imageView);

                final Food local = model;
                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                       //newActivity
                        Intent foodInfo= new Intent(FoodList.this, FoodInfo.class);
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
            public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.food_item, parent, false);
                view.setMinimumWidth(parent.getMeasuredWidth());

                return new FoodViewHolder(view);
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
        String restaurantIdName = restaurantId + s;
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("food")
                .child(restaurantId)
                .orderByChild("restaurantIdName").startAt(restaurantIdName).endAt(restaurantIdName+"\uf8ff")
                .limitToLast(50);

        FirebaseRecyclerOptions<Food> options =
                new FirebaseRecyclerOptions.Builder<Food>()
                        .setQuery(query, Food.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FoodViewHolder holder, int position, @NonNull Food model) {
                holder.foodName.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage())
                        .into(holder.imageView);

                final Food local = model;
                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                        //newActivity
                        Intent foodInfo= new Intent(FoodList.this, FoodInfo.class);
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
            public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.food_item, parent, false);
                view.setMinimumWidth(parent.getMeasuredWidth());

                return new FoodViewHolder(view);
            }
        };

        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }

}