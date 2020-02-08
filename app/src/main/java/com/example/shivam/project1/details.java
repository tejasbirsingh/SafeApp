package com.example.shivam.project1;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class details extends AppCompatActivity {
    public static final String TAG = "TAG";
    EditText firstNameEditText,lastNameEditText,emailEditText, emergencyContactEditText;
    Button saveBtn;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        firstNameEditText = findViewById(R.id.firstName);
        lastNameEditText = findViewById(R.id.lastName);
        emailEditText = findViewById(R.id.emailAddress);
        saveBtn = findViewById(R.id.saveBtn);
        emergencyContactEditText =findViewById(R.id.emergencycontact);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userID = fAuth.getCurrentUser().getUid();


        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(firstNameEditText.getText().toString().isEmpty()||lastNameEditText.getText().toString().isEmpty() || emailEditText.getText().toString().isEmpty()){
                    Toast.makeText(details.this, "Fill the required Details", Toast.LENGTH_SHORT).show();
                    return;
                }
                String firstName = firstNameEditText.getText().toString();
                String lastName = lastNameEditText.getText().toString();
                String email = emailEditText.getText().toString();
                String emergencyContact = emergencyContactEditText.getText().toString();
                if(emergencyContact == null || emergencyContact.length() < 10) {
                    Toast.makeText(details.this,"Add a valid emergency Number",Toast.LENGTH_LONG).show();
                    return;
                }
                DocumentReference docRef = fStore.collection("users").document(userID);

                Map<String,Object> user = new HashMap<>();


                user.put("first",firstName);
                user.put("last",lastName);
                user.put("email",email);
                user.put("EmergencyContact",emergencyContact);
                //add user to database
                docRef.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: User Profile Created." + userID);
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: Failed to Create User " + e.toString());
                    }
                });
            }
        });
    }
}
