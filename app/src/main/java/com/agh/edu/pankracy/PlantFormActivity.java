package com.agh.edu.pankracy;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.agh.edu.pankracy.data.PlantContract;
import com.agh.edu.pankracy.data.PlantDBHelper;

import java.util.Calendar;

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
    TextView lastWateringText;

    private DatePickerDialog.OnDateSetListener datePickerListener;

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

        setDatePickerOpener();

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
        String [] projection = {PlantContract.COLUMN_NAME,
                PlantContract.COLUMN_SPECIES,
                PlantContract.COLUMN_WATERING,
                PlantContract.COLUMN_MIN_TEMP,
                PlantContract.COLUMN_LAST_WATERING
        };
        String selection = PlantContract._ID + "=?";
        String [] selectionArgs = {String.valueOf(id)};

        Cursor cursor = getContentResolver().query(PlantContract.CONTENT_URI_ID(id), projection, selection, selectionArgs, null);

        int nameColumnIndex = cursor.getColumnIndex(PlantContract.COLUMN_NAME);
        int speciesColumnIndex = cursor.getColumnIndex(PlantContract.COLUMN_SPECIES);
        int wateringColumnIndex = cursor.getColumnIndex(PlantContract.COLUMN_WATERING);
        int minTempColumnIndex = cursor.getColumnIndex(PlantContract.COLUMN_MIN_TEMP);
        int lastWateringColumnIndex  = cursor.getColumnIndex(PlantContract.COLUMN_LAST_WATERING);

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
        String species = speciesText.getText().toString();
        String watering = wateringText.getText().toString();
        String minTemp = minTempText.getText().toString();
        String lastWatering = lastWateringText.getText().toString();

        if (name.isEmpty() || species.isEmpty() || watering.isEmpty() || minTemp.isEmpty() || lastWatering.isEmpty()){
            Toast.makeText(this, "Missing data!", Toast.LENGTH_SHORT).show();
            return;
        }

        int wateringInt = Integer.parseInt(watering);
        int minTempInt = Integer.parseInt(minTemp);

        ContentValues values = new ContentValues();
        values.put(PlantContract.COLUMN_NAME, name);
        values.put(PlantContract.COLUMN_SPECIES, species);
        values.put(PlantContract.COLUMN_WATERING, wateringInt);
        values.put(PlantContract.COLUMN_MIN_TEMP, minTempInt);
        values.put(PlantContract.COLUMN_LAST_WATERING, lastWatering);

        if (this.id == 0) {
            Uri newUri = getContentResolver().insert(PlantContract.CONTENT_URI, values);
            if (newUri == null) {
                Toast.makeText(this, "Adding plant failed", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Plant has been added successfully", Toast.LENGTH_SHORT).show();
                finish();
            }
        }

        else{
            int rows = getContentResolver().update(PlantContract.CONTENT_URI_ID(id), values, null, null);
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

    public void setDatePickerOpener() {
        datePickerListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                String monthText = month+"";
                if (month < 10)
                    monthText = "0" + monthText;
                lastWateringText.setText(dayOfMonth + "." + monthText + "." + year);
            }
        };

        lastWateringText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        PlantFormActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        datePickerListener,
                        year, month, day);

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });


    }
}
