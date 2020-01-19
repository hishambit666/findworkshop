package com.example.findworkshopuser;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UpdateProfile extends AppCompatActivity {
    private DatabaseReference mDatabase;
    EditText txtFullName, txtUserName, txtEmail, txtPassword;
    Spinner spins;
    Button buttonUpdate, buttonDelete;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_profile);
        buttonUpdate = (Button) findViewById(R.id.button_update);
        buttonDelete = (Button) findViewById(R.id.button_delete);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");


        mDatabase.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                spins = (Spinner)findViewById(R.id.spin_update);
                txtFullName = (EditText)findViewById(R.id.txt_update_full_name);
                txtUserName = (EditText)findViewById(R.id.txt_update_user_name);
                txtEmail = (EditText)findViewById(R.id.txt_update_email);
                txtPassword = (EditText)findViewById(R.id.txt_update_password);
                String fullName = (String) dataSnapshot.child("fullName").getValue();
                String userName = (String) dataSnapshot.child("userName").getValue();
                String email = (String) dataSnapshot.child("email").getValue();
                String password = (String) dataSnapshot.child("password").getValue();
                String spintext = (String) dataSnapshot.child("spintext").getValue();
                txtFullName.setText(fullName);
                txtUserName.setText(userName);
                txtEmail.setText(email);
                txtPassword.setText(password);
//                spins.setText(spintext);



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });


        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                UpdateUserInformation();
                txtFullName.getText().clear();
                txtUserName.getText().clear();
                txtEmail.getText().clear();
                txtPassword.getText().clear();
                spins.getSelectedItem();


            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DeleteUserInformation();
                txtFullName.getText().clear();
                txtUserName.getText().clear();
                txtEmail.getText().clear();
                txtPassword.getText().clear();
                spins.getSelectedItem();


            }
        });

    }

    public void ReadWorkshopInformation() {

    }

    private void UpdateUserInformation() {
        String fullName = txtFullName.getText().toString().trim();
        String userName = txtUserName.getText().toString().trim();
        String email = txtEmail.getText().toString().trim();
        String password = txtPassword.getText().toString().trim();
        String spintext = spins.getSelectedItem().toString().trim();

        final UserInformation userinformation = new UserInformation(fullName, userName, email, password, spintext, 1);
        mDatabase.child(user.getUid()).setValue(userinformation);
        Toast.makeText(UpdateProfile.this, "Updated", Toast.LENGTH_LONG).show();
    }


    private void DeleteUserInformation() {
        String fullName = txtFullName.getText().toString().trim();
        String userName = txtUserName.getText().toString().trim();
        String email = txtEmail.getText().toString().trim();
        String password = txtPassword.getText().toString().trim();
        String spintext = spins.getSelectedItem().toString().trim();
        final UserInformation userinformation = new UserInformation(fullName, userName, email, password, spintext, 1);
        mDatabase.child(user.getUid()).setValue(null);
        Toast.makeText(UpdateProfile.this, "Deleted", Toast.LENGTH_LONG).show();

    }
}


