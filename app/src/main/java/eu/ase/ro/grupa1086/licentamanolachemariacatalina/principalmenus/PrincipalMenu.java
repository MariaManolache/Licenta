package eu.ase.ro.grupa1086.licentamanolachemariacatalina.principalmenus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.andremion.counterfab.CounterFab;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
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

import java.util.HashMap;

import eu.ase.ro.grupa1086.licentamanolachemariacatalina.R;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.RestaurantsList;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.order.ShoppingCart;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.account.Account;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.cart.ItemClickListener;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.Category;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.Food;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.Restaurant;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.food.CompleteFoodList;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.food.FoodInfo;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.food.FoodList;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.order.OrdersList;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.viewHolder.MenuViewHolder;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.viewHolder.RestaurantViewHolder;

public class PrincipalMenu extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference category;
    DatabaseReference restaurants;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    DatabaseReference cart;

    TextView name;
    RecyclerView recyclerMenu;
    RecyclerView.LayoutManager layoutManager;
    FirebaseRecyclerAdapter adapter;

    RecyclerView recyclerRestaurants;
    RecyclerView.LayoutManager layoutManagerRestaurants;
    FirebaseRecyclerAdapter adapterRestaurants;

    CounterFab shoppingCart;
    int count = 0;
    BottomNavigationView bottomNavigationView;

    FrameLayout foodFrameLayout;
    HashMap<String, String> imageList;
    SliderLayout sliderLayout;

//    Button btnMaps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal_menu);

        database = FirebaseDatabase.getInstance();
        category = database.getReference("categories");
        restaurants = database.getReference("restaurants");
        firebaseAuth = FirebaseAuth.getInstance();
        shoppingCart = findViewById(R.id.fab);

        foodFrameLayout = findViewById(R.id.foodFrameLayout);

        firebaseUser = firebaseAuth.getCurrentUser();
        cart = FirebaseDatabase.getInstance().getReference("carts").child(firebaseUser.getUid()).child("foodList");
        count = 0;
        cart.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Food food = dataSnapshot.getValue(Food.class);
                    count += food.getQuantity();
                }
                shoppingCart.setCount(count);
                count = 0;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());
        shoppingCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shoppingCart = new Intent(PrincipalMenu.this, ShoppingCart.class);
                startActivity(shoppingCart);
            }
        });

        recyclerMenu = (RecyclerView) findViewById(R.id.recyclerMenu);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerMenu.setLayoutManager(layoutManager);
        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(recyclerMenu.getContext(),
                R.anim.layout_fall_down);
        recyclerMenu.setLayoutAnimation(controller);

        recyclerRestaurants = (RecyclerView) findViewById(R.id.recyclerRestaurants);
        recyclerRestaurants.setHasFixedSize(true);
        layoutManagerRestaurants = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerRestaurants.setLayoutManager(layoutManagerRestaurants);

        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("categories")
                .limitToLast(50);

        FirebaseRecyclerOptions<Category> options =
                new FirebaseRecyclerOptions.Builder<Category>()
                        .setQuery(query, Category.class)
                        .build();


        adapter = new FirebaseRecyclerAdapter<Category, MenuViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MenuViewHolder holder, int position, @NonNull Category model) {
                holder.menuName.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage())
                        .into(holder.imageView);
                Category clickItem = model;
                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //categoryId

                        Intent restaurantsList = new Intent(PrincipalMenu.this, RestaurantsList.class);
                        restaurantsList.putExtra("categoryId", adapter.getRef(position).getKey());
                        startActivity(restaurantsList);
                    }
                });
            }

            @NonNull
            @Override
            public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.menu_item, parent, false);
                view.setMinimumWidth(parent.getMeasuredWidth());

                return new MenuViewHolder(view);
            }
        };

        loadMenu();
        loadRestaurants();

//        btnMaps = findViewById(R.id.btnMaps);
//
//    btnMaps.setOnClickListener(new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            Intent maps = new Intent(PrincipalMenu.this, MapsActivity.class);
//            startActivity(maps);
//        }
//    });

        foodFrameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent completeFoodList = new Intent(PrincipalMenu.this, CompleteFoodList.class);
                startActivity(completeFoodList);
            }
        });



        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.restaurantsMenu);

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
                        return true;
                    case R.id.orders:
                        startActivity(new Intent(getApplicationContext(), OrdersList.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                }
                return false;
            }
        });

        setupSlider();

    }

    private void setupSlider() {
        sliderLayout = (SliderLayout) findViewById(R.id.sliderBanner);
        imageList = new HashMap<>();

        DatabaseReference banner = database.getReference("banner");
        banner.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snapshot1 : snapshot.getChildren()) {
                    Food food = snapshot1.getValue(Food.class);
                    imageList.put(food.getName() + "_" + food.getId(), food.getImage());

                }
                for(String key : imageList.keySet()) {
                    String[] keySplit = key.split("_");
                    String nameOfFood = keySplit[0];
                    String idOfFood = keySplit[1];

                    TextSliderView textSliderView = new TextSliderView(getBaseContext());
                    textSliderView.description(nameOfFood)
                            .image(imageList.get(key))
                            .setScaleType(BaseSliderView.ScaleType.FitCenterCrop)
                            .setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                                @Override
                                public void onSliderClick(BaseSliderView slider) {
                                    Intent intent = new Intent(PrincipalMenu.this, FoodInfo.class);
                                    intent.putExtra("origin", "banner");
                                    intent.putExtra("foodId", idOfFood);
                                    startActivity(intent);
                                }
                            });
                    textSliderView.bundle(new Bundle());
//                    textSliderView.getBundle().putString("foodId", idOfFood);
                    sliderLayout.addSlider(textSliderView);

                    banner.removeEventListener(this);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        sliderLayout.setPresetTransformer(SliderLayout.Transformer.Background2Foreground);
        sliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        sliderLayout.setCustomAnimation(new DescriptionAnimation());
        sliderLayout.setDuration(4000);
    }

    @Override
    protected void onStop() {
        super.onStop();
//        adapter.stopListening();
//        adapterRestaurants.stopListening();
        sliderLayout.stopAutoCycle();
    }

    private void loadRestaurants() {

        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("restaurants")
                .limitToLast(50);

        FirebaseRecyclerOptions<Restaurant> optionsForRestaurants =
                new FirebaseRecyclerOptions.Builder<Restaurant>()
                        .setQuery(query, Restaurant.class)
                        .build();

        adapterRestaurants = new FirebaseRecyclerAdapter<Restaurant, RestaurantViewHolder>(optionsForRestaurants) {
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
                        Intent foodList = new Intent(PrincipalMenu.this, FoodList.class);
                        foodList.putExtra("restaurantId", model.getId());
                        startActivity(foodList);

                    }
                });
            }

            @NonNull
            @Override
            public RestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.restaurant_item_principal_menu, parent, false);
                view.setMinimumWidth(parent.getMeasuredWidth());

                return new RestaurantViewHolder(view);
            }
        };

        recyclerRestaurants.setAdapter(adapterRestaurants);
    }

    private void loadMenu() {

        adapter.startListening();
        recyclerMenu.setAdapter(adapter);
        recyclerMenu.getAdapter().notifyDataSetChanged();
        recyclerMenu.scheduleLayoutAnimation();

    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
        adapterRestaurants.startListening();
    }

//    @Override
//    public boolean onCreateOptionsMenu(android.view.Menu menu) {
//        getMenuInflater().inflate(R.menu.main_menu, menu);
//        return true;
//    }
}