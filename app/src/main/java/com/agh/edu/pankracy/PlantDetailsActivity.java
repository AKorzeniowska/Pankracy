package com.agh.edu.pankracy;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.agh.edu.pankracy.data.PlantContract;
import com.agh.edu.pankracy.data.PlantDBHelper;
import com.agh.edu.pankracy.utils.DateUtils;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.util.Date;

public class PlantDetailsActivity extends AppCompatActivity {

    private static final String FINAL_PLANT_ID = "final_plant_id";
    PlantDBHelper mDbHelper;

    private Integer id;
    private String name;
    private String species;
    private Integer watering;
    private Integer minTemperature;
    private Date lastWatering = null;
    private String lastWateringText;
    private Boolean isOutside;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant_details);
        mDbHelper = new PlantDBHelper(this);
        id = getIntent().getIntExtra(FINAL_PLANT_ID, 0);
        dataGetter();
        dataSetter();

        // Toolbar
        Toolbar toolbar = findViewById(R.id.my_toolbar);
        TextView title = toolbar.findViewById(R.id.toolbar_title);
        title.setText(name);
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
        dataSetter();
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
            lastWateringText = cursor.getString(lastWateringColumnIndex);
            isOutside = cursor.getInt(isOutsideColumnIndex) == 1;
            try {
                lastWatering = DateUtils.sdf.parse(lastWateringText);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        cursor.close();
    }

    private void dataSetter (){
        TextView nameText = (TextView) findViewById(R.id.plant_name);
        TextView speciesText = (TextView) findViewById(R.id.plant_species);
        TextView wateringText = (TextView) findViewById(R.id.watering_freq);
        TextView minTempText = (TextView) findViewById(R.id.min_temp);
        TextView lastWateringText = (TextView) findViewById(R.id.last_watering);
        ImageView plantIcon = findViewById(R.id.avatar);

        nameText.setText(name);
        speciesText.setText(species);
        //wateringText.setText(getString(R.string.how_often_text, watering));
        wateringText.setText(watering.toString());
        lastWateringText.setText(this.lastWateringText);
        //minTempText.setText(getString(R.string.higher_than_temp_text, minTemperature));
        minTempText.setText(minTemperature.toString());
        if (lastWatering != null && DateUtils.getNumberOfDaysBetweenGivenDateAndNextWateringMyGodThatsALongAssMethodName(new Date(), lastWatering, watering) < 0){
            //changeColor
            plantIcon.setColorFilter(Color.argb(255, 219, 164, 164));
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
        int rows = getContentResolver().delete(PlantContract.CONTENT_URI_ID(id), null, null);
        if (rows == 0){
            Toast.makeText(this, "Deleting plant failed", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this, "Plant has been deleted successfully", Toast.LENGTH_SHORT).show();
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
