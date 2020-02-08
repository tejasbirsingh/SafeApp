package com.example.shivam.project1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MyReceiver extends BroadcastReceiver {
    private int countPowerOff = 0;
    private CountDownTimer countDownTimer;
    private long timeleftinmilliseconds = 6000;
    private boolean timerunning = false;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore fStore;
    private String userPhoneNumber;
    public MyReceiver() {

    }


    @Override
    public void onReceive(final Context context, Intent intent) {
        Intent phoneIntent = new Intent(Intent.ACTION_CALL);

        final long[] pattern = {0, 1000};
        firebaseAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        DocumentReference docRef =fStore.collection("users").document(firebaseAuth.getCurrentUser().getUid());
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                userPhoneNumber = documentSnapshot.getString("EmergencyContact");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(context,"Something went wrong!!!.",Toast.LENGTH_LONG).show();
                return;
            }
        });


        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {

            if(timerunning != true) {
                ++countPowerOff;
                countDownTimer = new CountDownTimer(timeleftinmilliseconds, 1000) {
                    @Override
                    public void onTick(long l) {

                        timeleftinmilliseconds = l;
                    }

                    @Override
                    public void onFinish() {
                        timeleftinmilliseconds = 6000;
                        timerunning = false;
                        countPowerOff = 0;
                    }
                }.start();
                timerunning = true;

            }
            else
            {
                ++countPowerOff;
                if(countPowerOff >= 5)
                {

                    SmsManager mySmsManager = SmsManager.getDefault();
                    mySmsManager.sendTextMessage(userPhoneNumber,null, "Hello", null, null);
                    Vibrator vibrator = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
                    long[] pattern1 = new long[]{0, 2000};
                    vibrator.vibrate(pattern1, -1);
                    Toast.makeText(context, "Message Sent ", Toast.LENGTH_LONG).show();
                    phoneIntent.setData(Uri.parse("tel:" + userPhoneNumber));

                    countPowerOff = 0;
                }
            }





        }
        else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            if(timerunning != true) {
                ++countPowerOff;
                countDownTimer = new CountDownTimer(timeleftinmilliseconds, 1000) {
                    @Override
                    public void onTick(long l) {
                        timeleftinmilliseconds = l;

                    }

                    @Override
                    public void onFinish() {
                        timeleftinmilliseconds = 6000;
                        timerunning = false;
                        countPowerOff = 0;
                    }
                }.start();
                timerunning = true;

            }
            else
            {
                ++countPowerOff;
                Log.d("broadcastt", String.valueOf(countPowerOff));
                if (countPowerOff >= 5) {

                    SmsManager mySmsManager = SmsManager.getDefault();
                    mySmsManager.sendTextMessage(userPhoneNumber,null, "Hello", null, null);
                    Vibrator vibrator = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
                    long[] pattern1 = new long[]{0, 2000};
                    vibrator.vibrate(pattern1, -1);
                    Toast.makeText(context, "Message Sent ", Toast.LENGTH_LONG).show();
                    countPowerOff = 0;

                }


            }




        }






    }
}
