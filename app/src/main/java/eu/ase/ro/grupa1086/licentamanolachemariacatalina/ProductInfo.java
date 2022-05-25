package eu.ase.ro.grupa1086.licentamanolachemariacatalina;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.Food;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.Restaurant;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.rating.CommentsList;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.rating.Rating;

public class ProductInfo extends AppCompatActivity {
    private ImageView productImage;
    CollapsingToolbarLayout collapsingToolbarLayout;
    private TextView productName;
    private RatingBar ratingBar;
    private TextView productPrice;
    private TextView productDescription;

    private ImageView editProductName;
    private ImageView editProductPrice;
    private ImageView editProductDescription;

    private TextView commentsList;
    float ratingValue = 0.0f;
    int nbOfRatings = 0;

    AlertDialog.Builder resetName;
    AlertDialog.Builder resetPhoneNumber;
    AlertDialog.Builder resetEmail;
    LayoutInflater inflater;

    String origin;
    String productId;

    FirebaseUser user;
    DatabaseReference foodItem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_info);

        productImage = findViewById(R.id.productImage);
        collapsingToolbarLayout = findViewById(R.id.collapsing);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExtendedAppbar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppbar);
        productName = findViewById(R.id.productName);
        productPrice = findViewById(R.id.productPrice);
        productDescription = findViewById(R.id.productDescription);
        commentsList = findViewById(R.id.comments);
        ratingBar = findViewById(R.id.ratingBar);

        editProductName = findViewById(R.id.editProductName);
        editProductPrice = findViewById(R.id.editProductPrice);
        editProductDescription = findViewById(R.id.editProductDescription);

        inflater = this.getLayoutInflater();

        resetName = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogStyle));
        resetPhoneNumber = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogStyle));
        resetEmail = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogStyle));

        user = FirebaseAuth.getInstance().getCurrentUser();
        foodItem = FirebaseDatabase.getInstance().getReference("food").child(user.getUid());

        if (getIntent() != null && getIntent().getExtras() != null) {
            origin = getIntent().getExtras().getString("origin");
            if (origin != null && origin.equals("activityRestaurantProducts")) {
                productId = getIntent().getStringExtra("id");

                foodItem.child(productId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Food food = snapshot.getValue(Food.class);

                        Picasso.with(getBaseContext()).load(food.getImage())
                                .into(productImage);
                        collapsingToolbarLayout.setTitle(food.getName());
                        productName.setText(food.getName());
                        productPrice.setText(String.valueOf(food.getPrice()));
                        productDescription.setText(food.getDescription());

                        ratingValue = 0.0f;
                        nbOfRatings = 0;
                        foodItem.child(food.getId()).child("ratings").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    Rating rating = dataSnapshot.getValue(Rating.class);
                                    ratingValue += rating.getRateValue();
                                    nbOfRatings++;
                                }

                                if (nbOfRatings >= 1) {
                                    ratingValue /= nbOfRatings;
                                }

                                ratingBar.setRating(ratingValue);

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });


                        editProductName.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                View view = inflater.inflate(R.layout.reset_name_pop_up, null);
                                EditText name = view.findViewById(R.id.etName);
                                name.setText(food.getName());
                                resetName.setTitle("Modificarea denumirii produsului")
                                        .setPositiveButton("Confirmare", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                if (name.getText().toString().isEmpty()) {
                                                    name.setError("Campul este necesar pentru modificarea denumirii");
                                                    return;
                                                }

                                                if(!(name.getText().toString().equals(food.getName()))) {
                                                    food.setName(name.getText().toString());
                                                    foodItem.child(productId).child("name").setValue(food.getName()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                            Toast.makeText(getApplicationContext(), "Denumirea produsului a fost modificata", Toast.LENGTH_SHORT).show();
                                                            productName.setText(name.getText().toString());
                                                            collapsingToolbarLayout.setTitle(name.getText().toString());
                                                        }
                                                    });
                                                }

                                            }
                                        }).setNegativeButton("Anuleaza", null)
                                        .setView(view)
                                        .create().show();

                            }
                        });


                        editProductPrice.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                View view = inflater.inflate(R.layout.reset_price_pop_up, null);
                                EditText price = view.findViewById(R.id.etPrice);
                                price.setText(String.valueOf(food.getPrice()));
                                resetName.setTitle("Modificarea pretului produsului")
                                        .setPositiveButton("Confirmare", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                if (price.getText().toString().isEmpty()) {
                                                    price.setError("Campul este necesar pentru modificarea pretului");
                                                    return;
                                                }

                                                if(!(price.getText().toString().equals(String.valueOf(food.getPrice())))) {
                                                    food.setPrice(Float.parseFloat(price.getText().toString()));
                                                    foodItem.child(productId).child("price").setValue(food.getPrice()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                            Toast.makeText(getApplicationContext(), "Pretul produsului a fost modificat", Toast.LENGTH_SHORT).show();
                                                            productPrice.setText(String.valueOf(Float.parseFloat(price.getText().toString())));
                                                        }
                                                    });
                                                }

                                            }
                                        }).setNegativeButton("Anuleaza", null)
                                        .setView(view)
                                        .create().show();

                            }
                        });


                        editProductDescription.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                View view = inflater.inflate(R.layout.reset_description_pop_up, null);
                                EditText description = view.findViewById(R.id.etDescription);
                                description.setText(food.getDescription());
                                resetName.setTitle("Modificarea descrierii produsului")
                                        .setPositiveButton("Confirmare", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                if (description.getText().toString().isEmpty()) {
                                                    description.setError("Campul este necesar pentru modificarea descrierii");
                                                    return;
                                                }

                                                if(!(description.getText().toString().equals(food.getDescription()))) {
                                                    food.setDescription(description.getText().toString());
                                                    foodItem.child(productId).child("description").setValue(food.getDescription()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                            Toast.makeText(getApplicationContext(), "Descrierea produsului a fost modificata", Toast.LENGTH_SHORT).show();
                                                            productDescription.setText(description.getText().toString());
                                                        }
                                                    });
                                                }

                                            }
                                        }).setNegativeButton("Anuleaza", null)
                                        .setView(view)
                                        .create().show();

                            }
                        });

                        commentsList.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent comments = new Intent(getApplicationContext(), CommentsList.class);
                                comments.putExtra("origin", "productInfo");
                                comments.putExtra("foodId", food.getId());
                                comments.putExtra("restaurantId", user.getUid());
                                startActivity(comments);
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        }

    }
}