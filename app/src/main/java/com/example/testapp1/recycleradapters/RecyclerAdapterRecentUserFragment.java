package com.example.testapp1.recycleradapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testapp1.ChatActivity;
import com.example.testapp1.R;
import com.example.testapp1.modelFolder.RecentUserModel;
import com.example.testapp1.modelFolder.UserListModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RecyclerAdapterRecentUserFragment extends RecyclerView.Adapter<RecyclerAdapterRecentUserFragment.viewHolder> {

    Context context;

    ArrayList<RecentUserModel> users = new ArrayList<>();
    RecentUserModel userListModel = new RecentUserModel();

    public RecyclerAdapterRecentUserFragment(Context context, ArrayList<RecentUserModel> users) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public RecyclerAdapterRecentUserFragment.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =  LayoutInflater.from(context).inflate(R.layout.user_tab_layout,parent,false);
        return new RecyclerAdapterRecentUserFragment.viewHolder(view) ;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapterRecentUserFragment.viewHolder holder, int position) {
        // setting items on usertab layout
        userListModel = users.get(position);

        if(userListModel.getUserimage()==null&& userListModel.getUsername()!=null){
            holder.userimage.setImageResource(R.drawable.defaultserprofile);
            holder.username.setText(userListModel.getUsername());
        }else if( userListModel.getUsername()==null&& userListModel.getUserimage()!=null){
            Picasso.get().load(userListModel.getUserimage()).into(holder.userimage);
            holder.username.setText("Anonymous");
        }else{
            Picasso.get().load(userListModel.getUserimage()).into(holder.userimage);
            holder.username.setText(userListModel.getUsername());
        }
        holder.useridentity.setText(userListModel.getUseridentity());
        //ends
        if(users.get(position).getUserimage()!=null) {
            holder.userimage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Dialog dialog = new Dialog(context);
                    dialog.setContentView(R.layout.imageviewlayout);
                    ImageView imageView = dialog.findViewById(R.id.anyimage);

                    Picasso.get().load(users.get(position).getUserimage()).into(imageView);

                    dialog.show();

                }
            });
        }



        // on tab click listener
        holder.usertabFragmentlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(context, users.get(position).getUsername(), Toast.LENGTH_SHORT).show();

                Intent intent  = new Intent(context, ChatActivity.class);
                intent.putExtra("username",users.get(position).getUsername() );
                intent.putExtra("userimage", users.get(position).getUserimage());
                intent.putExtra("snapshot", users.get(position).getSnapshot().getKey());
                context.startActivity(intent);


            }
        });


    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        ImageView userimage;
        TextView useridentity;
        LinearLayout usertabFragmentlayout;
        TextView username;
        public viewHolder(@NonNull View itemView) {
            super(itemView);

            userimage =itemView.findViewById(R.id.userimage);
            username = itemView.findViewById(R.id.username);
            useridentity = itemView.findViewById(R.id.useridentity);
            usertabFragmentlayout = itemView.findViewById(R.id.usertab);

        }
    }
}
