package com.example.caly.caly;



import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.wang.avi.AVLoadingIndicatorView;

public class LoginActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword;
    private FirebaseAuth auth;
    private AVLoadingIndicatorView progressBar;
    private Button btnSignup, btnLogin;
    FirebaseFirestore db, db1;
    String TAG;
    private final String BUSINESS_PATH = "Business";
    private final String USERS_PATH = "Users";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Get Firebase auth instance
        Log.d("B","before");
        auth = FirebaseAuth.getInstance();
        Log.d("C","after");




        // set the view now
        setContentView(R.layout.activity_login);
        setup();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        progressBar = findViewById(R.id.progressBar);
        btnSignup = (Button) findViewById(R.id.btn_signup);
        btnLogin = (Button) findViewById(R.id.btn_login);


        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            checkIfUserIsBusiness(auth.getCurrentUser().getUid());
            progressBar.setVisibility(View.VISIBLE);
        }


        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            }
        });


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = inputEmail.getText().toString();
                final String password = inputPassword.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), R.string.empty_email, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), R.string.empty_password, Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                //authenticate user
                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, task -> {
                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            progressBar.setVisibility(View.GONE);
                            if (!task.isSuccessful()) {
                                // there was an error
                                if (password.length() < 6) {
                                    inputPassword.setError(getString(R.string.minimum_password));
                                } else {
                                    Toast.makeText(LoginActivity.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                                }
                            } else {
                                String IdOfCurrentUser = auth.getCurrentUser().getUid();
                                checkIfUserIsBusiness(IdOfCurrentUser);
                            }
                        });
            }
        });
    }

    public void checkIfUserIsBusiness(final String UserId) {
        db.collection(BUSINESS_PATH)
                .get()
                .addOnCompleteListener(task -> {
                    boolean flag=true;

                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {

                            String inBusinessIdField = document.getId();

                            if(inBusinessIdField.equalsIgnoreCase(UserId)){
                                flag=false;
                                if(!document.getBoolean("filledUpDetails")){
                                    Intent intent = new Intent(LoginActivity.this, Business_Register.class);

                                    startActivity(intent);

                                }
                                else {
                                    Intent intent = new Intent(LoginActivity.this, Main_business_screen.class);
                                    intent.putExtra("USER_ID", UserId);
                                    startActivity(intent);
                                    finish();
                                }
                            }

                            //Log.d("getDocs", document.getId() + " => " + document.getData());
                        }
                        if(flag){
                            db1.collection(USERS_PATH).get().addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()){
                                    for (QueryDocumentSnapshot document : task1.getResult()){
                                        String inUsersIdField = document.getId();
                                        if(inUsersIdField.equalsIgnoreCase(UserId)){
                                            if(!document.getBoolean("filledUpDetails")){
                                                Intent intent = new Intent(LoginActivity.this, UserRegister.class);

                                                startActivity(intent);
                                                finish();
                                            }
                                            else {
                                                Intent intent = new Intent(LoginActivity.this, Main_user_screen.class);
                                                intent.putExtra("USER_ID", UserId);
                                                startActivity(intent);
                                                finish();
                                            }
                                        }
                                    }
                                }
                            });
                        }


                    } else {
                        Log.d("getDocs", "Error getting documents: ", task.getException());
                    }
                });


    }
    public void setup() {
        // [START get_firestore_instance]
        db = FirebaseFirestore.getInstance();
        db1 = FirebaseFirestore.getInstance();
        // [END get_firestore_instance]

        // [START set_firestore_settings]
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);
        // [END set_firestore_settings]
    }
}
