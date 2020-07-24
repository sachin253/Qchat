package com.example.qchat.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.qchat.MessageActivity;
import com.example.qchat.Model.Chat;
import com.example.qchat.Model.MUser;
import com.example.qchat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder>
{
    private final Context context;
    private List<MUser> mUsers;
    private  boolean ischat;

    String theLastMessage;

    public UserAdapter(@NonNull Context context, @Nullable List<MUser> mUsers,boolean ischat)

    {

        this.context = context;
        this.mUsers = mUsers;
        this.ischat = ischat;


    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view= LayoutInflater.from(context).inflate(R.layout.rv_item,parent,false);
        return new UserAdapter.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {

        final MUser u=mUsers.get(position);
        holder.userName.setText(u.getUserName());
        if(u.getImageURL().equals("default"))
        {
            holder.imageURL.setImageResource(R.drawable.person2);
        }
        else
        {
            Glide.with(context).load(u.getImageURL()).into(holder.imageURL);
        }

        if(ischat)
        {
            lastMessage(u.getUserId(),holder.last_msg);
        }
        else
        {
            holder.last_msg.setVisibility(View.GONE);
        }

        if(ischat)
        {
            if (u.getStatus().equals("online"))
            {
                holder.img_on.setVisibility(View.VISIBLE);
                holder.img_off.setVisibility(View.GONE);
            } else
                {
                holder.img_on.setVisibility(View.GONE);
                holder.img_off.setVisibility(View.VISIBLE);
                }
        }
        else
        {
            holder.img_on.setVisibility(View.GONE);
            holder.img_off.setVisibility(View.VISIBLE);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent=new Intent(context, MessageActivity.class);
                intent.putExtra("userId",u.getUserId());
                intent.putExtra("userName",u.getUserName());
                intent.putExtra("imageURL",u.getImageURL());
                context.startActivity(intent);

            }
        });
    }



    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public  class  ViewHolder extends  RecyclerView.ViewHolder
    {
        public TextView userName;
        public ImageView imageURL;
        private ImageView img_on;
        private ImageView img_off;
        private  TextView last_msg;

        public ViewHolder(View itemView)
        {
            super(itemView);
            userName=itemView.findViewById(R.id.userNameIdTv);
            imageURL=itemView.findViewById(R.id.profile_image);
            img_on=itemView.findViewById(R.id.img_on);
            img_off=itemView.findViewById(R.id.img_off);
            last_msg=itemView.findViewById(R.id.last_msg);
        }
    }

    private void lastMessage(final String userid, final TextView last_msg)
    {
        theLastMessage="default";
        final FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                try
                {
                    for(DataSnapshot snapshot1:snapshot.getChildren())
                    {
                        Chat chat=snapshot1.getValue(Chat.class);
                        if(chat.getReceiver().equals(userid) && chat.getSender().equals(firebaseUser.getUid() )|| chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userid))
                        {
                            theLastMessage=chat.getMessage();
                        }
                    }

                    switch (theLastMessage)
                    {
                        case  "default":
                            last_msg.setText("No Message");
                            break;
                        default:
                            last_msg.setText(theLastMessage);
                            break;
                    }
                    theLastMessage="default";

                } catch (Exception e)
                {

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}