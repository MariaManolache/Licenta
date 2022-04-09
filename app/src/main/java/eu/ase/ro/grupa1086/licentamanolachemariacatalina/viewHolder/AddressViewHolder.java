package eu.ase.ro.grupa1086.licentamanolachemariacatalina.viewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import eu.ase.ro.grupa1086.licentamanolachemariacatalina.R;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.cart.ItemClickListener;

public class AddressViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView addressName;
    public TextView addressDetails;

    private ItemClickListener itemClickListener;

    public AddressViewHolder(@NonNull View itemView) {
        super(itemView);

        addressName = itemView.findViewById(R.id.addressName);
        addressDetails = itemView.findViewById(R.id.addressDetails);

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
