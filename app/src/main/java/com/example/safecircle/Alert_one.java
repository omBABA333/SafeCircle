package com.example.safecircle;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.telephony.SmsManager;
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

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.io.FileOutputStream;

public class Alert_one extends AppCompatActivity {
    private Button camera, home, call, location, text;
    private FusedLocationProviderClient fusedLocationClient;
    private String phoneNo;
    private String formattedPhoneNo;
    private Bitmap imageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_alert_one);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        camera = findViewById(R.id.camera);
        home = findViewById(R.id.home);
        call = findViewById(R.id.call);
        location = findViewById(R.id.location);
        text = findViewById(R.id.text);


        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent dh = new Intent(Alert_one.this, Dashboard.class);
                startActivity(dh);
                finish();
            }
        });

        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent d = new Intent(Alert_one.this, Alert_two.class);
                startActivity(d);
                finish();
                //Toast.makeText(Alert_one.this, "Danger!", Toast.LENGTH_SHORT).show();
            }
        });
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(Alert_one.this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    getLocationAndSendSMS();
                } else {
                    ActivityCompat.requestPermissions(Alert_one.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                }
            }
        });
        getPhoneNumberFromFirebase();
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialPhoneNumber();
            }
        });


        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (ContextCompat.checkSelfPermission(Alert_one.this, android.Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(Alert_one.this,
                            new String[]{android.Manifest.permission.CAMERA}, 1);
                } else {
                    // Permission granted, open camera
                    dispatchTakePictureIntent();
                }
            }
        });
        getPhoneNumberFromFirebase();



    }

    //end of oncreate
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, fetch the location and send SMS
                getLocationAndSendSMS();
            } else {
                Toast.makeText(this, "Permission denied to access location", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getLocationAndSendSMS() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();
                            String locationString = "https://www.google.com/maps?q=" + latitude + "," + longitude;
                            sendSMSToContact(locationString);
                        } else {
                            ActivityCompat.requestPermissions(Alert_one.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    1);
                            //Toast.makeText(Alert_one.this, "Failed to get location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void sendSMSToContact(String location) {
        FirebaseDatabase.getInstance().getReference("User_Details")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        phoneNo = dataSnapshot.child("More_details").child("Emergency_contact").getValue(String.class);

                        if (phoneNo != null) {
                            String formattedPhoneNo = "+91" + phoneNo.replaceAll("^0", "");
                            String message = "I am in danger! My current location is: " + location;
                            sendSMS(formattedPhoneNo, message);
                        } else {
                            Toast.makeText(Alert_one.this, "Phone number not found!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(Alert_one.this, "Failed to load phone number.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void sendSMS(String phoneNo, String message) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, message, null, null);
            Toast.makeText(this, "SMS Sent!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "SMS failed, please try again.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
    private void getPhoneNumberFromFirebase() {
        FirebaseDatabase.getInstance().getReference("User_Details")
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                phoneNo = dataSnapshot.child("More_details").child("Emergency_contact").getValue(String.class);
                formattedPhoneNo = "+91" + phoneNo.replaceAll("^0", "");
                if (formattedPhoneNo != null) {
                    Toast.makeText(Alert_one.this, "Phone Number Retrieved: " + formattedPhoneNo, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Alert_one.this, "Phone number not found!", Toast.LENGTH_SHORT).show();
                }
            }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(Alert_one.this, "Failed to load phone number.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void dialPhoneNumber() {
        if (formattedPhoneNo != null && !formattedPhoneNo.isEmpty()) {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + formattedPhoneNo)); // Use tel: URI scheme
            startActivity(intent); // Open dialer with the phone number
        } else {
            Toast.makeText(this, "Phone number is not available.", Toast.LENGTH_SHORT).show();
        }
    }
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, 1);
        } else {
            Toast.makeText(this, "No camera app found", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            sendSMSWithBitmap(imageBitmap);
        }
    }
    private void sendSMSWithBitmap(Bitmap bitmap) {
        if (formattedPhoneNo == null || formattedPhoneNo.isEmpty()) {
            Toast.makeText(this, "Phone number is not available.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save bitmap to file
        try {
            File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "captured_image.png");
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();

            // Create URI for the image file
            Uri uri = Uri.fromFile(file);

            // Send SMS with image URI (Note: This will only work if the recipient can handle image links)
            String message = "Check out photo that i send you: " + uri.toString(); // This won't work directly in SMS; see note below.

            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(formattedPhoneNo, null, message, null, null);

            Toast.makeText(this, "SMS Sent with image link!", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(this, "Failed to save image or send SMS.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }


}
