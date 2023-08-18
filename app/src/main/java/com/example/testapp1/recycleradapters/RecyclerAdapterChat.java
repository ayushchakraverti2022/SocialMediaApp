package com.example.testapp1.recycleradapters;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testapp1.ChatActivity;
import com.example.testapp1.R;
import com.example.testapp1.modelFolder.ChatModel;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RecyclerAdapterChat extends  RecyclerView.Adapter<RecyclerAdapterChat.viewHolder> {

    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    ArrayList<ChatModel> chatModel =  new ArrayList<ChatModel>();
    Context context;
    ChatModel model = new ChatModel();
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    String userId = firebaseAuth.getUid();               // userID in string

    public RecyclerAdapterChat(ArrayList<ChatModel> chatM, Context context) {
        this.chatModel = chatM;
        this.context = context;
    }


    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.userchat_tab_layout,parent,false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
         model = chatModel.get(position);

         Pattern pattern = Pattern.compile("http");

         Matcher match = pattern.matcher(chatModel.get(position).getMessage());
         if(match.find()){
             holder.textView.setVisibility(View.GONE);
             holder.imageView.setVisibility(View.VISIBLE);
             Log.d("matches", String.valueOf(match.matches()));
             Picasso.get().load(chatModel.get(position).getMessage()).into(holder.imageView);
         }else {
             holder.textView.setText(model.getMessage());
         }

         if(!Pattern.matches(userId,model.getFriendid())){
             holder.linearLayout.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
         }else{
             holder.linearLayout.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
         }
        // message deletion
         holder.linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
             @Override
             public boolean onLongClick(View view) {
                 AlertDialog.Builder alerdialog = new AlertDialog.Builder(context);
                 alerdialog.setIcon(R.drawable.deletechat);
                 alerdialog.setTitle("clearchat ?");
                 alerdialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                     @Override
                     public void onClick(DialogInterface dialogInterface, int i) {

                         firebaseDatabase.getReference().child("users").child(userId).child("recentusers").child(chatModel.get(position).getmessengerid()).child(chatModel.get(position).getMessagetimenode()).removeValue();
                         firebaseDatabase.getReference().child("users").child(chatModel.get(position).getmessengerid()).child("recentusers").child(userId).child(chatModel.get(position).getMessagetimenode()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                             @Override
                             public void onSuccess(Void unused) {
                                 try {
                                     firebaseStorage.getReference().child("chatimage").child(userId).child(chatModel.get(position).getMessagetimenode()).delete();
                                     }catch (Exception e){

                                 }
                                 try {
                                     firebaseStorage.getReference().child("chatimage").child(chatModel.get(position).getmessengerid()).child(chatModel.get(position).getMessagetimenode()).delete();
                                 }catch (Exception e){

                                 }
                                 Toast.makeText(context, "deleted", Toast.LENGTH_SHORT).show();
                             }
                         });

                     }
                 }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                     @Override
                     public void onClick(DialogInterface dialogInterface, int i) {

                     }
                 });
                 alerdialog.show();


                 return false;
             }
         });

         holder.imageView.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 Dialog dialog = new Dialog(context);
                 dialog.setContentView(R.layout.imageviewlayout);
                 ImageView imageView2 = dialog.findViewById(R.id.anyimage);
                 Picasso.get().load(chatModel.get(position).getMessage()).into(imageView2);

                 dialog.show();
             }

         });




    }

    @Override
    public int getItemCount() {
        return chatModel.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        LinearLayout linearLayout;
        TextView textView;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            textView= itemView.findViewById(R.id.textviewchat);
            imageView  = itemView.findViewById(R.id.imageviewchat);
            linearLayout = itemView.findViewById(R.id.linearlayoutuserchat);
        }
    }
}
