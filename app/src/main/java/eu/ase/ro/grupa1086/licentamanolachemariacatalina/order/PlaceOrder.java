package eu.ase.ro.grupa1086.licentamanolachemariacatalina.order;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
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

import eu.ase.ro.grupa1086.licentamanolachemariacatalina.card.CardPayment;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.R;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.address.AddressPicking;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.address.AddressesList;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.Address;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.Food;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.Order;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.PaymentMethod;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.Restaurant;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.RestaurantOrder;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.Status;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;

public class PlaceOrder extends AppCompatActivity {

    Spinner paymentSpinner;
    Float total;
    TextView tvTotal;
    Button btnPlaceOrder;
    FrameLayout firstFrameLayout;
    FrameLayout secondFrameLayout;
//    LinearLayout firstRow;
//    LinearLayout secondRow;
//    LinearLayout thirdRow;
//    LinearLayout fourthRow;
//    LinearLayout mapRow;

//    EditText etStreet;
//    EditText etNumber;
//    EditText etBlock;
//    EditText etEntrance;
//    EditText etFloor;
//    EditText etApartment;
//    RadioGroup radioGroup;
//    EditText etCity;
//    EditText etRegion;
//
//    TextView tvCoordinates;
//    TextView tvCurrentAddress;

    FirebaseDatabase database;
    DatabaseReference cart;
    DatabaseReference addresses;
    DatabaseReference orders;
    DatabaseReference restaurants;
    DatabaseReference driverOrders;
    DatabaseReference restaurantOrders;

    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    String userId;

    List<Food> cartList = new ArrayList<Food>();

//    LocationRequest locationRequest;
//    LocationCallback locationCallback;
//    FusedLocationProviderClient fusedLocationProviderClient;
//    Location currentLocation;

    String mapsAddress;
    TextView tvAddress;
    TextView tvAddressInfo;
    TextView tvNewAddress;
//    Button btnConfirmAddress;

    Address newAddress;
    String currentAddress;
    Double latitude;
    Double longitude;
//
//    String street;
//    String number;
//    String block;
//    String entrance;
//    String floor;
//    String apartment;
//    String city;
//    String region;

    List<Restaurant> restaurantAddresses = new ArrayList<Restaurant>();

    TextView tvSavedAddresses;
    String idSavedAddress;
    int same = 0;
    int sameAddress = 0;

    String anotherAddress;

//    ImageButton mapButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_order);

        paymentSpinner = findViewById(R.id.paymentSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.paymentMethod,
                R.layout.spinner_layout);
        paymentSpinner.setAdapter(adapter);

