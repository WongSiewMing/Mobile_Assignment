package com.example.ywh.locality;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.Date;

public class AssistanceDetailsActivity extends AppCompatActivity {

    private TextView DetailView1;
    private TextView DetailView2;
    private TextView DetailView3;
    private TextView DetailView4;
    private TextView DetailView5;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assistance_details);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(R.string.assistance_display_title);

        Intent previousIntent = getIntent();
        Bundle b = previousIntent.getExtras();
        String assistanceRequester = (String)b.get("assistanceRequester");
        boolean assistanceIsSelf = (boolean)b.get("assistanceIsSelf");
        String assistanceType = (String)b.get("assistanceType");
        String assistanceDesc = (String)b.get("assistanceDesc");
        long assistanceTime = (long)b.get("assistanceTime");
        DetailView1 = findViewById(R.id.DetailView1Content);
        DetailView2 = findViewById(R.id.DetailView2Content);
        DetailView3 = findViewById(R.id.DetailView3Content);
        DetailView4 = findViewById(R.id.DetailView4Content);
        DetailView5 = findViewById(R.id.DetailView5Content);
        String self = "Other";
        if(assistanceIsSelf)
            self = "Himself/Herself";
        Date time = new Date();
        time.setTime(assistanceTime);
        DetailView1.setText(assistanceRequester);
        DetailView2.setText(assistanceType);
        DetailView3.setText(self);
        DetailView4.setText(time.toLocaleString());
        DetailView5.setText(assistanceDesc);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void showLess(View view) {
        finish();
    }
}
