package com.example.findworkshopuser;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<Workshopinformation> arrayList;
    private FirebaseRecyclerOptions<Workshopinformation> options;
    private FirebaseRecyclerAdapter<Workshopinformation, FirebaseViewHolder> adapter;
    private DatabaseReference databaseReference;
    ImageView imageWhatsapp;
    private LocationManager locationManager;
    double  userLat, userLong;

//

//    BottomNavigationView bottomNavigationView;

    @Override
    protected void onStart() {
        super.onStart();



        adapter.startListening();

        }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        MenuItem item = menu.findItem(R.id.search);
        MenuItem item1 = menu.findItem(R.id.filter);
        MenuItem item2 = menu.findItem(R.id.update_profile);
        MenuItem item3 = menu.findItem(R.id.logout);


        final androidx.appcompat.widget.SearchView searchView = (androidx.appcompat.widget.SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                firebaseSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                firebaseSearch(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }





    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Find Workshop!");
        imageWhatsapp = (ImageView) findViewById(R.id.image);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("nearby_workshop")
        .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        arrayList = new ArrayList<Workshopinformation>();
        databaseReference.keepSynced(true);

        // Ni untuk recyclerview biasa
        options = new FirebaseRecyclerOptions.Builder<Workshopinformation>().setQuery(databaseReference.orderByChild("distance"), Workshopinformation.class).build();

        adapter = new FirebaseRecyclerAdapter<Workshopinformation, FirebaseViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FirebaseViewHolder holder, final int position, @NonNull final Workshopinformation model) {
                DecimalFormat df2 = new DecimalFormat("#.##");
                Location crntLocation=new Location("crntlocation");
                crntLocation.setLatitude(userLat);
                crntLocation.setLongitude(userLong);

                Location newLocation=new Location("newlocation");
                newLocation.setLatitude(model.getLatitude());
                newLocation.setLongitude(model.getLongitude());


                //float distance = crntLocation.distanceTo(newLocation);  in meters
                double distance =crntLocation.distanceTo(newLocation) / 1000; // in km
//                Toast.makeText(HomeActivity.this, distance+"" , Toast.LENGTH_SHORT).show();
//                Log.d("testDistance",distance+"ni lokasi hang");

                holder.workshopname.setText(model.getName());
                holder.address.setText(model.getAddress());
                holder.spintext.setText(model.getSpintext());
                holder.contact.setText(model.getContact());
                holder.distance.setText(df2.format(distance)+"KM");
                holder.whatsapp.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {
                       String url = "https://api.whatsapp.com/send?phone=+6"+model.getContact();
                       Intent i = new Intent(Intent.ACTION_VIEW);
                       i.setData(Uri.parse(url));
                       startActivity(i);
                       }

               });
                Glide.with(getApplicationContext())
                        .load(model.getImage())
                        .into(holder.Image);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(HomeActivity.this, MapsActivity.class);
                        intent.putExtra("workshopname", model.getName());
                        intent.putExtra("address", model.getAddress());
                        intent.putExtra("spintext", model.getSpintext());
                        intent.putExtra("longitude", model.getLongitude());
                        intent.putExtra("latitude", model.getLatitude());
                        intent.putExtra("workshop_id",getSnapshots().getSnapshot(position).getKey());
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public FirebaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new FirebaseViewHolder(LayoutInflater.from(HomeActivity.this).inflate(R.layout.row, parent, false));
            }

        };

        recyclerView.setAdapter(adapter);


        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if(!locationManager. isProviderEnabled(LocationManager.GPS_PROVIDER)){
            onGPS();
        }else {
            getLocation();

        }


    }

    // Ni untuk search filter punya recyclerview
    private void firebaseSearch(String searchText)
    {
        String quary = searchText.toUpperCase();
        final Query firebaseSearchQuery = databaseReference.orderByChild("name").startAt(quary).endAt(quary + "\uf8ff");
        options = new FirebaseRecyclerOptions.Builder<Workshopinformation>().setQuery(firebaseSearchQuery,Workshopinformation.class).build();

        firebaseSearchQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChildren())
                {
                    arrayList.clear();
                    for(DataSnapshot dss: dataSnapshot.getChildren())
                    {
                        final Workshopinformation workshopinformation = dss.getValue(Workshopinformation.class);
                        arrayList.add(workshopinformation);
                    }



                    adapter = new FirebaseRecyclerAdapter<Workshopinformation, FirebaseViewHolder>(options) {
                        @Override
                        protected void onBindViewHolder(@NonNull FirebaseViewHolder holder, final int position, @NonNull final Workshopinformation model) {

                            DecimalFormat df2 = new DecimalFormat("#.##");
                            Location crntLocation=new Location("crntlocation");
                            crntLocation.setLatitude(userLat);
                            crntLocation.setLongitude(userLong);

                            Location newLocation=new Location("newlocation");
                            newLocation.setLatitude(model.getLatitude());
                            newLocation.setLongitude(model.getLongitude());


                            //float distance = crntLocation.distanceTo(newLocation);  in meters
                            double distance =crntLocation.distanceTo(newLocation) / 1000; // in km
//                Toast.makeText(HomeActivity.this, distance+"" , Toast.LENGTH_SHORT).show();
//                Log.d("testDistance",distance+"ni lokasi hang");
                            holder.workshopname.setText(model.getName());
                            holder.address.setText(model.getAddress());
                            holder.spintext.setText(model.getSpintext());
                            holder.contact.setText(model.getContact());
                            holder.distance.setText(df2.format(distance)+"KM");
                            holder.whatsapp.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    String url = "https://api.whatsapp.com/send?phone=+6"+model.getContact();
                                    Intent i = new Intent(Intent.ACTION_VIEW);
                                    i.setData(Uri.parse(url));
                                    startActivity(i);
                                }

                            });
                            Glide.with(getApplicationContext())
                                    .load(model.getImage())
                                    .into(holder.Image);
                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(HomeActivity.this, MapsActivity.class);
                                    intent.putExtra("workshopname", model.getName());
                                    intent.putExtra("address", model.getAddress());
                                    intent.putExtra("spintext", model.getSpintext());
                                    intent.putExtra("longitude", model.getLongitude());
                                    intent.putExtra("latitude", model.getLatitude());
                                    intent.putExtra("workshop_id",getSnapshots().getSnapshot(position).getKey());


                                    startActivity(intent);
                                }
                            });

                        }

                        @NonNull
                        @Override
                        public FirebaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                            return new FirebaseViewHolder(LayoutInflater.from(HomeActivity.this).inflate(R.layout.row, parent, false));

