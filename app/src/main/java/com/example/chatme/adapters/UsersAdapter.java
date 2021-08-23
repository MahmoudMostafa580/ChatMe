package com.example.chatme.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.chatme.R;
import com.example.chatme.models.User;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UsersViewHolder> {
    private ArrayList<User> usersList = new ArrayList<>();
    Context mContext;

    public UsersAdapter(Context mContext,ArrayList<User> usersList){
        this.mContext=mContext;
        this.usersList=usersList;
    }

    @NonNull
    @Override
    public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new UsersViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.users_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull UsersViewHolder holder, int position) {
        Glide.with(mContext)
                .load(Uri.parse(usersList.get(position).getImage()))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.ic_person)
                .into(holder.imageProfile);
       // holder.imageProfile.setImageURI(Uri.parse(usersList.get(position).getImage()));
        holder.nameTxt.setText(usersList.get(position).getName());
        holder.emailTxt.setText(usersList.get(position).getEmail());
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    public void setList(ArrayList<User> usersList) {
        this.usersList = usersList;
        notifyDataSetChanged();
    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder {
        RoundedImageView imageProfile;
        TextView nameTxt,emailTxt;

        public UsersViewHolder(@NonNull View itemView) {
            super(itemView);
            imageProfile=itemView.findViewById(R.id.imageProfile);
            nameTxt=itemView.findViewById(R.id.name_txt);
            emailTxt=itemView.findViewById(R.id.email_txt);
        }
    }
}
