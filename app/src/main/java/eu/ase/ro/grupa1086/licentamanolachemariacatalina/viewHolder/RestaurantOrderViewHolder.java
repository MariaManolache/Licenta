package eu.ase.ro.grupa1086.licentamanolachemariacatalina.viewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import eu.ase.ro.grupa1086.licentamanolachemariacatalina.R;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.cart.ItemClickListener;

public class RestaurantOrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView restaurantsName;
    public TextView orderStatus;
    public TextView orderAddress;
    public TextView orderPriceTotal;
    public ImageView restaurantImage;
    public ImageView downArrow;
    public ImageView upArrow;

    private ItemClickListener itemClickListener;
    public RecyclerView secondRecyclerView;


    public RestaurantOrderViewHolder(@NonNull View itemView) {
        super(itemView);

        restaurantsName = itemView.findViewById(R.id.restaurants);
        orderStatus = itemView.findViewById(R.id.orderStatus);
        orderAddress = itemView.findViewById(R.id.orderAddress);
        orderPriceTotal = itemView.findViewById(R.id.orderPriceTotal);
        restaurantImage = itemView.findViewById(R.id.restaurantImage);
        downArrow = itemView.findViewById(R.id.downArrow);
        upArrow = itemView.findViewById(R.id.upArrow);

        secondRecyclerView = itemView.findViewById(R.id.orderDetails);

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
