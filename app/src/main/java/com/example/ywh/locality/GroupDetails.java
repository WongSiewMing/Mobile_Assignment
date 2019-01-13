package com.example.ywh.locality;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.ywh.locality.Adapter.GroupParticipantAdapter;
import com.example.ywh.locality.Model.ChatGroup;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupDetails extends AppCompatActivity {
    private CircleImageView groupIcon;
    private TextView groupName;
    private RecyclerView recyclerView;
    private GroupParticipantAdapter participantAdapter;
    private ImageButton back;
    private EditText newGroupName;
    private RelativeLayout relativeLayout;
    private RelativeLayout relativeLayout1;
    DatabaseReference databaseReference;
    Intent intent1;
    String groupID;
    StorageReference storageReference;
    private static final int request = 1;
    private Uri imageUri;
    private StorageTask stask;
    private boolean unEditable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_details);

        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.bar_color));
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.group_detail_bar);
        View view = getSupportActionBar().getCustomView();
        back = view.findViewById(R.id.back);

        intent1 = getIntent();
        groupID = intent1.getStringExtra("groupID");
        newGroupName = findViewById(R.id.new_group_name);
        relativeLayout = findViewById(R.id.display_group_name);
        relativeLayout1 = findViewById(R.id.edit_group_name);
        groupIcon = findViewById(R.id.group_icon);
        groupName = findViewById(R.id.group_name);
        recyclerView = findViewById(R.id.participant_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        showParticipants(groupID);

        storageReference = FirebaseStorage.getInstance().getReference("chgGroupIcon");
        databaseReference = FirebaseDatabase.getInstance().getReference("chatgroup").child(groupID);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ChatGroup chatGroup = dataSnapshot.getValue(ChatGroup.class);
                groupName.setText(chatGroup.getGroupName());
                if (!GroupDetails.this.isDestroyed()) {
                    if (chatGroup.getGroupIcon().equals("default")) {
                        groupIcon.setImageResource(R.drawable.ic_group_icon);
                    } else {
                        Glide.with(GroupDetails.this).load(chatGroup.getGroupIcon()).into(groupIcon);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        groupIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        FloatingActionButton create_group = findViewById(R.id.add_member_btn);
        create_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addMember();
            }
        });
    }

    private void addMember(){
        Intent intent2 = new Intent(this, AddNewGroupMember.class);
        intent2.putExtra("groupID", groupID);
        startActivity(intent2);
    }

    private void showParticipants(String groupID) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("chatgroup").child(groupID);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ChatGroup group = dataSnapshot.getValue(ChatGroup.class);
                assert group != null;
                participantAdapter = new GroupParticipantAdapter(GroupDetails.this, group.getJoinedMemberList());
                recyclerView.setAdapter(participantAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, request);
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = GroupDetails.this.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage() {
        final ProgressDialog progressDialog = new ProgressDialog(GroupDetails.this);
        progressDialog.setMessage("Uploading");
        progressDialog.show();

        if (imageUri != null) {
            final StorageReference reference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));
            stask = reference.putFile(imageUri);
            stask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return reference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        String url = downloadUri.toString();

                        intent1 = getIntent();
                        String groupID = intent1.getStringExtra("groupID");
                        databaseReference = FirebaseDatabase.getInstance().getReference("chatgroup").child(groupID);
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("groupIcon", url);
                        databaseReference.updateChildren(map);

                        Snackbar.make(findViewById(R.id.group_details_layout), "Update Successfully!", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        progressDialog.dismiss();
                    } else {
                        Toast.makeText(GroupDetails.this, "Update Failed!", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(GroupDetails.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });
        } else {
            Toast.makeText(GroupDetails.this, "No image is selected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == request && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            if (stask != null && stask.isInProgress()) {
                Toast.makeText(GroupDetails.this, "Upload in progress", Toast.LENGTH_SHORT).show();
            } else {
                uploadImage();
            }
        }
    }

    public void cancelRename(View view) {
        relativeLayout.setVisibility(View.VISIBLE);
        relativeLayout1.setVisibility(View.GONE);
    }

    public void renameGroup(View view) {
        if(newGroupName.getText().toString().matches("")){
            Toast.makeText(GroupDetails.this, "Please Type Group Name", Toast.LENGTH_SHORT).show();
        }
        else{
            FirebaseDatabase.getInstance().getReference("chatgroup").child(groupID).child("groupName").setValue(newGroupName.getText().toString());
            relativeLayout.setVisibility(View.VISIBLE);
            relativeLayout1.setVisibility(View.GONE);
        }
    }

    public void editGroupName(View view) {
        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("belongsCity");
        reference1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    String key = snapshot.getKey();
                    System.out.println(key);
                    if(key.equals(groupID)){
                        unEditable = true;
                    }
                }

                if(unEditable){
                    Toast.makeText(GroupDetails.this, "Area Group Chat Unable to Edit", Toast.LENGTH_LONG).show();
                }
                else{
                    String group_name = groupName.getText().toString();
                    relativeLayout.setVisibility(View.GONE);
                    relativeLayout1.setVisibility(View.VISIBLE);
                    newGroupName.setText(group_name);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
