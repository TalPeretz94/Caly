package com.example.caly.caly.Fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.caly.caly.Adapters.UserReservationsAdapter;
import com.example.caly.caly.R;
import com.example.caly.caly.Reservation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;


/**
 * A simple {@link Fragment} subclass.
 */
public class UserReservationsFragment extends Fragment {


    ArrayList<Reservation> userReservationsArrayList;
    ListView lv;
    private static UserReservationsAdapter adapter;
    FirebaseFirestore db;
    FirebaseAuth auth;
    String test;

    public UserReservationsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_reservations, container, false);

//        Bundle bundle = this.getArguments();
//        if (bundle != null) {
//            test = bundle.getString("USER_ID");
//        }

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        test = auth.getCurrentUser().getUid();

        lv = view.findViewById(R.id.fragment_user_res_list);
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
                            Calendar cal = new GregorianCalendar();
                            cal.set(res.getmYear(), res.getmMonth(), res.getmDay(), res.getmHour(), res.getmMinute());
                            res.setCompareDate(cal.getTime());
                            userReservationsArrayList.add(res);
                        }

                    }
                }

//                if (userReservationsArrayList.size() > 0) {
//                    Collections.sort(userReservationsArrayList, new Comparator<Reservation>() {
//                        @Override
//                        public int compare(final Reservation object1, final Reservation object2) {
//                            return object1.getTime().compareTo(object2.getTime());
//                        }
//                    });
//                }

                Collections.sort(userReservationsArrayList, new Comparator<Reservation>() {
                    @Override
                    public int compare(Reservation o1, Reservation o2) {
                        return o1.getCompareDate().compareTo(o2.getCompareDate());
                    }
                });

                if ((getActivity()!=null) && (userReservationsArrayList!=null)) {
                    adapter = new UserReservationsAdapter(userReservationsArrayList, getActivity().getApplicationContext());
                    lv.setAdapter(adapter);
                }
                //adapter = new ArrayAdapter<String>(CalenderYanai.this,android.R.layout.simple_list_item_1, rv);
                //lv.setAdapter(adapter);

            }

        });
    }

    public static Fragment newInstance() {
        return new UserReservationsFragment();
    }



}
