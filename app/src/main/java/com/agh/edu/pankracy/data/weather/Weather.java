package com.agh.edu.pankracy.data.weather;

import java.util.Date;

public class Weather {
    private Date date;
    private String description;
    private double temperature;
    private int cloudiness;
    private double windSpeed;
    private double rain;

    private int pressure;
    private int humidity;

    public Weather(Date date, String description, double temperature, int pressure, int humidity, int cloudiness, double windSpeed) {
        this.date = date;
        this.description = description;
        this.temperature = temperature;
        this.pressure = pressure;
        this.humidity = humidity;
        this.cloudiness = cloudiness;
        this.windSpeed = windSpeed;
    }

    public Weather(Date date, String description, double temperature, int pressure, int humidity, int cloudiness, double windSpeed, double rain) {
        this(date, description, temperature, pressure, humidity, cloudiness, windSpeed);
        this.rain = rain;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public int getPressure() {
        return pressure;
    }

    public void setPressure(int pressure) {
        this.pressure = pressure;
    }

    public int getCloudiness() {
        return cloudiness;
    }

    public void setCloudiness(int cloudiness) {
        this.cloudiness = cloudiness;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(double windSpeed) {
        this.windSpeed = windSpeed;
    }

    public double getRain() {
        return rain;
    }

    public void setRain(double rain) {
        this.rain = rain;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Weather{" + '\n' +
                "date=" + date + '\n' +
                ", description='" + description + '\'' + '\n' +
                ", temperature=" + temperature + " °C" + '\n' +
                ", cloudiness=" + cloudiness + "%" + '\n' +
                ", windSpeed=" + windSpeed + " m/s" + '\n' +
                ", rain=" + rain + '\n' +
                ", pressure=" + pressure + " hPa" + '\n' +
                ", humidity=" + humidity + "%" + '\n' +
                '}';
    }
}
