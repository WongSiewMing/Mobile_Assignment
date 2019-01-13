package com.example.ywh.locality;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;

public class CreateEventStep2 extends AppCompatActivity {

    private static final String LOG_TAG = CreateEventStep2.class.getSimpleName();
    public static final String eventTypePassed = "com.example.pc.locality.extra.eventTypePassed";
    public static final String eventTitlePassed = "com.example.pc.locality.extra.eventTitlePassed";
    public static final String eventDescriptionPassed = "com.example.pc.locality.extra.eventDescriptionPassed";
    public static final String participantTypePassed = "com.example.pc.locality.extra.participantTypePassed";
    public static final String participantAmtPassed = "com.example.pc.locality.extra.participantAmtPassed";

    private EditText mEventTitle;
    private EditText mEventDescription;
    private EditText mParticipantAmt;

    private String eventTitle;
    private String eventDescription;
    private String participantType;
    private RadioButton all;
    private RadioButton children;
    private RadioButton adult;
    private RadioButton oldfolk;
    private String participantAmt;

    private String eventType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event_step2);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Create Event");

        mEventTitle = findViewById(R.id.eventTitleTxt);
        mEventDescription = findViewById(R.id.descriptionTxt);
        mParticipantAmt = findViewById(R.id.participantAmtTxt);
        all = findViewById(R.id.allRadioBtn);
        children = findViewById(R.id.childRadioBtn);
        adult = findViewById(R.id.adultRadioBtn);
        oldfolk = findViewById(R.id.oldfolkRadioBtn);

        Intent intent = getIntent();

        eventType = intent.getStringExtra(CreateEventStep1.eventTypePassed);
    }

    public void proceedToStep3(View view) {
        if(!validateForm()){
            return;
        }else {
            eventTitle = mEventTitle.getText().toString();
            eventDescription = mEventDescription.getText().toString();
            participantAmt = mParticipantAmt.getText().toString();

            if (all.isChecked())
                participantType = "All";
            else if (children.isChecked())
                participantType = "Children";
            else if (adult.isChecked())
                participantType = "Adult";
            else
                participantType = "OldFolk";

            Intent intent = new Intent(this, CreateEventStep3.class);
            intent.putExtra(eventTypePassed, eventType);
            intent.putExtra(eventTitlePassed, eventTitle);
            intent.putExtra(eventDescriptionPassed, eventDescription);
            intent.putExtra(participantTypePassed, participantType);
            System.out.println("This is participant amount" + participantAmt);
            intent.putExtra(participantAmtPassed, participantAmt);
            intent.putExtra("activityID","From_Create_Event_2");
            System.out.println("This is the passed value" + participantAmt);
            startActivity(intent);
        }
    }

    public Boolean validateForm(){
        boolean valid = true;

        String eventTitle = mEventTitle.getText().toString();
        String participantAmt = mParticipantAmt.getText().toString();

        if(TextUtils.isEmpty(eventTitle)){
            mEventTitle.setError("Required Field.");
            valid = false;
        }else
            mEventTitle.setError(null);

        if(TextUtils.isEmpty(participantAmt)){
            mParticipantAmt.setError("Cannot be Empty");
            valid = false;
        }else if(Integer.parseInt(participantAmt)<1){
            mParticipantAmt.setError("Must have at least 1 participant");
            valid = false;
        }else
            mParticipantAmt.setError(null);

        return valid;


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

