package eu.ase.ro.grupa1086.licentamanolachemariacatalina.admin;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

import eu.ase.ro.grupa1086.licentamanolachemariacatalina.R;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.Category;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.Food;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.Order;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.Restaurant;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.RestaurantAccount;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.User;

public class NewRestaurantAccount extends AppCompatActivity {

    private EditText etRestaurantName;
    private EditText etRestaurantEmail;
    private EditText etRestaurantPhoneNumber;
    private EditText etRestaurantAddress;
    private Button btnAddNewRestaurant;
    private DatabaseReference databaseReference;
    private DatabaseReference restaurants;
    private StorageReference storageReference;
    private DatabaseReference restaurantsByCategories;

    private Uri restaurantImageUri;
    ActivityResultLauncher<Intent> someActivityResultLauncher;

    private FirebaseAuth firebaseAuth;
    private ProgressBar progressBar;

    private String subject = "Vi s-a creat un cont de restaurant în aplicația Deliver It Right!";
    private String message = "";
    private String password;

    //private Spinner categorySpinner;
    private ImageView restaurantImage;
    ArrayAdapter<String> departsAdapter;
    private DatabaseReference categories;

    List<String> listCategoryId = new ArrayList<>();
    String[] categoriesArray;
    Map<String, String> mapCategoryId = new HashMap<>();
    String categoryId;
    private String restaurantImageString;

    List<Integer> selectedCategoriesList = new ArrayList<>();

    TextView chooseCategory;
    boolean[] selectedCategory;

    List<String> chosenCategoriesList = new ArrayList<>();
    List<String> chosenCategoriesIdList = new ArrayList<>();

    ImageView close;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_restaurant_account);

        initializeComponents();
    }

    private void initializeComponents() {

        close = findViewById(R.id.closeActivity);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_nothing, R.anim.slide_down);
            }
        });

        etRestaurantName = findViewById(R.id.etRestaurantName);
        etRestaurantEmail = findViewById(R.id.etRestaurantEmail);
        etRestaurantPhoneNumber = findViewById(R.id.etRestaurantPhoneNumber);
        etRestaurantAddress = findViewById(R.id.etRestaurantAddress);
        restaurantImage = findViewById(R.id.restaurantImage);
        //categorySpinner = findViewById(R.id.categorySpinner);
        categories = FirebaseDatabase.getInstance().getReference("categories");

        storageReference = FirebaseStorage.getInstance().getReference("restaurantsImages");
        restaurants = FirebaseDatabase.getInstance().getReference("restaurants");
        restaurantsByCategories = FirebaseDatabase.getInstance().getReference("restaurantsByCategories");
        restaurantImage = findViewById(R.id.restaurantImage);

        chooseCategory = findViewById(R.id.chooseCategory);


        categories.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mapCategoryId = new HashMap<>();
                listCategoryId = new ArrayList<>();
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    Category category = snap.getValue(Category.class);
                    Log.i("categoryName", category.toString());
                    mapCategoryId.put(category.getId(), category.getName());
                    listCategoryId.add(category.getName());

                }

                categoriesArray = new String[listCategoryId.size()];
                listCategoryId.toArray(categoriesArray);

                selectedCategory = new boolean[listCategoryId.size()];

                chooseCategory.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(NewRestaurantAccount.this, R.style.AlertDialogStyle2));
                        builder.setTitle("Alege categoriile");
                        builder.setCancelable(false);

                        builder.setMultiChoiceItems(categoriesArray, selectedCategory, new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                if (isChecked) {
                                    selectedCategoriesList.add(which);
                                    Collections.sort(selectedCategoriesList);
                                } else {
                                    selectedCategoriesList.remove(Integer.valueOf(which));
                                }
                            }
                        });

                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                StringBuilder stringBuilder = new StringBuilder();

                                for (int j = 0; j < selectedCategoriesList.size(); j++) {
                                    chosenCategoriesList.add(categoriesArray[selectedCategoriesList.get(j)]);
                                    stringBuilder.append(categoriesArray[selectedCategoriesList.get(j)]);
                                    if (j != selectedCategoriesList.size() - 1) {
                                        stringBuilder.append(", ");
                                    }
                                }
                                chooseCategory.setText(stringBuilder.toString());

                            }
                        });

                        builder.setNegativeButton("Anulează", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                        builder.setNeutralButton("Debifează toate opțiunile", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                for (int j = 0; j < selectedCategory.length; j++) {
                                    selectedCategory[j] = false;
                                    selectedCategoriesList.clear();
                                    chooseCategory.setText("");
                                }
                            }
                        });

                        builder.show();

                    }
                });

                departsAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_layout, listCategoryId);
