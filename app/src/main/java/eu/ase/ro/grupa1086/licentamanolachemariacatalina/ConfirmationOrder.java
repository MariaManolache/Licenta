package eu.ase.ro.grupa1086.licentamanolachemariacatalina;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.loader.content.AsyncTaskLoader;

import android.graphics.Color;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
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
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.DirectionsStep;
import com.google.maps.model.EncodedPolyline;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.Address;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.Food;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.Order;
//import eu.ase.ro.grupa1086.licentamanolachemariacatalina.databinding.ActivityConfirmationOrderBinding;

public class ConfirmationOrder extends FragmentActivity implements OnMapReadyCallback, RoutingListener {

    private String TAG = "so47492459";
    private GoogleMap mMap;
//    private ActivityConfirmationOrderBinding binding;

    FirebaseDatabase database;
    DatabaseReference orders;

    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    String userId;
    String orderId;
    Address userAddress;
    String mapsAddress;
    //LatLng latLngUserAddress;

    List<String> restaurantAddresses = new ArrayList<String>();

    List<LatLng> latLngRestaurantAddresses = new ArrayList<LatLng>();

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

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        userId = user.getUid();
        orders = database.getInstance().getReference("orders").child(userId);

        if (getIntent() != null && getIntent().getExtras() != null) {
            String origin = getIntent().getExtras().getString("origin");
            if (origin != null && (origin.equals("currentAddress") || origin.equals("mapsLocation"))) {
                orderId = getIntent().getStringExtra("orderId");

                orders.child(orderId).child("address").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Address address = snapshot.getValue(Address.class);
                        mapsAddress = address.getMapsAddress();
                        Log.i("mapsAddress", mapsAddress);

                        orders.child(orderId).child("restaurantAddress").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot2 : dataSnapshot.getChildren()) {
                                    String strAddress= snapshot2.getValue(String.class);
                                    restaurantAddresses.add(strAddress);
                                    Log.i("mapsAddress", strAddress);

                                    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                                            .findFragmentById(R.id.map);
                                    assert mapFragment != null;
                                    mapFragment.getMapAsync(ConfirmationOrder.this);
                                }

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
            if (origin != null && origin.equals("addAnotherAddress")) {
                orderId = getIntent().getStringExtra("orderId");

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

                        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                                .findFragmentById(R.id.map);
                        assert mapFragment != null;
                        mapFragment.getMapAsync(ConfirmationOrder.this);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        }


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


        for (String address: restaurantAddresses) {
            LatLng restaurantAddress = new LatLng(getLocationFromAddress(address).latitude,getLocationFromAddress(address).longitude);
            latLngRestaurantAddresses.add(restaurantAddress);
            Log.i("mapsAddress", String.valueOf(restaurantAddress));
        }

        //mMap.addMarker(new MarkerOptions().position(latLngRestaurantAddresses.get(0)).title("Marker 2"));
        for(LatLng latLngAddress: latLngRestaurantAddresses) {
            mMap.addMarker(new MarkerOptions().position(latLngAddress).title("Marker restaurant"));
            Log.i("mapsAddress", String.valueOf(latLngAddress));
        }

        if(latLngRestaurantAddresses.size() > 1) {
            for(int i = 0; i < latLngRestaurantAddresses.size() - 1; i++) {
//                String url = getRequestedUrl(latLngRestaurantAddresses.get(i), latLngRestaurantAddresses.get(i+1));
                //getRoute(latLngRestaurantAddresses.get(i), latLngRestaurantAddresses.get(i+1));
            }

        }

        mMap.addMarker(new MarkerOptions().position(latLngUserAddress).title("Marker adresa utilizator"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngUserAddress, 15));



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

        if(e != null) {
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

        if(polylines.size() > 0) {
            for(Polyline polyline : polylines) {
                polyline.remove();
            }
        }

        polylines = new ArrayList<>();

        for(int i = 0; i < route.size(); i++) {

            int colorIndex = i % COLORS.length;

            PolylineOptions polylineOptions = new PolylineOptions();
            polylineOptions.color(getResources().getColor(COLORS[colorIndex]));
            polylineOptions.width(10 + i * 3);
            polylineOptions.addAll(route.get(i).getPoints());
            Polyline polyline = mMap.addPolyline(polylineOptions);
            polylines.add(polyline);

            Toast.makeText(getApplicationContext(), "Route " + (i+1) + ": distance - " +
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
}