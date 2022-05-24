package eu.ase.ro.grupa1086.licentamanolachemariacatalina;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

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

public class ProductInfo extends AppCompatActivity {
    private ImageView productImage;
    CollapsingToolbarLayout collapsingToolbarLayout;
    private TextView productName;
    private RatingBar ratingBar;
    private TextView productPrice;
    private TextView productDescription;

    String origin;
    String productId;

    FirebaseUser user;
    DatabaseReference food;


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

        user = FirebaseAuth.getInstance().getCurrentUser();
        food = FirebaseDatabase.getInstance().getReference("food").child(user.getUid());

        if (getIntent() != null && getIntent().getExtras() != null) {
            origin = getIntent().getExtras().getString("origin");
            if (origin != null && origin.equals("activityRestaurantProducts")) {
                productId = getIntent().getStringExtra("id");

                food.child(productId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Food food = snapshot.getValue(Food.class);

                        Picasso.with(getBaseContext()).load(food.getImage())
                                .into(productImage);
                        collapsingToolbarLayout.setTitle(food.getName());
                        productName.setText(food.getName());
                        productPrice.setText(food.getPrice() + " lei");
                        productDescription.setText(food.getDescription());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        }
    }
}