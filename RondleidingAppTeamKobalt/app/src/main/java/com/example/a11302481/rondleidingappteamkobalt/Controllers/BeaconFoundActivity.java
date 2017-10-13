package com.example.a11302481.rondleidingappteamkobalt.Controllers;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.a11302481.rondleidingappteamkobalt.Models.RetrieveData;
import com.example.a11302481.rondleidingappteamkobalt.R;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import java.util.ArrayList;
import java.util.List;

public class BeaconFoundActivity extends YouTubeBaseActivity implements View.OnClickListener {

    private List dataToDisplay;
    private List typesOfDataToDisplay;
    private int currentIndex=0;
    private Button nextButton;
    private Button previousButton;
    private Button closeButton;
    YouTubePlayerView youTubePlayerView; //youtube instance declareren
    YouTubePlayer.OnInitializedListener onInitializedListener;
    private static final String KEY = "AIzaSyAMtPCSxzJk0i9ErDbZySSZW_gP7wscoc4";
    private static final String TAG="BeaconFoundActivity";
    private RetrieveData dataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataToDisplay= new ArrayList<>();
        typesOfDataToDisplay= new ArrayList<>();
        dataSource=new RetrieveData();

        //ophalen van de meegstuurde major/minor uit de vorige activity
        Intent intent = getIntent();
        int major=0;
        major=intent.getIntExtra("major",major);
        int minor=0;
        minor=intent.getIntExtra("minor",minor);
        //haalt content op en laat deze op het scherm zien
        getContent(minor);
        nextButton=(Button) findViewById(R.id.nextButton);
        previousButton=(Button) findViewById(R.id.previousButton);
        closeButton=(Button) findViewById(R.id.closeButton);

        //youtube config



    }

    private void displayContent(final int index){
        //switch om verschillende soorten data te laten zien
        String type=(String)typesOfDataToDisplay.get(index);
        switch(type){
            case "image":
                setContentView(R.layout.image_view);
                checkButtons();

                ImageView imageToDisplayImageView;
                imageToDisplayImageView=(ImageView) findViewById(R.id.imageToDisplayImageView);
                imageToDisplayImageView.setImageResource((Integer)dataToDisplay.get(index));
                break;
            case "text":
                setContentView(R.layout.text_view);
                checkButtons();
                TextView textToDisplayTextView;
                textToDisplayTextView=(TextView) findViewById(R.id.textToDisplayTextView);
                textToDisplayTextView.setText((String)dataToDisplay.get(index));
                break;
            case "html":
                setContentView(R.layout.html_view);
                checkButtons();
                WebView htmlToDisplayWebView=(WebView)findViewById(R.id.htmlToDisplayWebView);
                htmlToDisplayWebView.loadData((String)dataToDisplay.get(index), "text/html; charset=utf-8", "UTF-8");
                break;
            case "youtube":
                setContentView(R.layout.youtube_view);
                checkButtons();
                //wanneer youtube moet geladen worden
                youTubePlayerView = (YouTubePlayerView) findViewById(R.id.youtubeVideo);
                onInitializedListener = new YouTubePlayer.OnInitializedListener() {
                    @Override
                    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                        youTubePlayer.loadVideo((String)dataToDisplay.get(index));
                    }

                    @Override
                    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

                    }
                };
                youTubePlayerView.initialize(KEY, onInitializedListener);
                break;
        }

    }

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

    private void getContent(int minor){
        //toewijzen van data aan beacons dit zal vervangen worden daar een call naar de api voor data ipv de statische testdata
        List returnValue=dataSource.getDataPerBeacon(minor);

        dataToDisplay=(List)returnValue.get(0);
        typesOfDataToDisplay=(List)returnValue.get(1);
        displayContent(currentIndex);
    }

    public void closeFunction(){
        finish();
    }

    public void onBackPressed(){
        closeFunction();
    }

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
}
