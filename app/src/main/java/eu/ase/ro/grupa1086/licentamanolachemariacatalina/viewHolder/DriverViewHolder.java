package eu.ase.ro.grupa1086.licentamanolachemariacatalina.viewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import eu.ase.ro.grupa1086.licentamanolachemariacatalina.R;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.cart.ItemClickListener;

public class DriverViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

    public TextView driverName;
    public TextView driverEmail;
    public TextView driverPhoneNumber;
    public  TextView driverOrderNumbers;

    public ImageView downArrow;
    public ImageView upArrow;
    private ItemClickListener itemClickListener;

    public RecyclerView secondRecyclerView;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public DriverViewHolder(@NonNull View itemView) {
        super(itemView);

        driverName = (TextView) itemView.findViewById(R.id.driverName);
        driverEmail = (TextView) itemView.findViewById(R.id.driverEmail);
        driverPhoneNumber = (TextView) itemView.findViewById(R.id.driverPhoneNumber);
        driverOrderNumbers = (TextView) itemView.findViewById(R.id.driverNumberOfOrders);
        downArrow = itemView.findViewById(R.id.downArrow);
        upArrow = itemView.findViewById(R.id.upArrow);

        secondRecyclerView = itemView.findViewById(R.id.orderDetails);
        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
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
