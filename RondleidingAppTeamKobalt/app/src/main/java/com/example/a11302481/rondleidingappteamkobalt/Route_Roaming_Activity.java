package com.example.a11302481.rondleidingappteamkobalt;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Route_Roaming_Activity extends AppCompatActivity implements View.OnClickListener {

    private int major;
    private Button roamingButton;
    private Button routeButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route__roaming_);

        roamingButton=(Button) findViewById(R.id.roamingButton);
        roamingButton.setOnClickListener(this);

        routeButton=(Button) findViewById(R.id.routeButton);
        routeButton.setOnClickListener(this);

        //ophalen van de meegstuurde major uit de vorige activity
        Intent intent = getIntent();
        major = intent.getIntExtra("major",major);

    }

    @Override
    public void onClick(View v) {
        Intent intent;
        //openen van nieuwe activity en doorgeven van de major a.d.v de ingedrukte button
        switch (v.getId()) {
            case R.id.roamingButton:
                intent = new Intent(this, SearchingActivity.class);
                intent.putExtra("major", major);
                startActivity(intent);
                break;
            case R.id.routeButton:
                intent = new Intent(this, RouteChoiceActivity.class);
                intent.putExtra("major", major);
                startActivity(intent);
                break;
        }

    }
}
