package com.example.safecircle;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private Button register;
    private Button login;

    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        auth = FirebaseAuth.getInstance();
        register = findViewById(R.id.register);
        login = findViewById(R.id.login);

        //String sessionkey = UUID.randomUUID().toString();
        long currenttime = System.currentTimeMillis();
        long sessionDuration = 60000;
        //FirebaseDatabase.getInstance().getReference("sessions").child("Session_Details").child("sessionToken").setValue(sessionkey);
        //sessionRef.child(dbuser).child("sessionToken").setValue(sessionToken);
        //FirebaseDatabase.getInstance().getReference("sessions").child("Session_Details").child("timestamp").setValue(currenttime);

//        if (System.currentTimeMillis() - currenttime < sessionDuration){
//            Intent j=new Intent(MainActivity.this, Alert_one.class);
//            startActivity(j);
//            finish();
//        }else{
//
//            FirebaseDatabase.getInstance().getReference("sessions").removeValue();
//            Toast.makeText(this, "Session Expired", Toast.LENGTH_SHORT).show();
//        }



        FirebaseDatabase.getInstance().getReference("sessions").child("Session_Details").child("timestamp").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    long sessionStartTime = dataSnapshot.getValue(Long.class);
                    long sessionDuration = 60000;
                    // Check if the session has expired
                    if (System.currentTimeMillis() - sessionStartTime < sessionDuration) {
                        // Session Validation
                        //FirebaseDatabase.getInstance().getReference("sessions").child("Session_Details").removeValue();
                        //Toast.makeText(MainActivity.this, "Session Expired", Toast.LENGTH_SHORT).show();
                        Intent ho = new Intent(MainActivity.this, Sos1.class);
                        startActivity(ho);
                        finish();
                    } else {
                        // Session is still invalid
                        //FirebaseDatabase.getInstance().getReference("sessions").child("Session_Details").removeValue();
                        //Toast.makeText(MainActivity.this, "Session Expired", Toast.LENGTH_SHORT).show();

                    }
                } else {
                    // No session found
                    //Toast.makeText(MainActivity.this, "No active session found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(MainActivity.this, Register_Page.class);
                startActivity(i);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent t=new Intent(MainActivity.this, Login_Page.class);
                startActivity(t);
            }
        });
    }
}