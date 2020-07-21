package com.example.qchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity
{

    private EditText Name;
    private EditText Password;
    private TextView Info;
    private Button Login;
    private int counter = 5;
    private TextView userRegistration;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private TextView forgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Name = (EditText)findViewById(R.id.NameeditText1);
        Password = (EditText)findViewById(R.id.PasseditText3);
        Info = (TextView)findViewById(R.id.infotextView);
        Login = (Button)findViewById(R.id.Logbutton22);
        userRegistration = (TextView)findViewById(R.id.RegtextView2);
        forgotPassword = (TextView)findViewById(R.id.ForgetPasstextView2);

        Info.setText("No of attempts remaining: 5");

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        FirebaseUser user = firebaseAuth.getCurrentUser();

        if(user != null){
            finish();
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
        }

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try
                {
                    if(Name.getText().toString()==null||Password.getText().toString()==null)
                    {
                        Toast.makeText(LoginActivity.this,"Please Enter UserName and Password",Toast.LENGTH_LONG).show();
                    }
                    else
                        {
                        validate(Name.getText().toString(), Password.getText().toString());
                        }

                } catch (Exception e)
                {
                    e.printStackTrace();
                }

            }
        });

        userRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });


        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                startActivity(new Intent(LoginActivity.this, PasswordActivity.class));
            }
        });
    }

    private void validate(String userName, String userPassword) {

        progressDialog.setMessage("Process working.....");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(userName, userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    progressDialog.dismiss();

                    checkEmailVerification();
                }
                else
                    {
                    Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                    counter--;
                    Info.setText("No of attempts remaining: " + counter);
                    progressDialog.dismiss();
                    if(counter == 0)
                    {
                        Login.setEnabled(false);
                    }
                }
            }
        });


    }

    private void checkEmailVerification()
    {
        FirebaseUser firebaseUser = firebaseAuth.getInstance().getCurrentUser();
        Boolean emailflag = firebaseUser.isEmailVerified();

        if(emailflag)
        {
           Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
            finish();
            startActivity(new Intent(LoginActivity.this, MainActivity.class));

       }
        else
           {
           Toast.makeText(this, "Verify your email", Toast.LENGTH_SHORT).show();

              startActivity(new Intent(LoginActivity.this, LoginActivity.class));
               firebaseAuth.signOut();
            }
    }


}

