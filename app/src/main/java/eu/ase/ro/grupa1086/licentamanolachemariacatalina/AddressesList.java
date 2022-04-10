package eu.ase.ro.grupa1086.licentamanolachemariacatalina;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import eu.ase.ro.grupa1086.licentamanolachemariacatalina.cart.ItemClickListener;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.Address;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.Restaurant;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.food.FoodList;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.viewHolder.AddressViewHolder;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.viewHolder.RestaurantViewHolder;

public class AddressesList extends AppCompatActivity {

    public RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;

    FirebaseRecyclerAdapter<Address, AddressViewHolder> adapter;

    FirebaseDatabase database;
    DatabaseReference addresses;
    FirebaseUser user;

    String origin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addresses_list);

        if(getIntent() != null) {
            origin = getIntent().getStringExtra("origin");
        }

        database = FirebaseDatabase.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        addresses = database.getReference("addresses").child(user.getUid());

        recyclerView = findViewById(R.id.addressesList);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        
        loadAddresses();
    }

    private void loadAddresses() {

        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("addresses")
                .child(user.getUid())
                .child("addresses")
                .orderByChild("id")
                .limitToLast(50);


        FirebaseRecyclerOptions<Address> options =
                new FirebaseRecyclerOptions.Builder<Address>()
                        .setQuery(query, Address.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<Address, AddressViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull AddressViewHolder holder, int position, @NonNull Address model) {
                int nextPosition = position + 1;
                holder.addressName.setText("Adresa#" + nextPosition);
                holder.addressDetails.setText(model.getMapsAddress());

                final Address local = model;
                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                        Toast.makeText(AddressesList.this, model.getMapsAddress(), Toast.LENGTH_LONG).show();

                        if(origin.equals("placeOrder")) {
                            Intent placeOrder = new Intent(AddressesList.this, PlaceOrder.class);
                            placeOrder.putExtra("origin", "savedAddresses");
                            placeOrder.putExtra("addressId", model.getId());
                            startActivity(placeOrder);
                            finish();
                        }
                    }
                });
            }

            @NonNull
            @Override
            public AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.address_layout, parent, false);
                view.setMinimumWidth(parent.getMeasuredWidth());

                return new AddressViewHolder(view);
            }
        };

        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }
}