package com.agh.edu.pankracy;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
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
    private Boolean isOutside;

    EditText nameText;
    EditText speciesText;
    EditText wateringText;
    EditText minTempText;
    TextView lastWateringText;
    RadioButton isOutsideTrue;
    RadioButton isOutsideFalse;

    private DatePickerDialog.OnDateSetListener datePickerListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant_form);
        mDbHelper = new PlantDBHelper(this);
        id = getIntent().getIntExtra(FINAL_PLANT_ID, 0);

        nameText = findViewById(R.id.name_edit_text);
        speciesText = findViewById(R.id.species_edit_text);
        wateringText = findViewById(R.id.watering_edit_text);
        minTempText = findViewById(R.id.min_temp_edit_text);
        lastWateringText = findViewById(R.id.last_watering_edit_text);
        isOutsideTrue = findViewById(R.id.radio_outside);
        isOutsideFalse = findViewById(R.id.radio_inside);

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setDatePickerOpener();

        if (id != 0) {
            dataGetter();
            dataSetter();
            TextView title = toolbar.findViewById(R.id.toolbar_title);
            title.setText(R.string.edit_the_plant);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.solo_confirm, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            cancel_form();
        }
        else if(item.getItemId() == R.id.add) {
            save_form();
        }
        return super.onOptionsItemSelected(item);
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
                PlantContract.COLUMN_LAST_WATERING,
                PlantContract.COLUMN_IS_OUTSIDE
        };
        String selection = PlantContract._ID + "=?";
        String [] selectionArgs = {String.valueOf(id)};

        Cursor cursor = getContentResolver().query(PlantContract.CONTENT_URI_ID(id), projection, selection, selectionArgs, null);

        int nameColumnIndex = cursor.getColumnIndex(PlantContract.COLUMN_NAME);
        int speciesColumnIndex = cursor.getColumnIndex(PlantContract.COLUMN_SPECIES);
        int wateringColumnIndex = cursor.getColumnIndex(PlantContract.COLUMN_WATERING);
        int minTempColumnIndex = cursor.getColumnIndex(PlantContract.COLUMN_MIN_TEMP);
        int lastWateringColumnIndex  = cursor.getColumnIndex(PlantContract.COLUMN_LAST_WATERING);
        int isOutsideColumnIndex = cursor.getColumnIndex(PlantContract.COLUMN_IS_OUTSIDE);

        while (cursor.moveToNext()){
            name = cursor.getString(nameColumnIndex);
            species = cursor.getString(speciesColumnIndex);
            watering = cursor.getInt(wateringColumnIndex);
            minTemperature = cursor.getInt(minTempColumnIndex);
            lastWatering = cursor.getString(lastWateringColumnIndex);
            isOutside = cursor.getInt(isOutsideColumnIndex) == 1;
        }
        cursor.close();
    }

    private void dataSetter (){
        nameText.setText(name);
        speciesText.setText(species);
        wateringText.setText(watering.toString());
        lastWateringText.setText(lastWatering);
        minTempText.setText(minTemperature.toString());
        if (isOutside){
            isOutsideTrue.setChecked(true);
            isOutsideFalse.setChecked(false);
        } else {
            isOutsideTrue.setChecked(false);
            isOutsideFalse.setChecked(true);
        }

    }

    public void save_form() {
        String name = nameText.getText().toString();
        String species = speciesText.getText().toString();
        String watering = wateringText.getText().toString();
        String minTemp = minTempText.getText().toString();
        String lastWatering = lastWateringText.getText().toString();

        if (name.isEmpty() || species.isEmpty() || watering.isEmpty() || minTemp.isEmpty() || lastWatering.isEmpty() || isOutside == null){
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
        values.put(PlantContract.COLUMN_IS_OUTSIDE, isOutside ? 1 : 0);

        if (this.id == 0) {
            Uri newUri = getContentResolver().insert(PlantContract.CONTENT_URI, values);
            if (newUri == null) {
                Toast.makeText(this, "Adding plant failed", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Plant has been added successfully", Toast.LENGTH_SHORT).show();
                setResult(Activity.RESULT_OK);
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
                setResult(Activity.RESULT_OK);
                finish();
            }
        }
    }

    public void cancel_form() {
        setResult(Activity.RESULT_CANCELED);
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

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        switch(view.getId()) {
            case R.id.radio_inside:
                if (checked)
                    isOutside = false;
                    break;
            case R.id.radio_outside:
                if (checked)
                    isOutside = true;
                    break;
        }

    }
}
