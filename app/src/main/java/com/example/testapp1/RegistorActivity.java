package com.example.testapp1;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Transaction;

public class RegistorActivity extends AppCompatActivity {
    public TextInputEditText uemail;
    ConstraintLayout constraintLayout;
    public TextInputEditText upassword;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Button button = findViewById(R.id.login);
        uemail  = findViewById(R.id.email);
        constraintLayout = findViewById(R.id.constraintregistor);
        upassword  = findViewById(R.id.password);
        constraintLayout.setVisibility(View.GONE);
        button.setText("Registor");

       button.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               String email = uemail.getText().toString().trim();
               String password = upassword.getText().toString().trim();
               if(!email.isEmpty() && !password.isEmpty())
               {
                   ProgressDialog pd = new ProgressDialog(RegistorActivity.this);
                   pd.setMessage("Creating User");
                   pd.show();
                   
                   FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                   firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                       @Override
                       public void onComplete(@NonNull Task<AuthResult> task) {
                           if (task.isSuccessful()) {

                                       Toast.makeText(RegistorActivity.this, "User Created\nNow Login", Toast.LENGTH_SHORT).show();

                                 pd.dismiss();
                               new Handler().postDelayed(new Runnable() {
                                   @Override
                                   public void run() {
                                       onBackPressed();
                                   }
                               }, 1000);

                           } else {
                               pd.dismiss();
                               Toast.makeText(RegistorActivity.this, "Retry again", Toast.LENGTH_SHORT).show();
                           }
                       }
                   });
               }
               else{
                   Toast.makeText(RegistorActivity.this, "Fill the entries", Toast.LENGTH_SHORT).show();
               }
           }
       });





    }
}
