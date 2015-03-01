package net.tundigital.carrefourdemo;

import android.content.Intent;
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

import datamodels.Constants;
import datamodels.Offer;
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
                // add offer to shopping cart in server
                AppController.sendAddAction(getApplicationContext(), offer.getId());
                Toast.makeText(OfferDetailsActivity.this, R.string.added_successfully, Toast.LENGTH_SHORT).show();
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
     * static method, used to open offer details activity with beautiful image transition animation
     */
    public static void launch(ActionBarActivity activity, View transitionView, Offer offer) {
        ActivityOptionsCompat options =
                ActivityOptionsCompat.makeSceneTransitionAnimation(activity, transitionView, Constants.TRANSITION_IMAGE);
        Intent intent = new Intent(activity, OfferDetailsActivity.class);
        intent.putExtra(Constants.KEY_OFFER, offer);
        ActivityCompat.startActivity(activity, intent, options.toBundle());
    }
}
