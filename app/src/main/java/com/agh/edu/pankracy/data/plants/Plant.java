package com.agh.edu.pankracy.data.plants;

public class Plant {
    String name;
    String species;
    int watering;
    int minTemp;
    String lastWatering;
    int isOutside;

    public Plant(String name, String species, int watering, int minTemp, String lastWatering, int isOutside) {
        this.name = name;
        this.species = species;
        this.watering = watering;
        this.minTemp = minTemp;
        this.lastWatering = lastWatering;
        this.isOutside = isOutside;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public int getWatering() {
        return watering;
    }

    public void setWatering(int watering) {
        this.watering = watering;
    }

    public int getMinTemp() {
        return minTemp;
    }

    public void setMinTemp(int minTemp) {
        this.minTemp = minTemp;
    }

    public String getLastWatering() {
        return lastWatering;
    }

    public void setLastWatering(String lastWatering) {
        this.lastWatering = lastWatering;
    }

    public int getIsOutside() {
        return isOutside;
    }

    public void setIsOutside(int isOutside) {
        this.isOutside = isOutside;
    }
}
