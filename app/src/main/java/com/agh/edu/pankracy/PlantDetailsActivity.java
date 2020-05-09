package com.agh.edu.pankracy;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.agh.edu.pankracy.data.PlantContract;
import com.agh.edu.pankracy.data.PlantContract.PlantEntry;
import com.agh.edu.pankracy.data.PlantDBHelper;

public class PlantDetailsActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_plant_details);
        mDbHelper = new PlantDBHelper(this);
        id = getIntent().getIntExtra(FINAL_PLANT_ID, 0);
        dataGetter();
        dataSetter();
    }

    @Override
    protected void onResume() {
        super.onResume();
        dataGetter();
        dataSetter();
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
        TextView nameText = (TextView) findViewById(R.id.plant_name);
        TextView speciesText = (TextView) findViewById(R.id.plant_species);
        TextView wateringText = (TextView) findViewById(R.id.watering_freq);
        TextView minTempText = (TextView) findViewById(R.id.min_temp);
        TextView lastWateringText = (TextView) findViewById(R.id.last_watering);

        nameText.setText(name);
        speciesText.setText(species);
        //wateringText.setText(getString(R.string.how_often_text, watering));
        wateringText.setText(watering.toString());
        lastWateringText.setText(lastWatering);
        //minTempText.setText(getString(R.string.higher_than_temp_text, minTemperature));
        minTempText.setText(minTemperature.toString());
    }

//    public void editPlant (View view) {
//        Intent editPlantIntent = new Intent(PlantDetailsActivity.this, EditPlantActivity.class);
//        editPlantIntent.putExtra(FINAL_PLANT_ID, id);
//        startActivity(editPlantIntent);
//        finish();
//    }

    public void deletePlantClick (View view) {
        showDeleteConfirmationDialog();
    }

    private void deletePlant (){
        int rows = getContentResolver().delete(PlantEntry.CONTENT_URI_ID(id), null, null);
        if (rows == 0){
            Toast.makeText(this, "Deleting plant failed", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this, "Plant has been deleted successfully", Toast.LENGTH_SHORT).show();
        }
        Intent plantListIntent = new Intent(PlantDetailsActivity.this, PlantListActivity.class);
        startActivity(plantListIntent);
        finish();
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.dialog_layout, null));

        builder.setPositiveButton("Usuń", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deletePlant();
            }
        });
        builder.setNegativeButton("anuluj", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
