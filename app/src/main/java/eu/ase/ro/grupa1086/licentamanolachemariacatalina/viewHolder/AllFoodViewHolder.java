package eu.ase.ro.grupa1086.licentamanolachemariacatalina.viewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import eu.ase.ro.grupa1086.licentamanolachemariacatalina.R;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.cart.ItemClickListener;

public class AllFoodViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView foodName;
    public ImageView imageView;
    public TextView restaurantName;

    private ItemClickListener itemClickListener;

    public AllFoodViewHolder(@NonNull View itemView) {
        super(itemView);

        foodName = (TextView) itemView.findViewById(R.id.foodName);
        imageView = (ImageView) itemView.findViewById(R.id.foodImage);
        restaurantName = (TextView) itemView.findViewById(R.id.restaurantName);

        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v, getAdapterPosition(), false);
    }
}
