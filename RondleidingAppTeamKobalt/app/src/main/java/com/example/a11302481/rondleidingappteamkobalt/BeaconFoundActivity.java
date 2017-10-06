package com.example.a11302481.rondleidingappteamkobalt;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class BeaconFoundActivity extends AppCompatActivity {

    TextView majorTextView;
    TextView minorTextView;
    TextView distanceTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon_found);


        Intent intent = getIntent();

        int major=0;
        major=intent.getIntExtra("major",major);
        int minor=0;
        minor=intent.getIntExtra("minor",minor);
        String distance=intent.getStringExtra("distance");


        majorTextView=(TextView) findViewById(R.id.majorTextView);
        minorTextView=(TextView) findViewById(R.id.minorTextView);
        distanceTextView=(TextView) findViewById(R.id.distanceTextView);


        majorTextView.setText(Integer.toString(major));
        minorTextView.setText(Integer.toString(minor));
        distanceTextView.setText(distance);

    }
}
