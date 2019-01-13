package com.example.ywh.locality;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.ywh.locality.Adapter.AddChatAdapter;
import com.example.ywh.locality.Model.ChatGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ru.dimorinny.floatingtextbutton.FloatingTextButton;

public class AddNewGroupMember extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView recyclerView1;
    private RelativeLayout relativeLayout;
    private RelativeLayout relativeLayout1;
    private EditText search;
    private ImageButton back;
    private ImageButton searchBtn;
    private Intent intent;
    private String groupID;
    private AddChatAdapter addAdapter;
    private AddChatAdapter addAdapter1;
    private List<String> familyFriends = new ArrayList<>();
    private List<String> userList = new ArrayList<>();
    private List<String> selectedFamily = new ArrayList<>();
    private List<String> selectedUser = new ArrayList<>();
    private List<String> groupExistingMemberList = new ArrayList<>();
    private boolean checkSearchUser = false;
    private boolean isRepeat = false;
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_group_member);

        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.bar_color));
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.add_member_bar);
        View view = getSupportActionBar().getCustomView();
        back = view.findViewById(R.id.back);
        searchBtn = view.findViewById(R.id.search_button);

        intent = getIntent();
        groupID = intent.getStringExtra("groupID");

        relativeLayout = findViewById(R.id.familyFriend);
        relativeLayout1 = findViewById(R.id.otherUser);

        recyclerView = findViewById(R.id.addMember_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView1 = findViewById(R.id.otherUser_recyclerView);
        recyclerView1.setHasFixedSize(true);
        recyclerView1.setLayoutManager(new LinearLayoutManager(this));

        showFriendFamily();

        search = findViewById(R.id.search);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchUser(s.toString());
                if (!userList.isEmpty()) {
                    relativeLayout1.setVisibility(View.VISIBLE);
                } else {
                    relativeLayout1.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        FloatingTextButton create_group = findViewById(R.id.create_group_btn);
        create_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewMember();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (search.getVisibility() == View.GONE) {
                    search.setVisibility(View.VISIBLE);
                } else {
                    search.setVisibility(View.GONE);
                }
            }
        });
    }

    private void addNewMember() {
        List<String> selected = new ArrayList<>();
        selectedFamily = addAdapter.selectedUsers;
        if (checkSearchUser == true) {
            selectedUser = addAdapter1.selectedUsers;
        }

        if (selectedFamily.size() > 0 || selectedUser.size() > 0) {
            if (selectedFamily.size() > 0) {
                for (int i = 0; i < addAdapter.selectedUsers.size(); i++) {
                    selected.add(addAdapter.selectedUsers.get(i));
                }
            }
            if (selectedUser.size() > 0) {
                for (int i = 0; i < addAdapter1.selectedUsers.size(); i++) {
                    selected.add(addAdapter1.selectedUsers.get(i));
                }
            }

            List<String> newMemberList = groupExistingMemberList;
            for(int i = 0; i < selected.size(); i++){
                newMemberList.add(selected.get(i));
            }

            DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("chatgroup").child(groupID);
            HashMap<String, Object> map = new HashMap<>();
            map.put("joinedMemberList", newMemberList);
            databaseReference1.updateChildren(map);

            Snackbar.make(findViewById(R.id.add_member_layout), "Update Successfully!", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            finish();
        } else if (selectedFamily.size() <= 0 && selectedUser.size() <= 0) {
            Toast.makeText(AddNewGroupMember.this, "Please Select User", Toast.LENGTH_SHORT).show();
        }
    }

    private void searchUser(String searchKey) {
        if (searchKey.equals("")) {
            userList.clear();
        } else {
            Query query = FirebaseDatabase.getInstance().getReference("users").orderByChild("email").startAt(searchKey).endAt(searchKey + "\uf8ff");
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    userList.clear();
                    isRepeat = false;
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String user = snapshot.getKey();
                        assert user != null;
                        for(int i = 0; i < groupExistingMemberList.size(); i++){
                            if(groupExistingMemberList.get(i).equals(user)){
                                isRepeat = true;
                            }
                        }
                        if(!isRepeat) {
                            userList.add(user);
                        }
                    }
                    addAdapter1 = new AddChatAdapter(AddNewGroupMember.this, userList);
                    recyclerView1.setAdapter(addAdapter1);
                    checkSearchUser = true;
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void showFriendFamily() {
        final List<String> friendList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("friendslist").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    friendList.add(snapshot.getValue(String.class));
                }

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("chatgroup").child(groupID);
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ChatGroup group = dataSnapshot.getValue(ChatGroup.class);
                        for (int i = 0; i < group.getJoinedMemberList().size(); i++) {
                            groupExistingMemberList.add(group.getJoinedMemberList().get(i));
                        }
                        for (int i = 0; i < friendList.size(); i++) {
                            boolean isDuplicate = false;
                            for (int j = 0; j < groupExistingMemberList.size(); j++) {
                                if (friendList.get(i).equals(groupExistingMemberList.get(j)))
                                    isDuplicate = true;
                            }
                            if (!isDuplicate)
                                familyFriends.add(friendList.get(i));
                        }
                        addAdapter = new AddChatAdapter(AddNewGroupMember.this, familyFriends);
                        recyclerView.setAdapter(addAdapter);
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
