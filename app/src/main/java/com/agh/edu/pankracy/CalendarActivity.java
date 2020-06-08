package com.agh.edu.pankracy;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.agh.edu.pankracy.adapters.EventsListAdapter;
import com.agh.edu.pankracy.data.PlantContract;
import com.agh.edu.pankracy.models.CalendarEvent;
import com.agh.edu.pankracy.notifications.NotificationUtils;
import com.agh.edu.pankracy.utils.DateUtils;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CalendarActivity extends AppCompatActivity {

    private int wateringColor = Color.YELLOW;
    private int forgotToWaterColor = Color.RED;
    private int lowTemperature = Color.BLUE;

    CompactCalendarView compactCalendar;
    ArrayList<Event> selectedDayEvents = new ArrayList<>();
    ListView eventsList;
    EventsListAdapter eventsListAdapter;
    private SimpleDateFormat dateFormatMonth = new SimpleDateFormat("MMMM yyyy", Locale.ENGLISH);//Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        eventsList = findViewById(R.id.calendar_list_view);
        eventsListAdapter = new EventsListAdapter(getApplicationContext(), selectedDayEvents);
        eventsList.setAdapter(eventsListAdapter);

        compactCalendar = findViewById(R.id.compactcalendar_view);
        compactCalendar.setUseThreeLetterAbbreviation(true);

        setViewAndListener();

        try {
            getData();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void setViewAndListener(){
        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setTitle(dateFormatMonth.format(Calendar.getInstance().getTime()));

        eventsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Event clicked = selectedDayEvents.get(position);
                showFinishEventDialog(clicked, position);
            }
        });

        compactCalendar.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                Context context = getApplicationContext();
                selectedDayEvents.clear();

                if (compactCalendar.getEvents(dateClicked).isEmpty())
                    Toast.makeText(context, "No events", Toast.LENGTH_SHORT).show();

                selectedDayEvents.addAll(compactCalendar.getEvents(dateClicked));

                eventsListAdapter.notifyDataSetChanged();

            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                actionBar.setTitle(dateFormatMonth.format(firstDayOfNewMonth));
            }
        });
    }

    private void getData() throws ParseException {
        String [] projection = {PlantContract._ID, PlantContract.COLUMN_NAME, PlantContract.COLUMN_SPECIES, PlantContract.COLUMN_LAST_WATERING, PlantContract.COLUMN_WATERING};
        Cursor cursor = getContentResolver().query(PlantContract.CONTENT_URI, projection, null, null, null);

        int nameColumnIndex = cursor.getColumnIndex(PlantContract.COLUMN_NAME);
        int speciesColumnIndex = cursor.getColumnIndex(PlantContract.COLUMN_SPECIES);
        int idColumnIndex = cursor.getColumnIndex(PlantContract._ID);
        int wateringColumnIndex = cursor.getColumnIndex(PlantContract.COLUMN_WATERING);
        int lastWateringColumnIndex = cursor.getColumnIndex(PlantContract.COLUMN_LAST_WATERING);

        while (cursor.moveToNext()){
            String currentName = cursor.getString(nameColumnIndex);
            int currentId = cursor.getInt(idColumnIndex);
            Date lastWatering = DateUtils.sdf.parse(cursor.getString(lastWateringColumnIndex));
            int wateringFrequency = cursor.getInt(wateringColumnIndex);
            if (currentName.equals("")){
                currentName = cursor.getString(speciesColumnIndex);
            }

            Event event;
            Date nextWateringDate = DateUtils.getNextWateringDate(lastWatering, wateringFrequency);

            if (nextWateringDate.before(new Date())) {
                CalendarEvent calendarEvent = new CalendarEvent(currentName, currentId, CalendarEvent.WATERING);
                event = new Event(forgotToWaterColor, nextWateringDate.getTime(), calendarEvent);//currentName + ": you forgot to water me!");
            }
            else {
                CalendarEvent calendarEvent = new CalendarEvent(currentName, currentId, CalendarEvent.FORGOT_TO_WATER);
                event = new Event(wateringColor, nextWateringDate.getTime(), calendarEvent);// currentName + ": remember to water me");
            }
            compactCalendar.addEvent(event);
        }
        cursor.close();
        eventsListAdapter.notifyDataSetChanged();
    }

    private void showFinishEventDialog(Event event, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialog = inflater.inflate(R.layout.dialog_layout, null);
        builder.setView(dialog);
        ((TextView)dialog.findViewById(R.id.dialog_title)).setText("Hmm");
        final CalendarEvent calendarEvent = (CalendarEvent)event.getData();
        if(calendarEvent.getEventType() == CalendarEvent.FORGOT_TO_WATER || calendarEvent.getEventType() == CalendarEvent.WATERING)
            ((TextView)dialog.findViewById(R.id.dialog_message)).setText("Have you already watered " + calendarEvent.getPlantName() + "?");
        else if (calendarEvent.getEventType() == CalendarEvent.TOO_COLD)
            ((TextView)dialog.findViewById(R.id.dialog_message)).setText("Have you taken " + calendarEvent.getPlantName() + " inside?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                selectedDayEvents.remove(position);
                eventsListAdapter.notifyDataSetChanged();
                if (calendarEvent.getEventType() == CalendarEvent.FORGOT_TO_WATER || calendarEvent.getEventType() == CalendarEvent.WATERING){
                    String today = DateUtils.sdf.format(new Date());
                    ContentValues values = new ContentValues();
                    values.put(PlantContract.COLUMN_LAST_WATERING, today);
                    getContentResolver().update(PlantContract.CONTENT_URI_ID(calendarEvent.getPlantId()), values, null, null);

                    try {
                        getData();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        builder.setNegativeButton("Not yet", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    public void updateWateringDate(int id) {
        String today = DateUtils.sdf.format(new Date());

        ContentValues values = new ContentValues();
        values.put(PlantContract.COLUMN_LAST_WATERING, today);

        getContentResolver().update(PlantContract.CONTENT_URI_ID(id), values, null, null);
    }
}
