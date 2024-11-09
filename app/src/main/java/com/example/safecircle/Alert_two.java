package com.example.safecircle;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Alert_two extends AppCompatActivity {

    private CheckBox checkbox1,checkbox2,checkbox3,checkbox4,checkbox5,checkbox6;
    private Button submit;
    private EditText message;
    private String phoneNo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_alert_two);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        checkbox1 = findViewById(R.id.checkBox1);
        checkbox2 = findViewById(R.id.checkBox2);
        checkbox3 = findViewById(R.id.checkBox3);
        checkbox4 = findViewById(R.id.checkBox4);
        checkbox5 = findViewById(R.id.checkBox5);
        checkbox6 = findViewById(R.id.checkBox6);
        submit = findViewById(R.id.submit);
        message = findViewById(R.id.message);


        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.SEND_SMS}, 1);
        }
        getPhoneNumberFromFirebase();
        // Set onClickListener for the Send SMS button
        submit.setOnClickListener(v -> sendSMS());
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with sending SMS
                sendSMS();
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
                    Toast.makeText(Alert_two.this, "Phone Number Retrieved: " + phoneNo, Toast.LENGTH_SHORT).show();
                    Log.d("SMS", "Phone Number: " + phoneNo);
                } else {
                    Toast.makeText(Alert_two.this, "Phone number not found!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //handle any error
                Toast.makeText(Alert_two.this, "Failed to load phone number.", Toast.LENGTH_SHORT).show();
            }


        });
    }
    private void sendSMS() {


        if (phoneNo == null || phoneNo.isEmpty()) {
            Toast.makeText(this, "Phone number is not available.", Toast.LENGTH_SHORT).show();
            return;
        }
        String formattedPhoneNo = "+91" + phoneNo.replaceAll("^0", "");
        Log.d("Alert_two", "Sending SMS to: " + formattedPhoneNo);

        String customMessage = message.getText().toString();

        StringBuilder message = new StringBuilder();

        if (checkbox1.isChecked()) {
            message.append("Someone is stalking me!.\n");
        }
        if (checkbox2.isChecked()) {
            message.append("Facing domestic violence.\n");
        }
        if (checkbox3.isChecked()) {
            message.append("Someone is sexually harassing me.\n");
        }
        if (checkbox4.isChecked()) {
            message.append("Someone is threatening and abusing me.\n");
        }
        if (checkbox5.isChecked()) {
            message.append("Facing abuse.\n");
        }
        if (checkbox6.isChecked()) {
            message.append("Facing Deception and fraud.\n");
        }

        // Append the custom message
        if (!customMessage.isEmpty()) {
            message.append("Message: ").append(customMessage);
        }

        // Ensure the message is not empty before attempting to send
        if (message.length() > 0) {
            try {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(formattedPhoneNo, null, message.toString(), null, null);
                Toast.makeText(this, "SMS Sent!", Toast.LENGTH_SHORT).show();
                Intent i= new Intent(Alert_two.this, Alert_one.class);
                startActivity(i);
                finish();
            } catch (Exception e) {
                Toast.makeText(this, "SMS failed, please try again.", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Please enter a message or select an option", Toast.LENGTH_SHORT).show();
        }
    }
}