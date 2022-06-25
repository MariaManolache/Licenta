package eu.ase.ro.grupa1086.licentamanolachemariacatalina.principalmenus;

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
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
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

import eu.ase.ro.grupa1086.licentamanolachemariacatalina.MainActivity;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.R;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.RestaurantProducts;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.account.DriverAccount;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.account.ResetPassword;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.account.SignIn;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.admin.NewRestaurantAccount;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.Category;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.Order;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.Restaurant;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.User;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.order.RestaurantOrders;

public class RestaurantAccount extends AppCompatActivity {
    //Button logout;
    BottomNavigationView bottomNavigationView;
    private static final int PICK_IMAGE_REQUEST = 1;
    private FloatingActionButton editRestaurantImage;
    private ImageView restaurantImage;
    private TextView restaurantName;
    private TextView restaurantPhoneNumber;
    private TextView restaurantAddress;
    private ImageView editRestaurantName;
    private ImageView editRestaurantPhoneNumber;
    private ImageView editRestaurantAddress;
    private Uri restaurantImageUri;
    ActivityResultLauncher<Intent> someActivityResultLauncher;

    private StorageReference storageReference;
    private DatabaseReference restaurants;
    private DatabaseReference orders;
    private DatabaseReference restaurantAccounts;
    private DatabaseReference categories;
    private DatabaseReference restaurantsByCategories;
    private FirebaseUser user;

    //private Spinner categorySpinner;

    AlertDialog.Builder resetName;
    AlertDialog.Builder resetPhoneNumber;
    AlertDialog.Builder resetEmail;
    AlertDialog.Builder resetAddress;
    AlertDialog.Builder changeCategory;
    LayoutInflater inflater;

    ArrayAdapter<String> departsAdapter;
    Map<String, String> mapCategoryId = new HashMap<>();
    List<String> listCategoryId = new ArrayList<>();

    String[] categoriesArray;

    TextView chooseCategory;
    boolean[] selectedCategory;

    List<String> oldCategories = new ArrayList<>();
    String oldCategoryId;

    List<String> chosenCategoriesList = new ArrayList<>();
    List<Integer> selectedCategoriesList = new ArrayList<>();
    List<String> chosenCategoriesIdList = new ArrayList<>();

    AlertDialog.Builder signOutAlert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_account);

        editRestaurantImage = findViewById(R.id.editRestaurantImage);
        restaurantImage = findViewById(R.id.restaurantImage);
        restaurantName = findViewById(R.id.restaurantName);
        restaurantPhoneNumber = findViewById(R.id.restaurantPhoneNumber);
        restaurantAddress = findViewById(R.id.restaurantAddress);
        editRestaurantName = findViewById(R.id.changeName);
        editRestaurantPhoneNumber = findViewById(R.id.changePhoneNumber);
        editRestaurantAddress = findViewById(R.id.changeRestaurantAddress);

        resetName = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogStyle));
        resetPhoneNumber = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogStyle));
        resetEmail = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogStyle));
        resetAddress = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogStyle));
        changeCategory = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogStyle));

        user = FirebaseAuth.getInstance().getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference("restaurantsImages");
        restaurants = FirebaseDatabase.getInstance().getReference("restaurants");
        orders = FirebaseDatabase.getInstance().getReference("orders");
        restaurantAccounts = FirebaseDatabase.getInstance().getReference("restaurantAccounts");
        categories = FirebaseDatabase.getInstance().getReference("categories");
        restaurantsByCategories = FirebaseDatabase.getInstance().getReference("restaurantsByCategories");

        chooseCategory = findViewById(R.id.chooseCategory);

        inflater = this.getLayoutInflater();

        signOutAlert = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogStyle));

        // categorySpinner = findViewById(R.id.categorySpinner);
//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.categoryType,
//                R.layout.spinner_layout);
//        categorySpinner.setAdapter(adapter);

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

                //departsAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_layout, listCategoryId);