//                departsAdapter.setDropDownViewResource(R.layout.spinner_layout);
                //categorySpinner.setAdapter(departsAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        btnAddNewRestaurant = findViewById(R.id.btnAddNewRestaurant);

        firebaseAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBar);

        restaurantImage.setOnClickListener(new View.OnClickListener() {
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
                            Picasso.with(getApplicationContext()).load(restaurantImageUri).into(restaurantImage);
                        }
                    }
                });


        btnAddNewRestaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = etRestaurantName.getText().toString().trim();
                final String email = etRestaurantEmail.getText().toString().trim();
                final String phoneNumber = "+40" + etRestaurantPhoneNumber.getText().toString().trim();
                final String address = etRestaurantAddress.getText().toString().trim();

                if (TextUtils.isEmpty(name)) {
                    etRestaurantName.setError("Numele restaurantului este necesar pentru crearea contului");
                    return;
                }

                if (TextUtils.isEmpty(email)) {
                    etRestaurantEmail.setError("Email-ul restaurantului este necesar pentru crearea contului");
                    return;
                }

                if (TextUtils.isEmpty(name)) {
                    etRestaurantPhoneNumber.setError("Numarul de telefon al restaurantului este necesar pentru crearea contului");
                    return;
                }

                if (TextUtils.isEmpty(address)) {
                    etRestaurantAddress.setError("Adresa restaurantului este necesară pentru crearea contului");
                    return;
                }

                if (restaurantImageUri == null) {
                    Toast.makeText(getApplicationContext(), "Trebuie selectată o imagine", Toast.LENGTH_SHORT).show();
                    return;
                }


                //String categoryName = (String) categorySpinner.getSelectedItem();
                for (Map.Entry<String, String> entry : mapCategoryId.entrySet()) {
                    for (int i = 0; i < chosenCategoriesList.size(); i++) {
                        if (chosenCategoriesList.get(i).equals(entry.getValue())) {
                            chosenCategoriesIdList.add(entry.getKey());
                        }
                    }

                }

                ThreadLocalRandom random = ThreadLocalRandom.current();
                int rand = random.nextInt(8, 12);
                password = PasswordGenerator.process(rand);

                progressBar.setVisibility(View.VISIBLE);

                firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String id = Objects.requireNonNull(task.getResult().getUser()).getUid();

                            if (restaurantImageUri != null) {
                                StorageReference fileReference = storageReference.child(id + "." + getFileExtension(restaurantImageUri));
                                fileReference.putFile(restaurantImageUri)
                                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                Toast.makeText(getApplicationContext(), "Imaginea a fost încărcată", Toast.LENGTH_LONG).show();

                                                fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        restaurantImageString = uri.toString();

                                                        databaseReference = FirebaseDatabase.getInstance().getReference("restaurantAccounts").child(id);


                                                        RestaurantAccount restaurantAccount = new RestaurantAccount(id, name, email, phoneNumber, address, password, 1);

                                                        Restaurant restaurant = new Restaurant(id, name, restaurantImageString, chosenCategoriesIdList.get(0), address);
                                                        restaurant.setCategories(chosenCategoriesIdList);

                                                        message = "Bine ai venit în echipa Deliver It Right, " + name + "!" + '\n' + '\n' + "Folosește următoarele date pentru a te conecta în aplicație:" + '\n' + '\n' +
                                                                "Adresa de email: " + email + '\n' + "Parola: " + password + '\n' + '\n' + "Mulțumim și sperăm să avem o colaborare cât mai bună!" + '\n' + "Echipa Deliver It Right!";

                                                        databaseReference.setValue(restaurantAccount).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {

                                                                    for(int i = 0; i < chosenCategoriesIdList.size(); i++) {
                                                                        Restaurant restaurantByCategory = new Restaurant(id, name, restaurantImageString, chosenCategoriesIdList.get(i), address);
                                                                        restaurantByCategory.setCategories(chosenCategoriesIdList);

                                                                        restaurantsByCategories.child(chosenCategoriesIdList.get(i)).child(id).setValue(restaurantByCategory);
                                                                    }

                                                                    restaurants.child(id).setValue(restaurant).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void unused) {
                                                                            restaurants.child(id).child("categories").setValue(chosenCategoriesIdList).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    Toast.makeText(NewRestaurantAccount.this, "Restaurant adăugat", Toast.LENGTH_SHORT).show();
                                                                                    Intent sendEmail = new Intent(Intent.ACTION_SEND);
                                                                                    sendEmail.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
                                                                                    sendEmail.putExtra(Intent.EXTRA_SUBJECT, subject);
                                                                                    sendEmail.putExtra(Intent.EXTRA_TEXT, message);

                                                                                    sendEmail.setType("message/rfc822");

                                                                                    startActivity(Intent.createChooser(sendEmail, "Alege email-ul:"));

                                                                                    finish();
                                                                                    overridePendingTransition(R.anim.slide_nothing, R.anim.slide_down);
                                                                                }
                                                                            });
                                                                        }
                                                                    });

                                                                } else {
                                                                    Toast.makeText(NewRestaurantAccount.this, "Eroare la crearea contului" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                                                }
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

                            } else {
                                Toast.makeText(getApplicationContext(), "Nicio imagine selectata", Toast.LENGTH_SHORT).show();
                            }

                            //uploadFile(id);


                        } else {
                            Toast.makeText(NewRestaurantAccount.this, "Eroare la crearea contului" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }


                    }
                });

            }
        });
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadFile(String id) {
        if (restaurantImageUri != null) {
            StorageReference fileReference = storageReference.child(id + "." + getFileExtension(restaurantImageUri));
            fileReference.putFile(restaurantImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(getApplicationContext(), "Imaginea a fost încărcată", Toast.LENGTH_LONG).show();

                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    restaurantImageString = uri.toString();


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