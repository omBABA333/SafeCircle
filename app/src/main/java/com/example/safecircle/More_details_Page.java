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
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class More_details_Page extends AppCompatActivity {
    private EditText address,phoneno,emgno;
    private Button register;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_more_details_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        address = findViewById(R.id.address);
        phoneno = findViewById(R.id.phoneno);
        emgno = findViewById(R.id.emgno);
        register = findViewById(R.id.register);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String add = address.getText().toString().trim();
                String ph = phoneno.getText().toString().trim();
               // int phone=Integer.parseInt(ph);
                String em = emgno.getText().toString().trim();
                //int emgphone=Integer.parseInt(ph);
                if(TextUtils.isEmpty(add)||TextUtils.isEmpty(ph)||TextUtils.isEmpty(em)){
                    if(add.isEmpty()){
                        address.setError("Can't be empty");
                    }if(ph.isEmpty()){
                        phoneno.setError("Can't be empty");
                    }if(em.isEmpty()){
                        emgno.setError("Can't be empty");
                    }
                    return;
                }
                if (!isValidPhoneNumber(ph) || !isValidPhoneNumber(em)) {
                    phoneno.setError("Invalid phone number");
                    emgno.setError("Invalid emergency contact");

                }else {

                    try {
                        //int phone = Integer.parseInt(ph);
                        //int emgphone = Integer.parseInt(ph);
                        HashMap<String, Object> mh = new HashMap<String, Object>();
                        mh.put("Address", add);
                        mh.put("Phone_number", ph);
                        mh.put("Emergency_contact", em);
                        FirebaseDatabase.getInstance().getReference().child("User_Details").child("More_details").setValue(mh).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(More_details_Page.this, "User registered", Toast.LENGTH_SHORT).show();
                                    Intent hp = new Intent(More_details_Page.this, MainActivity.class);
                                    startActivity(hp);
                                    finish();
                                } else {
                                    Toast.makeText(More_details_Page.this, "Failed to register;<", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    } catch (NumberFormatException e) {
                        phoneno.setError("Enter valid number");
                        emgno.setError("Enter valid number");
                    }

                }

//                else {
//                    HashMap<String ,Object> mh=new HashMap<String ,Object>();
//                    mh.put("Address",add);
//                    mh.put("Phone_number",phone);
//                    mh.put("Emergency_contact",emgphone);
//                    FirebaseDatabase.getInstance().getReference().child("User_Details").push().setValue(mh);
//
//                }

            }
            public boolean isValidPhoneNumber(String phone){
                return phone.length()>=1 && phone.length()<=10 && phone.matches("^\\d{10}$");
            }
        });


    }
}