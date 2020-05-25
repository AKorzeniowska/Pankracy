package com.agh.edu.pankracy;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.agh.edu.pankracy.notifications.NotificationUtils;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void openListActivity(View view) {
        Intent plantIntent = new Intent(this, PlantListActivity.class);
        startActivity(plantIntent);
    }

    public void openCalendarActivity(View view) {
        Intent calendarIntent = new Intent(this, CalendarActivity.class);
        startActivity(calendarIntent);
    }

    public void openWeatherTestActivity(View view) {
        Intent plantIntent = new Intent(this, WeatherTestActivity.class);
        startActivity(plantIntent);
    }

    public void issueNotification(View view) {
        NotificationUtils.remindUserToWater(this);
    }
}