//                FirebaseViewHolder firebaseViewHolder = new FirebaseViewHolder(itemView);
//                firebaseViewHolder.
                        }
                    };

                    adapter.notifyDataSetChanged();
                    recyclerView.setAdapter(adapter);
                    adapter.startListening();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.filter:
                showSortDialog();
                return true;
            case R.id.update_profile:
                startActivity(new Intent(getApplicationContext(), UpdateProfile.class));
                return true;
            case R.id.logout:
                logout();
                Toast.makeText(this,"Successfully Logout!", Toast.LENGTH_SHORT);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private void showSortDialog(){
        String[] options = {"All", "Workshop", "Tyre", "Acessories", "Aircond", "Repaint"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Filter By Category");
        builder.setIcon(R.drawable.ic_filter_list_black_24dp);
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                if (which==0){//All clicked

                    Query query = databaseReference.orderByChild("spintext");
                    setupRecycler(query);
                    adapter.startListening();

                }
                if (which==1){//Workshop clicked

                    Query query = databaseReference.orderByChild("spintext").equalTo("workshop");
                    setupRecycler(query);
                    adapter.startListening();

                }
                if (which==2){//Tyre clicked

                    Query query = databaseReference.orderByChild("spintext").equalTo("tyre");
                    setupRecycler(query);
                    adapter.startListening();

                }
                if (which==3){//Acessories clicked
                    Query query = databaseReference.orderByChild("spintext").equalTo("acessories");
                    setupRecycler(query);
                    adapter.startListening();
                }
                if (which==4){//Aircond clicked
                    Query query = databaseReference.orderByChild("spintext").equalTo("aircond");
                    setupRecycler(query);
                    adapter.startListening();
                }
                if (which==5){//Repaint clicked
                    Query query = databaseReference.orderByChild("spintext").equalTo("repaint");
                    setupRecycler(query);
                    adapter.startListening();
                }
            }
        });
        builder.create().show(); //show dialog
    }

    // Ni untuk filter punya recyclerview
    private void setupRecycler(Query query) {

        options = new FirebaseRecyclerOptions.Builder<Workshopinformation>().setQuery(query, Workshopinformation.class).build();
        adapter = new FirebaseRecyclerAdapter<Workshopinformation, FirebaseViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FirebaseViewHolder holder, final int position, @NonNull final Workshopinformation model) {
                DecimalFormat df2 = new DecimalFormat("#.##");
                Location crntLocation=new Location("crntlocation");
                crntLocation.setLatitude(userLat);
                crntLocation.setLongitude(userLong);

                Location newLocation=new Location("newlocation");
                newLocation.setLatitude(model.getLatitude());
                newLocation.setLongitude(model.getLongitude());


                //float distance = crntLocation.distanceTo(newLocation);  in meters
                double distance =crntLocation.distanceTo(newLocation) / 1000; // in km
//                Toast.makeText(HomeActivity.this, distance+"" , Toast.LENGTH_SHORT).show();
//                Log.d("testDistance",distance+"ni lokasi hang");

                holder.workshopname.setText(model.getName());
                holder.address.setText(model.getAddress());
                holder.spintext.setText(model.getSpintext());
                holder.contact.setText(model.getContact());
                holder.distance.setText(df2.format(distance)+"KM");
                holder.whatsapp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String url = "https://api.whatsapp.com/send?phone=+6"+model.getContact();
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                    }

                });
                Glide.with(getApplicationContext())
                        .load(model.getImage())
                        .into(holder.Image);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(HomeActivity.this, MapsActivity.class);
                        intent.putExtra("workshopname", model.getName());
                        intent.putExtra("address", model.getAddress());
                        intent.putExtra("spintext", model.getSpintext());
                        intent.putExtra("longitude", model.getLongitude());
                        intent.putExtra("latitude", model.getLatitude());
                        intent.putExtra("workshop_id",getSnapshots().getSnapshot(position).getKey());
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public FirebaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new FirebaseViewHolder(LayoutInflater.from(HomeActivity.this).inflate(R.layout.row, parent, false));
            }

        };

        recyclerView.setAdapter(adapter);
    }

    private void logout(){
        FirebaseAuth.getInstance().signOut();

        Intent intent = new Intent(getApplicationContext(), User_login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void getLocation() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission
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

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 20, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        fetchData(location.getLatitude(),location.getLongitude());
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
                });

            }
            Log.d("test",userLat+ " "+userLong+"ni lokasi hang");

        }

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

