package com.agh.edu.pankracy.notifications;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;

import com.agh.edu.pankracy.data.PlantContract;
import com.agh.edu.pankracy.utils.DateUtils;

import java.util.Date;

public class NotificationIntentService extends IntentService {
    public static final String ACTION_CONFIRM = "already-watered";


    public NotificationIntentService() {
        super("DatabaseUpdateIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String action = intent.getAction();
        if (ACTION_CONFIRM.equals(action)) {
            updateData();
            clearAllNotifications(this);
        }
    }

    private void updateData() {
        for (Integer id : NotificationUtils.plantsToWaterData.keySet()) {
            updateWateringDate(id);
        }
    }


    public void updateWateringDate(int id) {
        String today = DateUtils.sdf.format(new Date());

        ContentValues values = new ContentValues();
        values.put(PlantContract.COLUMN_LAST_WATERING, today);

        getContentResolver().update(PlantContract.CONTENT_URI_ID(id), values, null, null);
    }

    private static void clearAllNotifications (Context context){
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }
}