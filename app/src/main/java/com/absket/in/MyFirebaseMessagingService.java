package com.absket.in;

/**
 * Created by Riju on 9/7/2016.
 */
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.webengage.sdk.android.WebEngage;

import java.util.Map;
import java.util.Random;

/**
 * Created by Belal on 5/27/2016.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    Random ran = new Random();
    int mId;
    SQLiteDatabase QuilSQL;
    Bitmap bitmap;
    static String sHeadLine,sImages,sCode,sImgUrl,sNewsId;
    UserSessionManager session;


    String sMesg,sBno,sPartnerName,sPartnerMob,sAmount,sOffer;

   /* @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {


        String sCode = remoteMessage.getData().get("code");
        sendNotificationOFFERS(sCode);


    }*/


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        System.out.println("MyFirebaseMsgService:"+remoteMessage.getData().toString());
        Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());
        if (remoteMessage.getData().size() > 0) {
            try {
                Map<String, String> data = remoteMessage.getData();
                if (data != null) {


                    for (Map.Entry<String, String> entry : data.entrySet())
                    {
                        System.out.println(entry.getKey() + "/" + entry.getValue());
                    }

                    System.out.println("datavalue:"+data);
                    if (data.containsValue("code")) {

                            sendPushNotification(data.get("title"), data.get("message"));
                                            }else{
                        WebEngage.get().receive(data);
                    }
                }

            } catch (Exception e) {

            }
        }
    }




    public void sendNotificationOFFERS(String sOffer)
    {
        Intent intent = new Intent(getApplicationContext(), MainActivity_New.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        // intent.putExtra("newsid",sNewsId);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Bitmap icon = BitmapFactory.decodeResource(getResources(),
                R.drawable.andhrabasket);
        NotificationCompat.Builder notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(getApplicationContext())
                .setLargeIcon(icon)
                .setContentTitle("ZopRent New Offers")
                .setContentText(sOffer)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        notificationBuilder.setSmallIcon(R.drawable.andhrabasket);


        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }

    private void sendPushNotification(String messageBody, String title) {
        Intent intent = new Intent(this, MainActivity_New.class);
        intent.putExtra("open_concierge","yes");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /*/ Request code /*/, intent,
                PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.app_icon).setContentTitle(title)
                .setContentText(messageBody).setAutoCancel(true)
                .setSound(defaultSoundUri).setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0 /*/ ID of notification /*/, notificationBuilder.build());
    }


/*    public void getBitmap(final String imageUrl)
    {
        new AsyncTask<String,Void,String>()
        {
            String sImage = imageUrl;
            @Override
            protected String doInBackground(String... params) {
                try {
                    URL url = new URL(sImage);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream input = connection.getInputStream();
                    bitmap = BitmapFactory.decodeStream(input);
                }
                catch (Exception e){
                    e.printStackTrace();
                    e.getMessage();
                    return null;
                }
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if(sCode.equals("NEWS"))
                {
                    sendNotificationNews(sHeadLine,bitmap);

                }
                else if(sCode.equals("AUDIO"))
                {
                    sendNotificationOther(sHeadLine,bitmap);
                }
                else
                {
                    sendNotificationOther1(sHeadLine,bitmap);
                }

            }
        }.execute();
    }*/




}