//                departsAdapter.setDropDownViewResource(R.layout.spinner_layout);
                //categorySpinner.setAdapter(departsAdapter);

                restaurants.child(user.getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Restaurant restaurant = snapshot.getValue(Restaurant.class);
                        if (restaurant != null) {
                            selectedCategory = new boolean[listCategoryId.size()];
                            selectedCategoriesList = new ArrayList<>();

                            restaurantName.setText(restaurant.getName());
                            restaurantAddress.setText(restaurant.getAddress());
                            Picasso.with(getBaseContext()).load(restaurant.getImage()).placeholder(R.drawable.loading).into(restaurantImage);

//                            oldCategoryId = restaurant.getCategoryId();
                            String categoryName = "";
                            for (Map.Entry<String, String> entry : mapCategoryId.entrySet()) {
                                for (int i = 0; i < restaurant.getCategories().size(); i++) {
                                    if (restaurant.getCategories().get(i).equals(entry.getKey())) {
                                        categoryName += entry.getValue() + "; ";
                                        Log.i("categoryName", categoryName);
//                                        oldCategories = restaurant.getCategories();
                                        for (int j = 0; j < listCategoryId.size(); j++) {
                                            if (entry.getValue().equals(listCategoryId.get(j))) {
                                                selectedCategory[j] = true;
                                                selectedCategoriesList.add(j);
                                                Log.i("verificare2", String.valueOf(j));
                                            }
                                        }

                                        //int spinnerPosition = departsAdapter.getPosition(categoryName);
                                        // categorySpinner.setSelection(spinnerPosition);
                                    }
                                }
                            }

                            chooseCategory.setText(categoryName);


                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


                chooseCategory.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(RestaurantAccount.this, R.style.AlertDialogStyle2));
                        builder.setTitle("Alege categoriile");
                        builder.setCancelable(false);


                        builder.setMultiChoiceItems(categoriesArray, selectedCategory, new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                if (isChecked) {
                                    if (!selectedCategoriesList.contains(which)) {
                                    selectedCategoriesList.add(which);
                                    Collections.sort(selectedCategoriesList);

                                    chosenCategoriesList.add(categoriesArray[which]);

//                                    for (Map.Entry<String, String> entry : mapCategoryId.entrySet()) {
//                                        Log.i("verificare", entry.getKey());
//                                        if (categoriesArray[which].equals(entry.getValue())) {
//                                            Log.i("verificare", entry.getKey());
//                                            chosenCategoriesIdList.add(entry.getKey());
//                                            restaurantsByCategories.child(entry.getKey()).child(user.getUid()).removeValue();
//                                        }
//
//                                    }
                                    }
                                } else {
                                    if (selectedCategoriesList.contains(which)) {
                                    selectedCategoriesList.remove(Integer.valueOf(which));
                                    }

                                    for (Map.Entry<String, String> entry : mapCategoryId.entrySet()) {
                                        if (categoriesArray[which].equals(entry.getValue())) {
                                            chosenCategoriesIdList.add(entry.getKey());
                                            restaurantsByCategories.child(entry.getKey()).child(user.getUid()).removeValue();
                                        }

                                    }
                                }

                                chosenCategoriesIdList = new ArrayList<>();
                                chosenCategoriesList = new ArrayList<>();

                                for (Map.Entry<String, String> entry : mapCategoryId.entrySet()) {
                                    for (int i = 0; i < oldCategories.size(); i++) {
                                        Log.i("categories", oldCategories.get(i).toString());
                                        if (oldCategories.get(i).equals(entry.getValue())) {
                                            restaurantsByCategories.child(entry.getKey()).child(user.getUid()).removeValue();
                                        }
                                    }

                                }
                            }
                        });

                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

