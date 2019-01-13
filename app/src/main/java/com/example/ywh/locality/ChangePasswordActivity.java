package com.example.ywh.locality;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class ChangePasswordActivity extends AppCompatActivity {

    private FirebaseAuth mAuth =FirebaseAuth.getInstance();
    private FirebaseUser currentUser= mAuth.getCurrentUser();
    private TextView mPasswordStatus;
    private EditText mPassword;
    private EditText mConfirmPassword;
    private ProgressBar mProgressBar1;
    private ProgressBar mProgressBar2;
    private ProgressBar mProgressBar3;
    private ProgressBar mProgressBar4;
    private TextView mPasswordMessage;
    private Button mChangePw;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Change Password");

        mPasswordStatus = findViewById(R.id.pwStatus);
        mPassword = findViewById(R.id.newPassword);
        mConfirmPassword = findViewById(R.id.confirmPassword);
        mProgressBar1 = findViewById(R.id.progressBar1);
        mProgressBar2 = findViewById(R.id.progressBar2);
        mProgressBar3 = findViewById(R.id.progressBar3);
        mProgressBar4 = findViewById(R.id.progressBar4);
        mPasswordMessage = findViewById(R.id.passwordMessage);
        mChangePw = findViewById(R.id.change_password);

        mPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                setProgressBar(calculateStrength(s.toString()));



            }
        });

        mChangePw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String newPassword = mPassword.getText().toString();
                if(!validateForm()){
                    return;
                }
                else {
                    currentUser.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Snackbar.make(findViewById(R.id.change_passwordLayout), "Password changed successfully.", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                                clearForm();


                            }
                            else if(!task.isSuccessful()){
                                Snackbar.make(findViewById(R.id.change_passwordLayout), "Password fail to change.", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                            }

                        }
                    });
                }
            }
        });

    }
    private void setProgressBar(int token){
        if(token==0){
            mPasswordStatus.setText("WEAK");
            mPasswordStatus.setTextColor(Color.RED);
            mPasswordStatus.setVisibility(View.VISIBLE);
            mProgressBar1.setProgress(100);
            mProgressBar2.setProgress(0);
            mProgressBar3.setProgress(0);
            mProgressBar4.setProgress(0);
            mProgressBar1.setProgressTintList(ColorStateList.valueOf(Color.RED));
            mPasswordMessage.setTextColor(Color.RED);
            mPasswordMessage.setText("Password must contain at least 6 characters.");
        }
        else if(token==1){
            mPasswordStatus.setText("NORMAL");
            mPasswordStatus.setTextColor(Color.rgb(255,127,0));
            mPasswordStatus.setVisibility(View.VISIBLE);
            mProgressBar1.setProgress(100);
            mProgressBar2.setProgress(100);
            mProgressBar3.setProgress(0);
            mProgressBar4.setProgress(0);
            mProgressBar1.setProgressTintList(ColorStateList.valueOf(Color.rgb(255,127,0)));
            mProgressBar2.setProgressTintList(ColorStateList.valueOf(Color.rgb(255,127,0)));
            mPasswordMessage.setTextColor(Color.rgb(255,127,0));
            mPasswordMessage.setText("Password can be improve with combination of upper case, lower case, digit and symbols.");
        }
        else if(token==2){
            mPasswordStatus.setText("Good");
            mPasswordStatus.setTextColor(Color.GREEN);
            mPasswordStatus.setVisibility(View.VISIBLE);
            mProgressBar1.setProgress(100);
            mProgressBar2.setProgress(100);
            mProgressBar3.setProgress(100);
            mProgressBar4.setProgress(0);
            mProgressBar1.setProgressTintList(ColorStateList.valueOf(Color.GREEN));
            mProgressBar2.setProgressTintList(ColorStateList.valueOf(Color.GREEN));
            mProgressBar3.setProgressTintList(ColorStateList.valueOf(Color.GREEN));
            mPasswordMessage.setTextColor(Color.GREEN);
            mPasswordMessage.setText("Password can be improve by length increment.");
        }
        else if(token==3){
            mPasswordStatus.setText("Excellent");
            mPasswordStatus.setTextColor(Color.rgb(50,50,255));
            mPasswordStatus.setVisibility(View.VISIBLE);
            mProgressBar1.setProgress(100);
            mProgressBar2.setProgress(100);
            mProgressBar3.setProgress(100);
            mProgressBar4.setProgress(100);
            mProgressBar1.setProgressTintList(ColorStateList.valueOf(Color.rgb(50,50,255)));
            mProgressBar2.setProgressTintList(ColorStateList.valueOf(Color.rgb(50,50,255)));
            mProgressBar3.setProgressTintList(ColorStateList.valueOf(Color.rgb(50,50,255)));
            mProgressBar4.setProgressTintList(ColorStateList.valueOf(Color.rgb(50,50,255)));
            mPasswordMessage.setTextColor(Color.rgb(50,50,255));
            mPasswordMessage.setText("You are a password master!");

        }
    }


    private int calculateStrength(String password){
            int currentScore = 0;
            int REQUIRED_LENGTH = 5;
            int MAXIMUM_LENGTH = 10;
            boolean sawUpper = false;
            boolean sawLower = false;
            boolean sawDigit = false;
            boolean sawSpecial = false;

            for (int i=0;i<password.length();i++){
                char current = password.charAt(i);
                if(!sawSpecial&&!Character.isLetterOrDigit(current)){
                    sawSpecial=true;
                }
                else {
                    if(!sawDigit&&Character.isDigit(current)){
                        sawDigit=true;
                    }
                    else {
                        if(!sawUpper||!sawLower){
                            if (Character.isUpperCase(current))
                                sawUpper=true;
                            else
                                sawLower=true;
                        }
                    }
                }
            }
        if (password.length() > REQUIRED_LENGTH) {
            if ((!sawSpecial)|| (!sawUpper)|| (!sawLower)|| (!sawDigit)) {
                currentScore = 1;
            }else{
                currentScore = 2;
                if (password.length() > MAXIMUM_LENGTH) {
                    currentScore = 3;
                }
            }
        }else{
            currentScore = 0;
        }

            return currentScore;
        }




    private boolean validateForm(){
        boolean valid = true;
        String newPassword = mPassword.getText().toString();
        String confirmNewPassword = mConfirmPassword.getText().toString();
        if(TextUtils.isEmpty(newPassword)&&TextUtils.isEmpty(confirmNewPassword)){
            mPassword.setError("Required Field.");
            mConfirmPassword.setError("Required Field.");
            valid = false;
        }
        else if(TextUtils.isEmpty(newPassword)){
            mPassword.setError("Required Field.");
            valid = false;
        }
        else if(TextUtils.isEmpty(confirmNewPassword)){
            mConfirmPassword.setError("Required Field.");
            valid = false;
        }
        else if(newPassword.length()<6){
            mPassword.setError("Password must contain at least 6 characters");
            valid = false;

        }
        else if (!newPassword.equals(confirmNewPassword)){
            mConfirmPassword.setError("Password Not Match");
            valid = false;
        }
        else {
            mPassword.setError(null);
            mConfirmPassword.setError(null);
        }
        return valid;

    }

    private void clearForm(){
        mPassword.setText("");
        mConfirmPassword.setText("");
        mProgressBar1.setProgress(0);
        mProgressBar2.setProgress(0);
        mProgressBar3.setProgress(0);
        mProgressBar4.setProgress(0);
        mPasswordStatus.setVisibility(View.INVISIBLE);
        mPasswordMessage.setVisibility(View.INVISIBLE);
    }

    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if(id==android.R.id.home){
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
