package com.example.a11302481.rondleidingappteamkobalt.Controllers;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a11302481.rondleidingappteamkobalt.Models.Beacon;
import com.example.a11302481.rondleidingappteamkobalt.Models.RetrieveData;
import com.example.a11302481.rondleidingappteamkobalt.R;
import com.example.a11302481.rondleidingappteamkobalt.Scanner.BeaconScanner;
import com.example.a11302481.rondleidingappteamkobalt.Scanner.OnScanListener;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class BeaconFoundActivity extends YouTubeBaseActivity implements View.OnClickListener, OnScanListener {


    boolean shouldExecuteOnResume;
    private List dataToDisplay;
    private List typesOfDataToDisplay;
    private List titleOfData;
    private int currentIndex=0;
    private Button nextButton;
    private Button previousButton;
    private Button closeButton;
    private TextView titelTextView;
    YouTubePlayerView youTubePlayerView; //youtube instance declareren
    YouTubePlayer.OnInitializedListener onInitializedListener;
    private static final String KEY = "AIzaSyAMtPCSxzJk0i9ErDbZySSZW_gP7wscoc4";
    private static final String TAG="BeaconFoundActivity";
    private RetrieveData dataSource;
    private static boolean youtubeLastContent=false;
    private static YouTubePlayer youtubePlayer2;

    private BluetoothAdapter btAdapter;
    private BeaconScanner beaconScanner;
    private int major=0, minor=0;


    /**
     * Checks if permissions is given.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        shouldExecuteOnResume=false;
        super.onCreate(savedInstanceState);
        dataToDisplay= new ArrayList<>();
        typesOfDataToDisplay= new ArrayList<>();
        titleOfData=new ArrayList<>();
        dataSource=new RetrieveData();


        //ophalen van de meegstuurde major/minor uit de vorige activity
        Intent intent = getIntent();

        major=intent.getIntExtra("major",major);

        minor=intent.getIntExtra("minor",minor);
        //haalt content op en laat deze op het scherm zien
        try {
            getContent(minor);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        nextButton=(Button) findViewById(R.id.nextButton);
        previousButton=(Button) findViewById(R.id.previousButton);
        closeButton=(Button) findViewById(R.id.closeButton);


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

    /**
     * Shows correct page according to content.
     *
     * @param index
     */
    private void displayContent(final int index){
        //switch om verschillende soorten data te laten zien
        String type=(String)typesOfDataToDisplay.get(index);
        if(youtubeLastContent){
            youtubeLastContent=false;

            youtubePlayer2.pause();
        }
        switch(type){
            case "image":
                setContentView(R.layout.image_view);
                checkButtons();

                ImageView imageToDisplayImageView;
                imageToDisplayImageView=(ImageView) findViewById(R.id.imageToDisplayImageView);
                imageToDisplayImageView.setImageResource((Integer)dataToDisplay.get(index));
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
                WebView htmlToDisplayWebView=(WebView)findViewById(R.id.htmlToDisplayWebView);
                htmlToDisplayWebView.loadData((String)dataToDisplay.get(index), "text/html; charset=utf-8", "UTF-8");
                titelTextView=(TextView) findViewById(R.id.titelTextView);
                titelTextView.setText((String)titleOfData.get(index));
                break;
            case "youtube":
                setContentView(R.layout.youtube_view);
                checkButtons();

                titelTextView=(TextView) findViewById(R.id.titelTextView);
                titelTextView.setText((String)titleOfData.get(index));
                //wanneer youtube moet geladen worden
                youTubePlayerView = (YouTubePlayerView) findViewById(R.id.youtubeVideo);
                onInitializedListener = new YouTubePlayer.OnInitializedListener() {
                    @Override
                    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                        youTubePlayer.loadVideo((String)dataToDisplay.get(index));
                        youtubeLastContent=true;
                        youtubePlayer2=youTubePlayer;
                    }

                    @Override
                    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

                    }
                };
                youTubePlayerView.initialize(KEY, onInitializedListener);
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
        nextButton=(Button) findViewById(R.id.nextButton);
        previousButton=(Button) findViewById(R.id.previousButton);
        closeButton=(Button) findViewById(R.id.closeButton);
        previousButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);
        closeButton.setOnClickListener(this);
        previousButton.setVisibility(View.VISIBLE);
        nextButton.setVisibility(View.VISIBLE);
        closeButton.setVisibility(View.GONE);
        if(currentIndex==0){

            previousButton.setVisibility(View.GONE);
        }
        if((currentIndex+1)==dataToDisplay.size()){

            nextButton.setVisibility(View.GONE);
            closeButton.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Gets the content from retrievedata class.
     *
     * @param minor minor from beacon.
     * @throws JSONException if exceptions occurs.
     */
    private void getContent(int minor) throws JSONException {
        //toewijzen van data aan beacons dit zal vervangen worden daar een call naar de api voor data ipv de statische testdata
        List returnValue=dataSource.getDataPerBeacon(minor,major);

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
                closeFunction();
                break;
        }

        displayContent(currentIndex);
    }

//
//    @Override
//    public void onResume(){
//        super.onResume();
//    }

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
     *
     * Empty, but needed.
     *
     */
    @Override
    public void onScanStopped() {

    }

    /**
     *
     * Empty, but needed.
     *
     */
    @Override
    public void onScanStarted() {

    }

    /**
     * If you are to far away from an beacon.
     *
     * @param beacon
     */
    @Override
    public void onBeaconFound(Beacon beacon) {
        if((beacon.getMajor()==major)&&(beacon.getMinor()==minor)){
            if(beacon.getAccuracy()>7){
                Toast.makeText(getApplicationContext(), "u bent nu te ver van het informatiepunt, ga terug of zoek een nieuw informatiepunt.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}
