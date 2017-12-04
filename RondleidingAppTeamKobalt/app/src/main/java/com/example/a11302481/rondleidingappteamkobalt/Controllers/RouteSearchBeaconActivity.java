package com.example.a11302481.rondleidingappteamkobalt.Controllers;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a11302481.rondleidingappteamkobalt.Models.Route;
import com.example.a11302481.rondleidingappteamkobalt.R;
import com.example.a11302481.rondleidingappteamkobalt.Scanner.BeaconScanner;

import static com.example.a11302481.rondleidingappteamkobalt.Controllers.SearchingActivity.REQUEST_ENABLE_BT;

public class RouteSearchBeaconActivity extends AppCompatActivity {
    private Route route;
    private TextView TextViewTest;
    private boolean searching;
    private BeaconScanner beaconScanner;
    private int majorToFind;

    private final static String TAG = RouteSearchBeaconActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_selected);
        Intent i = getIntent();
        majorToFind=i.getIntExtra("major",-1);
        route = i.getExtras().getParcelable("route");
        TextViewTest = (TextView) findViewById(R.id.textViewTest);
        TextViewTest.setText(route.getBeaconMinor(2));
        searching=true;

        // check for needed permissions and if they are granted, move on
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Logging
            Log.w(TAG, "Location access not granted!");
            // If not granted ask for permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 42);
        }

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            // show toast
            Toast.makeText(getApplicationContext(), "BLE not supported", Toast.LENGTH_SHORT).show();

            // end app
            finish();
        }

        // create BT intent
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        // starts the activity depending on the result
        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);

        BluetoothAdapter btAdapter = ((BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();
        btAdapter.enable();
        beaconScanner=new BeaconScanner(btAdapter,route.getBeaconMinor(route.getProgress()),5);
    }




}