package com.example.ywh.locality;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.ywh.locality.Adapter.showParticipantAdapter;
import com.example.ywh.locality.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class viewParticipantList extends AppCompatActivity {

    private DatabaseReference myRef;
    private DatabaseReference myRef1;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<String> UID;
    private List<String> selectedUID;
    private List<String> Email;
    private ArrayList<User> mParticipantList;
    private String eventID;

    private TextView text;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_participant_list);

        UID = new ArrayList();
        selectedUID = new ArrayList<>();
        Email = new ArrayList();
        mParticipantList = new ArrayList();
        text = findViewById(R.id.noPartiTxt);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        Intent intent = getIntent();
        eventID = intent.getStringExtra("eventID");
        DatabaseReference mdatabaseReference = FirebaseDatabase.getInstance().getReference("event").child(eventID).child("participantList");
        myRef = FirebaseDatabase.getInstance().getReference().child("users");
        mdatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    selectedUID.add(snapshot.getValue().toString());
                }

                myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                                for (int i = 0; i < selectedUID.size(); i++) {
                                    if (selectedUID.get(i).equals(snapshot.getKey())) {
                                        User user = snapshot.getValue(User.class);
                                        mParticipantList.add(user);
                                        UID.add(snapshot.getKey());
                                        Email.add(user.getEmail());
                                    }
                                }
                        }
                        if(!mParticipantList.isEmpty()) {
                            mAdapter = new showParticipantAdapter(mParticipantList);
                            mRecyclerView.setLayoutManager(mLayoutManager);
                            mRecyclerView.setAdapter(mAdapter);
                            mAdapter = new showParticipantAdapter(mParticipantList);
                        }else {
                            mRecyclerView.setVisibility(View.GONE);
                            text.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
    }

    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if(id==android.R.id.home){
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
