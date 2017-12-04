package com.example.a11302481.rondleidingappteamkobalt.Controllers;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a11302481.rondleidingappteamkobalt.Models.Route;
import com.example.a11302481.rondleidingappteamkobalt.R;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

/**
 * Created by 11302481 on 25/10/2017.
 */

public class YouTube_Activity extends YouTubeBaseActivity implements View.OnClickListener {

    private static YouTubePlayerView youTubePlayerView; //youtube instance declareren
    private static YouTubePlayer.OnInitializedListener onInitializedListener;
    private static String KEY = "AIzaSyAMtPCSxzJk0i9ErDbZySSZW_gP7wscoc4";
    private TextView titelTextView;
    private ImageButton nextButton;
    private ImageButton previousButton;
    private ImageButton closeButton;
    private Route route;
    private int currentIndex,maxIndex,major,minor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.youtube_view);

        Intent intent = getIntent();
        String titleOfData= intent.getStringExtra("titel");
        final String dataToDisplay=intent.getStringExtra("data");
        currentIndex=intent.getIntExtra("currentIndex",0);
        maxIndex=intent.getIntExtra("max",1);
        major=intent.getIntExtra("major",0);
        minor=intent.getIntExtra("minor",0);
        route=intent.getExtras().getParcelable("route");
        titelTextView=(TextView) findViewById(R.id.titelTextView);
        titelTextView.setText((String)titleOfData);
        //wanneer youtube moet geladen worden
        onInitializedListener=null;
        youTubePlayerView = (YouTubePlayerView) findViewById(R.id.youtubeVideo);
        onInitializedListener = new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                youTubePlayer.loadVideo(dataToDisplay);
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                Toast.makeText(getApplicationContext(), "youTube Error", Toast.LENGTH_SHORT).show();
            }
        };

        youTubePlayerView.initialize(KEY, onInitializedListener);
        youTubePlayerView.removeView(youTubePlayerView);
        checkButtons();
    }

    private void checkButtons(){
        //buttons enablen en disablen zodat de index niet out of bounds kan gaan
        nextButton=(ImageButton) findViewById(R.id.nextButton);
        previousButton=(ImageButton) findViewById(R.id.previousButton);
        closeButton=(ImageButton) findViewById(R.id.closeButton);
        previousButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);
        closeButton.setOnClickListener(this);
        previousButton.setVisibility(View.VISIBLE);
        nextButton.setVisibility(View.VISIBLE);
        closeButton.setVisibility(View.GONE);


        if(currentIndex==0){

            previousButton.setVisibility(View.GONE);
        }
        if((currentIndex+1)==maxIndex){

            nextButton.setVisibility(View.GONE);
            closeButton.setVisibility(View.VISIBLE);
        }
    }

    public void onClick(View v) {
            //navigatie via de buttons op het scherm
            switch (v.getId()) {
                case R.id.nextButton:
                    currentIndex++;
                    newScreen();
                    break;
                case R.id.previousButton:
                    currentIndex--;
                    newScreen();
                    break;
                case R.id.closeButton:
                    finish();
                    break;
            }
    }

    public void newScreen(){
        Intent intent =new Intent(this, BeaconFoundActivity.class);
        intent.putExtra("major",major);
        intent.putExtra("minor",minor);
        intent.putExtra("route",route);
        intent.putExtra("currentIndex",currentIndex);
        startActivity(intent);
        finish();
    }
}
