package com.example.shivam.project1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;
import android.widget.Toast;

public class ExampleBroadcastReciever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
            if(ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())){
                Log.d("CONN", "connection changed ho gyi");
                Toast.makeText(context,"Connectivity changed ho gyi", Toast.LENGTH_SHORT).show();

            }
    }
}
