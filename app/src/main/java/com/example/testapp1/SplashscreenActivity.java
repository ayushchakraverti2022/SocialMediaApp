package com.example.testapp1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;


public class SplashscreenActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen);



        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent;

                  intent =  new Intent(getApplicationContext(),LoginActivity.class);

              startActivity(intent);
                finish();
            }
        }, 4000);




    }
}
