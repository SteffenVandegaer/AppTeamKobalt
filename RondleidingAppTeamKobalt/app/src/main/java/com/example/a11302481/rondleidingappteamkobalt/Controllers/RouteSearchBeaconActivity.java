package com.example.a11302481.rondleidingappteamkobalt.Controllers;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.a11302481.rondleidingappteamkobalt.Models.Beacon;
import com.example.a11302481.rondleidingappteamkobalt.Models.Route;
import com.example.a11302481.rondleidingappteamkobalt.R;
import com.example.a11302481.rondleidingappteamkobalt.Scanner.BeaconScanner;

import java.util.List;

import static com.example.a11302481.rondleidingappteamkobalt.Controllers.SearchingActivity.REQUEST_ENABLE_BT;

public class RouteSearchBeaconActivity extends AppCompatActivity {
    private Route route;
    private boolean searching;
    private BeaconScanner beaconScanner;
    private int majorToFind;
    long startTime = 0;

    private final static String TAG = RouteSearchBeaconActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_selected);
        Intent i = getIntent();
        majorToFind=i.getIntExtra("major",-1);
        route = i.getExtras().getParcelable("route");
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
        beaconScanner.start();

        searching=true;
        startTime = System.currentTimeMillis();
        timerHandler.postDelayed(timerRunnable, 0);

    }

    int seconds, previousSeconds=0;
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

                    }else{

                        for(Object o:beaconLijst){
                            Beacon foundBeacon=(Beacon)o;
                            displayContent(foundBeacon);
                            searching=false;
                        }

                    }
                }
                timerHandler.postDelayed(this, 500);
            }

        }
    };

    public void displayContent(Beacon beacon){
        //deze functie start de BeaconFoundActivity op en geeft de info ban het dichtsbijzijnde beacon weer

        Intent i = new Intent(this, BeaconInRouteFoundActivity.class);

        i.putExtra("major", beacon.getMajor());
        i.putExtra("minor", beacon.getMinor());
        i.putExtra("route", route);
        startActivity(i);
        finish();
    }
}