package com.example.ywh.locality;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.ywh.locality.Adapter.ChatAdapter;
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
import java.util.List;


public class ChatFragment extends Fragment {
    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    private TextView text;
    private List<com.example.ywh.locality.Model.ChatGroup> groups;
    EditText search;
    ImageButton searchButton;
    ImageButton addChatButton;
    RelativeLayout relativeLayout;
    FirebaseUser firebaseUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        MainActivity activity;
        activity = (MainActivity)getActivity();
        activity.getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.bar_color));
        activity.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        activity.getSupportActionBar().setDisplayShowCustomEnabled(true);
        activity.getSupportActionBar().setCustomView(R.layout.chat_bar);
        View view1 = activity.getSupportActionBar().getCustomView();

        relativeLayout = view.findViewById(R.id.search_bar);
        text = view.findViewById(R.id.no_chat);
        recyclerView = view.findViewById(R.id.chat_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        groups = new ArrayList<>();
        findGroupChat();

        searchButton = view1.findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                relativeLayout.setVisibility(View.VISIBLE);
            }
        });

        addChatButton = view1.findViewById(R.id.add_chat_button);
        addChatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), CreateGroupActivity.class);
                startActivity(intent);
            }
        });

        search = view.findViewById(R.id.search);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchGroup(s.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        ImageButton closeSearchButton = view.findViewById(R.id.close_search);
        closeSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                relativeLayout.setVisibility(View.GONE);
                findGroupChat();
            }
        });

        return view;
    }

    private void searchGroup(String searchKey){
        Query query = FirebaseDatabase.getInstance().getReference("chatgroup").orderByChild("search").startAt(searchKey).endAt(searchKey+"\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                groups.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    ChatGroup group = snapshot.getValue(ChatGroup.class);
                    assert group != null;
                    assert firebaseUser != null;
                    for(String member : group.getJoinedMemberList()){
                        if(member.equals(firebaseUser.getUid())){
                            groups.add(group);
                        }
                    }
                }
                chatAdapter = new ChatAdapter(getContext(), groups);
                recyclerView.setAdapter(chatAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void findGroupChat(){
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("chatgroup");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(relativeLayout.getVisibility() == View.GONE) {
                    groups.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        ChatGroup group = snapshot.getValue(ChatGroup.class);
                        assert group != null;
                        assert firebaseUser != null;
                        for (String member : group.getJoinedMemberList()) {
                            if (member.equals(firebaseUser.getUid())) {
                                groups.add(group);
                            }
                        }
                    }

                    if (!groups.isEmpty()) {
                        text.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        chatAdapter = new ChatAdapter(getContext(), groups);
                        recyclerView.setAdapter(chatAdapter);
                    } else {
                        recyclerView.setVisibility(View.GONE);
                        text.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