// ni untuk filter
    public void fetchData(final double latitude, final double longitude){
        FirebaseDatabase.getInstance().getReference("Workshops").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (final DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    final Workshopinformation workshopinformation = postSnapshot.getValue(Workshopinformation.class);

                    final Location workshopLocation = new Location("workshop");
                    workshopLocation.setLatitude(workshopinformation.getLatitude());
                    workshopLocation.setLongitude(workshopinformation.getLongitude());

                    final Location myLocation = new Location("myLocation");
                    myLocation.setLatitude(latitude);
                    myLocation.setLongitude(longitude);
//Ni baru
                    if (myLocation.distanceTo(workshopLocation) <= 20000){
                        FirebaseDatabase.getInstance().getReference("nearby_workshop").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child(postSnapshot.getKey()).setValue(workshopinformation).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                FirebaseDatabase.getInstance().getReference("Workshops").child(postSnapshot.getKey()).child("rating")
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                for (DataSnapshot ratingSnapshot : dataSnapshot.getChildren()){
                                                    RatingModel ratingModel = ratingSnapshot.getValue(RatingModel.class);
                                                    FirebaseDatabase.getInstance().getReference("nearby_workshop").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                            .child(postSnapshot.getKey()).child("rating").child(ratingSnapshot.getKey()).setValue(ratingModel);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                Map<String,Object> distance = new HashMap<>();
                                distance.put("distance",myLocation.distanceTo(workshopLocation));
                                FirebaseDatabase.getInstance().getReference("nearby_workshop").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .child(postSnapshot.getKey()).updateChildren(distance);

                            }
                        });
                    }
                    else {
                        FirebaseDatabase.getInstance().getReference("nearby_workshop").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child(postSnapshot.getKey()).removeValue();
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
