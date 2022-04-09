package eu.ase.ro.grupa1086.licentamanolachemariacatalina.viewHolder;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import eu.ase.ro.grupa1086.licentamanolachemariacatalina.R;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.Food;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.Order;

class MyViewHolder extends RecyclerView.ViewHolder {

    public TextView restaurantName;
    public TextView foodName;
    public TextView foodPrice;
    public TextView foodQuantity;
    public TextView foodTotal;
    public ImageView foodImage;

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);

        restaurantName = itemView.findViewById(R.id.restaurantName);
        foodName = itemView.findViewById(R.id.foodName);
        foodPrice = itemView.findViewById(R.id.foodPrice);
        foodQuantity = itemView.findViewById(R.id.foodQuantity);
        foodTotal = itemView.findViewById(R.id.foodValue);
        foodImage = itemView.findViewById(R.id.foodImage);

    }
}

public class OrderDetailsAdapter extends RecyclerView.Adapter<MyViewHolder>{

    List<Food> foodList;

    public OrderDetailsAdapter(List<Food> foodList) {
        this.foodList = foodList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_detail_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Food food = foodList.get(position);
        holder.restaurantName.setText(String.format("Restaurant: %s", "test"));
        holder.foodName.setText(String.format("Produs: %s",food.getName()));
        holder.foodQuantity.setText(String.valueOf(food.getQuantity()));
        holder.foodPrice.setText(String.valueOf(food.getPrice()));
        holder.foodTotal.setText(String.valueOf(food.getPrice() * food.getQuantity()));
//        Picasso.with(getBaseContext()).load(food.getImage())
//                .into(holder.foodImage);

    }


    @Override
    public int getItemCount() {
        return foodList.size();
    }
}
