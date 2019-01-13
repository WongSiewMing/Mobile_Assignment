package com.example.ywh.locality;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.ywh.locality.Adapter.MessageAdapter;
import com.example.ywh.locality.Model.ChatGroup;
import com.example.ywh.locality.Model.ChatMessage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupChatActivity extends AppCompatActivity {
    Toolbar toolbar;
    CircleImageView group_icon;
    TextView group_name;
    TextView date;
    ImageButton imageButton;
    private ImageButton back;
    private ImageButton more_option;
    EditText sendText;
    MessageAdapter messageAdapter;
    List<ChatMessage> chatList;
    RecyclerView recyclerView;
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;
    Intent intent;
    String groupID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.bar_color));
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.group_chat_bar);
        View view = getSupportActionBar().getCustomView();
        back = view.findViewById(R.id.back);
        more_option = view.findViewById(R.id.more_option);

        recyclerView = findViewById(R.id.send_recyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        group_icon = findViewById(R.id.group_pic);
        group_name = findViewById(R.id.title_page);
        imageButton = findViewById(R.id.btn_send);
        sendText = findViewById(R.id.send_text);

        intent = getIntent();
        groupID = intent.getStringExtra("groupID");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        imageButton.setOnClickListener(v -> {
            String message = sendText.getText().toString();
            if (!message.equals("")) {
                sendMessage(firebaseUser.getUid(), firebaseUser.getDisplayName(), groupID, message);
            } else {
                Toast.makeText(GroupChatActivity.this, "Can't send empty message", Toast.LENGTH_SHORT).show();
            }
            sendText.setText("");
        });

        databaseReference = FirebaseDatabase.getInstance().getReference("chatgroup").child(groupID);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ChatGroup chatGroup = dataSnapshot.getValue(ChatGroup.class);
                group_name.setText(chatGroup.getGroupName());

                if(!GroupChatActivity.this.isDestroyed()) {
                    if (chatGroup.getGroupIcon().equals("default")) {
                        group_icon.setImageResource(R.drawable.ic_group_white);
                    } else {
                        Glide.with(GroupChatActivity.this).load(chatGroup.getGroupIcon()).into(group_icon);
                    }
                    readMessage(firebaseUser.getUid(), groupID, chatGroup.getGroupIcon());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        back.setOnClickListener(v -> finish());

        more_option.setOnClickListener(v -> nextActivity());
    }

    private void sendMessage(String senderID, String senderName, final String group, String message) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        final ChatMessage chatMessage = new ChatMessage(senderID, senderName, group, message);
        databaseReference.child("chatMessage").push().setValue(chatMessage);
    }

    private void readMessage(final String user, final String group, final String image) {
        chatList = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference("chatMessage");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ChatMessage chatMessage = snapshot.getValue(ChatMessage.class);
                    if (chatMessage.getGroup().equals(group)) {
                        chatList.add(chatMessage);
                    }

                    messageAdapter = new MessageAdapter(GroupChatActivity.this, chatList, image);
                    recyclerView.setAdapter(messageAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void nextActivity() {
        intent = getIntent();
        String groupID = intent.getStringExtra("groupID");
        Intent i = new Intent(this, GroupDetails.class);
        i.putExtra("groupID", groupID);
        startActivity(i);
    }
}
