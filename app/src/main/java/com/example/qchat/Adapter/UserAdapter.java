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
import com.example.qchat.MessageActivity;
import com.example.qchat.Model.MUser;
import com.example.qchat.R;



import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder>
{
    private final Context context;
    private List<MUser> mUsers;
    private  boolean ischat;

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

        public ViewHolder(View itemView)
        {
            super(itemView);
            userName=itemView.findViewById(R.id.userNameIdTv);
            imageURL=itemView.findViewById(R.id.profile_image);
            img_on=itemView.findViewById(R.id.img_on);
            img_off=itemView.findViewById(R.id.img_off);
        }
    }
}