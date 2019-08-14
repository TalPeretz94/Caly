package com.example.caly.caly;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class Business_Register extends AppCompatActivity {


    View mParentLayout;
    private EditText mName, mCategory, mTimeOfReservation, mAddress, mPhone;
    private TextView mCreate, mCancel;
    EditText mStartTime;
    EditText mFinishTime;
    String TAG = "Creation:";
    FirebaseAuth auth;
    int mHour, mMinute, mfHour,mfMinute,msHour,msMinute ;
    String msTime, mfTime, mAdressString, mPhoneString;
    Button mFinishTimePicker, mStartTimePicker,mCatagoryPicker;
    TimePickerDialog mStartTimePickerDialog, mFinishTimePickerDialog;
    private static int RESULT_LOAD_IMAGE = 1;
    private Business theBusiness;
    String theImageUrl;
    private CircleImageView mProfileImage;

    String chosenCatagory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business__register);
        mParentLayout = findViewById(android.R.id.content);
        mName = findViewById(R.id.business_register_name);
        mCategory = findViewById(R.id.business_register_category);
        mTimeOfReservation = findViewById(R.id.business_register_time);
        mStartTime = findViewById(R.id.business_start_time);
        mStartTimePicker = findViewById(R.id.start_time_button);

        mFinishTime = findViewById(R.id.business_finish_time);
        mFinishTimePicker = findViewById(R.id.finish_time_button);

        mPhone = findViewById(R.id.business_register_phone);
        mAddress = findViewById(R.id.business_register_address);
        mProfileImage = findViewById(R.id.set_user_profile_image_reg);

        theBusiness = new Business();

        mCreate = findViewById(R.id.business_register_create_button);
        mCancel = findViewById(R.id.business_register_cancel_button);
        mCatagoryPicker = findViewById(R.id.category_picker);
        setTextFiles();

        mCatagoryPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // setup the alert builder
                AlertDialog.Builder builder = new AlertDialog.Builder(Business_Register.this);
                builder.setTitle(R.string.dialog_category_title);

                // add a radio button list
                String[] categories = getResources().getStringArray(R.array.category);
                int checkedItem = 1;
                builder.setSingleChoiceItems(categories, checkedItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ListView lw = ((AlertDialog) dialog).getListView();
                        Object checkedItem = lw.getAdapter().getItem(lw.getCheckedItemPosition());
                        chosenCatagory = (String) checkedItem;
                        mCategory.setText(chosenCatagory);
                    }
                });

                // add OK and Cancel buttons
                builder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // user clicked OK
                    }
                });
                builder.setNegativeButton(R.string.dialog_cancel, null);

                // create and show the alert dialog
                AlertDialog dialog = builder.create();
                dialog.show();


            }
        });




        mCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name = mName.getText().toString();
                String category = mCategory.getText().toString();
                String time = mTimeOfReservation.getText().toString();
                String opening = mStartTime.getText().toString();
                String closing = mFinishTime.getText().toString();
                String phone = mPhone.getText().toString();
                String address = mAddress.getText().toString();


                if((!name.equals(""))&&(!category.equals(""))&&(!time.equals(""))&&(!opening.equals(""))&&(!closing.equals(""))&&(!phone.equals(""))&&(!address.equals(""))){

                    createNewBusiness(name,category,time,opening,closing,address,phone);
                    Intent intent =new Intent(Business_Register.this, Main_business_screen.class);
                    intent.putExtra("USER_ID", auth.getCurrentUser().getUid());
                    startActivity(intent);
                    finish();

                }
                else{
                    Toast.makeText(view.getContext(), R.string.toast_empty_fields_business, Toast.LENGTH_SHORT).show();
                }
            }
        });

        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mName.setText("");
                mCategory.setText("");
                mTimeOfReservation.setText("");
            }
        });

        mStartTimePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mStartTimePickerDialog = new TimePickerDialog(Business_Register.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                        msHour = hour;
                        msMinute = minute;
                        msTime = String.format("%02d:%02d", msHour, msMinute);

                        mStartTime.setText(msTime);
                    }
                },msHour,msMinute,true);
                mStartTimePickerDialog.show();
            }
        });

        mFinishTimePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFinishTimePickerDialog = new TimePickerDialog(Business_Register.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                        mfHour = hour;
                        mfMinute = minute;
                        mfTime = String.format("%02d:%02d", mfHour, mfMinute);
                        mFinishTime.setText(mfTime);
                    }
                },mfHour,mfMinute,true);
                mFinishTimePickerDialog.show();
            }
        });

        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            try{

                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();

                FirebaseStorage storage;

                FirebaseApp.initializeApp(getApplicationContext());
                storage = FirebaseStorage.getInstance();
                String[] tmp = selectedImage.getPath().split("/");
                mProfileImage.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                StorageReference riversRef;
                StorageReference storageRef = storage.getReferenceFromUrl("gs://caly-a7702.appspot.com");
                riversRef = storageRef.child("ProfilePictures/" + tmp[tmp.length - 1]);
                Log.d("file", selectedImage.getPath());
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                Bitmap bitmap;
                bitmap = MediaStore.Images.Media.
                        getBitmap(this.getContentResolver(), selectedImage);
                double maxSize = 450;
                double scale = maxSize / Math.max(bitmap.getHeight(), bitmap.getWidth());
                bitmap = getResizedBitmap(bitmap, (int) (scale * bitmap.getWidth()),
                        (int) (scale * bitmap.getHeight()));
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] byteData = baos.toByteArray();
                riversRef.putBytes(byteData)
                        // Register observers to listen for when the download is done
                        // or if it fails
                        .addOnFailureListener(exception ->
                                Toast.makeText(getApplicationContext(),
                                        "fail upload file",
                                        Toast.LENGTH_SHORT)
                                        .show())
                        .addOnSuccessListener(taskSnapshot -> {
                            riversRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                Log.i("profilePic", "onActivityResult: " + uri);
                                //image url;
                                theBusiness.setImageUrl(uri.toString());
                                theImageUrl = uri.toString();
                                Glide
                                        .with(Business_Register.this)
                                        .load(theImageUrl)
                                        .into(mProfileImage);
                                Log.i("imageURL",uri.toString());
                            });
                        });

            }catch (Exception e){
                Log.e("Upload Fail",""+e.getMessage());
                e.printStackTrace();
                throw new RuntimeException(""+e.getMessage());//TODO remove
            }


        }


    }




    public void createNewBusiness(String name, String category, String timeOfRes,String open, String close, String address, String phone) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
