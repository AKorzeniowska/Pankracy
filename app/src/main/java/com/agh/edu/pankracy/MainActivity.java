package com.agh.edu.pankracy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.agh.edu.pankracy.data.weather.WeatherCollection;
import com.agh.edu.pankracy.fragments.CalendarFragment;
import com.agh.edu.pankracy.fragments.AboutFragment;
import com.agh.edu.pankracy.fragments.ListOfPlantsFragment;
import com.agh.edu.pankracy.fragments.WeatherFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    public static WeatherCollection WEATHER_COLLECTION = null;

    BottomNavigationView bottomNavigation;
    Toolbar appToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);
        initiateToolbar();
        bottomNavigation = findViewById(R.id.bottom_nav);

        bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        if(null == savedInstanceState) {
            openFragment(ListOfPlantsFragment.newInstance());
        }
    }

    private void initiateToolbar() {
        appToolbar = findViewById(R.id.my_toolbar);
        TextView mTitle = appToolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(appToolbar);
        getSupportActionBar().setIcon(R.drawable.ic_logo_32dp);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }
    public void openCalendarActivity(View view) {
        Intent calendarIntent = new Intent(this, CalendarActivity.class);
        startActivity(calendarIntent);
    }

    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.nav_about:
                            openFragment(AboutFragment.newInstance());
                            return true;
                        case R.id.nav_list:
                            openFragment(ListOfPlantsFragment.newInstance());
                            return true;
                        case R.id.nav_weather:
                            openFragment(WeatherFragment.newInstance());
                            return true;
                        case R.id.nav_calendar:
                            openFragment(CalendarFragment.newInstance("", ""));
                            return true;
                    }
                    return false;
                }
            };

}
