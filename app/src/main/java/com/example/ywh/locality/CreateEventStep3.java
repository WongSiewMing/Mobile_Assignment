package com.example.ywh.locality;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class CreateEventStep3 extends AppCompatActivity {

    Dialog successDialog;
    ImageView success_msg_icon;
    TextView success_msg_text1, success_msg_text2;
    Button success_msg_button;

    public static final String eventDBid = "com.example.pc.locality.extra.eventDBid";

    //private EditText mAddress;
    private EditText mEventDate;
    private EditText mStartTime;
    private EditText mEndTime;
    private TimePicker time_picker;

    private String eventTitle;
    private String eventType;
    private String eventDescription;
    private String participantType;
    private String participantAmtPass;
    private String key;

    private Button insertDate;
    private Button insertStartTime;
    private Button insertEndTime;



    private int participantAmt;

    private int selectedStartHr, selectedStartMin, selectedEndHr, selectedEndMin;

    private int selectedYear, selectedMonth, selectedDay;

    private FirebaseAuth mAuth;
    private DatabaseReference myRef;

    private static final int ERROR_DIALOG_REQUEST = 9001;
    private static final String TAG = "CreateActivity3";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event_step3);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Create Event");

        mEventDate = findViewById(R.id.eventDateTxt);
        mStartTime = findViewById(R.id.startTimeTxt);
        mEndTime = findViewById(R.id.endTimeTxt);

        insertDate = findViewById(R.id.insertDateBtn);
        insertStartTime = findViewById(R.id.insertStartBtn);
        insertEndTime = findViewById(R.id.insertEndBtn);


        successDialog = new Dialog(this);

        Intent intent = getIntent();

        if(intent!=null){
                eventTitle = intent.getStringExtra(CreateEventStep2.eventTitlePassed);
                eventType = intent.getStringExtra(CreateEventStep2.eventTypePassed);
                eventDescription = intent.getStringExtra(CreateEventStep2.eventDescriptionPassed);
                participantType = intent.getStringExtra(CreateEventStep2.participantTypePassed);
                participantAmtPass =  intent.getStringExtra(CreateEventStep2.participantAmtPassed);
                participantAmt = Integer.parseInt(participantAmtPass);
            }


        FirebaseDatabase mFirebaseDB = FirebaseDatabase.getInstance();
        myRef = mFirebaseDB.getReference();

    }
    private void createEventIntoDB(String eventTitle, String eventType, String eventDescription, String participantType, int participantAmt, int selectedYear, int selectedMonth, int selectedDay, int selectedStartHr, int selectedStartMin, int selectedEndHr, int selectedEndMin){
        try {
            String date = (selectedYear + "/" + selectedMonth + "/" + selectedDay);
            SimpleDateFormat formmater = new SimpleDateFormat("yyyy/MM/dd");
            Date eventDate = formmater.parse(date);

            long eventDateLong = eventDate.getTime();

            Event newEvent = new Event(eventTitle, eventType, eventDescription, participantType, participantAmt);

            final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

            newEvent.setStartTime(selectedStartHr,selectedStartMin);
            newEvent.setEndTime(selectedEndHr,selectedEndMin);

            newEvent.setEventDate(eventDateLong);
            newEvent.setOwner(firebaseUser.getUid());

            newEvent.setImgNum(getRandomPicNum());

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("event");
            key = databaseReference.push().getKey();
            newEvent.setEventID(key);
            databaseReference.child(key).setValue(newEvent);

        }catch(Exception ex){

        }
    }

    public void proceedToGetLocation(View view) {
        if(!validateForm()){
            return;
        }else {
            createEventIntoDB(eventTitle, eventType, eventDescription, participantType, participantAmt, selectedYear, selectedMonth, selectedDay, selectedStartHr, selectedStartMin, selectedEndHr, selectedEndMin);
            Intent intent = new Intent(CreateEventStep3.this,MapActivity.class);
            intent.putExtra(eventDBid,key);
            startActivity(intent);
        }
    }

    public Boolean validateForm(){
        boolean valid = true;

        String eventDate = mEventDate.getText().toString();
        String eventStart = mStartTime.getText().toString();
        String eventEnd = mEndTime.getText().toString();
        Date endTime = new Date();
        Date startTime = new Date();

        if(TextUtils.isEmpty(eventDate)){
            mEventDate.setError("Please choose a date");
            valid = false;
        }else {
            mEventDate.setError(null);
            mEventDate.clearFocus();
            mStartTime.requestFocus();
        }

        if(TextUtils.isEmpty(eventStart)){
            mStartTime.setError("Please select a start time");
            valid = false;
        }else {
            mStartTime.setError(null);
            mStartTime.clearFocus();
            mEndTime.setFocusable(true);
            mEndTime.requestFocus();
        }


        startTime.setHours(selectedStartHr);
        startTime.setHours(selectedStartMin);
        endTime.setHours(selectedEndHr);
        endTime.setMinutes(selectedEndMin);

        if(TextUtils.isEmpty(eventEnd)){
            mEndTime.setError("Please select an end time");
            valid = false;
        }else if (selectedStartHr>selectedEndHr){
            mEndTime.setError("End Time must be after start time");
            valid = false;
        }
        else {
            mEndTime.setError(null);
            mEndTime.clearFocus();
        }

        return valid;
    }

    public void selectDate(View view) {
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                mEventDate.setText(year+"/"+(month+1)+"/"+dayOfMonth);
                selectedYear = year;
                selectedMonth=month+1;
                selectedDay=dayOfMonth;

            }
        }, mYear, mMonth, mDay);
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    protected Dialog onCreateDialog(int id){
        if(id == R.id.startTimeTxt)
            return new TimePickerDialog(CreateEventStep3.this, StartTimePickerLister, selectedStartHr, selectedStartMin,false);

        if(id == R.id.endTimeTxt)
            return new TimePickerDialog(CreateEventStep3.this, EndTimePickerLister, selectedStartHr, selectedStartMin,false);

        return null;
    }

    protected TimePickerDialog.OnTimeSetListener StartTimePickerLister = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            selectedStartHr = hourOfDay;
            selectedStartMin = minute;

            mStartTime.setText(selectedStartHr + " : " + selectedStartMin);
        }
    };

    protected TimePickerDialog.OnTimeSetListener EndTimePickerLister = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            selectedEndHr = hourOfDay;
            selectedEndMin = minute;

            mEndTime.setText(selectedEndHr + " : " + selectedEndMin);
        }
    };

    public void selectStartTime(View view) {
        showDialog(R.id.startTimeTxt);
    }

    public void selectEndTime(View view) {
        showDialog(R.id.endTimeTxt);
    }

    public void backToHomePage(View view) {
        Intent intent = new Intent(this, CreateEventStep1.class);
        startActivity(intent);
    }

    public boolean isServicesOK(){
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(CreateEventStep3.this);

        if(available == ConnectionResult.SUCCESS){
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        }else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(CreateEventStep3.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }else
            Toast.makeText(this,"You cant make map requests", Toast.LENGTH_SHORT).show();
        return false;
    }

    public int getRandomPicNum(){
        int picNum;
        Random rand = new Random();
        picNum = rand.nextInt((2 - 0) + 1) + 0;
        return picNum;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
