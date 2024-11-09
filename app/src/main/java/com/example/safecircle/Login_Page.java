package com.example.safecircle;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.UUID;

public class Login_Page extends AppCompatActivity {
    private EditText userid,passkey;
    private Button button;
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        button = findViewById(R.id.button);
        userid = findViewById(R.id.userid);
        passkey = findViewById(R.id.passkey);
        auth = FirebaseAuth.getInstance();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              String user=userid.getText().toString();
              String pass=passkey.getText().toString();

                if(TextUtils.isEmpty(user)||TextUtils.isEmpty(pass)){
                    if(user.isEmpty()){
                        userid.setError("Can't be empty");
                    }if(pass.isEmpty()){
                        passkey.setError("Can't be empty");
                    }
                }else{
                    //check(user,pass);
                    FirebaseDatabase.getInstance().getReference().child("User_Details").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                           // String val= snapshot.getValue();
                            //FirebaseDatabase.getInstance().getReference("sessions");
                            if(snapshot.exists()){

                                for(DataSnapshot snapshot1:snapshot.getChildren()) {
                                    User_Details userDetails = snapshot.getValue(User_Details.class);
                                    if (userDetails != null) {
                                        String dbuser = userDetails.getUsername();
                                        String dbkey = userDetails.getPassword();
                                        if (user.equals(dbuser) && pass.equals(dbkey)) {
                                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                                            //Toast.makeText(Login_Page.this, "Welcome," + dbuser, Toast.LENGTH_SHORT).show();

                                            String sessionkey = UUID.randomUUID().toString();
                                            long currenttime = System.currentTimeMillis();
                                            //long sessionDuration = 60000;
                                            FirebaseDatabase.getInstance().getReference("sessions").child("Session_Details").child("sessionToken").setValue(sessionkey);
                                            //sessionRef.child(dbuser).child("sessionToken").setValue(sessionToken);
                                            FirebaseDatabase.getInstance().getReference("sessions").child("Session_Details").child("timestamp").setValue(currenttime);

                                            //if (System.currentTimeMillis() - currenttime < sessionDuration){
                                            Intent j=new Intent(Login_Page.this, Sos1.class);
                                            startActivity(j);
                                            finish();
                                            //}else{
                                               // FirebaseDatabase.getInstance().getReference("sessions").removeValue();
                                                //Intent al=new Intent(Login_Page.this, MainActivity.class);
                                                //startActivity(al);
                                                //finish();

                                            //}
                                            //finish();
                                        } else {
                                            Toast.makeText(Login_Page.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Toast.makeText(Login_Page.this, "Enter valid details.", Toast.LENGTH_SHORT).show();
                                    }
                                }//end of for loop
                                //Toast.makeText(Login_Page.this, "Invalid;(", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(Login_Page.this, "User not found.", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(Login_Page.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    public void check(String email,String password){
        auth.signInWithEmailAndPassword(email,password).addOnSuccessListener(Login_Page.this, new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                //Toast.makeText(Login_Page.this, "Welcome user!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}