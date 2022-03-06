package eu.ase.ro.grupa1086.licentamanolachemariacatalina.adapters;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import eu.ase.ro.grupa1086.licentamanolachemariacatalina.R;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.Category;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.Food;

public class CategoryAdapter extends ArrayAdapter<Category> {

    private Context context;
    private int resource;
    private LayoutInflater inflater;
    private List<Category> categoriesList;


    public CategoryAdapter(@NonNull Context context, int resource, @NonNull List<Category> categoriesList,
                       LayoutInflater inflater) {
        super(context, resource, categoriesList);

        this.context = context;
        this.inflater = inflater;
        this.categoriesList = categoriesList;
        this.resource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = inflater.inflate(resource, parent, false);
        Category category = categoriesList.get(position);
        if(category!=null) {
            addName(view, category.getName());
            addImage(view, category.getImage());
        }
        return view;
    }

    private void addName(View view, String name) {
        TextView textView = view.findViewById(R.id.tv_category_name);
        populateContent(textView, name);
    }

    private void populateContent(TextView textView, String text) {
        if(text!=null && !text.isEmpty()) {
            textView.setText(text+" ");
        }
        else
            textView.setText(R.string.default_value);
    }

    private void addImage(View view, String image) {
        TextView textView = view.findViewById(R.id.tv_category_image);
        populateContent(textView, String.valueOf(image));
    }

}
