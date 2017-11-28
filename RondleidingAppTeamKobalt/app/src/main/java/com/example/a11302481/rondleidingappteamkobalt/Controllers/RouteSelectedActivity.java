package com.example.a11302481.rondleidingappteamkobalt.Controllers;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.a11302481.rondleidingappteamkobalt.Models.Beacon;
import com.example.a11302481.rondleidingappteamkobalt.Models.RetrieveData;
import com.example.a11302481.rondleidingappteamkobalt.Models.Route;
import com.example.a11302481.rondleidingappteamkobalt.R;
import com.example.a11302481.rondleidingappteamkobalt.Scanner.BeaconScanner;

public class RouteSelectedActivity extends AppCompatActivity {
    private Route route;
    private TextView TextViewTest;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_selected);
        Intent i=getIntent();
        route = (Route)i.getExtras().getParcelable("route");
        TextViewTest=(TextView) findViewById(R.id.textViewTest);
        TextViewTest.setText(route.getName());
    }


}
