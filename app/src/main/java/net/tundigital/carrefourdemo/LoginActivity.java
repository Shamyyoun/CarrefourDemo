package net.tundigital.carrefourdemo;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.devspark.appmsg.AppMsg;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import datamodels.Constants;
import datamodels.User;
import json.JsonReader;
import json.UserHandler;
import receivers.LocationUpdaterReceiver;
import utils.InternetUtil;
import utils.ViewUtil;


public class LoginActivity extends ActionBarActivity {
    private EditText textUsername;
    private EditText textPassword;
    private Button buttonLogin;
    private ProgressBar progressBar;

    private ArrayList<AsyncTask> runningTasks; // used to hold running tasks

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // check cached user
        String response = AppController.getActiveUserResponse(getApplicationContext());
        if (response != null) {
            // cached user exists
            // handle it in user object
            UserHandler userHandler = new UserHandler(response);
            User user = userHandler.handle();

            // save current user in global app controller
            AppController.getInstance(getApplicationContext()).activeUser = user;

            // open Main Activity
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            LoginActivity.this.startActivity(intent);
            finish();

            // --start alarm manager to update location right now--
            Intent mIntent = new Intent(getApplicationContext(), LocationUpdaterReceiver.class);
            mIntent.putExtra(Constants.KEY_ACTIVE_USER, user);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),
                    Constants.RECEIVER_LOCATION_UPDATER, mIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pendingIntent);
        } else {
            // no cached user >> proceed in login activity
            setContentView(R.layout.activity_login);

            initComponents();
        }
    }

    /**
     * method used to initialize components
     */
    private void initComponents() {
        textUsername = (EditText) findViewById(R.id.text_username);
        textPassword = (EditText) findViewById(R.id.text_password);
        buttonLogin = (Button) findViewById(R.id.button_login);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        runningTasks = new ArrayList<>();

        // add listeners
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = textUsername.getText().toString();
                String password = textPassword.getText().toString();
                new LoginTask(username, password).execute();
            }
        });
    }

    /**
     * method used to show or hide progress when login is running
     */
    private void showProgress(boolean show) {
        // enable/disable components
        textUsername.setEnabled(!show);
        textPassword.setEnabled(!show);
        buttonLogin.setEnabled(!show);

        ViewUtil.showView(buttonLogin, !show);
        ViewUtil.showView(progressBar, show);
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
     * sub class used to send login request
     */
    private class LoginTask extends AsyncTask<Void, Void, Void> {
        private String username;
        private String password;

        private String response;

        private LoginTask(String username, String password) {
            this.username = username;
            this.password = password;

            // save reference to this task, to destroy it if required
            runningTasks.add(this);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // check username & password;
            if (username.isEmpty() || password.isEmpty()) {
                // show error  msg
                AppMsg.cancelAll(LoginActivity.this);
                AppMsg.makeText(LoginActivity.this, R.string.invalid_inputs, AppMsg.STYLE_ALERT).show();

                cancel(true);
                return;
            }

            // check internet connection
            if (!InternetUtil.isConnected(getApplicationContext())) {
                // show error  msg
                AppMsg.cancelAll(LoginActivity.this);
                AppMsg.makeText(LoginActivity.this, R.string.no_internet_connection, AppMsg.STYLE_ALERT).show();

                cancel(true);
                return;
            }

            // show progress
            showProgress(true);
        }

        @Override
        protected Void doInBackground(Void... params) {
            // create json parser
            String url = AppController.END_POINT + "/user-login";
            JsonReader jsonReader = new JsonReader(url);

            // parameters
            List<NameValuePair> parameters = new ArrayList<>(2);
            parameters.add(new BasicNameValuePair("unm", username));
            parameters.add(new BasicNameValuePair("pwd", password));

            // execute request
            response = jsonReader.sendPostRequest(parameters);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            // hide progress
            showProgress(false);

            // validate response
            if (response == null) {
                // show error msg
                AppMsg.cancelAll(LoginActivity.this);
                AppMsg.makeText(LoginActivity.this, R.string.unexpected_error_try_again, AppMsg.STYLE_ALERT).show();

                return;
            }

            // check response
            if (response.equals(Constants.JSON_NULL_MSG)) {
                // show error msg
                AppMsg.cancelAll(LoginActivity.this);
                AppMsg.makeText(LoginActivity.this, R.string.invalid_username_or_password, AppMsg.STYLE_ALERT).show();

                return;
            }

            // --response is valid--
            // handle it in user object
            UserHandler userHandler = new UserHandler(response);
            User user = userHandler.handle();

            // check handling operation result
            if (user == null) {
                // show error msg
                AppMsg.cancelAll(LoginActivity.this);
                AppMsg.makeText(LoginActivity.this, R.string.unexpected_error_try_again, AppMsg.STYLE_ALERT).show();

                return;
            }

            // update user's reg_id in server
            new RegIdTask(user.getId()).execute();

            // cache current user
            AppController.updateActiveUserResponse(getApplicationContext(), response);

            // save current user in global app controller
            AppController.getInstance(getApplicationContext()).activeUser = user;

            // --start alarm manager to update location right now--
            Intent mIntent = new Intent(getApplicationContext(), LocationUpdaterReceiver.class);
            mIntent.putExtra(Constants.KEY_ACTIVE_USER, user);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),
                    Constants.RECEIVER_LOCATION_UPDATER, mIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP,
                    System.currentTimeMillis(),
                    pendingIntent);

            // open Main Activity
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            LoginActivity.this.startActivity(intent);
            finish();
        }
    }

    /**
     * sub class used to get and update user's reg_id in server
     */
    private class RegIdTask extends AsyncTask<Void, Void, Void> {
        private String userId;

        private GoogleCloudMessaging gcm;

        private RegIdTask(String userId) {
            this.userId = userId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // check user id
            if (userId.isEmpty()) {
                cancel(true);
                return;
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                // register user to GCM first
                if (gcm == null) {
                    gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
                }
                String regId = gcm.register(AppController.PROJECT_NUMBER);

                // create json parser
                String url = AppController.END_POINT + "/update-gen";
                JsonReader jsonReader = new JsonReader(url);

                // parameters
                List<NameValuePair> paramaters = new ArrayList<>(2);
                paramaters.add(new BasicNameValuePair("user_id", AppController.getInstance(getApplicationContext()).activeUser.getId()));
                paramaters.add(new BasicNameValuePair("reg_id", regId));

                // execute request
                jsonReader.sendPostRequest(paramaters);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}
