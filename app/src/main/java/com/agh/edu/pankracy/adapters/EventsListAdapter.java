package com.agh.edu.pankracy.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.agh.edu.pankracy.R;
import com.agh.edu.pankracy.models.CalendarEvent;
import com.github.sundeepk.compactcalendarview.domain.Event;

import java.util.ArrayList;

public class EventsListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Event> items;

    public EventsListAdapter(Context context, ArrayList<Event> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).
                    inflate(R.layout.layout_list_view_row_events, parent, false);
        }

        Event currentItem = (Event) getItem(position);
        TextView textViewItemName = convertView.findViewById(R.id.text_view_event_name);
        textViewItemName.setText(((CalendarEvent)currentItem.getData()).getMessage());
        return convertView;
    }
}