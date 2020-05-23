package com.agh.edu.pankracy.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.widget.Toast;

import com.agh.edu.pankracy.notifications.NotificationUtils;

public class BootCompletedDetection extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            NotificationUtils.remindUserToWater(context);
        }
    }
}
