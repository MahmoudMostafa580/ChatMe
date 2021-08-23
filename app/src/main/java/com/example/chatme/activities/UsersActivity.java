package com.example.chatme.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.chatme.R;
import com.example.chatme.adapters.UsersAdapter;
import com.example.chatme.databinding.ActivityUsersBinding;
import com.example.chatme.models.User;
import com.example.chatme.utilities.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class UsersActivity extends AppCompatActivity {
    ActivityUsersBinding usersBinding;
    private FirebaseFirestore mFirestore;
    private FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        usersBinding=ActivityUsersBinding.inflate(getLayoutInflater());
        setContentView(usersBinding.getRoot());

        mFirestore=FirebaseFirestore.getInstance();
        mFirebaseAuth=FirebaseAuth.getInstance();

        usersBinding.imageBack.setOnClickListener(v -> onBackPressed());

        getUsers();
    }

    private void getUsers(){
        loading(true);
        mFirestore.collection(Constants.KEY_COLLECTION_USERS)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()  && task.getResult()!= null){
                            loading(false);
                            ArrayList<User> users =new ArrayList<>();
                            for (QueryDocumentSnapshot queryDocumentSnapshot: task.getResult()){
                                if (mFirebaseAuth.getCurrentUser().getUid().equals(queryDocumentSnapshot.getId())){
                                    continue;
                                }
                                User user =new User();
                                user.setName(queryDocumentSnapshot.getString(Constants.KEY_NAME));
                                user.setEmail(queryDocumentSnapshot.getString(Constants.KEY_EMAIL));
                                user.setImage(queryDocumentSnapshot.getString(Constants.KEY_IMAGE));
                                users.add(user);
                            }
                            if (users.size()>0){
                                UsersAdapter usersAdapter=new UsersAdapter(UsersActivity.this,users);
                                //usersAdapter.setList(users);
                                usersBinding.usersRecyclerView.setAdapter(usersAdapter);
                                usersBinding.usersRecyclerView.setVisibility(View.VISIBLE);
                            }else
                                showErrorMessage();
                        }else
                            showErrorMessage();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show());

    }

    private void loading(boolean isLoading){
        if (isLoading){
            usersBinding.progressBar.setVisibility(View.VISIBLE);
        }else{
            usersBinding.progressBar.setVisibility(View.INVISIBLE);
        }
    }

    private void showErrorMessage(){
        usersBinding.errorTxt.setText(String.format("%s","No users available"));
        usersBinding.errorTxt.setVisibility(View.VISIBLE);
    }
}