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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.agh.edu.pankracy.MainActivity;
import com.agh.edu.pankracy.R;
import com.agh.edu.pankracy.adapters.EventsListAdapter;
import com.agh.edu.pankracy.data.plants.DBHelper;
import com.agh.edu.pankracy.data.plants.Plant;
import com.agh.edu.pankracy.data.weather.Weather;
import com.agh.edu.pankracy.models.CalendarEvent;
import com.agh.edu.pankracy.utils.DateUtils;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;

import java.sql.SQLException;
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

    private DBHelper dbHelper;

    public CalendarFragment() {
    }

    public static CalendarFragment newInstance() {
        return new CalendarFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        dbHelper = new DBHelper(getActivity());
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
        TextView month = view.findViewById(R.id.month_name);
        month.setText(dateFormatMonth.format(Calendar.getInstance().getTime()));

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
                month.setText(dateFormatMonth.format(firstDayOfNewMonth));
            }
        });
    }

    private void setWindEvent(Date date) throws ParseException{
        Double todayWind = getMaximalWindSpeedForDate(date);
        if (todayWind != null && todayWind > WIND_SPEED_THRESHOLD){
            CalendarEvent calendarEvent = new CalendarEvent(CalendarEvent.WINDY);
            Event event = new Event(windyColor, date.getTime(), calendarEvent);
            compactCalendar.addEvent(event);
        }
    }

    private void setTemperatureEvent(Plant plant, Double temperature, Date date){
        if (temperature != null && temperature <= plant.getMinTemp()) {
            CalendarEvent calendarEvent = new CalendarEvent(plant.getName(), (int)plant.getId(), CalendarEvent.TOO_COLD);
            Event tempEvent = new Event(lowTemperatureColor, date.getTime(), calendarEvent);
            compactCalendar.addEvent(tempEvent);
        }
    }

    private void getData() throws ParseException {
        compactCalendar.removeAllEvents();

        Date today = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(today);
        Double todayTemp = getMinimalTemperatureForDate(today);
        setWindEvent(today);

        c.add(Calendar.DATE, 1);
        Date tomorrow = c.getTime();
        Double tomorrowTemp = getMinimalTemperatureForDate(tomorrow);
        setWindEvent(tomorrow);

        c.add(Calendar.DATE, 1);
        Date inTwoDays = c.getTime();
        Double inTwoDaysTemp = getMinimalTemperatureForDate(inTwoDays);
        setWindEvent(inTwoDays);

        try {
            List<Plant> plants = dbHelper.getAll();
            for (Plant plant : plants){
                Date lastWatering = DateUtils.sdf.parse(plant.getLastWatering());
                Event event;
                Date nextWateringDate = DateUtils.getNextWateringDate(lastWatering, plant.getWatering());

                if (nextWateringDate.after(today)) {
                    CalendarEvent calendarEvent = new CalendarEvent(plant.getName(), (int)plant.getId(), CalendarEvent.FORGOT_TO_WATER);
                    event = new Event(wateringColor, nextWateringDate.getTime(), calendarEvent);
                }
                else {
                    CalendarEvent calendarEvent = new CalendarEvent(plant.getName(), (int)plant.getId(), CalendarEvent.WATERING);
                    event = new Event(forgotToWaterColor, nextWateringDate.getTime(), calendarEvent);
                }
                compactCalendar.addEvent(event);

                if (plant.getIsOutside() == 1) {
                    setTemperatureEvent(plant, todayTemp, today);
                    setTemperatureEvent(plant, tomorrowTemp, tomorrow);
                    setTemperatureEvent(plant, inTwoDaysTemp, inTwoDays);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

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
                positiveButtonLogic(calendarEvent);
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

    private void positiveButtonLogic(CalendarEvent calendarEvent){
        if (calendarEvent.getEventType() == CalendarEvent.FORGOT_TO_WATER || calendarEvent.getEventType() == CalendarEvent.WATERING){
            String today = DateUtils.sdf.format(new Date());
            try {
                Plant plant = dbHelper.getById(calendarEvent.getPlantId());
                plant.setLastWatering(today);
                dbHelper.createOrUpdate(plant);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else if (calendarEvent.getEventType() == CalendarEvent.TOO_COLD){
            try {
                Plant plant = dbHelper.getById(calendarEvent.getPlantId());
                plant.setIsOutside(0);
                dbHelper.createOrUpdate(plant);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        try {
            getData();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
