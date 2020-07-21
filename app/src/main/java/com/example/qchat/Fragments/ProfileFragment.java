package com.example.qchat.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.qchat.MessageActivity;
import com.example.qchat.Model.MUser;
import com.example.qchat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment extends Fragment
{
    TextView UserName;
    CircleImageView ImageURL;
    FirebaseUser fuser;
    DatabaseReference reference;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View  view=inflater.inflate(R.layout.fragment_profile,container,false);
        ImageURL=view.findViewById(R.id.PFprofile_image);
        UserName=view.findViewById(R.id.PFusernameTV);

        fuser= FirebaseAuth.getInstance().getCurrentUser();
        reference= FirebaseDatabase.getInstance().getReference("Profile").child(fuser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                MUser u=snapshot.getValue(MUser.class);

                UserName.setText(u.getUserName());

            if(u.getImageURL().equals("default"))
                {
                    ImageURL.setImageResource(R.drawable.black_user);
                }
                else
                {
                    Glide.with(getContext()).load(u.getImageURL()).into(ImageURL);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return view;
    }
}