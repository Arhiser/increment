package com.arhiser.increment.push;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.arhiser.increment.tools.Utils;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONException;
import org.json.JSONObject;

public class GcmIntentService extends IntentService {
    public static final int DEFAULT_NOTIFICATION_ID = 1;

    public GcmIntentService() {
        super("GcmIntentService");
    }

    public static final String TAG = "GCM";
    public static final String GCM_PREFERENCES_KEY = "GCM";
    public static final String TAGS_PREFERENCES_KEY = "tags";
    public static final String MESSAGE_PREFERENCES_KEY = "message";

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            Log.i(TAG, "Received: " + extras.toString());
            /*
             * Filter messages based on message type. Since it is likely that GCM will be
             * extended in the future with new message types, just ignore any message types you're
             * not interested in, or that you don't recognize.
             */

            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                // Post notification of received message.
                String text = extras.getString("message", "");
                int eventId = getEventId(extras);
                String url = getUrl(extras);
                int notificationId = DEFAULT_NOTIFICATION_ID;
                if (extras.containsKey("notification_id")) {
                    notificationId = Utils.parseInt(String.valueOf(extras.get("notification_id")), notificationId);
                }
                //if (extras.containsKey("user_id")) {
                    try {
                        //String userId = (String) extras.get("user_id");
                        //if (GlobalData.getInstance().getCurrentUser().getUserId() == Integer.valueOf(userId == null ? "0" : userId )){
                            sendNotificationMessage(notificationId, text, eventId, url);
                        //}
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                //}

            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    private String getUrl(Bundle extras)
    {
        if(extras == null || extras.getString("data") == null) return null;
        try {
            JSONObject json = new JSONObject(extras.getString("data"));
            return json.getString("url");
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private int getEventId(Bundle extras) {
        int id = 0;
        if(extras == null || extras.getString("data") == null) return id;
        try {
            JSONObject json = new JSONObject(extras.getString("data"));
            id = json.getInt("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return id;
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotificationMessage(int notificationId, String msg, int eventId, String url) {
        NotificationManager notificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);
/*
        Intent notificationIntent;
        ArrayList<String> sites = Utils.extractLinksFromString(msg);
        if (sites.size() > 0 || !Utils.isEmpty(url)) {
            String site = !Utils.isEmpty(url) ? url : sites.get(0);

            if (!site.startsWith("http")) site = "http://" + site;
            notificationIntent = new Intent(this, WebViewActivity.class);
            notificationIntent.putExtra(Constants.URL_EXTRA, site);
        } else {
            notificationIntent = new Intent(this, CustomerMainActivity.class);
        }

        String tag = "notification_" + notificationId;

        if (eventId != 0) {
            notificationIntent.putExtra("event_id", eventId);
        }

        SharedPreferences preferences = getSharedPreferences(GCM_PREFERENCES_KEY, Context.MODE_PRIVATE);
        Set<String> tags = preferences.getStringSet(TAGS_PREFERENCES_KEY, new HashSet<String>());

        tags.add(tag);
        preferences.edit()
                .putString(MESSAGE_PREFERENCES_KEY + tag, msg)
                .commit();

        SharedPreferences.Editor prefsEditor = preferences.edit();
        for (Iterator<String> iterator = tags.iterator(); iterator.hasNext(); ) {
            String messageTag = iterator.next();
            String message = preferences.getString(MESSAGE_PREFERENCES_KEY + messageTag, "");
            if (message.trim().isEmpty()) {
                iterator.remove();
                if (preferences.contains(MESSAGE_PREFERENCES_KEY + messageTag)) {
                    prefsEditor.remove(MESSAGE_PREFERENCES_KEY + messageTag);
                }
            }

            notificationManager.cancel(messageTag, DEFAULT_NOTIFICATION_ID);
        }

        prefsEditor.putStringSet(TAGS_PREFERENCES_KEY, tags);
        prefsEditor.apply();

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mBuilder;
        if (tags.size() < 1) {
            return;
        } else if (tags.size() == 1) {

            mBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.ic_logo)
                            .setContentTitle(getResources().getString(R.string.app_name))
                            .setContentText(msg)
                            .setAutoCancel(true)
                            .setSound(alarmSound)
                            .setStyle(new NotificationCompat.BigTextStyle()
                                    .bigText(msg))
                            .setContentText(msg);


        } else {
            NotificationCompat.InboxStyle inbox = new NotificationCompat.InboxStyle();

            String title = getResources().getQuantityString(R.plurals.new_notifications, tags.size(), tags.size());
            inbox.setBigContentTitle(title)
                    .setSummaryText(getResources().getString(R.string.new_messages));
            String[] tagsArray = tags.toArray(new String[tags.size()]);
            Arrays.sort(tagsArray);

            for (String messageTag : tagsArray) {
                inbox.addLine(preferences.getString(MESSAGE_PREFERENCES_KEY + messageTag, ""));
            }

            mBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.ic_logo)
                            .setContentTitle(getResources().getString(R.string.app_name))
                            .setContentText(msg)
                            .setAutoCancel(true)
                            .setSound(alarmSound)
                            .setStyle(inbox)
                            .setGroupSummary(true);

            notificationIntent.putExtra("group_notifications", true);
        }

        notificationIntent.putExtra("notification_id", notificationId);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        mBuilder.setContentIntent(contentIntent);
        notificationManager.notify(tag, DEFAULT_NOTIFICATION_ID, mBuilder.build());
        */
    }

}

