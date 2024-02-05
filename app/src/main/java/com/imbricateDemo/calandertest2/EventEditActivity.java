package com.imbricateDemo.calandertest2;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class EventEditActivity extends AppCompatActivity {

    private EditText eventNameET;
    private TextView eventDateTV, eventTimeTV, eventTimeSelect;

    Button timeButtonSelect;
    int hour, minute;

    private LocalTime time;

    FirebaseFirestore fStore;
    FirebaseAuth fAuth;

    String userID;
    public static final String TAG = "TAG";
  //  EditText editTextName;


    //select date variables
    private DatePickerDialog datePickerDialog;
    private Button dateButton;
    public String dateString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_edit);
        initWidgets();

        //select date
        initDatePicker();
        dateButton = findViewById(R.id.datePickerButton);
        dateButton.setText(getSelectedDate());


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            time = LocalTime.now();
        }
        // time = LocalTime.parse("10:15");
        eventDateTV.setText(String.format("Date: %s", CalendarUtils.formattedDate(CalendarUtils.selectedDate)));

        String currentTime = String.format("Time: %s", CalendarUtils.formattedTime(time)) ;
        eventTimeTV.setText(currentTime);

       // String currentTimeSelect = String.format("%02d:%02d",CalendarUtils.formattedTime(time));

        //set event Name and time based upon conditions


        Intent intent = getIntent();

        if( intent.getStringExtra("time")  != null){
            eventTimeSelect.setText("Select Time: " + intent.getStringExtra("time") );
        }
        else {
            eventTimeSelect.setText("Select Time: 00:00");
        }


        if( intent.getStringExtra("event")  != null){
            eventNameET.setText(intent.getStringExtra("event")  );
        }



     //   eventTimeSelect.setText("Select Time: 00:00");

    }



    private void initWidgets() {
        eventNameET = findViewById(R.id.eventNameET);
        eventDateTV = findViewById(R.id.eventDateTV);
        eventTimeTV = findViewById(R.id.eventTimeTV);
        timeButtonSelect = findViewById(R.id.eventTimeSelect);
        eventTimeSelect = findViewById(R.id.eventTimeSelect);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void saveEventAction(View view) {
        String eventName = eventNameET.getText().toString();
        String eventTimeSelectText = eventTimeSelect.getText().toString();
        eventTimeSelectText = eventTimeSelectText.replace("Select Time: ","");


     //   Log.d("TAG","name select:" + eventName);
     //   Log.d("TAG","time select: " + eventTimeSelectText);

        // time = LocalTime.parse("10:00");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            time = LocalTime.parse(eventTimeSelectText);
        }

        //date from input
        //add 1 to month to adjust month
        int month = datePickerDialog.getDatePicker().getMonth() +1;
        int day = datePickerDialog.getDatePicker().getDayOfMonth();
        int year = datePickerDialog.getDatePicker().getYear();
        LocalDate date = LocalDate.of(year,month,day);

        DateTimeFormatter formatter = null;
        String dateString = "";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            //   formatter = DateTimeFormatter.ofPattern("dd LLLL yyyy");
            //   formatter = DateTimeFormatter.ofPattern("yyyy MM dd");
            formatter = DateTimeFormatter.ofPattern("YYYY-MM-dd");
            dateString = date.format(formatter);
            Log.d("TAG", dateString);
        }


        //add selectedDate (dateString),time (eventTimeSelectText), and event (eventName) under collections (events) in current user

        /*
        LocalDate localDate = CalendarUtils.selectedDate;
        DateTimeFormatter formatter = null;
        String dateString = "";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
        //   formatter = DateTimeFormatter.ofPattern("dd LLLL yyyy");
         //   formatter = DateTimeFormatter.ofPattern("yyyy MM dd");
            formatter = DateTimeFormatter.ofPattern("YYYY-MM-dd");
            dateString = localDate.format(formatter);
        }
        */



        //set current time when event is created
      //  String currentTime = String.format("Time: %s", CalendarUtils.formattedTime(time));
        LocalTime curTime = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            curTime = LocalTime.now();
        }

        String currentTime = String.format("Time: %s", CalendarUtils.formattedTime(curTime));


        //event name set in conditions if it is empty
        if(eventName.equals("")){
            eventName = "none";
        }

        Intent intent = getIntent();

        /*
        //get count from add event
        String indexCurList = "";
        if( intent.getStringExtra("listCount") != null ) {
            indexCurList =  intent.getStringExtra("listCount") ;
        }

        String refEventDateIndex = dateString + " " + indexCurList;

         */


       // Log.d("TAG","name select:" + eventName);
       // Log.d("TAG","time select: " + eventTimeSelectText);

        //create id for document in firestore database where it contains event name, event name, time selected, and current time
       // String currentTime = String.format("Time: %s", CalendarUtils.formattedTime(time)) ;
        String eventID = eventName + " " + dateString + " " + eventTimeSelectText + " " + currentTime;



        //store event in firebase database
        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();

        userID = fAuth.getCurrentUser().getUid();

        DocumentReference documentReferenceR = fStore.collection("users").document(userID);

        //remove previous event under edit if it is requested
        //remove in arraylist events if planning to change

        if( intent.getStringExtra("indexR") != null ) {
            int indexR = Integer.parseInt( intent.getStringExtra("indexR") );
         //   Log.d("TAG",String.valueOf(indexR));
            Event.eventsList.remove(indexR );
         //   Log.d("TAG","event removed in arraylist");
        }

        //remove previous event if planning to change
        if( intent.getStringExtra("idR") != null ) {
            String idRemove = intent.getStringExtra("idR");
          //  Log.d("TAG",idRemove);
            documentReferenceR.collection("events").document(idRemove).delete();
         //   Log.d("TAG","event removed in database");
        }


        //start add event to database
        DocumentReference documentReference = fStore.collection("users").document(userID).collection("events").document(eventID);


        //take account index where it is added in list for selected date


        Map<String,Object> event = new HashMap<>();
        event.put("event name",eventName);
        event.put("date",dateString);
        event.put("time",eventTimeSelectText);
        event.put("id",eventID);
     //   event.put("refEvDateInd", refEventDateIndex);
       // event.put

        documentReference.set(event).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "onSuccess: event is created for "+ userID);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: " + e.toString());
            }
        });


        //event name, LocalDate, localTime, and string id
        //add event to eventList to display
        //Event newEvent = new Event(eventName, CalendarUtils.selectedDate, time,eventID,refEventDateIndex);
        //Event newEvent = new Event(eventName, CalendarUtils.selectedDate, time,eventID);
        Event newEvent = new Event(eventName, date, time,eventID);

        Event.eventsList.add(newEvent);
        finish();
    }



    //select date
    /*
    private String getTodaysDate()
    {

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        month = month + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return makeDateString(day, month, year);


    }
    */
    private String getSelectedDate()
    {
    LocalDate localDate = CalendarUtils.selectedDate;

        int year = 0;
        int month = 0;
        int day = 0;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
        year = localDate.getYear();
        month = localDate.getMonthValue();
        day = localDate.getDayOfMonth();
        }

        return makeDateString(day, month, year);
    }

    private void initDatePicker()
    {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day)
            {
                month = month + 1;
                String date = makeDateString(day, month, year);
                dateButton.setText(date);
            }
        };

        //set calender input with current date
        /*
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

         */



        //set calender input with selected date
        int year = 0;
        int month = 0;
        int day = 0;


        LocalDate localDate = CalendarUtils.selectedDate;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            year = localDate.getYear();
            month = localDate.getMonthValue() -1; //readjust to go back to right month
            day = localDate.getDayOfMonth();
        }


        int style = AlertDialog.THEME_HOLO_LIGHT;

        datePickerDialog = new DatePickerDialog(this, style, dateSetListener, year, month, day);
        //datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

    }

    private String makeDateString(int day, int month, int year)
    {
        return getMonthFormat(month) + " " + day + " " + year;
    }

    private String getMonthFormat(int month)
    {
        if(month == 1)
            return "JAN";
        if(month == 2)
            return "FEB";
        if(month == 3)
            return "MAR";
        if(month == 4)
            return "APR";
        if(month == 5)
            return "MAY";
        if(month == 6)
            return "JUN";
        if(month == 7)
            return "JUL";
        if(month == 8)
            return "AUG";
        if(month == 9)
            return "SEP";
        if(month == 10)
            return "OCT";
        if(month == 11)
            return "NOV";
        if(month == 12)
            return "DEC";

        //default should never happen
        return "JAN";
    }

    public void openDatePicker(View view)
    {
        datePickerDialog.show();
    }



    public void saveTime(View view)
    {
        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener()
        {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute)
            {
                hour = selectedHour;
                minute = selectedMinute;
                timeButtonSelect.setText(String.format(Locale.getDefault(), "%02d:%02d",hour, minute));
            }
        };

        int style = AlertDialog.THEME_HOLO_DARK;

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, style, onTimeSetListener, hour, minute, true);

        timePickerDialog.setTitle("Select Time: ");
        timePickerDialog.show();
    }

    public void backWeek(View view) {
        startActivity(new Intent(this, WeekViewActivity.class));
    }


}