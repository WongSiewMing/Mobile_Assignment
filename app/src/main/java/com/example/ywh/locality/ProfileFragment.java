package com.example.ywh.locality;
import android.app.ActionBar;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import de.hdodenhof.circleimageview.CircleImageView;


import static android.app.Activity.RESULT_OK;


public class ProfileFragment extends Fragment implements View.OnClickListener{

    private CircleImageView mProfilePic;
    private TextView mWelcomeUser;
    private TextView mChangeProfilePic;
    private TextView mEditDetails;
    private TextView mEditAddress;
    private TextView mChangePassword;
    private TextView mViewFamilyList;
    private TextView mAddFamilyList;
    private Button mLogout;
    private FirebaseDatabase mFirebaseDB;
    private FirebaseAuth mAuth =FirebaseAuth.getInstance();
    private FirebaseUser currentUser= mAuth.getCurrentUser();
    private DatabaseReference databaseReference;
    private String userID;
    private String displayName;
    private StorageReference storageReference;
    private static final int request = 1;
    private Uri imageUri;
    private StorageTask stask;
    private View rootView;




   @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       rootView = inflater.inflate(R.layout.fragment_profile, container, false);

       MainActivity activity;
       activity = (MainActivity)getActivity();
       activity.getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.bar_color));
       activity.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
       activity.getSupportActionBar().setDisplayShowCustomEnabled(true);
       activity.getSupportActionBar().setCustomView(R.layout.profile_bar);

       mProfilePic = rootView.findViewById(R.id.profilePic);
       mWelcomeUser = rootView.findViewById(R.id.HelloUser);
       mChangeProfilePic = rootView.findViewById(R.id.change_profilePic);
       mEditDetails = rootView.findViewById(R.id.edit_PersonalDetails);
       mEditAddress = rootView.findViewById(R.id.edit_address);
       mChangePassword = rootView.findViewById(R.id.change_password);
       mViewFamilyList = rootView.findViewById(R.id.viewFamilyList);
       mAddFamilyList = rootView.findViewById(R.id.addFamilyList);
       mLogout = rootView.findViewById(R.id.logout_btn);



       mFirebaseDB = FirebaseDatabase.getInstance();
       databaseReference = mFirebaseDB.getReference().child("users");
       userID = currentUser.getUid();
       displayName = currentUser.getDisplayName();
       storageReference = FirebaseStorage.getInstance().getReference("profilePic");
       mChangeProfilePic.setOnClickListener(this);
       mEditDetails.setOnClickListener(this);
       mEditAddress.setOnClickListener(this);
       mChangePassword.setOnClickListener(this);
       mViewFamilyList.setOnClickListener(this);
       mAddFamilyList.setOnClickListener(this);
       mLogout.setOnClickListener(this);


       if(currentUser.getPhotoUrl()!=null){

           Uri profilePic = currentUser.getPhotoUrl();
           Picasso.get().load(profilePic).into(mProfilePic);

       }
       mWelcomeUser.setText("Welcome "+displayName);


       return rootView;



   }


    @Override
    public void onClick(View v) {
       int id = v.getId();
       if(id==R.id.change_profilePic){
           openGallery();
       }
       else if(id==R.id.edit_PersonalDetails){
           Intent intent = new Intent(getActivity(),EditUserDetails.class);
           startActivity(intent);

       }
       else if(id==R.id.edit_address){
           Intent intent = new Intent(getActivity(),ViewAddressActivity.class);
           startActivity(intent);

       }
       else if(id==R.id.change_password){
           Intent intent = new Intent(getActivity(),ChangePasswordActivity.class);
           startActivity(intent);

       }
       else if(id==R.id.viewFamilyList){
           Intent intent = new Intent(getActivity(),ViewFamilyFriendsActivity.class);
           startActivity(intent);

       }
       else if(id==R.id.addFamilyList){
           Intent intent = new Intent(getActivity(),AddFamilyFriendsListActivity.class);
           startActivity(intent);

       }
       else if(id==R.id.logout_btn){
           FirebaseAuth.getInstance().signOut();
           Intent intent = new Intent(getActivity(),LoginActivity.class);
           intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
           intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
           intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
           startActivity(intent);

       }

    }

    private void uploadImage() {
       if (imageUri != null) {

            final StorageReference reference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));
            reference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                   reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setPhotoUri(uri).build();
                            currentUser.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Snackbar.make(rootView.findViewById(R.id.userProfileLayout), "Profile Picture Changed", Snackbar.LENGTH_LONG)
                                                .setAction("Action", null).show();
                                        Uri profilePic = currentUser.getPhotoUrl();
                                        Picasso.get().load(profilePic).into(mProfilePic);
                                        databaseReference.child(userID).child("profilePic").setValue(profilePic.toString());
                                    }
                                    else if(!task.isSuccessful()){
                                        Snackbar.make(rootView.findViewById(R.id.userProfileLayout), "Profile Picture Changing Fail", Snackbar.LENGTH_LONG)
                                                .setAction("Action", null).show();

                                    }
                                }
                            });
                        }
                    });
                }
            });

        }


    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, request);
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == request && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            if (stask != null && stask.isInProgress()) {
                Toast.makeText(getContext(), "Upload in progress", Toast.LENGTH_SHORT).show();
            } else {
                uploadImage();
            }
        }
    }

}
