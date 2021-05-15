package com.jaykit.minimal.api;
import com.loopj.android.http.*;

public class Darksky {
    public final static String API_KEY = "b79f77b723cbd0322d9ea222d1f95cb5";
    public final static String BASE_URL = "https://api.forecast.io/forecast/";

    // Location data
    public double latitude;
    public double longitude;

    private final AsyncHttpClient client;

    public Darksky(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        client = new AsyncHttpClient();
    }

    public void getCurrentForecast(AsyncHttpResponseHandler handler) {
        client.get(BASE_URL + API_KEY + "/" + latitude + "," + longitude + "?exclude=hourly,exclude=minutely,exclude=daily,exclude=flags", null, handler);
    }

}
