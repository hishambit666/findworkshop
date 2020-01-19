package com.example.findworkshopuser;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class User_signup extends AppCompatActivity {

    EditText txtFullName, txtUserName, txtEmail, txtPassword, txtConfirmPassword;
    Spinner spin;
    Button btn_register;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user = firebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_signup);
//        getSupportActionBar().setTitle("Signup Form");

        spin = (Spinner) findViewById(R.id.spin);
        txtFullName = (EditText)findViewById(R.id.txt_full_name);
        txtUserName = (EditText)findViewById(R.id.txt_User_name);
        txtEmail = (EditText)findViewById(R.id.txt_email);
        txtPassword = (EditText)findViewById(R.id.txt_password);
        txtConfirmPassword = (EditText)findViewById(R.id.txt_confirm_password);
        btn_register = (Button)findViewById(R.id.button_reg);

        firebaseAuth = FirebaseAuth.getInstance();

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String fullName = txtFullName.getText().toString().trim();
                final String userName = txtUserName.getText().toString().trim();
                final String email = txtEmail.getText().toString().trim();
                final String password = txtPassword.getText().toString().trim();
                final String confirmPassword = txtConfirmPassword.getText().toString().trim();
                final String spintext = spin.getSelectedItem().toString().trim();
//                    UserInformation userInformation = new UserInformation(fullName,userName,email,password,confirmPassword,spintext,1);
//                   mDatabase.child (user.getUid()).setValue(userInformation);
                    Toast.makeText (User_signup.this,"Saved", Toast.LENGTH_LONG).show();





                if(TextUtils.isEmpty(fullName)){
                    Toast.makeText(  User_signup.this,  "Please Enter Full Name",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(userName)){
                    Toast.makeText (User_signup.this, "Please Enter User Name",Toast.LENGTH_SHORT).show();

                    return;
                }
                if(TextUtils.isEmpty(email)){
                    Toast.makeText (  User_signup.this,  "Please Enter Email",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    Toast.makeText (User_signup.this,  "Please Enter Password",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(confirmPassword)){
                    Toast.makeText ( User_signup.this,  "Please Re Confirm Password",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(spintext)){
                    Toast.makeText ( User_signup.this, "Please Enter Gender",Toast.LENGTH_SHORT).show();
                    return;
                }

                if(password.length()<6){
                    Toast.makeText(User_signup.this, "Password too Short", Toast.LENGTH_SHORT).show();
                }

                ;
                if(password.equals(confirmPassword)){

                    firebaseAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(User_signup.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {

                                        user = FirebaseAuth.getInstance().getCurrentUser();

                                        UserInformation userInformation = new UserInformation(fullName,userName,email,password,confirmPassword,spintext,1);
                                        mDatabase.child (user.getUid()).setValue(userInformation);

                                        FirebaseAuth.getInstance().signOut();
                                        Toast.makeText(User_signup.this, "Registration Complete",Toast.LENGTH_SHORT).show();
                                        finish();
                                    } else {

                                        Log.e("signupError","authentication fail",task.getException());
                                        Toast.makeText(User_signup.this, "Authentication Failed",Toast.LENGTH_SHORT).show();
                                    }

                                    // ...
                                }
                            });

                }

            }
        });
    }


//    public void btn_workshopLogin(View view) {
//
//        startActivity(new Intent(getApplicationContext(), Workshop_Login.class));
//    }


}

