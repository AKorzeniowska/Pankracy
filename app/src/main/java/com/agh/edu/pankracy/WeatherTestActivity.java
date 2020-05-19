package com.agh.edu.pankracy;

import android.os.AsyncTask;
import android.os.Bundle;

import com.agh.edu.pankracy.data.weather.WeatherCollection;
import com.agh.edu.pankracy.utils.JSONUtils;
import com.agh.edu.pankracy.utils.NetworkUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.TextView;
import java.net.URL;

public class WeatherTestActivity extends AppCompatActivity {
    private TextView weatherTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_test);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        weatherTextView = (TextView) findViewById(R.id.weather_info_text);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadWeatherData();
            }
        });
    }


    // Weather API Implementation
    private void loadWeatherData() {
        // TODO: Add getting localization
        new FetchWeatherTask().execute(49.817225, 19.222505);
    }

    public class FetchWeatherTask extends AsyncTask<Double, Void, WeatherCollection> {

        @Override
        protected WeatherCollection doInBackground(Double... params) {
            if(params[0] == null || params[1] == null)
                return null;
            URL url = NetworkUtils.buildUrl(
                    params[0],
                    params[1]
            );
            try {
                String jsonWeatherResponse = NetworkUtils.getResponseFromHttpUrl(url);
                return JSONUtils.parseApiResponse(WeatherTestActivity.this, jsonWeatherResponse);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(WeatherCollection weatherCollection) {
            if(weatherCollection != null)
                weatherTextView.setText(weatherCollection.toString());
        }

    }
}
