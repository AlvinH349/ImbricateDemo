package com.imbricateDemo.calandertest2;

import static com.imbricateDemo.calandertest2.CalendarUtils.monthYearFromDate;
import static com.imbricateDemo.calandertest2.CalendarUtils.daysInWeekArray;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class WeekViewActivity extends AppCompatActivity implements CalendarAdapter.OnItemListener{

    private TextView monthYearText;
    private RecyclerView calendarRecyclerView;
    private ListView eventListView;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userId;

    public static final String TAG = "TAG";

    //set when changing event


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
      //  AddEvents();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_week_view);
        initWidgets();
        setWeekView();
    }

    private void initWidgets()
    {
        calendarRecyclerView = findViewById(R.id.calendarRecyclerView);
        monthYearText = findViewById(R.id.monthYearTV);
        eventListView = findViewById(R.id.eventListView);
    }

    private void setWeekView()
    {
        monthYearText.setText(monthYearFromDate(CalendarUtils.selectedDate));
        ArrayList<LocalDate> days = daysInWeekArray(CalendarUtils.selectedDate);

        CalendarAdapter calendarAdapter = new CalendarAdapter(days, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 7);
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);
        setEventAdpater();
    }


    public void previousWeekAction(View view)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CalendarUtils.selectedDate = CalendarUtils.selectedDate.minusWeeks(1);
        }
        setWeekView();
    }

    public void nextWeekAction(View view)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CalendarUtils.selectedDate = CalendarUtils.selectedDate.plusWeeks(1);
        }
        setWeekView();
    }

    @Override
    public void onItemClick(int position, LocalDate date)
    {
        CalendarUtils.selectedDate = date;
        setWeekView();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        setEventAdpater();
    }




    private void setEventAdpater()
    {

        ArrayList<Event> dailyEvents = Event.eventsForDate(CalendarUtils.selectedDate);
        EventAdapter eventAdapter = new EventAdapter(getApplicationContext(), dailyEvents);
        eventListView.setAdapter(eventAdapter);

        eventListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int listItem, long l) {
             //   Log.d("TAG","event clicked");


//
                new AlertDialog.Builder(WeekViewActivity.this)
                        .setTitle("Do you want to with this event?")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                         //       Log.d("TAG",String.valueOf(i));
                         //            Log.d("TAG",String.valueOf(listItem));
                         //       Log.d("TAG",String.valueOf(l));
                         //       Log.d("TAG",String.valueOf(view.getId()));
                        //        Log.d("TAG",String.valueOf(CalendarUtils.selectedDate) + " " + listItem);

                                //go through every events has the same date until you hit the one that is in the right location
                                Log.d("TAG",String.valueOf(CalendarUtils.selectedDate) + " " + listItem);

                                int count = 0;
                                int selectedItem = 0;
                                for(int j = 0; j <  Event.eventsList.size();j++){

                                    Event e = Event.eventsList.get(j);

                                    /*
                                    Log.d("TAG","count: " + count);
                                    Log.d("TAG","event: " + e.getName());
                                    Log.d("TAG","current date: " + String.valueOf(e.getDate()));
                                    Log.d("TAG","selected date: " + String.valueOf(CalendarUtils.selectedDate));

                                    Log.d("TAG","");
                                    */


                                    if( String.valueOf(e.getDate()).equals(  String.valueOf(CalendarUtils.selectedDate)  )   ){
                                     //   Log.d("TAG","count: " + count);
                                        if( count == listItem){
                                            selectedItem = j;
                                            break;
                                        }
                                        else{
                                            count++;
                                        }

                                    }
                                }


                                String idRemove = Event.eventsList.get(selectedItem).getID();

                                //     Log.d("TAG",idRemove);

                                Event.eventsList.remove(selectedItem);
                                //     Log.d("TAG","evemt removed");



                                //remove it from the database
                                fStore = FirebaseFirestore.getInstance();
                                fAuth = FirebaseAuth.getInstance();
                                userId = fAuth.getCurrentUser().getUid();
                                //   Log.d("TAG",userId);
                                DocumentReference documentReference = fStore.collection("users").document(userId);

                                //  Log.d("TAG","evemt referenced in database");

                                //remove event in firestore database
                                documentReference.collection("events").document(idRemove).delete();
                               //     Log.d("TAG","evemt removed in database");



                                eventAdapter.notifyDataSetChanged();

                                finish();
                                startActivity(getIntent());

                            }


                        }).setNeutralButton("change", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                //go through every events has the same date until you hit the one that is in the right location
                                int count = 0;
                                int selectedItem = 0;
                                for(int j = 0; j <  Event.eventsList.size();j++){

                                    Event e = Event.eventsList.get(j);

                                    if( String.valueOf(e.getDate()).equals(  String.valueOf(CalendarUtils.selectedDate)  )   ){
                                        if( count == listItem){
                                            selectedItem = j;
                                            break;
                                        }
                                        count++;
                                    }
                                }

                                String idRemove = Event.eventsList.get(selectedItem).getID();


                                String eventName = Event.eventsList.get(selectedItem).getName();

                            //    Log.d("TAG",eventName);


                          //      String time = String.format( "%02d:%02d",Event.eventsList.get(listItem).getTime());
                                LocalTime t = Event.eventsList.get(selectedItem).getTime();
                                String time = t.toString();

                           //     Log.d("TAG",time);

                                Intent intent = new Intent(WeekViewActivity.this, EventEditActivity.class);

                             //   Log.d("TAG",eventName);
                             //   Log.d("TAG",time);

                                intent.putExtra("event", eventName);
                                intent.putExtra("time", time);
                                intent.putExtra("indexR", String.valueOf(selectedItem) );
                                intent.putExtra("idR", idRemove);

                                startActivity(intent);
                            }



                        }).setNegativeButton("Go back", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }

                         }).create().show();




            } //close onClickItem


        }); //close eventListView.setOnItemClickListener


    } //close function setAdapter



    public void newEventAction(View view)
    {
        //pass current count - next number for current list in selected date to event
   //     int countList = eventListView.getCount();

        Intent intent = new Intent(this, EventEditActivity.class);

    //    intent.putExtra("listCount", String.valueOf(countList) );

        startActivity(intent);
    }

    public void backCal(View view) {
        startActivity(new Intent(this, MainActivity.class));
    }
}