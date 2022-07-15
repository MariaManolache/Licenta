package eu.ase.ro.grupa1086.licentamanolachemariacatalina.old;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import eu.ase.ro.grupa1086.licentamanolachemariacatalina.R;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.adapters.FoodAdapter;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.Food;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.firebase.Callback;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.firebase.FirebaseService;

public class AddFood extends AppCompatActivity {

    public static final String ADD_FOOD_LABEL = "addFood";
    private TextInputEditText tietName;
    private TextInputEditText tietPrice;
    private TextInputEditText tietQuantity;
    private Button btnClear;
    private FloatingActionButton fabDelete;
    private FloatingActionButton fabSave;
    private ListView lvFood;
    private List<Food> foodList = new ArrayList<>();
    private FirebaseService firebaseService;
    private int indexSelectedFood = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food);

        initializareComponente();
        firebaseService = FirebaseService.getInstance();
        firebaseService.addFoodListener(dataChangeCallback());

        final Intent intent = getIntent();

    }

    private Callback<List<Food>> dataChangeCallback() {
        return new Callback<List<Food>>() {
            @Override
            public void runResultOnUI(List<Food> rezultat) {
                foodList.clear();
                foodList.addAll(rezultat);
                notificareLVFoodAdapter();
                clearText();
            }
        };
    }

    private void notificareLVFoodAdapter() {
        FoodAdapter adapter = (FoodAdapter) lvFood.getAdapter();
        adapter.notifyDataSetChanged();
    }

    private void initializareComponente() {
        tietName = findViewById(R.id.main_tiet_name);
        tietPrice = findViewById(R.id.main_tiet_price);
        tietQuantity = findViewById(R.id.main_tiet_quantity);

        btnClear = findViewById(R.id.main_btn_clear);
        fabDelete = findViewById(R.id.main_fab_delete);
        fabSave = findViewById(R.id.main_fab_save);

        lvFood = findViewById(R.id.main_lv_food);

        addListViewFoodAdapter();
        btnClear.setOnClickListener(getClearClickEvent());
        fabSave.setOnClickListener(getSaveClickEvent());
        fabDelete.setOnClickListener(getDeleteClickEvent());
        lvFood.setOnItemClickListener(getFoodSelectedClickEvent());
    }

    private AdapterView.OnItemClickListener getFoodSelectedClickEvent() {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                indexSelectedFood=position;
                Food food = foodList.get(position);
                tietName.setText(food.getName());
                tietPrice.setText(String.valueOf(food.getPrice()));
                tietQuantity.setText(String.valueOf(food.getQuantity()));
            }
        };
    }


    private View.OnClickListener getDeleteClickEvent() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(indexSelectedFood != -1) {
                    firebaseService.deleteFood(foodList.get(indexSelectedFood));
                }
            }
        };
    }

    private View.OnClickListener getSaveClickEvent() {
        return new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(isValid()) {
                    if(indexSelectedFood==-1) {
                        Food food = updateFoodFromView(null);
                        firebaseService.insertFood(food);
                    }
                    else {
                        Food food = updateFoodFromView(foodList.get(indexSelectedFood).getId());
                        firebaseService.updateFood(food);
                    }
                }
            }
        };
    }

    private Food updateFoodFromView(String id) {
        Food food = new Food();
        food.setId(id);
        food.setName(tietName.getText().toString());
        food.setPrice(Float.parseFloat(tietPrice.getText().toString()));
        food.setQuantity(Integer.parseInt(tietQuantity.getText().toString()));
        return food;
    }

    private boolean isValid() {
        if(tietName.getText() == null || tietName.getText().toString().trim().isEmpty() ||
                tietPrice.getText() == null || tietPrice.getText().toString().trim().isEmpty() ||
                tietQuantity.getText() == null || tietQuantity.getText().toString().trim().isEmpty()) {
            Toast.makeText(getApplicationContext(),"Validarea nu a trecut",
                    Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private View.OnClickListener getClearClickEvent() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearText();
            }
        };
    }

    private void clearText() {
        tietName.setText(null);
        tietPrice.setText(null);
        tietQuantity.setText(null);
        indexSelectedFood = -1;
    }

    private void addListViewFoodAdapter() {
        FoodAdapter adapter = new FoodAdapter(getApplicationContext(),
                R.layout.lv_row_view, foodList, getLayoutInflater());
        lvFood.setAdapter(adapter);
    }


}
