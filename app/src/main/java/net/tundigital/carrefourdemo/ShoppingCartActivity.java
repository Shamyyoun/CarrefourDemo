package net.tundigital.carrefourdemo;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.devspark.appmsg.AppMsg;

import java.util.ArrayList;

import adapters.ShoppingCartAdapter;
import datamodels.Offer;
import json.JsonReader;
import json.OffersHandler;
import utils.InternetUtil;
import views.SwipeProgressActivity;


public class ShoppingCartActivity extends SwipeProgressActivity {
    private RecyclerView recyclerShoppingCart;

    private ArrayList<AsyncTask> runningTasks; // used to hold running tasks

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

        recyclerShoppingCart = (RecyclerView) findViewById(R.id.recycler_shoppingCart);
        recyclerShoppingCart.setLayoutManager(new LinearLayoutManager(this));

        runningTasks = new ArrayList<>();

        // load shopping cart
        new ShoppingCartTask().execute();
    }

    /**
     * overriden method to get layout resource id
     */
    @Override
    protected int getLayoutResource() {
        return R.layout.activity_shopping_cart;
    }

    /**
     * overriden method used to refresh view
     */
    @Override
    protected void onRefresh() {
        new ShoppingCartTask().execute();
    }

    /**
     * overriden method
     */
    @Override
    protected void onResume() {
        super.onResume();

        // reload shopping offers from server, to sync offers if user add from OfferDetailsActivity
        new ShoppingCartTask().execute();
    }

    /**
     * overriden method
     */
    @Override
    protected void onDestroy() {
        // cancel all appmsgs
        AppMsg.cancelAll(this);

        // stop all running tasks
        if (runningTasks != null) {
            for (AsyncTask task : runningTasks) {
                task.cancel(true);
            }
        }

        super.onDestroy();
    }

    /**
     * sub class used to load shopping cart offers
     */
    private class ShoppingCartTask extends AsyncTask<Void, Void, Void> {
        private String response;

        private ShoppingCartTask() {
            // save reference to this task, to destroy it if required
            runningTasks.add(this);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // check internet connection
            if (!InternetUtil.isConnected(ShoppingCartActivity.this)) {
                // show error
                showError(R.string.no_internet_connection);

                cancel(true);
                return;
            }

            // show progress
            showProgress();
        }

        @Override
        protected Void doInBackground(Void... params) {
            // create json parser
            String url = AppController.END_POINT + "/user-offers/"
                    + AppController.getInstance(getApplicationContext()).activeUser.getId();
            JsonReader jsonReader = new JsonReader(url);

            // execute request
            response = jsonReader.sendPostRequest();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            // validate response
            if (response == null) {
                // show error
                showError(R.string.unexpected_error_try_again);

                return;
            }

            // --response is valid--
            // handle it
            OffersHandler handler = new OffersHandler(response);
            final Offer[] offers = handler.handle();

            // check handling operation result
            if (offers == null) {
                // show error
                showError(R.string.unexpected_error_try_again);

                return;
            }

            // display shopping cart offers
            ShoppingCartAdapter adapter = new ShoppingCartAdapter(getApplicationContext(),
                    offers, R.layout.recycler_shopping_cart_item);
            recyclerShoppingCart.setAdapter(adapter);
            adapter.setOnItemClickListener(new ShoppingCartAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    OfferDetailsActivity.launch(ShoppingCartActivity.this,
                            view.findViewById(R.id.image_thumbnail), offers[position]);
                }
            });

            // show main view
            showMain();
        }
    }
}
