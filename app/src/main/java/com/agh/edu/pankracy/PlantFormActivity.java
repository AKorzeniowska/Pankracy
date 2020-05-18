package com.agh.edu.pankracy;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.EditText;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant_form);
        mDbHelper = new PlantDBHelper(this);
        id = getIntent().getIntExtra(FINAL_PLANT_ID, 0);
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
        EditText nameText = (EditText) findViewById(R.id.name_edit);
        EditText speciesText = (EditText) findViewById(R.id.species_edit);
        EditText wateringText = (EditText) findViewById(R.id.watering_edit);
        EditText minTempText = (EditText) findViewById(R.id.watering_edit);
        EditText lastWateringText = (EditText) findViewById(R.id.last_watering_edit);
        EditText minTemp = (EditText) findViewById(R.id.min_temp_edit) ;

        nameText.setText(name);
        speciesText.setText(species);
        //wateringText.setText(getString(R.string.how_often_text, watering));
        wateringText.setText(watering.toString());
        lastWateringText.setText(lastWatering);
        //minTempText.setText(getString(R.string.higher_than_temp_text, minTemperature));
        minTempText.setText(minTemperature.toString());

    }

}
