package com.example.ywh.locality.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.ywh.locality.GroupChatActivity;
import com.example.ywh.locality.Model.ChatGroup;
import com.example.ywh.locality.Model.ChatMessage;
import com.example.ywh.locality.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    private Context context;
    private List<ChatGroup> groups;
    private String lastGroupMessage;
    private String lastMessageSender;

    public ChatAdapter(Context context, List<ChatGroup> groups) {
        this.context = context;
        this.groups = groups;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_item, viewGroup, false);
        return new ChatAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        if (groups != null) {
            final ChatGroup group = groups.get(i);
            viewHolder.chat_name.setText(group.getGroupName());

            if (group.getGroupIcon().equals("default")) {
                viewHolder.group_pic.setImageResource(R.drawable.ic_group_icon);
            } else {
                Glide.with(context).load(group.getGroupIcon()).into(viewHolder.group_pic);
            }

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("chatMessage");
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    lastGroupMessage = "default";
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        ChatMessage message = snapshot.getValue(ChatMessage.class);
                        if (group.getGroupID().equals(message.getGroup())) {
                            lastGroupMessage = message.getMessage();
                            lastMessageSender = message.getSenderName();
                        }
                    }

                    if (lastGroupMessage.equals("default")) {
                        viewHolder.last_message.setVisibility(View.GONE);
                    } else {
                        viewHolder.last_message.setVisibility(View.VISIBLE);
                        viewHolder.last_message.setText(lastMessageSender+": "+lastGroupMessage);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String groupID = group.getGroupID();
                    Intent intent = new Intent(context, GroupChatActivity.class);
                    intent.putExtra("groupID", groupID);
                    context.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (groups != null)
            return groups.size();
        else
            return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView chat_name;
        public ImageView group_pic;
        public TextView last_message;

        public ViewHolder(View chatView) {
            super(chatView);
            chat_name = chatView.findViewById(R.id.group_chat_name);
            group_pic = chatView.findViewById(R.id.group_pic);
            last_message = chatView.findViewById(R.id.last_message);
        }
    }
}
