package com.example.a11302481.rondleidingappteamkobalt.Controllers;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a11302481.rondleidingappteamkobalt.Models.Beacon;
import com.example.a11302481.rondleidingappteamkobalt.Models.GetLink;
import com.example.a11302481.rondleidingappteamkobalt.Models.RetrieveData;
import com.example.a11302481.rondleidingappteamkobalt.Models.Route;
import com.example.a11302481.rondleidingappteamkobalt.R;
import com.example.a11302481.rondleidingappteamkobalt.Scanner.BeaconScanner;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class BeaconInRouteFoundActivity extends AppCompatActivity implements View.OnClickListener {

    private List dataToDisplay;
    private List typesOfDataToDisplay;
    private List titleOfData;
    private int currentIndex=0;
    private ImageButton nextButton;
    private ImageButton previousButton;
    private ImageButton closeButton;
    private TextView titelTextView;
    private static final String TAG="BeaconFoundActivity";
    private RetrieveData dataSource;
    private Route route;

    private BluetoothAdapter btAdapter;
    private BeaconScanner beaconScanner;
    private int major=0, minor=0;
    private boolean searching;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataToDisplay= new ArrayList<>();
        typesOfDataToDisplay= new ArrayList<>();
        titleOfData=new ArrayList<>();
        dataSource=new RetrieveData();


        //ophalen van de meegstuurde major/minor uit de vorige activity
        Intent intent = getIntent();

        route=intent.getExtras().getParcelable("route");

        major=intent.getIntExtra("major",major);

        currentIndex=intent.getIntExtra("currentIndex",currentIndex);

        minor=route.getBeaconMinor(route.getProgress()-1);

        //haalt content op en laat deze op het scherm zien
        try {
            getContent(minor);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        nextButton=(ImageButton) findViewById(R.id.nextButton);
        previousButton=(ImageButton) findViewById(R.id.previousButton);
        closeButton=(ImageButton) findViewById(R.id.closeButton);


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
        beaconScanner=new BeaconScanner(btAdapter,major);

        startScan();
    }



    /**
     * Shows correct page according to content.
     *
     * @param index
     */
    private void displayContent(final int index){
        //switch om verschillende soorten data te laten zien
        String type=(String)typesOfDataToDisplay.get(index);
        GetLink link = new GetLink();
        WebView htmlToDisplayWebView;
        switch(type){
            case "image":
                setContentView(R.layout.html_view);
                checkButtons();
                htmlToDisplayWebView=(WebView)findViewById(R.id.htmlToDisplayWebView);
                htmlToDisplayWebView.loadUrl(link.verkrijgImageLink()+(String)dataToDisplay.get(index));
                titelTextView=(TextView) findViewById(R.id.titelTextView);
                titelTextView.setText((String)titleOfData.get(index));
                break;
            case "text":
                setContentView(R.layout.text_view);
                checkButtons();
                TextView textToDisplayTextView;
                textToDisplayTextView=(TextView) findViewById(R.id.textToDisplayTextView);
                textToDisplayTextView.setText((String)dataToDisplay.get(index));
                titelTextView=(TextView) findViewById(R.id.titelTextView);
                titelTextView.setText((String)titleOfData.get(index));
                break;
            case "html":
                setContentView(R.layout.html_view);
                checkButtons();
                htmlToDisplayWebView=(WebView)findViewById(R.id.htmlToDisplayWebView);
                htmlToDisplayWebView.loadData((String)dataToDisplay.get(index), "text/html; charset=utf-8", "UTF-8");
                titelTextView=(TextView) findViewById(R.id.titelTextView);
                titelTextView.setText((String)titleOfData.get(index));
                break;
            case "youtube":
                Intent i =new Intent(this,YouTube_Activity.class);
                i.putExtra("currentIndex",currentIndex);
                i.putExtra("max",dataToDisplay.size());
                i.putExtra("major",major);
                i.putExtra("minor",minor);
                i.putExtra("route",route);
                i.putExtra("data",(String)dataToDisplay.get(index));
                startActivity(i);
                finish();
                break;
        }

    }

    /**
     *
     * Hides and shows button when needed on content pages.
     *
     */
    private void checkButtons(){
        //buttons enablen en disablen zodat de index niet out of bounds kan gaan
        nextButton=(ImageButton) findViewById(R.id.nextButton);
        previousButton=(ImageButton) findViewById(R.id.previousButton);
        closeButton=(ImageButton) findViewById(R.id.closeButton);
        previousButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);
        closeButton.setOnClickListener(this);
        previousButton.setClickable(true);
        previousButton.setAlpha(1f);
        nextButton.setVisibility(View.VISIBLE);
        closeButton.setVisibility(View.GONE);


        if(currentIndex==0){

            previousButton.setAlpha(.5f);
            previousButton.setClickable(false);
        }
        if((currentIndex+1)==dataToDisplay.size()){

            nextButton.setVisibility(View.GONE);
            closeButton.setVisibility(View.VISIBLE);
        }
    }

    private void getContent(int minor) throws JSONException {
        //toewijzen van data aan beacons dit zal vervangen worden daar een call naar de api voor data ipv de statische testdata
        List returnValue=dataSource.getDataPerBeaconInRoute(minor,major,route.getId(),route.getProgress());

        dataToDisplay=(List)returnValue.get(0);
        typesOfDataToDisplay=(List)returnValue.get(1);
        titleOfData=(List)returnValue.get(2);
        displayContent(currentIndex);
    }

    /**
     *
     * Closes the activity and goes to previous screen.
     *
     */
    public void closeFunction(){
        Intent intent = new Intent(this, RouteSearchBeaconActivity.class);
        intent.putExtra("major", major);
        intent.putExtra("previousMinor",minor);
        intent.putExtra("route",route);
        intent.putExtra("algeweest",1);
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

    /**
     * If next button is pressed go to the next content page.
     * If previous button is pressed go to the previous content page.
     * If close button is pressed call closeFunction.
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        //navigatie via de buttons op het scherm
        switch (v.getId()) {
            case R.id.nextButton:
                currentIndex++;
                break;
            case R.id.previousButton:
                currentIndex--;
                break;
            case R.id.closeButton:
                route.setProgress(route.getProgress()+1);
                closeFunction();
                break;
        }

        displayContent(currentIndex);
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
}
