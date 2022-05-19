package eu.ase.ro.grupa1086.licentamanolachemariacatalina.viewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.GlideContext;
import com.bumptech.glide.load.model.GlideUrl;

import eu.ase.ro.grupa1086.licentamanolachemariacatalina.cart.ItemClickListener;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.R;

public class RestaurantViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView restaurantName;
    public ImageView imageView;
    public TextView deliveryTime;

    private ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

//    public void setIcon(String url) {
//        Glide.with(this.itemView).load(url).into(imageView);
//    }

    public RestaurantViewHolder(@NonNull View itemView) {
        super(itemView);

        restaurantName = (TextView) itemView.findViewById(R.id.restaurantName);
        imageView = (ImageView) itemView.findViewById(R.id.restaurantImage);
        deliveryTime = (TextView) itemView.findViewById(R.id.deliveryTime);

        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v, getAbsoluteAdapterPosition(), false);
    }
}
