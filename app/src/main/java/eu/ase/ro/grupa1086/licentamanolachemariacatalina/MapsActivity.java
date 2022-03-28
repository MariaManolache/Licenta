package eu.ase.ro.grupa1086.licentamanolachemariacatalina;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.List;

import eu.ase.ro.grupa1086.licentamanolachemariacatalina.account.Account;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.account.SignIn;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.databinding.ActivityMapsBinding;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.food.FoodList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    SearchView searchView;
    AlertDialog.Builder noLocationFoundAlert;
    FloatingActionButton fabPickedAddressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
//        binding = ActivityMapsBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());

        searchView = findViewById(R.id.searchViewLocation);
        noLocationFoundAlert = new AlertDialog.Builder(this);
        fabPickedAddressed = findViewById(R.id.fabPickedAddress);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String location = searchView.getQuery().toString();
                List<Address> addressList = null;

                if (location != null || !location.equals("")) {
                    Geocoder geocoder = new Geocoder(getApplicationContext());
                    try {
                        addressList = geocoder.getFromLocationName(location, 1);
                    } catch(IOException e) {
                        e.printStackTrace();
                    }

                    if(addressList.size() > 0) {
                        Address address = addressList.get(0);
                        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                        mMap.addMarker(new MarkerOptions().position(latLng).title(location));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));

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
                            }
                        });
                    } else {
                        noLocationFoundAlert.setTitle("Locatie indisponibila")
                                .setMessage("Adresa introdusa nu este disponibila. Introduceti alta adresa valida.")
                                .setNeutralButton("OK", null)
                                .create().show();

                    }
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });



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


        LatLng romania = new LatLng(44.439663, 26.096306);
        mMap.addMarker(new MarkerOptions().position(romania));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(romania, 15));
    }
}