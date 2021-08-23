package com.example.chatme.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.example.chatme.R;
import com.example.chatme.activities.SignInActivity;
import com.example.chatme.utilities.Constants;
import com.example.chatme.utilities.Preferences;
import com.google.firebase.auth.FirebaseAuth;

public class LauncherActivity extends AppCompatActivity {
    Preferences preferences;
    FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        preferences=new Preferences(getApplicationContext());
        mFirebaseAuth=FirebaseAuth.getInstance();

        if (!preferences.getBoolean("hasRunBefore")){
            mFirebaseAuth.signOut();
            preferences.putBoolean(Constants.KEY_IS_SIGNED_IN,false);
            preferences.putBoolean("hasRunBefore",true);
            new Handler().postDelayed(() -> {
                startActivity(new Intent(getApplicationContext(),SignInActivity.class));
                finish();
            },2000);
        }else{
            if (preferences.getBoolean(Constants.KEY_IS_SIGNED_IN)){
                new Handler().postDelayed(() -> {
                    startActivity(new Intent(getApplicationContext(),DashboardActivity.class));
                    finish();
                },2000);
            }else{
                new Handler().postDelayed(() -> {
                    startActivity(new Intent(getApplicationContext(),SignInActivity.class));
                    finish();
                },2000);
            }
        }


    }
}