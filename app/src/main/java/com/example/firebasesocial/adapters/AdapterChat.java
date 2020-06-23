package com.example.firebasesocial.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.icu.text.SimpleDateFormat;
import android.icu.util.ULocale;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firebasesocial.ChatActivity;
import com.example.firebasesocial.R;
import com.example.firebasesocial.models.ModelChat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
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
    public void onBindViewHolder(@NonNull MyHolder holder, final int position) {

        //get the data
        String message = chatList.get(position).getMessage();
        String timeStamp = chatList.get(position).getTimestrap();
        //convert the time
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
       // calendar.setTimeInMillis(Long.parseLong(timeStamp));
        //Date date = new Date(ULocale.getTime());
       // String dateTime = DateFormat.getDateInstance().format(date).toString();
       // String dateTime = java.text.DateFormat.getDateTimeInstance().format(new Date());

        holder.messageTv.setText(message);
       holder.timeTV.setText(timeStamp);
        try{
            Picasso.get().load(imageUrl).into(holder.profileImage);
        } catch (Exception e){


           // SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd hh:mm:ss 'GMT'Z yyyy");
           // System.out.println(dateFormat.format(cal.getTime()));
        }

        //click to show dialog box
        holder.messageLyout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Delete");
                builder.setMessage("Are you sure you want to delete?");

                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        deleteMessage(position);
                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        dialogInterface.dismiss();
                    }
                });

                builder.create().show();
            }
        });

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

    private void deleteMessage(int position) {
    final String myUid =FirebaseAuth.getInstance().getCurrentUser().getUid();

        String msgtimeStamp = chatList.get(position).getTimestrap();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Chats");
        Query query = databaseReference.orderByChild("timestamp").equalTo(msgtimeStamp);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot ds : dataSnapshot.getChildren()){

                    if(ds.child("sender").getValue().equals(myUid)){

                        //remove the value from the chat..deletes the message totally with no alter message
                        //ds.getRef().removeValue();

                        //set the value to the chat was deleted message..doesnt delete the msg totally isnated keeps an alter message
                        HashMap<String,Object>hashMap = new HashMap<>();
                        hashMap.put("message","This message was deleted!");
                        ds.getRef().updateChildren(hashMap);

                        Toast.makeText(context,"Message was deleted!",Toast.LENGTH_SHORT).show();
                    } else{

                        Toast.makeText(context,"You can only delete your own messages",Toast.LENGTH_SHORT).show();

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
    LinearLayout messageLyout; //for any click in the layout

    public MyHolder(@NonNull View itemView) {
        super(itemView);

        profileImage = itemView.findViewById(R.id.profileIvv);
        messageTv = itemView.findViewById(R.id.messageTvv);
        timeTV = itemView.findViewById(R.id.timeTv);
        issenTV = itemView.findViewById(R.id.isSeenTv);
        messageLyout = itemView.findViewById(R.id.messageLyout);

    }
}

}

