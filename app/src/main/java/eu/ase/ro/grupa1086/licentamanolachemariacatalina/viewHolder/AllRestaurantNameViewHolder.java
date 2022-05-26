package eu.ase.ro.grupa1086.licentamanolachemariacatalina.viewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import eu.ase.ro.grupa1086.licentamanolachemariacatalina.R;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.cart.ItemClickListener;

public class AllRestaurantNameViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView restaurantName;

    private ItemClickListener itemClickListener;

    public RecyclerView secondRecyclerView;

    public AllRestaurantNameViewHolder(@NonNull View itemView) {
        super(itemView);

        restaurantName = (TextView) itemView.findViewById(R.id.restaurantName);
        secondRecyclerView = itemView.findViewById(R.id.recyclerAllFood);

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
