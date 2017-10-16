package com.example.a11302481.rondleidingappteamkobalt.Controllers;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.a11302481.rondleidingappteamkobalt.Models.RetrieveData;
import com.example.a11302481.rondleidingappteamkobalt.R;
import com.example.a11302481.rondleidingappteamkobalt.Scanner.BeaconScanner;
import com.example.a11302481.rondleidingappteamkobalt.Models.Beacon;
import com.example.a11302481.rondleidingappteamkobalt.Scanner.OnScanListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, OnScanListener {

    private String[] arraySpinner;

    private Button startButton;
    private Spinner s;
    private RetrieveData dataSource;
    private SharedPreferences savedValues;
    private final static String TAG = MainActivity.class.getSimpleName();
    private BluetoothAdapter btAdapter;

    private BeaconScanner beaconScanner;
    ArrayAdapter<String> adapter;

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
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinner);
        s.setAdapter(adapter);


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

        btAdapter = ((BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();
        beaconScanner=new BeaconScanner(btAdapter);
        beaconScanner.setScanEventListener(this);
        startScan();


    }

    public void onClick(View v) {

        //connectivity opzetten
        ConnectivityManager cManager = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        //bekijken als men verbonden is.
        NetworkInfo nInfo = cManager.getActiveNetworkInfo();
        //kijken als je verbonden bent of niet en toon een toast.
        if(nInfo != null && nInfo.isConnected()) {

            int major = dataSource.getCampusId(s.getSelectedItem().toString());
            //major per campus toewijzen aan de hand van de gemaakte keuze in spinnen (zal later met data uit de database vervangen worden)

            //starten van nieuwe activity en het doorgeven van de gewenste major aan de nieuwe activity
            Intent intent = new Intent(this, SearchingActivity.class);
            intent.putExtra("major", major);// if its int type

            stopScan();
            beaconScanner.removeScanEventListener(this);

            startActivity(intent);
        }else{
            Toast.makeText(this, "Deze applicatie vraagt wifi of 4G, gelieve deze aan te zetten.",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onPause(){

        super.onPause();
    }

    @Override
    public void onResume(){
        super.onResume();



    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // stop scanning
        stopScan();
    }

    private void startScan(){
        beaconScanner.start();
    }

    private void stopScan(){
        beaconScanner.stop();
    }

    @Override
    public void onScanStopped() {

    }

    @Override
    public void onScanStarted() {

    }

    @Override
    public void onBeaconFound(Beacon beacon) {
        s.setSelection(adapter.getPosition(dataSource.getCampusName(beacon.getMajor())));
        stopScan();
        beaconScanner.removeScanEventListener(this);


    }
}
