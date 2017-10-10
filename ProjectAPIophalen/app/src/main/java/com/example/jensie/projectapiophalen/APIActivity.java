package com.example.jensie.projectapiophalen;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class APIActivity extends AppCompatActivity
implements View.OnClickListener{

    public static TextView dataTextView;
    Button testButton;
    Button specificButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_api);

        //tekstview en knop init.
        dataTextView = (TextView) findViewById(R.id.dataTextView);
        testButton = (Button) findViewById(R.id.testButton);
        specificButton = (Button) findViewById(R.id.specificButton);

        testButton.setOnClickListener(this);
        specificButton.setOnClickListener(this);
    }

    //als men op de knop drukt.
    @Override
    public void onClick(View v) {

        //connectivity opzetten
        ConnectivityManager cManager = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        //bekijken als men verbonden is.
        NetworkInfo nInfo = cManager.getActiveNetworkInfo();
        //kijken als je verbonden bent of niet en toon een toast.
        if(nInfo != null && nInfo.isConnected()){
            //nakijken welke knop is gedrukt.
            switch (v.getId()){
                case R.id.testButton:
                    //klasse aanmaken.
                    fetchData process = new fetchData();
                    //uitvoeren.
                    process.execute();
                    break;
                case R.id.specificButton:
                    fetchspecificData processSpecific = new fetchspecificData();
                    processSpecific.execute();
            }
        }else{
            Toast.makeText(this, "Deze applicatie vraagt wifi of 4G, gelieve deze aan te zetten.",
                    Toast.LENGTH_LONG).show();
        }
    }
}
