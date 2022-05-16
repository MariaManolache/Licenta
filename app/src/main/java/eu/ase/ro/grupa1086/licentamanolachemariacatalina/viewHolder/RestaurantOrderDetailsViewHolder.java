package eu.ase.ro.grupa1086.licentamanolachemariacatalina.viewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import eu.ase.ro.grupa1086.licentamanolachemariacatalina.R;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.cart.ItemClickListener;

public class RestaurantOrderDetailsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView foodName;
    public TextView foodPrice;
    public TextView foodQuantity;
    public TextView foodTotal;
    public ImageView foodImage;

    private ItemClickListener itemClickListener;


    public RestaurantOrderDetailsViewHolder(@NonNull View itemView) {
        super(itemView);

        foodName = itemView.findViewById(R.id.foodName);
        foodPrice = itemView.findViewById(R.id.foodPrice);
        foodQuantity = itemView.findViewById(R.id.foodQuantity);
        foodTotal = itemView.findViewById(R.id.foodValue);
        foodImage = itemView.findViewById(R.id.foodImage);

        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v, getAbsoluteAdapterPosition(), false);
    }
}
