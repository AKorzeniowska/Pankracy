package com.agh.edu.pankracy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.agh.edu.pankracy.data.PlantDBHelper;
import com.agh.edu.pankracy.data.PlantContract.PlantEntry;

import java.util.ArrayList;

public class PlantListActivity extends AppCompatActivity {
    private PlantDBHelper mDbHelper;
    private ArrayList<String> listViewData = new ArrayList<>();
    private ArrayList<Integer> idList = new ArrayList<>();
    private final static String FINAL_PLANT_ID = "final_plant_id";
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant_list);

        mDbHelper = new PlantDBHelper(this);
        listView = (ListView) findViewById(R.id.plants_listview);

        listGetter();
        chosenPlantIntentCreator();
    }

    @Override
    protected void onStart() {
        super.onStart();
        listGetter();
    }

    @Override
    protected void onResume() {
        super.onResume();
        listGetter();
    }

    private void listGetter(){
        listViewData.clear();
        String [] projection = {PlantEntry._ID, PlantEntry.COLUMN_NAME, PlantEntry.COLUMN_SPECIES};
        Cursor cursor = getContentResolver().query(PlantEntry.CONTENT_URI, projection, null, null, null);

        int nameColumnIndex = cursor.getColumnIndex(PlantEntry.COLUMN_NAME);
        int speciesColumnIndex = cursor.getColumnIndex(PlantEntry.COLUMN_SPECIES);
        int idColumnIndex = cursor.getColumnIndex(PlantEntry._ID);

        while (cursor.moveToNext()){
            String currentName = cursor.getString(nameColumnIndex);
            Integer currentId = Integer.parseInt(cursor.getString(idColumnIndex));
            if (currentName.equals("")){
                currentName = cursor.getString(speciesColumnIndex);
            }
            listViewData.add(currentName);
            idList.add(currentId);
        }
        cursor.close();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.listview_row_layout, listViewData);
        adapter.notifyDataSetChanged();
        listView.setAdapter(adapter);
    }

    private void chosenPlantIntentCreator(){

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent chosenPlantIntent = new Intent(PlantListActivity.this, PlantDetailsActivity.class);
                chosenPlantIntent.putExtra(FINAL_PLANT_ID, idList.get(position));
                startActivity(chosenPlantIntent);
            }
        });
    }

    public void openNewPlantForm(View view) {
        Intent editPlantIntent = new Intent(PlantListActivity.this, PlantFormActivity.class);
        editPlantIntent.putExtra(FINAL_PLANT_ID, 0);
        startActivity(editPlantIntent);
    }
}
