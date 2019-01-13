package com.example.ywh.locality;

import android.app.ActionBar;
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
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.ywh.locality.Adapter.GroupParticipantAdapter;
import com.example.ywh.locality.Model.ChatGroup;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetChatTitle extends AppCompatActivity {
    private CircleImageView selectGroupIcon;
    private TextView groupNumber;
    private EditText groupName;
    private RecyclerView recyclerView;
    private GroupParticipantAdapter participantAdapter;
    private Intent intent1;
    private ImageButton back;
    private List<String> participants = new ArrayList<>();
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    private static final int request = 1;
    private Uri imageUri;
    private String url;
    private StorageTask stask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_chat_title);

        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.bar_color));
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.set_chat_title_bar);
        View view = getSupportActionBar().getCustomView();
        back = view.findViewById(R.id.back);

        selectGroupIcon = findViewById(R.id.select_photo);
        groupNumber = findViewById(R.id.member_number);
        groupName = findViewById(R.id.group_name);
        recyclerView = findViewById(R.id.member_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        intent1 = getIntent();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        ArrayList<String> members = intent1.getStringArrayListExtra("memberList");
        for (int i = 0; i < members.size(); i++) {
            participants.add(members.get(i));
        }
        participants.add(firebaseUser.getUid());
        showParticipants(participants);

        groupNumber.setText("Participants: "+participants.size());

        url = "default";
        storageReference = FirebaseStorage.getInstance().getReference("groupIcon");
        selectGroupIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        back.setOnClickListener(v -> finish());

        FloatingActionButton create_group = findViewById(R.id.create_group_btn);
        create_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createGroup();
            }
        });
    }

    private void createGroup() {
        if (groupName.getText().toString().matches("")) {
            Toast.makeText(SetChatTitle.this, "Please Type Group Name", Toast.LENGTH_SHORT).show();
        } else {
            ChatGroup chatGroup = new ChatGroup(groupName.getText().toString(), firebaseUser.getUid());
            chatGroup.setGroupIcon(url);
            chatGroup.setJoinedMemberList(participants);

            databaseReference = FirebaseDatabase.getInstance().getReference("chatgroup");
            String key = databaseReference.push().getKey();
            chatGroup.setGroupID(key);
            databaseReference.child(key).setValue(chatGroup);

            finish();
        }
    }

    private void showParticipants(List<String> participants) {
        participantAdapter = new GroupParticipantAdapter(SetChatTitle.this, participants);
        recyclerView.setAdapter(participantAdapter);
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, request);
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = SetChatTitle.this.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage() {
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
                        url = downloadUri.toString();

                        Glide.with(SetChatTitle.this).load(url).into(selectGroupIcon);
                        Snackbar.make(findViewById(R.id.set_title_layout), "Update Successfully!", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    } else {
                        Toast.makeText(SetChatTitle.this, "Update Failed!", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(SetChatTitle.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(SetChatTitle.this, "No image is selected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == request && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            if (stask != null && stask.isInProgress()) {
                Toast.makeText(SetChatTitle.this, "Upload in progress", Toast.LENGTH_SHORT).show();
            } else {
                uploadImage();
            }
        }
    }
}
