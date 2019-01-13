package com.example.ywh.locality.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
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

import java.util.ArrayList;
import java.util.List;

public class AddChatAdapter extends RecyclerView.Adapter<AddChatAdapter.ViewHolder> {
    Context context;
    List<String> participants;
    public List<String> selectedUsers = new ArrayList<>();

    public AddChatAdapter(Context context, List<String> participants) {
        this.context = context;
        this.participants = participants;
    }

    @NonNull
    @Override
    public AddChatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.create_chat_item, viewGroup, false);
        return new AddChatAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AddChatAdapter.ViewHolder viewHolder, int i) {
        if (participants != null) {
            final String participant = participants.get(i);
            final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users").child(participant);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    viewHolder.participant_name.setText(user.getLastName() + " " + user.getFirstName());

                    if (user.getProfilePic().equals("default")) {
                        viewHolder.profile_pic.setImageResource(R.drawable.default_profile);
                    } else {
                        Glide.with(context).load(user.getProfilePic()).into(viewHolder.profile_pic);
                    }

                    viewHolder.setItemClickListener(new ItemClickListener() {
                        @Override
                        public void onItemClick(View v, int position) {
                            CheckBox checkBox = (CheckBox) v;
                            if (checkBox.isChecked()) {
                                selectedUsers.add(participants.get(position));
                            } else {
                                selectedUsers.remove(participants.get(position));
                            }
                        }
                    });
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

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView participant_name;
        public ImageView profile_pic;
        public CheckBox check_box;
        ItemClickListener itemClickListener;

        public ViewHolder(View view) {
            super(view);
            participant_name = view.findViewById(R.id.participant_name);
            profile_pic = view.findViewById(R.id.profile_pic);
            check_box = view.findViewById(R.id.checkBox);

            check_box.setOnClickListener(this);
        }

        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View v) {
            this.itemClickListener.onItemClick(v, getLayoutPosition());
        }
    }

    public interface ItemClickListener {
        void onItemClick(View v, int position);
    }
}
