package com.agh.edu.pankracy.data.plants;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "plants")
public class Plant {
    @DatabaseField(columnName = "id", generatedId = true)
    private long id;
    @DatabaseField(columnName = "name", canBeNull = false)
    private String name;
    @DatabaseField(columnName = "species", canBeNull = false)
    private String species;
    @DatabaseField(columnName = "watering", canBeNull = false)
    private int watering;
    @DatabaseField(columnName = "min_temp", canBeNull = false)
    private int minTemp;
    @DatabaseField(columnName = "last_watering", canBeNull = false)
    private String lastWatering;
    @DatabaseField(columnName = "is_outside", canBeNull = false)
    private int isOutside;

    public Plant(long id, String name, String species, int watering, int minTemp, String lastWatering, int isOutside) {
        this.id = id;
        this.name = name;
        this.species = species;
        this.watering = watering;
        this.minTemp = minTemp;
        this.lastWatering = lastWatering;
        this.isOutside = isOutside;
    }

    public Plant(String name, String species, int watering, int minTemp, String lastWatering, int isOutside) {
        this.name = name;
        this.species = species;
        this.watering = watering;
        this.minTemp = minTemp;
        this.lastWatering = lastWatering;
        this.isOutside = isOutside;
    }

    public Plant() {
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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
