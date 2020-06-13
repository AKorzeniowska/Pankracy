package com.agh.edu.pankracy.fragments;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.agh.edu.pankracy.CalendarActivity;
import com.agh.edu.pankracy.MainActivity;
import com.agh.edu.pankracy.R;
import com.agh.edu.pankracy.adapters.EventsListAdapter;
import com.agh.edu.pankracy.data.PlantContract;
import com.agh.edu.pankracy.data.weather.Weather;
import com.agh.edu.pankracy.models.CalendarEvent;
import com.agh.edu.pankracy.utils.DateUtils;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CalendarFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CalendarFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private int wateringColor = Color.YELLOW;
    private int forgotToWaterColor = Color.RED;
    private int lowTemperatureColor = Color.WHITE;
    private int windyColor = Color.GREEN;

    private static final double WIND_SPEED_THRESHOLD = 1.0;

    private CompactCalendarView compactCalendar;
    private ArrayList<Event> selectedDayEvents = new ArrayList<>();
    private ListView eventsList;
    private EventsListAdapter eventsListAdapter;
    private SimpleDateFormat dateFormatMonth = new SimpleDateFormat("MMMM yyyy", Locale.ENGLISH);//Locale.getDefault());

    public CalendarFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CalendarFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CalendarFragment newInstance(String param1, String param2) {
        CalendarFragment fragment = new CalendarFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_calendar,
                container, false);

        eventsList = view.findViewById(R.id.calendar_list_view);
        eventsListAdapter = new EventsListAdapter(getActivity(), selectedDayEvents);
        eventsList.setAdapter(eventsListAdapter);

        compactCalendar = view.findViewById(R.id.compactcalendar_view);
        compactCalendar.setUseThreeLetterAbbreviation(true);

        setViewAndListener(view);

        try {
            getData();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return view;
    }

    private void setViewAndListener(View view){
        Toolbar toolbar = view.findViewById(R.id.my_toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        final ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
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
                Context context = getActivity();
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
        compactCalendar.removeAllEvents();

        Date today = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(today);
        Double todayTemp = getMinimalTemperatureForDate(today);
        Double todayWind = getMaximalWindSpeedForDate(today);
        if (todayWind != null && todayWind > WIND_SPEED_THRESHOLD){
        }

        c.add(Calendar.DATE, 1);
        Date tomorrow = c.getTime();
        Double tomorrowTemp = getMinimalTemperatureForDate(tomorrow);
        Double tomorrowWind = getMaximalWindSpeedForDate(tomorrow);
        if (tomorrowWind != null && tomorrowWind > WIND_SPEED_THRESHOLD){
            CalendarEvent calendarEvent = new CalendarEvent(CalendarEvent.WINDY);
            Event event = new Event(windyColor, tomorrow.getTime(), calendarEvent);
            compactCalendar.addEvent(event);
        }

        c.add(Calendar.DATE, 1);
        Date inTwoDays = c.getTime();
        Double inTwoDaysTemp = getMinimalTemperatureForDate(inTwoDays);
        Double inTwoDaysWind = getMaximalWindSpeedForDate(inTwoDays);
        if (inTwoDaysWind != null && inTwoDaysWind > WIND_SPEED_THRESHOLD){
        }

        String [] projection = {PlantContract._ID, PlantContract.COLUMN_NAME, PlantContract.COLUMN_SPECIES, PlantContract.COLUMN_LAST_WATERING, PlantContract.COLUMN_WATERING, PlantContract.COLUMN_MIN_TEMP, PlantContract.COLUMN_IS_OUTSIDE};
        Cursor cursor = getActivity().getContentResolver().query(PlantContract.CONTENT_URI, projection, null, null, null);

        int nameColumnIndex = cursor.getColumnIndex(PlantContract.COLUMN_NAME);
        int speciesColumnIndex = cursor.getColumnIndex(PlantContract.COLUMN_SPECIES);
        int idColumnIndex = cursor.getColumnIndex(PlantContract._ID);
        int wateringColumnIndex = cursor.getColumnIndex(PlantContract.COLUMN_WATERING);
        int lastWateringColumnIndex = cursor.getColumnIndex(PlantContract.COLUMN_LAST_WATERING);
        int minTemperatureColumnIndex = cursor.getColumnIndex(PlantContract.COLUMN_MIN_TEMP);
        int isOutsideColumnIndex = cursor.getColumnIndex(PlantContract.COLUMN_IS_OUTSIDE);

        while (cursor.moveToNext()){
            String currentName = cursor.getString(nameColumnIndex);
            int currentId = cursor.getInt(idColumnIndex);
            Date lastWatering = DateUtils.sdf.parse(cursor.getString(lastWateringColumnIndex));
            int wateringFrequency = cursor.getInt(wateringColumnIndex);
            int minTemperature = cursor.getInt(minTemperatureColumnIndex);
            boolean isOutside = cursor.getInt(isOutsideColumnIndex) == 1;
            if (currentName.equals("")){
                currentName = cursor.getString(speciesColumnIndex);
            }

            Event event;
            Date nextWateringDate = DateUtils.getNextWateringDate(lastWatering, wateringFrequency);

            if (nextWateringDate.before(new Date())) {
                CalendarEvent calendarEvent = new CalendarEvent(currentName, currentId, CalendarEvent.WATERING);
                event = new Event(forgotToWaterColor, nextWateringDate.getTime(), calendarEvent);
            }
            else {
                CalendarEvent calendarEvent = new CalendarEvent(currentName, currentId, CalendarEvent.FORGOT_TO_WATER);
                event = new Event(wateringColor, nextWateringDate.getTime(), calendarEvent);
            }
            compactCalendar.addEvent(event);


            if (isOutside) {
                if (todayTemp != null && todayTemp <= minTemperature) {
                    CalendarEvent calendarEvent = new CalendarEvent(currentName, currentId, CalendarEvent.TOO_COLD);
                    Event tempEvent = new Event(lowTemperatureColor, today.getTime(), calendarEvent);
                    compactCalendar.addEvent(tempEvent);
                }

                if (tomorrowTemp != null && tomorrowTemp <= minTemperature) {
                    CalendarEvent calendarEvent = new CalendarEvent(currentName, currentId, CalendarEvent.TOO_COLD);
                    Event tempEvent = new Event(lowTemperatureColor, tomorrow.getTime(), calendarEvent);
                    compactCalendar.addEvent(tempEvent);
                }

                if (inTwoDaysTemp != null && inTwoDaysTemp <= minTemperature) {
                    CalendarEvent calendarEvent = new CalendarEvent(currentName, currentId, CalendarEvent.TOO_COLD);
                    Event tempEvent = new Event(lowTemperatureColor, inTwoDays.getTime(), calendarEvent);
                    compactCalendar.addEvent(tempEvent);
                }
            }
        }
        cursor.close();

        eventsListAdapter.notifyDataSetChanged();
    }

    private Double getMinimalTemperatureForDate(Date date){
        if(MainActivity.WEATHER_COLLECTION != null) {
            List<Weather> weather = MainActivity.WEATHER_COLLECTION.getHourlyWeather();
            List<Weather> todayWeather = weather.stream().filter(x -> x.getDate().getDate() == date.getDate())
                    .collect(Collectors.toList());
            if (todayWeather.isEmpty())
                return null;
            return todayWeather.stream().min(Comparator.comparing(Weather::getTemperature))
                    .get().getTemperature();
        }
        return null;
    }

    private Double getMaximalWindSpeedForDate(Date date){
        if(MainActivity.WEATHER_COLLECTION != null) {
            List<Weather> weather = MainActivity.WEATHER_COLLECTION.getHourlyWeather();
            List<Weather> todayWeather = weather.stream().filter(x -> x.getDate().getDate() == date.getDate())
                    .collect(Collectors.toList());
            if (todayWeather.isEmpty())
                return null;
            return todayWeather.stream().max(Comparator.comparing(Weather::getWindSpeed))
                    .get().getWindSpeed();
        }
        return null;
    }

    private void showFinishEventDialog(Event event, final int position) {
        final CalendarEvent calendarEvent = (CalendarEvent)event.getData();
        if (calendarEvent.getEventType() == CalendarEvent.WINDY)
            return;

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        View dialog = inflater.inflate(R.layout.dialog_layout, null);
        builder.setView(dialog);
        ((TextView)dialog.findViewById(R.id.dialog_title)).setText("Hey");

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
                    getActivity().getContentResolver().update(PlantContract.CONTENT_URI_ID(calendarEvent.getPlantId()), values, null, null);
                }
                else if (calendarEvent.getEventType() == CalendarEvent.TOO_COLD){
                    ContentValues values = new ContentValues();
                    values.put(PlantContract.COLUMN_IS_OUTSIDE, 0);
                    getActivity().getContentResolver().update(PlantContract.CONTENT_URI_ID(calendarEvent.getPlantId()), values, null, null);
                }
                try {
                    getData();
                } catch (ParseException e) {
                    e.printStackTrace();
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
}
