package com.jaykit.minimal.api;

public class Forecast {
    public int time;
    public String summary;
    public String icon;
    public int uvIndex;
    public double temperature;
    public double humidity;
    public double windSpeed;

    public Forecast() {

    }

    public Forecast(int time, String summary, String icon, int uvIndex, double temperature, double humidity, double windSpeed) {
        this.time = time;
        this.summary = summary;
        this.icon = icon;
        this.uvIndex = uvIndex;
        this.temperature = temperature;
        this.humidity = humidity;
        this.windSpeed = windSpeed;
    }

    public int getTime() {
        return time;
    }

    public String getSummary() {
        return summary;
    }

    public String getIcon() {
        return icon;
    }

    public int getUvIndex() {
        return uvIndex;
    }

    public double getTemperature() {
        return temperature;
    }

    public double getHumidity() {
        return humidity;
    }

    public double getWindSpeed() {
        return windSpeed;
    }
}
