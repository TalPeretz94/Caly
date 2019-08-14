package com.example.caly.caly;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.caly.caly.Adapters.CalenderReservationListAdapter;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class Main_business_screen extends AppCompatActivity {

    FirebaseFirestore db;
    CompactCalendarView compactCalendar;
    private SimpleDateFormat dateFormatMonth = new SimpleDateFormat("MMMM- yyyy", Locale.getDefault());
    String test;
    SimpleDateFormat sdf;
    String dateInString;
    ListView lv;
    ArrayAdapter<Reservation> adapter1;
    ArrayList<String> arraylist;
    ArrayList<Reservation> reservationsArraylist;
    private FirebaseAuth auth;//add
    private FirebaseAuth.AuthStateListener authListener;//add
    TextView theMonth;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_business_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(R.string.app_name);
        Bundle extras = getIntent().getExtras();
        test = extras.getString("USER_ID");
        reservationsArraylist = new ArrayList<>();
        auth = FirebaseAuth.getInstance();
        theMonth = findViewById(R.id.display_month);


        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            //actionBar.setDisplayHomeAsUpEnabled(false);
            //actionBar.setTitle(null);
        }

        Date currentTime = Calendar.getInstance().getTime();
        //actionBar.setTitle(dateFormatMonth.format(currentTime));
        theMonth.setText(android.text.format.DateFormat.format("MMMM yyyy", currentTime));


        compactCalendar = (CompactCalendarView) findViewById(R.id.compactcalendar_view1);
        compactCalendar.setUseThreeLetterAbbreviation(true);

        getReservationsOfBuisness();

        lv = findViewById(R.id.compactcalendar_view_list1);
        lv.setAdapter(adapter1);

        auth = FirebaseAuth.getInstance();
        //adddddddddddd
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(Main_business_screen.this, LoginActivity.class));
                    finish();
                }
            }
        };
        //adddddddddddd



        compactCalendar.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                Context context = getApplicationContext();
                final long d = dateClicked.getTime();
                reservationsArraylist.clear();
                db.collection("Business").document(test).collection("Reservations").get().addOnCompleteListener(task -> {
                    boolean flag = true;

                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if (document.getLong("mDateInMilli") == d) {
                                String name = document.getString("mCustomerName");
                                int hour = document.getLong("mHour").intValue();
                                int min = document.getLong("mMinute").intValue();
                                long dInMilli = document.getLong("mDateInMilli");
                                int day = document.getLong("mDay").intValue();
                                int month = document.getLong("mMonth").intValue();
                                int year = document.getLong("mYear").intValue();
                                String date = document.getString("mDate");
                                String uid = document.getString("mUserId");
                                String phone = document.getString("mCustomerPhone");

                                Reservation res = new Reservation(name, hour, min, uid, date, year, month, day, dInMilli, phone);
                                Calendar cal = new GregorianCalendar();
                                cal.set(res.getmYear(), res.getmMonth(), res.getmDay(), res.getmHour(), res.getmMinute());
                                res.setCompareDate(cal.getTime());
                                reservationsArraylist.add(res);
                            }
                        }


                        Collections.sort(reservationsArraylist, (o1, o2) -> o1.getCompareDate().compareTo(o2.getCompareDate()));
                        adapter1 = new CalenderReservationListAdapter(reservationsArraylist, getApplicationContext());
                        lv.setAdapter(adapter1);

                    }
                });
                System.out.print(dateClicked);

            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                //actionBar.setTitle(dateFormatMonth.format(firstDayOfNewMonth));
                theMonth.setText(android.text.format.DateFormat.format("MMMM yyyy", firstDayOfNewMonth));
            }
        });
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_business_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.business_signout) {
            auth.signOut();
            return true;
        }

        if (id == R.id.business_profile_settings) {
            Intent intent = new Intent(Main_business_screen.this, BusinessUpdate.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void getReservationsOfBuisness() {
        db = FirebaseFirestore.getInstance();
        db.collection("Business").document(test).collection("Reservations").get().addOnCompleteListener(task -> {

            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {


                    String name = document.getString("mCustomerName");
                    int year = document.getLong("mYear").intValue();
                    int month = document.getLong("mMonth").intValue();
                    int day = document.getLong("mDay").intValue();

                    sdf = new SimpleDateFormat("dd-M-yyyy");
                    dateInString = String.format("%02d-%02d-%04d", day, month, year);
                    try {
                        Date finalDate = sdf.parse(dateInString);

                        Event ev1 = new Event(Color.RED, finalDate.getTime(), name);
                        compactCalendar.addEvent(ev1);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }








}
