package com.example.a11302481.rondleidingappteamkobalt;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private String[] arraySpinner;

    private Button startButton;
    private Spinner s;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startButton=(Button) findViewById(R.id.startButton);
        startButton.setOnClickListener(this);

        //opvullen van de spinner met alle campi van UCLL (statische data zal later vervangen worden door data uit de database)
        this.arraySpinner = new String[] {
                "Clenardus","Comenius","Diepenbeek", "Gasthuisberg", "Hertogstraat", "LiZa", "Oude Luikerbaan", "Proximus", "Sociale School"
        };
        s = (Spinner) findViewById(R.id.campusSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinner);
        s.setAdapter(adapter);
    }

    public void onClick(View v) {
        int major=0;
        //major per campus toewijzen aan de hand van de gemaakte keuze in spinnen (zal later met data uit de database vervangen worden)
        switch(s.getSelectedItem().toString()){
            case "Clenardus":
                major=1;
                break;
            case "Comenius":
                major=2;
                break;
            case "Diepenbeek":
                major=3;
                break;
            case "Gasthuisberg":
                major=4;
                break;
            case "Hertogstraat":
                major=5;
                break;
            case "LiZa":
                major=6;
                break;
            case "Oude Luikerbaan":
                major=7;
                break;
            case "Proximus":
                major=8;
                break;
            case "Sociale School":
                major=9;
                break;
        }
        //starten van nieuwe activity en het doorgeven van de gewenste major aan de nieuwe activity
        Intent intent = new Intent(this, SearchingActivity.class);
        intent.putExtra("major", major);// if its int type
        startActivity(intent);

    }
}
