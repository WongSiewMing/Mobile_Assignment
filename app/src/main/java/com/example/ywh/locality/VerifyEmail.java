package com.example.ywh.locality;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class VerifyEmail extends AppCompatActivity {

    private FirebaseDatabase mFirebaseDB;
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private String userID;
    private String userEmail;
    private Button mVerify;
    private TextView mUserEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verifyemail);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDB = FirebaseDatabase.getInstance();
        myRef = mFirebaseDB.getReference();
        final FirebaseUser user = mAuth.getCurrentUser();
        userEmail = user.getEmail();
        mVerify = findViewById(R.id.verify);
        mUserEmail = findViewById(R.id.user_email);
        mUserEmail.setText(userEmail);

        mVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmailVerification();
            }
        });

    }



    private void sendEmailVerification() {

        findViewById(R.id.verify).setEnabled(false);

        final FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        findViewById(R.id.verify).setEnabled(true);

                        if (task.isSuccessful()) {
                            Snackbar.make(findViewById(R.id.verifyEmail_layout), "Verification email sent to " + userEmail, Snackbar.LENGTH_INDEFINITE)
                                    .setAction("CONFIRM", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent login = new Intent(VerifyEmail.this,LoginActivity.class);
                                            startActivity(login);
                                        }
                                    }).show();

                        } else {
                            Snackbar.make(findViewById(R.id.verifyEmail_layout), "Failed to send verification email.", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }

                    }
                });

    }
}
