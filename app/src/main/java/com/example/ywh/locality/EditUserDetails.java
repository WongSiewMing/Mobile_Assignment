package com.example.ywh.locality;

import android.app.ActionBar;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class EditUserDetails extends AppCompatActivity implements View.OnClickListener{
    private EditText mFirstName;
    private EditText mLastName;
    private EditText mBirthday;
    private Button mSave;
    private FirebaseDatabase mFirebaseDB;
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private FirebaseUser currentUser;
    private String userID;
    private int selectedYear,selectedMonth, selectedDay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_details);

        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.bar_color));
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.personal_details_bar);
        View view = getSupportActionBar().getCustomView();

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Update Personal Details");

        mFirstName = findViewById(R.id.firstname);
        mLastName = findViewById(R.id.lastname);
        mBirthday = findViewById(R.id.birthday);
        mSave = findViewById(R.id.Save);

        mFirebaseDB = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        myRef = mFirebaseDB.getReference().child("users");

        mBirthday.setInputType(InputType.TYPE_NULL);
        mBirthday.setOnClickListener(this);
        mSave.setOnClickListener(this);

        currentUser = mAuth.getCurrentUser();
        userID = currentUser.getUid();


        myRef.child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String firstName = dataSnapshot.child("firstName").getValue(String.class);
                String lastName = dataSnapshot.child("lastName").getValue(String.class);
                Date date = dataSnapshot.child("birthday").getValue(Date.class);
                String birthday = formatDate(date);

                mFirstName.setText(firstName);
                mLastName.setText(lastName);
                mBirthday.setText(birthday);

                mFirstName.setSelection(firstName.length());
                mLastName.setSelection(lastName.length());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                throw databaseError.toException();

            }
        });



    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.birthday) {
            final Calendar c = Calendar.getInstance();
            int mYear = 2000;
            int mMonth = c.get(Calendar.MONTH);
            int mDay = c.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                mBirthday.setText("");
                mBirthday.setText(year + "/" + (month + 1) + "/" + dayOfMonth);
                selectedYear = year;
                selectedMonth = month + 1;
                selectedDay = dayOfMonth;

            }, mYear, mMonth, mDay);
            datePickerDialog.show();

        } else if (id == R.id.Save){
            try {
                String date = (selectedYear + "/" + selectedMonth + "/" + selectedDay);
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
                Date birthday = formatter.parse(date);

                Map<String, Object> refUpdates = new HashMap<>();
                refUpdates.put(userID + "/firstName", mFirstName.getText().toString());
                refUpdates.put(userID + "/lastName", mLastName.getText().toString());
                refUpdates.put(userID + "/birthday", birthday);

                if (!myRef.updateChildren(refUpdates).isSuccessful()) {
                    Snackbar.make(findViewById(R.id.updateDetialsLayout), "Update Success", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    Snackbar.make(findViewById(R.id.updateDetialsLayout), "Error Occur", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }

            } catch (Exception ex) { }
        }
    }

    private String formatDate(Date date){
        String birthday="";
        String year,month,day;
        if(date.getYear()<100){
            year = "19"+date.getYear();
        }else {
            year = "20"+(String.valueOf(date.getYear())).substring(1,3);
        }
        month = String.valueOf(date.getMonth()+1);
        day = String.valueOf(date.getDate());

        birthday = year+"/"+month+"/"+day;
        return birthday;
    }


    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if(id==android.R.id.home){
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
