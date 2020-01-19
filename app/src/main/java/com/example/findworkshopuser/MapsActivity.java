package com.example.findworkshopuser;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener, GoogleMap.OnMarkerClickListener {

    RatingBar mRatingBar;
    TextView mRatingScale;
    EditText mFeedback;
    Button mSendFeedback;
    private GoogleMap mMap;
    private ChildEventListener mChildEventListener;
    private DatabaseReference mUsers;
    Marker marker;
    String workshop;
    double lat, lon, userLat, userLong;
    private LocationManager locationManager;
    private DatabaseReference mDatabase;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    private RecyclerView recyclerView;
    private ArrayList<Feedbackinformation> arrayList;
    private FirebaseRecyclerOptions<Feedbackinformation> options;
    private FirebaseRecyclerAdapter<Feedbackinformation, RatingViewHolder> adapter;
    private DatabaseReference databaseReference;


    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();

    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Intent extras = getIntent();
        if (extras != null) {
            lat = extras.getDoubleExtra("latitude",0);
            lon = extras.getDoubleExtra("longitude",0);
            workshop = extras.getStringExtra("workshopname");
            //The key argument here must match that used in the other activity
        }
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Workshops").child(extras.getStringExtra("workshop_id")).child("rating");
        recyclerView = findViewById(R.id.recyclerviews);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        arrayList = new ArrayList<Feedbackinformation>();

        databaseReference.keepSynced(true);
        options = new FirebaseRecyclerOptions.Builder<Feedbackinformation>().setQuery(databaseReference, Feedbackinformation.class).build();

        adapter = new FirebaseRecyclerAdapter<Feedbackinformation, RatingViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull RatingViewHolder holder, final int position, @NonNull final Feedbackinformation model) {

                holder.ratingBar.setRating((int) model.getRatingbar());
                holder.ratingscale.setText(model.getRatingscale());
                holder.sendfeedback.setText(model.getSendfeedback());
                holder.userName.setText(model.getUserName());
//                holder.itemView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        Intent intent = new Intent(MapsActivity.this, MapsActivity.class);
//                        intent.putExtra("ratingbar", model.getRatingbar());
//                        intent.putExtra("ratingscale", model.getRatingscale());
//                        intent.putExtra("sendfeedback", model.getSendfeedback());
//                        intent.putExtra("userName", model.getUserName());
//                        startActivity(intent);
//                    }
//                });
            }

            @NonNull
            @Override
            public RatingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new RatingViewHolder(LayoutInflater.from(MapsActivity.this).inflate(R.layout.reviewrow, parent, false));
            }
        };


        recyclerView.setAdapter(adapter);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        ChildEventListener mChildEventListener;
        mUsers = FirebaseDatabase.getInstance().getReference("Workshops");
        mUsers.push().setValue(marker);

        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            onGPS();
        }else {
            getLocation();

        }

        //For Rating and Review
        mDatabase = FirebaseDatabase.getInstance().getReference("Workshops");
        mRatingBar = (RatingBar) findViewById(R.id.ratingBar);
        mRatingScale = (TextView) findViewById(R.id.tvRatingScale);
        mFeedback = (EditText) findViewById(R.id.etFeedback);
        mSendFeedback = (Button) findViewById(R.id.btnSubmit);

        mRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                mRatingScale.setText(String.valueOf(v));
                switch ((int) ratingBar.getRating()) {
                    case 1:
                        mRatingScale.setText("Very bad");
                        break;
                    case 2:
                        mRatingScale.setText("Need some improvement");
                        break;
                    case 3:
                        mRatingScale.setText("Good");
                        break;
                    case 4:
                        mRatingScale.setText("Great");
                        break;
                    case 5:
                        mRatingScale.setText("Awesome. I love it");
                        break;
                    default:
                        mRatingScale.setText("");
                }
            }
        });

        mSendFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mFeedback.getText().toString().isEmpty()) {
                    Toast.makeText(com.example.findworkshopuser.MapsActivity.this, "Please fill in feedback text box", Toast.LENGTH_LONG).show();
                } else {
                    saveFeedbackInformation();
                    mRatingBar.getRating();
                    mRatingScale.getText();
                    mFeedback.getText();
                    mFeedback.setText("");
                    mRatingBar.setRating(0);
                    Toast.makeText(com.example.findworkshopuser.MapsActivity.this, "Thank you for sharing your feedback", Toast.LENGTH_SHORT).show();
                }
            }
        });

//        mRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
//            @Override
//            public void onRatingChanged(RatingBar ratingBar, float ratingbar, boolean b) {
//                submitRating();
//                FirebaseDatabase.getInstance().getReference("Workshops").child("rating").orderByChild("ratingbar");
//
//                double intRating = ratingbar;
//                mDatabase.setValue(intRating);
//            }
//        });



    }



//    public void submitRating(){
//        try {
//            FirebaseDatabase.getInstance().getReference("Workshops").child("rating");
//
//            mDatabase.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    double total = 0.0;
//                    double count = 0.0;
//                    double average = 0.0;
//
//                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
//                        double mRatingbar = Double.parseDouble(ds.child("ratingbar").getValue().toString());
//                        total = total + mRatingbar;
//                        count = count + 1;
//                        average = total / count;
//                    }
//
//                    mDatabase.child("AverageRating").child("rating").setValue(average);
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//                    throw databaseError.toException();
//
//                }
//            });
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }


        private void saveFeedbackInformation() {
        final float ratingbar = mRatingBar.getRating();
        final String ratingscale = mRatingScale.getText().toString().trim();
        final String feedback = mFeedback.getText().toString().trim();

        FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser()
        .getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserInformation userInformation = dataSnapshot.getValue(UserInformation.class);
                Feedbackinformation feedbackinformation= new Feedbackinformation(ratingbar,ratingscale,feedback,userInformation.getUserName());
                Intent extras = getIntent();
                mDatabase.child(extras.getStringExtra("workshop_id")).child("rating")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(feedbackinformation);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }



    private void getLocation() {
        if(ActivityCompat.checkSelfPermission(this,Manifest.permission
                .ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED && ActivityCompat
                .checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION},1);
        }else {
            Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Location locationNetwork = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            Location locationPassive = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

            if (locationGPS != null){
                userLat = locationGPS.getLatitude();
                userLong = locationGPS.getLongitude();
//                mMap.addMarker(new MarkerOptions()
//                        .position(new LatLng(userLat, userLong))
//                        .title("Me"));
            }
        }

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(lat, lon))
                .title(workshop));

        googleMap.setOnMarkerClickListener(this);
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mUsers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot s : dataSnapshot.getChildren()) {
//                    Workshopinformation user = s.getValue(Workshopinformation.class);
//                    LatLng location = new LatLng(lat, lon);
//                    mMap.addMarker(new MarkerOptions().position(location).title(user.name)).setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mMap.setMyLocationEnabled(true);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat,lon), 12.0f));


    }

    @Override
    public void onLocationChanged(Location location) {

        userLat = location.getLatitude();
        userLong = location.getLongitude();
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(userLat, userLong))
                .title("Me"));


        //Move the camera to the user's location and zoom in!



    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    private void onGPS() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Enable GPS").setCancelable(false).setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}

