package com.agh.edu.pankracy.utils;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class NetworkUtils {

    private static final String LOG_TAG = NetworkUtils.class.getSimpleName();

    private final static String FORECAST_BASE_URL =
            "https://api.openweathermap.org/data/2.5/onecall";

    /* Parameters values */
    private final static String API_KEY = "57550cef4c674dce68c6ef7b557d4d90";
    private final static String lang = "en";
    private static final String format = "json";
    private static final String units = "metric";
    private static final String excluded = "daily,minutely";

    /* Parameters names */
    private final static String APPID_PARAM = "appid";
    private final static String LAT_PARAM = "lat";
    private final static String LON_PARAM = "lon";
    private final static String LANG_PARAM = "lang";
    private final static String FORMAT_PARAM = "mode";
    private final static String UNITS_PARAM = "units";
    private final static String EXCLUDE_PARAM = "exclude";


    /**
     * Builds the URL to call the weather server using coordinates.
     *
     * @param lat The latitude of the location
     * @param lon The longitude of the location
     * @return The Url to use to query the weather server.
     */
    public static URL buildUrl(Double lat, Double lon) {
        Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                .appendQueryParameter(APPID_PARAM, API_KEY)
                .appendQueryParameter(LANG_PARAM, lang)
                .appendQueryParameter(FORMAT_PARAM, format)
                .appendQueryParameter(UNITS_PARAM, units)
                .appendQueryParameter(EXCLUDE_PARAM, excluded)
                .appendQueryParameter(LON_PARAM, String.valueOf(lon))
                .appendQueryParameter(LAT_PARAM, String.valueOf(lat))
                .build();

        try {
            URL url = new URL(builtUri.toString());
            Log.v(LOG_TAG, "Built URI " + url);
            return url;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * This method returns the result from HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            String response = null;
            if (hasInput) {
                response = scanner.next();
            }
            scanner.close();
            return response;
        } finally {
            urlConnection.disconnect();
        }
    }

}
