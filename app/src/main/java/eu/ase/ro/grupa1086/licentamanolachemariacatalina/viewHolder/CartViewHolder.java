package eu.ase.ro.grupa1086.licentamanolachemariacatalina.viewHolder;

import android.media.Image;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import eu.ase.ro.grupa1086.licentamanolachemariacatalina.ItemClickListener;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.R;

public class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView tvCartName;
    public TextView tvCartPrice;
    public TextView tvCartQuantity;
    public TextView tvCartValue;
    public ImageView imgCartCount;

    private ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void setTvCartName(TextView tvCartName) {
        this.tvCartName = tvCartName;
    }

    public CartViewHolder(@NonNull View itemView) {
        super(itemView);
        tvCartName = (TextView) itemView.findViewById(R.id.cartItemName);
        tvCartPrice = (TextView) itemView.findViewById(R.id.cartItemPrice);
        tvCartQuantity = (TextView) itemView.findViewById(R.id.cartItemQuantity);
        tvCartValue = (TextView) itemView.findViewById(R.id.cartItemValue);
        imgCartCount = (ImageView) itemView.findViewById(R.id.cartItemCount);

        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v, getAdapterPosition(), false);

    }
}
