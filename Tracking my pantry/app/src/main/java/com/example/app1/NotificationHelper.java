package com.example.app1;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.provider.Settings;
import androidx.core.app.NotificationCompat;
import java.util.Calendar;

//Questa classe serve per la creazione delle notifiche
class NotificationHelper {

    private String title="Reminder per compere";
    private String description ;
    private Context mContext;
    private static final String NOTIFICATION_CHANNEL_ID = "10001";

    NotificationHelper(Context context) {
        mContext = context;
    }

    void createNotification()
    {
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        //A seconda dei giorni della settimana, avvengono diversi pop up
        if(dayOfWeek == 1 ||dayOfWeek == 3||dayOfWeek == 5 ){
            description = "Sono stati comprati carboidrati e bevande?";
        }
        else if(dayOfWeek == 2 ||dayOfWeek == 4||dayOfWeek == 6 ){
            description = "Sono stati comprati protein e bevande?";
        }
        else{
            description = "Sono stati comprati i lipidi?";
        }

        Intent intent = new Intent(mContext , MainActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent resultPendingIntent = PendingIntent.getActivity(mContext,
                0 /* Request code */, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext, NOTIFICATION_CHANNEL_ID);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        mBuilder.setContentTitle(title);
        mBuilder.setContentText(description)
                .setAutoCancel(false)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
        {
            //Qui vengono dichiarate diverse caratteristiche della notifica
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            assert mNotificationManager != null;
            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
        assert mNotificationManager != null;
        mNotificationManager.notify(0 /* Request Code */, mBuilder.build());
    }
}
