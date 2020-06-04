package com.agh.edu.pankracy.utils;

import android.content.Context;
import android.util.Log;

import com.agh.edu.pankracy.data.weather.Weather;
import com.agh.edu.pankracy.data.weather.WeatherCollection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JSONUtils {
    private static final String LOG_TAG = JSONUtils.class.getSimpleName();

    /* Parameters names */
    private final static String LABEL_WEATHER = "weather";
    private final static String LABEL_WEATHER_DESCRIPTION = "description";

    private final static String LABEL_TEMPERATURE = "temp";
    private final static String LABEL_CLOUDS = "clouds";
    private final static String LABEL_WINDSPEED = "wind_speed";

    private final static String LABEL_ICON = "icon";

    private final static String LABEL_RAIN = "rain";
    private final static String LABEL_RAIN_INFO = "1h";

    private final static String LABEL_CURRENT = "current";
    private final static String LABEL_HOURLY = "hourly";
    private final static String LABEL_DATETIME = "dt";
    private final static String LABEL_TIMEZONE_OFFSET = "timezone_offset";

    private final static String LABEL_PRESSURE = "pressure";
    private final static String LABEL_HUMIDITY = "humidity";

    private static Weather parseMeasurement(JSONObject weatherJson) throws JSONException {
        JSONObject weatherObject = weatherJson.getJSONArray(LABEL_WEATHER).getJSONObject(0);
        Weather weather = new Weather(
                DateUtils.getDateFromMillis(weatherJson.getLong(LABEL_DATETIME)),
                weatherObject.getString(LABEL_WEATHER_DESCRIPTION),
                weatherJson.getDouble(LABEL_TEMPERATURE),
                weatherJson.getInt(LABEL_PRESSURE),
                weatherJson.getInt(LABEL_HUMIDITY),
                weatherJson.getInt(LABEL_CLOUDS),
                weatherJson.getDouble(LABEL_WINDSPEED),
                weatherObject.getString(LABEL_ICON)
        );
        if (weatherJson.has(LABEL_RAIN)) {
            JSONObject rain = weatherJson.getJSONObject(LABEL_RAIN);
            if(!rain.toString().equals("{}"))
                weather.setRain(rain.getDouble(LABEL_RAIN_INFO));
        }
        return weather;
    }

    public static WeatherCollection parseApiResponse(String jsonStr) throws JSONException {
        JSONObject forecastJson = new JSONObject(jsonStr);
        JSONArray hourlyWeatherArray = forecastJson.getJSONArray(LABEL_HOURLY);
        DateUtils.TIMEZONE_OFFSET = forecastJson.getInt(LABEL_TIMEZONE_OFFSET);

        Weather currentWeather = parseMeasurement(forecastJson.getJSONObject(LABEL_CURRENT));
        List<Weather> resultHourlyWeather = new ArrayList<>();
        for (int i = 0; i < hourlyWeatherArray.length(); ++i) {
            resultHourlyWeather.add(parseMeasurement(hourlyWeatherArray.getJSONObject(i)));
        }
        Log.v(LOG_TAG, "JSON weather data parsed to Weather objects.");
        return new WeatherCollection(currentWeather, resultHourlyWeather);
    }
}
