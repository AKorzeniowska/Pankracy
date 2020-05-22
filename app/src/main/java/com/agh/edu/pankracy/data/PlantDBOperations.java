package com.agh.edu.pankracy.data;

import android.content.Context;
import android.database.Cursor;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.agh.edu.pankracy.utils.DateUtils;

public class PlantDBOperations {
    public static Map<Integer, String> getPlantsToWaterAtDate(Date date, Context context){
       Map<Integer, String> plantsToWater = new HashMap<>();

        String[] projection = {PlantContract._ID, PlantContract.COLUMN_NAME, PlantContract.COLUMN_SPECIES, PlantContract.COLUMN_WATERING, PlantContract.COLUMN_LAST_WATERING};
        Cursor cursor = context.getContentResolver().query(PlantContract.CONTENT_URI, projection, null, null, null);

        int idColumnIndex = cursor.getColumnIndex(PlantContract._ID);
        int nameColumnIndex = cursor.getColumnIndex(PlantContract.COLUMN_NAME);
        int speciesColumnIndex = cursor.getColumnIndex(PlantContract.COLUMN_SPECIES);
        int wateringColumnIndex = cursor.getColumnIndex(PlantContract.COLUMN_WATERING);
        int lastWateringColumnIndex = cursor.getColumnIndex(PlantContract.COLUMN_LAST_WATERING);

        Integer id;
        String nameOrSpecies;
        Integer wateringFrequency;
        Date lastWatering = null;

        while (cursor.moveToNext()) {
            id = cursor.getInt(idColumnIndex);
            nameOrSpecies = cursor.getString(nameColumnIndex);

            if (nameOrSpecies.equals("")) {
                nameOrSpecies = cursor.getString(speciesColumnIndex);
            }
            wateringFrequency = cursor.getInt(wateringColumnIndex);
            try {
                lastWatering = DateUtils.sdf.parse(cursor.getString(lastWateringColumnIndex));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (lastWatering != null && DateUtils.getNumberOfDaysBetweenGivenDateAndNextWateringMyGodThatsALongAssMethodName(date, lastWatering, wateringFrequency) < 0){
                plantsToWater.put(id, nameOrSpecies);
            }

        }
        cursor.close();
        return plantsToWater;
    }

    public static Map<Integer, String> getPlantsThatWouldGetCold(int temperature, Context context){
        Map<Integer, String> plants = new HashMap<>();

        String[] projection = {PlantContract._ID, PlantContract.COLUMN_NAME, PlantContract.COLUMN_SPECIES, PlantContract.COLUMN_MIN_TEMP};
        Cursor cursor = context.getContentResolver().query(PlantContract.CONTENT_URI, projection, null, null, null);

        int idColumnIndex = cursor.getColumnIndex(PlantContract._ID);
        int nameColumnIndex = cursor.getColumnIndex(PlantContract.COLUMN_NAME);
        int speciesColumnIndex = cursor.getColumnIndex(PlantContract.COLUMN_SPECIES);
        int minTempColumnIndex = cursor.getColumnIndex(PlantContract.COLUMN_MIN_TEMP);

        Integer id;
        String nameOrSpecies;
        Integer minTemp;

        while (cursor.moveToNext()) {
            id = cursor.getInt(idColumnIndex);
            nameOrSpecies = cursor.getString(nameColumnIndex);

            if (nameOrSpecies.equals("")) {
                nameOrSpecies = cursor.getString(speciesColumnIndex);
            }
            minTemp = cursor.getInt(minTempColumnIndex);
            if (minTemp > temperature){
                plants.put(id, nameOrSpecies);
            }

        }
        cursor.close();
        return plants;
    }
}
