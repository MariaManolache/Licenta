package eu.ase.ro.grupa1086.licentamanolachemariacatalina.old;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import eu.ase.ro.grupa1086.licentamanolachemariacatalina.R;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.adapters.CategoryAdapter;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.Category;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.firebase.Callback;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.firebase.FirebaseService;

public class AddCategory extends AppCompatActivity {

    public static final String ADD_CATEGORY_LABEL = "addCategory";
    private TextInputEditText tietName;
    private TextInputEditText tietImage;
    private Button btnClear;
    private FloatingActionButton fabDelete;
    private FloatingActionButton fabSave;
    private ListView lvCategories;
    private List<Category> categoryList = new ArrayList<>();
    private FirebaseService firebaseService;
    private int indexSelectedCategory = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);

        initializareComponente();
        firebaseService = FirebaseService.getInstance();
        firebaseService.addCategoryListener(dataChangeCallback());
    }

    private Callback<List<Category>> dataChangeCallback() {
        return new Callback<List<Category>>() {
            @Override
            public void runResultOnUI(List<Category> rezultat) {
                categoryList.clear();
                categoryList.addAll(rezultat);
                notificareLVCategoryAdapter();
                clearText();
            }
        };
    }

    private void notificareLVCategoryAdapter() {
        CategoryAdapter adapter = (CategoryAdapter) lvCategories.getAdapter();
        adapter.notifyDataSetChanged();
    }

    private void initializareComponente() {
        tietName = findViewById(R.id.main_tiet_name);
        tietImage = findViewById(R.id.main_tiet_image);

        btnClear = findViewById(R.id.main_btn_clear);
        fabDelete = findViewById(R.id.main_fab_delete);
        fabSave = findViewById(R.id.main_fab_save);

        lvCategories = findViewById(R.id.main_lv_category);

        addListViewCategoryAdapter();
        btnClear.setOnClickListener(getClearClickEvent());
        fabSave.setOnClickListener(getSaveClickEvent());
        fabDelete.setOnClickListener(getDeleteClickEvent());
        lvCategories.setOnItemClickListener(getCategorySelectedClickEvent());
    }

    private AdapterView.OnItemClickListener getCategorySelectedClickEvent() {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                indexSelectedCategory=position;
                Category category = categoryList.get(position);
                tietName.setText(category.getName());
                tietImage.setText(String.valueOf(category.getImage()));
            }
        };
    }


    private View.OnClickListener getDeleteClickEvent() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(indexSelectedCategory != -1) {
                    firebaseService.deleteCategory(categoryList.get(indexSelectedCategory));
                }
            }
        };
    }

    private View.OnClickListener getSaveClickEvent() {
        return new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(isValid()) {
                    if(indexSelectedCategory==-1) {
                        Category category = updateCategoryFromView(null);
                        firebaseService.insertCategory(category);
                    }
                    else {
                        Category category = updateCategoryFromView(categoryList.get(indexSelectedCategory).getId());
                        firebaseService.updateCategory(category);
                    }
                }
            }
        };
    }

    private Category updateCategoryFromView(String id) {
        Category category = new Category();
        category.setId(id);
        category.setName(tietName.getText().toString());
        //category.setImage(tietImage.getText().toString());
        return category;
    }

    private boolean isValid() {
        if(tietName.getText() == null || tietName.getText().toString().trim().isEmpty() ||
                tietImage.getText() == null || tietImage.getText().toString().trim().isEmpty()) {
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
        tietImage.setText(null);
        indexSelectedCategory = -1;
    }

    private void addListViewCategoryAdapter() {
        CategoryAdapter adapter = new CategoryAdapter(getApplicationContext(),
                R.layout.lv_category_view, categoryList, getLayoutInflater());
        lvCategories.setAdapter(adapter);
    }
}