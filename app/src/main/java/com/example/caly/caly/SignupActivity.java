package com.example.caly.caly;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignupActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword;
    private Button btnSignIn, btnSignUp;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private CheckBox mCheckbox;
    FirebaseFirestore db;
    DocumentReference newBusinessRef, newUserRef;
    String name, category, timeOfRes, customerName, customerPhone, customerAddress, customerStatus, openingTime, closingTime, address, phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        btnSignIn = (Button) findViewById(R.id.sign_in_button);
        btnSignUp = (Button) findViewById(R.id.sign_up_button);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        mCheckbox = findViewById(R.id.checkBox_business);


        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                finish();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String email = inputEmail.getText().toString().trim();
                final String password = inputPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), R.string.empty_email, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), R.string.empty_password, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6) {
                    Toast.makeText(getApplicationContext(), R.string.illegal_password, Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                //create user
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Toast.makeText(SignupActivity.this, R.string.successfully_register+ "" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    Toast.makeText(SignupActivity.this, R.string.failed_register+"" + task.getException(),
                                            Toast.LENGTH_SHORT).show();
                                }
                                else if(mCheckbox.isChecked()){

                                    auth.signInWithEmailAndPassword(email, password)
                                            .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                                                @Override
                                                public void onComplete(@NonNull Task<AuthResult> task) {
                                                    // If sign in fails, display a message to the user. If sign in succeeds
                                                    // the auth state listener will be notified and logic to handle the
                                                    // signed in user can be handled in the listener.
                                                    progressBar.setVisibility(View.GONE);
                                                    String id = auth.getCurrentUser().getUid();

                                                    if (!task.isSuccessful()) {
                                                        // there was an error
                                                        makeSnackBarMessage("Failed!");
                                                    }
                                                    else {
                                                        newBusinessRef = db.collection("Business").document(id);
                                                        String t = "1";
                                                        name="";
                                                        category ="";
                                                        timeOfRes = "";
                                                        openingTime = "";
                                                        closingTime = "";
                                                        address = "";
                                                        phone = "";



                                                        Business business = new Business(name,category,timeOfRes,openingTime,closingTime, address, phone);
                                                        newBusinessRef.set(business).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if(task.isSuccessful()){
                                                                    makeSnackBarMessage("New Business has been created!");
                                                                }
                                                                else{
                                                                    makeSnackBarMessage("Failed! Check the details.");
                                                                }
                                                            }
                                                        });
                                                    }
                                                }
                                            });


                                    startActivity(new Intent(SignupActivity.this, Business_Register.class));
                                    finish();
                                }

                                else {


                                    auth.signInWithEmailAndPassword(email, password)
                                            .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                                                @Override
                                                public void onComplete(@NonNull Task<AuthResult> task) {
                                                    // If sign in fails, display a message to the user. If sign in succeeds
                                                    // the auth state listener will be notified and logic to handle the
                                                    // signed in user can be handled in the listener.
                                                    progressBar.setVisibility(View.GONE);
                                                    String id = auth.getCurrentUser().getUid();

                                                    if (!task.isSuccessful()) {
                                                        // there was an error
                                                        makeSnackBarMessage("Failed!");
                                                    }
                                                    else {
                                                        newUserRef = db.collection("Users").document(id);
                                                        String t = "1";
                                                        customerName = "";
                                                        customerPhone = "";
                                                        customerAddress = "";
                                                        customerStatus = "";

                                                        User user = new User();
                                                        user.setName(customerName);
                                                        user.setPhone(customerPhone);
                                                        user.setAddress(customerAddress);
                                                        user.setStatus(customerStatus);
                                                        newUserRef.set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if(task.isSuccessful()){
                                                                    makeSnackBarMessage("New user has been created!");
                                                                }
                                                                else{
                                                                    makeSnackBarMessage("Failed! Check the details.");
                                                                }
                                                            }
                                                        });
                                                    }
                                                }
                                            });


                                    startActivity(new Intent(SignupActivity.this, UserRegister.class));
                                    finish();
                                }
                            }
                        });

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }



    private void makeSnackBarMessage(String message){
        Toast.makeText(SignupActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}
