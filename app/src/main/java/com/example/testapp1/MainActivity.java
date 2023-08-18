package com.example.testapp1;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.testapp1.databinding.ActivityMainBinding;
import com.example.testapp1.fragments.FragmentAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    ActivityResultLauncher<String> launcher;
    FragmentAdapter allFragmentAdapter;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);


        // userimage picker
        launcher = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            ProgressDialog pd = new ProgressDialog(MainActivity.this);

            @Override
            public void onActivityResult(Uri result) {
                try {
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
                                            Toast.makeText(MainActivity.this, "Image Uploaded", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                        }
                    });

                } catch (Exception e) {
                    pd.dismiss();
                }
            }
        });


        //userimage ends

        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Recent chats"));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Users"));

        // fragment adapter
        FragmentManager fragmentManager = getSupportFragmentManager();
        allFragmentAdapter = new FragmentAdapter(fragmentManager, getLifecycle());
        binding.viewPager2.setAdapter(allFragmentAdapter);
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                binding.viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        binding.viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                binding.tabLayout.selectTab(binding.tabLayout.getTabAt(position));
            }
        });
        // viewpager ends


    }

    // option menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mainactivity_toolbar_menu_items, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.setuserimage) {
            launcher.launch("image/*");
        } else if (item.getItemId() == R.id.setusername) {
            Dialog dialog = new Dialog(MainActivity.this);
            dialog.setContentView(R.layout.activity_setusername);
            EditText editText = dialog.findViewById(R.id.edittextsetusername);
            Button button = dialog.findViewById(R.id.buttonsave);
            dialog.show();
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String username = editText.getText().toString().trim();
                    ProgressDialog pd = new ProgressDialog(MainActivity.this);
                    pd.setMessage("Setting up");
                    pd.show();

                    firebaseDatabase.getReference().child("users").child(firebaseAuth.getUid()).child("username").setValue(username).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                pd.dismiss();
                                Toast.makeText(MainActivity.this, "Username set", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            } else {
                                pd.dismiss();
                                Toast.makeText(MainActivity.this, "Check Internet Connectivity ", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    ;
                }
            });


        } else if (item.getItemId() == R.id.logout) {
            Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
            firebaseAuth.signOut();
            // shared pref
            SharedPreferences preferences = getSharedPreferences("login", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("flag", false);
            editor.apply();
            // shared pref

            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();


        } else if (item.getItemId() == R.id.deleteaccount) {
            try {
                firebaseDatabase.getReference().child("users").child(firebaseAuth.getUid()).removeValue();

            } catch (Exception e) {

            }
            try {
                firebaseStorage.getReference().child(firebaseAuth.getUid()).delete();

            } catch (Exception e) {

            }


            firebaseAuth.signOut();

            // shared pref
            SharedPreferences preferences = getSharedPreferences("login", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("flag", false);
            editor.apply();
            // shared pref

            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            Toast.makeText(MainActivity.this, "Your all data deleted", Toast.LENGTH_SHORT).show();


        }

        return super.onOptionsItemSelected(item);
    }

    //option menu ends


    @Override
    public void onBackPressed() {
        if (binding.tabLayout.getSelectedTabPosition() == 0) {
            super.onBackPressed();
        } else {
            binding.viewPager2.setCurrentItem(0);
        }
    }
}