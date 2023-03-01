package com.example.testapp1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import com.example.testapp1.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    ActivityLoginBinding binding;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        firebaseAuth =  FirebaseAuth.getInstance();


        binding.textviewregistor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),RegistorActivity.class);
                startActivity(intent);
              
            }
        });

        if(firebaseAuth.getCurrentUser()!=null) {
            // shared prefrences
            SharedPreferences sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);

            Intent intent2;
            boolean check = sharedPreferences.getBoolean("flag", false);
            if (check) {
                intent2 = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent2);
                finish();
            } else {
                intent2 = new Intent(LoginActivity.this, UserWelcomeActivity.class);
                startActivity(intent2);
                finish();

            }
        }  // shared prefrences




        binding.login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String uemail = binding.email.getText().toString();
                    String upassword = binding.password.getText().toString();

                  if(!uemail.isEmpty() && !upassword.isEmpty()){
                    ProgressDialog pd = new ProgressDialog(LoginActivity.this);
                    pd.setMessage("Logging");
                    pd.setCanceledOnTouchOutside(false);
                    pd.show();

                    firebaseAuth.signInWithEmailAndPassword(uemail, upassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                pd.dismiss();
                                Intent intent = new Intent(getApplicationContext(), UserWelcomeActivity.class);
                                startActivity(intent);
                                finish();

                            } else {
                                pd.dismiss();
                                Toast.makeText(LoginActivity.this, "Incorrect entries", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });}
               else{
                        Toast.makeText(LoginActivity.this, "Fill the entries", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
}
