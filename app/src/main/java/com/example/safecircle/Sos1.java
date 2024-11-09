package com.example.safecircle;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class Sos1 extends AppCompatActivity {
    private Button sos,sostwo;
    private String phoneNo;
    private String formattedPhoneNo;
    private FusedLocationProviderClient fusedLocationClient;
    private String test = "+91"+"9321985590";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sos1);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sos = findViewById(R.id.sos);
        sostwo = findViewById(R.id.sostwo);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
               ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.SEND_SMS}, 1);
            }

        sos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(Sos1.this, "Hello", Toast.LENGTH_SHORT).show();
                if (ContextCompat.checkSelfPermission(Sos1.this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    getPhoneNumberFromFirebase();
                } else {
                    //ActivityCompat.requestPermissions(Sos1.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                    //Toast.makeText(Sos1.this, "Permission not granted!", Toast.LENGTH_SHORT).show();
                    ActivityCompat.requestPermissions(Sos1.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                }
            }
        });

        sostwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent more=new Intent(Sos1.this, Alert_one.class);
                startActivity(more);

            }
        });
        FirebaseDatabase.getInstance().getReference("sessions").child("Session_Details").child("timestamp").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    long sessionStartTime = dataSnapshot.getValue(Long.class);
                    long sessionDuration = 60000;
                    // Check if the session has expired
                    if (System.currentTimeMillis() - sessionStartTime >= sessionDuration) {
                        // Session expired
                        FirebaseDatabase.getInstance().getReference("sessions").child("Session_Details").removeValue();
                        Toast.makeText(Sos1.this, "Session Expired", Toast.LENGTH_SHORT).show();
                        Intent ho = new Intent(Sos1.this, MainActivity.class);
                        startActivity(ho);
                        finish();
                    } else {
                        //FirebaseDatabase.getInstance().getReference("sessions").child("Session_Details")
                        // Session is still valid
                        //Toast.makeText(Sos1.this, "Session Restored", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // No session found
                    //Toast.makeText(Alert_one.this, "No active session found", Toast.LENGTH_SHORT).show();
                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Sos1.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });
       // if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
         //   ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.SEND_SMS}, 1);
        //}
        //getPhoneNumberFromFirebase();

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with sending SMS
                getPhoneNumberFromFirebase();
            } else {
                Toast.makeText(this, "Permission denied to send SMS", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void getPhoneNumberFromFirebase() {
        // Reference to Firebase Realtime Database
        //DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users/phoneNumber");

        // Add a listener to retrieve the phone number
        FirebaseDatabase.getInstance().getReference("User_Details").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Retrieve the phone number from Firebase
                phoneNo = dataSnapshot.child("More_details").child("Emergency_contact").getValue(String.class);
                if (phoneNo != null) {
                    Toast.makeText(Sos1.this, "Phone Number Retrieved: " + phoneNo, Toast.LENGTH_SHORT).show();
                    Log.d("SMS", "Phone Number: " + phoneNo);
                    getLocationAndSendSMS();
                } else {
                    Toast.makeText(Sos1.this, "Phone number not found!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //handle any error
                Toast.makeText(Sos1.this, "Failed to load phone number.", Toast.LENGTH_SHORT).show();
            }


        });

    }


    private void getLocationAndSendSMS() {
//        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            return;
//        }
//        fusedLocationClient.getLastLocation()
//                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
//                    @Override
//                    public void onSuccess(Location location) {
//                        if (location != null) {
//                            double latitude = location.getLatitude();
//                            double longitude = location.getLongitude();
//                            String locationString = "https://www.google.com/maps?q=" + latitude + "," + longitude;
//                            sendSMSToContact(locationString);
//                        } else {
//                            ActivityCompat.requestPermissions(Sos1.this,
//                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                                    1);
//                            //Toast.makeText(Alert_one.this, "Failed to get location", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });

        //loocatio
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();
                            String locationString = "https://www.google.com/maps?q=" + latitude + "," + longitude;
                            sendSMSToContact(locationString);
                        } else {
                            Toast.makeText(Sos1.this, "Location not available", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            ActivityCompat.requestPermissions(Sos1.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

    }
    private void sendSMSToContact(String location) {
        FirebaseDatabase.getInstance().getReference("User_Details")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //phoneNo = dataSnapshot.child("More_details").child("Emergency_contact").getValue(String.class);
                        String username = dataSnapshot.child("More_details").child("Username").getValue(String.class);
                        String address = dataSnapshot.child("More_details").child("Address").getValue(String.class);
                        String myphone = dataSnapshot.child("More_details").child("Phone_number").getValue(String.class);
                        //formattedPhoneNo = "+91" + phoneNo.replaceAll("^0", "");
                        formattedPhoneNo = "+91" + phoneNo.replaceAll("^0", "");

                        if (formattedPhoneNo != null) {
                            //formattedPhoneNo = "+91" + phoneNo.replaceAll("^0", "");
                            String message = "I am in danger! My current location is this: " + location +"\n" + "My address"+address +"\n" + "Phone no."+phoneNo;
                            sendSMS(formattedPhoneNo, message);
                        } else {
                            Toast.makeText(Sos1.this, "Phone number not found!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(Sos1.this, "Failed to load phone number.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void sendSMS(String test, String message) {
        if (message.length() > 0) {
            try {

                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(test, null, message, null, null);
                Toast.makeText(this, "SMS Sent!", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, "SMS failed, please try again.", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Message is empty.", Toast.LENGTH_SHORT).show();
        }
    }
}