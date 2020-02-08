package com.example.shivam.project1;

import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Splash extends AppCompatActivity {
ImageView im;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        im=findViewById(R.id.imageview1);

        Vibrator v = (Vibrator) getSystemService(Splash.this.VIBRATOR_SERVICE);
        v.vibrate(1000);

        Animation obj1 = AnimationUtils.loadAnimation(this, R.anim.vibrate);
        im.startAnimation(obj1);
        TextView text = (TextView) findViewById(R.id.textView1);
        //Typeface face = Typeface.createFromAsset(getAssets(), "Pacifico.ttf");
       // text.setTypeface(face);

        Thread timer = new Thread() {
            public void run() {

                try {
                    sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();

                } finally {
                    Intent i = new Intent(Splash.this, register.class);
                    startActivity(i);
                    finish();
                }

            }
        };
        timer.start();

    }
}
