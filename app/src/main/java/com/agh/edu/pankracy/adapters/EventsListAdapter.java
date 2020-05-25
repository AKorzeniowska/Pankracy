package com.agh.edu.pankracy.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.agh.edu.pankracy.R;
import com.github.sundeepk.compactcalendarview.domain.Event;

import java.util.ArrayList;

public class EventsListAdapter extends BaseAdapter {
    private Context context; //context
    private ArrayList<Event> items; //data source of the list adapter

    //public constructor 
    public EventsListAdapter(Context context, ArrayList<Event> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size(); //returns total of items in the list
    }

    @Override
    public Object getItem(int position) {
        return items.get(position); //returns list item at the specified position
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // inflate the layout for each list row
        if (convertView == null) {
            convertView = LayoutInflater.from(context).
                    inflate(R.layout.layout_list_view_row_events, parent, false);
        }

        // get current item to be displayed
        Event currentItem = (Event) getItem(position);

        // get the TextView for item name and item description
        TextView textViewItemName = convertView.findViewById(R.id.text_view_event_name);
//        TextView textViewItemDescription = (TextView)
//                convertView.findViewById(R.id.text_view_item_description);

        //sets the text for item name and item description from the current item object
        textViewItemName.setText(currentItem.toString());
//        textViewItemDescription.setText(currentItem.getItemDescription());

        // returns the view for the current row
        return convertView;
    }
}