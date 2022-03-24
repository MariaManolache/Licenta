package eu.ase.ro.grupa1086.licentamanolachemariacatalina;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
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
import android.widget.ImageButton;
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
import com.google.android.gms.maps.model.LatLng;
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
    LinearLayout thirdRow;
    LinearLayout fourthRow;
//    LinearLayout mapRow;

    EditText etStreet;
    EditText etNumber;
    EditText etBlock;
    EditText etEntrance;
    EditText etFloor;
    EditText etApartment;
    RadioGroup radioGroup;
    EditText etCity;
    EditText etRegion;

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

    String mapsAddress;
    TextView tvAddress;
    TextView tvAddressInfo;
    Button btnConfirmAddress;

    Address newAddress;
    Double latitude;
    Double longitude;

    String street;
    String number;
    String block;
    String entrance;
    String floor;
    String apartment;
    String city;
    String region;

//    ImageButton mapButton;

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
        thirdRow = findViewById(R.id.thirdRow);
        fourthRow = findViewById(R.id.fourthRow);
//        mapRow = findViewById(R.id.mapRow);

        btnConfirmAddress = findViewById(R.id.btnConfirmAddress);
        etStreet = findViewById(R.id.etStreet);
        etNumber = findViewById(R.id.etStreetNumber);
        etBlock = findViewById(R.id.etBlock);
        etEntrance = findViewById(R.id.etEntrance);
        etFloor = findViewById(R.id.etFloor);
        etApartment = findViewById(R.id.etApartment);
        tvCoordinates = findViewById(R.id.tvCoordinates);
        tvCurrentAddress = findViewById(R.id.tvCurrentAddress);
        radioGroup = findViewById(R.id.radioGroup);

        etCity = findViewById(R.id.etCity);
        etRegion = findViewById(R.id.etRegion);

        tvTotal = findViewById(R.id.total);
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder);

        database = FirebaseDatabase.getInstance();

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        userId = user.getUid();
        cart = database.getInstance().getReference("carts").child(userId).child("foodList");
        addresses = database.getInstance().getReference("addresses").child(userId).child("addresses");
        orders = database.getInstance().getReference("orders").child(userId);

        tvAddress = findViewById(R.id.tvPickedAddress);
        tvAddressInfo = findViewById(R.id.tvPickedAddressInfo);
//        mapButton = findViewById(R.id.btnImageMap);

        RadioGroup rg = (RadioGroup) findViewById(R.id.radioGroup);


        if (getIntent() != null && getIntent().getExtras() != null) {
            String origin = getIntent().getExtras().getString("origin");
            if (origin != null && origin.equals("mapsActivity")) {
                rg.check(R.id.radioBtnMapsLocation);
                mapsAddress = getIntent().getStringExtra("address");
                latitude = getIntent().getDoubleExtra("latitude", 0.0);
                longitude = getIntent().getDoubleExtra("longitude", 0.0);
            }
        }

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @SuppressLint("MissingPermission")
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radioBtnAnotherLocation:
                        firstRow.setVisibility(View.VISIBLE);
                        secondRow.setVisibility(View.VISIBLE);
                        thirdRow.setVisibility(View.VISIBLE);
                        fourthRow.setVisibility(View.VISIBLE);
//                        tvCoordinates.setVisibility(View.GONE);
//                        tvCurrentAddress.setVisibility(View.GONE);
                        tvAddressInfo.setVisibility(View.GONE);
                        tvAddress.setVisibility(View.GONE);


//                        btnCoordinates.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                String street = etStreet.getText().toString();
//                                String number = etNumber.getText().toString();
//                                String city = etCity.getText().toString();
//                                String region = etRegion.getText().toString();
//
//                                StringBuilder stringBuilder = new StringBuilder();
//                                stringBuilder.append(street);
//                                stringBuilder.append(number);
//                                stringBuilder.append(city);
//                                stringBuilder.append(region);
//
//                                String result = stringBuilder.toString();
//                                coordinates.setText(getLocationFromAddress(result).toString());
//
//                                Single<LatLng> singleAddress = Single.just(getLocationFromAddress(result));