        tvTotal = findViewById(R.id.total);
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder);

        database = FirebaseDatabase.getInstance();

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        userId = user.getUid();
        cart = database.getInstance().getReference("carts").child(userId).child("foodList");
        addresses = database.getInstance().getReference("addresses").child(userId).child("addresses");
        orders = database.getInstance().getReference("orders").child(userId);
        restaurants = database.getInstance().getReference("restaurants");
        driverOrders = database.getInstance().getReference("driverOrders");
        restaurantOrders = database.getInstance().getReference("restaurantOrders");

        tvAddress = findViewById(R.id.tvPickedAddress);
        tvAddressInfo = findViewById(R.id.tvPickedAddressInfo);

        firstFrameLayout = findViewById(R.id.firstFrameLayout);
        secondFrameLayout = findViewById(R.id.secondFrameLayout);

        tvSavedAddresses = findViewById(R.id.tvSavedAddresses);
        tvNewAddress = findViewById(R.id.tvAddress);

        secondFrameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addressPicking = new Intent(PlaceOrder.this, AddressPicking.class);
                startActivity(addressPicking);
            }
        });

        firstFrameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent savedAddresses = new Intent(PlaceOrder.this, AddressesList.class);
                savedAddresses.putExtra("origin", "placeOrder");
                startActivity(savedAddresses);
            }
        });


        if (getIntent() != null && getIntent().getExtras() != null) {
            String origin = getIntent().getExtras().getString("origin");
            if (origin != null && origin.equals("mapsActivity")) {
                mapsAddress = getIntent().getStringExtra("address");
                latitude = getIntent().getDoubleExtra("latitude", 0.0);
                longitude = getIntent().getDoubleExtra("longitude", 0.0);

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

                        btnPlaceOrder.setEnabled(true);

                        String addressId = addresses.push().getKey();

                        newAddress = new Address(addressId, s);
                        newAddress.setChosenAddress(false);

//                            tvAddressInfo.setText(mapsAddress);
//                            tvAddressInfo.setVisibility(View.VISIBLE);
//                            tvAddress.setVisibility(View.VISIBLE);

                        btnPlaceOrder.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                same = 0;
                                addresses.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                            Address address1 = dataSnapshot.getValue(Address.class);
                                            if(address1.getMapsAddress().equals(newAddress.getMapsAddress())) {
                                                same = 1;
                                            }
                                        }

                                        if(same != 1) {
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
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });


                                String orderId = orders.push().getKey();
                                PaymentMethod paymentMethod = PaymentMethod.valueOf(paymentSpinner.getSelectedItem().toString().toUpperCase().replace(" ", "_"));
                                Status status = Status.plasata;
                                Order order = new Order(orderId, total, userId, paymentMethod, newAddress, cartList, status, restaurantAddresses);

                                List<String> restaurantsId = new ArrayList<>();
                                restaurantsId.add(cartList.get(0).getRestaurantId());

                                for(int i = 0; i < cartList.size() - 1; i++) {
                                    if(!cartList.get(i).getRestaurantId().equals(cartList.get(i+1).getRestaurantId())) {
                                        restaurantsId.add(cartList.get(i+1).getRestaurantId());
                                    }
                                }
                                List<Food> restaurantFood = new ArrayList<>();
                                for(int i = 0; i < restaurantsId.size(); i++) {
                                    for(int j = 0; j < cartList.size(); j++) {
                                        if(restaurantsId.get(i).equals(cartList.get(j).getRestaurantId())) {
                                            restaurantFood.add(cartList.get(j));
                                        }
                                    }
                                    RestaurantOrder restaurantOrder = new RestaurantOrder(restaurantsId.get(i), restaurantFood, orderId, status);
                                    restaurantOrders.child(restaurantsId.get(i)).child(orderId).setValue(restaurantOrder);
                                }

                                driverOrders.child(orderId).setValue(order);
                                orders.child(orderId).setValue(order).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(PlaceOrder.this, "Comanda plasata", Toast.LENGTH_LONG).show();


                                            if(paymentMethod.equals(PaymentMethod.CARD_ONLINE)) {
                                                Intent cardPayment = new Intent(PlaceOrder.this, CardPayment.class);
                                                cardPayment.putExtra("orderId", orderId);
                                                cardPayment.putExtra("origin", "cardPayment");
                                                startActivity(cardPayment);
                                                finish();
                                            } else {
                                                Intent confirmationOrder = new Intent(PlaceOrder.this, ConfirmationOrder.class);
                                                confirmationOrder.putExtra("orderId", orderId);
                                                confirmationOrder.putExtra("origin", "mapsLocation");
                                                startActivity(confirmationOrder);
                                                finish();
                                            }

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
//                        tvCoordinates.setText(e.getMessage());
//                        tvCurrentAddress.setText(e.getMessage());
//                        tvCoordinates.setVisibility(View.VISIBLE);
//                        tvCurrentAddress.setVisibility(View.VISIBLE);

                        tvAddressInfo.setText(e.getMessage());
                        tvAddressInfo.setVisibility(View.VISIBLE);
                        tvAddress.setVisibility(View.VISIBLE);
                    }
                });


                if (tvAddress.getVisibility() == View.GONE) {
                    btnPlaceOrder.setEnabled(false);
                }
                if (tvAddress.getVisibility() == View.VISIBLE) {
                    btnPlaceOrder.setEnabled(true);
                }

            }
            if (origin != null && origin.equals("savedAddresses")) {
                idSavedAddress = getIntent().getStringExtra("addressId");
            }
            if(origin != null && origin.equals("currentLocation")) {
                currentAddress = getIntent().getStringExtra("currentAddress");

                tvAddressInfo.setVisibility(View.VISIBLE);
                //tvAddress.setVisibility(View.VISIBLE);
                //tvAddress.setText(currentAddress);

                btnPlaceOrder.setOnClickListener(new View.OnClickListener() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onClick(View v) {
                        String addressId = addresses.push().getKey();

                        newAddress = new Address(addressId, currentAddress);
                        newAddress.setChosenAddress(false);


                        same = 0;
                        addresses.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    Address address1 = dataSnapshot.getValue(Address.class);
                                    if(address1.getMapsAddress().equals(newAddress.getMapsAddress())) {
                                        same = 1;
                                    }
                                }

                                if(same != 1) {
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
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });


                        String orderId = orders.push().getKey();
                        PaymentMethod paymentMethod = PaymentMethod.valueOf(paymentSpinner.getSelectedItem().toString().toUpperCase().replace(" ", "_"));
                        Status status = Status.plasata;
                        Order order = new Order(orderId, total, userId, paymentMethod, newAddress, cartList, status, restaurantAddresses);

                        List<String> restaurantsId = new ArrayList<>();
                        restaurantsId.add(cartList.get(0).getRestaurantId());

                        for(int i = 0; i < cartList.size() - 1; i++) {
                            if(!cartList.get(i).getRestaurantId().equals(cartList.get(i+1).getRestaurantId())) {
                                restaurantsId.add(cartList.get(i+1).getRestaurantId());
                            }
                        }
                        List<Food> restaurantFood = new ArrayList<>();
                        for(int i = 0; i < restaurantsId.size(); i++) {
                            for(int j = 0; j < cartList.size(); j++) {
                                if(restaurantsId.get(i).equals(cartList.get(j).getRestaurantId())) {
                                    restaurantFood.add(cartList.get(j));
                                }
                            }
                            RestaurantOrder restaurantOrder = new RestaurantOrder(restaurantsId.get(i), restaurantFood, orderId, status);
                            restaurantOrders.child(restaurantsId.get(i)).child(orderId).setValue(restaurantOrder);
                        }

                        Log.i("restaurant", String.valueOf(restaurantAddresses));
                        driverOrders.child(orderId).setValue(order);
                        orders.child(orderId).setValue(order).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(PlaceOrder.this, "Comanda plasata", Toast.LENGTH_LONG).show();


                                    if(paymentMethod.equals(PaymentMethod.CARD_ONLINE)) {
                                        Intent cardPayment = new Intent(PlaceOrder.this, CardPayment.class);
                                        cardPayment.putExtra("orderId", orderId);
                                        cardPayment.putExtra("origin", "cardPayment");
                                        startActivity(cardPayment);
                                        finish();
                                    } else {
                                        Intent confirmationOrder = new Intent(PlaceOrder.this, ConfirmationOrder.class);
                                        confirmationOrder.putExtra("orderId", orderId);
                                        confirmationOrder.putExtra("origin", "currentAddress");
                                        startActivity(confirmationOrder);
                                        finish();
                                    }
                                } else {
                                    Toast.makeText(PlaceOrder.this, "Eroare la plasarea comenzii" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }

                });

                tvAddress.setVisibility(View.VISIBLE);
                if (tvAddress.getVisibility() == View.GONE) {
                    btnPlaceOrder.setEnabled(false);
                }
                if (tvAddress.getVisibility() == View.VISIBLE) {
                    btnPlaceOrder.setEnabled(true);
                }

            }
            if (origin != null && origin.equals("anotherAddress")) {
                anotherAddress = getIntent().getStringExtra("result");
                String street = getIntent().getStringExtra("street");
                String number = getIntent().getStringExtra("number");
                String city = getIntent().getStringExtra("city");
                String region = getIntent().getStringExtra("region");
                String block = getIntent().getStringExtra("block");
                String entrance = getIntent().getStringExtra("entrance");
                String floor = getIntent().getStringExtra("floor");
                String apartment = getIntent().getStringExtra("apartment");

                StringBuilder coordinates = new StringBuilder();
                coordinates.append(street);
                coordinates.append(number);
                coordinates.append(city);
                coordinates.append(region);

                String coordinatesString = coordinates.toString();

                LatLng coordinatesNewAddress = getLocationFromAddress(coordinatesString);
                String address = getAddressFromLatLng(coordinatesNewAddress.latitude, coordinatesNewAddress.longitude);

                tvAddressInfo.setVisibility(View.VISIBLE);
                tvAddress.setVisibility(View.VISIBLE);
                tvAddressInfo.setText(address);

                btnPlaceOrder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String addressId = addresses.push().getKey();


                        newAddress = new Address(addressId, street, number, block, entrance, floor, apartment, city, region, userId, address);

                        newAddress.setChosenAddress(false);
                        same = 0;
                        addresses.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    Address address1 = dataSnapshot.getValue(Address.class);
                                    if (address1.getMapsAddress().equals(newAddress.getMapsAddress())) {
                                        same = 1;
                                    }
                                }

                                if (same != 1) {
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
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });


                        String orderId = orders.push().getKey();
                        PaymentMethod paymentMethod = PaymentMethod.valueOf(paymentSpinner.getSelectedItem().toString().toUpperCase().replace(" ", "_"));
                        Status status = Status.plasata;
                        Order order = new Order(orderId, total, userId, paymentMethod, newAddress, cartList, status, restaurantAddresses);

                        List<String> restaurantsId = new ArrayList<>();
                        restaurantsId.add(cartList.get(0).getRestaurantId());

                        for(int i = 0; i < cartList.size() - 1; i++) {
                            if(!cartList.get(i).getRestaurantId().equals(cartList.get(i+1).getRestaurantId())) {
                                restaurantsId.add(cartList.get(i+1).getRestaurantId());
                            }
                        }
                        List<Food> restaurantFood = new ArrayList<>();
                        for(int i = 0; i < restaurantsId.size(); i++) {
                            for(int j = 0; j < cartList.size(); j++) {
                                if(restaurantsId.get(i).equals(cartList.get(j).getRestaurantId())) {
                                    restaurantFood.add(cartList.get(j));
                                }
                            }
                            RestaurantOrder restaurantOrder = new RestaurantOrder(restaurantsId.get(i), restaurantFood, orderId, status);
                            restaurantOrders.child(restaurantsId.get(i)).child(orderId).setValue(restaurantOrder);
                        }

                        driverOrders.child(orderId).setValue(order);
                        orders.child(orderId).setValue(order).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(PlaceOrder.this, "Comanda plasata", Toast.LENGTH_LONG).show();

                                    //cart.removeValue();

                                    if(paymentMethod.equals(PaymentMethod.CARD_ONLINE)) {
                                        Intent cardPayment = new Intent(PlaceOrder.this, CardPayment.class);
                                        cardPayment.putExtra("orderId", orderId);
                                        cardPayment.putExtra("origin", "cardPayment-anotherAddress");
                                        startActivity(cardPayment);
                                        finish();
                                    } else {
                                        Intent confirmationOrder = new Intent(PlaceOrder.this, ConfirmationOrder.class);
                                        confirmationOrder.putExtra("orderId", orderId);
                                        confirmationOrder.putExtra("origin", "addAnotherAddress");
                                        startActivity(confirmationOrder);
                                        finish();
                                    }

                                } else {
                                    Toast.makeText(PlaceOrder.this, "Eroare la plasarea comenzii" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });

                    }

                });
            }
        }

        if (idSavedAddress != null) {
            addresses.child(idSavedAddress).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Address address = snapshot.getValue(Address.class);
                    tvAddressInfo.setVisibility(View.VISIBLE);
                    tvAddress.setVisibility(View.VISIBLE);
                    tvAddressInfo.setText(address.getMapsAddress());

                    if (tvAddress.getVisibility() == View.GONE) {
                        btnPlaceOrder.setEnabled(false);
                    }
                    if (tvAddress.getVisibility() == View.VISIBLE) {
                        btnPlaceOrder.setEnabled(true);
                    }


                    btnPlaceOrder.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String orderId = orders.push().getKey();
                            PaymentMethod paymentMethod = PaymentMethod.valueOf(paymentSpinner.getSelectedItem().toString().toUpperCase().replace(" ", "_"));
                            Status status = Status.plasata;
                            Order order = new Order(orderId, total, userId, paymentMethod, address, cartList, status, restaurantAddresses);

                            List<String> restaurantsId = new ArrayList<>();
                            restaurantsId.add(cartList.get(0).getRestaurantId());

                            for(int i = 0; i < cartList.size() - 1; i++) {
                                if(!cartList.get(i).getRestaurantId().equals(cartList.get(i+1).getRestaurantId())) {
                                    restaurantsId.add(cartList.get(i+1).getRestaurantId());
                                }
                            }
                            List<Food> restaurantFood = new ArrayList<>();
                            for(int i = 0; i < restaurantsId.size(); i++) {
                                for(int j = 0; j < cartList.size(); j++) {
                                    if(restaurantsId.get(i).equals(cartList.get(j).getRestaurantId())) {
                                        restaurantFood.add(cartList.get(j));
                                    }
                                }
                                RestaurantOrder restaurantOrder = new RestaurantOrder(restaurantsId.get(i), restaurantFood, orderId, status);
                                restaurantOrders.child(restaurantsId.get(i)).child(orderId).setValue(restaurantOrder);
                            }


                            driverOrders.child(orderId).setValue(order);
                            orders.child(orderId).setValue(order).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(PlaceOrder.this, "Comanda plasata", Toast.LENGTH_LONG).show();

//                                        cart.removeValue();

                                        if(paymentMethod.equals(PaymentMethod.CARD_ONLINE)) {
                                            Intent cardPayment = new Intent(PlaceOrder.this, CardPayment.class);
                                            cardPayment.putExtra("orderId", orderId);
                                            cardPayment.putExtra("origin", "cardPayment");
                                            startActivity(cardPayment);
                                            finish();
                                        } else {
                                            Intent confirmationOrder = new Intent(PlaceOrder.this, ConfirmationOrder.class);
                                            confirmationOrder.putExtra("orderId", orderId);
                                            confirmationOrder.putExtra("origin", "savedAddress");
                                            startActivity(confirmationOrder);
                                            finish();
                                        }

                                    } else {
                                        Toast.makeText(PlaceOrder.this, "Eroare la plasarea comenzii" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });


                        }
                    });

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        cart.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                total = 0.0f;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Food food = dataSnapshot.getValue(Food.class);
                    cartList.add(food);
                    total += food.getPrice() * food.getQuantity();
                    sameAddress = 0;
                    restaurants.child(food.getRestaurantId()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            sameAddress = 0;
                            Restaurant restaurant = snapshot.getValue(Restaurant.class);
                            for (Restaurant alreadySavedRestaurant : restaurantAddresses) {
                                if (restaurant.getId().equals(alreadySavedRestaurant.getId())) {
                                    sameAddress = 1;
                                }
                            }
                            if (sameAddress == 0) {
                                restaurantAddresses.add(restaurant);
                            }

                            Log.i("incercare", restaurant.toString());

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }

                tvTotal.setText(total + " LEI");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

