package com.agh.edu.pankracy;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.agh.edu.pankracy.adapters.EventsListAdapter;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class CalendarActivity extends AppCompatActivity {

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

        Event evencik = new Event(Color.BLUE, 1590503738000L, "Podlej se roslinke");
        Event evencik2 = new Event(Color.YELLOW, 1590503738000L, "Nawiez se roslinke");
        compactCalendar.addEvent(evencik);
        compactCalendar.addEvent(evencik2);
    }

    private void setViewAndListener(){
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setTitle("test");
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
}
