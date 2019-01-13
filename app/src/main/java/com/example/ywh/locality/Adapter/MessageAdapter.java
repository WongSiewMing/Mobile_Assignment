package com.example.ywh.locality.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ywh.locality.Model.ChatMessage;
import com.example.ywh.locality.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    public static final int receiverMessage = 0;
    public static final int senderMessage = 1;
    private Context context;
    private List<ChatMessage> chatList;
    private String receiverImage;
    FirebaseUser firebaseUser;

    public MessageAdapter(Context context, List<ChatMessage> chatList, String receiverImage) {
        this.context = context;
        this.chatList = chatList;
        this.receiverImage = receiverImage;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (i == senderMessage) {
            View view = LayoutInflater.from(context).inflate(R.layout.sender_chat_item, viewGroup, false);
            return new MessageAdapter.ViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.receiver_chat_item, viewGroup, false);
            return new MessageAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder viewHolder, int i) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        ChatMessage chatMessage = chatList.get(i);
        if (!chatMessage.getSenderName().equals(firebaseUser.getDisplayName())) {
            viewHolder.sender.setText(chatMessage.getSenderName());
        }
        viewHolder.message.setText(chatMessage.getMessage());

        Date time = calculateTime(chatMessage.getSentTime());
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm aa");
        viewHolder.time.setText(dateFormat.format(time));

        /*if(receiverImage.equals("default")){
             viewHolder.profile_pic.setImageResource(R.drawable.ic_receiver);
        }
        else{
             Glide.with(context).load(receiverImage).into(viewHolder.profile_pic);
        }*/
    }

    @Override
    public int getItemCount() {
        if (chatList != null)
            return chatList.size();
        else
            return 0;
    }

    private Date calculateTime(long time) {
        Date date1 = new Date();
        try {
            Date date = new Date(time);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm aa");
            String dateText = dateFormat.format(date);
            date1 = dateFormat.parse(dateText);
        } catch (Exception e) {
        }
        return date1;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView message;
        public TextView sender;
        public ImageView profile_pic;
        public TextView time;

        public ViewHolder(View chatView) {
            super(chatView);
            sender = chatView.findViewById(R.id.sender_name);
            message = chatView.findViewById(R.id.message);
            profile_pic = chatView.findViewById(R.id.profile_pic);
            time = chatView.findViewById(R.id.time);
        }
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (chatList.get(position).getSenderID().equals(firebaseUser.getUid())) {
            return senderMessage;
        } else {
            return receiverMessage;
        }
    }
}
