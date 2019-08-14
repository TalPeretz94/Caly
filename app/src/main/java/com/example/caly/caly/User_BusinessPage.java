package com.example.caly.caly;

import android.app.AlertDialog;							   
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;								   
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;							 		
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;							   
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.text.DateFormat;							
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;						
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;						 
import java.util.List;
import java.util.Locale;						

public class User_BusinessPage extends AppCompatActivity {

    List businessList;
    FirebaseFirestore db;
    String sessionId;
    TextView textView_businessName;
    TextView textView_businessCategory;
    TextView textView_businessTime;
    EditText mFullName, mDate, mTime;
    Button mCreate, mCancel, mDatePicker, mTimePicker;
    DatePickerDialog mDatePickerDialog;
    TimePickerDialog mTimePickerDialog;
    int mDay,mMonth,mYear, mHour, mMinute;
    FirebaseAuth auth;
    SimpleDateFormat sdf;
    Calendar mCal;
    Date finalDate;
    String phone;
    final String REAL_RESERVATION_CODE = "2";
    String businessName, category, timeOfReservation;
	ArrayList<String> filterHoursList;
    ArrayList<String> allHourList;
    ArrayList<String> displayHours;
    String[] hours;

    ArrayList<ResevationListCompare> resArr;
    HashMap<Date, ArrayList<String>> reservationMap;
    String open;
    String close;
    String businessPhone;
    String duration;
    String theTime;
    int count = 0;
    String setNameText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_business_page);
        sessionId = getIntent().getStringExtra("BUSINESS_ID");

        auth = FirebaseAuth.getInstance();
        setup();
        textView_businessName = findViewById(R.id.business_name);
        textView_businessCategory = findViewById(R.id.business_category);
        textView_businessTime = findViewById(R.id.business_time);

        fillExampleList();
		resArr = new ArrayList<>();
        allHourList = new ArrayList<>();
        filterHoursList = new ArrayList<>();
        displayHours = new ArrayList<>();
        reservationMap = new HashMap<>();

        mFullName = findViewById(R.id.note_user_name);
        mDate =findViewById(R.id.note_user_date);
        mTime = findViewById(R.id.note_hour);

        mCreate = findViewById(R.id.create_button);
        mCancel = findViewById(R.id.cancel_button);
        mDatePicker = findViewById(R.id.date_picker_button);
        mTimePicker = findViewById(R.id.time_picker_button);



		
		mDate.addTextChangedListener(blabla);
        setTextFiles();


        db.collection("Business").document(sessionId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                duration = documentSnapshot.getString("timeOfReservation");
                open = documentSnapshot.getString("openingTime");
                close = documentSnapshot.getString("closingTime");
                businessPhone = documentSnapshot.getString("phone");

            }
        });

        mTimePicker.setEnabled(false);

        mTimePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
																														   
							 
																						
									 
										 


                getAllRes();
                allHourList.clear();
                filterHoursList.clear();
                displayHours.clear();
                reservationMap.clear();



                AlertDialog.Builder builder = new AlertDialog.Builder(User_BusinessPage.this);
                builder.setTitle("Pick a time");
                getHoursList();
                getAllRes();
                deleteTakenHours();
//                if (count > 0) {
//                    getHoursList();
//                    getAllRes();
//                    deleteTakenHours();
//                }
//                count++;
                int a = resArr.size();
                hours = new String[displayHours.size()];
                if(count>0){
                    hours = displayHours.toArray(hours);

                }
                hours = displayHours.toArray(hours);

               if(hours.length>0){
                   int checkedItem = 1;
                   builder.setSingleChoiceItems(hours, checkedItem, new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialog, int which) {
                           // user checked an item
                       }
                   });

                   builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialog, int which) {
                           ListView lw = ((AlertDialog) dialog).getListView();
                           Object checkedItem = lw.getAdapter().getItem(lw.getCheckedItemPosition());
                           theTime = (String) checkedItem;
                           //String string = "004-034556";
                           String[] parts = theTime.split(":");
                           mHour = Integer.parseInt(parts[0]);
                           mMinute = Integer.parseInt(parts[1]);
                           mTime.setText("" + checkedItem);

                       }
                   });

                   builder.setNegativeButton("Cancel", null);

