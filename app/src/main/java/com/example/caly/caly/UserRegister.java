package com.example.caly.caly;

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
import android.widget.ImageView;
import android.widget.TextView;
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

public class UserRegister extends AppCompatActivity {


    View mParentLayout;
    private EditText mName, mPhone, mAddress, mStatus;
    private TextView mCreate;
    private Button bt;
    private CircleImageView mProfileImage;
    String TAG = "Creation:";
    FirebaseAuth auth;
    FirebaseFirestore db;
    private static int RESULT_LOAD_IMAGE = 1;
    private User customer;
    String theImageUrl;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_register);
        mCreate = findViewById(R.id.set_user_profile_register);
        mName = findViewById(R.id.set_user_profile_name_reg);
        mPhone = findViewById(R.id.set_user_profile_phone_reg);
        mProfileImage = findViewById(R.id.set_user_profile_image_reg);
        mAddress = findViewById(R.id.set_user_profile_address_reg);
        mStatus = findViewById(R.id.set_user_profile_status_reg);
        customer = new User();


        mCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name = mName.getText().toString();
                String phone = mPhone.getText().toString();
                String address = mAddress.getText().toString();
                db = FirebaseFirestore.getInstance();
                auth = FirebaseAuth.getInstance();
                String t = "1";

                if((!name.equals(""))&&(!phone.equals(""))){


                    customer.setName(name);
                    customer.setName(phone);

                    if(!address.equals("")){
                        customer.setName(address);
                    }

                    DocumentReference docRef = db.collection("Users").document(auth.getCurrentUser().getUid());
                    docRef.update("name",name);
                    docRef.update("phone",phone);
                    docRef.update("address",address);
                    if(theImageUrl == null){
                        theImageUrl ="https://firebasestorage.googleapis.com/v0/b/caly-a7702.appspot.com/o/profile_image.png?alt=media&token=b7ffa7d3-1e27-41a0-b2bb-ae9554e035b2";
                    }
                    docRef.update("imageUrl",theImageUrl);
                    docRef.update("filledUpDetails",true);
                    DocumentReference newDocInCol = db.collection("Users").document(auth.getCurrentUser().getUid()).collection("Reservations").document();
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



                    Intent intent =new Intent(UserRegister.this, Main_user_screen.class);
                    intent.putExtra("USER_ID", auth.getCurrentUser().getUid());
                    startActivity(intent);
                    finish();

                }
                else{
                    Toast.makeText(view.getContext(), R.string.user_register_empty_fields, Toast.LENGTH_SHORT).show();
                }
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
                                customer.setImageUrl(uri.toString());
                                theImageUrl = uri.toString();
                                Glide
                                        .with(UserRegister.this)
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



    private void makeSnackBarMessage(String message){
        Toast.makeText(UserRegister.this, message, Toast.LENGTH_SHORT).show();
    }

    private void uploadPic(){

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


}
