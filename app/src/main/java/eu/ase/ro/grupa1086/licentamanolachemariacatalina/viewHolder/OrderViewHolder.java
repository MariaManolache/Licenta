package eu.ase.ro.grupa1086.licentamanolachemariacatalina.viewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import eu.ase.ro.grupa1086.licentamanolachemariacatalina.R;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.cart.ItemClickListener;


public class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

    public TextView restaurantsName;
    public TextView orderStatus;
    public TextView orderAddress;
    public TextView orderDateAndTime;
    public TextView orderPriceTotal;
    public ImageView restaurantImage;
    public ImageView downArrow;
    public ImageView upArrow;

    private ItemClickListener itemClickListener;

    //private RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();

    public RecyclerView secondRecyclerView;
    public RecyclerView.LayoutManager secondLayoutManager;

    public OrderViewHolder(@NonNull View itemView) {
        super(itemView);

        restaurantsName = itemView.findViewById(R.id.restaurants);
        orderStatus = itemView.findViewById(R.id.orderStatus);
        orderAddress = itemView.findViewById(R.id.orderAddress);
        orderDateAndTime = itemView.findViewById(R.id.orderDateAndTime);
        orderPriceTotal = itemView.findViewById(R.id.orderPriceTotal);
        restaurantImage = itemView.findViewById(R.id.restaurantImage);
        downArrow = itemView.findViewById(R.id.downArrow);
        upArrow = itemView.findViewById(R.id.upArrow);

        secondRecyclerView = itemView.findViewById(R.id.orderDetails);
        //secondRecyclerView.setHasFixedSize(true);

        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v, getAbsoluteAdapterPosition(), false);
    }

    @Override
    public boolean onLongClick(View v) {
        itemClickListener.onClick(v, getAbsoluteAdapterPosition(), true);
        return true;
    }
}