// create and show the alert dialog
                   AlertDialog dialog = builder.create();
                   dialog.show();
               }
               else{
                   Toast.makeText(User_BusinessPage.this, "full pick anther date!", Toast.LENGTH_LONG).show();
               }

            }
        });


        mDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCal = Calendar.getInstance();
                mYear = mCal.get(Calendar.YEAR);
                mMonth = mCal.get(Calendar.MONTH);
                mDay = mCal.get(Calendar.DAY_OF_MONTH);


                mDatePickerDialog = new DatePickerDialog(User_BusinessPage.this, new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                        mYear = year;
                        mMonth =month+1;
                        mDay = day;
                        mDate.setText(String.format("%02d/%02d/%04d", mDay,mMonth, mYear));
                    }
                }, mYear,mMonth,mDay);
                mDatePickerDialog.show();
                mDatePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

            }
        });


        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFullName.setText("");
                mDate.setText("");
                mTime.setText("");
            }
        });
        mCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name = mFullName.getText().toString();
                String date = mDate.getText().toString();
                String hour = mTime.getText().toString();
                sdf = new SimpleDateFormat("dd-M-yyyy");
                String dateForCal = String.format("%02d-%02d-%04d",mDay,mMonth,mYear);
                try {
                    finalDate = sdf.parse(dateForCal);

                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if((!name.equals(""))&&(!date.equals(""))&&(!hour.equals(""))){

                    DocumentReference newDocInCol = db.collection("Business").document(sessionId).collection("Reservations").document();
                    final Reservation reservation = new Reservation(name,mHour,mMinute, auth.getCurrentUser().getUid(),dateForCal, mYear,mMonth,mDay,finalDate.getTime(),phone);
                    newDocInCol.set(reservation).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(User_BusinessPage.this, "Success!", Toast.LENGTH_SHORT).show();

                            }
                            else{
                                Toast.makeText(User_BusinessPage.this, "Failed!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    //THE RESERVATION DOCUMENT IN USER!!!!!

                    DocumentReference newResInUser = db.collection("Users").document(auth.getCurrentUser().getUid()).collection("Reservations").document();
                    Reservation reservation2 = new Reservation(businessName,mHour,mMinute, sessionId,dateForCal, mYear,mMonth,mDay,finalDate.getTime(),businessPhone);
                    reservation2.setTimeOfRes(REAL_RESERVATION_CODE);
                    newResInUser.set(reservation2).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(User_BusinessPage.this, "Success!", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                            else{
                                Toast.makeText(User_BusinessPage.this, "Failed!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    //THE END OF THE RESERVATION DOCUMENT IN USER!!!!!


                }
                else{
                    Toast.makeText(view.getContext(), "Please fill up all the fields!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    private void fillExampleList(){
        businessList = new ArrayList<>();
        db.collection("Business")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                if(document.getId().equalsIgnoreCase(sessionId)){

                                    category = document.getString("category");
                                    businessName = document.getString("name");
                                    timeOfReservation = document.getString("timeOfReservation");
                                    textView_businessName.setText(businessName);
                                    textView_businessCategory.setText(category);
                                    textView_businessTime.setText(timeOfReservation);
                                }
                            }
                        } else {
                            Log.d("X", "Error getting documents: ", task.getException());
                        }
                    }
                });
        db.collection(("Users")).document(auth.getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                phone = documentSnapshot.getString("phone");

            }
        });
    }

    public void setup() {
        // [START get_firestore_instance]
        db = FirebaseFirestore.getInstance();
        // [END get_firestore_instance]

        // [START set_firestore_settings]
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);
        // [END set_firestore_settings]
    }

	 private void getHoursList() {
        DateFormat df = new SimpleDateFormat("HH:mm");
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        int interval = Integer.parseInt(duration);
        int startDate = cal.get(Calendar.DATE);
        while (cal.get(Calendar.DATE) == startDate) {
            allHourList.add(df.format(cal.getTime()));
            cal.add(Calendar.MINUTE, interval);
        }

        String[] parts = open.split(":");
        Calendar startCal = Calendar.getInstance();
        startCal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(parts[0]));
        startCal.set(Calendar.MINUTE, Integer.parseInt(parts[1]));

        parts = close.split(":");
        Calendar endCal = Calendar.getInstance();
        endCal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(parts[0]));
        endCal.set(Calendar.MINUTE, Integer.parseInt(parts[1]));

        // Add 1 day because you mean 00:16:23 the next day
        //endCal.add(Calendar.DATE, 1);

        Calendar tempCal = Calendar.getInstance();

        for (String theHour : allHourList) {
            parts = theHour.split(":");
            tempCal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(parts[0]));
            tempCal.set(Calendar.MINUTE, Integer.parseInt(parts[1]));
            if (tempCal.before(endCal) && tempCal.after(startCal)) {
                filterHoursList.add(theHour);
            }

        }

    }

    private void getAllRes() {
        db.collection("Business")
                .document(sessionId)
                .collection("Reservations")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            resArr.clear();
                            DateFormat format = new SimpleDateFormat("dd-MM-yyyy");
                            Date theDate = null;
                            ArrayList<String> resList;
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                String date = document.getString("mDate");
                                try {
                                    theDate = format.parse(date);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                String hour = document.getString("time");
                                if (theDate != null) {
                                    resArr.add(new ResevationListCompare(theDate, hour));

                                }


                            }
                        }


                    }
                });
    }

    public void deleteTakenHours() {
        setMap();
        DateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        Date theDate = null;
        String dateForCal = String.format("%02d-%02d-%04d", mDay, mMonth, mYear);

        try {
            theDate = format.parse(dateForCal);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // setMap();
        if (reservationMap.get(theDate) != null) {

            if (theDate != null) {
                for (String elemnt : filterHoursList) {
                    if (!reservationMap.get(theDate).contains(elemnt)) {
                        displayHours.add(elemnt);
                    }

                }
            }

        } else {
            displayHours.addAll(filterHoursList);
        }


    }

    private void setMap() {
        for (ResevationListCompare elemnt : resArr) {
            if (!reservationMap.containsKey(elemnt.getDate())) {
                reservationMap.put(elemnt.getDate(), new ArrayList<String>());

            }

        }

        for (ResevationListCompare elemnt : resArr) {

            reservationMap.get(elemnt.getDate()).add(elemnt.getMyTime());


        }

    }

    private TextWatcher blabla = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String str = mDate.getText().toString().trim();
            if (str.isEmpty()) {
                mTimePicker.setEnabled(false);

            } else {
                mTimePicker.setEnabled(true);

            }

        }

        @Override
        public void afterTextChanged(Editable s) {
            allHourList.clear();
            filterHoursList.clear();
            displayHours.clear();
            reservationMap.clear();
           // Arrays.fill(hours, null);
            mTime.setText("");

        }
    };

    //EditText mFullName, mDate, mTime;

    private void setTextFiles(){
        mFullName.setClickable(false);
        mFullName.setCursorVisible(false);
        mFullName.setFocusable(false);
        mFullName.setFocusableInTouchMode(false);

        mDate.setClickable(false);
        mDate.setCursorVisible(false);
        mDate.setFocusable(false);
        mDate.setFocusableInTouchMode(false);

        mTime.setClickable(false);
        mTime.setCursorVisible(false);
        mTime.setFocusable(false);
        mTime.setFocusableInTouchMode(false);

        db.collection(("Users")).document(auth.getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                setNameText = documentSnapshot.getString("name");
                mFullName.setText(setNameText);

            }
        });

    }

							 

}
