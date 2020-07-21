package com.example.qchat;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewStructure;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.qchat.Adapter.MessageAdapter;
import com.example.qchat.Model.Chat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity {
CircleImageView imageURL;
TextView UserName;
FirebaseUser fuser;
DatabaseReference reference;
Intent intent;
ImageButton btn_send;
EditText text_send;
MessageAdapter messageAdapter;
List<Chat> mChat;
RecyclerView recyclerView;
ValueEventListener seenListener;



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
       Toolbar toolbar=findViewById(R.id.Mtoolbar);

        recyclerView=findViewById(R.id.MrecyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
       setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                finish();
                //startActivity(new Intent(MessageActivity.this,MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });
        imageURL=findViewById(R.id.Mprofile_image);
        UserName=findViewById(R.id.Musername);
        btn_send=findViewById(R.id.btn_send);
        text_send=findViewById(R.id.text_send);

        intent=getIntent();


       final String userId=intent.getStringExtra("userId");
        final String userName=intent.getStringExtra("userName");
        final String url=intent.getStringExtra("imageURL");
       btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg=text_send.getText().toString();
                if(!msg.equals(""))
                {
                    sendMessage(fuser.getUid(),userId,msg);
                }
                else
                {
                    Toast.makeText(MessageActivity.this,"You can't send empty message",Toast.LENGTH_LONG).show();
                }
                text_send.setText("");
            }
        });
        fuser= FirebaseAuth.getInstance().getCurrentUser();
        reference= FirebaseDatabase.getInstance().getReference("Profile").child(userId);
        reference.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {

               UserName.setText(userName);
               if(url.equals("default"))
                    imageURL.setImageResource(R.drawable.person2);
                else
                    Glide.with(getApplicationContext()).load(url).into(imageURL);

                readMessage(fuser.getUid(),userId,url);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });
            seenMessage(userId);
    }
    private void  seenMessage(final String userid)
    {
        reference=FirebaseDatabase.getInstance().getReference("Chats");
       seenListener= reference.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
              for(DataSnapshot snapshot1:snapshot.getChildren())
               {
                   Chat chat=snapshot1.getValue(Chat.class);
                   if(chat.getReceiver().equals(fuser.getUid()) && chat.getSender().equals(userid))
                   {
                       HashMap<String ,Object> hashMap=new HashMap<>();
                       hashMap.put("isseen",true);
                       snapshot1.getRef().updateChildren(hashMap);
                   }
               }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });
    }
   private void sendMessage(String sender,String receiver,String message)
    {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference();
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("sender",sender);
        hashMap.put("receiver",receiver);
        hashMap.put("message",message);
        hashMap.put("isseen",false);

        reference.child("Chats").push().setValue(hashMap);
        intent=getIntent();
        final String userId=intent.getStringExtra("userId");
        final DatabaseReference chatRef= FirebaseDatabase.getInstance().getReference("Chatlist").child(fuser.getUid()).child(userId);
        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                chatRef.child("id").setValue(userId);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readMessage(final String myid, final String userid, final String imgurl)
    {
        mChat=new ArrayList<>();
        reference=FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mChat.clear();
                for(DataSnapshot snapshot1 : snapshot.getChildren())
                {

                        Chat chat = snapshot1.getValue(Chat.class);
                    if(chat.getReceiver().equals(myid)&& chat.getSender().equals(userid)||chat.getReceiver().equals(userid)&&chat.getSender().equals(myid))
                    {

                        mChat.add(chat);
                    }
                        messageAdapter = new MessageAdapter(MessageActivity.this, mChat, imgurl);
                        recyclerView.setAdapter(messageAdapter);
                    }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });
    }
    private void status(String status)
    {
        reference= FirebaseDatabase.getInstance().getReference("Profile").child(fuser.getUid());
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("status",status);
        reference.updateChildren(hashMap);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        status("online");

    }
    @Override
    protected void  onPause()
    {
        super.onPause();
        reference.removeEventListener(seenListener);
        status("offline");
    }
}