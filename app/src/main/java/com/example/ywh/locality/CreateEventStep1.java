package com.example.ywh.locality;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

public class CreateEventStep1 extends AppCompatActivity implements View.OnClickListener {

    public static final String eventTypePassed = "com.example.pc.locality.extra.message";

    private String EventType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event_step1);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Create Event");

        ImageButton mIndoorBtn = findViewById(R.id.indoorButton);
        ImageButton mOutdoorBtn = findViewById(R.id.outdoorButton);
        ImageButton mSportsBtn = findViewById(R.id.sportsButton);
        ImageButton mCommunityBtn = findViewById(R.id.communityButton);
        Button mProceedBtn = findViewById(R.id.proceedButton);

        mIndoorBtn.setOnClickListener(this);
        mOutdoorBtn.setOnClickListener(this);
        mSportsBtn.setOnClickListener(this);
        mCommunityBtn.setOnClickListener(this);
        mProceedBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if(id==R.id.indoorButton) {
            Toast.makeText(this,"Indoor selected",Toast.LENGTH_LONG).show();
            EventType = "Indoor";
        }
        else if(id==R.id.outdoorButton) {
            Toast.makeText(this,"Outdoor selected",Toast.LENGTH_LONG).show();
            EventType = "Outdoor";
        }
        else if(id==R.id.sportsButton) {
            Toast.makeText(this,"Sports selected",Toast.LENGTH_LONG).show();
            EventType = "Sports";
        }
        else if(id==R.id.communityButton) {
            Toast.makeText(this,"Community selected",Toast.LENGTH_LONG).show();
            EventType = "Community";
        }
        else if(id==R.id.proceedButton){
            if(TextUtils.isEmpty(EventType)) {
                Toast.makeText(this,"Please a type of event before proceed",Toast.LENGTH_LONG).show();
                return;
            }
            else {
                Intent intent = new Intent(this, CreateEventStep2.class);
                intent.putExtra(eventTypePassed, EventType);
                startActivity(intent);
            }
        }
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

