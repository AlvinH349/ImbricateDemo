package com.imbricateDemo.calandertest2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;


public class DailyWeatherAdapter extends ArrayAdapter<Weather> {

    private Context context;
    private List<Weather> weatherList;

    public DailyWeatherAdapter(@NonNull Context context,@NonNull List<Weather> weatherList) {
        super(context, 0, weatherList);
        this.context = context;
        this.weatherList = weatherList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        convertView = LayoutInflater.from(context).inflate(R.layout.item_weather,parent,false);

        TextView tvDate = convertView.findViewById(R.id.tvDate);
        TextView tvTemp = convertView.findViewById(R.id.tvTemp);
        ImageView iconWeather = convertView.findViewById(R.id.iconWeather);

        Weather weather = weatherList.get(position);
        tvTemp.setText(weather.getTemp() + " C");



        String conditionN = weather.getIcon();
        String conditionName = updateWeatherIcon2(conditionN);
      //  int resourceID=getResources().getIdentifier(conditionName ,"drawable",getPackageName());
       // int resourceID = 0;
      //  iconWeather.setImageResource(resourceID);
        iconWeather.setImageResource(R.drawable.sun1);

        Date date  = new Date(weather.getDate()*1000);
        DateFormat dateFormat = new SimpleDateFormat("EEE,MMM yy", Locale.ENGLISH);
        dateFormat.setTimeZone(TimeZone.getTimeZone(weather.getTimeZone()));
        tvDate.setText(dateFormat.format(date));



        return convertView;
    }



    private static String updateWeatherIcon2(String condition)
    {
        if(condition.equals("Thunderstorm"))
        {
            return "thunderstrom1";
        }
        else if( condition.equals("Drizzle") ||  condition.equals("Rain") ){
            return "lightrain";
        }
        else if( condition.equals("Snow") ){
            return "snow1";
        }
        else if( condition.equals("Fog") ){
            return "fog";
        }
        else if( condition.equals("Clear") ){
            return "sunny";
        }
        else if( condition.equals("Clouds") ){
            return "cloudy";
        }

        return "finding";
    }

}
