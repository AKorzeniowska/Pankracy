package com.agh.edu.pankracy.adapters;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.agh.edu.pankracy.R;
import com.agh.edu.pankracy.data.PlantContract;
import com.agh.edu.pankracy.data.plants.Plant;
import com.agh.edu.pankracy.fragments.ListOfPlantsFragment;
import com.agh.edu.pankracy.utils.DateUtils;

import java.text.ParseException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

public class PlantListAdapter extends BaseAdapter {

    private Context context;
    private LinkedHashMap<Integer, Plant> mData;
    private Integer[] mKeys;
    private ListOfPlantsFragment lopFragment;

    public PlantListAdapter(Context context, LinkedHashMap<Integer, Plant> items,
                            ListOfPlantsFragment lopFragment) {
        this.context = context;
        this.mData = items;
        mKeys = mData.keySet().toArray(new Integer[items.size()]);
        this.lopFragment = lopFragment;
    }

    private class ViewHolder {
        ImageView icon;
        ImageView waterIcon;
        ImageView isOutsideIcon;
        TextView title;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Plant getItem(int position) {
        return mData.get(mKeys[position]);
    }

    @Override
    public long getItemId(int position) {
        return mKeys[position];
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        LayoutInflater mInflater = (LayoutInflater)
                context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item_plant, null);
            holder = new ViewHolder();
            holder.title = convertView.findViewById(R.id.title);
            holder.icon = convertView.findViewById(R.id.icon);
            holder.waterIcon = convertView.findViewById(R.id.water_it_icon);
            holder.isOutsideIcon = convertView.findViewById(R.id.is_outside_icon);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }
        Plant plant = getItem(position);
        holder.title.setText(plant.getName());

        try {
            if (plant.getLastWatering() != null && DateUtils.getNumberOfDaysBetweenGivenDateAndNextWateringMyGodThatsALongAssMethodName(new Date(), DateUtils.sdf.parse(plant.getLastWatering()), plant.getWatering()) < 0){
                //changeColor
                holder.icon.setColorFilter(ContextCompat.getColor(context, R.color.colorRedDimmed));
                holder.waterIcon.setColorFilter(ContextCompat.getColor(context, R.color.colorRedDimmed));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(plant.getIsOutside() == 0) {
            holder.isOutsideIcon.setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary));
        }

        holder.isOutsideIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues values = new ContentValues();
                values.put(PlantContract.COLUMN_IS_OUTSIDE, plant.getIsOutside() == 0 ? 1 : 0);
                context.getContentResolver().update(PlantContract.CONTENT_URI_ID((int) getItemId(position)), values, null, null);
                lopFragment.listGetter();
                lopFragment.chosenPlantIntentCreator();
            }
        });

        holder.waterIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues values = new ContentValues();
                values.put(PlantContract.COLUMN_LAST_WATERING, DateUtils.sdf.format(new Date()));
                context.getContentResolver().update(PlantContract.CONTENT_URI_ID((int) getItemId(position)), values, null, null);
                lopFragment.listGetter();
                lopFragment.chosenPlantIntentCreator();
            }
        });

        return convertView;
    }
}
