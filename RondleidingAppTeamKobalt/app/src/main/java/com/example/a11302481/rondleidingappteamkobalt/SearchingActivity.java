package com.example.a11302481.rondleidingappteamkobalt;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewAnimator;


public class SearchingActivity extends AppCompatActivity {

    private int majorToFind;
    private static final int MY_PERMISSION_RESPONSE = 42;

    private BluetoothManager btManager;
    public BluetoothAdapter btAdapter;
    private Handler scanHandler = new Handler();
    private int scan_interval_ms = 1000;
    private boolean isScanning = false;


    private int nearMinor=0, nearMajor=0, teller=0, previousMinor=-1;
    private double nearAccuracy=100;

    private TextView testTextView;
    //private ImageSwitcher gifDisplay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searching);

        //ophalen van de meegstuurde major uit de vorige activity
        Intent intent = getIntent();
        majorToFind = intent.getIntExtra("major",majorToFind);

        testTextView=(TextView) findViewById(R.id.testTextview);


        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }
        // Prompt for permissions
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSION_RESPONSE);
        }


        //starten van de scanner
        btManager = (BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);
        btAdapter = btManager.getAdapter();

        btAdapter.enable();
        scanHandler.post(scanRunnable);

        startTime = System.currentTimeMillis();
        timerHandler.postDelayed(timerRunnable, 0);


        //Bitmap logo = BitmapFactory.decodeResource(this.getResources(),
        //        R.drawable.waiting);

        //gifDisplay=(ImageSwitcher) findViewById(R.id.gifDisplay);
        //gifDisplay.setImageResource(R.drawable.waiting);
    }

    @Override
    public void onResume()
    {

        super.onResume();
        reset();
    }

    private void reset(){
        //resetten van de scanner
        startTime=System.currentTimeMillis();
        previousMinor=nearMinor;
        timerHandler.postDelayed(timerRunnable, 0);
        teller=0;
        nearAccuracy=100;
    }



    private BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback()
    {
        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord)
        {
            int startByte = 2;
            boolean patternFound = false;
            while (startByte <= 5)
            {
                if (    ((int) scanRecord[startByte + 2] & 0xff) == 0x02 && //Identifies an iBeacon
                        ((int) scanRecord[startByte + 3] & 0xff) == 0x15)
                { //Identifies correct data length
                    patternFound = true;
                    break;
                }
                startByte++;
            }

            if (patternFound)
            {

                //Convert to hex String
                byte[] uuidBytes = new byte[16];
                System.arraycopy(scanRecord, startByte + 4, uuidBytes, 0, 16);
                String hexString = bytesToHex(uuidBytes);

                //UUID detection
                String uuid =  hexString.substring(0,8) + "-" +
                        hexString.substring(8,12) + "-" +
                        hexString.substring(12,16) + "-" +
                        hexString.substring(16,20) + "-" +
                        hexString.substring(20,32);

                    // major
                    final int major = (scanRecord[startByte + 20] & 0xff) * 0x100 + (scanRecord[startByte + 21] & 0xff);
                    // minor
                    final int minor = (scanRecord[startByte + 22] & 0xff) * 0x100 + (scanRecord[startByte + 23] & 0xff);
                if((majorToFind==major)&&(minor!=previousMinor)) {

                    int TXPOWER = scanRecord[startByte + 24];

                    final double accuracy = calculateDistance(TXPOWER, rssi);

                    //nakijken of gevonden beacon dichterbij ligt
                    if(accuracy<nearAccuracy){
                        nearMajor=major;
                        nearMinor=minor;
                        nearAccuracy=accuracy;
                    }
                    teller++;
                    testTextView.setText(Integer.toString(teller));
                    //als er 5 beacons gevonden zijn wordt de beaconFound functie uitgevoerd
                    if (teller==5){
                        beaconFound(nearMajor, nearMinor, nearAccuracy);
                    }



                }
            }

        }
    };

    long startTime = 0;

    //runs without a timer by reposting this handler at the end of the runnable
    //als de timer afgelopen is en er is een beacon gevonden wordt de beaconFound functie uitgevoerd
    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            long millis = System.currentTimeMillis() - startTime;
            int seconds = (int) (millis / 1000);
            if((seconds>5)&&(teller>0)&&(teller<5)){
                teller=6;
                timerHandler.removeCallbacks(timerRunnable);
                beaconFound(nearMajor, nearMinor, nearAccuracy);
            }
            if (seconds>10){
                previousMinor=-1;
            }

            timerHandler.postDelayed(this, 500);
        }
    };

    private void beaconFound(final int major, final int minor, double distance){

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        displayContent(major, minor);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        reset();
                        break;
                }
            }
        };
        RetrieveData dataSource=new RetrieveData();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Informatiepunt:"+dataSource.getBeaconName(minor,major)+ " gevonden op een afstand van "+String.format( "%.2f", distance )+"m. wil u de informatie van dit punt zien?").setPositiveButton("Ja", dialogClickListener)
                .setNegativeButton("Nee", dialogClickListener).show();

    }

    public void displayContent(int major, int minor){
        //deze functie start de BeaconFoundActivity op en geeft de info ban het dichtsbijzijnde beacon weer

        Intent i = new Intent(this, BeaconFoundActivity.class);

        i.putExtra("major", major);
        i.putExtra("minor", minor);
        startActivity(i);
    }

    static final char[] hexArray = "0123456789ABCDEF".toCharArray();
    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public double calculateDistance(int txPower, double rssi) {
        //berekend de accuracy
        if (rssi == 0) {
            return -1.0; // if we cannot determine accuracy, return -1.
        }
        double ratio = rssi*1.0/txPower;
        if (ratio < 1.0) {
            return Math.pow(ratio,10);
        }
        else {
            double accuracy =  (0.89976)*Math.pow(ratio,7.7095) + 0.111;
            return accuracy;
        }
    }

    private Runnable scanRunnable = new Runnable()
    {
        @Override
        public void run() {
                //zet de scanner aan en uit a.d.h.v scan_interval_ms
                if (isScanning)
                {
                    if (btAdapter != null)
                    {
                        btAdapter.stopLeScan(leScanCallback);
                    }
                }
                else
                {
                    if (btAdapter != null)
                    {
                        btAdapter.startLeScan(leScanCallback);
                    }
                }



            isScanning = !isScanning;

            scanHandler.postDelayed(this, scan_interval_ms);
        }
    };
}
