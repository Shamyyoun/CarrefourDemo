package adapters;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import net.turndigital.carrefourdemo.AppController;
import net.turndigital.carrefourdemo.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

import datamodels.Constants;
import datamodels.Offer;
import json.JsonReader;
import utils.InternetUtil;

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

            if (offer.isInCart()) {
                buttonAddToCart.setImageResource(R.drawable.ic_add_to_cart_blue);
            }

            // add listeners
            buttonAddToCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // check offer in shopping cart
                    if (!offer.isInCart()) {
                        // add offer to shopping cart in server
                        new UserOfferTask(offer, buttonAddToCart).execute();
                    } else {
                        // show error msg
                        Toast.makeText(context, R.string.item_already_added, Toast.LENGTH_LONG).show();
                    }
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

    private class UserOfferTask extends AsyncTask<Void, Void, Void> {
        private Offer offer;
        private ImageButton buttonAddToCart;
        private String response;

        private UserOfferTask(Offer offer, ImageButton buttonAddToCart) {
            this.offer = offer;
            this.buttonAddToCart = buttonAddToCart;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // check internet connection
            if (!InternetUtil.isConnected(context)) {
                Toast.makeText(context, R.string.no_internet_connection, Toast.LENGTH_LONG).show();
                cancel(true);

                return;
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            // prepare url
            String url = AppController.END_POINT + "/add-action";

            // create json reader
            JsonReader jsonReader = new JsonReader(url);
            List<NameValuePair> parameters = new ArrayList<>();
            parameters.add(new BasicNameValuePair("user_id", AppController.getInstance(context).activeUser.getId()));
            parameters.add(new BasicNameValuePair("offer_id", offer.getId()));
            parameters.add(new BasicNameValuePair("behavior_id", Constants.BEHAVIOUR_ADD));

            // execute request
            response = jsonReader.sendPostRequest(parameters);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            // check response
            if (response == null) {
                Toast.makeText(context, R.string.unexpected_error_try_again, Toast.LENGTH_LONG).show();
                return;
            }

            if (response.equals(Constants.JSON_USER_OFFER_EXISTS)) {
                // show failure msg
                Toast.makeText(context, R.string.item_already_added, Toast.LENGTH_LONG).show();
            } else if (response.equals(Constants.JSON_USER_OFFER_ADDED)) {
                // show success msg
                Toast.makeText(context, R.string.added_successfully, Toast.LENGTH_LONG).show();
            }

            // change state
            offer.setInCart(true);
            buttonAddToCart.setImageResource(R.drawable.ic_add_to_cart_blue);
        }
    }
}