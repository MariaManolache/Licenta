package eu.ase.ro.grupa1086.licentamanolachemariacatalina;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import eu.ase.ro.grupa1086.licentamanolachemariacatalina.order.PlaceOrder;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnMarkerDragListener {

    private GoogleMap mMap;
    SearchView searchView;
    AlertDialog.Builder noLocationFoundAlert;
    FloatingActionButton fabPickedAddressed;
    LatLng latLngAddress;
    Button btnPickLocation;
    List<Address> addressList = new ArrayList<Address>();
    TextView pickedAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
//        binding = ActivityMapsBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());

        searchView = findViewById(R.id.searchViewLocation);
        noLocationFoundAlert = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogStyle));
        fabPickedAddressed = findViewById(R.id.fabPickedAddress);
        pickedAddress = findViewById(R.id.pickedAddress);

//        btnPickLocation = findViewById(R.id.btnPickLocation);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String location = searchView.getQuery().toString();

                if (location != null || !location.equals("")) {
                    Geocoder geocoder = new Geocoder(getApplicationContext());
                    try {
                        addressList = geocoder.getFromLocationName(location, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

                if (addressList.size() > 0) {
                    Address address = addressList.get(0);
                    latLngAddress = new LatLng(address.getLatitude(), address.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(latLngAddress).title(location).draggable(true));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngAddress, 18));

                    pickedAddress.setVisibility(View.VISIBLE);
                    pickedAddress.setText("Adresa aleasă este: " + address.getAddressLine(0));

                    fabPickedAddressed.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent placingOrder = new Intent(MapsActivity.this, PlaceOrder.class);
                            placingOrder.putExtra("origin", "mapsActivity");
                            placingOrder.putExtra("address", address.toString());
                            placingOrder.putExtra("latitude", address.getLatitude());
                            placingOrder.putExtra("longitude", address.getLongitude());
                            startActivity(placingOrder);
                            finish();
                            overridePendingTransition(R.anim.slide_up, R.anim.slide_nothing);
                        }
                    });
                } else {
                    noLocationFoundAlert.setTitle("Locatie indisponibila")
                            .setMessage("Adresa introdusa nu este disponibila. Introduceti alta adresa valida.")
                            .setNeutralButton("OK", null)
                            .create().show();

                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });


//        btnPickLocation.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mMap.clear();
//                MarkerOptions options = new MarkerOptions();
//                options.draggable(true);
//                options.position(new LatLng(latLngAddress.latitude, latLngAddress.longitude));
//                options.title("Destinatie");
//
//
//            }
//        });


        assert mapFragment != null;
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        LatLng newYork = new LatLng(40.7128, -74.0060);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.addMarker(new MarkerOptions().position(newYork).title("Marker in New York"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newYork, 15.0f));

//        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
//            @Override
//            public void onMapClick(@NonNull LatLng latLng) {
//                MarkerOptions markerOptions = new MarkerOptions();
//                markerOptions.position(latLng);
//                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
//                mMap.addMarker(markerOptions);
//            }
//        });

        mMap.setOnMarkerDragListener(this);
        mMap.setOnMarkerClickListener(this);

        LatLng romania = new LatLng(44.439663, 26.096306);
        mMap.addMarker(new MarkerOptions().position(romania).draggable(true));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(romania, 15));

    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        marker.setDraggable(true);
        return false;
    }

    @Override
    public void onMarkerDrag(@NonNull Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(@NonNull Marker marker) {
        mMap.clear();
        latLngAddress = marker.getPosition();

        String location = getAddressFromLatLng(latLngAddress.latitude, latLngAddress.longitude);

        mMap.addMarker(new MarkerOptions().position(latLngAddress).title(location).draggable(true));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngAddress, 18));

        pickedAddress.setVisibility(View.VISIBLE);
        pickedAddress.setText("Adresa aleasa este: " + location);

        Geocoder geocoder = new Geocoder(getApplicationContext());
        try {
            addressList = geocoder.getFromLocationName(location, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Address pickedAddress = addressList.get(0);

        fabPickedAddressed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent placingOrder = new Intent(MapsActivity.this, PlaceOrder.class);
                placingOrder.putExtra("origin", "mapsActivity");
                placingOrder.putExtra("address", pickedAddress.toString());
                placingOrder.putExtra("latitude", pickedAddress.getLatitude());
                placingOrder.putExtra("longitude", pickedAddress.getLongitude());
                startActivity(placingOrder);
                finish();
            }
        });
    }

    @Override
    public void onMarkerDragStart(@NonNull Marker marker) {

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
}