package eu.ase.ro.grupa1086.licentamanolachemariacatalina.firebase;

import android.provider.ContactsContract;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.Category;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.Food;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.User;


public class FirebaseService {

    public static final String FOOD_REFERENCE="food";
    public static final String USER_REFERENCE="users";
    public static final String CATEGORY_REFERENCE="categories";

    private final DatabaseReference foodReference;
    private final DatabaseReference userReference;
    private final DatabaseReference categoryReference;

    private static FirebaseService firebaseService;

    private FirebaseService() {
        foodReference = FirebaseDatabase.getInstance().getReference(FOOD_REFERENCE);
        userReference = FirebaseDatabase.getInstance().getReference(USER_REFERENCE);
        categoryReference = FirebaseDatabase.getInstance().getReference(CATEGORY_REFERENCE);
    }

    public static FirebaseService getInstance() {
        if(firebaseService == null) {
            synchronized (FirebaseService.class) {
                if(firebaseService == null) {
                    firebaseService = new FirebaseService();
                }
            }
        }
        return firebaseService;
    }

    public void insertFood(Food food) {
        if(food==null || (food.getId() != null && !food.getId().trim().isEmpty())) {
            return;
        }

        String id = foodReference.push().getKey();
        food.setId(id);
        foodReference.child(food.getId()).setValue(food);
    }

    public void insertUser(User user) {
        if(user==null || (user.getId() != null && !user.getId().trim().isEmpty())) {
            return;
        }

        String id = userReference.push().getKey();
        user.setId(id);
        userReference.child(user.getId()).setValue(user);
    }

    public void insertCategory(Category category) {
        if(category==null || (category.getId() != null && !category.getId().trim().isEmpty())) {
            return;
        }

        String id = categoryReference.push().getKey();
        category.setId(id);
        userReference.child(category.getId()).setValue(category);
    }

    public void updateFood(Food food) {
        if(food==null || food.getId() == null && food.getId().trim().isEmpty()) {
            return;
        }
        foodReference.child(food.getId()).setValue(food);
    }

    public void updateUser(User user) {
        if(user==null || user.getId() == null && user.getId().trim().isEmpty()) {
            return;
        }
        userReference.child(user.getId()).setValue(user);
    }

    public void updateCategory(Category category) {
        if(category==null || category.getId() == null && category.getId().trim().isEmpty()) {
            return;
        }
        categoryReference.child(category.getId()).setValue(category);
    }

    public void deleteFood(Food food) {
        if(food==null || food.getId() == null && food.getId().trim().isEmpty()) {
            return;
        }
        foodReference.child(food.getId()).removeValue();
    }

    public void deleteUser(User user) {
        if(user==null || user.getId() == null && user.getId().trim().isEmpty()) {
            return;
        }
        userReference.child(user.getId()).removeValue();
    }

    public void deleteCategory(Category category) {
        if(category==null || category.getId() == null && category.getId().trim().isEmpty()) {
            return;
        }
        categoryReference.child(category.getId()).removeValue();
    }

    public void addFoodListener(Callback<List<Food>> callback) {
        foodReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Food> foodList = new ArrayList<>();
                for(DataSnapshot data: snapshot.getChildren()) {
                    Food food = data.getValue(Food.class);
                    if(food != null) {
                        foodList.add(food);
                    }
                }
                callback.runResultOnUI(foodList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FirebaseService", "Felul de mancare nu este disponibil");
            }
        });
    }

    public void addUserListener(Callback<List<User>> callback) {
        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<User> usersList = new ArrayList<>();
                for(DataSnapshot data: snapshot.getChildren()) {
                    User user = data.getValue(User.class);
                    if(user != null) {
                        usersList.add(user);
                    }
                }
                callback.runResultOnUI(usersList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FirebaseService", "Utilizatorul nu este disponibil");
            }
        });
    }

    public void addCategoryListener(Callback<List<Category>> callback) {
        categoryReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Category> categoriesList = new ArrayList<>();
                for(DataSnapshot data: snapshot.getChildren()) {
                    Category category  = data.getValue(Category.class);
                    if(category != null) {
                        categoriesList.add(category);
                    }
                }
                callback.runResultOnUI(categoriesList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FirebaseService", "Felul de mancare nu este disponibil");
            }
        });
    }

}
