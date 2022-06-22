package eu.ase.ro.grupa1086.licentamanolachemariacatalina.viewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import eu.ase.ro.grupa1086.licentamanolachemariacatalina.R;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.cart.ItemClickListener;

public class RestaurantFoodViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    public TextView foodName;
    public ImageView imageView;
    public FloatingActionButton fabRemoveProduct;

    private ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public RestaurantFoodViewHolder(@NonNull View itemView) {
        super(itemView);

        foodName = (TextView) itemView.findViewById(R.id.foodName);
        imageView = (ImageView) itemView.findViewById(R.id.foodImage);
        fabRemoveProduct = (FloatingActionButton) itemView.findViewById(R.id.fabRemoveProduct);

        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v, getAbsoluteAdapterPosition(), false);
    }
}
