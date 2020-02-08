package com.example.shivam.project1;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;
public class fragmentSafetyAudit extends Fragment {
    private TextView mTextView;
    public static final String TAG = fragmentSafetyAudit.class.getName();
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private DatabaseReference databaseReference;
    String userID;
    String postalcode;
    public fragmentSafetyAudit()
    {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragement_safety_audit,container,false);
        View ratingView = view.findViewById(R.id.checkRatingView);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userID = FirebaseAuth.getInstance().getUid();
        postalcode = "144008";
        final ProgressBar progressBar = view.findViewById(R.id.checkRatingProgressBar);
        Bundle bundle = getArguments();
        if(bundle != null) {
            postalcode = bundle.getString("pincode");
            if(postalcode != null) {
                checkSafetyRatings(ratingView,progressBar);
            }
            else{
                Log.i(TAG,"Postal code not provided");
            }
        } else {
            Log.i(TAG,"Bundle arguments not provided");
            checkSafetyRatings(view,progressBar);
        }
        return view;
    }

//    @Override
//    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        mTextView = (TextView) getActivity().findViewById(R.id.pincode);
//        Bundle bundle = getArguments();
//        if(bundle != null)
//        {
////            This code is never running when you directly visit safety audit
//            postalcode = bundle.getString("pincode");
//            Log.d("bundle",postalcode);
//            mTextView.setText(postalcode);
//        }
//    }
    private void checkSafetyRatings(final View view,final ProgressBar progressBar){
        final RatingBar securityRatingBar = view.findViewById(R.id.securityRating1);
        final RatingBar lightRatingBar = view.findViewById(R.id.lightRating1);
        final RatingBar transportRatingBar = view.findViewById(R.id.transportRating1);
        final RatingBar walkPathRatingBar = view.findViewById(R.id.walkRating1);
        final RatingBar peopleRatingBar = view.findViewById(R.id.peopleRating);
        final RatingBar genderRatingBar = view.findViewById(R.id.genderRating);
        final RatingBar feelingRatingBar = view.findViewById(R.id.feelingRating);
        final TextView totalUsersTextView = view.findViewById(R.id.total_users_text_view);
        fStore.collection("ratings")
                .document(postalcode).collection("all")
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int size = queryDocumentSnapshots.size();
                if (size  <= 0) {
                    view.setVisibility(View.GONE);

                    return;
                }
                float securityRatings = 0;
                float lightRatings = 0;
                float transportRatings = 0;
                float walkpathRatings = 0;
                float peopleRating = 0;
                float genderRating = 0;
                float feelingRating = 0;
                Log.i(TAG,"size :"+size);
                for (DocumentSnapshot snapshot: queryDocumentSnapshots){
                    Map<String,Object> data = snapshot.getData();
                    for (String key:data.keySet()){
                        switch (key){
                            case "security":
                                Log.i(TAG,"security : "+data.get(key));
                                securityRatings += Float.parseFloat(data.get(key).toString());
                                Log.i(TAG,"securityRating :"+securityRatings);
                                break;
                            case "lights":
                                lightRatings += Float.parseFloat(data.get(key).toString());
                                break;
                            case "transport":
                                transportRatings += Float.parseFloat(data.get(key).toString());
                                break;
                            case "walkpath":
                                walkpathRatings += Float.parseFloat(data.get(key).toString());
                                break;
                            case "people":
                                peopleRating += Float.parseFloat(data.get(key).toString());
                                break;
                            case "gender":
                                genderRating += Float.parseFloat(data.get(key).toString());
                                break;
                            case "feeling":
                                feelingRating += Float.parseFloat(data.get(key).toString());
                                break;
                        }
                    }
                }
                float averageSecurityRatings = securityRatings/size;
                float averageLightRatings = lightRatings/size;
                float averageTransportRatings = transportRatings/size;
                float averageWalkPathRatings = walkpathRatings/size;
                float averagePeopleRating  = peopleRating/size;
                float averageGenderRating = genderRating/size;
                float averageFeelingRating = feelingRating/size;
                securityRatingBar.setRating((int)averageSecurityRatings);
                lightRatingBar.setRating((int)averageLightRatings);
                transportRatingBar.setRating((int)averageTransportRatings);
                walkPathRatingBar.setRating((int)averageWalkPathRatings);
                peopleRatingBar.setRating((int)averagePeopleRating);
                genderRatingBar.setRating((int)averageGenderRating);
                feelingRatingBar.setRating((int)averageFeelingRating);
                totalUsersTextView.setText(size+"");
                progressBar.setVisibility(View.GONE);
                Log.i(TAG,averageSecurityRatings+"");
                Log.i(TAG,averageLightRatings+"");

                Log.i(TAG,averageTransportRatings+"");

                Log.i(TAG,averageWalkPathRatings+"");


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG,"Failed");
            }
        });
    }
}

// To see the structure of data while recieving a QuerSnapshot -> ArrayList<DocumentSnapShot>

//String str = "metadata=SnapshotMetadata{hasPendingWrites=false, isFromCache=false}," +
//        " doc=Document{key=ratings/144008/all/OowLBU5XHogjUDyAAYKNtCRPepk1," +
//        " data=ArraySortedMap{(lights=>4.0), (security=>5.0), (transport=>4.0), " +
//        "(walkpath=>4.0)};, " +
//        "version=SnapshotVersion(seconds=1580909539, nanos=102635000)," +
//        " documentState=SYNCED}}," +
//        " DocumentSnapshot{key=ratings/144008/all/QLFv5XTicdMcraEWLNHHNKU6aqa2," +
//        " metadata=SnapshotMetadata{hasPendingWrites=false, isFromCache=false}, " +
//        "doc=Document{key=ratings/144008/all/QLFv5XTicdMcraEWLNHHNKU6aqa2," +
//        " data=ArraySortedMap{(lights=>4.0), (security=>4.5), (transport=>4.0)," +
//        " (walkpath=>4.0)};, version=SnapshotVersion(seconds=1580909705, nanos=46765000)," +
//        " documentState=SYNCED}}]";