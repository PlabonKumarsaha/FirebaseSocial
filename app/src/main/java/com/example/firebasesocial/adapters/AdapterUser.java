package com.example.firebasesocial.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firebasesocial.ChatActivity;
import com.example.firebasesocial.R;
import com.example.firebasesocial.models.ModelUser;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterUser extends RecyclerView.Adapter<AdapterUser.MyHolder>{

    Context context;
    List<ModelUser>userList;


    public AdapterUser(Context context, List<ModelUser> userList) {
        this.context = context;
        this.userList = userList;
    }


    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //infalte layout
        View view = LayoutInflater.from(context).inflate(R.layout.row_users,parent,false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int i) {

        //get the datas
        final String hisUID =userList.get(i).getUid();
        String userImage = userList.get(i).getImage();
        String userName = userList.get(i).getName();
        final String userEmail = userList.get(i).getEmail();

        //set the datas

        holder.mNameTV.setText(userName);
        holder.mEmailTV.setText(userEmail);
        try {

//                  Picasso.get().load(userImage)
                       //   .placeholder(R.drawable.ic_face_default2).into(holder.mAvatarIV);

        } catch (Exception e){

            Toast.makeText(context,""+e.getMessage(),Toast.LENGTH_SHORT).show();

        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // Toast.makeText(context,""+userEmail,Toast.LENGTH_SHORT).show();

                //send this when a user wants to send message to a particular user
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("hisUID",hisUID);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    //extend the view holder

   public class MyHolder extends RecyclerView.ViewHolder{

        ImageView mAvatarIV;
        TextView mNameTV,mEmailTV;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            mAvatarIV = itemView.findViewById(R.id.avatarIV);
            mNameTV = itemView.findViewById(R.id.nameTVU);
            mEmailTV = itemView.findViewById(R.id.emailTVU);

        }
    }
}
