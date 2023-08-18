package com.example.testapp1.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.testapp1.R;
import com.example.testapp1.databinding.FragmentUserListBinding;
import com.example.testapp1.modelFolder.RecentUserModel;
import com.example.testapp1.recycleradapters.RecyclerAdapterRecentUserFragment;
import com.example.testapp1.recycleradapters.RecyclerAdapterUsersFragment;
import com.example.testapp1.databinding.FragmentRecentUserBinding;
import com.example.testapp1.modelFolder.UserListModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class RecentUserFragment extends Fragment {

    FragmentRecentUserBinding binding;

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    ArrayList<RecentUserModel> userModels = new ArrayList<RecentUserModel>();
    ArrayList<String> recusers = new ArrayList<>();
    RecyclerAdapterRecentUserFragment recyclerAdapterUsersFragment;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    public RecentUserFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // firebaseDatabase getting and setting in Recyclerview
        firebaseDatabase.getReference().child("users").child(firebaseAuth.getUid()).child("recentusers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snapshot1:snapshot.getChildren()){
                    recusers.add(snapshot1.getKey());
                    Log.d("recuser",snapshot1.getKey());
                    {
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

                                            usrname = snapshot1.child("username").getValue().toString();
                                            if(recusers.contains( snapshot1.getKey())){

                                        userModels.add(new RecentUserModel(usridentity,usrimage, usrname,snapshot1));}
}
                                    }
                                }
                                recyclerAdapterUsersFragment.notifyDataSetChanged();



                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_recent_user, container, false);
        binding = FragmentRecentUserBinding.bind(view);
        recyclerAdapterUsersFragment = new RecyclerAdapterRecentUserFragment(getContext(), userModels);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.recyclerviewrecentusers.setLayoutManager(linearLayoutManager);

        binding.recyclerviewrecentusers.setAdapter(recyclerAdapterUsersFragment);




        return view;
    }
}