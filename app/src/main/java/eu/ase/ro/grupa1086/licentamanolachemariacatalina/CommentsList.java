package eu.ase.ro.grupa1086.licentamanolachemariacatalina;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;

import eu.ase.ro.grupa1086.licentamanolachemariacatalina.cart.ItemClickListener;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.Address;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.viewHolder.AddressViewHolder;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.viewHolder.CommentViewHolder;

public class CommentsList extends AppCompatActivity {

    public RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;

    FirebaseRecyclerAdapter<Rating, CommentViewHolder> adapter;

    FirebaseDatabase database;
    DatabaseReference addresses;
    FirebaseUser user;
    String foodId;
    String userName;
    int count = 0;
    List<Rating> ratingList = new ArrayList<>();
    TextView noComments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments_list);

        if(getIntent() != null) {
            foodId = getIntent().getStringExtra("foodId");
            Log.i("foodId", foodId);
            userName = getIntent().getStringExtra("userName");
        }

        database = FirebaseDatabase.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        addresses = database.getReference("addresses").child(user.getUid());

        recyclerView = findViewById(R.id.recyclerComments);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        noComments = findViewById(R.id.noComments);
        if(ratingList.size() == 0) {
            noComments.setVisibility(View.VISIBLE);
        } else if(ratingList.size() != 0) {
            noComments.setVisibility(View.GONE);
        }

        loadComments();
    }

    private void loadComments() {

        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("food")
                .child(foodId)
                .child("ratings")
                .orderByChild("rateValue")
                .limitToLast(50);


        FirebaseRecyclerOptions<Rating> options =
                new FirebaseRecyclerOptions.Builder<Rating>()
                        .setQuery(query, Rating.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<Rating, CommentViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CommentViewHolder holder, int position, @NonNull Rating model) {
                holder.userName.setText(model.getName());
                holder.textComment.setText(model.getComment());
                ratingList.add(model);
                Log.i("rating", ratingList.toString());
                count++;

                final Rating local = model;
                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                        Toast.makeText(CommentsList.this, model.getComment(), Toast.LENGTH_LONG).show();

                    }
                });

                if(ratingList.size() == 0) {
                    noComments.setVisibility(View.VISIBLE);
                } else if(ratingList.size() != 0) {
                    noComments.setVisibility(View.GONE);
                }
            }

            @NonNull
            @Override
            public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.comment_layout, parent, false);
                view.setMinimumWidth(parent.getMeasuredWidth());

                return new CommentViewHolder(view);
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