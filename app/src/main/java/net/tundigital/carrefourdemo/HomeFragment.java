package net.tundigital.carrefourdemo;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.devspark.appmsg.AppMsg;
import com.pascalwelsch.holocircularprogressbar.HoloCircularProgressBar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import adapters.HotOffersAdapter;
import adapters.InstantOffersAdapter;
import database.OfferDAO;
import datamodels.Offer;
import json.JsonParser;
import json.OffersHandler;
import utils.InternetUtil;
import views.ExpandableHeightGridView;
import views.ExpandableHeightListView;

/**
 * Created by Shamyyoun on 2/24/2015.
 */
public class HomeFragment extends Fragment {
    public static final String TAG = "home";

    // main view objects
    private MainActivity activity;

    // hot offer objects
    private ExpandableHeightGridView gridHotOffers;

    // instant offers objects
    private ExpandableHeightListView listInstantOffers;
    private InstantOffersAdapter instantOffersAdapter;
    private List<Offer> instantOffers;

    private ArrayList<AsyncTask> runningTasks; // used to hold running tasks
    // instant offers timer objects
    private Handler timerHandler;
    private Runnable timerRunnable;
    private boolean timerRunning;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // save reference in app controller
        AppController.getInstance(getActivity().getApplicationContext()).homeFragment = this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        initComponents(rootView, savedInstanceState);

        return rootView;
    }

    /**
     * method used to initialize components
     */
    private void initComponents(final View rootView, Bundle savedInstanceState) {
        activity = (MainActivity) getActivity();

        listInstantOffers = (ExpandableHeightListView) rootView.findViewById(R.id.list_instantOffers);
        gridHotOffers = (ExpandableHeightGridView) rootView.findViewById(R.id.grid_hotOffers);

        runningTasks = new ArrayList<>();

        // customize components
        listInstantOffers.setExpanded(true);
        gridHotOffers.setExpanded(true);

        // load instant offers
        final OfferDAO offerDAO = new OfferDAO(activity.getApplicationContext());
        offerDAO.open();
        instantOffers = offerDAO.getAll();
        offerDAO.close();

        // set list adapter
        instantOffersAdapter = new InstantOffersAdapter(activity, R.layout.list_instant_offers_item, instantOffers);
        listInstantOffers.setAdapter(instantOffersAdapter);

        // check offers size
        if (instantOffers.size() > 0) {
            startInstantOffersTimer();
        }

        // load hot offers
        new HotOffersTask().execute();
    }

    /**
     * method, used to start instant offers timers
     */
    private void startInstantOffersTimer() {
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < instantOffers.size(); i++) {
                    // calculate values
                    Offer offer = instantOffers.get(i);
                    long[] remainTime = offer.getRemainTime();
                    long createTime = offer.getCreateDate().getTime();
                    long expireTime = offer.getExpireDate().getTime();
                    long currentTime = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
                    float progress = ((float) (currentTime - createTime) / (expireTime - createTime));

                    // get row view
                    final View rowView = listInstantOffers.getChildAt(i - listInstantOffers.getFirstVisiblePosition());
                    if (rowView == null)
                        return;

                    // check values
                    if (expireTime > currentTime) {
                        // get views
                        TextView textRemainHours = (TextView) rowView.findViewById(R.id.text_remainHours);
                        TextView textRemainMinutes = (TextView) rowView.findViewById(R.id.text_remainMinutes);
                        TextView textRemainSeconds = (TextView) rowView.findViewById(R.id.text_remainSeconds);
                        HoloCircularProgressBar progressBar = (HoloCircularProgressBar) rowView.findViewById(R.id.holoCircularProgressBar);

                        // ---update values---
                        textRemainHours.setText((remainTime[2] < 10) ? "0" + remainTime[2] : "" + remainTime[2]);
                        textRemainMinutes.setText((remainTime[1] < 10) ? "0" + remainTime[1] : "" + remainTime[1]);
                        textRemainSeconds.setText((remainTime[0] < 10) ? "0" + remainTime[0] : "" + remainTime[0]);

                        progressBar.setProgress(progress);
                    } else {
                        // remove offer from database
                        OfferDAO offerDAO = new OfferDAO(activity.getApplicationContext());
                        offerDAO.open();
                        offerDAO.delete(offer.getId());
                        offerDAO.close();
                    }
                }

                timerHandler.postDelayed(this, 1000);
            }
        };
        timerHandler = new Handler();
        timerHandler.postDelayed(timerRunnable, 1000);
        timerRunning = true;
    }

    /**
     * public method, used to add offer to instant offers when arrived from push notifications
     */
    public void addInstantOffer(Offer offer) {
        instantOffers.add(0, offer);
        instantOffersAdapter.notifyDataSetChanged();

        // check to start timer if required
        if (!timerRunning)
            startInstantOffersTimer();
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

        // stop instant offers handlers
        if (timerHandler != null && timerRunnable != null)
            timerHandler.removeCallbacks(timerRunnable);

        // remove reference in app controller
        AppController.getInstance(getActivity().getApplicationContext()).homeFragment = null;

        super.onDestroy();
    }

    /**
     * sub class used to load hot offers
     */
    private class HotOffersTask extends AsyncTask<Void, Void, Void> {
        private ProgressDialog progressDialog;
        private String response;

        private HotOffersTask() {
            progressDialog = new ProgressDialog(activity);
            progressDialog.setMessage("Please wait...");
            progressDialog.setCancelable(false);

            // save reference to this task, to destroy it if required
            runningTasks.add(this);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // check internet connection
            if (!InternetUtil.isConnected(activity)) {
                // show error
                AppMsg.makeText(activity, R.string.no_internet_connection, AppMsg.STYLE_ALERT).show();

                cancel(true);
                return;
            }

            // show progress
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            // create json parser
            String url = AppController.END_POINT + "/offers-listing";
            JsonParser jsonParser = new JsonParser(url);

            // execute request
            response = jsonParser.sendPostRequest();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            progressDialog.dismiss();

            // validate response
            if (response == null) {
                // show error
                AppMsg.makeText(activity, R.string.unexpected_error_try_again, AppMsg.STYLE_ALERT).show();

                return;
            }

            // --response is valid--
            // handle it
            OffersHandler handler = new OffersHandler(response);
            final Offer[] offers = handler.handle();

            // check handling operation result
            if (offers == null) {
                // show error
                AppMsg.makeText(activity, R.string.unexpected_error_try_again, AppMsg.STYLE_ALERT).show();

                return;
            }

            // display hot offers
            HotOffersAdapter adapter = new HotOffersAdapter(activity.getApplicationContext(), R.layout.grid_hot_offers_item, offers);
            gridHotOffers.setAdapter(adapter);
            gridHotOffers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    OfferDetailsActivity.launch(activity, view.findViewById(R.id.image_thumbnail), offers[position]);
                }
            });
        }
    }
}