package com.example.chatme.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.chatme.R;
import com.example.chatme.databinding.ActivityDashboardBinding;
import com.example.chatme.utilities.Constants;
import com.example.chatme.utilities.Preferences;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class DashboardActivity extends AppCompatActivity {
    private ActivityDashboardBinding dashboardBinding;
    private Preferences preferences;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser mFirebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dashboardBinding=ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(dashboardBinding.getRoot());
        preferences=new Preferences(getApplicationContext());
        firebaseAuth=FirebaseAuth.getInstance();
        mFirebaseUser=FirebaseAuth.getInstance().getCurrentUser();

        loadUserDetails();

        dashboardBinding.logOutImageView.setOnClickListener(v -> {
            firebaseAuth.signOut();
            preferences.putBoolean(Constants.KEY_IS_SIGNED_IN,false);
            startActivity(new Intent(getApplicationContext(),SignInActivity.class));
            finish();
        });

        dashboardBinding.addUserBtn.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(),UsersActivity.class));
        });
    }

    private void loadUserDetails() {
        StorageReference mStorageReference = FirebaseStorage.getInstance().getReference();
        StorageReference img= mStorageReference.child("usersPictures/"+firebaseAuth.getCurrentUser().getEmail());
        img.getDownloadUrl()
                .addOnSuccessListener(uri ->{
                    Glide.with(DashboardActivity.this)
                            .load(uri)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .centerCrop()
                            .placeholder(R.drawable.ic_person)
                            .into(dashboardBinding.dashProfileImage);
                    dashboardBinding.nameTxt.setText(mFirebaseUser.getDisplayName());
                }).addOnFailureListener(e ->
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show());

    }
}