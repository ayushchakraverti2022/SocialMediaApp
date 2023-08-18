package com.example.testapp1;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.testapp1.databinding.ActivityUserWelcomeBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class UserWelcomeActivity extends AppCompatActivity {
    ActivityResultLauncher<String> launcher;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    ActivityUserWelcomeBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserWelcomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        launcher = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            ProgressDialog pd = new ProgressDialog(UserWelcomeActivity.this);
            @Override
            public void onActivityResult(Uri result) {
                try{
                    pd.setCanceledOnTouchOutside(false);
                    pd.setMessage("Uploading");
                    pd.show();
                    final StorageReference storageReference = firebaseStorage.getReference().child(firebaseAuth.getUid());
                    storageReference.putFile(result).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    firebaseDatabase.getReference().child("users").child(firebaseAuth.getUid()).child("userimage").setValue(uri.toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            pd.dismiss();
                                            Toast.makeText(UserWelcomeActivity.this, "Image Uploaded", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                        }
                    });

                }catch (Exception e){
                    pd.dismiss();
                }
            }
        });

        binding.imageviewuserimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launcher.launch("image/*");
            }
        });



       firebaseDatabase.getReference().child("users").child(firebaseAuth.getUid()).child("userimage").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                Picasso.get().load(snapshot.getValue().toString()).into(binding.imageviewuserimage);}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        binding.buttonsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = binding.edittextusername.getText().toString().trim();
                if(!username.isEmpty()){
                ProgressDialog pd = new ProgressDialog(UserWelcomeActivity.this);
                pd.setMessage("Setting up");
                pd.setCanceledOnTouchOutside(false);
                pd.show();


                firebaseDatabase.getReference().child("users").child(firebaseAuth.getUid()).child("username").setValue(username).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()){
                            pd.dismiss();
                            Toast.makeText(UserWelcomeActivity.this, "Username set", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(UserWelcomeActivity.this, MainActivity.class);
                            // shared pref
                            SharedPreferences sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
                            SharedPreferences.Editor editor =   sharedPreferences.edit();
                            editor.putBoolean("flag", true);
                            editor.apply();


                            // shared pref


                            startActivity(intent);
                            finish();


                        }else{
                            pd.dismiss();
                            Toast.makeText(UserWelcomeActivity.this, "Check Internet Connectivity ", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                ;
            }else{
                    Toast.makeText(UserWelcomeActivity.this, "Enter your name", Toast.LENGTH_SHORT).show();

            }}
        });




    }
}