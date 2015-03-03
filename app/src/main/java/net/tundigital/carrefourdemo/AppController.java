package net.tundigital.carrefourdemo;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

import datamodels.Constants;
import datamodels.User;
import json.JsonReader;
import utils.FontUtil;

/**
 * Created by Shamyyoun on 2/8/2015.
 */
public class AppController extends Application {
    public static final String END_POINT = "http://apps.turndigital.net/carrefour";
    public static final String PROJECT_NUMBER = "834640672125";
    public static final long LOCATION_UPDATER_DELAY = 1000; // time in milli seconds
    public static final int MAX_LOCATION_UPDATER_TRIES = 5;

    public User activeUser;
    public HomeFragment homeFragment; // used to handle incoming instant offers

    public AppController() {
        super();
    }

    /**
     * method used to return current application instance
     */
    public static AppController getInstance(Context context) {
        return (AppController) context;
    }

    /**
     * method used too update active user response in SP
     */
    public static void updateActiveUserResponse(Context context, String value) {
        updateCachedString(context, Constants.SP_RESPONSES,
                Constants.SP_KEY_ACTIVE_USER_RESPONSE, value);
    }

    /**
     * method used to get active user response from SP
     */
    public static String getActiveUserResponse(Context context) {
        String response = getCachedString(context,
                Constants.SP_RESPONSES, Constants.SP_KEY_ACTIVE_USER_RESPONSE);

        return response;
    }

    /**
     * method used to remove cached used response from SP
     */
    public static void removeActiveUserResponse(Context context) {
        removeCachedValue(context, Constants.SP_RESPONSES, Constants.SP_KEY_ACTIVE_USER_RESPONSE);
    }

    /**
     * method used to update string value in SP
     */
    private static void updateCachedString(Context context, String spName, String valueName, String value) {
        SharedPreferences sp = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(valueName, value);
        editor.commit();
    }

    /**
     * method used to get cached String from SP
     */
    private static String getCachedString(Context context, String spName, String valueName) {
        SharedPreferences sp = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
        String value = sp.getString(valueName, null);

        return value;
    }

    /**
     * method used to remove value from SP
     */
    private static void removeCachedValue(Context context, String spName, String valueName) {
        SharedPreferences sp = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(valueName);
        editor.commit();
    }

    /**
     * method, used to send view action to server
     */
    public static void sendViewAction(Context context, String offerId) {
        // prepare url
        String url = AppController.END_POINT + "/add-action";

        // create json reader
        JsonReader jsonReader = new JsonReader(url);

        // prepare parameters
        List<NameValuePair> parameters = new ArrayList<>();
        parameters.add(new BasicNameValuePair("user_id", getInstance(context).activeUser.getId()));
        parameters.add(new BasicNameValuePair("offer_id", offerId));
        parameters.add(new BasicNameValuePair("behavior_id", Constants.BEHAVIOUR_VIEW));

        // execute request
        jsonReader.sendAsyncPostRequest(parameters);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // override default monospace font
        FontUtil.setDefaultFont(this, "MONOSPACE", "roboto.ttf");
    }
}
