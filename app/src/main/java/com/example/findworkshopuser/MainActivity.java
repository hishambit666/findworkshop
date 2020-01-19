package com.example.findworkshopuser;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    Button btnproceed;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String workshopname = getIntent().getStringExtra("workshopname");
        String address = getIntent().getStringExtra("address");
        Log.i("OUR VALUE",workshopname);
        Log.i("OUR VALUE 2",address);
        Toast.makeText(this,""+workshopname, Toast.LENGTH_SHORT).show();


        btnproceed=(Button)findViewById(R.id.button_proceed);

        firebaseAuth = FirebaseAuth.getInstance();
        btnproceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(MainActivity.this,MapsActivity.class);
                startActivity(i);
            }
        });

    }
}