//        DocumentReference newBusinessRef = db.collection("Business").document();
        String t = "1";


        Business business = new Business(name,category,timeOfRes,open,close, address, phone);
        business.setmIdOfUser(auth.getCurrentUser().getUid());


        DocumentReference docRef = db.collection("Business").document(auth.getCurrentUser().getUid());
        docRef.update("name",name);
        docRef.update("category",category);
        docRef.update("timeOfReservation",timeOfRes);
        docRef.update("openingTime", open);
        docRef.update("closingTime", close);
        docRef.update("filledUpDetails",true);
        docRef.update("address", address);
        docRef.update("phone", phone);
        docRef.update("mIdOfUser", auth.getCurrentUser().getUid());
        if(theImageUrl == null){
            theImageUrl ="https://firebasestorage.googleapis.com/v0/b/caly-a7702.appspot.com/o/profile_image.png?alt=media&token=b7ffa7d3-1e27-41a0-b2bb-ae9554e035b2";
        }
        docRef.update("imageUrl",theImageUrl);


        DocumentReference newDocInCol = db.collection("Business").document(auth.getCurrentUser().getUid()).collection("Reservations").document();
        Reservation reservation = new Reservation("",0, 0, "", "", 0, 0, 0, 0, "");
        newDocInCol.set(reservation).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    makeSnackBarMessage("Registration Succeeded! ");
                }
                else{
                    makeSnackBarMessage("Failed! Check the details.");
                }
            }
        });

        finish();

    }

    private void makeSnackBarMessage(String message){
        Toast.makeText(Business_Register.this, message, Toast.LENGTH_SHORT).show();
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }

    private void setTextFiles(){
        mStartTime.setClickable(false);
        mStartTime.setCursorVisible(false);
        mStartTime.setFocusable(false);
        mStartTime.setFocusableInTouchMode(false);

        mFinishTime.setClickable(false);
        mFinishTime.setCursorVisible(false);
        mFinishTime.setFocusable(false);
        mFinishTime.setFocusableInTouchMode(false);

        mCategory.setClickable(false);
        mCategory.setCursorVisible(false);
        mCategory.setFocusable(false);
        mCategory.setFocusableInTouchMode(false);


    }


}
