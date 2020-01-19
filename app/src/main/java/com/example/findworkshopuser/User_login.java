package com.example.findworkshopuser;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class User_login extends AppCompatActivity {

    EditText txtEmail, txtPassword;
    Button btn_login, btn_reg;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);
//        getSupportActionBar().setTitle("Login Form");


        txtEmail = (EditText) findViewById(R.id.txt_email);
        txtPassword = (EditText) findViewById(R.id.txt_password);
        btn_login = (Button) findViewById(R.id.buttonLogin);
        txtEmail.setText("john@gmail.com");
        txtPassword.setText("123456");
        firebaseAuth = FirebaseAuth.getInstance();

        btn_login.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String Email = txtEmail.getText().toString().trim();
                String Password = txtPassword.getText().toString().trim();

                if (TextUtils.isEmpty(Email)) {
                    Toast.makeText(User_login.this, "Please Enter Email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(Password)) {
                    Toast.makeText(User_login.this, "Please Enter Password", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (Password.length() < 6) {
                    Toast.makeText(User_login.this, "Password too Short", Toast.LENGTH_SHORT).show();
                }
                firebaseAuth.signInWithEmailAndPassword(Email, Password)
                        .addOnCompleteListener(User_login.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {

                                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));


                                } else {

                                    Toast.makeText(User_login.this, "Login Failed or User not Available", Toast.LENGTH_SHORT).show();

                                }

                            }
                        });
            }






        });


    }
    public void btn_signupForm(View view){
        startActivity(new Intent(getApplicationContext(),User_signup.class));
    }
}