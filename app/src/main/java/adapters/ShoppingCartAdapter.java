package adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import net.turndigital.carrefourdemo.AppController;
import net.turndigital.carrefourdemo.R;

import datamodels.Offer;

/**
 * Created by Shamyyoun on 2/8/2015.
 */
public class ShoppingCartAdapter extends RecyclerView.Adapter<ShoppingCartAdapter.ViewHolder> {
    private Context context;
    private Offer[] data;
    private int layoutResourceId;

    private OnItemClickListener onItemClickListener;

    public ShoppingCartAdapter(Context context, Offer[] data, int layoutResourceId) {
        this.context = context;
        this.data = data;
        this.layoutResourceId = layoutResourceId;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Offer offer = data[position];
        String strPrice = ("" + offer.getPriceBefore()).replace(".0", "");

        if (!offer.getImageUrl().isEmpty())
            Picasso.with(context).load(offer.getImageUrl()).error(R.drawable.product).into(holder.imageThumbnail);

        holder.textTitle.setText(offer.getName());
        holder.textPrice.setText(strPrice + " " + AppController.CURRENCY);
    }

    @Override
    public int getItemCount() {
        return data.length;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                layoutResourceId, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    public void setOnItemClickListener(
            final OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        protected ImageView imageThumbnail;
        protected TextView textTitle;
        protected TextView textPrice;

        public ViewHolder(View v) {
            super(v);
            imageThumbnail = (ImageView) v.findViewById(R.id.image_thumbnail);
            textTitle = (TextView) v.findViewById(R.id.text_title);
            textPrice = (TextView) v.findViewById(R.id.text_price);

            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(v, getPosition());
            }
        }
    }

}