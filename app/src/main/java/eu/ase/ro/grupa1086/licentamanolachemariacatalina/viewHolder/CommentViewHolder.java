package eu.ase.ro.grupa1086.licentamanolachemariacatalina.viewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import eu.ase.ro.grupa1086.licentamanolachemariacatalina.R;
import eu.ase.ro.grupa1086.licentamanolachemariacatalina.cart.ItemClickListener;


public class CommentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView userName;
    public TextView textComment;

    private ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public CommentViewHolder(@NonNull View itemView) {
        super(itemView);

        userName = (TextView) itemView.findViewById(R.id.userName);
        textComment = (TextView) itemView.findViewById(R.id.textComment);

        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v, getAbsoluteAdapterPosition(), false);
    }
}
