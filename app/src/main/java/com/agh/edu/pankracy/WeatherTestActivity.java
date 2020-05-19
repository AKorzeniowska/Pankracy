package com.agh.edu.pankracy;

import android.os.AsyncTask;
import android.os.Bundle;

import com.agh.edu.pankracy.utils.JSONUtils;
import com.agh.edu.pankracy.utils.NetworkUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
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
        new FetchWeatherTask().execute(String.valueOf(49.817225), String.valueOf(19.222505));
    }

    public class FetchWeatherTask extends AsyncTask<String, Void, String[]> {

        @Override
        protected String[] doInBackground(String... params) {
            if(params[0] == null || params[1] == null)
                return null;
            URL url = NetworkUtils.buildUrl(
                    Double.parseDouble(params[0]),
                    Double.parseDouble(params[1])
            );
            try {
                String jsonWeatherResponse = NetworkUtils.getResponseFromHttpUrl(url);
                String[] weatherData = JSONUtils
                        .parseToStrings(WeatherTestActivity.this, jsonWeatherResponse);
                return weatherData;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String[] weatherData) {
            if(weatherData != null) {
                StringBuilder weatherText = new StringBuilder();
                for (String weatherString : weatherData) {
                    weatherText.append(weatherString).append("\n\n\n");
                }
                weatherTextView.setText(weatherText);
            }
        }
    }
}
