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
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a11302481.rondleidingappteamkobalt.Models.RetrieveData;
import com.example.a11302481.rondleidingappteamkobalt.Models.Beacon;
import com.example.a11302481.rondleidingappteamkobalt.Scanner.BeaconScanner;
import com.example.a11302481.rondleidingappteamkobalt.R;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;


public class SearchingActivity extends AppCompatActivity{

    private int majorToFind, previousMinor=-1;

    private static List previousMinors=new ArrayList<>();


    private TextView testTextView;

    private BeaconScanner beaconScanner;
    private Beacon nearestBeacon;
    private boolean searching=true;

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
        previousMinor=intent.getIntExtra("previousMinor",previousMinor);
        List previousMinorItem=new ArrayList();
        previousMinorItem.add(0,previousMinor);
        previousMinorItem.add(1,0);
        previousMinors.add(previousMinorItem);
        testTextView=(TextView) findViewById(R.id.testTextview);

        startTime = System.currentTimeMillis();
        timerHandler.postDelayed(timerRunnable, 0);


        // create BT intent
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        // starts the activity depending on the result
        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);

        BluetoothAdapter btAdapter = ((BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();
        btAdapter.enable();
        beaconScanner=new BeaconScanner(btAdapter,majorToFind,5);

    }


    long startTime = 0;

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
            //timerHandler.removeCallbacksAndMessages(null);
            if(searching) {
                List beaconLijst = beaconScanner.getFoundBeacons();
                if (!beaconLijst.isEmpty()) {
                    if (beaconLijst.get(0) instanceof Integer) {

                    } else {

                        for (Object o : beaconLijst) {
                            Beacon foundBeacon = (Beacon) o;
                            if (nearestBeacon == null) {
                                nearestBeacon = foundBeacon;
                            } else {
                                if (previousMinors.size() >= 1) {
                                    for (Object O : previousMinors) {
                                        List<Integer> i = ((List<Integer>) O);
                                        if (foundBeacon.getMinor() != i.get(0)) {
                                            if (nearestBeacon.getAccuracy() > foundBeacon.getAccuracy()) {
                                                nearestBeacon = foundBeacon;
                                                searching = false;
                                                List previousMinor = new ArrayList();
                                                previousMinor.add(0, nearestBeacon.getMinor());
                                                previousMinor.add(1, 0);
                                                previousMinors.add(previousMinor);
                                                beaconFound();
                                                break;
                                            }
                                        }
                                    }
                                } else {
                                    nearestBeacon = foundBeacon;
                                    searching = false;
                                    List previousMinor = new ArrayList();
                                    previousMinor.add(0, nearestBeacon.getMinor());
                                    previousMinor.add(1, 0);
                                    previousMinors.add(previousMinor);
                                    beaconFound();
                                }


                            }
                        }

                    }
                }
                //timerHandler.postDelayed(timerRunnable, 0);


                long millis = System.currentTimeMillis() - startTime;
                seconds = (int) (millis / 1000);

                int teller = 0;
                List<Object> indexesToDelete = new ArrayList<>();
                String test = "";
                if (previousSeconds != seconds) {
                    previousSeconds = seconds;
                    if (previousMinors.size() > 0) {
                        for (Object O : previousMinors) {
                            List<Integer> i = ((List<Integer>) O);
                            if (i.get(1) >= 10) {
                                indexesToDelete.add(O);
                            } else {
                                List previousMinor = new ArrayList();
                                previousMinor.add(0, i.get(0));
                                previousMinor.add(1, i.get(1) + 1);
                                previousMinors.set(teller, previousMinor);
                            }
                            test += i.get(0) + " ,";
                            teller++;
                        }
                    }

                    if (indexesToDelete.size() > 0) {
                        int size = indexesToDelete.size();
                        for (int i = size; i > 0; i--) {
                            previousMinors.remove(indexesToDelete.get(i - 1));
                        }
                    }
                    testTextView.setText(test);


                }
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
        try {
            searching = false;
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            // Vibrate for 500 milliseconds
            v.vibrate(500);
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            displayContent();
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            List previousMinorItem = new ArrayList();
                            previousMinorItem.add(0, nearestBeacon.getMinor());
                            previousMinorItem.add(1, 0);
                            previousMinors.add(previousMinorItem);
                            reset();
                            break;
                    }
                }
            };
            RetrieveData dataSource = new RetrieveData();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            List beaconInfo = dataSource.getBeaconName(nearestBeacon.getMinor(), nearestBeacon.getMajor());
            builder.setMessage("U bevind zich in " + beaconInfo.get(1) + " bij informatiepunt " + beaconInfo.get(0) + ". wil u de informatie van dit punt zien?").setPositiveButton("Ja", dialogClickListener)
                    .setNegativeButton("Nee", dialogClickListener).show();
        }catch(JSONException j){
            j.printStackTrace();
        }

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
        finish();
    }

    /**
     *
     * If application goes to background the scanner stops with scanning.
     *
     */
    @Override
    public void onPause(){

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
        startTime = System.currentTimeMillis();

    }


    /**
     *
     * When the user pressed the "nee" button, reset.
     *
     */
    private void reset(){
        startTime=System.currentTimeMillis();
        searching=true;
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

    public void closeFunction(){
        previousMinors.clear();
        timerHandler.removeCallbacksAndMessages(null);
        stopScan();
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
