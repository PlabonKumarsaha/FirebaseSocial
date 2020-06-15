package com.example.firebasesocial.adapters;

import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.icu.util.ULocale;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firebasesocial.ChatActivity;
import com.example.firebasesocial.R;
import com.example.firebasesocial.models.ModelChat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AdapterChat extends RecyclerView.Adapter<AdapterChat.MyHolder>{

    private static final int MSG_TYPE_LEFT = 0;
    private static final int MSG_TYPE_RIGHT = 1;
    Context context;
    List<ModelChat> chatList;
    String imageUrl;
    FirebaseUser fUser;

    public AdapterChat(Context context, List<ModelChat> chatList, String imageUrl) {
        this.context = context;
        this.chatList = chatList;
        this.imageUrl = imageUrl;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if(viewType ==MSG_TYPE_RIGHT){
            View view = LayoutInflater.from(context).inflate(R.layout.row_chat_right,parent,false);
            return  new MyHolder(view);

        } else{
            View view = LayoutInflater.from(context).inflate(R.layout.row_chat_left,parent,false);
            return  new MyHolder(view);
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {

        //get the data
        String message = chatList.get(position).getMessage();
        String timeStamp = chatList.get(position).getTimestrap();
        //convert the time
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
       // calendar.setTimeInMillis(Long.parseLong(timeStamp));
        //Date date = new Date(ULocale.getTime());
       // String dateTime = DateFormat.getDateInstance().format(date).toString();
        String dateTime = java.text.DateFormat.getDateTimeInstance().format(new Date());

        holder.messageTv.setText(message);
       holder.timeTV.setText(dateTime);
        try{
            Picasso.get().load(imageUrl).into(holder.profileImage);
        } catch (Exception e){


           // SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd hh:mm:ss 'GMT'Z yyyy");
           // System.out.println(dateFormat.format(cal.getTime()));
        }

        //set sent or delivered sattus
        if(position == chatList.size()-1){
            if(chatList.get(position).isSeen()){
                holder.issenTV.setText("seen");
            } else{
                holder.issenTV.setText("delivered");
            }
        } else{

            holder.issenTV.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    @Override
    public int getItemViewType(int position) {
        //get currently signed in user
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        if(chatList.get(position).getSender().equals(fUser.getUid())){

            return MSG_TYPE_RIGHT;
        }
        else {
            return MSG_TYPE_LEFT;
        }

    }

    class MyHolder extends RecyclerView.ViewHolder{

    //views
    ImageView profileImage;
    TextView messageTv,timeTV, issenTV;

    public MyHolder(@NonNull View itemView) {
        super(itemView);

        profileImage = itemView.findViewById(R.id.profileIvv);
        messageTv = itemView.findViewById(R.id.messageTvv);
        timeTV = itemView.findViewById(R.id.timeTv);
        issenTV = itemView.findViewById(R.id.isSeenTv);

    }
}

}

