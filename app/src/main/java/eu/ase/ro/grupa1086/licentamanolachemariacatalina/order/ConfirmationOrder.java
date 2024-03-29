package eu.ase.ro.grupa1086.licentamanolachemariacatalina.order;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.RequestResult;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Info;
import com.akexorcist.googledirection.model.Leg;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.RoutingListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.SphericalUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.Order;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.principalmenus.PrincipalMenu;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.R;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.Address;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.Restaurant;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.principalmenus.UserComparator;
//import eu.ase.ro.grupa1086.licentamanolachemariacatalina.databinding.ActivityConfirmationOrderBinding;

public class ConfirmationOrder extends FragmentActivity implements OnMapReadyCallback, RoutingListener {

    private String TAG = "so47492459";
    private GoogleMap mMap;
//    private ActivityConfirmationOrderBinding binding;

    FirebaseDatabase database;
    DatabaseReference orders;
    DatabaseReference cart;
    DatabaseReference users;
    DatabaseReference ratings;
    DatabaseReference driverOrders;
    String name;

    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    String userId;
    String orderId;
    Address userAddress;
    String mapsAddress;
    //LatLng latLngUserAddress;
    TextView tvDistance;
    TextView tvTime;

    LatLng clientAddress;
    List<LatLng> restaurantCoordinates = new ArrayList<LatLng>();

    List<Restaurant> restaurantAddresses = new ArrayList<Restaurant>();

    List<LatLng> latLngRestaurantAddresses = new ArrayList<LatLng>();

    Button btnReturnToMainMenu;
    ProgressBar pbDistance;
    ProgressBar pbPreparationTime;
    View map;

    LinearLayout llProgressBar;

    private List<Polyline> polylines;
    private static final int[] COLORS = new int[]{androidx.navigation.ui.R.color.design_default_color_primary_dark, com.google.android.material.R.color.design_default_color_primary_variant, androidx.navigation.ui.R.color.design_default_color_primary};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation_order);

//        binding = ActivityConfirmationOrderBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());

        polylines = new ArrayList<>();

        database = FirebaseDatabase.getInstance();

        tvDistance = findViewById(R.id.tvDistance);
        tvTime = findViewById(R.id.tvTime);

        btnReturnToMainMenu = findViewById(R.id.btnReturnToMainMenu);
        llProgressBar = findViewById(R.id.llProgressBar);
        llProgressBar.setVisibility(View.VISIBLE);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        if (user != null) {
            userId = user.getUid();
        }
        orders = database.getReference("orders").child(userId);
        cart = database.getReference("carts").child(userId).child("foodList");
        ratings = database.getReference("ratings");
        driverOrders = database.getReference("driverOrders");

        pbDistance = findViewById(R.id.pbDistance);
        pbPreparationTime = findViewById(R.id.pbPreparationTime);
        map = findViewById(R.id.map);

        cart.removeValue();

        users = database.getReference("users").child(user.getUid()).child("name");

        users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                name = snapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


