package com.agh.edu.pankracy.models;

public class CalendarEvent {
    public static final int WATERING = 1;
    public static final int FORGOT_TO_WATER = 2;
    public static final int TOO_COLD = 3;

    private String plantName;
    private int plantId;
    private int eventType;

    public CalendarEvent(String plantName, int plantId, int eventType) {
        this.plantName = plantName;
        this.plantId = plantId;
        this.eventType = eventType;
    }

    public String getPlantName() {
        return plantName;
    }

    public void setPlantName(String plantName) {
        this.plantName = plantName;
    }

    public int getPlantId() {
        return plantId;
    }

    public void setPlantId(int plantId) {
        this.plantId = plantId;
    }

    public int getEventType() {
        return eventType;
    }

    public void setEventType(int eventType) {
        this.eventType = eventType;
    }

    public String getMessage(){
        switch (this.eventType){
            case FORGOT_TO_WATER:
                return this.plantName + ": don't forget to water me.";
            case WATERING:
                return this.plantName + ": you forgot to water me! Hurry up!";
            case TOO_COLD:
                return this.plantName + ": it's getting cold, take me back inside.";
            default:
                return "";
        }
    }
}
