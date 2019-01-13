package com.example.ywh.locality;


import android.Manifest;
import android.app.ActionBar;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.ywh.locality.Adapter.AssistanceAdpater;
import com.example.ywh.locality.Model.Assistance;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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


public class AssistentFragment extends Fragment implements OnMapReadyCallback {

    private static final int ERROR_DIALOD_REQUEST = 9001;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private Boolean mLocationPermissionGranted = false;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 0001;
    public static GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private FloatingActionButton RequestButton;
    private FloatingActionButton CurrentLocationButton;
    public static Location currentLocation = new Location("Current Location");
    private RecyclerView assistListView;
    private List<Assistance> assistanceList = new ArrayList<>();
    private AssistanceAdpater assistanceAdpater;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_assistance, container, false);

        MainActivity activity;
        activity = (MainActivity)getActivity();
        activity.getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.bar_color));
        activity.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        activity.getSupportActionBar().setDisplayShowCustomEnabled(true);
        activity.getSupportActionBar().setCustomView(R.layout.assistance_bar);

        if(isServicesOK()) {
            getLocationPermisson();
        }
        assistListView = rootView.findViewById(R.id.assistList);
        assistListView.setHasFixedSize(true);
        assistListView.setLayoutManager(new LinearLayoutManager(getContext()));
        assistanceList.clear();
        findAssistance(false);
        BottomNavigationView navigation = rootView.findViewById(R.id.assistance_navigation);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.assistance_all:
                        findAssistance(false);
                        break;
                    case R.id.navigation_ff:
                        findAssistance(true);
                        break;
                }
                return true;
            }
        });
        RequestButton = rootView.findViewById(R.id.floatingActionButton);
        RequestButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),RequestAssistActivity.class);
                intent.putExtra("currentLocation",currentLocation);
                startActivity(intent);
                assistanceList.clear();
            }
        });
        CurrentLocationButton = rootView.findViewById(R.id.currentLocation);
        CurrentLocationButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                        13,"Current Location");
            }
        });
        return rootView;
    }

    private void getDeviceLocation(){
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this.getActivity());

        try{
            if(mLocationPermissionGranted){

                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            currentLocation = (Location) task.getResult();

                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                    13,"Current Location");

                        }else{

                            Toast.makeText(AssistentFragment.this.getContext(), "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }catch (SecurityException e){

        }
    }

    private void moveCamera(LatLng latLng, float zoom,String title){
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        if(!title.equals("Current Location")){
            MarkerOptions options = new MarkerOptions().position(latLng).title(title);
            mMap.addMarker(options);
        }
    }

    private void initMap(){
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void getLocationPermisson(){
        String[] permissons = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getContext(),FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getContext(),COURSE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                mLocationPermissionGranted = true;
                initMap();
            }else {
                ActivityCompat.requestPermissions(this.getActivity(),permissons,LOCATION_PERMISSION_REQUEST_CODE);
            }


        }else {
            ActivityCompat.requestPermissions(this.getActivity(),permissons,LOCATION_PERMISSION_REQUEST_CODE);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;

        switch (requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if(grantResults.length>0){
                    for(int i=0;i<grantResults.length;i++){
                        if(grantResults[i]!=PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionGranted = false;
                            return;
                        }
                    }
                    mLocationPermissionGranted = true;
                    initMap();
                }
            }
        }
    }

    public boolean isServicesOK(){
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this.getContext());

        if(available == ConnectionResult.SUCCESS){
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(this.getActivity(),available,ERROR_DIALOD_REQUEST);
            ((Dialog) dialog).show();
        }else {
            Toast.makeText(this.getContext(),"Map Services not available",Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (mLocationPermissionGranted) {
            getDeviceLocation();

            if (ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.getContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);


        }

    }

    private void findAssistance(final boolean isSelectedPeople){
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final List<String> friendsList = findFriendsList(firebaseUser);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("assistance");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                assistanceList.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Assistance assistance = snapshot.getValue(Assistance.class);
                    Date date1 = new Date();
                    Date date2 = new Date();
                    date1.setTime(assistance.getRequestTime()+86400000);
                    if(date1.after(date2))
                        if(isSelectedPeople){
                            for(int i=0;i<friendsList.size();i++){
                                if(assistance.getRequester().equals(friendsList.get(i))&&assistance.isSelf())
                                    assistanceList.add(assistance);
                            }
                        }
                        else
                            assistanceList.add(assistance);
                    else{
                        FirebaseDatabase.getInstance().getReference("assistance").child(snapshot.getKey()).removeValue();
                    }

                }

                if(!assistanceList.isEmpty()){
                    assistListView.setVisibility(View.VISIBLE);
                    assistanceAdpater = new AssistanceAdpater(getContext(), assistanceList);
                    assistListView.setAdapter(assistanceAdpater);
                }
                else {
                    assistListView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private List<String> findFriendsList(FirebaseUser user){
        final List<String> friendlist = new ArrayList<>();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("friendslist").child(user.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    friendlist.add(snapshot.getValue(String.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return friendlist;
    }
}
