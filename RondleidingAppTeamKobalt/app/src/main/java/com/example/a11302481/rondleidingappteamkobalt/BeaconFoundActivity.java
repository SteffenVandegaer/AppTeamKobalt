package com.example.a11302481.rondleidingappteamkobalt;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import java.util.ArrayList;
import java.util.List;

public class BeaconFoundActivity extends AppCompatActivity implements View.OnClickListener {

    private List dataToDisplay;
    private List typesOfDataToDisplay;
    private int currentIndex=0;
    private Button nextButton;
    private Button previousButton;
    private static final String TAG="BeaconFoundActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataToDisplay= new ArrayList<>();
        typesOfDataToDisplay= new ArrayList<>();

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


    }

    private void displayContent(int index){
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
                VideoView videoToDisplayVideoView=(VideoView)findViewById(R.id.videoToDisplayVideoView);
                MediaController mediaController= new MediaController(this);
                mediaController.setAnchorView(videoToDisplayVideoView);
                Uri uri=Uri.parse("https://www.youtube.com/watch?v=sOCQEH5_N9Y");
                videoToDisplayVideoView.setMediaController(mediaController);
                videoToDisplayVideoView.setVideoURI(uri);
                break;
        }

    }

    private void checkButtons(){
        //buttons enablen en disablen zodat de index niet out of bounds kan gaan
        nextButton=(Button) findViewById(R.id.nextButton);
        previousButton=(Button) findViewById(R.id.previousButton);
        previousButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);
        previousButton.setEnabled(true);
        nextButton.setEnabled(true);
        if(currentIndex==0){

            previousButton.setEnabled(false);
        }
        if((currentIndex+1)==dataToDisplay.size()){

            nextButton.setEnabled(false);

        }
    }

    private void getContent(int minor){
        //toewijzen van data aan beacons dit zal vervangen worden daar een call naar de api voor data ipv de statische testdata
        int image;
        String text;
        String html;
        switch(minor){
            case 11559:
                image = getResources().getIdentifier("next", "drawable",  getPackageName());
                dataToDisplay.add(0,image);
                typesOfDataToDisplay.add(0,"image");
                image = getResources().getIdentifier("previous", "drawable",  getPackageName());
                dataToDisplay.add(1,image);
                typesOfDataToDisplay.add(1,"image");
                text="u bent in de buurt van een estimote beacon";
                dataToDisplay.add(2,text);
                typesOfDataToDisplay.add(2,"text");
                html="<html><head><title>htmltest</title></head><body><h1>html test</h1><br/><p>dit is de html test bij het estimote beacon</p></body></html>";
                dataToDisplay.add(3,html);
                typesOfDataToDisplay.add(3,"html");
                html="<html><head><title>htmltest</title></head><body><h1>html test</h1><br/><p>dit is de html test bij het estimote beacon</p></body></html>";
                dataToDisplay.add(4,html);
                typesOfDataToDisplay.add(4,"youtube");
                break;
            case 9:
                text="u bent in de buurt van beacon 9";
                dataToDisplay.add(0,text);
                typesOfDataToDisplay.add(0,"text");
                break;
        }
        displayContent(currentIndex);
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
        }
        displayContent(currentIndex);
    }
}
