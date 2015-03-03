package net.tundigital.carrefourdemo;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

import datamodels.Constants;
import datamodels.Offer;
import json.JsonReader;
import utils.InternetUtil;
import views.BaseActivity;


public class OfferDetailsActivity extends BaseActivity {
    private Offer offer;
    private TextView textTitle;
    private ImageView imageImage;
    private View buttonAddOffer;
    private TextView textListPrice;
    private TextView textDiscount;
    private TextView textSavings;
    private TextView textDesc;

    /**
     * static method, used to open offer details activity with beautiful image transition animation
     */
    public static void launch(ActionBarActivity activity, View transitionView, Offer offer) {
        ActivityOptionsCompat options =
                ActivityOptionsCompat.makeSceneTransitionAnimation(activity, transitionView, Constants.TRANSITION_IMAGE);
        Intent intent = new Intent(activity, OfferDetailsActivity.class);
        intent.putExtra(Constants.KEY_OFFER, offer);
        ActivityCompat.startActivity(activity, intent, options.toBundle());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initComponents();
    }

    /**
     * method used to initialize components
     */
    private void initComponents() {
        // customize actionbar
        setActionBarIcon(R.drawable.ic_back);
        setActionBarIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        offer = (Offer) getIntent().getSerializableExtra(Constants.KEY_OFFER);

        textTitle = (TextView) findViewById(R.id.text_title);
        imageImage = (ImageView) findViewById(R.id.image_image);
        buttonAddOffer = findViewById(R.id.button_addOffer);
        textListPrice = (TextView) findViewById(R.id.text_listPrice);
        textDiscount = (TextView) findViewById(R.id.text_discount);
        textSavings = (TextView) findViewById(R.id.text_savings);
        textDesc = (TextView) findViewById(R.id.text_description);

        // set transition scene
        ViewCompat.setTransitionName(imageImage, Constants.TRANSITION_IMAGE);

        // calculate some additional values
        double savings = offer.getPriceBefore() - offer.getPriceAfter();
        int discount = (int) ((savings / offer.getPriceBefore()) * 100);
        String strListPrice = ("" + offer.getPriceBefore()).replace(".0", "");
        String strSavings = ("" + savings).replace(".0", "");

        // --set values--
        textTitle.setText(offer.getName());

        // load image
        if (offer.getImageUrl() != null || !"".equals(offer.getImageUrl()))
            Picasso.with(this).load(offer.getImageUrl()).error(R.drawable.product).into(imageImage);

        textListPrice.setText("$" + strListPrice);
        textDiscount.setText(discount + "%");
        textSavings.setText("$" + strSavings);
        textDesc.setText(offer.getDescription());

        buttonAddOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // check if offer in shopping cart
                if (offer.isInCart()) {
                    // show error msg
                    Toast.makeText(getApplicationContext(), R.string.item_already_added, Toast.LENGTH_LONG).show();
                } else {
                    // add offer to shopping cart in server
                    new UserOfferTask(offer);
                }
            }
        });

        // send offer view to server
        AppController.sendViewAction(getApplicationContext(), offer.getId());
    }

    /**
     * overriden method to get layout resource id
     */
    @Override
    protected int getLayoutResource() {
        return R.layout.activity_offer_details;
    }

    /**
     * sub class used to add offer to shopping cart in server
     */
    private class UserOfferTask extends AsyncTask<Void, Void, Void> {
        private Offer offer;
        private String response;

        private UserOfferTask(Offer offer) {
            this.offer = offer;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // check internet connection
            if (!InternetUtil.isConnected(getApplicationContext())) {
                Toast.makeText(getApplicationContext(), R.string.no_internet_connection, Toast.LENGTH_LONG).show();
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
            parameters.add(new BasicNameValuePair("user_id", AppController.getInstance(getApplicationContext()).activeUser.getId()));
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
                Toast.makeText(getApplicationContext(), R.string.unexpected_error_try_again, Toast.LENGTH_LONG).show();
                return;
            }

            if (response.equals(Constants.JSON_USER_OFFER_EXISTS)) {
                // show failure msg
                Toast.makeText(getApplicationContext(), R.string.item_already_added, Toast.LENGTH_LONG).show();
            } else if (response.equals(Constants.JSON_USER_OFFER_ADDED)) {
                // change state
                offer.setInCart(true);

                // show success msg
                Toast.makeText(getApplicationContext(), R.string.added_successfully, Toast.LENGTH_LONG).show();
            }
        }
    }
}
