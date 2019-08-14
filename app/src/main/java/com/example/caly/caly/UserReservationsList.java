package com.example.caly.caly;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.example.caly.caly.Adapters.UserReservationsAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class UserReservationsList extends AppCompatActivity {

    ArrayList<Reservation> userReservationsArrayList;
    ListView lv;
    private static UserReservationsAdapter adapter;
    FirebaseFirestore db;
    FirebaseAuth auth;
    String test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_reservations_list);
        Bundle extras = getIntent().getExtras();
        test = extras.getString("USER_ID");

        db = FirebaseFirestore.getInstance();

        lv = (ListView) findViewById(R.id.user_res_list);
        lv.setAdapter(adapter);

        userReservationsArrayList = new ArrayList<>();

        db.collection("Users").document(test).collection("Reservations").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String realReservationCode = document.getString("mInitialize");
                        if (realReservationCode!=null && realReservationCode.equalsIgnoreCase("2")) {
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


                            //Toast.makeText(CalenderYanai.this, name, Toast.LENGTH_SHORT).show();
                            String time = String.format("%02d:%02d", document.getLong("mHour").intValue(), document.getLong("mMinute").intValue());
                            //String rvl  = name + time;
                            //rv.add(rvl);
                            //reservationsHm.put(name,time);
                            Reservation res = new Reservation(name, hour, min, uid, date, year, month, day, dInMilli, phone);
                            userReservationsArrayList.add(res);
                        }

                    }
                }

//                if (userReservationsArrayList.size() > 0) {
//                    Collections.sort(userReservationsArrayList, new Comparator<Reservation>() {
//                        @Override
//                        public int compare(final Reservation object1, final Reservation object2) {
//                            return object1.getmDate().compareTo(object2.getmDate());
//                        }
//                    });
//                }

                adapter = new UserReservationsAdapter(userReservationsArrayList, getApplicationContext());
                lv.setAdapter(adapter);
                //adapter = new ArrayAdapter<String>(CalenderYanai.this,android.R.layout.simple_list_item_1, rv);
                //lv.setAdapter(adapter);


            }

        });
    }
}






