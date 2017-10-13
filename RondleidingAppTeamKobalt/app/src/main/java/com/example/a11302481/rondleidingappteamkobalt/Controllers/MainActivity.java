package com.example.a11302481.rondleidingappteamkobalt.Controllers;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.example.a11302481.rondleidingappteamkobalt.Models.RetrieveData;
import com.example.a11302481.rondleidingappteamkobalt.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private String[] arraySpinner;

    private Button startButton;
    private Spinner s;
    private RetrieveData dataSource;
    private SharedPreferences savedValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startButton=(Button) findViewById(R.id.startButton);
        startButton.setOnClickListener(this);
        dataSource=new RetrieveData();
        //opvullen van de spinner met alle campi van UCLL (statische data zal later vervangen worden door data uit de database)
        this.arraySpinner = dataSource.getAllCampi();
        s = (Spinner) findViewById(R.id.campusSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinner);
        s.setAdapter(adapter);
        savedValues=getSharedPreferences("SavedValues",MODE_PRIVATE);
    }

    public void onClick(View v) {
        int major=dataSource.getCampusId(s.getSelectedItem().toString());
        //major per campus toewijzen aan de hand van de gemaakte keuze in spinnen (zal later met data uit de database vervangen worden)

        //starten van nieuwe activity en het doorgeven van de gewenste major aan de nieuwe activity
        Intent intent = new Intent(this, SearchingActivity.class);
        intent.putExtra("major", major);// if its int type
        startActivity(intent);

    }

    @Override
    public void onPause(){
        int positie=s.getSelectedItemPosition();

        SharedPreferences.Editor editor= savedValues.edit();

        editor.putInt("spinnerPositie",positie);
        editor.commit();
        super.onPause();
    }

    @Override
    public void onResume(){
        super.onResume();

        s.setSelection(savedValues.getInt("spinnerPositie", 0));

    }
}
