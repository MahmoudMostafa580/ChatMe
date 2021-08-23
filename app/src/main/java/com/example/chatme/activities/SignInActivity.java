package com.example.chatme.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.example.chatme.databinding.ActivitySignInBinding;
import com.example.chatme.utilities.Constants;
import com.example.chatme.utilities.Preferences;
import com.google.firebase.auth.FirebaseAuth;

public class SignInActivity extends AppCompatActivity {
    private ActivitySignInBinding signInBinding;
    FirebaseAuth firebaseAuth;
    Preferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        signInBinding=ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(signInBinding.getRoot());

        firebaseAuth=FirebaseAuth.getInstance();
        preferences=new Preferences(getApplicationContext());

        signInBinding.createNewAccountTxt.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(),SignUpActivity.class)));
        signInBinding.signInBtn.setOnClickListener(v -> {
            if (isValidData()){
                signIn();
            }
        });
    }

    private void signIn() {
        loading(true);
        String email=signInBinding.emailLayout.getEditText().getText().toString();
        String pasword= signInBinding.passLayout.getEditText().getText().toString();
        firebaseAuth.signInWithEmailAndPassword(email,pasword)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        loading(false);
                        showToast("Login successfully");
                        startActivity(new Intent(getApplicationContext(),DashboardActivity.class));
                        finish();
                        preferences.putBoolean(Constants.KEY_IS_SIGNED_IN,true);
                        preferences.putString(Constants.KEY_USER_ID,task.getResult().getUser().getUid());
                    }
                })
                .addOnFailureListener(e -> {
                    loading(false);
                   showToast(e.getMessage());
                });
    }

    private boolean isValidData(){
        if (signInBinding.emailLayout.getEditText().getText().toString().trim().isEmpty()) {
            signInBinding.emailLayout.setError("Please enter your mail!");
            return false;
        }else if (!Patterns.EMAIL_ADDRESS.matcher(signInBinding.emailLayout.getEditText().getText().toString()).matches()){
            signInBinding.emailLayout.setError("Enter valid mail!");
            return false;
        }else if (signInBinding.passLayout.getEditText().getText().toString().trim().isEmpty()){
            signInBinding.passLayout.setError("Please enter password!");
            return false;
        }else {
            return true;
        }
    }

    private void showToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void loading(boolean isLoading){
        if (isLoading){
            signInBinding.signInProgressBar.setVisibility(View.VISIBLE);
            signInBinding.signInBtn.setVisibility(View.INVISIBLE);
        }else{
            signInBinding.signInProgressBar.setVisibility(View.INVISIBLE);
            signInBinding.signInBtn.setVisibility(View.VISIBLE);
        }
    }
}