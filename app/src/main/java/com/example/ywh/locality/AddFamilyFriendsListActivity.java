package com.example.ywh.locality;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Adapter;
import android.widget.CheckBox;

import com.example.ywh.locality.Adapter.UserListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AddFamilyFriendsListActivity extends AppCompatActivity {

    private FirebaseAuth mAuth =FirebaseAuth.getInstance();
    private FirebaseUser currentUser= mAuth.getCurrentUser();
    private DatabaseReference myRef;
    private DatabaseReference mfamilyfriendsRef;
    private RecyclerView mRecyclerView;
    private UserListAdapter adapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<DataSnapshot> UserList;
    private String userID;
    private FloatingActionButton mSaveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_familyfriends_list);
        mSaveButton = findViewById(R.id.save_btn);
        userID = currentUser.getUid();
        UserList = new ArrayList<>();

        mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase mFirebaseDB = FirebaseDatabase.getInstance();

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Adding Family and Friends");
        mfamilyfriendsRef = FirebaseDatabase.getInstance().getReference().child("friendslist");


        List<String> existFList = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference().child("friendslist").child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    existFList.add(snapshot.getValue(String.class));
                }


                myRef = FirebaseDatabase.getInstance().getReference().child("users");
                myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                            if(!Objects.equals(snapshot.getKey(), userID)) {
                                boolean isFriend = true;
                                for (int i = 0; i < existFList.size(); i++) {
                                    if (existFList.get(i).equals(snapshot.getKey()))
                                        isFriend = false;
                                }
                                if(isFriend)
                                    UserList.add(snapshot);
                            }
                        }
                        setUpRecyclerView();
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

        mSaveButton.setOnClickListener(v -> {
            List<String> existList = new ArrayList<>();
            DatabaseReference existRef = mfamilyfriendsRef.child(userID);
            existRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                        existList.add(snapshot.getValue(String.class));
                    }
                    for(int i=0;i<adapter.getSelectedList().size();i++) {
                        existList.add(adapter.getSelectedList().get(i));
                    }
                    FirebaseDatabase.getInstance().getReference().child("friendslist").child(userID).setValue(existList);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            for(int i=0;i<adapter.getSelectedList().size();i++) {
                List<String> addedFList = new ArrayList<>();
                DatabaseReference addedRef = mfamilyfriendsRef.child(adapter.getSelectedList().get(i));
                int finalI = i;
                addedRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                            addedFList.add(snapshot.getValue(String.class));
                        }
                        addedFList.add(userID);
                        FirebaseDatabase.getInstance().getReference().child("friendslist").child(adapter.getSelectedList().get(finalI)).setValue(addedFList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
            final  Snackbar snackbar = Snackbar.make(findViewById(R.id.addFamilyFriendsLayout), "Family and friends added successfully", Snackbar.LENGTH_LONG);
            snackbar.setAction("Noted", v1 -> finish());
            snackbar.show();
        });

        }

    private void setUpRecyclerView(){
        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        adapter = new UserListAdapter(UserList);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if(id==android.R.id.home){
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
