package eu.ase.ro.grupa1086.licentamanolachemariacatalina.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import eu.ase.ro.grupa1086.licentamanolachemariacatalina.R;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.Food;

public class FoodAdapter extends ArrayAdapter<Food> {

    private Context context;
    private int resource;
    private LayoutInflater inflater;
    private List<Food> foodList;


    public FoodAdapter(@NonNull Context context, int resource, @NonNull List<Food> foodList,
                       LayoutInflater inflater) {
        super(context, resource, foodList);

        this.context = context;
        this.inflater = inflater;
        this.foodList = foodList;
        this.resource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = inflater.inflate(resource, parent, false);
        Food food = foodList.get(position);
        if(food!=null) {
            addName(view, food.getName());
            addPrice(view, food.getPrice());
            addQuantity(view, food.getQuantity());
        }
        return view;
    }

    private void addName(View view, String name) {
        TextView textView = view.findViewById(R.id.tv_row_name);
        populateContent(textView, name);
    }

    private void populateContent(TextView textView, String text) {
        if(text!=null && !text.isEmpty()) {
            textView.setText(text+" ");
        }
        else
            textView.setText(R.string.default_value);
    }

    private void addPrice(View view, float price) {
        TextView textView = view.findViewById(R.id.tv_row_price);
        populateContent(textView, String.valueOf(price));
    }

    private void addQuantity(View view, int quantity) {
        TextView textView = view.findViewById(R.id.tv_row_quantity);
        populateContent(textView, String.valueOf(quantity));
    }
}
