package com.example.ywh.locality;


import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ywh.locality.Adapter.showEventAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class EventFragment extends Fragment {

    private RecyclerView recyclerView;
    private showEventAdapter showEventAdapter;
    private TextView text1;
    private TextView text2;
    private List<Event> events;
    private FloatingActionButton createEventButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_event, container, false);
        final MainActivity activity;
        activity = (MainActivity)getActivity();
        activity.getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.bar_color));
        activity.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        activity.getSupportActionBar().setDisplayShowCustomEnabled(true);
        activity.getSupportActionBar().setCustomView(R.layout.event_bar);

        createEventButton = view.findViewById(R.id.createEventIcon);
        createEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, CreateEventStep1.class);
                startActivity(intent);
            }
        });

        text1 = view.findViewById(R.id.no_event_txt1);
        text2 = view.findViewById(R.id.no_event_txt2);

        recyclerView = view.findViewById(R.id.event_recyler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        events = new ArrayList<>();
        retrieveEventItems();
        return view;
    }

    private void retrieveEventItems(){
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("event");
        final Date currentDate = new Date();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                events.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Event event = snapshot.getValue(Event.class);
                    assert event != null;
                    assert firebaseUser != null;

                    Long eventDateLong = event.getEventDate();
                    Date eventDateDate = new Date();
                    eventDateDate.setTime(eventDateLong);

                    if(eventDateDate.after(currentDate))
                        events.add(event);
                    }

                if(!events.isEmpty()){
                    text1.setVisibility(View.GONE);
                    text2.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    showEventAdapter = new showEventAdapter(getContext(),events);
                    recyclerView.setAdapter(showEventAdapter);
                }else{
                    recyclerView.setVisibility(View.GONE);
                    text1.setVisibility(View.VISIBLE);
                    text2.setVisibility(View.VISIBLE);
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
