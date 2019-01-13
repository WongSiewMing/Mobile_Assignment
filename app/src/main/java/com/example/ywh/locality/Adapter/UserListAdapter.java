package com.example.ywh.locality.Adapter;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.example.ywh.locality.Model.Assistance;
import com.example.ywh.locality.Model.User;
import com.example.ywh.locality.R;
import com.google.firebase.database.DataSnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.UserViewHolder> implements Filterable{
    private ArrayList<DataSnapshot> userList;
    private ArrayList<DataSnapshot> userListFull;
    private ArrayList<String> selectedList = new ArrayList<>();
    public CheckBox checkBox;



    public static class UserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public CircleImageView mProfilePic;
        public TextView mName;
        public TextView mEmail;
        public CheckBox checkBox;
        ItemClickListener itemClickListener;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            mProfilePic = itemView.findViewById(R.id.profilePic);
            mName = itemView.findViewById(R.id.nameField);
            mEmail = itemView.findViewById(R.id.emailField);
            checkBox = itemView.findViewById(R.id.checkBox);
            checkBox.setOnClickListener(this);
        }

        public void setItemClickListener(ItemClickListener itemClickListener){
            this.itemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View v) {
            this.itemClickListener.onItemClick(v, getLayoutPosition());
        }
    }

    public UserListAdapter(ArrayList<DataSnapshot> userList){
        this.userList = userList;
        userListFull = new ArrayList<>(userList);
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v =LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cardview_userlist,viewGroup,false);
        UserViewHolder uvh = new UserViewHolder(v);
        return uvh;
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder userViewHolder, int i) {
        DataSnapshot currentItem = userList.get(i);
        Picasso.get().load(Uri.parse(currentItem.getValue(User.class).getProfilePic())).into(userViewHolder.mProfilePic);
        userViewHolder.mEmail.setText(currentItem.getValue(User.class).getEmail());
        userViewHolder.mName.setText(currentItem.getValue(User.class).getLastName()+" "+currentItem.getValue(User.class).getFirstName());

        userViewHolder.setItemClickListener((v, position) -> {
            CheckBox checkBox = (CheckBox)v;
                if(checkBox.isChecked()){
                    selectedList.add(currentItem.getKey());
                }
                else{
                    selectedList.remove(currentItem.getKey());
                }



        });

    }
    public ArrayList<String> getSelectedList(){
        return selectedList;

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    @Override
    public Filter getFilter() {
        return userFilter;
    }

    private Filter userFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<DataSnapshot> filteredList = new ArrayList<>();
            if(constraint == null || constraint.length()==0){
                filteredList.addAll(userListFull);
            }else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for(DataSnapshot snapshot : userListFull){
                    if((snapshot.getValue(User.class).getEmail().toLowerCase().contains(filterPattern))||((snapshot.getValue(User.class).getLastName().toLowerCase().contains(filterPattern))||
                            ((snapshot.getValue(User.class).getFirstName().toLowerCase().contains(filterPattern))))){
                        filteredList.add(snapshot);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values=filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            userList.clear();
            userList.addAll((ArrayList)results.values);
            notifyDataSetChanged();

        }
    };

    public interface ItemClickListener{
        void onItemClick(View v, int position);
    }
}
