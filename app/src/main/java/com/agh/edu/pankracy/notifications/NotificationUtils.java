package com.agh.edu.pankracy.notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Pair;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;


import com.agh.edu.pankracy.MainActivity;
import com.agh.edu.pankracy.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class NotificationUtils {

    private static final int WATER_NOTIFICATION_ID = 1138;
    private static final int WATER_PENDING_INTENT_ID = 3417;
    private static final String WATER_NOTIFICATION_CHANNEL_ID = "watering_notification_channel";

    private static final int ACTION_DISMISS_PENDING_INTENT_ID = 7;
    private static final int ACTION_CONFIRM_PENDING_INTENT_ID = 8;

    private ArrayList<String> plantsToWater = new ArrayList<>();

    private static PendingIntent contentIntent (Context context){
        Intent startActivityIntent = new Intent(context, MainActivity.class);
        return PendingIntent.getActivity(
                context,
                WATER_PENDING_INTENT_ID,
                startActivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private static Bitmap largeIcon (Context context){
        Resources resources = context.getResources();
        Bitmap largeIcon = null;//BitmapFactory.decodeResource(resources, R.drawable.icon_flowers);
        return largeIcon;
    }

    public static void remindUserToWater (Context context){//}, ArrayList<String> plantsToWater){
//        StringBuilder stringBuilder = new StringBuilder();
//        for (String plant : plantsToWater){
//            stringBuilder.append(plant);
//            stringBuilder.append("\n");
//        }

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannel mChannel = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mChannel = new NotificationChannel(
                    WATER_NOTIFICATION_CHANNEL_ID,
                    //context.getString(R.string.main_notification_channel_name),
                    "watering reminder on boot",
                    NotificationManager.IMPORTANCE_HIGH);
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(mChannel);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, WATER_NOTIFICATION_CHANNEL_ID)
                //.setColor(ContextCompat.getColor(context, R.color.main_title))
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                //.setLargeIcon(largeIcon(context))
                //.setContentTitle(context.getString(R.string.notification_title))
                //.setContentText(context.getString(R.string.notification_body))
                .setContentTitle("Oh look!")
                .setContentText("The notification is here")
//                .setStyle(new NotificationCompat.BigTextStyle()
//                        .bigText(stringBuilder.toString()))
//                .setStyle(new NotificationCompat.BigTextStyle()
//                        .bigText("blabla"))
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setContentIntent(contentIntent(context))
                //.addAction(confirmAction((context)))
                .setAutoCancel(true);

        Notification notification = notificationBuilder.build();

        notificationManager.notify(WATER_NOTIFICATION_ID, notificationBuilder.build());
    }


    public static void clearAllNotifications (Context context){
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

//    private static NotificationCompat.Action confirmAction(Context context){
//        Intent confirmIntent = new Intent(context, NotificationIntentService.class);
//        confirmIntent.setAction(NotificationTasks.ACTION_CONFIRM);
//        PendingIntent confirmPendingIntent = PendingIntent.getService(
//                context,
//                ACTION_CONFIRM_PENDING_INTENT_ID,
//                confirmIntent,
//                PendingIntent.FLAG_UPDATE_CURRENT);
//
//        NotificationCompat.Action confirmAction = new NotificationCompat.Action(
//                R.drawable.icon_flowers,
//                "Already did it!",
//                confirmPendingIntent);
//
//        return confirmAction;
//    }

//    private static NotificationCompat.Action quitReminding (Context context){
//        Intent quitRemindingIntent = new Intent(context, NotificationIntentService.class);
//        quitRemindingIntent.setAction(NotificationTasks.ACTION_CONFIRM);
//        PendingIntent quitRemindingPendingIntent
//    }



}