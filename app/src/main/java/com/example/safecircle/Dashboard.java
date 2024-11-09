package com.example.safecircle;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.Manifest;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.location.LocationRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import android.location.Location;
import android.widget.Toast;

//import com.google.android.gms.location.CLocationRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class Dashboard extends AppCompatActivity implements OnMapReadyCallback{
    private TextView user,phone,add;
    private ListView phoneListView;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private Button direct;
    private LocationRequest locationRequest;

    String[] phoneNumbers = {
            "Police: 100",
            "Emergency: 112",
            "Ambulance: 102",
            "Women-Helpline: 181",
            "Medical-Helpline: 108"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        user = findViewById(R.id.user);
        phone = findViewById(R.id.phone);
        phoneListView = findViewById(R.id.phoneListView);
        add = findViewById(R.id.add);
        direct = findViewById(R.id.direct);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, phoneNumbers);

        // Set the adapter to the ListView
        phoneListView.setAdapter(adapter);

        // Set an item click listener to open the dialer when a phone number is clicked
        phoneListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected item
                String selectedItem = (String) parent.getItemAtPosition(position);

                // Extract the phone number (assuming the format is "Text: PhoneNumber")
                String phoneNumber = selectedItem.split(": ")[1]; // Get the phone number after the ": "
                String cleanPhoneNumber = phoneNumber.replaceAll("[^0-9]", "");
                // Create an Intent to open the phone dialer
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + cleanPhoneNumber));  // Add the phone number to the URI

                // Start the dialer activity
                startActivity(intent);
            }
        });
        direct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Dashboard.this,Sos1.class);
                startActivity(i);
                finish();
            }
        });



        FirebaseDatabase.getInstance().getReference("User_Details").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String username = snapshot.child("Username").getValue(String.class);
                    String daddress = snapshot.child("More_details").child("Address").getValue(String.class);
                    String phoneNumber = snapshot.child("More_details").child("Phone_number").getValue(String.class);

                    //user.setText(username);
                    //address.setText(daddress);
                    //phone.setText(phoneNumber);

                    user.setText(username != null ? username : "N/A");
                    add.setText(daddress != null ? daddress : "N/A");
                    phone.setText(phoneNumber != null ? phoneNumber : "N/A");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //handle any error
            }
        });


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        SupportMapFragment mapFragment= (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if(mapFragment!=null){
            mapFragment.getMapAsync(this);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
        }

    }

    //outside onCreate
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Check for location permissions
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
//                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
//            return;
//        }
//        mMap.setMyLocationEnabled(true);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }

        // Get current location and move the camera
        getCurrentLocation();

//        LocationRequest locationRequest = LocationRequest.create();
//        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        locationRequest.setInterval(10000); // 10 seconds
//        locationRequest.setFastestInterval(5000); // 5 seconds
//        CurrentLocationRequest currentLocationRequest = CurrentLocationRequest.create();
//        currentLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        currentLocationRequest.setMaxUpdateAgeMillis(10000); // Optional: Set max age for cached locations

//        fusedLocationClient.getCurrentLocation(curr,null)
//                .addOnSuccessListener(this, location -> {
//                    if (location != null) {
//                        // Get latitude and longitude
//                        LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
//                        mMap.addMarker(new MarkerOptions().position(userLocation).title("You are here"));
//                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
//                    }else {
//                        // Handle case where location is null
//                        Toast.makeText(this, "Unable to retrieve location", Toast.LENGTH_SHORT).show();
//                    }
//                });


    }
    private void getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            // Get the current location
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();

                            // Move the camera to the current location
                            LatLng currentLocation = new LatLng(latitude, longitude);
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));

                            // Add a marker at the current location
                            mMap.addMarker(new MarkerOptions().position(currentLocation).title("You are here"));
                        }
                    });
        } else {
            // Request permission if not granted
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, retry getting the location
                onMapReady(mMap);
                getCurrentLocation();
            }else {
                // Permission denied, show a message
                Toast.makeText(this, "Location permission required", Toast.LENGTH_SHORT).show();
            }
        }
    }
}