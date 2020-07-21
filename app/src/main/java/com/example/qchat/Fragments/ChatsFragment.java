package com.example.qchat.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.qchat.Adapter.UserAdapter;
import com.example.qchat.Model.Chat;
import com.example.qchat.Model.Chatlist;
import com.example.qchat.Model.MUser;
import com.example.qchat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;


public class ChatsFragment extends Fragment
{

    RecyclerView recyclerView;
    private List<MUser> newUser;
    private List<Chatlist> userlist;
    FirebaseUser fuser;
    DatabaseReference reference;
    UserAdapter userAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_chats, container, false);
        recyclerView = view.findViewById(R.id.ChatsRvId);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        userlist = new ArrayList<>();
        reference=FirebaseDatabase.getInstance().getReference("Chatlist").child(fuser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userlist.clear();
                for(DataSnapshot snapshot1:snapshot.getChildren())
                {
                    Chatlist chatlist=snapshot1.getValue(Chatlist.class);
                    userlist.add(chatlist);
                }
                chatList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        return view;

    }
    private  void chatList()
    {
        newUser=new ArrayList<>();
        reference=FirebaseDatabase.getInstance().getReference("Profile");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                newUser.clear();
                for(DataSnapshot snapshot1:snapshot.getChildren())
                {
                    MUser user=snapshot1.getValue(MUser.class);
                    for(Chatlist chatlist:userlist)
                    {
                        if(user.getUserId().equals(chatlist.getId()))
                        {
                            newUser.add(user);
                        }
                    }
                }
                userAdapter =new UserAdapter(getContext(),newUser,true);
                recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}