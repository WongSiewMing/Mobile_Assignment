package com.example.ywh.locality;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;

import com.example.ywh.locality.Model.Assistance;
import com.example.ywh.locality.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText mFirstName;
    private EditText mLastName;
    private EditText mBirthday;
    private EditText mEmail;
    private EditText mPassword;
    private EditText mConfirmPassword;
    private RadioButton mMale;
    private RadioButton mFemale;

    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private String userID;
    private String userEmail;

    private int selectedYear,selectedMonth, selectedDay;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);


        mFirstName = findViewById(R.id.firstname);
        mLastName = findViewById(R.id.lastname);
        Button mSubmit = findViewById(R.id.addUserDetails);
        mMale = findViewById(R.id.radioMale);
        mFemale = findViewById(R.id.radioFemale);
        mBirthday = findViewById(R.id.birthday);
        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        mConfirmPassword = findViewById(R.id.confirm_Password);
        Button mInsertDate = findViewById(R.id.insert_date);

        mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase mFirebaseDB = FirebaseDatabase.getInstance();
        myRef = mFirebaseDB.getReference();
        mSubmit.setOnClickListener(this);
        mInsertDate.setOnClickListener(this);
        mBirthday.setInputType(InputType.TYPE_NULL);


    }
private void signUp(String firstname, String lastname, String email, String gender, Date birthday){
        User newUser = new User(firstname,lastname,email,gender,birthday);
        myRef.child("users").child(userID).setValue(newUser);

    }

    private void signUp(String email, String password){
        if(!validateForm()){
            return;
        }
        Task<AuthResult> authResultTask = mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    try {
                        FirebaseUser user = mAuth.getCurrentUser();
                        userID = user.getUid();
                        userEmail = user.getEmail();
                        String gender = "";
                        String date = (selectedYear + "/" + selectedMonth + "/" + selectedDay);
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
                        Date birthday = formatter.parse(date);
                        if (mMale.isChecked()) {
                            gender = "Male";
                        } else if (mFemale.isChecked()) {
                            gender = "Female";
                        }

                        UserProfileChangeRequest displayChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(mLastName.getText()+" "+mFirstName.getText()).build();
                        user.updateProfile(displayChangeRequest);
                        UserProfileChangeRequest profilePicChangeRequest = new UserProfileChangeRequest.Builder().setPhotoUri
                                (Uri.parse("https://firebasestorage.googleapis.com/v0/b/assignment-863e3.appspot.com/o/defaultProfilePic%2Fdefault_profile.png?alt=media&token=8cda042e-d902-41de-8d75-cec4b540477b")).build();
                        user.updateProfile(profilePicChangeRequest);

                        signUp(mFirstName.getText().toString(), mLastName.getText().toString(), userEmail, gender, birthday);
                        Intent verifyEmail = new Intent(SignUpActivity.this, VerifyEmail.class);
                        startActivity(verifyEmail);
                    } catch (Exception ex) {
                        }
                }else if(!task.isSuccessful()){
                    if(task.getException() instanceof FirebaseAuthUserCollisionException){
                        Snackbar.make(findViewById(R.id.signup_layout), "Address addded successfully", Snackbar.LENGTH_LONG)
                                .setAction("Noted", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        mEmail.setText("");
                                    }
                                }).show();
                    }else{
                        Snackbar.make(findViewById(R.id.signup_layout), "Address addded successfully", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                }
            }
        });
    }


    private boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = mEmail.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmail.setError("Required Field.");
            valid = false;
        }else if(!isEmailValid(email)){
            mEmail.setError("Invalid Email Format.");

        }else{
            mEmail.setError(null);
        }


        String firstName = mFirstName.getText().toString();
        if (TextUtils.isEmpty(firstName)) {
            mFirstName.setError("Required Field.");
            valid = false;
        }else{
            mFirstName.setError(null);
        }

        String LastName = mLastName.getText().toString();
        if (TextUtils.isEmpty(LastName)) {
            mLastName.setError("Required Field.");
            valid = false;
        } else {
            mLastName.setError(null);
        }
        String date = mBirthday.getText().toString();
        if (TextUtils.isEmpty(date)) {
            mBirthday.setError("Required Field.");
            valid = false;
        } else {
            mBirthday.setError(null);
        }
        String password = mPassword.getText().toString();
        String confrimPassword = mConfirmPassword.getText().toString();
        if(TextUtils.isEmpty(password)||TextUtils.isEmpty(confrimPassword)){
            mPassword.setError("Required Field");
            mConfirmPassword.setError("Required Field");
            valid=false;
        }else if (!(confrimPassword.equals(password))){
            mPassword.setError("Password not match");
            mConfirmPassword.setError("Password not match");
        }else{
            mPassword.setError(null);
            mConfirmPassword.setError(null);
        }

        return valid;
    }

    @Override
    public void onClick(View v){

        int id = v.getId();

        if(id==R.id.addUserDetails){
            if(!validateForm()){
                return;
            }
            signUp(mEmail.getText().toString(), mPassword.getText().toString());

        }
        else if (id==R.id.insert_date){
            final Calendar c = Calendar.getInstance();
            int mYear = 2000;
            int mMonth = c.get(Calendar.MONTH);
            int mDay = c.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    mBirthday.setText(year+"/"+(month+1)+"/"+dayOfMonth);
                    selectedYear = year;
                    selectedMonth=month+1;
                    selectedDay=dayOfMonth;

                }
            }, mYear, mMonth, mDay);
            datePickerDialog.show();


        }
    }

}

