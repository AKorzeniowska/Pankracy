package com.agh.edu.pankracy.data;

import android.content.Context;
import android.database.Cursor;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.agh.edu.pankracy.data.PlantContract.PlantEntry;
import com.agh.edu.pankracy.utils.DateUtils;

public class PlantDBOperations {
    public static Map<Integer, String> getPlantsToWaterAtDate(Date date, Context context){
       Map<Integer, String> plantsToWater = new HashMap<>();

        String[] projection = {PlantEntry._ID, PlantEntry.COLUMN_NAME, PlantEntry.COLUMN_SPECIES, PlantEntry.COLUMN_WATERING, PlantEntry.COLUMN_LAST_WATERING};
        Cursor cursor = context.getContentResolver().query(PlantEntry.CONTENT_URI, projection, null, null, null);

        int idColumnIndex = cursor.getColumnIndex(PlantEntry._ID);
        int nameColumnIndex = cursor.getColumnIndex(PlantEntry.COLUMN_NAME);
        int speciesColumnIndex = cursor.getColumnIndex(PlantEntry.COLUMN_SPECIES);
        int wateringColumnIndex = cursor.getColumnIndex(PlantEntry.COLUMN_WATERING);
        int lastWateringColumnIndex = cursor.getColumnIndex(PlantEntry.COLUMN_LAST_WATERING);

        Integer id;
        Integer nextWatering;
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
}
