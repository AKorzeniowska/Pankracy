package com.agh.edu.pankracy.notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;


import com.agh.edu.pankracy.MainActivity;
import com.agh.edu.pankracy.R;
import com.agh.edu.pankracy.data.plants.DBHelper;
import com.agh.edu.pankracy.data.plants.Plant;
//import com.agh.edu.pankracy.data.PlantDBOperations;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationUtils {

    private static final int WATER_NOTIFICATION_ID = 1138;
    private static final int WATER_PENDING_INTENT_ID = 3417;
    private static final String WATER_NOTIFICATION_CHANNEL_ID = "watering_notification_channel";

    private static DBHelper dbHelper;

    private static final int ACTION_CONFIRM_PENDING_INTENT_ID = 8;

    static Map<Integer, String> plantsToWaterData = new HashMap<>();

    private static PendingIntent contentIntent (Context context){
        Intent startActivityIntent = new Intent(context, MainActivity.class);
        return PendingIntent.getActivity(
                context,
                WATER_PENDING_INTENT_ID,
                startActivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static void remindUserToWater (Context context){
        List<Plant> plants = null;
        StringBuilder stringBuilder = new StringBuilder();
        dbHelper = new DBHelper(context);
        try {
            plants = dbHelper.getPlantsForDate(new Date());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (plants != null && !plants.isEmpty()) {
            stringBuilder.append("You have plants to water today:\n");
            for (Plant plant : plants) {
                plantsToWaterData.put((int)plant.getId(), plant.getName());
                stringBuilder.append(plant.getName());
                stringBuilder.append("\n");
            }
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
                .setSmallIcon(R.drawable.ic_flower)
                .setContentTitle("Don't forget about your plants!")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(stringBuilder.toString()))
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setContentIntent(contentIntent(context))
                .setAutoCancel(true);
        if (plants != null && !plants.isEmpty())
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