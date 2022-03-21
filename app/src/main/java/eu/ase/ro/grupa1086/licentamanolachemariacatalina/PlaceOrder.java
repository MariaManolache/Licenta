package eu.ase.ro.grupa1086.licentamanolachemariacatalina;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.Address;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.Food;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.Order;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.PaymentMethod;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.Status;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;

public class PlaceOrder extends AppCompatActivity {

    Spinner paymentSpinner;
    Float total;
    TextView tvTotal;
    Button btnPlaceOrder;
    LinearLayout firstRow;
    LinearLayout secondRow;

    EditText etStreet;
    EditText etNumber;
    EditText etBlock;
    EditText etEntrance;
    EditText etFloor;
    EditText etApartment;
    RadioGroup radioGroup;

    TextView tvCoordinates;
    TextView tvCurrentAddress;

    FirebaseDatabase database;
    DatabaseReference cart;
    DatabaseReference addresses;
    DatabaseReference orders;

    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    String userId;

    List<Food> cartList = new ArrayList<Food>();

    LocationRequest locationRequest;
    LocationCallback locationCallback;
    FusedLocationProviderClient fusedLocationProviderClient;
    Location currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_order);

        paymentSpinner = findViewById(R.id.paymentSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.paymentMethod,
                R.layout.spinner_layout);
        paymentSpinner.setAdapter(adapter);

        firstRow = findViewById(R.id.firstRow);
        secondRow = findViewById(R.id.secondRow);
        etStreet = findViewById(R.id.etStreet);
        etNumber = findViewById(R.id.etStreetNumber);
        etBlock = findViewById(R.id.etBlock);
        etEntrance = findViewById(R.id.etEntrance);
        etFloor = findViewById(R.id.etFloor);
        etApartment = findViewById(R.id.etApartment);
        tvCoordinates = findViewById(R.id.tvCoordinates);
        tvCurrentAddress = findViewById(R.id.tvCurrentAddress);
        radioGroup = findViewById(R.id.radioGroup);

        tvTotal = findViewById(R.id.total);
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder);

        database = FirebaseDatabase.getInstance();

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        userId = user.getUid();
        cart = database.getInstance().getReference("carts").child(userId).child("foodList");
        addresses = database.getInstance().getReference("addresses").child(userId).child("addresses");
        orders = database.getInstance().getReference("orders").child(userId);

        RadioGroup rg = (RadioGroup) findViewById(R.id.radioGroup);

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.radioBtnAnotherLocation:
                        firstRow.setVisibility(View.VISIBLE);
                        secondRow.setVisibility(View.VISIBLE);
                        tvCoordinates.setVisibility(View.GONE);

                        btnPlaceOrder.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                    String street = etStreet.getText().toString();
                                    String number = etNumber.getText().toString();
                                    String block = etBlock.getText().toString();
                                    String entrance = etEntrance.getText().toString();
                                    String floor = etFloor.getText().toString();
                                    String apartment = etApartment.getText().toString();


                                    if (TextUtils.isEmpty(street)) {
                                        etStreet.setError("Strada este necesara pentru plasarea comenzii");
                                        return;
                                    }

                                    if (TextUtils.isEmpty(number)) {
                                        etNumber.setError("Numarul strazii este necesar pentru plasarea comenzii");
                                        return;
                                    }

                                    String addressId = addresses.push().getKey();
                                    Address newAddress = new Address(addressId, street, number, block, entrance, floor, apartment, userId);



                                    addresses.child(addressId).setValue(newAddress).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(PlaceOrder.this, "Adresa adaugata", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(PlaceOrder.this, "Eroare la adaugarea adresei" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });

                                    String orderId = orders.push().getKey();
                                    PaymentMethod paymentMethod = PaymentMethod.valueOf(paymentSpinner.getSelectedItem().toString().toUpperCase().replace(" ", "_"));
                                    Status status = Status.plasata;
                                    Order order = new Order(orderId, total, userId, paymentMethod, newAddress, cartList, status);

                                    orders.child(orderId).setValue(order).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(PlaceOrder.this, "Comanda plasata", Toast.LENGTH_LONG).show();
                                            } else {
                                                Toast.makeText(PlaceOrder.this, "Eroare la plasarea comenzii" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });

                                }

                        });
                        break;
                    case R.id.radioBtnCurrentLocation:
                        firstRow.setVisibility(View.GONE);
                        secondRow.setVisibility(View.GONE);
                        btnPlaceOrder.setOnClickListener(new View.OnClickListener() {
                            @SuppressLint("MissingPermission")
                            @Override
                            public void onClick(View v) {
                                initializeLocation();

                                fusedLocationProviderClient.getLastLocation()
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getApplicationContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                tvCoordinates.setVisibility(View.GONE);
                                            }
                                        })
                                        .addOnCompleteListener(new OnCompleteListener<Location>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Location> task) {
                                                String coordinates = new StringBuilder()
                                                        .append(task.getResult().getLatitude())
                                                        .append("/")
                                                        .append(task.getResult().getLongitude()).toString();

                                                Single<String> singleAddress = Single.just(getAddressFromLatLng(task.getResult().getLatitude(),
                                                        task.getResult().getLongitude()));

                                                Disposable disposable = singleAddress.subscribeWith(new DisposableSingleObserver<String>() {
                                                    @Override
                                                    public void onSuccess(String s) {
                                                        tvCoordinates.setText(coordinates);
                                                        tvCurrentAddress.setText(s);
                                                        tvCoordinates.setVisibility(View.VISIBLE);
                                                        tvCurrentAddress.setVisibility(View.VISIBLE);
                                                    }

                                                    @Override
                                                    public void onError(Throwable e) {
                                                        tvCoordinates.setText(e.getMessage());
                                                        tvCurrentAddress.setText(e.getMessage());
                                                        tvCoordinates.setVisibility(View.VISIBLE);
                                                        tvCurrentAddress.setVisibility(View.VISIBLE);
                                                    }
                                                });


//                                                tvCoordinates.setText(coordinates);
//                                                tvCoordinates.setVisibility(View.VISIBLE);

                                            }
                                        });
                            }

                        });
                        break;
                }
            }
        });

//        if(selectedText.equals(R.string.current_location)) {
//            firstRow.setVisibility(View.VISIBLE);
//            secondRow.setVisibility(View.VISIBLE);
//        }
//
//        if(selectedText.equals(R.string.add_another_address)) {
//            firstRow.setVisibility(View.GONE);
//            secondRow.setVisibility(View.GONE);
//        }

        cart.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                total = 0.0f;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Food food = dataSnapshot.getValue(Food.class);
                    cartList.add(food);
                    total += food.getPrice() * food.getQuantity();
                }

                tvTotal.setText(String.valueOf(total + " LEI"));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


//        if(getIntent() != null) {
//            total = getIntent().getStringExtra("total");
//            tvTotal.setText(total);
//        }
    }

    private String getAddressFromLatLng(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        String result = "";
        try {
            List<android.location.Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);
            if(addressList != null && addressList.size() > 0)
            {
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
}