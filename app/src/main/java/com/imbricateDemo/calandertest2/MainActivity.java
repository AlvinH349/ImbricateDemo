package com.imbricateDemo.calandertest2;

import static com.imbricateDemo.calandertest2.CalendarUtils.daysInMonthArray;
import static com.imbricateDemo.calandertest2.CalendarUtils.monthYearFromDate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;


import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

/*
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
 */

import javax.annotation.Nullable;

import androidx.recyclerview.widget.GridLayoutManager;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements CalendarAdapter.OnItemListener{
    private static final int GALLERY_INTENT_CODE = 1023 ;
    TextView fullName,email,phone;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userId;
    FirebaseUser user;

    private TextView monthYearText;
    private RecyclerView calendarRecyclerView;

    public static final String TAG = "TAG";


    private void initWidgets()
    {
        calendarRecyclerView = findViewById(R.id.calendarRecyclerView);
        monthYearText = findViewById(R.id.monthYearTV);
    }

    private void setMonthView()
    {
        monthYearText.setText(monthYearFromDate(CalendarUtils.selectedDate));
        ArrayList<LocalDate> daysInMonth = daysInMonthArray(CalendarUtils.selectedDate);

        CalendarAdapter calendarAdapter = new CalendarAdapter(daysInMonth, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 7);
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);
    }




    public void previousMonthAction(View view)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CalendarUtils.selectedDate = CalendarUtils.selectedDate.minusMonths(1);
        }
        setMonthView();
    }

    public void nextMonthAction(View view)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CalendarUtils.selectedDate = CalendarUtils.selectedDate.plusMonths(1);
        }
        setMonthView();
    }

    @Override
    public void onItemClick(int position, LocalDate date)
    {
        if(date != null)
        {
            CalendarUtils.selectedDate = date;
            setMonthView();
        }
    }

    public void weeklyAction(View view) {
        startActivity(new Intent(this, WeekViewActivity.class));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AddEvents();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initWidgets();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CalendarUtils.selectedDate = LocalDate.now();
        }
        setMonthView();

        phone = findViewById(R.id.profilePhone);
        fullName = findViewById(R.id.profileName);
        email    = findViewById(R.id.profileEmail);



        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();






        userId = fAuth.getCurrentUser().getUid();
        user = fAuth.getCurrentUser();






        DocumentReference documentReference = fStore.collection("users").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(documentSnapshot.exists()){

                    //display account info
                    phone.setText(documentSnapshot.getString("phone"));
                    fullName.setText(documentSnapshot.getString("fName"));
                    email.setText(documentSnapshot.getString("email"));





                }else {
                    Log.d("tag", "onEvent: Document do not exists");
                }
            }
        });




    }

    public void AddEvents(){
        Event.eventsList.clear();
      //  Log.d(TAG, "start add events 1");

        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        userId = fAuth.getCurrentUser().getUid();
        DocumentReference documentReference = fStore.collection("users").document(userId);

        documentReference.collection("events")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                              //   Log.d(TAG, document.getId() + " => " + document.getData());
                                //    Log.d(TAG, document.getString("event name"));

                                String eventName = "";
                                LocalDate date = null;
                                LocalTime time = null;
                                String id="";
                               // String refEvDateInd = "";


                                eventName = document.getString("event name");

                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                    //     DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy MM dd");
                                    //          Log.d(TAG, "date start");
                                  //  DateTimeFormatter formatter = DateTimeFormatter.ofPattern("YYYY-MM-dd");
                                    //         Log.d(TAG, "date format");
                                    date = LocalDate.parse( document.getString("date") );
                                    //          Log.d(TAG, "date converted");
                                }

                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                    time = LocalTime.parse( document.getString("time") );
                                    //          Log.d(TAG, "time converted");
                                }


                                id= document.getString("id");

                              //  refEvDateInd = document.getString("refEvDateInd");


                              //  Event newEvent = new Event(eventName, date, time,id, refEvDateInd );
                                Event newEvent = new Event(eventName, date, time,id );
                                Event.eventsList.add(newEvent);

                                //  Log.d(TAG,"event added");
                                // finish();


                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

        // finish();
    //    Log.d(TAG, "end add events");
    }

    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();//logout
        startActivity(new Intent(getApplicationContext(),Login.class));
        finish();
    }


    public void weatherAction(View view) {
        startActivity(new Intent(this,weatherActivity3.class));
    }


    public void backCal(View view) {
        startActivity(new Intent(this, MainActivity.class));
    }



    public void chatAction(View view)  {
        startActivity(new Intent(this, chatActivity.class));
    }

    public void movieAction(View view) {
        startActivity(new Intent(this, movieActivity.class));
    }


}