//                                Disposable disposable = singleAddress.subscribeWith(new DisposableSingleObserver<String>() {
//                                    @Override
//                                    public void onSuccess(String s) {
//                                        //tvCoordinates.setText(singleAddress);
//                                        tvCurrentAddress.setText(s);
//                                        tvCoordinates.setVisibility(View.VISIBLE);
//                                        tvCurrentAddress.setVisibility(View.VISIBLE);
//                                    }
//
//                                    @Override
//                                    public void onError(Throwable e) {
//                                        tvCoordinates.setText(e.getMessage());
//                                        tvCurrentAddress.setText(e.getMessage());
//                                        tvCoordinates.setVisibility(View.VISIBLE);
//                                        tvCurrentAddress.setVisibility(View.VISIBLE);
//                                    }
//                                });
//                            }
//                        });

                        btnConfirmAddress.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                street = etStreet.getText().toString();
                                number = etNumber.getText().toString();
                                block = etBlock.getText().toString();
                                entrance = etEntrance.getText().toString();
                                floor = etFloor.getText().toString();
                                apartment = etApartment.getText().toString();
                                city = etCity.getText().toString();
                                region = etRegion.getText().toString();


                                if (TextUtils.isEmpty(street)) {
                                    etStreet.setError("Strada este necesara pentru plasarea comenzii");
                                    return;
                                }

                                if (TextUtils.isEmpty(number)) {
                                    etNumber.setError("Numarul strazii este necesar pentru plasarea comenzii");
                                    return;
                                }


                                StringBuilder stringBuilder = new StringBuilder();
                                stringBuilder.append(street + " ");
                                stringBuilder.append(number + " ");
                                stringBuilder.append(block + " ");
                                stringBuilder.append(entrance + " ");
                                stringBuilder.append(floor + " ");
                                stringBuilder.append(apartment + " ");
                                stringBuilder.append(city + " ");
                                stringBuilder.append(region + " ");

                                String result = stringBuilder.toString();


//                                tvAddressInfo.setText(getLocationFromAddress(result).toString());
                                tvAddressInfo.setText(result);
                                tvAddressInfo.setVisibility(View.VISIBLE);
                                tvAddress.setVisibility(View.VISIBLE);
                            }
                        });

                        btnPlaceOrder.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                if (TextUtils.isEmpty(street)) {
                                    etStreet.setError("Strada este necesara pentru plasarea comenzii");
                                    return;
                                }

                                if (TextUtils.isEmpty(number)) {
                                    etNumber.setError("Numarul strazii este necesar pentru plasarea comenzii");
                                    return;
                                }

                                String addressId = addresses.push().getKey();

                                StringBuilder coordinates = new StringBuilder();
                                coordinates.append(street);
                                coordinates.append(number);
                                coordinates.append(city);
                                coordinates.append(region);

                                String coordinatesString = coordinates.toString();

                                newAddress = new Address(addressId, street, number, block, entrance, floor, apartment, city, region, userId, getLocationFromAddress(coordinatesString).toString());


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
                        thirdRow.setVisibility(View.GONE);
                        fourthRow.setVisibility(View.GONE);

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
//                                                tvCoordinates.setText(coordinates);
//                                                tvCurrentAddress.setText(s);
//                                                tvCoordinates.setVisibility(View.VISIBLE);
//                                                tvCurrentAddress.setVisibility(View.VISIBLE);

                                                tvAddressInfo.setText(s);
                                                tvAddressInfo.setVisibility(View.VISIBLE);
                                                tvAddress.setVisibility(View.VISIBLE);

                                                btnPlaceOrder.setOnClickListener(new View.OnClickListener() {
                                                    @SuppressLint("MissingPermission")
                                                    @Override
                                                    public void onClick(View v) {
                                                        String addressId = addresses.push().getKey();

                                                        newAddress = new Address(addressId, s);


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
                                            }

                                            @Override
                                            public void onError(Throwable e) {
                                                tvCoordinates.setText(e.getMessage());
                                                tvCurrentAddress.setText(e.getMessage());
                                                tvCoordinates.setVisibility(View.VISIBLE);
                                                tvCurrentAddress.setVisibility(View.VISIBLE);

                                                tvAddressInfo.setText(e.getMessage());
                                                tvAddressInfo.setVisibility(View.VISIBLE);
                                                tvAddress.setVisibility(View.VISIBLE);
                                            }
                                        });


