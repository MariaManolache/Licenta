package eu.ase.ro.grupa1086.licentamanolachemariacatalina.viewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import eu.ase.ro.grupa1086.licentamanolachemariacatalina.R;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.cart.ItemClickListener;

public class RestaurantAccountViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    public TextView name;
    public TextView email;
    public TextView address;
    public TextView phoneNumber;
    public TextView restaurantOrdersNumber;
    public ImageView downArrow;
    public ImageView upArrow;

    private ItemClickListener itemClickListener;
    public RecyclerView secondRecyclerView;

    public RestaurantAccountViewHolder(@NonNull View itemView) {
        super(itemView);

        name = itemView.findViewById(R.id.restaurantName);
        email = itemView.findViewById(R.id.restaurantEmail);
        address = itemView.findViewById(R.id.restaurantAddress);
        phoneNumber = itemView.findViewById(R.id.restaurantPhoneNumber);
        restaurantOrdersNumber = itemView.findViewById(R.id.restaurantOrdersNumber);
        downArrow = itemView.findViewById(R.id.downArrow);
        upArrow = itemView.findViewById(R.id.upArrow);

        secondRecyclerView = itemView.findViewById(R.id.restaurantAccountOrders);

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
