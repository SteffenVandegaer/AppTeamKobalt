package com.example.a11302481.rondleidingappteamkobalt.Controllers;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a11302481.rondleidingappteamkobalt.Models.RetrieveData;
import com.example.a11302481.rondleidingappteamkobalt.Models.Beacon;
import com.example.a11302481.rondleidingappteamkobalt.Scanner.BeaconScanner;
import com.example.a11302481.rondleidingappteamkobalt.Scanner.OnScanListener;
import com.example.a11302481.rondleidingappteamkobalt.R;


public class SearchingActivity extends AppCompatActivity implements OnScanListener {

    private int majorToFind, teller=0, previousMinor=-1;

    private TextView testTextView;
    private SharedPreferences savedValues;

    private BeaconScanner beaconScanner;
    private Beacon nearestBeacon;

    private final static String TAG = SearchingActivity.class.getSimpleName();

    // request code for bluetooth
    public static final int REQUEST_ENABLE_BT = 1;

    /**
     * Checks if permissions is given. Activates bluetooth.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searching);

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

        //ophalen van de meegstuurde major uit de vorige activity
        Intent intent = getIntent();
        majorToFind = intent.getIntExtra("major",majorToFind);

        testTextView=(TextView) findViewById(R.id.testTextview);

        startTime = System.currentTimeMillis();
        timerHandler.postDelayed(timerRunnable, 0);

        BluetoothAdapter btAdapter = ((BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();
        btAdapter.enable();
        // create BT intent
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        // starts the activity depending on the result
        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        beaconScanner=new BeaconScanner(btAdapter);
        beaconScanner.setScanEventListener(this);
        stopScan();
        startScan();

        //savedValues=getSharedPreferences("SavedValues",MODE_PRIVATE);
    }


    long startTime = 0;
    int seconds;
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
            long millis = System.currentTimeMillis() - startTime;
            seconds = (int) (millis / 1000);

            if((seconds>5)&&(teller>0)&&(teller<5)){
                teller=6;
                timerHandler.removeCallbacks(timerRunnable);
                beaconFound();
            }
            if (seconds>10){
                previousMinor=-1;
            }
            timerHandler.postDelayed(this, 500);
        }
    };

    /**
     *
     * If an beacon is found there wil be an message.
     * If the user presses "ja" the displaycontent function is launched.
     * If the user presses "nee" the reset function is launched.
     *
     */
    private void beaconFound(){

        previousMinor=nearestBeacon.getMinor();
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        displayContent();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        reset();
                        break;
                }
            }
        };
        RetrieveData dataSource=new RetrieveData();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Informatiepunt:"+dataSource.getBeaconName(nearestBeacon.getMinor(),nearestBeacon.getMajor())+ " gevonden op een afstand van "+String.format( "%.2f", nearestBeacon.getAccuracy())+"m. wil u de informatie van dit punt zien?").setPositiveButton("Ja", dialogClickListener)
                .setNegativeButton("Nee", dialogClickListener).show();

    }

    /**
     *
     * This function launches the beacon foundactivity and gives info about the nearest beacon.
     *
     */
    public void displayContent(){
        //deze functie start de BeaconFoundActivity op en geeft de info ban het dichtsbijzijnde beacon weer

        Intent i = new Intent(this, BeaconFoundActivity.class);

        i.putExtra("major", nearestBeacon.getMajor());
        i.putExtra("minor", nearestBeacon.getMinor());
        startActivity(i);
    }

    /**
     *
     * If application goes to background the scanner stops with scanning.
     *
     */
    @Override
    public void onPause(){

        stopScan();

        super.onPause();
    }

    /**
     *
     * If the application is opened from the background the scan is restarted.
     *
     */
    @Override
    public void onResume(){
        super.onResume();
        startScan();
        teller=0;
        testTextView.setText(Integer.toString(teller));

    }


    /**
     *
     * When the user pressed the "nee" button, reset.
     *
     */
    private void reset(){
        nearestBeacon=null;
        teller=0;
        testTextView.setText(Integer.toString(teller));
        startTime = System.currentTimeMillis();
    }

    /**
     *
     * When the application is started the scanning is started.
     *
     */
    @Override
    protected void onStart() {
        super.onStart();
        startScan();
    }

    /**
     *
     * When the application is shutting down the scanning is stopped.
     *
     */
    @Override
    protected void onStop() {
        super.onStop();
        // stop scanning
        stopScan();
    }

    /**
     *
     * Starts the scanner.
     *
     */
    private void startScan(){
        beaconScanner.start();
    }

    /**
     *
     * Stops the scanner.
     *
     */
    private void stopScan(){
        beaconScanner.stop();
    }

    /**
     * Empty, but needed.
     */
    @Override
    public void onScanStopped() {

    }

    /**
     * Empty, but needed.
     */
    @Override
    public void onScanStarted() {

    }

    /**
     * If beacon is found we check if its in range of 5 meters and the majors are correct.
     * After that its checks for nearest valid beacon.
     *
     * @param beacon
     */
    @Override
    public synchronized void onBeaconFound(Beacon beacon) {

        if(beacon.getAccuracy()<5){
            if(beacon.getMajor()==majorToFind){

                if(nearestBeacon!=null){
                    if(beacon.getAccuracy()<nearestBeacon.getAccuracy()){
                        if(beacon.getMinor()!=previousMinor){
                            nearestBeacon=beacon;
                            teller++;
                            testTextView.setText(Integer.toString(teller));
                        }

                    }
                }else{
                    if(beacon.getMinor()!=previousMinor){
                        nearestBeacon=beacon;
                        teller++;
                        testTextView.setText(Integer.toString(teller));
                    }

                }
                if(teller==5){
                    stopScan();
                    beaconFound();
                }
            }
        }
    }
}
