package com.example.caly.caly.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.caly.caly.R;
import com.example.caly.caly.Reservation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserReservationsAdapter extends ArrayAdapter<Reservation> implements View.OnClickListener {

    private ArrayList<Reservation> dataSet;
    Context mContext;
    FirebaseFirestore db;
    String businessName;
    Date k, dayOfWeekName;
    String TAG = "TAG";
    String[] strDays = new String[]{"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
    private FirebaseAuth auth;
    private String docToDelete;
    String cusId;
    String buisId;
    Reservation dataModel;
    String imageUrl;


    // View lookup cache
    private static class ViewHolder {
        TextView txtName;
        TextView txtDate;
        TextView txtTime;
        TextView txtDay;
        ImageView phoneIcon;
        ImageView cancelIcon;


        CircleImageView profilePhoto;
    }

    public UserReservationsAdapter(ArrayList<Reservation> data, Context context) {
        super(context, R.layout.row_user_res_item, data);
        this.dataSet = data;
        this.mContext = context;
    }

    @Override
    public void onClick(View v) {

        int position = (Integer) v.getTag();
        Object object = getItem(position);
        dataModel = (Reservation) object;
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        buisId = dataModel.getmUserId();
        cusId = auth.getCurrentUser().getUid();

        switch (v.getId()) {
            case R.id.item_business_phone_for_user_list:

                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:" + dataModel.getmCustomerPhone()));
                callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(callIntent);
                break;

            case R.id.item_cancel_for_user_list:

                AlertDialog.Builder builder = new AlertDialog.Builder(v.getRootView().getContext());
                builder.setTitle("Cancel Reservation!");
                builder.setMessage("You are about to delete your reservation, Are you sure?");
                builder.setCancelable(false);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteReservation();                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                builder.show();

                break;


        }
    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Reservation dataModel = getItem(position);
        db = FirebaseFirestore.getInstance();
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;


        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_user_res_item, parent, false);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.business_name_for_user_list);
            viewHolder.txtDate = (TextView) convertView.findViewById(R.id.date_for_user_list);
            viewHolder.txtDay = (TextView) convertView.findViewById(R.id.day_for_user_list);
            viewHolder.phoneIcon = (ImageView) convertView.findViewById(R.id.item_business_phone_for_user_list);
            viewHolder.profilePhoto = (CircleImageView) convertView.findViewById(R.id.business_profile_photo_for_user_list);
            viewHolder.cancelIcon = (ImageView) convertView.findViewById(R.id.item_cancel_for_user_list);
            viewHolder.txtTime = (TextView) convertView.findViewById(R.id.time_for_user_list);


            result = convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;

        String d = dataModel.getmDate();
        try {
            k = new SimpleDateFormat("dd-M-yyyy").parse(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar c = Calendar.getInstance();
        c.setTime(k);
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);


        viewHolder.txtName.setText(dataModel.getmCustomerName());
        viewHolder.txtDay.setText(strDays[dayOfWeek - 1]);
        viewHolder.txtDate.setText(dataModel.getmDate());
        viewHolder.txtTime.setText(dataModel.getTime());
        viewHolder.phoneIcon.setOnClickListener(this);
        viewHolder.phoneIcon.setTag(position);
        viewHolder.cancelIcon.setTag(position);
        viewHolder.cancelIcon.setOnClickListener(this);
        // Return the completed view to render on screen

        db.collection("Business").document(dataModel.getmUserId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                imageUrl = documentSnapshot.getString("imageUrl");


                Glide
                        .with(mContext)
                        .load(imageUrl)
                        .into(viewHolder.profilePhoto);
            }
        });


        return convertView;
    }

    private void deleteReservation(){
        db.collection("Business")
                .document(buisId)
                .collection("Reservations")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String date = document.getString("mDate");
                                String hour = document.getString("time");
                                String custumerId = document.getString("mUserId");
                                if (dataModel.getTime().equalsIgnoreCase(hour) && dataModel.getmDate().equalsIgnoreCase(date) &&
                                        cusId.equalsIgnoreCase(custumerId)) {
                                    docToDelete = document.getId();
                                }
                            }

                            db.collection("Business").document(buisId)
                                    .collection("Reservations")
                                    .document(docToDelete)
                                    .delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                        }
                                    });
                        }
                    }
                });




        db.collection("Users")
                .document(cusId)
                .collection("Reservations")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String date = document.getString("mDate");
                                String hour = document.getString("time");
                                String buisnessId = document.getString("mUserId");
                                if (dataModel.getTime().equalsIgnoreCase(hour) && dataModel.getmDate().equalsIgnoreCase(date) &&
                                        dataModel.getmUserId().equalsIgnoreCase(buisnessId)) {
                                    docToDelete = document.getId();


                                }


                            }

                        }
                        db.collection("Users").document(cusId)
                                .collection("Reservations")
                                .document(docToDelete)
                                .delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                    }
                                });


                    }
                });
    }
}
