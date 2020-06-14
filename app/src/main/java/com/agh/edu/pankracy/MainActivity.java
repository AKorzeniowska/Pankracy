package com.agh.edu.pankracy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.MenuItem;

import com.agh.edu.pankracy.data.weather.WeatherCollection;
import com.agh.edu.pankracy.fragments.CalendarFragment;
import com.agh.edu.pankracy.fragments.AboutFragment;
import com.agh.edu.pankracy.fragments.ListOfPlantsFragment;
import com.agh.edu.pankracy.fragments.WeatherFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

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
            openFragment(ListOfPlantsFragment.newInstance(), false);
        }
    }

    private void initiateToolbar() {
        appToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(appToolbar);
        getSupportActionBar().setIcon(R.drawable.ic_logo_32dp);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @SuppressLint("SourceLockedOrientationActivity")
    public void openFragment(Fragment fragment, boolean twoOrientations) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        if(twoOrientations)
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        else
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        transaction.commit();
    }

    BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.nav_about:
                            openFragment(AboutFragment.newInstance(), false);
                            return true;
                        case R.id.nav_list:
                            openFragment(ListOfPlantsFragment.newInstance(), false);
                            return true;
                        case R.id.nav_weather:
                            openFragment(WeatherFragment.newInstance(), false);
                            return true;
                        case R.id.nav_calendar:
                            openFragment(CalendarFragment.newInstance(), true);
                            return true;
                    }
                    return false;
                }
            };

}
