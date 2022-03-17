package eu.ase.ro.grupa1086.licentamanolachemariacatalina;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.Address;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.Food;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.Order;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.PaymentMethod;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.Status;

public class PlaceOrder extends AppCompatActivity {
    
    Spinner paymentSpinner;
    Float total;
    TextView tvTotal;
    Button btnPlaceOrder;

    EditText etStreet;
    EditText etNumber;
    EditText etBlock;
    EditText etEntrance;
    EditText etFloor;
    EditText etApartment;

    FirebaseDatabase database;
    DatabaseReference cart;
    DatabaseReference addresses;
    DatabaseReference orders;

    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    String userId;

    List<Food> cartList = new ArrayList<Food>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_order);

        paymentSpinner = findViewById(R.id.paymentSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.paymentMethod,
                R.layout.spinner_layout);
        paymentSpinner.setAdapter(adapter);

        etStreet = findViewById(R.id.etStreet);
        etNumber = findViewById(R.id.etStreetNumber);
        etBlock = findViewById(R.id.etBlock);
        etEntrance = findViewById(R.id.etEntrance);
        etFloor = findViewById(R.id.etFloor);
        etApartment = findViewById(R.id.etApartment);

        tvTotal = findViewById(R.id.total);
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder);

        database = FirebaseDatabase.getInstance();

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        userId = user.getUid();
        cart = database.getInstance().getReference("carts").child(userId).child("foodList");
        addresses = database.getInstance().getReference("addresses").child(userId).child("addresses");
        orders = database.getInstance().getReference("orders").child(userId);

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

        btnPlaceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String street = etStreet.getText().toString();
                String number = etNumber.getText().toString();
                String block = etBlock.getText().toString();
                String entrance = etEntrance.getText().toString();
                String floor = etFloor.getText().toString();
                String apartment = etApartment.getText().toString();

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

//        if(getIntent() != null) {
//            total = getIntent().getStringExtra("total");
//            tvTotal.setText(total);
//        }
    }
}