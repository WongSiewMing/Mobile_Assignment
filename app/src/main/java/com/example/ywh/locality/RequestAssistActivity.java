package com.example.ywh.locality;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.ywh.locality.Model.Assistance;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

public class RequestAssistActivity extends AppCompatActivity {

    private TextView request1;
    private TextView request2;
    private TextView request3;
    private RadioGroup request1_group;
    private RadioButton request1_1;
    private RadioButton request1_2;
    private RadioGroup request2_group;
    private RadioButton request2_1;
    private RadioButton request2_2;
    private RadioButton request2_3;
    private EditText request3_desc;
    private FloatingActionButton send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_assist);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(R.string.assistance_request_title);

        request1 = findViewById(R.id.request1);
        request2 = findViewById(R.id.request2);
        request3 = findViewById(R.id.request3);
        request1_group = findViewById(R.id.request1_group);
        request1_1 = findViewById(R.id.request1_option1);
        request1_2 = findViewById(R.id.request1_option2);
        request2_group = findViewById(R.id.request2_group);
        request2_1 = findViewById(R.id.request2_option1);
        request2_2 = findViewById(R.id.request2_option2);
        request2_3 = findViewById(R.id.request2_option3);
        request3_desc = findViewById(R.id.request3_desc);
        send = findViewById(R.id.send);
        final String example = new EditText(this).getText().toString();
        send.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Assistance assistance = new Assistance();
                assistance.setRequester(FirebaseAuth.getInstance().getCurrentUser().getUid());
                if (request1_1.isChecked())
                    assistance.setSelf(true);
                else
                    assistance.setSelf(false);
                if (request2_1.isChecked()) {
                    assistance.setEmergenceType("Accident");
                } else if (request2_2.isChecked()) {
                    assistance.setEmergenceType("Medical");
                } else {
                    assistance.setEmergenceType("Crime");
                }
                Intent previousIntent = getIntent();
                Bundle b = previousIntent.getExtras();
                assistance.setLatitude(((Location) b.get("currentLocation")).getLatitude());
                assistance.setLongitude(((Location) b.get("currentLocation")).getLongitude());
                String description = request3_desc.getText().toString();
                if (!description.equals(example))
                    assistance.setDescription(description);
                else
                    assistance.setDescription("None Description");
                assistance.setRequestTime(new Date().getTime());
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("assistance").push();
                databaseReference.setValue(assistance);
                finish();
            }
        });
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

