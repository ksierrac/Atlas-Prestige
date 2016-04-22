package com.example.bryan.myapplication;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

/**
 * Splash Screen
 */
public class Start2Activity extends AppCompatActivity {

    /**
     * Creates splashscreen displaying app logo and delays into main application screen
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start2);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(Start2Activity.this, MapsActivity.class);
                startActivity(intent);
                finish();
            }
        }, 5000);
    }


}