//                                for (Map.Entry<String, String> entry : mapCategoryId.entrySet()) {
//                                    for (int i = 0; i < oldCategories.size(); i++) {
//                                        Log.i("categories", oldCategories.get(i).toString());
//                                        if (oldCategories.get(i).equals(entry.getValue())) {
//                                            restaurantsByCategories.child(entry.getKey()).child(user.getUid()).removeValue();
//                                        }
//                                    }
//
//                                }


                                StringBuilder stringBuilder = new StringBuilder();

                                for (int j = 0; j < selectedCategoriesList.size(); j++) {
                                    chosenCategoriesList.add(categoriesArray[selectedCategoriesList.get(j)]);
                                    stringBuilder.append(categoriesArray[selectedCategoriesList.get(j)]);
                                    if (j != selectedCategoriesList.size() - 1) {
                                        stringBuilder.append(", ");
                                    }
                                }
                                chooseCategory.setText(stringBuilder.toString());

                                for (Map.Entry<String, String> entry : mapCategoryId.entrySet()) {
                                    for (int i = 0; i < chosenCategoriesList.size(); i++) {
                                        if (chosenCategoriesList.get(i).equals(entry.getValue())) {
                                            chosenCategoriesIdList.add(entry.getKey());
                                        }
                                    }

                                }

                                restaurants.child(user.getUid()).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        Restaurant restaurant1 = snapshot.getValue(Restaurant.class);
                                        if (restaurant1 != null) {
                                            restaurant1.setCategories(chosenCategoriesIdList);
                                            restaurant1.setCategoryId(chosenCategoriesIdList.get(0));

                                            restaurants.child(user.getUid()).setValue(restaurant1).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    restaurants.child(user.getUid()).child("categories").setValue(chosenCategoriesIdList);

                                                    for (int i = 0; i < chosenCategoriesIdList.size(); i++) {
                                                        restaurant1.setCategoryId(chosenCategoriesIdList.get(i));

                                                        restaurantsByCategories.child(chosenCategoriesIdList.get(i)).child(user.getUid()).setValue(restaurant1).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {

                                                            }
                                                        });
                                                    }
                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                            }
                        });

                        builder.setNegativeButton("Anulează", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

//                        builder.setNeutralButton("Debifează toate opțiunile", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                for (int j = 0; j < selectedCategory.length; j++) {
//                                    selectedCategory[j] = false;
//                                    selectedCategoriesList.clear();
//                                    chooseCategory.setText("");
//                                }
//                            }
//                        });

                        builder.show();

                    }
                });


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

