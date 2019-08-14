package com.example.caly.caly.Fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.caly.caly.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserProfileFragment extends Fragment {

    private Button mUpdate;
    private EditText mName,mPhone, mAddress, mStatus;
    private Button mEdit;
    private CircleImageView mProfileImage;
    private String mUserId;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    boolean editFlag;
    private final static int GalleryPick = 1;
    String imageUrl;


    public UserProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setup();
        InitFields(view);
        disableOrEnableEditing(false);
        getUserProfileInfo(view);
        mUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = mName.getText().toString();
                String phone =mPhone.getText().toString();
                if(!name.equals("") && !phone.equals("")) {
                    updateProfileInfo();
                    mEdit.setText(R.string.btn_edit_profile);
                    disableOrEnableEditing(false);
                    editFlag = false;
                    getUserProfileInfo(view);
                    mName.setText("");
                    mPhone.setText("");
                    mAddress.setText("");
                    mStatus.setText("");

                }
                else{
                    Toast.makeText(view.getContext(), R.string.toast_empty_user_details, Toast.LENGTH_SHORT).show();
                }
            }
        });



        mEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!editFlag){
                    mEdit.setText(R.string.btn_cancel_edit_profile);
                    disableOrEnableEditing(true);
                    editFlag = true;

                }
                else {
                    mEdit.setText(R.string.btn_edit_profile);
                    disableOrEnableEditing(false);
                    getUserProfileInfo(view);
                    mName.setText("");
                    mPhone.setText("");
                    mAddress.setText("");
                    mStatus.setText("");
                    editFlag = false;
                }

            }
        });
    }

    public void setup() {
        auth = FirebaseAuth.getInstance();
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

    public void InitFields(View view){
        mUpdate = view.findViewById(R.id.set_user_profile_update);
        mName = view.findViewById(R.id.set_user_profile_name);
        mPhone = view.findViewById(R.id.set_user_profile_phone);
        mAddress = view.findViewById(R.id.set_user_profile_address);
        mStatus = view.findViewById(R.id.set_user_profile_status);
        mEdit = view.findViewById(R.id.edit_profile_button);
        mProfileImage = view.findViewById(R.id.set_user_profile_image);
    }

    public void getUserProfileInfo(View view){
        db.collection("Users").document(auth.getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                mName.setHint(documentSnapshot.getString("name"));
                mPhone.setHint(documentSnapshot.getString("phone"));
                mAddress.setHint(documentSnapshot.getString("address"));
                mStatus.setHint(documentSnapshot.getString("status"));
                imageUrl = documentSnapshot.getString("imageUrl");

                Glide
                        .with(view.getContext())
                        .load(imageUrl)
                        .into(mProfileImage);
            }
        });
    }

    public void updateProfileInfo(){

        String name = mName.getText().toString();
        String phone = mPhone.getText().toString();
        String address =mAddress.getText().toString();
        String status = mStatus.getText().toString();
        DocumentReference docRef = db.collection("Users").document(auth.getCurrentUser().getUid());
        docRef.update("name",name);
        docRef.update("phone",phone);
        docRef.update("address",address);
        docRef.update("status", status);
    }

    public static Fragment newInstance() {
        return new UserProfileFragment();
    }

    public void disableOrEnableEditing(boolean bool) {
        mName.setClickable(bool);
        mName.setCursorVisible(bool);
        mName.setFocusable(bool);
        mName.setFocusableInTouchMode(bool);
        mPhone.setClickable(bool);
        mPhone.setCursorVisible(bool);
        mPhone.setFocusable(bool);
        mPhone.setFocusableInTouchMode(bool);
        mAddress.setClickable(bool);
        mAddress.setCursorVisible(bool);
        mAddress.setFocusable(bool);
        mAddress.setFocusableInTouchMode(bool);
        mStatus.setClickable(bool);
        mStatus.setCursorVisible(bool);
        mStatus.setFocusable(bool);
        mStatus.setFocusableInTouchMode(bool);
    }






}
