package com.example.a11302481.rondleidingappteamkobalt.Controllers;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
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
import com.example.a11302481.rondleidingappteamkobalt.Models.Beacon;
import com.example.a11302481.rondleidingappteamkobalt.Scanner.BeaconScanner;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private String[] arraySpinner;

    private Button startButton;
    private Spinner s;
    private RetrieveData dataSource;
    private final static String TAG = MainActivity.class.getSimpleName();
    private BluetoothAdapter btAdapter;
    private boolean searching;
    private Beacon nearestBeacon;

    private BeaconScanner beaconScanner;
    ArrayAdapter<String> adapter;

    /**
     * Get the campi and set them in the spinner.
     * Check permissions an d start the scan for a beacon.
     *
     * @param savedInstanceState
     */
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
        timerHandler.postDelayed(timerRunnable, 0);
        searching=true;
        startScan();
    }

    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {

        /**
         *
         * runs without a timer by reposting this handler at the end of the runnable.
         * If the timer is ended and there is an beacon, the beaconfound function is activated.
         *
         */
        @Override
        public void run() {
            if(searching){
                List beaconLijst=beaconScanner.getFoundBeacons();
                if(!beaconLijst.isEmpty()){
                    if(beaconLijst.get(0) instanceof Integer){

                    }else {
                        for (Object o : beaconLijst) {
                            Beacon foundBeacon = ((Beacon) o);
                            if (nearestBeacon == null) {
                                nearestBeacon = foundBeacon;
                            } else {
                                if (nearestBeacon.getAccuracy() > foundBeacon.getAccuracy()) {
                                    nearestBeacon = foundBeacon;
                                }
                            }
                        }
                        searching = false;
                        s.setSelection(adapter.getPosition(dataSource.getCampusName(nearestBeacon.getMajor())));
                    }
                }
            }

            timerHandler.postDelayed(this, 500);
        }
    };

    /**
     * If user pressed start button.
     * Check if internet service is on. Gets the selected campus and launches new activity.
     *
     * @param v
     */
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


            startActivity(intent);
        }else{
            Toast.makeText(this, "Deze applicatie vraagt wifi of 4G, gelieve deze aan te zetten.",
                    Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Stop the scanning when activity goes on background.
     */
    @Override
    protected void onStop() {
        super.onStop();
        // stop scanning
        stopScan();
    }

    /**
     * Starts the scanner.
     */
    private void startScan(){
        beaconScanner.start();
    }

    /**
     * Stops the scanner.
     */
    private void stopScan(){
        beaconScanner.stop();
    }
}