//        alreadyDisplayed = false;
//        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                if (!alreadyDisplayed) {
//                    alreadyDisplayed = true;
//                } else {
//                    changeCategory.setTitle("Modificarea categoriei restaurantului")
//                            .setMessage("Sunteti sigur ca doriti sa realizati aceasta modificare?")
//                            .setPositiveButton("Confirmare", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//
//                                    restaurants.child(user.getUid()).addValueEventListener(new ValueEventListener() {
//                                        @Override
//                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                            Restaurant restaurant = snapshot.getValue(Restaurant.class);
//                                            String categoryName = (String) categorySpinner.getSelectedItem();
//                                            for (Map.Entry<String, String> entry : mapCategoryId.entrySet()) {
//                                                if (categoryName.equals(entry.getValue())) {
//                                                    String categoryId = entry.getKey();
//                                                    restaurant.setCategoryId(categoryId);
//                                                    restaurants.child(user.getUid()).setValue(restaurant);
//                                                }
//                                            }
//
//                                        }
//
//                                        @Override
//                                        public void onCancelled(@NonNull DatabaseError error) {
//
//                                        }
//                                    });
//
//                                    alreadyDisplayed = false;
//
//                                }
//                            }).setNegativeButton("Anulează", null)
//                            .create().show();
//                    alreadyDisplayed = false;
//                }
//
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });


        restaurantAccounts.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User restaurantUser = snapshot.getValue(User.class);
                if (restaurantUser != null) {
                    restaurantPhoneNumber.setText(restaurantUser.getPhoneNumber());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        editRestaurantName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = inflater.inflate(R.layout.reset_name_pop_up, null);
                resetName.setTitle("Modificarea denumirii restaurantului")
                        .setPositiveButton("Confirmare", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                EditText name = view.findViewById(R.id.etName);
                                if (name.getText().toString().isEmpty()) {
                                    name.setError("Câmpul este necesar pentru modificarea denumirii");
                                    return;
                                }

                                restaurants.child(user.getUid()).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        Restaurant restaurant = snapshot.getValue(Restaurant.class);
                                        restaurant.setName(name.getText().toString());
                                        restaurants.child(user.getUid()).setValue(restaurant);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                                restaurantAccounts.child(user.getUid()).child("name").setValue(name.getText().toString());

                            }
                        }).setNegativeButton("Anulează", null)
                        .setView(view)
                        .create().show();
            }

        });

        editRestaurantPhoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = inflater.inflate(R.layout.reset_phone_number_pop_up, null);
                resetPhoneNumber.setTitle("Modificarea numărului de telefon")
                        .setPositiveButton("Confirmare", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                EditText phoneNumber = view.findViewById(R.id.etPhoneNumber);
                                if (phoneNumber.getText().toString().isEmpty()) {
                                    phoneNumber.setError("Câmpul este necesar pentru modificarea numărului de telefon");
                                    return;
                                }

                                restaurantAccounts.child(user.getUid()).child("phoneNumber").setValue(phoneNumber.getText().toString());

                            }
                        }).setNegativeButton("Anulează", null)
                        .setView(view)
                        .create().show();
            }

        });

        editRestaurantAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = inflater.inflate(R.layout.reset_address_pop_up, null);
                resetPhoneNumber.setTitle("Modificarea adresei")
                        .setPositiveButton("Confirmare", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                EditText address = view.findViewById(R.id.etAddress);
                                if (address.getText().toString().isEmpty()) {
                                    address.setError("Câmpul este necesar pentru modificarea adresei");
                                    return;
                                }

                                restaurants.child(user.getUid()).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        Restaurant restaurant = snapshot.getValue(Restaurant.class);
                                        restaurant.setAddress(address.getText().toString());
                                        restaurants.child(user.getUid()).setValue(restaurant);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                                restaurantAccounts.child(user.getUid()).child("address").setValue(address.getText().toString());

                            }
                        }).setNegativeButton("Anuleaza", null)
                        .setView(view)
                        .create().show();
            }
        });

        editRestaurantImage.setOnClickListener(new View.OnClickListener() {
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
                            Picasso.with(getApplicationContext()).load(restaurantImageUri).placeholder(R.drawable.loading).into(restaurantImage);
                            uploadFile();
                        }
                    }
                });

//        logout = findViewById(R.id.btnLogout);
//        logout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FirebaseAuth.getInstance().signOut();
//                startActivity(new Intent(getApplicationContext(), MainActivity.class));
//                finish();
//            }
//        });

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.account);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if(id == R.id.account) {
                    return true;
                } else if(id == R.id.orders) {
                    startActivity(new Intent(getApplicationContext(), RestaurantOrders.class));
                    overridePendingTransition(R.anim.slide_right, R.anim.slide_left);
                    finish();
                    return true;
                } else if(id == R.id.products) {
                    startActivity(new Intent(getApplicationContext(), RestaurantProducts.class));
                    overridePendingTransition(R.anim.slide_right, R.anim.slide_left);
                    finish();
                    return true;
                }
