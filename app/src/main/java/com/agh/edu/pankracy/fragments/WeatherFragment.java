package com.agh.edu.pankracy.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.agh.edu.pankracy.MainActivity;
import com.agh.edu.pankracy.R;
import com.agh.edu.pankracy.adapters.WeatherListAdapter;
import com.agh.edu.pankracy.data.weather.Weather;
import com.agh.edu.pankracy.data.weather.WeatherCollection;
import com.agh.edu.pankracy.utils.JSONUtils;
import com.agh.edu.pankracy.utils.NetworkUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.net.URL;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WeatherFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WeatherFragment extends Fragment {
    private static final String LOG_TAG = WeatherFragment.class.getSimpleName();

    private int PERMISSION_ID = 45;
    private FusedLocationProviderClient mFusedLocationClient;
    private Double LATITUDE = null;
    private Double LONGITUDE = null;

    WeatherListAdapter adapter;
    private ListView listView;

    public WeatherFragment() {
        // Required empty public constructor
    }

    public static WeatherFragment newInstance() {
        return new WeatherFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_weather, container, false);
        listView = view.findViewById(R.id.weather_list);
        loadWeatherData(view);
        //setHasOptionsMenu(true);
        return view;
    }

    private void displayEmptyContent(View view) {
        listView.setVisibility(View.GONE);
        view.findViewById(R.id.current_weather_wrapper).setVisibility(View.GONE);
        view.findViewById(R.id.divider).setVisibility(View.GONE);
        view.findViewById(R.id.no_location_wrapper).setVisibility(View.VISIBLE);
    }

    private void displayWeather(WeatherCollection weatherCollection, View view) {
        listView.setVisibility(View.VISIBLE);
        view.findViewById(R.id.no_location_wrapper).setVisibility(View.GONE);
        view.findViewById(R.id.divider).setVisibility(View.VISIBLE);
        view.findViewById(R.id.current_weather_wrapper).setVisibility(View.VISIBLE);

        Weather currentWeather = weatherCollection.getCurrentWeather();
        ImageView mainWeatherIcon = view.findViewById(R.id.current_weather_icon);
        Context context = mainWeatherIcon.getContext();
        int iconId = context.getResources().getIdentifier(
                "weather_icon_" + currentWeather.getIcon(),
                "drawable",
                context.getPackageName()
        );
        mainWeatherIcon.setImageResource(iconId);

        setTextValue(view, R.id.current_weather_temperature, Double.toString(currentWeather.getTemperature()));
        setTextValue(view, R.id.current_weather_humidity, Integer.toString(currentWeather.getHumidity()));
        setTextValue(view, R.id.current_weather_wind, Double.toString(currentWeather.getWindSpeed()));
        setTextValue(view, R.id.current_weather_rain, Double.toString(currentWeather.getRain()));
        setTextValue(view, R.id.current_weather_description, currentWeather.getDescription().toUpperCase());

        adapter = new WeatherListAdapter(getActivity(), weatherCollection.getHourlyWeather());
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void setTextValue(View view, int viewId, String value) {
        TextView textView = view.findViewById(viewId);
        textView.setText(value);
    }


    // -- Weather API Implementation --
    private void loadWeatherData(View view) {
        if(MainActivity.WEATHER_COLLECTION != null) {
            displayWeather(MainActivity.WEATHER_COLLECTION, view);
        }
        else {
            displayEmptyContent(view);
        }
        getLastLocation();
    }

    @SuppressLint("StaticFieldLeak")
    public class FetchWeatherTask extends AsyncTask<Double, Void, WeatherCollection> {

        @Override
        protected WeatherCollection doInBackground(Double... params) {
            if (params[0] == null || params[1] == null)
                return null;
            URL url = NetworkUtils.buildUrl(
                    params[0],
                    params[1]
            );
            try {
                String jsonWeatherResponse = NetworkUtils.getResponseFromHttpUrl(url);
                return JSONUtils.parseApiResponse(jsonWeatherResponse);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(WeatherCollection weatherCollection) {
            if(weatherCollection == null) {
                if(MainActivity.WEATHER_COLLECTION == null) {
                    displayEmptyContent(getView());
                }
                else {
                    displayWeather(MainActivity.WEATHER_COLLECTION, getView());
                }
            }
            else {
                MainActivity.WEATHER_COLLECTION = weatherCollection;
                displayWeather(MainActivity.WEATHER_COLLECTION, getView());
            }
        }
    }

    // -- Getting Location --
    private void assignLocation(Double latitude, Double longitude) {
        LATITUDE = latitude;
        LONGITUDE = longitude;
        new FetchWeatherTask().execute(LATITUDE, LONGITUDE);
    }

    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                getActivity(),
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }

    private void getLastLocationWrapper() {
        mFusedLocationClient.getLastLocation().addOnCompleteListener(
                new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        if (location == null) {
                            requestNewLocationData();
                        } else {
                            assignLocation(location.getLatitude(), location.getLongitude());
                        }
                    }
                }
        );
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                getLastLocationWrapper();
            } else  {
                Toast.makeText(getActivity(), "Turn on location to get the weather data", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(intent, 1234);
            }
        } else {
            requestPermissions();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1234) {
            if(isLocationEnabled()) {
                getLastLocationWrapper();
            }
            else {
                assignLocation(null, null);
            }
        }
    }


    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );

    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            assignLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        }
    };

}
