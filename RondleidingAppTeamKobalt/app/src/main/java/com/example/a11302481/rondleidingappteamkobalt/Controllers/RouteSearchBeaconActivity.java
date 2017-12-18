package com.example.a11302481.rondleidingappteamkobalt.Controllers;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.a11302481.rondleidingappteamkobalt.Models.Beacon;
import com.example.a11302481.rondleidingappteamkobalt.Models.RetrieveData;
import com.example.a11302481.rondleidingappteamkobalt.Models.Route;
import com.example.a11302481.rondleidingappteamkobalt.R;
import com.example.a11302481.rondleidingappteamkobalt.Scanner.BeaconScanner;

import java.util.ArrayList;
import java.util.List;

import static com.example.a11302481.rondleidingappteamkobalt.Controllers.SearchingActivity.REQUEST_ENABLE_BT;

public class RouteSearchBeaconActivity extends AppCompatActivity implements View.OnClickListener {
    private Route route;
    private boolean searching;
    private BeaconScanner beaconScanner;
    private int majorToFind;
    long startTime = 0;
    private int setTimer;

    private final static String TAG = RouteSearchBeaconActivity.class.getSimpleName();
    private ImageButton closeButton;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_selected);
        Intent i = getIntent();
        majorToFind=i.getIntExtra("major",-1);
        route = i.getExtras().getParcelable("route");
        setTimer=i.getExtras().getInt("algeweest",0);
        if(route.getProgress()+1>route.countBeacons()){
            setContentView(R.layout.end_route_view);
            closeButton=(ImageButton) findViewById(R.id.closeButton);
            closeButton.setOnClickListener(this);

        }else {

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
            beaconScanner = new BeaconScanner(btAdapter, majorToFind, route.getBeaconMinor(route.getProgress()), 5);
            beaconScanner.start();

            searching = true;
            startTime = System.currentTimeMillis();
            timerHandler.postDelayed(timerRunnable, 0);

        }
    }

    void showAlert(){
        searching=false;
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        v.vibrate(500);
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        startTime=System.currentTimeMillis();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        resetFunction();
                        break;
                }
                searching=true;
            }
        };
        RetrieveData dataSource = new RetrieveData();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Het volgende informatiepunt is niet gevonden. Bent u nog onderweg? Indien u niet meer onderweg bent keer dan terug naar het vorige informtiepunt en klik nee.").setPositiveButton("Ja", dialogClickListener)
                .setNegativeButton("Nee", dialogClickListener).setCancelable(false).show();
    }

    public void resetFunction(){
        startTime=System.currentTimeMillis();
        if(route.getProgress()>0) {
            route.setProgress(route.getProgress() - 1);
        }
        timerHandler.removeCallbacksAndMessages(null);
        beaconScanner.stop();
        searching=false;
        Intent intent = new Intent(this, RouteSearchBeaconActivity.class);
        intent.putExtra("major", majorToFind);
        intent.putExtra("route",route);
        intent.putExtra("algeweest",1);
        startActivity(intent);
        finish();
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
                long millis = System.currentTimeMillis() - startTime;
                if(setTimer!=0) {
                    if (millis > 10000) {
                        searching = false;
                        showAlert();
                    } else {
                        List beaconLijst = beaconScanner.getFoundBeacons();
                        if (!beaconLijst.isEmpty()) {
                            if (beaconLijst.get(0) instanceof Integer) {

                            } else {

                                for (Object o : beaconLijst) {

                                    if(((Beacon)o).getMinor()==route.getBeaconMinor(route.getProgress())){
                                        Beacon foundBeacon = (Beacon) o;
                                        displayContent(foundBeacon);
                                        searching = false;
                                    }else{
                                        searching=true;
                                    }


                                }

                            }
                        }
                    }
                }else{
                    List beaconLijst = beaconScanner.getFoundBeacons();
                    if (!beaconLijst.isEmpty()) {
                        if (beaconLijst.get(0) instanceof Integer) {

                        } else {

                            for (Object o : beaconLijst) {
                                searching = false;
                                Beacon foundBeacon = (Beacon) o;
                                displayContent(foundBeacon);

                            }

                        }
                    }
                }

            }
            timerHandler.postDelayed(this, 500);

        }
    };



    public void displayContent(Beacon beacon){

        //deze functie start de BeaconFoundActivity op en geeft de info ban het dichtsbijzijnde beacon weer
        timerHandler.removeCallbacksAndMessages(null);
        beaconScanner.stop();
        searching=false;
        Intent i = new Intent(this, BeaconInRouteFoundActivity.class);

        i.putExtra("major", beacon.getMajor());
        i.putExtra("minor", beacon.getMinor());
        i.putExtra("route", route);
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        v.vibrate(500);
        startActivity(i);
        finish();
    }

    @Override
    public void onClick(View v) {
        timerHandler.removeCallbacksAndMessages(null);
        Intent intent = new Intent(this, Route_Roaming_Activity.class);
        intent.putExtra("major", majorToFind);// if its int type
        startActivity(intent);
        finish();
    }

    public void closeFunction(){
        timerHandler.removeCallbacksAndMessages(null);
        beaconScanner.stop();
        Intent intent = new Intent(this, Route_Roaming_Activity.class);
        intent.putExtra("major", majorToFind);
        startActivity(intent);
        finish();
    }

    /**
     *
     * If back button is pressed the close function is called.
     *
     */
    public void onBackPressed(){
        closeFunction();
    }
}