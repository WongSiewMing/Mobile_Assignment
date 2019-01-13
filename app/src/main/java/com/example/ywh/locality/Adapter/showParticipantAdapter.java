package com.example.ywh.locality.Adapter;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ywh.locality.R;
import com.example.ywh.locality.Model.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class showParticipantAdapter extends RecyclerView.Adapter<showParticipantAdapter.UserViewHolder> {

    private ArrayList<User> mParticipantList;

    public static class UserViewHolder extends RecyclerView.ViewHolder{
        public ImageView mProfilePic;
        public TextView mName;
        public TextView mEmail;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            mProfilePic = itemView.findViewById(R.id.profilePic);
            mName = itemView.findViewById(R.id.nameField);
            mEmail = itemView.findViewById(R.id.emailField);
        }
    }

    public showParticipantAdapter(ArrayList<User> userList){
        mParticipantList = userList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v =LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.participant_cardview,viewGroup,false);
        UserViewHolder uvh = new UserViewHolder(v);
        return uvh;
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder userViewHolder, int i) {
        User currentItem = mParticipantList.get(i);
        Picasso.get().load(Uri.parse(currentItem.getProfilePic())).into(userViewHolder.mProfilePic);
        userViewHolder.mProfilePic.setImageURI(Uri.parse(currentItem.getProfilePic()));
        userViewHolder.mEmail.setText(currentItem.getEmail());
        userViewHolder.mName.setText(currentItem.getLastName()+" "+currentItem.getFirstName());

    }

    @Override
    public int getItemCount() {
        return mParticipantList.size();
    }
}
