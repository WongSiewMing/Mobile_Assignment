package com.example.ywh.locality;

import android.app.ActionBar;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ywh.locality.Model.Address;
import com.example.ywh.locality.Model.ChatGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class showEventDetails extends AppCompatActivity {

    private String eventID;
    private TextView mEventTitle;
    private TextView mEventDesc;
    private TextView mEventDate;
    private TextView mEventTime;
    private TextView mEventLocation;
    private Button mParticipantBtn;
    private ImageButton mShowLocationBtn;
    private ImageButton back;
    private Button mJoinBtn;
    private Button mRemoveBtn;
    private Button mFullBtn;
    private ImageButton mChatBtn;
    private ImageView mEventPic;

    private Event eventSelected;
    private FirebaseAuth mAuth;


    private Address addressSelected;
    private Boolean isParticipant = false;
    private List<String> participantList = new ArrayList<>();
    private List<String> chatgroupMemList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_event_details);

        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.bar_color));
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.event_details_bar);
        View view = getSupportActionBar().getCustomView();
        back = view.findViewById(R.id.back);

        mEventTitle = findViewById(R.id.eventTitle);
        mEventDesc = findViewById(R.id.eventDescTxt);
        mEventDate = findViewById(R.id.eventDate);
        mEventTime = findViewById(R.id.eventTime);
        mEventLocation = findViewById(R.id.eventLocation);
        mJoinBtn = findViewById(R.id.joinEventBtn);
        mShowLocationBtn = findViewById(R.id.checkEventLocationBtn);
        mParticipantBtn = findViewById(R.id.checkEventParticipantBtn);
        mRemoveBtn = findViewById(R.id.removeBtn);
        mFullBtn = findViewById(R.id.fullBtn);
        mChatBtn = view.findViewById(R.id.chatGroupBtn);
        mEventPic = findViewById(R.id.eventPic);

        Intent intent = getIntent();
        eventID = intent.getStringExtra("eventID");
        retrieveEventDetails();

        mParticipantBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(showEventDetails.this, viewParticipantList.class);
                intent.putExtra("eventID", eventID);
                startActivity(intent);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("event").child(eventID).child("participantList");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String participantID = snapshot.getValue().toString();
                    participantList.add(participantID);
                }

                for (int i = 0; i < participantList.size(); i++) {
                    if (firebaseUser.getUid().equals(participantList.get(i))) {
                        mJoinBtn.setVisibility(View.GONE);
                        i = participantList.size();
                    }
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        mJoinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int remainingAmt = eventSelected.getParticipantAmt();
                if (remainingAmt > 0) {
                    final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("event").child(eventID).child("participantList");
                    participantList.add(firebaseUser.getUid());
                    databaseReference.setValue(participantList);
                    remainingAmt = remainingAmt - 1;
                    DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("event").child(eventID).child("participantAmt");
                    databaseReference1.setValue(remainingAmt);

                    DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("belongsto").child(eventID);
                    databaseReference2.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            final DatabaseReference databaseReference3 = FirebaseDatabase.getInstance().getReference("event").child(eventID);
                            databaseReference3.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    Event event = dataSnapshot.getValue(Event.class);
                                    final List<String> newMemberList = new ArrayList<>();
                                    for(int i=0;i<event.getparticipantList().size();i++){
                                        newMemberList.add(event.getparticipantList().get(i));
                                    }
                                    newMemberList.add(event.getOwner());
                                    FirebaseDatabase.getInstance().getReference("belongsto").child(eventID).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            FirebaseDatabase.getInstance().getReference("chatgroup")
                                                    .child(dataSnapshot.getValue(String.class))
                                                    .child("joinedMemberList").setValue(newMemberList);
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
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    Snackbar.make(findViewById(R.id.eventChatGrp), "Joined Success", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
            }
        });

        mChatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("event");
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Event event = snapshot.getValue(Event.class);
                            assert event != null;
                            assert firebaseUser != null;
                            if (eventID.equals(event.getEventID()))
                                eventSelected = event;
                        }
                        String eventTitle = eventSelected.getEventTitle();
                        String eventOwner = eventSelected.getOwner();

                        ChatGroup chatgroup = new ChatGroup(eventTitle, eventOwner);
                        chatgroupMemList = participantList;

                        chatgroupMemList.add(eventOwner);
                        chatgroup.setJoinedMemberList(chatgroupMemList);
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("chatgroup");
                        String key = reference.push().getKey();
                        chatgroup.setGroupID(key);
                        reference.child(key).setValue(chatgroup);

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("belongsto").child(eventID);
                        ref.setValue(chatgroup.getGroupID());

                        Snackbar.make(findViewById(R.id.eventChatGrp), "Create Success", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }
        });


        mRemoveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(showEventDetails.this);
                builder.setTitle("Confirmation");
                builder.setMessage("Confirm to remove this event?");
                builder.setCancelable(false);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("event");
                        databaseReference.child(eventID).removeValue();
                        Toast.makeText(getApplicationContext(), "Event has been deleted", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(showEventDetails.this, MainActivity.class);
                        startActivity(intent);
                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });

    }

    private void retrieveEventDetails() {

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("event");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Event event = snapshot.getValue(Event.class);
                    assert event != null;
                    assert firebaseUser != null;
                    if (eventID.equals(event.getEventID()))
                        eventSelected = event;
                }

                if (eventSelected != null) {
                    mEventTitle.setText(eventSelected.getEventTitle());
                    Date date = new Date(eventSelected.getEventDate());
                    Date startTime = new Date(eventSelected.getStartTime());
                    Date endTime = new Date(eventSelected.getEndTime());

                    SimpleDateFormat dateFormater = new SimpleDateFormat("dd/MM/yyyy");
                    SimpleDateFormat timeFormater = new SimpleDateFormat("HHmm");

                    mEventDate.setText(dateFormater.format(date));
                    timeFormater.setTimeZone(TimeZone.getTimeZone("UTC"));
                    mEventTime.setText(timeFormater.format(startTime) + " - " + timeFormater.format(endTime));

                    mEventDesc.setText(eventSelected.getEventDesc());

                    String eventType = eventSelected.getEventType();

                    if(eventType.equals("Indoor")){
                        if(eventSelected.getImgNum()==0)
                            mEventPic.setImageResource(R.drawable.indoor_pic_1);
                        else if(eventSelected.getImgNum()==1)
                            mEventPic.setImageResource(R.drawable.indoor_pic_2);
                        else
                            mEventPic.setImageResource(R.drawable.indoor_pic_3);

                    }
                    if(eventType.equals("Outdoor")){
                        if(eventSelected.getImgNum()==0)
                            mEventPic.setImageResource(R.drawable.outdoor_pic_1);
                        else if(eventSelected.getImgNum()==1)
                            mEventPic.setImageResource(R.drawable.outdoor_pic_2);
                        else
                            mEventPic.setImageResource(R.drawable.outdoor_pic_3);

                    }
                    if(eventType.equals("Sports")){
                        if(eventSelected.getImgNum()==0)
                            mEventPic.setImageResource(R.drawable.sports_pic_1);
                        else if(eventSelected.getImgNum()==1)
                            mEventPic.setImageResource(R.drawable.sports_pic_2);
                        else
                            mEventPic.setImageResource(R.drawable.sports_pic_3);
                    }
                    if(eventType.equals("Community")){
                        if(eventSelected.getImgNum()==0)
                            mEventPic.setImageResource(R.drawable.community_pic_1);
                        else if(eventSelected.getImgNum()==1)
                            mEventPic.setImageResource(R.drawable.community_pic_2);
                        else
                            mEventPic.setImageResource(R.drawable.community_pic_3);
                    }

                    if (eventSelected.getParticipantAmt() == 0) {
                        mJoinBtn.setVisibility(View.GONE);
                        mFullBtn.setVisibility(View.VISIBLE);
                    }

                    if (eventSelected.getOwner().equals(firebaseUser.getUid())) {
                        mJoinBtn.setVisibility(View.GONE);
                        mParticipantBtn.setVisibility(View.VISIBLE);
                        mRemoveBtn.setVisibility(View.VISIBLE);
                        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("belongsto");

                        dbRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                boolean isChatGrpCreated = false;
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    String EventID = snapshot.getKey();
                                    if(eventSelected.getEventID().equals(EventID)){
                                        isChatGrpCreated = true;
                                    }
                                }
                                if(isChatGrpCreated)
                                    mChatBtn.setVisibility(View.GONE);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    } else {
                        if (isParticipant.equals(true)) {
                            mJoinBtn.setVisibility(View.GONE);
                        }
                        mChatBtn.setVisibility(View.GONE);
                        mParticipantBtn.setVisibility(View.GONE);
                        mRemoveBtn.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        DatabaseReference mdatabaseReference = FirebaseDatabase.getInstance().getReference("event").child(eventID).child("address");
        mdatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Address address = dataSnapshot.getValue(Address.class);
                assert address != null;
                assert firebaseUser != null;
                addressSelected = address;
                if (addressSelected != null) {
                    mEventLocation.setText(addressSelected.toString());
                }

                mShowLocationBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?daddr=" + addressSelected.getLatitude() + "," + addressSelected.getLongtitude()));
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }


}
