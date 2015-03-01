package net.tundigital.carrefourdemo;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.devspark.appmsg.AppMsg;

import java.util.ArrayList;

import adapters.CategoryOffersAdapter;
import datamodels.Constants;
import datamodels.Offer;
import json.JsonParser;
import json.OffersHandler;
import utils.InternetUtil;
import views.SwipeProgressFragment;

/**
 * Created by Shamyyoun on 2/24/2015.
 */
public class CategoryOffersFragment extends SwipeProgressFragment {
    public static final String TAG = "category";

    // main objects
    private String categoryId;
    private MainActivity activity;
    private GridView gridOffers;

    private ArrayList<AsyncTask> runningTasks; // used to hold running tasks

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        initComponents(rootView);

        return rootView;
    }

    /**
     * overriden abstract method, used to set content layout resource
     */
    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_category_offers;
    }

    /**
     * method used to initialize components
     */
    private void initComponents(View rootView) {
        categoryId = getArguments().getString(Constants.KEY_CATEGORY_ID);
        activity = (MainActivity) getActivity();
        gridOffers = (GridView) rootView.findViewById(R.id.grid_offers);

        runningTasks = new ArrayList<>();

        // load category offers
        new CategoryOffersTask().execute();
    }

    /**
     * overriden method, used to refresh content
     */
    @Override
    protected void onRefresh() {
        new CategoryOffersTask().execute();
    }

    /*
     * overriden method
     */
    @Override
    public void onDestroy() {
        // cancel all appmsgs
        AppMsg.cancelAll(activity);

        // stop all running tasks
        if (runningTasks != null) {
            for (AsyncTask task : runningTasks) {
                task.cancel(true);
            }
        }

        super.onDestroy();
    }

    /**
     * sub class used to load category offers
     */
    private class CategoryOffersTask extends AsyncTask<Void, Void, Void> {
        private String response;

        private CategoryOffersTask() {
            // save reference to this task, to destroy it if required
            runningTasks.add(this);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // check internet connection
            if (!InternetUtil.isConnected(activity)) {
                // show error
                showError(R.string.no_internet_connection);

                cancel(true);
                return;
            }

            showProgress();
        }

        @Override
        protected Void doInBackground(Void... params) {
            // create json parser
            String url = AppController.END_POINT + "/offers-listing/" + categoryId;
            JsonParser jsonParser = new JsonParser(url);

            // execute request
            response = jsonParser.sendPostRequest();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            // validate response
            if (response == null) {
                // show error msg
                showError(R.string.unexpected_error_try_again);

                return;
            }

            // ---response is valid---
            // handle it in offers array
            OffersHandler offersHandler = new OffersHandler(response);
            final Offer[] offers = offersHandler.handle();

            // check handling operation result
            if (offers == null) {
                // show error msg
                showError(R.string.unexpected_error_try_again);

                return;
            }

            // update offers grid adapter
            CategoryOffersAdapter adapter = new CategoryOffersAdapter(activity.getApplicationContext(), R.layout.grid_category_offers_item, offers);
            gridOffers.setAdapter(adapter);
            gridOffers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    OfferDetailsActivity.launch(activity, view.findViewById(R.id.image_thumbnail), offers[position]);
                }
            });

            showMain();
        }
    }
}
