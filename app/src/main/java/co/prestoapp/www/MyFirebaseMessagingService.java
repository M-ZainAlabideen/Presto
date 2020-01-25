package co.prestoapp.www;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import co.prestoapp.www.Views.StatusActivity;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    JSONObject object,dataObject;
    String destination,order_id;
    NotificationCompat.Builder notificationBuilder;
    RemoteMessage remoteMessage;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        this.remoteMessage=remoteMessage;

         notificationBuilder = new
                NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.app_icon_notification)
                .setContentTitle(remoteMessage.getNotification().getTitle())
                .setContentText(remoteMessage.getNotification().getBody())
                .setAutoCancel(true);

        //add sound
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        notificationBuilder.setSound(uri);
        //vibrate
        long[] v = {500,1000};
        notificationBuilder.setVibrate(v);

        NotificationManager notificationManager =(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("NotifiCounter", Context.MODE_PRIVATE);
        int i =sharedPref.getInt("NotifiCounterKEY",0)+1;
        sharedPref.edit().putInt("NotifiCounterKEY",i).commit();

        NotificationIntent();
        notificationManager.notify(i, notificationBuilder.build());

    }

    private void NotificationIntent(){
        object = new JSONObject(remoteMessage.getData());

        Log.e("JSON OBJECT",object.toString());
        Iterator<String> keys = object.keys();
        while (keys.hasNext())
        {

            if(keys.next().equals("data"))
            {

                try {

                    dataObject = new JSONObject(object.getString("data"));
                    destination = dataObject.getString("destination");
                    switch (destination)
                    {
                        case "order":
                            order_id = dataObject.getString("data_id");
                            Intent notifyIntent = new Intent(this, StatusActivity.class);
                            notifyIntent.putExtra("COME_FROM","fireBaseNotification");
                            notifyIntent.putExtra("ORDER_ID",order_id);
                            // Set the Activity to start in a new, empty task
                            notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            // Create the PendingIntent
                            PendingIntent notifyPendingIntent = PendingIntent.getActivity(this, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                            notificationBuilder.setContentIntent(notifyPendingIntent);
                            break;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }}
      }
}