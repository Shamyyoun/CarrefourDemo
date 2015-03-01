package services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;

import net.tundigital.carrefourdemo.AppController;
import net.tundigital.carrefourdemo.MainActivity;
import net.tundigital.carrefourdemo.R;

import java.util.Calendar;
import java.util.Locale;

import database.OfferDAO;
import datamodels.Constants;
import datamodels.Offer;
import receivers.GcmBroadcastReceiver;

public class GcmMessageHandler extends IntentService {
    private Handler handler;

    public GcmMessageHandler() {
        super("GcmMessageHandler");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();

        // get notification key
        String key = extras.getString("key");

        // check notification key
        if (Constants.PUSHN_KEY_NEW_OFFER.equals(key)) {
            // get values
            String name = extras.getString("name");
            String desc = extras.getString("description");
            String expireDate = extras.getString("expire");

            // create suitable offer object
            final Offer offer = new Offer("");
            offer.setName(name);
            offer.setDescription(desc);
            offer.setExpireDate(expireDate);
            offer.setCreateDate(Calendar.getInstance(Locale.getDefault()).getTime());

            // save instant offer in Database.
            OfferDAO offerDAO = new OfferDAO(getApplicationContext());
            offerDAO.open();
            long offerId = offerDAO.save(offer);
            offerDAO.close();

            // show notification
            Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
            showNotification(1, name, desc, notificationIntent);

            // show this offer in home if possible
            if (AppController.getInstance(getApplicationContext()).homeFragment != null) {
                offer.setId("" + offerId);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        AppController.getInstance(getApplicationContext()).homeFragment.addInstantOffer(offer);
                    }
                });
            }
        }

        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    public void showNotification(final int id, final String title, final String desc, final Intent notificationIntent) {
        handler.post(new Runnable() {
            public void run() {
                NotificationManager mNotificationManager = (NotificationManager) getApplicationContext().
                        getSystemService(Context.NOTIFICATION_SERVICE);

                int icon = R.drawable.ic_launcher;
                long when = System.currentTimeMillis();
                Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(),
                        id, notificationIntent, 0);
                Notification notification = new Notification(icon, title, when);
                notification.sound = soundUri;
                notification.setLatestEventInfo(getApplicationContext(), title, desc, contentIntent);
                notification.flags |= Notification.FLAG_AUTO_CANCEL;

                mNotificationManager.notify(id, notification);
            }
        });

    }
}