//        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @SuppressLint("MissingPermission")
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//
//                switch (checkedId) {
//                    case R.id.radioBtnAnotherLocation:
//                        firstRow.setVisibility(View.VISIBLE);
//                        secondRow.setVisibility(View.VISIBLE);
//                        thirdRow.setVisibility(View.VISIBLE);
//                        fourthRow.setVisibility(View.VISIBLE);
////                        tvCoordinates.setVisibility(View.GONE);
////                        tvCurrentAddress.setVisibility(View.GONE);
//                        tvAddressInfo.setVisibility(View.GONE);
//                        tvAddress.setVisibility(View.GONE);
//
//
////                        btnCoordinates.setOnClickListener(new View.OnClickListener() {
////                            @Override
////                            public void onClick(View v) {
////                                String street = etStreet.getText().toString();
////                                String number = etNumber.getText().toString();
////                                String city = etCity.getText().toString();
////                                String region = etRegion.getText().toString();
////
////                                StringBuilder stringBuilder = new StringBuilder();
////                                stringBuilder.append(street);
////                                stringBuilder.append(number);
////                                stringBuilder.append(city);
////                                stringBuilder.append(region);
////
////                                String result = stringBuilder.toString();
////                                coordinates.setText(getLocationFromAddress(result).toString());
////
////                                Single<LatLng> singleAddress = Single.just(getLocationFromAddress(result));
//
////                                Disposable disposable = singleAddress.subscribeWith(new DisposableSingleObserver<String>() {
////                                    @Override
////                                    public void onSuccess(String s) {
////                                        //tvCoordinates.setText(singleAddress);
////                                        tvCurrentAddress.setText(s);
////                                        tvCoordinates.setVisibility(View.VISIBLE);
////                                        tvCurrentAddress.setVisibility(View.VISIBLE);
////                                    }
////
////                                    @Override
////                                    public void onError(Throwable e) {
////                                        tvCoordinates.setText(e.getMessage());
////                                        tvCurrentAddress.setText(e.getMessage());
////                                        tvCoordinates.setVisibility(View.VISIBLE);
////                                        tvCurrentAddress.setVisibility(View.VISIBLE);
////                                    }
////                                });
////                            }
////                        });
//
//                        btnConfirmAddress.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                street = etStreet.getText().toString();
//                                number = etNumber.getText().toString();
//                                block = etBlock.getText().toString();
//                                entrance = etEntrance.getText().toString();
//                                floor = etFloor.getText().toString();
//                                apartment = etApartment.getText().toString();
//                                city = etCity.getText().toString();
//                                region = etRegion.getText().toString();
//
//
//                                if (TextUtils.isEmpty(street)) {
//                                    etStreet.setError("Strada este necesara pentru plasarea comenzii");
//                                    return;
//                                }
//
//                                if (TextUtils.isEmpty(number)) {
//                                    etNumber.setError("Numarul strazii este necesar pentru plasarea comenzii");
//                                    return;
//                                }
//
//
//                                StringBuilder stringBuilder = new StringBuilder();
//                                stringBuilder.append(street + " ");
//                                stringBuilder.append(number + " ");
//                                stringBuilder.append(block + " ");
//                                stringBuilder.append(entrance + " ");
//                                stringBuilder.append(floor + " ");
//                                stringBuilder.append(apartment + " ");
//                                stringBuilder.append(city + " ");
//                                stringBuilder.append(region + " ");
//
//                                String result = stringBuilder.toString();
//
//
////                                tvAddressInfo.setText(getLocationFromAddress(result).toString());
//                                tvAddressInfo.setText(result);
//                                tvAddressInfo.setVisibility(View.VISIBLE);
//                                tvAddress.setVisibility(View.VISIBLE);
//
//                                tvAddress.setVisibility(View.VISIBLE);
//                                if (tvAddress.getVisibility() == View.GONE) {
//                                    btnPlaceOrder.setEnabled(false);
//                                }
//                                if (tvAddress.getVisibility() == View.VISIBLE) {
//                                    btnPlaceOrder.setEnabled(true);
//                                }
//                            }
//                        });
//
//                        btnPlaceOrder.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//
//                                if (TextUtils.isEmpty(street)) {
//                                    etStreet.setError("Strada este necesara pentru plasarea comenzii");
//                                    return;
//                                }
//
//                                if (TextUtils.isEmpty(number)) {
//                                    etNumber.setError("Numarul strazii este necesar pentru plasarea comenzii");
//                                    return;
//                                }
//
//                                String addressId = addresses.push().getKey();
//
//                                StringBuilder coordinates = new StringBuilder();
//                                coordinates.append(street);
//                                coordinates.append(number);
//                                coordinates.append(city);
//                                coordinates.append(region);
//
//                                String coordinatesString = coordinates.toString();
//
//                                LatLng coordinatesNewAddress = getLocationFromAddress(coordinatesString);
//                                String address = getAddressFromLatLng(coordinatesNewAddress.latitude, coordinatesNewAddress.longitude);
//
//                                newAddress = new Address(addressId, street, number, block, entrance, floor, apartment, city, region, userId, address);
//
//                                same = 0;
//                                addresses.addValueEventListener(new ValueEventListener() {
//                                    @Override
//                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                                            Address address1 = dataSnapshot.getValue(Address.class);
//                                            if(address1.getMapsAddress().equals(newAddress.getMapsAddress())) {
//                                                same = 1;
//                                            }
//                                        }
//
//                                        if(same != 1) {
//                                            addresses.child(addressId).setValue(newAddress).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                @Override
//                                                public void onComplete(@NonNull Task<Void> task) {
//                                                    if (task.isSuccessful()) {
//                                                        Toast.makeText(PlaceOrder.this, "Adresa adaugata", Toast.LENGTH_SHORT).show();
//                                                    } else {
//                                                        Toast.makeText(PlaceOrder.this, "Eroare la adaugarea adresei" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
//                                                    }
//                                                }
//                                            });
//                                        }
//                                    }
//
//                                    @Override
//                                    public void onCancelled(@NonNull DatabaseError error) {
//
//                                    }
//                                });
//
//
//                                String orderId = orders.push().getKey();
//                                PaymentMethod paymentMethod = PaymentMethod.valueOf(paymentSpinner.getSelectedItem().toString().toUpperCase().replace(" ", "_"));
//                                Status status = Status.plasata;
//                                Order order = new Order(orderId, total, userId, paymentMethod, newAddress, cartList, status, restaurantAddresses);
//
//                                orders.child(orderId).setValue(order).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<Void> task) {
//                                        if (task.isSuccessful()) {
//                                            Toast.makeText(PlaceOrder.this, "Comanda plasata", Toast.LENGTH_LONG).show();
//
//                                            cart.removeValue();
//                                            Intent confirmationOrder = new Intent(PlaceOrder.this, ConfirmationOrder.class);
//                                            confirmationOrder.putExtra("orderId", orderId);
//                                            confirmationOrder.putExtra("origin", "addAnotherAddress");
//                                            startActivity(confirmationOrder);
//                                            finish();
//
//                                        } else {
//                                            Toast.makeText(PlaceOrder.this, "Eroare la plasarea comenzii" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
//                                        }
//                                    }
//                                });
//
//                            }
//
//                        });
//
//                        break;
//                    case R.id.radioBtnCurrentLocation:
//                        firstRow.setVisibility(View.GONE);
//                        secondRow.setVisibility(View.GONE);
//                        thirdRow.setVisibility(View.GONE);
//                        fourthRow.setVisibility(View.GONE);
//                        tvAddress.setVisibility(View.VISIBLE);
//
//                        initializeLocation();
//
//                        fusedLocationProviderClient.getLastLocation()
//                                .addOnFailureListener(new OnFailureListener() {
//                                    @Override
//                                    public void onFailure(@NonNull Exception e) {
//                                        Toast.makeText(getApplicationContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
//                                        tvCoordinates.setVisibility(View.GONE);
//                                    }
//                                })
//                                .addOnCompleteListener(new OnCompleteListener<Location>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<Location> task) {
//                                        String coordinates = new StringBuilder()
//                                                .append(task.getResult().getLatitude())
//                                                .append("/")
//                                                .append(task.getResult().getLongitude()).toString();
//
//                                        Single<String> singleAddress = Single.just(getAddressFromLatLng(task.getResult().getLatitude(),
//                                                task.getResult().getLongitude()));
//
//                                        Disposable disposable = singleAddress.subscribeWith(new DisposableSingleObserver<String>() {
//                                            @Override
//                                            public void onSuccess(String s) {
////                                                tvCoordinates.setText(coordinates);
////                                                tvCurrentAddress.setText(s);
////                                                tvCoordinates.setVisibility(View.VISIBLE);
////                                                tvCurrentAddress.setVisibility(View.VISIBLE);
//
//                                                tvAddressInfo.setText(s);
//                                                tvAddressInfo.setVisibility(View.VISIBLE);
//                                                tvAddress.setVisibility(View.VISIBLE);
//
//                                                btnPlaceOrder.setOnClickListener(new View.OnClickListener() {
//                                                    @SuppressLint("MissingPermission")
//                                                    @Override
//                                                    public void onClick(View v) {
//                                                        String addressId = addresses.push().getKey();
//
//                                                        newAddress = new Address(addressId, s);
//
//
//                                                        same = 0;
//                                                        addresses.addValueEventListener(new ValueEventListener() {
//                                                            @Override
//                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                                                                    Address address1 = dataSnapshot.getValue(Address.class);
//                                                                    if(address1.getMapsAddress().equals(newAddress.getMapsAddress())) {
//                                                                        same = 1;
//                                                                    }
//                                                                }
//
//                                                                if(same != 1) {
//                                                                    addresses.child(addressId).setValue(newAddress).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                                        @Override
//                                                                        public void onComplete(@NonNull Task<Void> task) {
//                                                                            if (task.isSuccessful()) {
//                                                                                Toast.makeText(PlaceOrder.this, "Adresa adaugata", Toast.LENGTH_SHORT).show();
//                                                                            } else {
//                                                                                Toast.makeText(PlaceOrder.this, "Eroare la adaugarea adresei" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
//                                                                            }
//                                                                        }
//                                                                    });
//                                                                }
//                                                            }
//
//                                                            @Override
//                                                            public void onCancelled(@NonNull DatabaseError error) {
//
//                                                            }
//                                                        });
//
//
//                                                        String orderId = orders.push().getKey();
//                                                        PaymentMethod paymentMethod = PaymentMethod.valueOf(paymentSpinner.getSelectedItem().toString().toUpperCase().replace(" ", "_"));
//                                                        Status status = Status.plasata;
//                                                        Order order = new Order(orderId, total, userId, paymentMethod, newAddress, cartList, status, restaurantAddresses);
//
//                                                        Log.i("restaurant", String.valueOf(restaurantAddresses));
//                                                        orders.child(orderId).setValue(order).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                            @Override
//                                                            public void onComplete(@NonNull Task<Void> task) {
//                                                                if (task.isSuccessful()) {
//                                                                    Toast.makeText(PlaceOrder.this, "Comanda plasata", Toast.LENGTH_LONG).show();
//
//                                                                    Intent confirmationOrder = new Intent(PlaceOrder.this, ConfirmationOrder.class);
//                                                                    confirmationOrder.putExtra("orderId", orderId);
//                                                                    confirmationOrder.putExtra("origin", "currentAddress");
//                                                                    startActivity(confirmationOrder);
//                                                                    finish();
//                                                                } else {
//                                                                    Toast.makeText(PlaceOrder.this, "Eroare la plasarea comenzii" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
//                                                                }
//                                                            }
//                                                        });
//                                                    }
//
//                                                });
//                                            }
//
//                                            @Override
//                                            public void onError(Throwable e) {
//                                                tvCoordinates.setText(e.getMessage());
//                                                tvCurrentAddress.setText(e.getMessage());
//                                                tvCoordinates.setVisibility(View.VISIBLE);
//                                                tvCurrentAddress.setVisibility(View.VISIBLE);
//
//                                                tvAddressInfo.setText(e.getMessage());
//                                                tvAddressInfo.setVisibility(View.VISIBLE);
//                                                tvAddress.setVisibility(View.VISIBLE);
//                                            }
//                                        });
//
//
//                                    }
//                                });
//
//                        tvAddress.setVisibility(View.VISIBLE);
//                        if (tvAddress.getVisibility() == View.GONE) {
//                            btnPlaceOrder.setEnabled(false);
//                        }
//                        if (tvAddress.getVisibility() == View.VISIBLE) {
//                            btnPlaceOrder.setEnabled(true);
//                        }
//                        break;
//
//                    case R.id.radioBtnMapsLocation:
//
//                        if (getIntent() != null && getIntent().getExtras() != null) {
//                            String origin = getIntent().getExtras().getString("origin");
//                            if (origin != null && origin.equals("mapsActivity")) {
//                                mapsAddress = getIntent().getStringExtra("address");
//                                latitude = getIntent().getDoubleExtra("latitude", 0.0);
//                                longitude = getIntent().getDoubleExtra("longitude", 0.0);
//                                // rg.check(R.id.radioBtnMapsLocation);
//                            }
//                        }
//
//                        firstRow.setVisibility(View.GONE);
//                        secondRow.setVisibility(View.GONE);
//                        thirdRow.setVisibility(View.GONE);
//                        fourthRow.setVisibility(View.VISIBLE);
//                        tvAddressInfo.setVisibility(View.GONE);
//                        tvAddress.setVisibility(View.GONE);
//
//                        btnConfirmAddress.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                startActivity(new Intent(getApplicationContext(), MapsActivity.class));
//                                finish();
//                            }
//                        });
//
//
//                        if (mapsAddress != null && longitude != 0.0 && latitude != 0.0) {
//
//                            String coordinates = new StringBuilder()
//                                    .append(latitude)
//                                    .append("/")
//                                    .append(longitude).toString();
//
//                            Single<String> singleAddress = Single.just(getAddressFromLatLng(latitude,
//                                    longitude));
//
//                            Disposable disposable = singleAddress.subscribeWith(new DisposableSingleObserver<String>() {
//                                @Override
//                                public void onSuccess(String s) {
////                                                tvCoordinates.setText(coordinates);
////                                                tvCurrentAddress.setText(s);
////                                                tvCoordinates.setVisibility(View.VISIBLE);
////                                                tvCurrentAddress.setVisibility(View.VISIBLE);
//
//                                    tvAddressInfo.setText(s);
//                                    tvAddressInfo.setVisibility(View.VISIBLE);
//                                    tvAddress.setVisibility(View.VISIBLE);
//
//                                    btnPlaceOrder.setEnabled(true);
//
//                                    String addressId = addresses.push().getKey();
//
//                                    newAddress = new Address(addressId, s);
//
////                            tvAddressInfo.setText(mapsAddress);
////                            tvAddressInfo.setVisibility(View.VISIBLE);
////                            tvAddress.setVisibility(View.VISIBLE);
//
//                                    btnPlaceOrder.setOnClickListener(new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View v) {
//
//                                            same = 0;
//                                            addresses.addValueEventListener(new ValueEventListener() {
//                                                @Override
//                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                                                        Address address1 = dataSnapshot.getValue(Address.class);
//                                                        if(address1.getMapsAddress().equals(newAddress.getMapsAddress())) {
//                                                            same = 1;
//                                                        }
//                                                    }
//
//                                                    if(same != 1) {
//                                                        addresses.child(addressId).setValue(newAddress).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                            @Override
//                                                            public void onComplete(@NonNull Task<Void> task) {
//                                                                if (task.isSuccessful()) {
//                                                                    Toast.makeText(PlaceOrder.this, "Adresa adaugata", Toast.LENGTH_SHORT).show();
//                                                                } else {
//                                                                    Toast.makeText(PlaceOrder.this, "Eroare la adaugarea adresei" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
//                                                                }
//                                                            }
//                                                        });
//                                                    }
//                                                }
//
//                                                @Override
//                                                public void onCancelled(@NonNull DatabaseError error) {
//
//                                                }
//                                            });
//
//
//                                            String orderId = orders.push().getKey();
//                                            PaymentMethod paymentMethod = PaymentMethod.valueOf(paymentSpinner.getSelectedItem().toString().toUpperCase().replace(" ", "_"));
//                                            Status status = Status.plasata;
//                                            Order order = new Order(orderId, total, userId, paymentMethod, newAddress, cartList, status, restaurantAddresses);
//
//                                            orders.child(orderId).setValue(order).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                @Override
//                                                public void onComplete(@NonNull Task<Void> task) {
//                                                    if (task.isSuccessful()) {
//                                                        Toast.makeText(PlaceOrder.this, "Comanda plasata", Toast.LENGTH_LONG).show();
//
//
//                                                        Intent confirmationOrder = new Intent(PlaceOrder.this, ConfirmationOrder.class);
//                                                        confirmationOrder.putExtra("orderId", orderId);
//                                                        confirmationOrder.putExtra("origin", "mapsLocation");
//                                                        startActivity(confirmationOrder);
//                                                        finish();
//
//                                                    } else {
//                                                        Toast.makeText(PlaceOrder.this, "Eroare la plasarea comenzii" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
//                                                    }
//                                                }
//                                            });
//                                        }
//                                    });
//
//                                }
//
//                                @Override
//                                public void onError(Throwable e) {
//                                    tvCoordinates.setText(e.getMessage());
//                                    tvCurrentAddress.setText(e.getMessage());
//                                    tvCoordinates.setVisibility(View.VISIBLE);
//                                    tvCurrentAddress.setVisibility(View.VISIBLE);
//
//                                    tvAddressInfo.setText(e.getMessage());
//                                    tvAddressInfo.setVisibility(View.VISIBLE);
//                                    tvAddress.setVisibility(View.VISIBLE);
//                                }
//                            });
//
//
//                        } else {
//                            Toast.makeText(PlaceOrder.this, "Nu s-a selectat nici o adresa de pe harta", Toast.LENGTH_LONG).show();
//                        }
//
//                        //tvAddress.setVisibility(View.VISIBLE);
//                        if (tvAddress.getVisibility() == View.GONE) {
//                            btnPlaceOrder.setEnabled(false);
//                        }
//                        if (tvAddress.getVisibility() == View.VISIBLE) {
//                            btnPlaceOrder.setEnabled(true);
//                        }
//                        break;
//                }
//            }
//        });
//
//
//        if (tvAddress.getVisibility() == View.GONE) {
//            btnPlaceOrder.setEnabled(false);
//        }
//        if (tvAddress.getVisibility() == View.VISIBLE) {
//            btnPlaceOrder.setEnabled(true);
//        }
//    }
//
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
//
//    private void initializeLocation() {
//        buildLocationRequest();
//        buildLocationCallback();
//        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
//    }
//
//    private void buildLocationCallback() {
//        locationCallback = new LocationCallback() {
//            @Override
//            public void onLocationResult(@NonNull LocationResult locationResult) {
//                super.onLocationResult(locationResult);
//                currentLocation = locationResult.getLastLocation();
//            }
//        };
//    }
//
//    private void buildLocationRequest() {
//        locationRequest = new LocationRequest();
//        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        locationRequest.setInterval(5000);
//        locationRequest.setFastestInterval(3000);
//        locationRequest.setSmallestDisplacement(10f);
//    }
//
//    @Override
//    public void onStop() {
//        if (fusedLocationProviderClient != null) {
//            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
//        }
//        super.onStop();
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        if (fusedLocationProviderClient != null) {
//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                // TODO: Consider calling
//                //    ActivityCompat#requestPermissions
//                // here to request the missing permissions, and then overriding
//                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                //                                          int[] grantResults)
//                // to handle the case where the user grants the permission. See the documentation
//                // for ActivityCompat#requestPermissions for more details.
//                return;
//            }
//            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
//        }
//    }
}