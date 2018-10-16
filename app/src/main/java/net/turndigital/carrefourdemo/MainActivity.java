package net.turndigital.carrefourdemo;

import android.content.Intent;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.devspark.appmsg.AppMsg;
import com.melnykov.fab.FloatingActionButton;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

import adapters.CategoriesAdapter;
import datamodels.Category;
import datamodels.Constants;
import json.CategoriesHandler;
import json.JsonReader;
import views.BaseActivity;
import views.ExpandableHeightListView;


public class MainActivity extends BaseActivity implements View.OnClickListener {
    public static final int MENU_DRAWER_GRAVITY = Gravity.START;
    public static final int REVEAL_DURATION = 340;

    private DrawerLayout menuDrawer;
    private ImageView imageProfile;
    private TextView textName;
    private TextView textHome;
    private ExpandableHeightListView listCategories;
    private TextView textAbout;
    private TextView textLogout;

    private FloatingActionButton fabShoppingCart;

    private ArrayList<AsyncTask> runningTasks; // used to hold running tasks
    private boolean flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initComponents();
    }

    /**
     * method used to initialize components
     */
    private void initComponents() {
        menuDrawer = (DrawerLayout) findViewById(R.id.menuDrawer);
        imageProfile = (ImageView) findViewById(R.id.image_profile);
        textName = (TextView) findViewById(R.id.text_name);
        textHome = (TextView) findViewById(R.id.text_home);
        listCategories = (ExpandableHeightListView) findViewById(R.id.list_categories);
        textAbout = (TextView) findViewById(R.id.text_about);
        textLogout = (TextView) findViewById(R.id.text_logout);

        fabShoppingCart = (FloatingActionButton) findViewById(R.id.fab_shoppingCart);
        ;
        runningTasks = new ArrayList<>();

        // customize menu drawer
        menuDrawer.setDrawerShadow(R.drawable.drawer_shadow, MENU_DRAWER_GRAVITY);
        setActionBarIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (menuDrawer.isDrawerOpen(MENU_DRAWER_GRAVITY))
                    menuDrawer.closeDrawer(MENU_DRAWER_GRAVITY);
                else
                    menuDrawer.openDrawer(MENU_DRAWER_GRAVITY);
            }
        });
        setActionBarLogoClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onHome();
            }
        });

        // set initial user info in menu drawer
        textName.setText(AppController.getInstance(getApplicationContext()).activeUser.getName());

        // load categories
        new CategoriesTask().execute();

        // add listeners
        fabShoppingCart.setOnClickListener(this);
        textHome.setOnClickListener(this);
        textAbout.setOnClickListener(this);
        textLogout.setOnClickListener(this);
        listCategories.setExpanded(true);

        // load home fragment as default fragment
        onHome();
    }

    /**
     * overriden method to get layout resource id
     */
    @Override
    protected int getLayoutResource() {
        return R.layout.activity_main;
    }

    /**
     * overriden method to do on click actions
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.text_home:
                onHome();
                break;

            case R.id.text_logout:
                onLogout();
                break;

            case R.id.fab_shoppingCart:
                onShoppingCart();
                break;
        }
    }

    /**
     * method used to load home fragment
     */
    private void onHome() {
        // hide menu drawer
        menuDrawer.closeDrawer(MENU_DRAWER_GRAVITY);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.container_home, new HomeFragment(), HomeFragment.TAG);
        ft.commit();
    }

    /**
     * method used to logout
     */
    private void onLogout() {
        // remove reg_id on server
        String url = AppController.END_POINT + "/update-gen";
        JsonReader jsonReader = new JsonReader(url);
        List<NameValuePair> paramaters = new ArrayList<>(2);
        paramaters.add(new BasicNameValuePair("user_id", AppController.getInstance(getApplicationContext()).activeUser.getId()));
        paramaters.add(new BasicNameValuePair("reg_id", ""));
        jsonReader.sendPostRequest(paramaters);

        // clear cached user response
        AppController.removeActiveUserResponse(this);
        // remove current active user
        AppController.getInstance(getApplicationContext()).activeUser = null;

        // goto login activity
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * method used to open shopping cart activity
     */
    private void onShoppingCart() {
        Intent intent = new Intent(this, ShoppingCartActivity.class);
        startActivity(intent);
    }

    private Point getLocationInView(View src, View target) {
        final int[] l1 = new int[2];
        src.getLocationOnScreen(l1);

        final int[] l2 = new int[2];
        target.getLocationOnScreen(l2);

        l2[0] = l2[0] - l1[0] + target.getWidth() / 2;
        l2[1] = l2[1] - l1[1] + target.getHeight() / 2;

        return new Point(l2[0], l2[1]);
    }

    /*
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
     * sub class used to load categories
     */
    private class CategoriesTask extends AsyncTask<Void, Void, Void> {
        private String response;

        private CategoriesTask() {
            // save reference to this task, to destroy it if required
            runningTasks.add(this);
        }

        @Override
        protected Void doInBackground(Void... params) {
            // create json parser
            String url = AppController.END_POINT + "/categories-listing";
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
                return;
            }

            // --response is valid--
            // handle it
            CategoriesHandler handler = new CategoriesHandler(response);
            final Category[] categories = handler.handle();

            // check handling operation result
            if (categories == null) {
                return;
            }

            // display categories in menu drawer list
            CategoriesAdapter adapter = new CategoriesAdapter(getApplicationContext(), R.layout.list_categories_item, categories);
            listCategories.setAdapter(adapter);
            listCategories.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // hide menu drawer
                    menuDrawer.closeDrawer(MENU_DRAWER_GRAVITY);

                    Bundle bundle = new Bundle();
                    bundle.putString(Constants.KEY_CATEGORY_ID, categories[position].getId());
                    CategoryOffersFragment fragment = new CategoryOffersFragment();
                    fragment.setArguments(bundle);

                    // show category fragment
                    FragmentManager fm = getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.replace(R.id.container_home, fragment);
                    ft.commit();
                }
            });
        }
    }
}
