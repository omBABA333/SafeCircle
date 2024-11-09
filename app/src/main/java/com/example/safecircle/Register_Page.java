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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;


public class Register_Page extends AppCompatActivity {
    private FirebaseAuth auth;
    private EditText pass,user;
    private Button button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        auth = FirebaseAuth.getInstance();
        pass = findViewById(R.id.pass);
        user = findViewById(R.id.user);
        button = findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = user.getText().toString();
                String passkey = pass.getText().toString();
                if(TextUtils.isEmpty(username)||TextUtils.isEmpty(passkey)){
                    if(username.isEmpty()){
                        user.setError("Can't be empty");
                    }if(passkey.isEmpty()){
                        pass.setError("Can't be empty");
                    }
                }else{
                    //add(username,passkey);
                    HashMap<String, Object> hm=new HashMap<String, Object>();
                    hm.put("Username",username);
                    hm.put("Password",passkey);
                    FirebaseDatabase.getInstance().getReference().child("User_Details").setValue(hm).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(Register_Page.this, "Redirecting...", Toast.LENGTH_SHORT).show();
                            Intent in=new Intent(Register_Page.this,More_details_Page.class);
                            startActivity(in);

                        }
                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(Register_Page.this, "Error saving user details!", Toast.LENGTH_SHORT).show();
                                }
                            });

                }
            }
        });

    }
    public void add(String userid,String key){
        auth.createUserWithEmailAndPassword(userid,key).addOnCompleteListener(Register_Page.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    //Toast.makeText(Register_Page.this, "User Registered!", Toast.LENGTH_SHORT).show();
                }else{
                    //Toast.makeText(Register_Page.this, "Failed;(", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}