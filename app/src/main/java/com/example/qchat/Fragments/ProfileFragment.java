package com.example.qchat.Fragments;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.qchat.MessageActivity;
import com.example.qchat.Model.MUser;
import com.example.qchat.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;


public class ProfileFragment extends Fragment
{
    TextView UserName;
   CircleImageView ImageURL;
    ImageView uploadImage;
    FirebaseUser fuser;
    DatabaseReference reference;

    StorageReference storageReference;
    StorageReference storageReference1;
    FirebaseStorage firebaseStorage;
    private  static  final int IMAGE_REQUEST=1;
    private Uri imageUri;
    private StorageTask uploadTask;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View  view=inflater.inflate(R.layout.fragment_profile,container,false);
        ImageURL=view.findViewById(R.id.PFprofile_image);
        uploadImage=view.findViewById(R.id.UploadImageId);
        UserName=view.findViewById(R.id.PFusernameTV);
        firebaseStorage=FirebaseStorage.getInstance();
        storageReference=firebaseStorage.getReference();
        storageReference1=firebaseStorage.getReference();
        fuser= FirebaseAuth.getInstance().getCurrentUser();
        reference= FirebaseDatabase.getInstance().getReference("Profile").child(fuser.getUid());
        storageReference1.child(fuser.getUid()).child("Images/Profile Pic").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
        {
            @Override
            public void onSuccess(Uri uri)
            {
                Glide.with(getContext()).load(uri).fitCenter().centerCrop().into(ImageURL);
                String mUrise=uri.toString();

                reference=FirebaseDatabase.getInstance().getReference("Profile").child(fuser.getUid());
                HashMap<String,Object>map=new HashMap<>();
                map.put("imageURL",mUrise);
                reference.updateChildren(map);
            }
        });

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                MUser u=snapshot.getValue(MUser.class);

                UserName.setText(u.getUserName());

            if(u.getImageURL().equals("default"))
                {
                    ImageURL.setImageResource(R.drawable.person2);
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

        uploadImage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                openImage();
            }
        });
        return view;
    }

    private void openImage()
    {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,IMAGE_REQUEST);

    }
    private  String getFileExtention(Uri uri)
    {
        ContentResolver contentResolver=getContext().getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
    private void uploadImage()
    {
        final ProgressDialog pd=new ProgressDialog(getContext());
        pd.setMessage("Uploading...");
        pd.show();

        if(imageUri !=null)
        {
            final  StorageReference fileReference= storageReference.child(fuser.getUid()).child("Images").child("Profile Pic");
            uploadTask=fileReference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot,Task<Uri>>()
            {
                @Override
                public Task<Uri> then(@NonNull Task task) throws Exception
                {
                    if(!task.isSuccessful())
                    {
                        throw task.getException();
                    }
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>()
            {
                @Override
                public void onComplete(@NonNull Task<Uri> task)
                {
                    if(task.isSuccessful())
                    {
                        Uri downloadFile=task.getResult();
                        String mUri=downloadFile.toString();

                        reference=FirebaseDatabase.getInstance().getReference("Profile").child(fuser.getUid());
                        HashMap<String,Object>map=new HashMap<>();
                        map.put("imageURL",mUri);
                        reference.updateChildren(map);

                        pd.dismiss();
                    }
                    else
                    {
                        Toast.makeText(getContext(),"Faild",Toast.LENGTH_LONG).show();
                        pd.dismiss();
                    }

                }
            }).addOnFailureListener(new OnFailureListener()
            {
                @Override
                public void onFailure(@NonNull Exception e)
                {
                    Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_LONG).show();

                }
            });
        }
        else
        {
            Toast.makeText(getContext(),"No image selected",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==IMAGE_REQUEST && resultCode==RESULT_OK && data!=null && data.getData()!=null)
        {
            imageUri=data.getData();
            if(uploadTask!=null && uploadTask.isInProgress())
            {
                Toast.makeText(getContext(),"Upload to progress",Toast.LENGTH_LONG).show();
            }
            else
            {
                uploadImage();
            }
        }
    }
}