package eu.ase.ro.grupa1086.licentamanolachemariacatalina.admin;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import eu.ase.ro.grupa1086.licentamanolachemariacatalina.R;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.Category;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.Restaurant;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.RestaurantAccount;

public class NewCategory extends AppCompatActivity {

    private EditText etCategoryName;
    private ImageView categoryImage;
    private DatabaseReference databaseReference;
    private DatabaseReference categories;
    private StorageReference storageReference;

    private Uri categoryImageUri;
    ActivityResultLauncher<Intent> someActivityResultLauncher;

    private FirebaseAuth firebaseAuth;
    private ProgressBar progressBar;
    private String categoryImageString;
    private Button btnAddNewCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_category);

        initializeComponents();

    }

    private void initializeComponents() {


        etCategoryName = findViewById(R.id.etCategoryName);
        categoryImage = findViewById(R.id.categoryImage);

        categories = FirebaseDatabase.getInstance().getReference("categories");
        storageReference = FirebaseStorage.getInstance().getReference("categoryImages");
        btnAddNewCategory = findViewById(R.id.btnAddNewCategory);

        firebaseAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBar);

        categoryImage.setOnClickListener(new View.OnClickListener() {
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
                            categoryImageUri = result.getData().getData();
                            Picasso.with(getApplicationContext()).load(categoryImageUri).into(categoryImage);
                        }
                    }
                });

        btnAddNewCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = etCategoryName.getText().toString().trim();

                progressBar.setVisibility(View.VISIBLE);

                if (TextUtils.isEmpty(name)) {
                    etCategoryName.setError("Numele categoriei este necesar pentru crearea contului");
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                if (categoryImageUri == null) {
                    Toast.makeText(getApplicationContext(), "Trebuie selectată o imagine", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                String id = categories.push().getKey();

                if (categoryImageUri != null) {
                    StorageReference fileReference = storageReference.child(id + "." + getFileExtension(categoryImageUri));
                    fileReference.putFile(categoryImageUri)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Toast.makeText(getApplicationContext(), "Imaginea a fost încărcată", Toast.LENGTH_LONG).show();

                                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            categoryImageString = uri.toString();

                                            Category category = new Category(id, name, categoryImageString);


                                            categories.child(id).setValue(category).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    finish();
                                                }
                                            });
                                        }
                                    })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(getApplicationContext(), "Imaginea nu a putut fi incarcata", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            });

                }
            }
        });

    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        someActivityResultLauncher.launch(intent);
    }
}