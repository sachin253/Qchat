package com.example.qchat.Fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.qchat.Adapter.UserAdapter;
import com.example.qchat.Model.MUser;
import com.example.qchat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;


public class UsersFragment extends Fragment
{
    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    public List<MUser> mUser;
    EditText search_users;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
       View view=inflater.inflate(R.layout.fragment_users,container,false);
       recyclerView=view.findViewById(R.id.UsersRvId);
       recyclerView.setHasFixedSize(true);
       recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        search_users=view.findViewById(R.id.search_users);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));

        search_users.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
                searchUsers(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable)
            {

            }
        });
        mUser=new ArrayList<>();

       readUsers();


       return  view;
    }
    private void searchUsers(String s)
    {
        final FirebaseUser fuser=FirebaseAuth.getInstance().getCurrentUser();
        Query query=FirebaseDatabase.getInstance().getReference("Profile").orderByChild("userName").startAt(s).endAt(s+"\uf0ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUser.clear();
                for(DataSnapshot snapshot1:snapshot.getChildren())
                {
                    MUser u=snapshot1.getValue(MUser.class);
                    assert u !=null;
                    assert  fuser !=null;
                    if(!u.getUserId().equals(fuser.getUid()))
                    {
                        mUser.add(u);
                    }
                }
                userAdapter=new UserAdapter(getContext(),mUser,true);
                recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

     private  void readUsers()
    {
        final FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Profile");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if(search_users.getText().toString().equals(""))
                {
                    mUser.clear();
                    for (DataSnapshot npsnapshot : snapshot.getChildren()) {
                        MUser u = npsnapshot.getValue(MUser.class);
                        mUser.add(u);
                    }

                    userAdapter = new UserAdapter(getContext(), mUser, true);

                    recyclerView.setAdapter(userAdapter);
                    userAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });
    }
}