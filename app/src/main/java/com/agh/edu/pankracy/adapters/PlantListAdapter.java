package com.agh.edu.pankracy.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.agh.edu.pankracy.R;

import java.util.LinkedHashMap;
import java.util.List;

public class PlantListAdapter extends BaseAdapter {

    private Context context;
    private LinkedHashMap<Integer, String> mData;
    private Integer[] mKeys;

    public PlantListAdapter(Context context, LinkedHashMap<Integer, String> items) {
        this.context = context;
        this.mData = items;
        mKeys = mData.keySet().toArray(new Integer[items.size()]);
    }

    private class ViewHolder {
        ImageView icon;
        TextView title;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public String getItem(int position) {
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
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.title.setText(getItem(position));
        holder.icon.setImageResource(R.drawable.ic_local_florist_black_24dp);

        return convertView;
    }
}
