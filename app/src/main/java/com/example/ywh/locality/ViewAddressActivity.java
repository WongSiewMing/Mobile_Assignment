package com.example.ywh.locality;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.ywh.locality.Model.Address;
import com.example.ywh.locality.Model.Assistance;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class ViewAddressActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView mPostcode;
    private TextView mStreet;
    private TextView mSubStreet;
    private TextView mDoorPlate;
    private TextView mState;
    private TextView mCity;
    private Button mEditAddress;
    private FirebaseDatabase mFirebaseDB;
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private FirebaseUser currentUser;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_address);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Edit Address");

        mPostcode = findViewById(R.id.postcode);
        mStreet = findViewById(R.id.streetline);
        mSubStreet = findViewById(R.id.substreetline);
        mDoorPlate = findViewById(R.id.doorplate);
        mState = findViewById(R.id.state);
        mCity = findViewById(R.id.city);
        mEditAddress = findViewById(R.id.editaddress);

        mFirebaseDB = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        myRef = mFirebaseDB.getReference().child("users");
        mEditAddress.setOnClickListener(this);

        currentUser = mAuth.getCurrentUser();
        userID = currentUser.getUid();

        myRef.child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Address address = dataSnapshot.child("address").getValue(Address.class);
                mPostcode.setText(address != null ? address.getPostCode() : null);
                mStreet.setText(address != null ? address.getStreetAddress() : null);
                mSubStreet.setText(address != null ? address.getSubStreetAddress() : null);
                mDoorPlate.setText(address != null ? address.getDoorPlate() : null);
                mState.setText(address != null ? address.getState() : null);
                mCity.setText(address != null ? address.getCity() : null);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                throw databaseError.toException();

            }
        });

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
        if(id == R.id.editaddress){
            Intent intent = new Intent(this,EditAddress.class);
            startActivity(intent);
            finish();
        }
    }
}
