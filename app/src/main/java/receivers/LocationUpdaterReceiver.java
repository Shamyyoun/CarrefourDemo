package receivers;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.provider.Settings;

import net.tundigital.carrefourdemo.AppController;
import net.tundigital.carrefourdemo.GPSTracker;
import net.tundigital.carrefourdemo.R;

import datamodels.Constants;
import datamodels.User;
import json.JsonParser;
import json.UserHandler;
import utils.InternetUtil;

public class LocationUpdaterReceiver extends BroadcastReceiver {
    private User user;

    @Override
    public void onReceive(Context context, Intent intent) {
        user = (User) intent.getSerializableExtra(Constants.KEY_ACTIVE_USER);

        // check if internet is enabled
        if (InternetUtil.isConnected(context)) {
            // check if user is null
            if (user == null) {
                // get cached user
                String response = AppController.getActiveUserResponse(context);
                UserHandler userHandler = new UserHandler(response);
                user = userHandler.handle();
            }

            GPSTracker gpsTracker = new GPSTracker(context);
            // check if gps is enabled
            if (gpsTracker.isGPSEnabled()) {
                // get current location from GPS
                Location location = gpsTracker.getLocation();

                // update location
                new UpdateLocationTask(location, AppController.MAX_LOCATION_UPDATER_TRIES).execute();
            } else {
                // not enabled
                // show notification
                NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                int icon = R.drawable.ic_launcher;
                long when = System.currentTimeMillis();
                String title = context.getString(R.string.app_name);
                String content = context.getString(R.string.should_enable_gps);

                Intent notificationIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                PendingIntent contentIntent = PendingIntent.getActivity(context, Constants.NOTIFICATION_OPEN_GPS, notificationIntent, 0);
                Notification notification = new Notification(icon, title, when);
                notification.setLatestEventInfo(context, title, content, contentIntent);
                notification.flags |= Notification.FLAG_AUTO_CANCEL;

                mNotificationManager.notify(Constants.NOTIFICATION_OPEN_GPS, notification);
            }

            // --start alarm manager to update location after static time--
            Intent mIntent = new Intent(context, LocationUpdaterReceiver.class);
            mIntent.putExtra(Constants.KEY_ACTIVE_USER, user);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                    Constants.RECEIVER_LOCATION_UPDATER, mIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP,
                    System.currentTimeMillis() + AppController.LOCATION_UPDATER_DELAY,
                    pendingIntent);
        }
    }

    /*
     * async task, used to update current location
     */
    private class UpdateLocationTask extends AsyncTask<Void, Void, Void> {
        private Location location;
        private int triesCount;

        private double latitude = 0.0;
        private double longitude = 0.0;
        private String response;

        private UpdateLocationTask(Location location, int triesCount) {
            this.location = location;
            this.triesCount = triesCount;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // get lat & long
            if (location != null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
            }

            //  validate lat & long
            if (latitude == 0.0 || longitude == 0.0) {
                cancel(true);
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            // create json parser
            String url = AppController.END_POINT + "/user-update/" + user.getId() + "/lat,lon/" + latitude + "," + longitude;
            JsonParser jsonParser = new JsonParser(url);

            // execute request
            response = jsonParser.sendGetRequest();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            // --check response--
            if (response == null || Constants.JSON_FALSE_MSG.equals(response)) {
                // request has problems, retry if possible
                if (triesCount > 0) {
                    // decrement tries count
                    triesCount--;
                    // retry
                    new UpdateLocationTask(location, triesCount).execute();
                }
            }
        }
    }
}
