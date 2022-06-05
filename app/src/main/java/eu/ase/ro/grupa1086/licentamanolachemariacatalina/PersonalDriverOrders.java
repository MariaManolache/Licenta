package eu.ase.ro.grupa1086.licentamanolachemariacatalina;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
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
import com.google.maps.android.SphericalUtil;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import eu.ase.ro.grupa1086.licentamanolachemariacatalina.account.DriverAccount;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.cart.ItemClickListener;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.Food;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.Order;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.Restaurant;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.Status;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.principalmenus.DriverMenu;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.principalmenus.UserComparator;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.viewHolder.DriverOrderViewHolder;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.viewHolder.OrderDetailsViewHolder;

public class PersonalDriverOrders extends AppCompatActivity {
    public RecyclerView recyclerViewMyOrders;

    public RecyclerView.LayoutManager layoutManagerMyOrders;
    public RecyclerView.LayoutManager secondLayoutManagerMyOrders;


    FirebaseRecyclerAdapter<Order, DriverOrderViewHolder> adapterMyOrders;
    FirebaseRecyclerAdapter<Food, OrderDetailsViewHolder> secondAdapterMyOrders;


    FirebaseDatabase database;
    DatabaseReference orders;
    DatabaseReference driverOrders;
    DatabaseReference restaurants;
    DatabaseReference restaurantAddresses;
    DatabaseReference cart;
    DatabaseReference ratings;
    DatabaseReference food;
    DatabaseReference users;
    DatabaseReference restaurantOrders;
    FirebaseUser user;

    String restaurantName;
    String restaurantImage;
    String orderId;

    ImageView callButton;

    AlertDialog.Builder acceptOrder;

    BottomNavigationView bottomNavigationView;

    List<Order> orderList = new ArrayList<Order>();

    LocationRequest locationRequest;
    LocationCallback locationCallback;
    FusedLocationProviderClient fusedLocationProviderClient;
    Location currentLocation;

    TextView tvCurrentLocation;
    LayoutInflater inflater;
    LatLng latLngLocation;

