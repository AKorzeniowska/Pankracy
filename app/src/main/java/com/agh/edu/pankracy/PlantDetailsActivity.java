package com.agh.edu.pankracy;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.agh.edu.pankracy.data.plants.DBHelper;
import com.agh.edu.pankracy.data.plants.Plant;
import com.agh.edu.pankracy.utils.DateUtils;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;

public class PlantDetailsActivity extends AppCompatActivity {

    private static final String FINAL_PLANT_ID = "final_plant_id";
    private DBHelper dbHelper;

    private Integer id;
    private Plant plant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant_details);
        dbHelper = new DBHelper(this);
        id = getIntent().getIntExtra(FINAL_PLANT_ID, 0);
        dataGetter();

        // Toolbar
        Toolbar toolbar = findViewById(R.id.my_toolbar);
        TextView title = toolbar.findViewById(R.id.toolbar_title);
        title.setText(plant.getName());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.plantdetails_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(Activity.RESULT_CANCELED);
                finish();
                return true;
            case R.id.plantdetails_edit:
                editPlant();
                return true;
            case R.id.plantdetails_delete:
                deletePlantClick();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        dataGetter();
//        dataSetter();
    }

    protected void dataGetter (){

        try {
            plant = dbHelper.getById(id);
            TextView nameText = (TextView) findViewById(R.id.plant_name);
            TextView speciesText = (TextView) findViewById(R.id.plant_species);
            TextView wateringText = (TextView) findViewById(R.id.watering_freq);
            TextView minTempText = (TextView) findViewById(R.id.min_temp);
            TextView lastWateringText = (TextView) findViewById(R.id.last_watering);
            TextView isOutsideText = (TextView) findViewById(R.id.is_outside);
            ImageView plantIcon = findViewById(R.id.avatar);

            nameText.setText(plant.getName());
            speciesText.setText(plant.getSpecies());
            wateringText.setText(String.valueOf(plant.getWatering()));
            lastWateringText.setText(String.valueOf(plant.getLastWatering()));
            minTempText.setText(String.valueOf(plant.getMinTemp()));
            isOutsideText.setText(plant.getIsOutside() == 1 ? "Yes" : "No");

            Date lastWatering = DateUtils.sdf.parse(plant.getLastWatering());

            if (lastWatering != null && DateUtils.getNumberOfDaysBetweenGivenDateAndNextWatering(new Date(), lastWatering, plant.getWatering()) < 0) {
                //changeColor
                plantIcon.setColorFilter(Color.argb(255, 219, 164, 164));
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }


    public void editPlant () {
        Intent editPlantIntent = new Intent(PlantDetailsActivity.this, PlantFormActivity.class);
        editPlantIntent.putExtra(FINAL_PLANT_ID, id);
        startActivityForResult(editPlantIntent, 10002);
    }

    public void deletePlantClick () {
        showDeleteConfirmationDialog();
    }

    private void deletePlant (){
        try {
            dbHelper.deleteById((long)id);
            Toast.makeText(this, "Plant has been deleted successfully", Toast.LENGTH_SHORT).show();
        } catch (SQLException e) {
            Toast.makeText(this, "Deleting plant failed", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        setResult(Activity.RESULT_OK);
        finish();
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.dialog_layout, null));

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deletePlant();
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)     {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10002) {
            setResult(resultCode);
            if(resultCode == Activity.RESULT_OK)
                finish();
        }
    }
}
