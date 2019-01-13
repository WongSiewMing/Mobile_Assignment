package com.example.ywh.locality;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.ywh.locality.Model.Address;
import com.example.ywh.locality.Model.Assistance;
import com.example.ywh.locality.Model.ChatGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ReviewAddress extends AppCompatActivity implements View.OnClickListener{

    private EditText mPostcode;
    private EditText mStreet;
    private EditText mSubStreet;
    private EditText mDoorPlate;
    private EditText mState;
    private EditText mCity;
    private Button mSaveAddress;
    private FirebaseDatabase mFirebaseDB;
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private FirebaseUser currentUser;
    private String userID;
    private ChatGroup chatGroup;
    private boolean isExist = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_address);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Review Address");

        mPostcode = findViewById(R.id.postcode);
        mStreet = findViewById(R.id.streetline);
        mSubStreet = findViewById(R.id.substreetline);
        mDoorPlate = findViewById(R.id.doorplate);
        mState = findViewById(R.id.state);
        mCity = findViewById(R.id.city);
        mSaveAddress = findViewById(R.id.saveaddress);

        mFirebaseDB = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        myRef = mFirebaseDB.getReference().child("users");
        mSaveAddress.setOnClickListener(this);

        currentUser = mAuth.getCurrentUser();
        userID = currentUser.getUid();
        getSelectedAddress();
    }

    private void getSelectedAddress(){
        Intent previousIntent = getIntent();
        Bundle b = previousIntent.getExtras();

        String doorPlate = (String)b.get("doorPlate");
        String streetAddress = (String)b.get("streetAddress");
        String subStreetAddress = (String)b.get("subStreetAddress");
        String city = (String)b.get("city");
        String state = (String)b.get("state");
        String postCode = (String)b.get("postCode");

        if(doorPlate!=null){
            mDoorPlate.setText(doorPlate);
            mDoorPlate.setSelection(doorPlate.length());
        }
        if(streetAddress!=null){
            mStreet.setText(streetAddress);
            mStreet.setSelection(streetAddress.length());
        }
        if(subStreetAddress!=null){
            mSubStreet.setText(subStreetAddress);
            mSubStreet.setSelection(subStreetAddress.length());
        }
        if(city!=null){
            mCity.setText(city);
            mCity.setSelection(city.length());
        }
        if(state!=null){
            mState.setText(state);
            mState.setSelection(state.length());
        }
        if(postCode!=null){
            mPostcode.setText(postCode);
            mPostcode.setSelection(postCode.length());
        }

    }

    private boolean validateForm() {
        boolean valid = true;

        String postcode = mPostcode.getText().toString();
        if (TextUtils.isEmpty(postcode)) {
            mPostcode.setError("Required Field.");
            valid = false;
        }else{
            mPostcode.setError(null);
        }


        String street = mStreet.getText().toString();
        if (TextUtils.isEmpty(street)) {
            mStreet.setError("Required Field.");
            valid = false;
        }else{
            mStreet.setError(null);
        }

        String substreet = mSubStreet.getText().toString();
        if (TextUtils.isEmpty(substreet)) {
            mSubStreet.setError("Required Field.");
            valid = false;
        } else {
            mSubStreet.setError(null);
        }

        String doorplate = mDoorPlate.getText().toString();
        if (TextUtils.isEmpty(doorplate)) {
            mDoorPlate.setError("Required Field.");
            valid = false;
        } else {
            mDoorPlate.setError(null);
        }

        String state = mState.getText().toString();
        if (TextUtils.isEmpty(state)) {
            mState.setError("Required Field.");
            valid = false;
        } else {
            mState.setError(null);
        }

        String city = mCity.getText().toString();
        if (TextUtils.isEmpty(city)) {
            mCity.setError("Required Field.");
            valid = false;
        } else {
            mCity.setError(null);
        }

        return valid;
    }

    private void saveAddress(String doorPlate, String streetAddress, String subStreetAddress, String city, String state, String postCode){
        Address address = new Address(doorPlate,streetAddress,subStreetAddress,city,state,postCode);
        myRef.child(userID).child("address").setValue(address);
        Snackbar.make(findViewById(R.id.review_address), "Address added successfully", Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }

    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if(id==android.R.id.home){
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if(id==R.id.saveaddress) {
            if(!validateForm()){
                return;
            }
            saveAddress(mDoorPlate.getText().toString(),mStreet.getText().toString(),mSubStreet.getText().toString(),mCity.getText().toString(),mState.getText().toString(),mPostcode.getText().toString());

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Want to Join the Group Chat of "+mCity.getText().toString()+" ?");
            builder.setCancelable(true);

            builder.setPositiveButton(
                    "Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                            DatabaseReference db = FirebaseDatabase.getInstance().getReference("chatgroup");
                            db.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                                        ChatGroup findGroup = snapshot.getValue(ChatGroup.class);
                                        if(findGroup.getGroupName().equals(mCity.getText().toString())){
                                            isExist = true;
                                            chatGroup = findGroup;
                                            break;
                                        }
                                    }
                                    List<String> participants = new ArrayList<>();
                                    if(isExist){
                                        boolean isUserRepeat = false;
                                        for(int i = 0;i < chatGroup.getJoinedMemberList().size(); i++){
                                            participants.add(chatGroup.getJoinedMemberList().get(i));
                                        }

                                        for(int i = 0; i < participants.size(); i++){
                                            if(participants.get(i).equals(firebaseUser.getUid())){
                                                isUserRepeat = true;
                                                break;
                                            }
                                        }

                                        if(!isUserRepeat) {
                                            participants.add(firebaseUser.getUid());
                                            FirebaseDatabase.getInstance().getReference().child("chatgroup").child(chatGroup.getGroupID()).child("joinedMemberList").setValue(participants);
                                        }
                                    }
                                    else if(!isExist){
                                        ChatGroup chatGroup = new ChatGroup(mCity.getText().toString(), firebaseUser.getUid());
                                        participants.add(firebaseUser.getUid());
                                        chatGroup.setJoinedMemberList(participants);
                                        String key = FirebaseDatabase.getInstance().getReference().child("chatgroup").push().getKey();
                                        chatGroup.setGroupID(key);
                                        FirebaseDatabase.getInstance().getReference().child("chatgroup").child(key).setValue(chatGroup);

                                        FirebaseDatabase.getInstance().getReference("belongsCity").child(key).setValue(mCity.getText().toString());
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                            dialog.cancel();
                            finish();
                        }
                    });

            builder.setNegativeButton(
                    "No",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();

                        }
                    });

            AlertDialog alert11 = builder.create();
            alert11.show();
        }


    }
}
