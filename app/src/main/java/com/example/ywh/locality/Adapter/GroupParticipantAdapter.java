package com.example.ywh.locality.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.ywh.locality.Model.User;
import com.example.ywh.locality.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class GroupParticipantAdapter extends RecyclerView.Adapter<GroupParticipantAdapter.ViewHolder> {
    private Context context;
    private List<String> participants;

    public GroupParticipantAdapter(Context context, List<String> participants) {
        this.context = context;
        this.participants = participants;
    }

    @NonNull
    @Override
    public GroupParticipantAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.show_participant, viewGroup, false);
        return new GroupParticipantAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final GroupParticipantAdapter.ViewHolder viewHolder, int i) {
        if (participants != null) {
            final String participant = participants.get(i);
            final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users").child(participant);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    viewHolder.participant_name.setText(user.getLastName()+" "+user.getFirstName());

                    if (user.getProfilePic().equals("default")) {
                        viewHolder.profile_pic.setImageResource(R.drawable.default_profile);
                    } else {
                        Glide.with(context).load(user.getProfilePic()).into(viewHolder.profile_pic);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (participants != null)
            return participants.size();
        else
            return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView participant_name;
        public ImageView profile_pic;

        public ViewHolder(View chatView) {
            super(chatView);
            participant_name = chatView.findViewById(R.id.participant_name);
            profile_pic = chatView.findViewById(R.id.profile_pic);
        }
    }
}
