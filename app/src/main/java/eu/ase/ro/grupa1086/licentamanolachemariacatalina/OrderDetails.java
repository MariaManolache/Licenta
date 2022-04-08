package eu.ase.ro.grupa1086.licentamanolachemariacatalina;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.Food;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.Status;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.viewHolder.OrderDetailsAdapter;

public class OrderDetails extends AppCompatActivity {

    TextView restaurantName;
    TextView orderStatus;
    TextView orderAddress;
    TextView orderTotal;
    ImageView restaurantImage;

    String orderId;
    String restaurantNameString;
    String restaurantImageString;
    String status;
    String total;
    String address;

    DatabaseReference cart;
    FirebaseDatabase database;
    FirebaseUser user;

    List<Food> foods = new ArrayList<Food>();

    RecyclerView foodList;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        restaurantName = findViewById(R.id.restaurantName);
        orderStatus = findViewById(R.id.orderStatus);
        orderAddress = findViewById(R.id.orderAddress);
        orderTotal = findViewById(R.id.orderPriceTotal);
        restaurantImage = findViewById(R.id.restaurantImage);

        foodList = findViewById(R.id.orderDetails);
        foodList.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        foodList.setLayoutManager(layoutManager);

        if(getIntent() != null) {
            orderId = getIntent().getStringExtra("orderId");
            restaurantNameString = getIntent().getStringExtra("restaurantName");
            restaurantImageString = getIntent().getStringExtra("restaurantImage");
            address = getIntent().getStringExtra("address");
            status = getIntent().getStringExtra("orderStatus");
            total = getIntent().getStringExtra("total");
        }

        database = FirebaseDatabase.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        cart = database.getReference("orders").child(user.getUid()).child(orderId).child("cart");

        restaurantName.setText(restaurantNameString);
        //restaurantImage.setImageURI(Uri.parse(restaurantImageString));
        orderStatus.setText(status);
        orderTotal.setText(total);
        orderAddress.setText(address);

        Picasso.with(getBaseContext()).load(restaurantImageString)
                .into(restaurantImage);

        cart.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Food food = dataSnapshot.getValue(Food.class);
                    Log.i("foods", food.toString());
                    foods.add(food);
                }

                OrderDetailsAdapter adapter = new OrderDetailsAdapter(foods);
                adapter.notifyDataSetChanged();
                foodList.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}