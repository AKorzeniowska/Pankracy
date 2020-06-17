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

import com.agh.edu.pankracy.data.plants.DBHelper;
import com.agh.edu.pankracy.data.plants.Plant;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.Calendar;

public class PlantFormActivity extends AppCompatActivity {
    private static final String FINAL_PLANT_ID = "final_plant_id";
    private DBHelper dbHelper;

    private Integer id;
    private Plant plant;
    private Boolean isOutside;

    private EditText nameText;
    private EditText speciesText;
    private EditText wateringText;
    private EditText minTempText;
    private TextView lastWateringText;
    private RadioButton isOutsideTrue;
    private RadioButton isOutsideFalse;

    private DatePickerDialog.OnDateSetListener datePickerListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant_form);
        dbHelper = new DBHelper(this);
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
        }
    }

    protected void dataGetter (){
        try {
            plant = dbHelper.getById(id);
            nameText.setText(plant.getName());
            speciesText.setText(plant.getSpecies());
            wateringText.setText(String.valueOf(plant.getWatering()));
            lastWateringText.setText(plant.getLastWatering());
            minTempText.setText(String.valueOf(plant.getMinTemp()));
            if (plant.getIsOutside() == 1){
                isOutsideTrue.setChecked(true);
                isOutsideFalse.setChecked(false);
            } else {
                isOutsideTrue.setChecked(false);
                isOutsideFalse.setChecked(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void save_form() {
        String name = nameText.getText().toString();
        String species = speciesText.getText().toString();
        String watering = wateringText.getText().toString();
        String minTemp = minTempText.getText().toString();
        String lastWatering = lastWateringText.getText().toString();

        if (name.isEmpty() || species.isEmpty() || watering.isEmpty() || minTemp.isEmpty() || lastWatering.isEmpty() || (!isOutsideFalse.isChecked() && !isOutsideTrue.isChecked())){
            Toast.makeText(this, "Missing data!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (plant == null)
            plant = new Plant();
        plant.setName(name);
        plant.setSpecies(species);
        plant.setWatering(Integer.parseInt(watering));
        plant.setMinTemp(Integer.parseInt(minTemp));
        plant.setLastWatering(lastWatering);
        if (isOutside == null)
            isOutside = isOutsideTrue.isChecked();
        plant.setIsOutside(isOutside ? 1 : 0);

        Dao.CreateOrUpdateStatus result = null;

        try{
            result = dbHelper.createOrUpdate(plant);
        } catch (SQLException e){
            System.out.println("unable to add plant");
        }

        if (result.isCreated()){
            Toast.makeText(this, "Plant has been created successfully", Toast.LENGTH_SHORT).show();
            setResult(Activity.RESULT_OK);
            finish();
        }
        else if (result.isUpdated()){
            Toast.makeText(this, "Plant has been updated successfully", Toast.LENGTH_SHORT).show();
            setResult(Activity.RESULT_OK);
            finish();
        }
        else{
            Toast.makeText(this, "Saving updated plant failed", Toast.LENGTH_SHORT).show();
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
