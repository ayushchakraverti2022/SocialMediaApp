package com.example.testapp1;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.testapp1.databinding.ActivityChatBinding;
import com.example.testapp1.modelFolder.ChatModel;
import com.example.testapp1.recycleradapters.RecyclerAdapterChat;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class ChatActivity extends AppCompatActivity {
    ActivityChatBinding binding;
    ActivityResultLauncher<String> launcher;

    String friendUID = null;
    ArrayList<ChatModel> chatModel = new ArrayList<ChatModel>();
    RecyclerAdapterChat recyclerAdapterChat ;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    String userID = firebaseAuth.getUid();   // USerID
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbarchat);

        Intent intent = getIntent();
        String pname = intent.getStringExtra("username");
        String pimage = intent.getStringExtra("userimage");
        friendUID = intent.getStringExtra("snapshot");   // friend UserID
        binding.username.setText(pname);

        //imagepickerlauncher
        launcher = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            ProgressDialog pd = new ProgressDialog(ChatActivity.this);
            @Override
            public void onActivityResult(Uri result) {
                try{
                    String time2 = String.valueOf(System.currentTimeMillis());
                    pd.setCanceledOnTouchOutside(false);
                    pd.setMessage("Uploading");
                    pd.show();
                    final StorageReference storageReference = firebaseStorage.getReference().child("chatimage").child(userID).child(time2);
                    storageReference.putFile(result).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String message = uri.toString();
                                    if(!message.isEmpty()){

                                        if(Pattern.matches(friendUID,userID)){
                                            firebaseDatabase.getReference().child("users").child(userID).child("recentusers")
                                                    .child(userID).child(time2).child(userID).setValue(message);
                                            pd.dismiss();
                                        }else{
                                            firebaseDatabase.getReference().child("users").child(userID).child("recentusers")
                                                    .child(friendUID).child(time2).child(userID).setValue(message);
                                            firebaseDatabase.getReference().child("users").child(friendUID).child("recentusers")
                                                    .child(userID).child(time2).child(userID).setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                            Toast.makeText(ChatActivity.this, "sent", Toast.LENGTH_SHORT).show();
                                                            pd.dismiss();
                                                        }
                                                    });
                                        }
                                    }

                                }
                            });
                        }
                    });

                }catch (Exception e){
                    pd.dismiss();
                }
            }
        });
        //imagepickerlauncher



        // userimage viewwer
         if(pimage!=null){
        binding.userimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(ChatActivity.this);
                dialog.setContentView(R.layout.imageviewlayout);
                ImageView imageView2 = dialog.findViewById(R.id.anyimage);
                Picasso.get().load(pimage).into(imageView2);

                dialog.show();
            }
        });}
        // userimage viewwer





        // UserImage setter
        if(pimage==null){
            binding.userimage.setImageResource(R.drawable.defaultuserprofile);
        }else {
            Picasso.get().load(pimage).into(binding.userimage);
        }

        //getting image message
        binding.imagechat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                   launcher.launch("image/*");
            }
        });
        //getting image message


        // message sending code
        binding.imageViewSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String time = String.valueOf(System.currentTimeMillis());
                String message = binding.editTextchatTab.getText().toString();
                if(!message.isEmpty()){

                    if(Pattern.matches(friendUID,userID)){
                        firebaseDatabase.getReference().child("users").child(userID).child("recentusers")
                                .child(userID).child(time).child(userID).setValue(message);
                    }else{
                        firebaseDatabase.getReference().child("users").child(userID).child("recentusers")
                                .child(friendUID).child(time).child(userID).setValue(message);
                        firebaseDatabase.getReference().child("users").child(friendUID).child("recentusers")
                                .child(userID).child(time).child(userID).setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(ChatActivity.this, "sent", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }

                binding.editTextchatTab.getText().clear();
            }
        });
        // message send code ends


        // recycler adapter here
        recyclerAdapterChat = new RecyclerAdapterChat(chatModel,ChatActivity.this);
        binding.recyclerViewChat.setLayoutManager(new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false));
        binding.recyclerViewChat.setAdapter(recyclerAdapterChat);
        //recycerl adapter ends


        //setting message to chat activity
        firebaseDatabase.getReference().child("users").child(userID).child("recentusers")
                .child(friendUID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        chatModel.clear();
                        for (DataSnapshot snapshot1:snapshot.getChildren()){
                            Log.d("timedd", snapshot1.getKey());
                            for(DataSnapshot snapshot2:snapshot1.getChildren()){
                                chatModel.add(new ChatModel(snapshot2.getKey(),snapshot.getKey(),snapshot2.getValue().toString(),snapshot1.getKey()));
                                  }
                        }
                        recyclerAdapterChat.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        //setting message to chat activity ends





    }// oncreate ends


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = new MenuInflater(ChatActivity.this);
        menuInflater.inflate(R.menu.recentchatactivity_toolbar_menu_items,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.clearchats){
            try {
                firebaseDatabase.getReference().child("users").child(userID).child("recentusers").child(friendUID).removeValue();

            }catch (Exception e){}

            try {
                firebaseStorage.getReference().child("chatimage").child(userID).delete();

            }catch (Exception e){}




        }else if(item.getItemId()==R.id.videocall){
            Toast.makeText(this, "Working on it ", Toast.LENGTH_SHORT).show();
        };
        return super.onOptionsItemSelected(item);
    }
}