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
import com.agh.edu.pankracy.data.PlantDBOperations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class NotificationUtils {

    private static final int WATER_NOTIFICATION_ID = 1138;
    private static final int WATER_PENDING_INTENT_ID = 3417;
    private static final String WATER_NOTIFICATION_CHANNEL_ID = "watering_notification_channel";

    private static final int ACTION_CONFIRM_PENDING_INTENT_ID = 8;

    static Map<Integer, String> plantsToWaterData = null;

    private static PendingIntent contentIntent (Context context){
        Intent startActivityIntent = new Intent(context, MainActivity.class);
        return PendingIntent.getActivity(
                context,
                WATER_PENDING_INTENT_ID,
                startActivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static void remindUserToWater (Context context){
        Map<Integer, String> plantsToWater = PlantDBOperations.getPlantsToWaterAtDate(new Date(), context);

//        if (plantsToWater.isEmpty())
//            return;

        plantsToWaterData = plantsToWater;

        StringBuilder stringBuilder = new StringBuilder();
        if (!plantsToWater.isEmpty())
            stringBuilder.append("You have plants to water today:\n");
        for (String plant : plantsToWater.values()){
            stringBuilder.append(plant);
            stringBuilder.append("\n");
        }

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
                .setSmallIcon(R.drawable.ic_flower)
                .setContentTitle("Don't forget about your plants!")
                //.setContentText("You have plants to water today")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(stringBuilder.toString()))
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setContentIntent(contentIntent(context))
                //.addAction(confirmAction((context)))
                .setAutoCancel(true);
        if (!plantsToWater.isEmpty())
            notificationBuilder.addAction(confirmAction(context));

        notificationManager.notify(WATER_NOTIFICATION_ID, notificationBuilder.build());
    }


    private static NotificationCompat.Action confirmAction(Context context){
        Intent confirmIntent = new Intent(context, NotificationIntentService.class);
        confirmIntent.setAction(NotificationIntentService.ACTION_CONFIRM);
        PendingIntent confirmPendingIntent = PendingIntent.getService(
                context,
                ACTION_CONFIRM_PENDING_INTENT_ID,
                confirmIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        return new NotificationCompat.Action(
                R.drawable.ic_flower,
                "Already did it!",
                confirmPendingIntent);
    }


}