//                                                tvCoordinates.setText(coordinates);
//                                                tvCoordinates.setVisibility(View.VISIBLE);

                                    }
                                });
                        break;

                    case R.id.radioBtnMapsLocation:

                        if (getIntent() != null && getIntent().getExtras() != null) {
                            String origin = getIntent().getExtras().getString("origin");
                            if (origin != null && origin.equals("mapsActivity")) {
                                mapsAddress = getIntent().getStringExtra("address");
                                latitude = getIntent().getDoubleExtra("latitude", 0.0);
                                longitude = getIntent().getDoubleExtra("longitude", 0.0);
                               // rg.check(R.id.radioBtnMapsLocation);
                            }
                        }

                        firstRow.setVisibility(View.GONE);
                        secondRow.setVisibility(View.GONE);
                        thirdRow.setVisibility(View.GONE);
                        fourthRow.setVisibility(View.VISIBLE);
                        tvAddressInfo.setVisibility(View.GONE);
                        tvAddress.setVisibility(View.GONE);
//                        mapRow.setVisibility(View.VISIBLE);

                        btnConfirmAddress.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(getApplicationContext(), MapsActivity.class));
                                finish();
                            }
                        });

//                        mapButton.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                startActivity(new Intent(getApplicationContext(), MapsActivity.class));
//                                finish();
//                            }
//                        });


                        if (mapsAddress != null && longitude != 0.0 && latitude != 0.0) {

                            String coordinates = new StringBuilder()
                                    .append(latitude)
                                    .append("/")
                                    .append(longitude).toString();

                            Single<String> singleAddress = Single.just(getAddressFromLatLng(latitude,
                                    longitude));

                            Disposable disposable = singleAddress.subscribeWith(new DisposableSingleObserver<String>() {
                                @Override
                                public void onSuccess(String s) {
//                                                tvCoordinates.setText(coordinates);
//                                                tvCurrentAddress.setText(s);
//                                                tvCoordinates.setVisibility(View.VISIBLE);
//                                                tvCurrentAddress.setVisibility(View.VISIBLE);

                                    tvAddressInfo.setText(s);
                                    tvAddressInfo.setVisibility(View.VISIBLE);
                                    tvAddress.setVisibility(View.VISIBLE);


                                    String addressId = addresses.push().getKey();

                                    newAddress = new Address(addressId, s);

//                            tvAddressInfo.setText(mapsAddress);
//                            tvAddressInfo.setVisibility(View.VISIBLE);
//                            tvAddress.setVisibility(View.VISIBLE);

                                    btnPlaceOrder.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
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

                                }

                                @Override
                                public void onError(Throwable e) {
                                    tvCoordinates.setText(e.getMessage());
                                    tvCurrentAddress.setText(e.getMessage());
                                    tvCoordinates.setVisibility(View.VISIBLE);
                                    tvCurrentAddress.setVisibility(View.VISIBLE);

                                    tvAddressInfo.setText(e.getMessage());
                                    tvAddressInfo.setVisibility(View.VISIBLE);
                                    tvAddress.setVisibility(View.VISIBLE);
                                }
                            });


                        } else {
                            Toast.makeText(PlaceOrder.this, "Nu s-a selectat nici o adresa de pe harta", Toast.LENGTH_LONG).show();
                        }

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