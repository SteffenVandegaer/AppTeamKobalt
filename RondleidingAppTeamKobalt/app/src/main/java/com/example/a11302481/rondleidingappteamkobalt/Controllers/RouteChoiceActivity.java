package com.example.a11302481.rondleidingappteamkobalt.Controllers;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.a11302481.rondleidingappteamkobalt.Models.Beacon;
import com.example.a11302481.rondleidingappteamkobalt.Models.RetrieveData;
import com.example.a11302481.rondleidingappteamkobalt.Models.Route;
import com.example.a11302481.rondleidingappteamkobalt.R;
import com.example.a11302481.rondleidingappteamkobalt.Scanner.BeaconScanner;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RouteChoiceActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private BeaconScanner beaconScanner;
    private RetrieveData dataSource;
    private final static String TAG = MainActivity.class.getSimpleName();
    private BluetoothAdapter btAdapter;
    private boolean searching;
    private Beacon nearestBeacon;
    private long tStart;
    private List routes;
    private ListView itemsListView;
    private int major=-1;
    private List previousMinors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.route_searching);
        previousMinors=new ArrayList();
        routes=new ArrayList<>();
        dataSource=new RetrieveData();
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

        Intent intent = getIntent();
        major = intent.getIntExtra("major",major);

        btAdapter = ((BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();
        beaconScanner=new BeaconScanner(btAdapter,major);
        tStart = System.currentTimeMillis();
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
        boolean change=false;
        boolean newbeacon=true;
        if(searching){
            List beaconLijst=beaconScanner.getFoundBeacons();
            if(!beaconLijst.isEmpty()){
                if(beaconLijst.get(0) instanceof Integer){

                }else {
                    for (Object beacon : beaconLijst) {
                        nearestBeacon=(Beacon)beacon;
                        try {
                            for (Object i : previousMinors) {
                                if (((int) i) == nearestBeacon.getMinor()) {
                                    newbeacon = false;
                                }
                            }
                            if (newbeacon) {
                                previousMinors.add(nearestBeacon.getMinor());
                                List newRoutes = dataSource.getRoutesWithBeacon(nearestBeacon.getMajor(), nearestBeacon.getMinor());
                                for (Object b : newRoutes) {
                                    boolean check = true;
                                    for (Object o : routes) {
                                        if (((Route) o).getId() == ((Route) b).getId()) {
                                            if(((Route)o).getProgress()==((Route) b).getProgress()){
                                                check = false;
                                            }else{
                                                check=true;
                                            }

                                        }
                                    }
                                    if (check) {
                                        if (((Route) b).getId() != 0) {
                                            routes.add(b);
                                            change = true;
                                        }
                                    }
                                }
                            }
                        } catch(JSONException e){
                            e.printStackTrace();
                        }
                        if (change) {
                            displayRoutes();
                        }
                    }
                }
            }
        }
        timerHandler.postDelayed(this, 500);
        }
    };

    private void displayRoutes(){
        setContentView(R.layout.route_choice);
        int resource = R.layout.listview_item;
        ArrayList<HashMap<String, String>> data =
                new ArrayList<HashMap<String, String>>();
        for (Object item : routes) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("title", ((Route)item).getName());
            map.put("description", ((Route)item).getDescription());
            if(((Route)item).getProgress()!=0){
                RetrieveData source=new RetrieveData();
                try {
                    List beaconData=source.getBeaconName(((Route)item).getBeaconMinor(((Route)item).getProgress()),major);
                    map.put("progress", ("Inpikken in route op "+beaconData.get(1)+" bij informatiepunt "+beaconData.get(0)));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else{
                map.put("progress","");
            }

            data.add(map);
        }
        String[] from = {"title", "description", "progress"};

        int[] to = {R.id.routeNaamTextView, R.id.routeBeschrijvingTextView, R.id.positieRouteTextView};

        SimpleAdapter adapter =
                new SimpleAdapter(this, data, resource, from, to);
        itemsListView=(ListView) findViewById(R.id.itemsListView);
        itemsListView.setOnItemClickListener(this);
        itemsListView.setAdapter(adapter);
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

    public void closeFunction(){
        timerHandler.removeCallbacksAndMessages(null);
        stopScan();
        Intent intent = new Intent(this, Route_Roaming_Activity.class);
        intent.putExtra("major", major);
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        timerHandler.removeCallbacksAndMessages(null);
        Intent i =new Intent(this,RouteSearchBeaconActivity.class);
        i.putExtra("major",major);
        i.putExtra("route", (Parcelable)routes.get(position));
        startActivity(i);
        finish();
    }
}