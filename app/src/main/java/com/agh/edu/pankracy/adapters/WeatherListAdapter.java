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
import com.agh.edu.pankracy.data.weather.Weather;

import org.w3c.dom.Text;
import android.text.format.DateFormat;

import java.util.List;

public class WeatherListAdapter extends BaseAdapter {

    private Context context;
    private List<Weather> mData;

    public WeatherListAdapter(Context context, List<Weather> weatherList) {
        this.context = context;
        this.mData = weatherList;
    }

    private class ViewHolder {
        ImageView icon;
        TextView time;
        TextView humidity;
        TextView wind;
        TextView temperature;
        TextView rain;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Weather getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mData.indexOf(getItem(position));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        LayoutInflater mInflater = (LayoutInflater)
                context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item_weather, null);
            holder = new ViewHolder();
            holder.time = convertView.findViewById(R.id.list_weather_time);
            holder.icon = convertView.findViewById(R.id.list_weather_icon);
            holder.humidity = convertView.findViewById(R.id.list_weather_humidity);
            holder.wind = convertView.findViewById(R.id.list_weather_wind);
            holder.temperature = convertView.findViewById(R.id.list_weather_temperature);
            holder.rain = convertView.findViewById(R.id.list_weather_rain);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }
        Weather weather = getItem(position);

        String day = (String) DateFormat.format("dd",   weather.getDate());
        String month = (String) DateFormat.format("MM",   weather.getDate());
        String hour = (String) DateFormat.format("HH", weather.getDate());
        String minutes = (String) DateFormat.format("mm", weather.getDate());

        String time = day + "." + month + System.getProperty ("line.separator") + hour + ":" + minutes;
        String temperature = weather.getTemperature() + "°C";
        String wind = weather.getWindSpeed() + "km/h";
        String humidity = weather.getHumidity() + "%";
        String rain = weather.getRain() + "mm";

        holder.time.setText(time);
        holder.temperature.setText(temperature);
        holder.wind.setText(wind);
        holder.humidity.setText(humidity);
        holder.rain.setText(rain);

        Context context = holder.icon.getContext();
        int iconId = context.getResources().getIdentifier(
                "weather_icon_" + weather.getIcon(),
                "drawable",
                context.getPackageName()
        );
        holder.icon.setImageResource(iconId);

        return convertView;
    }
}
