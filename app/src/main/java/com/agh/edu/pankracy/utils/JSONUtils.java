package com.agh.edu.pankracy.utils;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONUtils {
    private static final String LOG_TAG = JSONUtils.class.getSimpleName();

    /* Parameters names */
    // TODO: Discuss which parameters we want for Pankracy
    // Example: https://api.openweathermap.org/data/2.5/onecall?lang=pl&exclude=daily,minutely&units=metric&lat=49.817225&lon=19.222505&appid=57550cef4c674dce68c6ef7b557d4d90
    private final static String LABEL_WEATHER = "weather";
    private final static String LABEL_WEATHER_DESCRIPTION = "description";

    private final static String LABEL_TEMPERATURE = "temp";
    private final static String LABEL_PRESSURE = "pressure";
    private final static String LABEL_HUMIDITY = "humidity";
    private final static String LABEL_CLOUDS = "clouds";
    private final static String LABEL_WINDSPEED = "wind_speed";

    private final static String LABEL_RAIN = "rain";
    private final static String LABEL_RAIN_INFO = "1h";

    private final static String LABEL_CURRENT = "current";
    private final static String LABEL_HOURLY = "hourly";
    private final static String LABEL_DATETIME = "dt";

    // Returns current weather as the first array element; hourly for the next elements
    public static String[] parseToStrings(Context context, String jsonStr) throws JSONException {
        String[] parsedWeatherData = null; // Result String array

        JSONObject forecastJson = new JSONObject(jsonStr);
        JSONArray hourlyWeatherArray = forecastJson.getJSONArray(LABEL_HOURLY);
        parsedWeatherData = new String[hourlyWeatherArray.length()+1];
        parsedWeatherData[0] = parseWeather(forecastJson.getJSONObject(LABEL_CURRENT));
        for(int i = 1; i < parsedWeatherData.length; ++i) {
            parsedWeatherData[i] = parseWeather(hourlyWeatherArray.getJSONObject(i-1));
        }

        Log.v(LOG_TAG, "JSON weather data parsed to string array.");
        return parsedWeatherData;
    }

    private static String parseWeather(JSONObject weatherJson)
            throws JSONException {
        String result = DateUtils.getFormattedDate(weatherJson.getLong(LABEL_DATETIME)) + '\n';
        result += weatherJson.getJSONArray(LABEL_WEATHER)
                .getJSONObject(0)
                .getString(LABEL_WEATHER_DESCRIPTION)
                .toUpperCase() + '\n';
        result += "Temperature: " + weatherJson.getString(LABEL_TEMPERATURE) + "°C\n";
        result += "Pressure: " + weatherJson.getInt(LABEL_PRESSURE) + " hPa\n";
        result += "Humidity: " + weatherJson.getInt(LABEL_HUMIDITY) + "%\n";
        result += "Cloudiness: " + weatherJson.getInt(LABEL_CLOUDS) + "%\n";
        result += "Wind speed: " + weatherJson.getDouble(LABEL_WINDSPEED) + "m/s\n";
        if(weatherJson.has(LABEL_RAIN)) {
            result += "Rain in 1h: " + weatherJson.getJSONObject(LABEL_RAIN).getString(LABEL_RAIN_INFO);
        }
        return result;
    }
}