//        cart.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                    Food food = dataSnapshot.getValue(Food.class);
//                    cart.child(food.getId()).removeValue();
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });


        if (getIntent() != null && getIntent().getExtras() != null) {
            pbPreparationTime.setVisibility(View.VISIBLE);
            pbDistance.setVisibility(View.VISIBLE);
            String origin = getIntent().getExtras().getString("origin");
            if (origin != null && (origin.equals("currentAddress") || origin.equals("mapsLocation") || origin.equals("savedAddress") || origin.equals("cardPayment"))) {
                orderId = getIntent().getStringExtra("orderId");

                if (ActivityCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(this,
                                android.Manifest.permission.ACCESS_COARSE_LOCATION) !=
                                PackageManager.PERMISSION_GRANTED) {
                    return;
                }

                orders.child(orderId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Order order = snapshot.getValue(Order.class);
                        pbPreparationTime.setVisibility(View.GONE);
                        if (order != null) {
                            tvTime.setText(order.getPreparationTime() + " de minute");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                orders.child(orderId).child("address").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Address address = snapshot.getValue(Address.class);
                        if (address != null) {
                            mapsAddress = address.getMapsAddress();
                        }
                        Log.i("mapsAddress", mapsAddress);

                        clientAddress = getLocationFromAddress(mapsAddress);

                        orders.child(orderId).child("restaurantAddress").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot2 : dataSnapshot.getChildren()) {
                                    Restaurant restaurantAddress = snapshot2.getValue(Restaurant.class);
                                    restaurantAddresses.add(restaurantAddress);
                                    Log.i("mapsAddress", restaurantAddress.toString());
                                    restaurantCoordinates.add(getLocationFromAddress(restaurantAddress.getAddress()));

//                                    float results[] = new float[10];
//                                    Double distance = SphericalUtil.computeDistanceBetween(clientAddress, restaurantCoordinates.get(0));
//                                    Location.distanceBetween(restaurantCoordinates.get(0).latitude, restaurantCoordinates.get(0).longitude, clientAddress.latitude, clientAddress.longitude, results);
////                                    tvDistance.setText("Distanta " + String.valueOf(results[0]));
//                                    tvDistance.setText("Distanta " + String.format("%.2f", distance / 1000) + " km");

                                }


                                float results[] = new float[10];
                                Map<LatLng, Double> distances = new LinkedHashMap<>();
                                for (int i = 0; i < restaurantCoordinates.size(); i++) {
                                    LatLng latLngClient = getLocationFromAddress(mapsAddress);
                                    Double distance = SphericalUtil.computeDistanceBetween(latLngClient, restaurantCoordinates.get(i));
                                    Log.i("distancesCheck", distance.toString());
                                    distances.put(restaurantCoordinates.get(i), distance);
                                    Location.distanceBetween(latLngClient.latitude, latLngClient.longitude, restaurantCoordinates.get(i).latitude, restaurantCoordinates.get(i).longitude, results);
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

                                if (sortedLocations.size() > 1) {
                                    for (int i = 0; i < sortedLocations.size() - 1; i++) {
                                        totalDistance += SphericalUtil.computeDistanceBetween(sortedLocations.get(i), sortedLocations.get(i + 1));
                                        Log.i("distancesCheck", totalDistance.toString() + "...");
                                    }
                                }

                                LatLng latLngClient = getLocationFromAddress(mapsAddress);
                                Double deliveryDistanceKm = SphericalUtil.computeDistanceBetween(sortedLocations.get(sortedLocations.size() - 1), clientAddress);


                                pbDistance.setVisibility(View.GONE);
                                tvDistance.setText(String.format("%.2f", (totalDistance + deliveryDistanceKm) / 1000) + " km");
//                                    holder.pickUpDistance.setText("Distanta de preluare: " + String.format("%.2f", totalDistance / 1000) + " km");
//                                    holder.deliveryDistance.setText("Distanta de livrare: " + String.format("%.2f", deliveryDistanceKm / 1000) + " km");
//                                    holder.totalDistance.setText("Distanta totala: " + String.format("%.2f", (totalDistance + deliveryDistanceKm) / 1000) + " km");

                                getDestinationInfo(restaurantCoordinates.get(0));

                                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                                        .findFragmentById(R.id.map);
                                assert mapFragment != null;
                                mapFragment.getMapAsync(ConfirmationOrder.this);

//                                map.setVisibility(View.VISIBLE);
//                                btnReturnToMainMenu.setVisibility(View.VISIBLE);

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }
            if (origin != null && (origin.equals("addAnotherAddress") || origin.equals("cardPayment-anotherAddress"))) {
                orderId = getIntent().getStringExtra("orderId");

                orders.child(orderId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Order order = snapshot.getValue(Order.class);
                        pbPreparationTime.setVisibility(View.GONE);
                        if (order != null) {
                            tvTime.setText(order.getPreparationTime() + " de minute");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                orders.child(orderId).child("address").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Address address = snapshot.getValue(Address.class);
//                        mapsAddress = address.getMapsAddress();

                        StringBuilder coordinates = new StringBuilder();
                        coordinates.append(address.getStreet() + " ");
                        coordinates.append(address.getNumber() + " ");
                        coordinates.append(address.getCity() + " ");
                        coordinates.append(address.getRegion() + " ");

                        String coordinatesString = coordinates.toString();
                        mapsAddress = coordinatesString;
                        Log.i("mapsAddress", mapsAddress);

                        clientAddress = getLocationFromAddress(mapsAddress);
                        orders.child(orderId).child("restaurantAddress").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot2 : dataSnapshot.getChildren()) {
                                    Restaurant restaurantAddress = snapshot2.getValue(Restaurant.class);
                                    restaurantAddresses.add(restaurantAddress);
                                    Log.i("mapsAddress", restaurantAddress.toString());
                                    restaurantCoordinates.add(getLocationFromAddress(restaurantAddress.getAddress()));

//                                    float results[] = new float[10];
//                                    Double distance = SphericalUtil.computeDistanceBetween(clientAddress, restaurantCoordinates.get(0));
//                                    Location.distanceBetween(restaurantCoordinates.get(0).latitude, restaurantCoordinates.get(0).longitude, clientAddress.latitude, clientAddress.longitude, results);
////                                    tvDistance.setText("Distanta " + String.valueOf(results[0]));
//                                    tvDistance.setText("Distanta " + String.format("%.2f", distance / 1000) + " km");

                                }


                                float results[] = new float[10];
                                Map<LatLng, Double> distances = new LinkedHashMap<>();
                                for (int i = 0; i < restaurantCoordinates.size(); i++) {
                                    LatLng latLngClient = getLocationFromAddress(mapsAddress);
                                    Double distance = SphericalUtil.computeDistanceBetween(latLngClient, restaurantCoordinates.get(i));
                                    Log.i("distancesCheck", distance.toString());
                                    distances.put(restaurantCoordinates.get(i), distance);
                                    Location.distanceBetween(latLngClient.latitude, latLngClient.longitude, restaurantCoordinates.get(i).latitude, restaurantCoordinates.get(i).longitude, results);
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

                                if (sortedLocations.size() > 1) {
                                    for (int i = 0; i < sortedLocations.size() - 1; i++) {
                                        totalDistance += SphericalUtil.computeDistanceBetween(sortedLocations.get(i), sortedLocations.get(i + 1));
                                        Log.i("distancesCheck", totalDistance.toString() + "...");
                                    }
                                }

                                LatLng latLngClient = getLocationFromAddress(mapsAddress);
                                Double deliveryDistanceKm = SphericalUtil.computeDistanceBetween(sortedLocations.get(sortedLocations.size() - 1), clientAddress);


                                pbDistance.setVisibility(View.GONE);
                                tvDistance.setText(String.format("%.2f", (totalDistance + deliveryDistanceKm) / 1000) + " km");

                                getDestinationInfo(restaurantCoordinates.get(0));

                                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                                        .findFragmentById(R.id.map);
                                assert mapFragment != null;
                                mapFragment.getMapAsync(ConfirmationOrder.this);

//                                map.setVisibility(View.VISIBLE);
//                                btnReturnToMainMenu.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

        }

        btnReturnToMainMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainMenu = new Intent(ConfirmationOrder.this, PrincipalMenu.class);
                startActivity(mainMenu);
                overridePendingTransition(R.anim.slide_nothing, R.anim.slide_down);
                finish();
            }
        });


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
        LatLng latLngUserAddress = new LatLng(getLocationFromAddress(mapsAddress).latitude, getLocationFromAddress(mapsAddress).longitude);


        for (Restaurant address : restaurantAddresses) {
            LatLng restaurantAddress = new LatLng(getLocationFromAddress(address.getAddress()).latitude, getLocationFromAddress(address.getAddress()).longitude);
            latLngRestaurantAddresses.add(restaurantAddress);
            Log.i("mapsAddress", String.valueOf(restaurantAddress));
        }

        //mMap.addMarker(new MarkerOptions().position(latLngRestaurantAddresses.get(0)).title("Marker 2"));
        for (LatLng latLngAddress : latLngRestaurantAddresses) {
            mMap.addMarker(new MarkerOptions().position(latLngAddress).title("Marker restaurant"));
            Log.i("mapsAddress", String.valueOf(latLngAddress));
        }

        if (latLngRestaurantAddresses.size() > 1) {
            for (int i = 0; i < latLngRestaurantAddresses.size() - 1; i++) {
//                String url = getRequestedUrl(latLngRestaurantAddresses.get(i), latLngRestaurantAddresses.get(i+1));
                //getRoute(latLngRestaurantAddresses.get(i), latLngRestaurantAddresses.get(i+1));
            }

        }


        mMap.addMarker(new MarkerOptions().position(latLngUserAddress).title("Marker adresa utilizator"));

//        for(int i = 0; i < latLngRestaurantAddresses.size(); i++) {
//            mMap.addMarker(new MarkerOptions().position(latLngRestaurantAddresses.get(i)).title("Marker " + i));
//        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngUserAddress, 15));

        map.setVisibility(View.VISIBLE);
        btnReturnToMainMenu.setVisibility(View.VISIBLE);

//        LatLng barcelona = new LatLng(41.385064,2.173403);
//        mMap.addMarker(new MarkerOptions().position(barcelona).title("Marker in Barcelona"));
//
//        LatLng madrid = new LatLng(40.416775,-3.70379);
//        mMap.addMarker(new MarkerOptions().position(madrid).title("Marker in Madrid"));
//
//        List<LatLng> path = new ArrayList();
//
//
//        LatLng zaragoza = new LatLng(41.648823,-0.889085);
//        //Execute Directions API request
//        GeoApiContext context = new GeoApiContext.Builder()
//                .apiKey("YOUR_API_KEY")
//                .build();
//        DirectionsApiRequest req = DirectionsApi.getDirections(context, "41.385064,2.173403", "40.416775,-3.70379");
//        try {
//            DirectionsResult res = req.await();
//
//            //Loop through legs and steps to get encoded polylines of each step
//            if (res.routes != null && res.routes.length > 0) {
//                DirectionsRoute route = res.routes[0];
//
//                if (route.legs !=null) {
//                    for(int i=0; i<route.legs.length; i++) {
//                        DirectionsLeg leg = route.legs[i];
//                        if (leg.steps != null) {
//                            for (int j=0; j<leg.steps.length;j++){
//                                DirectionsStep step = leg.steps[j];
//                                if (step.steps != null && step.steps.length >0) {
//                                    for (int k=0; k<step.steps.length;k++){
//                                        DirectionsStep step1 = step.steps[k];
//                                        EncodedPolyline points1 = step1.polyline;
//                                        if (points1 != null) {
//                                            //Decode polyline and add points to list of route coordinates
//                                            List<com.google.maps.model.LatLng> coords1 = points1.decodePath();
//                                            for (com.google.maps.model.LatLng coord1 : coords1) {
//                                                path.add(new LatLng(coord1.lat, coord1.lng));
//                                            }
//                                        }
//                                    }
//                                } else {
//                                    EncodedPolyline points = step.polyline;
//                                    if (points != null) {
//                                        //Decode polyline and add points to list of route coordinates
//                                        List<com.google.maps.model.LatLng> coords = points.decodePath();
//                                        for (com.google.maps.model.LatLng coord : coords) {
//                                            path.add(new LatLng(coord.lat, coord.lng));
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        } catch(Exception ex) {
//            Log.e("eroare", "eroare");
//        }
//
//        //Draw the polyline
//        if (path.size() > 0) {
//            PolylineOptions opts = new PolylineOptions().addAll(path).color(Color.BLUE).width(5);
//            mMap.addPolyline(opts);
//        }
//
//        mMap.getUiSettings().setZoomControlsEnabled(true);
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(zaragoza, 6));


        //getRoute(latLngRestaurantAddresses.get(0), latLngUserAddress);

//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngRestaurantAddresses.get(0), 15));
    }

