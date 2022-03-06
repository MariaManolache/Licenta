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

    public TextView tvCartName, tvCartPrice;
    public ImageView imgCartCount;

    private ItemClickListener itemClickListener;

    public void setTvCartName(TextView tvCartName) {
        this.tvCartName = tvCartName;
    }

    public CartViewHolder(@NonNull View itemView) {
        super(itemView);
        tvCartName = (TextView) itemView.findViewById(R.id.cartItemName);
        tvCartPrice = (TextView) itemView.findViewById(R.id.cartItemPrice);
        imgCartCount = (ImageView) itemView.findViewById(R.id.cartItemCount);

        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v, getAdapterPosition(), false);

    }
}
