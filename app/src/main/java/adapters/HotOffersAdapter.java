package adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import net.tundigital.carrefourdemo.AppController;
import net.tundigital.carrefourdemo.R;

import datamodels.Offer;

public class HotOffersAdapter extends BaseAdapter {
    private Context context;
    private int layoutResoucreId;
    private Offer[] data;

    public HotOffersAdapter(Context context, int layoutResourceId, Offer[] data) {
        this.context = context;
        this.layoutResoucreId = layoutResourceId;
        this.data = data;
    }

    public View getView(int position, final View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View gridView;
        if (convertView == null) {
            // get view from layout
            gridView = inflater.inflate(layoutResoucreId, null);

            // init views
            ImageView imageThumbnail = (ImageView) gridView.findViewById(R.id.image_thumbnail);
            TextView textTitle = (TextView) gridView.findViewById(R.id.text_title);
            final ImageButton buttonAddToCart = (ImageButton) gridView.findViewById(R.id.button_addToCart);

            // set values
            final Offer offer = data[position];

            if (!offer.getImageUrl().isEmpty())
                Picasso.with(context).load(offer.getImageUrl()).error(R.drawable.product).into(imageThumbnail);

            textTitle.setText(offer.getName());

            // add listeners
            buttonAddToCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // change button icon
                    buttonAddToCart.setImageResource(R.drawable.ic_add_to_cart_blue);

                    // add offer to shopping cart in server
                    AppController.sendAddAction(context, offer.getId());
                    Toast.makeText(context, R.string.added_successfully, Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            gridView = convertView;
        }

        return gridView;
    }

    @Override
    public int getCount() {
        return data.length;
    }

    @Override
    public Object getItem(int position) {
        return data[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

}
