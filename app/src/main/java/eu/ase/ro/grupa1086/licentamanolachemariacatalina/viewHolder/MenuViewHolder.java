package eu.ase.ro.grupa1086.licentamanolachemariacatalina.viewHolder;

import android.content.ClipData;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import eu.ase.ro.grupa1086.licentamanolachemariacatalina.ItemClickListener;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.R;

public class MenuViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView menuName;
    public ImageView imageView;

    private ItemClickListener itemClickListener;

    public MenuViewHolder(@NonNull View itemView) {
        super(itemView);

        menuName = (TextView) itemView.findViewById(R.id.menuName);
        imageView = (ImageView) itemView.findViewById(R.id.menuImage);

        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
    itemClickListener.onClick(v, getAdapterPosition(), false);
    }
}
