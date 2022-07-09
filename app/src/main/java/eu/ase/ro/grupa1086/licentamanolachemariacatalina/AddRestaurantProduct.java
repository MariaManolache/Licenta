package eu.ase.ro.grupa1086.licentamanolachemariacatalina;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.time.Duration;

import de.hdodenhof.circleimageview.CircleImageView;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.Food;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.Order;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.Restaurant;

public class AddRestaurantProduct extends AppCompatActivity {

    private EditText addProductName;
    private EditText addProductPrice;
    private EditText addProductDescription;
    private EditText addProductTime;
    private CircleImageView addProductImage;
    private Button btnAddProduct;

    private FirebaseUser user;
    private DatabaseReference restaurants;

    private Uri restaurantImageUri;
    ActivityResultLauncher<Intent> someActivityResultLauncher;
    private StorageReference storageReference;
    private String productImage;

    ImageView close;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_restaurant_product);

        addProductName = findViewById(R.id.addProductName);
        addProductPrice = findViewById(R.id.addProductPrice);
        addProductDescription = findViewById(R.id.addProductDescription);
        addProductImage = findViewById(R.id.addProductImage);
        addProductTime = findViewById(R.id.addPreparationTime);
        btnAddProduct = findViewById(R.id.btnAddProduct);

        user = FirebaseAuth.getInstance().getCurrentUser();
        restaurants = FirebaseDatabase.getInstance().getReference("food").child(user.getUid());
        storageReference = FirebaseStorage.getInstance().getReference("foodImages");

        close = findViewById(R.id.closeActivity);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_nothing, R.anim.slide_down);
            }
        });

        btnAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadFile();
            }
        });


        addProductImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });


        someActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // There are no request codes
                            restaurantImageUri = result.getData().getData();
                            Picasso.with(getApplicationContext()).load(restaurantImageUri).into(addProductImage);
                        }
                    }
                });

    }


    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadFile() {
        if (restaurantImageUri != null) {
            String id = restaurants.push().getKey();
            StorageReference fileReference = storageReference.child(id + "." + getFileExtension(restaurantImageUri));
            fileReference.putFile(restaurantImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(getApplicationContext(), "Imaginea a fost încărcată", Toast.LENGTH_SHORT).show();

                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    productImage = uri.toString();

                                    String productName = addProductName.getText().toString();
                                    float productPrice = Float.parseFloat(addProductPrice.getText().toString());
                                    String productDescription = addProductDescription.getText().toString();
                                    int preparationTime = Integer.parseInt(addProductTime.getText().toString());
                                    Log.i("preparation", String.valueOf(preparationTime));

                                    if (TextUtils.isEmpty(productName)) {
                                        addProductName.setError("Numele este necesar pentru adăugarea produsului");
                                        return;
                                    }

                                    if (TextUtils.isEmpty(String.valueOf(productPrice))) {
                                        addProductPrice.setError("Prețul este necesar pentru adăugarea produsului");
                                        return;
                                    }

                                    if (TextUtils.isEmpty(productDescription)) {
                                        addProductDescription.setError("Descrierea este necesară pentru adăugarea produsului");
                                        return;
                                    }

                                    if (restaurantImageUri == null) {
                                        Toast.makeText(getApplicationContext(), "Trebuie selectată o imagine pentru produs", Toast.LENGTH_SHORT).show();
                                        return;
                                    }


                                    Food food = new Food(id, productName, productPrice, productDescription, 0, productImage, user.getUid(), preparationTime);
                                    restaurants.child(id).setValue(food).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(getApplicationContext(), "Produsul a fost adăugat", Toast.LENGTH_LONG).show();
                                            finish();
                                        }
                                    });
                                }
                            })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Imaginea nu a putut fi încărcată", Toast.LENGTH_SHORT).show();
                        }
                    });


        } else {
            Toast.makeText(getApplicationContext(), "Nicio imagine selectata", Toast.LENGTH_SHORT).show();
        }
    }


    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        someActivityResultLauncher.launch(intent);
    }

}