//                switch (item.getItemId()) {
//                    case R.id.account:
//                        return true;
//                    case R.id.orders:
//                        startActivity(new Intent(getApplicationContext(), RestaurantOrders.class));
//                        overridePendingTransition(R.anim.slide_right, R.anim.slide_left);
//                        finish();
//                        return true;
//                    case R.id.products:
//                        startActivity(new Intent(getApplicationContext(), RestaurantProducts.class));
//                        overridePendingTransition(R.anim.slide_right, R.anim.slide_left);
//                        finish();
//                        return true;
//                }
                return false;
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
            StorageReference fileReference = storageReference.child(user.getUid() + "." + getFileExtension(restaurantImageUri));
            fileReference.putFile(restaurantImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(getApplicationContext(), "Imaginea a fost incarcata", Toast.LENGTH_LONG).show();
//                            restaurants.child(user.getUid()).addValueEventListener(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                    Restaurant restaurant = snapshot.getValue(Restaurant.class);
//                                    assert restaurant != null;
//                                    restaurant.setImage(String.valueOf(taskSnapshot.getStorage().getDownloadUrl().toString()));
//                                    restaurants.child(user.getUid()).setValue(restaurant);
//                                }
//
//                                @Override
//                                public void onCancelled(@NonNull DatabaseError error) {
//
//                                }
//                            });
                            //restaurants.child(user.getUid()).child("image").setValue(taskSnapshot.getStorage().getDownloadUrl().toString());
                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    restaurants.child(user.getUid()).child("image").setValue(uri.toString());
                                    orders.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                                Log.i("verificare", dataSnapshot.toString());
                                                String userId = dataSnapshot.getKey();

//                                                Log.i("verificare", order.toString());
//                                                for(int i = 0; i < order.getRestaurantAddress().size(); i++) {
//                                                    if(order.getRestaurantAddress().get(i).getId().equals(user.getUid())) {
//                                                        order.getRestaurantAddress().get(i).setImage(uri.toString());
//                                                        orders.child(order.getId()).child(String.valueOf(i)).child("image").setValue(uri.toString());
//                                                    }
//                                                }
//
//                                                orders.child(order.getId()).child("restaurants").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
//                                                    @Override
//                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                                        if(snapshot.exists()) {
//                                                            Restaurant restaurant = snapshot.getValue(Restaurant.class);
//                                                            restaurant.setImage(uri.toString());
//                                                            orders.child(order.getId()).child("restaurants").child(user.getUid()).child("image").setValue(uri.toString());
//                                                        }
//                                                    }
//
//                                                    @Override
//                                                    public void onCancelled(@NonNull DatabaseError error) {
//
//                                                    }
//                                                });


                                                assert userId != null;
                                                orders.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        for (DataSnapshot dataSnapshot2 : snapshot.getChildren()) {
                                                            Log.i("verificare", dataSnapshot2.toString());

                                                            String orderId = dataSnapshot2.getKey();
                                                            assert orderId != null;
                                                            orders.child(userId).child(orderId).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                    Order order = snapshot.getValue(Order.class);
                                                                    Log.i("verificare", order.toString());
                                                                    for (int i = 0; i < order.getRestaurantAddress().size(); i++) {
                                                                        if (order.getRestaurantAddress().get(i).getId().equals(user.getUid())) {
                                                                            order.getRestaurantAddress().get(i).setImage(uri.toString());
                                                                            orders.child(userId).child(orderId).child("restaurantAddress").setValue(order.getRestaurantAddress());
                                                                        }
                                                                    }

                                                                }

                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError error) {

                                                                }
                                                            });
                                                            orders.child(userId).child(orderId).child("restaurants").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                    if (snapshot.exists()) {
                                                                        Restaurant restaurant = snapshot.getValue(Restaurant.class);
                                                                        restaurant.setImage(uri.toString());
                                                                        orders.child(userId).child(orderId).child("restaurants").child(user.getUid()).setValue(restaurant);
                                                                    }
                                                                }

                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError error) {

                                                                }
                                                            });
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                    }
                                                });
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

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
                            Toast.makeText(getApplicationContext(), "Imaginea nu a putut fi incarcata", Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onStart() {
        super.onStart();
        //alreadyDisplayed = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        //alreadyDisplayed = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.reset_only_password, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.resetPassword) {
            Intent changePassword = new Intent(getApplicationContext(), ResetPassword.class);
            changePassword.putExtra("origin", "restaurant");
            startActivity(changePassword);
        }
        else if (item.getItemId() == R.id.logout) {
            signOutAlert.setTitle("Ieșire din cont")
                    .setMessage("Ești sigur că dorești să ieși din cont?")
                    .setPositiveButton("Da", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            FirebaseAuth.getInstance().signOut();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            finish();

                        }
                    }).setNegativeButton("Nu", null)
                    .create().show();
        }

        return false;
    }
}