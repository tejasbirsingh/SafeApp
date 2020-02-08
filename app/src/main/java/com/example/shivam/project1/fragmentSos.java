package com.example.shivam.project1;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class fragmentSos extends Fragment {
    private Button sos;
    private CountDownTimer countDownTimer;
    private long timeleftinmilliseconds = 30000;
    private boolean timerrunning = true;
    private boolean isRecording = false;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore fStore;
    private  String userPhoneNumber;
    private  ProgressWheel linear;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
      View view= inflater.inflate(R.layout.fragement_sos, container, false);
        firebaseAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        view.clearFocus();
        DocumentReference docRef =fStore.collection("users").document(firebaseAuth.getCurrentUser().getUid());
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                userPhoneNumber = documentSnapshot.getString("EmergencyContact");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(getContext(),"Something went wrong!!!.",Toast.LENGTH_LONG).show();
                return;
            }
        });


        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        sos = getActivity().findViewById(R.id.sos);
        sos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                onSosclick(v);
            }
        });
    }

    public void onSosclick(View view) {
        SmsManager mySmsManager = SmsManager.getDefault();
        mySmsManager.sendTextMessage(userPhoneNumber,null, "Help! Help! Help! Need Help", null, null);
        if (!isRecording) {
            final AudioRecord record = new AudioRecord();
            record.startRecording();
            Toast.makeText(getActivity(), "Recording in background started", Toast.LENGTH_SHORT).show();
            isRecording = true;
            countDownTimer = new CountDownTimer(timeleftinmilliseconds, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    timeleftinmilliseconds = millisUntilFinished;
                }

                @Override
                public void onFinish() {
                    timeleftinmilliseconds = 30000;
                    timerrunning = false;
                    record.stopRecording();
                    isRecording = false;
                    Toast.makeText(getActivity(), "Recording is stopped", Toast.LENGTH_SHORT).show();
                }
            }.start();
        }

    }

}

