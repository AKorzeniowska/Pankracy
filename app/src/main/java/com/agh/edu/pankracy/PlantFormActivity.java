package com.agh.edu.pankracy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.EditText;
import android.widget.Toast;

import com.agh.edu.pankracy.data.PlantContract;
import com.agh.edu.pankracy.data.PlantContract.PlantEntry;
import com.agh.edu.pankracy.data.PlantDBHelper;

public class PlantFormActivity extends AppCompatActivity {
    private static final String FINAL_PLANT_ID = "final_plant_id";
    PlantDBHelper mDbHelper;

    private Integer id;
    private String name;
    private String species;
    private Integer watering;
    private Integer minTemperature;
    private String lastWatering;

    EditText nameText;
    EditText speciesText;
    EditText wateringText;
    EditText minTempText;
    EditText lastWateringText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant_form);
        mDbHelper = new PlantDBHelper(this);
        id = getIntent().getIntExtra(FINAL_PLANT_ID, 0);

        nameText = (EditText) findViewById(R.id.name_edit);
        speciesText = (EditText) findViewById(R.id.species_edit);
        wateringText = (EditText) findViewById(R.id.watering_edit);
        minTempText = (EditText) findViewById(R.id.min_temp_edit);
        lastWateringText = (EditText) findViewById(R.id.last_watering_edit);

        if (id != 0) {
            dataGetter();
            dataSetter();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (id != 0) {
            dataGetter();
            dataSetter();
        }
    }

    protected void dataGetter (){
        String [] projection = {PlantEntry.COLUMN_NAME,
                PlantEntry.COLUMN_SPECIES,
                PlantEntry.COLUMN_WATERING,
                PlantEntry.COLUMN_MIN_TEMP,
                PlantEntry.COLUMN_LAST_WATERING
        };
        String selection = PlantEntry._ID + "=?";
        String [] selectionArgs = {String.valueOf(id)};

        Cursor cursor = getContentResolver().query(PlantEntry.CONTENT_URI_ID(id), projection, selection, selectionArgs, null);

        int nameColumnIndex = cursor.getColumnIndex(PlantEntry.COLUMN_NAME);
        int speciesColumnIndex = cursor.getColumnIndex(PlantEntry.COLUMN_SPECIES);
        int wateringColumnIndex = cursor.getColumnIndex(PlantEntry.COLUMN_WATERING);
        int minTempColumnIndex = cursor.getColumnIndex(PlantEntry.COLUMN_MIN_TEMP);
        int lastWateringColumnIndex  = cursor.getColumnIndex(PlantEntry.COLUMN_LAST_WATERING);

        while (cursor.moveToNext()){
            name = cursor.getString(nameColumnIndex);
            species = cursor.getString(speciesColumnIndex);
            watering = cursor.getInt(wateringColumnIndex);
            minTemperature = cursor.getInt(minTempColumnIndex);
            lastWatering = cursor.getString(lastWateringColumnIndex);
        }
        cursor.close();
    }

    private void dataSetter (){
        nameText.setText(name);
        speciesText.setText(species);
        wateringText.setText(watering.toString());
        lastWateringText.setText(lastWatering);
        minTempText.setText(minTemperature.toString());

    }

    public void save_form(View view) {
        String name = nameText.getText().toString();
        String species = nameText.getText().toString();
        String watering = wateringText.getText().toString();
        String minTemp = minTempText.getText().toString();
        String lastWatering = lastWateringText.getText().toString();

        int wateringInt = Integer.parseInt(watering);
        int minTempInt = Integer.parseInt(minTemp);

        ContentValues values = new ContentValues();
        values.put(PlantEntry.COLUMN_NAME, name);
        values.put(PlantEntry.COLUMN_SPECIES, species);
        values.put(PlantEntry.COLUMN_WATERING, wateringInt);
        values.put(PlantEntry.COLUMN_MIN_TEMP, minTempInt);
        values.put(PlantEntry.COLUMN_LAST_WATERING, lastWatering);

        if (this.id == 0) {
            Uri newUri = getContentResolver().insert(PlantEntry.CONTENT_URI, values);
            if (newUri == null) {
                Toast.makeText(this, "Adding plant failed", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Plant has been added successfully", Toast.LENGTH_SHORT).show();
                finish();
            }
        }

        else{
            int rows = getContentResolver().update(PlantEntry.CONTENT_URI_ID(id), values, null, null);
            if (rows == 0){
                Toast.makeText(this, "Saving updated plant failed", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(this, "Plant has been updated successfully", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    public void cancel_form(View view) {
        finish();
    }
}
