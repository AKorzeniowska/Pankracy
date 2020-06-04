package com.agh.edu.pankracy.fragments;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.agh.edu.pankracy.PlantDetailsActivity;
import com.agh.edu.pankracy.PlantFormActivity;
import com.agh.edu.pankracy.R;
import com.agh.edu.pankracy.adapters.PlantListAdapter;
import com.agh.edu.pankracy.data.PlantContract;
import com.agh.edu.pankracy.data.PlantDBHelper;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ListOfPlantsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListOfPlantsFragment extends Fragment {
    private static final String LOG_TAG = ListOfPlantsFragment.class.getSimpleName();

    private PlantDBHelper mDbHelper;
    private LinkedHashMap<Integer, String> listViewData = new LinkedHashMap<>();
    private ArrayList<Integer> idList = new ArrayList<>();
    private final static String FINAL_PLANT_ID = "final_plant_id";
    private ListView listView;
    PlantListAdapter adapter;

    public ListOfPlantsFragment() {
        // Required empty public constructor
    }

    public static ListOfPlantsFragment newInstance() {
        return new ListOfPlantsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDbHelper = new PlantDBHelper(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_of_plants, container, false);
        setHasOptionsMenu(true);
        listView = view.findViewById(R.id.plants_list);

        listGetter();
        chosenPlantIntentCreator();
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.plantlist_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.plantlist_add :
                openNewPlantForm();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void listGetter(){
        listViewData.clear();
        String [] projection = {PlantContract.PlantEntry._ID, PlantContract.PlantEntry.COLUMN_NAME, PlantContract.PlantEntry.COLUMN_SPECIES};
        Cursor cursor = getActivity().getContentResolver().query(PlantContract.PlantEntry.CONTENT_URI, projection, null, null, null);

        int nameColumnIndex = cursor.getColumnIndex(PlantContract.PlantEntry.COLUMN_NAME);
        int speciesColumnIndex = cursor.getColumnIndex(PlantContract.PlantEntry.COLUMN_SPECIES);
        int idColumnIndex = cursor.getColumnIndex(PlantContract.PlantEntry._ID);

        while (cursor.moveToNext()){
            String currentName = cursor.getString(nameColumnIndex);
            Integer currentId = Integer.parseInt(cursor.getString(idColumnIndex));
            if (currentName.equals("")){
                currentName = cursor.getString(speciesColumnIndex);
            }
            listViewData.put(currentId, currentName);
        }
        cursor.close();

        adapter = new PlantListAdapter(getActivity(), listViewData);
        listView.setAdapter(adapter);

        adapter.notifyDataSetChanged();
    }

    private void chosenPlantIntentCreator() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent chosenPlantIntent = new Intent(getActivity(), PlantDetailsActivity.class);
                chosenPlantIntent.putExtra(FINAL_PLANT_ID, (int) adapter.getItemId(position));
                startActivityForResult(chosenPlantIntent, 10001);
            }
        });
    }

    private void openNewPlantForm() {
        Intent editPlantIntent = new Intent(getActivity(), PlantFormActivity.class);
        editPlantIntent.putExtra(FINAL_PLANT_ID, 0);
        startActivityForResult(editPlantIntent, 10001);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10001) {
            if(resultCode == Activity.RESULT_OK) {
                Log.v(LOG_TAG, "Result OK");
                listGetter();
                chosenPlantIntentCreator();
            }
        }
    }
}