    TextView myOrders;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_driver_orders);

        database = FirebaseDatabase.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        orders = database.getReference("orders");
        restaurants = database.getReference("restaurants");
        restaurantAddresses = database.getReference("orders");
        cart = database.getReference().child("orders");
        food = database.getReference().child("food");
        driverOrders = database.getReference().child("driverOrders");
        restaurantOrders = database.getReference().child("restaurantOrders");

        users = database.getReference().child("users");

        callButton = findViewById(R.id.callButton);

        recyclerViewMyOrders = (RecyclerView) findViewById(R.id.personalOrderList);
        recyclerViewMyOrders.setHasFixedSize(true);
        layoutManagerMyOrders = new LinearLayoutManager(this);
        recyclerViewMyOrders.setLayoutManager(layoutManagerMyOrders);

        tvCurrentLocation = findViewById(R.id.currentLocation);

        inflater = this.getLayoutInflater();
        acceptOrder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogStyle));

        myOrders = findViewById(R.id.myOrders);

        loadMyOrders();

        tvCurrentLocation.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                acceptOrder.setTitle("Doresti sa livrezi comanda selectata?")
                        .setPositiveButton("Confirmare", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                Toast.makeText(PersonalDriverOrders.this, "Mesaj", Toast.LENGTH_LONG).show();

                            }
                        }).setNegativeButton("Anuleaza", null)
                        .create().show();

                return true;
            }
        });

        //loadOrders();
        initializeLocation();

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.personalOrders);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.account:
                        startActivity(new Intent(getApplicationContext(), DriverAccount.class));
                        finish();
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.personalOrders:
                        return true;
                    case R.id.orders:
                        startActivity(new Intent(getApplicationContext(), DriverMenu.class));
                        finish();
                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }
        });
    }

    private void loadMyOrders() {
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("driverOrders")
                .child(user.getUid())
                .orderByChild("id")
                .limitToLast(50);

        FirebaseRecyclerOptions<Order> options =
                new FirebaseRecyclerOptions.Builder<Order>()
                        .setQuery(query, Order.class)
                        .build();

        driverOrders.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!(snapshot.exists())) {
                    myOrders.setVisibility(View.GONE);
                    recyclerViewMyOrders.setVisibility(View.GONE);
                } else {
                    myOrders.setVisibility(View.VISIBLE);
                    recyclerViewMyOrders.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        adapterMyOrders = new FirebaseRecyclerAdapter<Order, DriverOrderViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull DriverOrderViewHolder holder, int position, @NonNull Order model) {

                restaurantName = null;
                restaurantImage = null;
                orderList.add(model);
                orderId = model.getId();

                if (!model.getStatus().equals(Status.finalizata)) {
                    driverOrders.child(user.getUid()).child(model.getId()).child("restaurantAddress").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                Restaurant restaurant = dataSnapshot.getValue(Restaurant.class);
                                if (restaurantName == null) {
                                    restaurantName = restaurant.getName();
                                    restaurantImage = restaurant.getImage();
                                } else {
                                    restaurantName += ", " + restaurant.getName();
                                }
                            }

                            holder.restaurantsName.setText(restaurantName);
                            holder.orderStatus.setText("Status: " + String.valueOf(model.getStatus()).substring(0, 1).toUpperCase(Locale.ROOT) + String.valueOf(model.getStatus()).replace("_", " ").substring(1));
                            holder.orderAddress.setText("Adresa: " + String.valueOf(model.getAddress().getMapsAddress()));
                            holder.orderPriceTotal.setText("Total: " + model.getTotal() + " lei");
                            Picasso.with(getBaseContext()).load(restaurantImage)
                                    .into(holder.restaurantImage);

                            float results[] = new float[10];
                            if (latLngLocation != null) {
                                Map<LatLng, Double> distances = new LinkedHashMap<>();
                                for(int i = 0; i < model.getRestaurantAddress().size(); i++) {
                                    LatLng latLngRestaurant = getLocationFromAddress(model.getRestaurantAddress().get(i).getAddress());
                                    Double distance = SphericalUtil.computeDistanceBetween(latLngLocation, latLngRestaurant);
                                    Log.i("distancesCheck", distance.toString());
                                    distances.put(latLngRestaurant, distance);
                                    Location.distanceBetween(latLngLocation.latitude, latLngLocation.longitude, latLngRestaurant.latitude, latLngRestaurant.longitude, results);
                                }

                                UserComparator comparator = new UserComparator(distances);
                                Map<LatLng, Double> sortedDistances = new TreeMap<>(comparator);
                                sortedDistances.putAll(distances);


                                Map.Entry<LatLng, Double> entry = sortedDistances.entrySet().iterator().next();
                                LatLng key = entry.getKey();
                                Double minimumDistance = entry.getValue();
                                Double totalDistance = minimumDistance;
                                Log.i("distancesCheck", totalDistance.toString() + "...");

                                List<LatLng> sortedLocations = new ArrayList<>();
                                for (Map.Entry<LatLng, Double> entry2 : sortedDistances.entrySet()) {
                                    sortedLocations.add(entry2.getKey());
                                }

                                if(sortedLocations.size() > 1) {
                                    for(int i = 0; i < sortedLocations.size() - 1; i++) {
                                        totalDistance += SphericalUtil.computeDistanceBetween(sortedLocations.get(i), sortedLocations.get(i+1));
                                        Log.i("distancesCheck", totalDistance.toString() + "...");
                                    }
                                }

                                LatLng clientAddress = getLocationFromAddress(String.valueOf(model.getAddress().getMapsAddress()));
                                Double deliveryDistanceKm = SphericalUtil.computeDistanceBetween(sortedLocations.get(sortedLocations.size()-1), clientAddress);


                                holder.pickUpDistance.setText("Distanta de preluare: " + String.format("%.2f", totalDistance / 1000) + " km");
                                holder.deliveryDistance.setText("Distanta de livrare: " + String.format("%.2f", deliveryDistanceKm / 1000) + " km");
                                holder.totalDistance.setText("Distanta totala: " + String.format("%.2f", (totalDistance + deliveryDistanceKm) / 1000) + " km");

                                users.child(model.getUserId()).child("phoneNumber").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        String phoneNumber = snapshot.getValue(String.class);
                                        holder.callButton.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Intent dialIntent = new Intent(Intent.ACTION_DIAL);
                                                dialIntent.setData(Uri.parse("tel:" + phoneNumber));
                                                startActivity(dialIntent);
                                                finish();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                            String restaurantName2 = restaurantName;
                            String restaurantImage2 = restaurantImage;
                            restaurantName = null;
                            restaurantImage = null;

                            final Order local = model;
                            holder.setItemClickListener(new ItemClickListener() {
                                @Override
                                public void onClick(View view, int position, boolean isLongClick) {

                                    if (!isLongClick) {
                                        //Toast.makeText(OrdersList.this, model.getCart().toString(), Toast.LENGTH_LONG).show();
//                                    loadOrderDetails(model.getId());

                                        if (holder.downArrow.getVisibility() == View.VISIBLE) {
                                            holder.downArrow.setVisibility(View.GONE);
                                        } else {
                                            holder.downArrow.setVisibility(View.VISIBLE);
                                        }

                                        if (holder.upArrow.getVisibility() == View.VISIBLE) {
                                            holder.upArrow.setVisibility(View.GONE);
                                        } else {
                                            holder.upArrow.setVisibility(View.VISIBLE);
                                        }

                                        cart = database.getReference().child("driverOrders").child(user.getUid()).child(model.getId()).child("cart");
                                        restaurantAddresses = database.getReference("driverOrders").child(user.getUid()).child(model.getId()).child("restaurantAddress");

                                        Query query = cart
                                                .orderByChild("id")
                                                .limitToLast(50);

                                        FirebaseRecyclerOptions<Food> options =
                                                new FirebaseRecyclerOptions.Builder<Food>()
                                                        .setQuery(query, Food.class)
                                                        .build();

                                        secondAdapterMyOrders = new FirebaseRecyclerAdapter<Food, OrderDetailsViewHolder>(options) {
                                            @Override
                                            protected void onBindViewHolder(@NonNull OrderDetailsViewHolder holder2, int position, @NonNull Food model) {

                                                holder2.foodName.setText(model.getName());

                                                restaurantAddresses.addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                                            Restaurant restaurant = dataSnapshot.getValue(Restaurant.class);
                                                            if (model.getRestaurantId().equals(restaurant.getId())) {
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

                                                                Toast.makeText(PersonalDriverOrders.this, model.getName(), Toast.LENGTH_LONG).show();
//                                                            showRatingDialog(model.getId());

//                                                                Intent foodInfo = new Intent(DriverMenu.this, FoodInfo.class);
//                                                                foodInfo.putExtra("origin", "ordersList");
//                                                                foodInfo.putExtra("orderId", local.getId());
//                                                                foodInfo.putExtra("quantity", local2.getQuantity());
//                                                                foodInfo.putExtra("foodId", model.getId());
//                                                                startActivity(foodInfo);

                                                            }
                                                        });
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                    }
                                                });


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


                                        if (holder.secondRecyclerView.getVisibility() == View.GONE) {
                                            holder.secondRecyclerView.setVisibility(View.VISIBLE);
                                        } else {
                                            holder.secondRecyclerView.setVisibility(View.GONE);
                                        }
                                        secondLayoutManagerMyOrders = new LinearLayoutManager(getApplicationContext());
                                        //holder.secondRecyclerView.setHasFixedSize(true);
                                        holder.secondRecyclerView.setLayoutManager(secondLayoutManagerMyOrders);
                                        holder.secondRecyclerView.setAdapter(secondAdapterMyOrders);
                                        secondAdapterMyOrders.startListening();

                                    } else if (isLongClick) {
//                                        if (model.getStatus().equals(Status.confirmata)) {
////                                    View viewPopUp = inflater.inflate(R.layout.reset_name_pop_up, null);
//                                            acceptOrder.setTitle("Doresti sa livrezi comanda selectata?")
//                                                    .setPositiveButton("Confirmare", new DialogInterface.OnClickListener() {
//                                                        @Override
//                                                        public void onClick(DialogInterface dialog, int which) {
//
//                                                            model.setStatus(Status.in_curs_de_livrare);
//                                                            driverOrders.child(model.getId()).child("status").setValue(Status.in_curs_de_livrare);
//                                                            orders.child(model.getUserId()).child(model.getId()).child("status").setValue(Status.in_curs_de_livrare);
//
//                                                            for(int i = 0; i < model.getRestaurantAddress().size(); i++) {
//                                                                restaurantOrders.child(model.getRestaurantAddress().get(i).getId()).child("orders").child(model.getId()).child("status").setValue(Status.in_curs_de_livrare);
//                                                            }
//
////                                                            driverOrders.child(model.getId()).child("userId").addValueEventListener(new ValueEventListener() {
////                                                                @Override
////                                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
////                                                                    String userId = snapshot.getValue(String.class);
////                                                                    if (userId != null)
////                                                                        orders.child(userId).child(model.getId()).child("status").setValue(Status.in_curs_de_livrare);
////                                                                }
////
////                                                                @Override
////                                                                public void onCancelled(@NonNull DatabaseError error) {
////
////                                                                }
////                                                            });
//
//
////                                                            orders.child(id).child(model.getId()).child("status").setValue(Status.in_curs_de_livrare);
//
//
//                                                        }
//                                                    }).setNegativeButton("Anuleaza", null)
//                                                    .create().show();
//
//                                        } else
                                        if (model.getStatus().equals(Status.in_curs_de_livrare)) {
                                            acceptOrder.setTitle("Ai terminat de livrat aceasta comanda?")
                                                    .setPositiveButton("Confirmare", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {

                                                            model.setStatus(Status.finalizata);

                                                            driverOrders.child(user.getUid()).child(model.getId()).child("status").setValue(Status.finalizata);

                                                            orders.child(model.getUserId()).child(model.getId()).child("status").setValue(Status.finalizata);
                                                            driverOrders.child(user.getUid()).child(model.getId()).removeValue();

                                                            for(int i = 0; i < model.getRestaurantAddress().size(); i++) {
                                                                restaurantOrders.child(model.getRestaurantAddress().get(i).getId()).child("orders").child(model.getId()).child("status").setValue(Status.finalizata);
                                                                restaurantOrders.child(model.getRestaurantAddress().get(i).getId()).child("orders").child(model.getId()).removeValue();
                                                            }

//                                                            driverOrders.child(model.getId()).child("userId").addValueEventListener(new ValueEventListener() {
//                                                                @Override
//                                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                                                    String userId = snapshot.getValue(String.class);
//                                                                    if (userId != null)
//                                                                        orders.child(userId).child(model.getId()).child("status").setValue(Status.finalizata).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                                            @Override
//                                                                            public void onComplete(@NonNull Task<Void> task) {
//                                                                                driverOrders.child(model.getId()).removeValue();
//                                                                            }
//                                                                        });
//                                                                }
//
//                                                                @Override
//                                                                public void onCancelled(@NonNull DatabaseError error) {
//
//                                                                }
//                                                            });

                                                        }
                                                    }).setNegativeButton("Anuleaza", null)
                                                    .create().show();
                                        }
                                    }

                                }
                            });


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

            }

            @NonNull
            @Override
            public DriverOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.driver_order_layout, parent, false);
                view.setMinimumWidth(parent.getMeasuredWidth());

                return new DriverOrderViewHolder(view);
            }
        };


        recyclerViewMyOrders.setAdapter(adapterMyOrders);
        adapterMyOrders.startListening();
    }

    private void initializeLocation() {
        buildLocationRequest();
        buildLocationCallback();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    private void buildLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                currentLocation = locationResult.getLastLocation();
                String sCurrentLocation = getAddressFromLatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                tvCurrentLocation.setText(sCurrentLocation);
                latLngLocation = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            }
        };
    }

    private void buildLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setSmallestDisplacement(10f);
    }

    @Override
    public void onStop() {
        if (fusedLocationProviderClient != null) {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        }
        super.onStop();
        adapterMyOrders.stopListening();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (fusedLocationProviderClient != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        }

    }

    private String getAddressFromLatLng(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        String result = "";
        try {
            List<android.location.Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);
            if (addressList != null && addressList.size() > 0) {
                android.location.Address address = addressList.get(0);
                StringBuilder sb = new StringBuilder(address.getAddressLine(0));
                result = sb.toString();
            } else {
                result = "Address not found";
            }
        } catch (IOException e) {
            e.printStackTrace();
            result = e.getMessage();
        }
        Log.i("result", result);
        return result;
    }

    public LatLng getLocationFromAddress(String strAddress) {

        Geocoder coder = new Geocoder(getApplicationContext());
        List<android.location.Address> address;
        LatLng coordinates = null;

        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }

            android.location.Address location = address.get(0);
            coordinates = new LatLng(location.getLatitude(), location.getLongitude());

        } catch (IOException ex) {

            ex.printStackTrace();
        }

        return coordinates;
    }

    @Override
    protected void onStart() {
        super.onStart();
        //adapter.startListening();
    }

}

class ParentViewHolderDriver
        extends RecyclerView.ViewHolder {

    private TextView ParentItemTitle;
    private RecyclerView ChildRecyclerView;

    ParentViewHolderDriver(final View itemView) {
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