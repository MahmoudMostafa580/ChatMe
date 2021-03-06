package com.example.chatme.activities;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;

import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.example.chatme.databinding.ActivitySignUpBinding;
import com.example.chatme.models.User;
import com.example.chatme.utilities.Constants;
import com.example.chatme.utilities.Preferences;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private ActivitySignUpBinding signUpBinding;
    private Uri profileImage;
    private Preferences preferences;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser mFirebaseUser;
    private StorageReference mStorageReference;
    private FirebaseFirestore mFirestore;
    private StorageTask mUploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        signUpBinding=ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(signUpBinding.getRoot());

        preferences=new Preferences(getApplicationContext());
        firebaseAuth=FirebaseAuth.getInstance();
        mStorageReference= FirebaseStorage.getInstance().getReference();
        mFirestore=FirebaseFirestore.getInstance();

        signUpBinding.signInTxt.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(),SignInActivity.class)));

        signUpBinding.imageFrameLayout.setOnClickListener(v -> {
            openFileChooser();
        });

        signUpBinding.signUpBtn.setOnClickListener(v -> {
            if (isValidData()){
                signUp();
            }
        });
    }

    private void signUp() {
        loading(true);
        String name=signUpBinding.nameLayout.getEditText().getText().toString();
        String email=signUpBinding.emailLayout.getEditText().getText().toString();
        String password=signUpBinding.passLayout.getEditText().getText().toString();
        firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        StorageReference imageReference=mStorageReference.child("usersPictures/"+firebaseAuth.getCurrentUser().getEmail());
                        mUploadTask=imageReference.putFile(profileImage)
                                .addOnSuccessListener(taskSnapshot -> {
                                    imageReference.getDownloadUrl()
                                            .addOnSuccessListener(uri -> {
                                                Map<String,Object> user =new HashMap<>();
                                                user.put(Constants.KEY_NAME,name);
                                                user.put(Constants.KEY_EMAIL,email);
                                                user.put(Constants.KEY_IMAGE,uri.toString());
                                                user.put(Constants.KEY_PASSWORD,password);
                                                user.put(Constants.KEY_VISIBILITY_STATUS,true);
                                                DocumentReference documentReference=mFirestore.collection(Constants.KEY_COLLECTION_USERS).document(firebaseAuth.getCurrentUser().getUid());
                                                documentReference.set(user)
                                                        .addOnSuccessListener(unused -> {
                                                            mFirebaseUser=FirebaseAuth.getInstance().getCurrentUser();
                                                            UserProfileChangeRequest profileChangeRequest=new UserProfileChangeRequest.Builder()
                                                                    .setDisplayName(name)
                                                                    .setPhotoUri(uri)
                                                                    .build();
                                                            mFirebaseUser.updateProfile(profileChangeRequest);
                                                            showToast("Account created successfully");
                                                            preferences.putBoolean(Constants.KEY_IS_SIGNED_IN,true);
                                                            startActivity(new Intent(getApplicationContext(),DashboardActivity.class));
                                                            finish();
                                                        })
                                                        .addOnFailureListener(e ->
                                                                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show());

                                            })
                                            .addOnFailureListener(e ->
                                                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show());

                                })
                                .addOnFailureListener(e ->
                                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show());
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void openFileChooser() {
        Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            profileImage=data.getData();
            signUpBinding.profileImage.setImageURI(profileImage);
            Picasso.with(this).load(profileImage).fit().centerCrop().into(signUpBinding.profileImage);
            signUpBinding.addImageTxt.setVisibility(View.GONE);
        }
    }

    private void showToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    private boolean isValidData(){
        if (profileImage==null){
            showToast("Select profile image!");
            return false;
        }else if (signUpBinding.nameLayout.getEditText().getText().toString().trim().isEmpty()){
            signUpBinding.nameLayout.setError("Please enter your name!");
            return false;
        }else if (signUpBinding.emailLayout.getEditText().getText().toString().trim().isEmpty()){
            signUpBinding.emailLayout.setError("Please enter your mail!");
            return false;
        }else if (!Patterns.EMAIL_ADDRESS.matcher(signUpBinding.emailLayout.getEditText().getText().toString()).matches()){
            signUpBinding.emailLayout.setError("Enter valid mail!");
            return false;
        }else if (signUpBinding.passLayout.getEditText().getText().toString().trim().isEmpty()){
            signUpBinding.passLayout.setError("Please enter password!");
            return false;
        }else if (signUpBinding.confirmPassLayout.getEditText().getText().toString().trim().isEmpty()){
            signUpBinding.confirmPassLayout.setError("Please confirm password!");
            return false;
        }else if (!signUpBinding.passLayout.getEditText().getText().toString()
                .equals(signUpBinding.confirmPassLayout.getEditText().getText().toString())){
            showToast("Password and confirm password must be the same");
            return false;
        }else{
            return true;
        }
    }

    private void loading(boolean isLoading){
        if (isLoading){
            signUpBinding.signUpProgressBar.setVisibility(View.VISIBLE);
            signUpBinding.signUpBtn.setVisibility(View.INVISIBLE);
        }else{
            signUpBinding.signUpProgressBar.setVisibility(View.INVISIBLE);
            signUpBinding.signUpBtn.setVisibility(View.VISIBLE);
        }
    }
}