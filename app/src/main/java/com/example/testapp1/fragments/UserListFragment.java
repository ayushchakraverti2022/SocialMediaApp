package com.example.testapp1.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.testapp1.R;
import com.example.testapp1.recycleradapters.RecyclerAdapterUsersFragment;
import com.example.testapp1.databinding.FragmentUserListBinding;
import com.example.testapp1.modelFolder.UserListModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.regex.Pattern;


public class UserListFragment extends Fragment {
    FragmentUserListBinding binding;

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    ArrayList<UserListModel> userModels = new ArrayList<UserListModel>();
    RecyclerAdapterUsersFragment recyclerAdapterUsersFragment;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();

    public UserListFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // firebaseDatabase getting and setting in Recyclerview
        firebaseDatabase.getReference().child("users").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userModels.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    if (snapshot.exists()) {
                        String usrimage = null;String usridentity = null;String usrname = null;

                        if (snapshot1.child("userimage").exists()) {
                            usrimage = snapshot1.child("userimage").getValue().toString();
                        }
                        if (snapshot1.child("username").exists()) {
                            String fuid = String.valueOf(firebaseDatabase.getReference().child("user").child(firebaseAuth.getUid()).getKey());
                            String suid = snapshot1.getKey().trim();
                            if(Pattern.matches(fuid,suid)){
                                usridentity = "You";
                            }
                            usrname = snapshot1.child("username").getValue().toString();                       }

                        userModels.add(0,new UserListModel(usridentity,usrimage, usrname,snapshot1));

                    }
                }
                recyclerAdapterUsersFragment.notifyDataSetChanged();

            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        // firebaseDatabase getting and setting in Recyclerview ends

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_list, container, false);
        binding = FragmentUserListBinding.bind(view);
        // recycler view adapter
        recyclerAdapterUsersFragment = new RecyclerAdapterUsersFragment(getContext(), userModels);
       LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
       binding.recyclerviewuserlist.setLayoutManager(linearLayoutManager);

       binding.recyclerviewuserlist.setAdapter(recyclerAdapterUsersFragment);


       // recycler view ends

        return view;
    }
}