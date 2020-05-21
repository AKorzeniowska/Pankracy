package com.agh.edu.pankracy.data.weather;

import java.util.List;

public class WeatherCollection {
    private Weather currentWeather;
    private List<Weather> hourlyWeather;

    public WeatherCollection(Weather currentWeather, List<Weather> hourlyWeather) {
        this.currentWeather = currentWeather;
        this.hourlyWeather = hourlyWeather;
    }


    public Weather getCurrentWeather() {
        return currentWeather;
    }

    public void setCurrentWeather(Weather currentWeather) {
        this.currentWeather = currentWeather;
    }

    public List<Weather> getHourlyWeather() {
        return hourlyWeather;
    }

    public void setHourlyWeather(List<Weather> hourlyWeather) {
        this.hourlyWeather = hourlyWeather;
    }


    @Override
    public String toString() {
        return "WeatherCollection{" + '\n' +
                "currentWeather=" + currentWeather + '\n' +
                ", hourlyWeather=" + hourlyWeather + '\n' +
                '}';
    }
}
