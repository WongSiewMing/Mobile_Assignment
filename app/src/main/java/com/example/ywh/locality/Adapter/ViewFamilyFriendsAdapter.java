package com.example.ywh.locality.Adapter;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ywh.locality.Model.Assistance;
import com.example.ywh.locality.Model.User;
import com.example.ywh.locality.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class ViewFamilyFriendsAdapter extends RecyclerView.Adapter<ViewFamilyFriendsAdapter.ViewFamilyViewHolder>{
    private List<String> mFamilyFriendsList;
    private ArrayList<String> mFamilyFriendsListFull;



    public static class ViewFamilyViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView mProfilePic;
        public TextView mName;
        public TextView mEmail;

        public ViewFamilyViewHolder(View itemView) {
            super(itemView);
            mProfilePic = itemView.findViewById(R.id.profilePic);
            mName = itemView.findViewById(R.id.nameField);
            mEmail = itemView.findViewById(R.id.emailField);
        }
    }

    public ViewFamilyFriendsAdapter(List<String> familyfriendsList) {
        mFamilyFriendsList = familyfriendsList;
        mFamilyFriendsListFull = new ArrayList<>(familyfriendsList);
    }


    @Override
    public ViewFamilyFriendsAdapter.ViewFamilyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cardview_viewuser, viewGroup, false);
        ViewFamilyViewHolder uvh = new ViewFamilyViewHolder(v);
        return uvh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewFamilyFriendsAdapter.ViewFamilyViewHolder userViewHolder, int i) {
        String currentItem = mFamilyFriendsList.get(i);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users").child(currentItem);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Picasso.get().load(Uri.parse(user.getProfilePic())).into(userViewHolder.mProfilePic);
                userViewHolder.mEmail.setText(user.getEmail());
                userViewHolder.mName.setText(user.getLastName() + " " + user.getFirstName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    public int getItemCount() {
        return mFamilyFriendsList.size();
    }


}

