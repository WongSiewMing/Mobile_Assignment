package com.example.ywh.locality;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.example.ywh.locality.Model.Assistance;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    EventFragment eventFragment;
    ChatFragment chatFragment;
    ProfileFragment profileFragment;
    AssistentFragment assistanceFragment;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;
            switch (item.getItemId()) {
                case R.id.navigation_event:
                    selectedFragment = eventFragment;
                    break;
                case R.id.navigation_chat:
                    selectedFragment = chatFragment;
                    break;
                case R.id.navigation_profile:
                    selectedFragment = profileFragment;
                    break;
                case R.id.navigation_assistent:
                    selectedFragment = assistanceFragment;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectedFragment).commit();
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        eventFragment = new EventFragment();
        chatFragment = new ChatFragment();
        profileFragment = new ProfileFragment();
        assistanceFragment = new AssistentFragment();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createNotificationChannel();
        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new EventFragment()).commit();
        }

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        getNotificationFromAssistanceUpdate();
    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(getString(R.string.channel_id), name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void getNotificationFromAssistanceUpdate(){
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("assistance");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Assistance assistance = new Assistance();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    assistance = snapshot.getValue(Assistance.class);
                }

                final List<String> friendsList = new ArrayList<>();
                final Assistance finalAssistance = assistance;
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("friendslist").child(firebaseUser.getUid());
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                            friendsList.add(snapshot.getValue(String.class));
                        }
                        String notificationHelpTitle = "Someone is requesting assistance from you!";
                        String notificationHelpDesc = "People is facing "+finalAssistance.getEmergenceType()+" of emergency case and need your help. " +
                                "Hurry up and lend your hand to other.";
                        for(int i=0;i<friendsList.size();i++){
                            if(friendsList.get(i).equals(finalAssistance.getRequester())) {
                                notificationHelpTitle = "Your friends/family member is need your help!";
                                notificationHelpDesc = "Your friends/family member is facing " + finalAssistance.getEmergenceType() + " of emergency case and need your help. " +
                                        "Hurry up and lend your hand to other.";
                            }
                        }

                        if(!firebaseUser.getUid().equals(finalAssistance.getRequester())) {
                            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(MainActivity.this, getString(R.string.channel_id))
                                    .setSmallIcon(R.drawable.locality)
                                    .setContentTitle(notificationHelpTitle)
                                    .setContentText(notificationHelpDesc)
                                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                                    .setStyle(new NotificationCompat.BigTextStyle());
                            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(MainActivity.this);
                            notificationManager.notify(123124, mBuilder.build());
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
    }
}
