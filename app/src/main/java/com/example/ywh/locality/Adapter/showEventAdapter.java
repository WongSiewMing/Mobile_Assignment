package com.example.ywh.locality.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ywh.locality.Event;
import com.example.ywh.locality.R;
import com.example.ywh.locality.showEventDetails;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class showEventAdapter extends RecyclerView.Adapter<showEventAdapter.myViewHolder> {

    private Context mContext;
    private List<Event> events;


    public showEventAdapter(Context mContext, List<Event> events) {
        this.mContext = mContext;
        this.events = events;
    }

    @Override
    public myViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.event_card_item,viewGroup,false);
        return new showEventAdapter.myViewHolder(view);

    }

    @Override
    public void onBindViewHolder(myViewHolder myViewHolder, int i) {

        if(events != null) {
            final Event event = events.get(i);
            long datelong = event.getEventDate();
            Date eventdate = new Date(datelong);
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

            String eventType = event.getEventType();

            if(eventType.equals("Indoor")){
                if(event.getImgNum()==0)
                    myViewHolder.event_pic.setImageResource(R.drawable.indoor_pic_1);
                else if(event.getImgNum()==1)
                    myViewHolder.event_pic.setImageResource(R.drawable.indoor_pic_2);
                else
                    myViewHolder.event_pic.setImageResource(R.drawable.indoor_pic_3);

            }
            if(eventType.equals("Outdoor")){
                if(event.getImgNum()==0)
                    myViewHolder.event_pic.setImageResource(R.drawable.outdoor_pic_1);
                else if(event.getImgNum()==1)
                    myViewHolder.event_pic.setImageResource(R.drawable.outdoor_pic_2);
                else
                    myViewHolder.event_pic.setImageResource(R.drawable.outdoor_pic_3);

            }
            if(eventType.equals("Sports")){
                if(event.getImgNum()==0)
                    myViewHolder.event_pic.setImageResource(R.drawable.sports_pic_1);
                else if(event.getImgNum()==1)
                    myViewHolder.event_pic.setImageResource(R.drawable.sports_pic_2);
                else
                    myViewHolder.event_pic.setImageResource(R.drawable.sports_pic_3);
            }
            if(eventType.equals("Community")){
                if(event.getImgNum()==0)
                    myViewHolder.event_pic.setImageResource(R.drawable.community_pic_1);
                else if(event.getImgNum()==1)
                    myViewHolder.event_pic.setImageResource(R.drawable.community_pic_2);
                else
                    myViewHolder.event_pic.setImageResource(R.drawable.community_pic_3);
            }

            myViewHolder.event_title.setText(event.getEventTitle());
            myViewHolder.event_date.setText(String.valueOf(formatter.format(eventdate)));
            myViewHolder.event_view_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String eventID = event.getEventID();
                    Intent intent = new Intent(mContext, showEventDetails.class);
                    intent.putExtra("eventID", eventID);
                    mContext.startActivity(intent);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        if(events!=null)
            return events.size();
        else
            return 0;
    }

    public class myViewHolder extends RecyclerView.ViewHolder{

        public ImageView event_pic;
        public TextView event_title;
        public TextView event_date;
        public Button event_view_btn;

        public myViewHolder(View itemView){
            super(itemView);
            event_pic = itemView.findViewById(R.id.event_item_img);
            event_title = itemView.findViewById(R.id.event_item_title);
            event_date = itemView.findViewById(R.id.event_item_date);
            event_view_btn = itemView.findViewById(R.id.event_item_view_btn);
        }
    }
}
