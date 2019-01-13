package com.example.ywh.locality.Adapter;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ywh.locality.Model.Assistance;
import com.example.ywh.locality.AssistanceDetailsActivity;
import com.example.ywh.locality.AssistentFragment;
import com.example.ywh.locality.Model.User;
import com.example.ywh.locality.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class AssistanceAdpater extends RecyclerView.Adapter<AssistanceAdpater.ViewHolder>{

    private Context context;
    private List<Assistance> assistanceList;

    public AssistanceAdpater(Context context, List<Assistance> assistanceList) {
        this.context = context;
        this.assistanceList = assistanceList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.assistance_card, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        final Assistance assistance = assistanceList.get(i);
        final GoogleMap mMap = AssistentFragment.mMap;
        final LatLng latlng = new LatLng(assistance.getLatitude(),assistance.getLongitude());
        viewHolder.accidentTitle.setText(assistance.getEmergenceType());
        viewHolder.accidentDesc.setText(assistance.getDescription());
        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 13));
            }
        });
        MarkerOptions options = new MarkerOptions();
        options.position(latlng).title(assistanceList.get(i).getEmergenceType());
        AssistentFragment.mMap.addMarker(options);
        Location assistLocation = new Location("Asisstance");
        assistLocation.setLatitude(latlng.latitude);
        assistLocation.setLongitude(latlng.longitude);
        viewHolder.distance.setText(String.format("%.2f",AssistentFragment.currentLocation.distanceTo(assistLocation)/1000)+"km");
        viewHolder.seeMore.setOnClickListener(v -> {
            final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        User user = snapshot.getValue(User.class);
                        if(snapshot.getKey().equals(assistance.getRequester())){
                            Intent intent = new Intent(v.getContext(),AssistanceDetailsActivity.class);
                            intent.putExtra("assistanceRequester",user.getFirstName() +" "+ user.getLastName());
                            intent.putExtra("assistanceIsSelf",assistance.isSelf());
                            intent.putExtra("assistanceType",assistance.getEmergenceType());
                            intent.putExtra("assistanceDesc",assistance.getDescription());
                            intent.putExtra("assistanceTime",assistance.getRequestTime());
                            v.getContext().startActivity(intent);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        });

    }

    @Override
    public int getItemCount() {
        if (assistanceList != null)
            return assistanceList.size();
        else
            return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView accidentTitle;
        public TextView accidentDesc;
        public TextView distance;
        public TextView seeMore;
        public CardView cardView;

        public ViewHolder(View assistanceView) {
            super(assistanceView);
            accidentTitle = assistanceView.findViewById(R.id.accidentType);
            accidentDesc = assistanceView.findViewById(R.id.accidentDesc);
            distance = assistanceView.findViewById(R.id.distance);
            cardView = assistanceView.findViewById(R.id.assistance_card);
            seeMore = assistanceView.findViewById(R.id.linktodetails);
        }
    }
}
