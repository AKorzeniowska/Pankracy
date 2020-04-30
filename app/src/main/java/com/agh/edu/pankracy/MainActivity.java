package com.agh.edu.pankracy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

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

    }
}
