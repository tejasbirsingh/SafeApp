package com.example.shivam.project1;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hsalf.smilerating.SmileRating;

import java.util.HashMap;
import java.util.Map;

public class fragmentAddSafetyRatings extends AppCompatActivity {
    private String postalcode;
    public static final String TAG = "FragmentAddSafetyRating";
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private DatabaseReference databaseReference;
    String userID;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_add_rating);
        final View addRatingsView =  findViewById(R.id.addRatingView);
        final ProgressBar progressBar = findViewById(R.id.submitProgressBar);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userID = FirebaseAuth.getInstance().getUid();

//        Get the intent and intent arguments
//        make a bundle
//        get the postal code addSafetyratings();
        Intent intent = getIntent();
        postalcode = intent.getStringExtra("pincode");
        Log.i(TAG,"postal code :"+postalcode);
        addSafetyRatings(progressBar);

    }
    private void addSafetyRatings(final ProgressBar progressBar) {
        final SmileRating secureRatingBar = findViewById(R.id.securityRatingBar);
        final SmileRating lightRatingBar = findViewById(R.id.lightRatingBar);
        final SmileRating transportRatingBar = findViewById(R.id.transportRatingBar);
        final SmileRating walkPathBar = findViewById(R.id.walkRatingBar);
        final SmileRating peopleRatingBar = findViewById(R.id.peopleRatingBar);
        final SmileRating genderRatingBar = findViewById(R.id.genderRatingBar);
        final SmileRating feelingRatingBar = findViewById(R.id.feelingRatingBar);
        final Button submit = findViewById(R.id.submitRatingButton);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                float securityRating =secureRatingBar.getRating();
                float lightRating = lightRatingBar.getRating();
                float transportRating = transportRatingBar.getRating();
                float walkPathRating = walkPathBar.getRating();
                float peopleRating = peopleRatingBar.getRating();
                float genderRating = genderRatingBar.getRating();
                float feelingRating = feelingRatingBar.getRating();
//              to add the multiple ratings for a single place by multiple users
                DocumentReference documentReference = fStore.collection("ratings")
                        .document(postalcode).collection("all").document(userID);
                Map<String,Object> user  = new HashMap<>();
                user.put("security",(int)securityRating);
                user.put("lights",(int)lightRating);
                user.put("transport",(int)transportRating);
                user.put("walkpath",(int)walkPathRating);
                user.put("people",(int)peopleRating);
                user.put("gender",(int)genderRating);
                user.put("feeling",(int)feelingRating);
                documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(fragmentAddSafetyRatings.this,"Ratings added successfully",Toast.LENGTH_SHORT).show();
                        Log.i(TAG,"on success: ratings added") ;
                      finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(fragmentAddSafetyRatings.this,"Ratings not added. Something went wrong. Try again later.",Toast.LENGTH_SHORT).show();
                        submit.setVisibility(View.VISIBLE);
                        Log.e(TAG,"on failure: Something went wrong rating not added");
                    }
                });

            }
        });
    }

}
