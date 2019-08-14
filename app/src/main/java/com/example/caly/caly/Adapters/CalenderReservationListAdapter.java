package com.example.caly.caly.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.caly.caly.R;
import com.example.caly.caly.Reservation;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class CalenderReservationListAdapter extends ArrayAdapter<Reservation> implements View.OnClickListener{

    private ArrayList<Reservation> dataSet;
    Context mContext;
    FirebaseFirestore db;
    String imageUrl;



    // View lookup cache
    private static class ViewHolder {
        TextView txtName;
        TextView txtType;
        TextView txtVersion;
        ImageView info;
        CircleImageView profilePhoto;
    }

    public CalenderReservationListAdapter(ArrayList<Reservation> data, Context context) {
        super(context, R.layout.row_business_calender_item, data);
        this.dataSet = data;
        this.mContext=context;

    }

    @Override
    public void onClick(View v) {

        int position=(Integer) v.getTag();
        Object object= getItem(position);
        Reservation dataModel=(Reservation)object;

        switch (v.getId())
        {
            case R.id.item_info:
//                Snackbar.make(v, "Release date " +dataModel.getmDate(), Snackbar.LENGTH_LONG)
//                        .setAction("No action", null).show();
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:"+dataModel.getmCustomerPhone()));
                callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(callIntent);
                break;
        }
    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Reservation dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        db = FirebaseFirestore.getInstance();

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_business_calender_item, parent, false);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.name);
            viewHolder.txtVersion = (TextView) convertView.findViewById(R.id.version_number);
            viewHolder.info = (ImageView) convertView.findViewById(R.id.item_info);
            viewHolder.profilePhoto = (CircleImageView ) convertView.findViewById(R.id.profile_photo_cal);



            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;

        viewHolder.txtName.setText(dataModel.getmCustomerName());
        viewHolder.txtVersion.setText(String.format("%02d:%02d",dataModel.getmHour(),dataModel.getmMinute()));
        viewHolder.info.setOnClickListener(this);
        viewHolder.info.setTag(position);

        db.collection("Users").document(dataModel.getmUserId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                imageUrl = documentSnapshot.getString("imageUrl");
                Glide
                        .with(mContext)
                        .load(imageUrl)
                        .into(viewHolder.profilePhoto);


            }
        });








        // Return the completed view to render on screen
        return convertView;
    }
}
