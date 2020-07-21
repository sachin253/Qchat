package com.example.qchat;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;

import android.os.Bundle;

import android.view.View;
import android.widget.Button;

import android.widget.Toast;

import com.example.qchat.Model.MUser;
import com.google.android.gms.tasks.OnCompleteListener;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.rengwuxian.materialedittext.MaterialEditText;



public class RegisterActivity extends AppCompatActivity
{

    FirebaseAuth fAuth;
    DatabaseReference dbrefrence;
    MaterialEditText userName,email,password,cPassword;
    String Semail, Sname,ScPassword, Spassword,userId;
    Button RegBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        userName=findViewById(R.id.userNameId);
        email=findViewById(R.id.emaiId);
        password=findViewById(R.id.passwordId);
        cPassword=findViewById(R.id.conformPasswordId);
        RegBtn=findViewById(R.id.regBtnId);

        fAuth=FirebaseAuth.getInstance();
        RegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validate()){
                    String user_email = email.getText().toString().trim();
                    String user_password = password.getText().toString().trim();

                    fAuth.createUserWithEmailAndPassword(user_email, user_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if(task.isSuccessful())
                            {

                                sendEmailVerification();
                               /* sendUserData();
                                Toast.makeText(RegisterActivity.this, "Successfully Registered, Upload complete!", Toast.LENGTH_SHORT).show();                                fAuth.signOut();
                                finish();
                                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                              fAuth.signOut();
                                finish();
                                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));*/
                            }else
                                {
                                Toast.makeText(RegisterActivity.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }
            }
        });
    }

    private Boolean validate(){
        Boolean result = false;

        Sname = userName.getText().toString();
        Spassword = password.getText().toString();
        Semail = email.getText().toString();
        ScPassword=cPassword.getText().toString();



        if(Sname.isEmpty() || Spassword.isEmpty() || Semail.isEmpty() || ScPassword.isEmpty() ){
            Toast.makeText(this, "Please enter all the details", Toast.LENGTH_SHORT).show();
        }else{
            result = true;
        }

        return result;
    }


    private void sendEmailVerification()
    {
        final FirebaseUser firebaseUser =fAuth.getCurrentUser();
        if(firebaseUser!=null){
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(RegisterActivity.this, "Verification mail sent!", Toast.LENGTH_LONG).show();
                        userId=firebaseUser.getUid();
                        sendUserData();
                        fAuth.signOut();
                        finish();
                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                    }else
                        {

                        Toast.makeText(RegisterActivity.this, "Verification mail has'nt been sent!", Toast.LENGTH_LONG).show();
                        }
                }
            });
        }
    }


    private void sendUserData()
    {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        dbrefrence = firebaseDatabase.getReference("Profile");
        MUser user=new MUser(Semail,Sname,"default",userId,"offline");
        dbrefrence.child(userId).setValue(user);
        Toast.makeText(this, "Data successfully stored in database", Toast.LENGTH_SHORT).show();

    }

}