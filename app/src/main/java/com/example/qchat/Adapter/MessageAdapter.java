package com.example.qchat.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.qchat.Model.Chat;

import com.example.qchat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder>
{
    public  static final int MSG_TYPE_LEFT=0;
    public  static final int MSG_TYPE_RIGHT=1;
    private final Context context;
    private List<Chat> mChat;
    private String imgurl;
    FirebaseUser fuser;

    public MessageAdapter(@NonNull Context context, @Nullable List<Chat> mChat,String imgurl)
    {

        this.context = context;
        this.mChat = mChat;
        this.imgurl = imgurl;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        if(viewType==MSG_TYPE_RIGHT)
        {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_right, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }
        else
            {
                View view = LayoutInflater.from(context).inflate(R.layout.chat_item_left, parent, false);
                return new MessageAdapter.ViewHolder(view);
            }

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        final Chat chat=mChat.get(position);
        holder.show_message.setText(chat.getMessage());
        if(imgurl.equals("default"))
        {
            holder.imageURL.setImageResource(R.drawable.black_user);

        }
        else
        {
            Glide.with(context).load(imgurl).into(holder.imageURL);
        }

        if(position == mChat.size()-1)
        {
            if(chat.isIsseen())
            {
                holder.text_seen.setText("Seen");
            }
            else
            {
                holder.text_seen.setText("Delivered");
            }
        }
        else
        {
            holder.text_seen.setVisibility(View.GONE);
        }

    }


    @Override
    public int getItemCount() {
        return mChat.size();
    }

    public  class  ViewHolder extends  RecyclerView.ViewHolder
    {
        public TextView show_message;
        public ImageView imageURL;

        public TextView text_seen;
        public ViewHolder(View itemView)
        {
            super(itemView);
            show_message=itemView.findViewById(R.id.show_message);
            imageURL=itemView.findViewById(R.id.profile_image);
            text_seen=itemView.findViewById(R.id.txt_seen);

        }
    }

    @Override
    public int getItemViewType(int position)
    {
      fuser= FirebaseAuth.getInstance().getCurrentUser();
      if(mChat.get(position).getSender().equals(fuser.getUid()))
      {
          return  MSG_TYPE_RIGHT;

      }
      else
      {
          return MSG_TYPE_LEFT;
      }
    }
}
