package com.imbricateDemo.calandertest2;

import android.os.Build;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Event
{
    public static ArrayList<Event> eventsList = new ArrayList<>();

    public static ArrayList<Event> eventsForDate(LocalDate date)
    {
        ArrayList<Event> events = new ArrayList<>();

        sortEvents();

        for(Event event : eventsList)
        {
            if(event.getDate().equals(date))
                events.add(event);
        }

        return events;
    }


    private String name;
    private LocalDate date;
    private LocalTime time;
    private String id;
   // private String refEvDateInd;

    /*
    public Event(String name, LocalDate date, LocalTime time,String id, String refEvDateInd)
    {
        this.name = name;
        this.date = date;
        this.time = time;
        this.id = id;
        this.refEvDateInd = refEvDateInd;
    }
     */

    public Event(String name, LocalDate date, LocalTime time,String id)
    {
        this.name = name;
        this.date = date;
        this.time = time;
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public LocalDate getDate()
    {
        return date;
    }

    public void setDate(LocalDate date)
    {
        this.date = date;
    }

    public LocalTime getTime()
    {
        return time;
    }

    public void setTime(LocalTime time)
    {
        this.time = time;
    }

    public String getID() {return id;}

    public void setID(String name)
    {
        this.id = id;
    }

    /*
    public String getrefEvDateInd() {return refEvDateInd;}

    public void setrefEvDateInd(String refEvDateInd)
    {
        this.refEvDateInd = refEvDateInd;
    }

     */

    public static void sortEvents(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Collections.sort(eventsList, Comparator.comparing(Event::getTime));
        }
    }
}