//    private void getRoute(LatLng start, LatLng stop) {
//        Routing routing = new Routing.Builder()
//                .key("AIzaSyAbg-2wF83FMLewDZHmYeHGba7Feq0ODlc")
//                .travelMode(Routing.TravelMode.DRIVING)
//                .withListener(this)
//                .alternativeRoutes(false)
//                .waypoints(start, stop)
//                .build();
//        routing.execute();
//    }

//    private String getRequestedUrl(LatLng origin, LatLng destination) {
//        String originString = "origin=" + origin.latitude+ "," + origin.longitude;
//
//        String destinationString = "destination=" + destination.latitude + "," + destination.longitude;
//
//        String sensor = "sensor=false";
//
//        String mode = "mode=driving";
//
//        String param = originString + "&" + destinationString + "&" + sensor + "&" + mode;
//
//        String output = "json";
//
//        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + param;
//        return url;
//
//    }
//
//    private String requestDirection(String reqUrl) throws IOException {
//        String responseString = "";
//        InputStream inputStream = null;
//        HttpURLConnection httpURLConnection = null;
//        try {
//            URL url = new URL(reqUrl);
//            httpURLConnection = (HttpURLConnection) url.openConnection();
//            httpURLConnection.connect();
//
//            inputStream = httpURLConnection.getInputStream();
//            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
//            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
//
//            StringBuffer stringBuffer = new StringBuffer();
//            String line = "";
//
//            while ((line = bufferedReader.readLine()) != null) {
//                stringBuffer.append(line);
//            }
//
//            responseString = stringBuffer.toString();
//            bufferedReader.close();
//            inputStreamReader.close();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if(inputStream != null) {
//                inputStream.close();
//            }
//            httpURLConnection.disconnect();
//        }
//
//        return responseString;
//    }

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
    public void onRoutingFailure(RouteException e) {

        if (e != null) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Something went wrong. Try again", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRoutingStart() {

    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {

        if (polylines.size() > 0) {
            for (Polyline polyline : polylines) {
                polyline.remove();
            }
        }

        polylines = new ArrayList<>();

        for (int i = 0; i < route.size(); i++) {

            int colorIndex = i % COLORS.length;

            PolylineOptions polylineOptions = new PolylineOptions();
            polylineOptions.color(getResources().getColor(COLORS[colorIndex]));
            polylineOptions.width(10 + i * 3);
            polylineOptions.addAll(route.get(i).getPoints());
            Polyline polyline = mMap.addPolyline(polylineOptions);
            polylines.add(polyline);

            Toast.makeText(getApplicationContext(), "Route " + (i + 1) + ": distance - " +
                    route.get(i).getDistanceValue() + ": duration - " + route.get(i).getDurationValue(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRoutingCancelled() {

    }

    private void erasePolylines() {
        for (Polyline polyline : polylines) {
            polyline.remove();
        }
        polylines.clear();
    }

//    public class TaskRequestDirections extends AsyncTask<String, Void, String> {
//
//        @Override
//        protected String doInBackground(String... strings) {
//            String responseString = "";
//            try {
//                responseString = requestDirection(strings[0]);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            return responseString;
//        }
//
//        @Override
//        protected void onPostExecute(String s) {
//            super.onPostExecute(s);
//        }
//    }


    private void getDestinationInfo(LatLng latLngDestination) {
        //progressDialog();
        String serverKey = "AIzaSyCysjrCb9e2N5sNip39CegvBxQTgUCmphU"; // Api Key For Google Direction API \\
        final LatLng origin = new LatLng(clientAddress.latitude, clientAddress.longitude);
        final LatLng destination = latLngDestination;
        //-------------Using AK Exorcist Google Direction Library---------------\\
        GoogleDirection.withServerKey(serverKey)
                .from(origin)
                .to(destination)
                .transportMode(TransportMode.DRIVING)
                .execute(new DirectionCallback() {

                    @Override
                    public void onDirectionSuccess(Direction direction, String rawBody) {
//                        dismissDialog(0);
                        String status = direction.getStatus();
                        if (status.equals(RequestResult.OK)) {
                            com.akexorcist.googledirection.model.Route route = direction.getRouteList().get(0);
                            Leg leg = route.getLegList().get(0);
                            Info distanceInfo = leg.getDistance();
                            Info durationInfo = leg.getDuration();
                            String distance = distanceInfo.getText();
                            String duration = durationInfo.getText();
                            tvTime.setText(duration);

                            //------------Displaying Distance and Time-----------------\\
                            //                           showingDistanceTime(distance, duration); // Showing distance and time to the user in the UI \\
//                            String message = "Total Distance is " + distance + " and Estimated Time is " + duration;
//                            StaticMethods.customSnackBar(consumerHomeActivity.parentLayout, message,
//                                    getResources().getColor(R.color.colorPrimary),
//                                    getResources().getColor(R.color.colorWhite), 3000);

                            //--------------Drawing Path-----------------\\
                            ArrayList<LatLng> directionPositionList = leg.getDirectionPoint();
                            PolylineOptions polylineOptions = DirectionConverter.createPolyline(getApplicationContext(),
                                    directionPositionList, 5, getResources().getColor(R.color.purple_200));
                            mMap.addPolyline(polylineOptions);
                            //--------------------------------------------\\

                            //-----------Zooming the map according to marker bounds-------------\\
                            LatLngBounds.Builder builder = new LatLngBounds.Builder();
                            builder.include(origin);
                            builder.include(destination);
                            LatLngBounds bounds = builder.build();

                            int width = getResources().getDisplayMetrics().widthPixels;
                            int height = getResources().getDisplayMetrics().heightPixels;
                            int padding = (int) (width * 0.20); // offset from edges of the map 10% of screen

                            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
                            mMap.animateCamera(cu);
                            //------------------------------------------------------------------\\

                        } else if (status.equals(RequestResult.NOT_FOUND)) {
                            Toast.makeText(getApplicationContext(), "No routes exist", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onDirectionFailure(Throwable t) {
                        // Do something here
                    }
                });
        //-------------------------------------------------------------------------------\\

    }
}
