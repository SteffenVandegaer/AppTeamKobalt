package com.example.a11302481.rondleidingappteamkobalt.Models;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.ArrayList;
/**
 * Created by 11302481 on 10/10/2017.
 *
 */

public class RetrieveData {
    public RetrieveData(){

    }

    public String[] getAllCampi(){
        String[] campusArray=new String[] {
                "Clenardus","Comenius","Diepenbeek", "Gasthuisberg", "Hertogstraat", "LiZa", "Oude Luikerbaan", "Proximus", "Sociale School"
        };
        return campusArray;
    }

    public int getCampusId(String campusName){
        int major=0;
        //major per campus toewijzen aan de hand van de gemaakte keuze in spinnen (zal later met data uit de database vervangen worden)
        switch(campusName){
            case "Clenardus":
                major=3;
                break;
            case "Comenius":
                major=2;
                break;
            case "Diepenbeek":
                major=1;
                break;
            case "Gasthuisberg":
                major=4;
                break;
            case "Hertogstraat":
                major=5;
                break;
            case "LiZa":
                major=6;
                break;
            case "Oude Luikerbaan":
                major=7;
                break;
            case "Proximus":
                major=8;
                break;
            case "Sociale School":
                major=9;
                break;
        }
        return major;
    }

    public String getCampusName(int major){
        String campusName="";
        //major per campus toewijzen aan de hand van de gemaakte keuze in spinner (zal later met data uit de database vervangen worden)
        switch(major){
            case 3:
                campusName="Clenardus";
                break;
            case 2:
                campusName="Comenius";
                break;
            case 1:
                campusName="Diepenbeek";
                break;
            case 4:
                campusName="Gasthuisberg";
                break;
            case 5:
                campusName="Hertogstraat";
                break;
            case 6:
                campusName="LiZa";
                break;
            case 7:
                campusName="Oude Luikerbaan";
                break;
            case 8:
                campusName="Proximus";
                break;
            case 9:
                campusName="Sociale School";
                break;
        }
        return campusName;
    }

    public List getDataPerBeacon(int minor) throws JSONException {
        List returnList;
        List dataToDisplay;
        List typesOfDataToDisplay;
        List titleOfData;
        dataToDisplay= new ArrayList<>();
        typesOfDataToDisplay= new ArrayList<>();
        returnList=new ArrayList<>();
        titleOfData=new ArrayList<>();

        int image;
        String text;
        String html;
        String youtube;
        switch(minor){
            case 11559:

                //class instantie
                staticData staticData = new staticData();

                //minor doorgeven zodat men specifieke data kan verkrijgen van een beacon.
                staticData.setMinor(minor);

                //uitvoeren van de klasse en data verkrijgen in de klasse zelf.
                staticData.execute();

                //data verkrijgen.
                JSONArray jA = staticData.getData();
                while(jA==null){
                    jA = staticData.getData();
                }

                //de volledige data over gaan
                for(int i = 0; i < jA.length(); i++) {

                    //per object de gegevens door geven.
                    JSONObject jO = (JSONObject) jA.get(i);

                    //type aanvragen
                    String type = (String) jO.get("metatype_sn");

                    //kijken welk type en doorgeven.
                    switch (type) {
                        case "text":

                            text = (String) jO.get("content_txt");
                            dataToDisplay.add(i, text);
                            typesOfDataToDisplay.add(i, "text");
                            titleOfData.add(i, "test titel met tekst");

                            break;

                        case "youtube":

                            youtube = (String) jO.get("content_txt");
                            dataToDisplay.add(i,youtube);
                            typesOfDataToDisplay.add(i, "youtube");
                            titleOfData.add(i, "Een video");

                            break;

                        case "html":

                            html = (String) jO.get("content_txt");
                            dataToDisplay.add(i, html);
                            typesOfDataToDisplay.add(i, "html");
                            titleOfData.add(i, "test met html (titel)");

                            break;

                        //case "image":

                        //image = getResources().getIdentifier("next", "drawable",  getPackageName());
                        //dataToDisplay.add(0,image);
                        //typesOfDataToDisplay.add(0,"image");
                        //image = getResources().getIdentifier("previous", "drawable",  getPackageName());
                        //dataToDisplay.add(1,image);
                        //typesOfDataToDisplay.add(1,"image");

                        //break;

                    }
                }


                //staticData staticData = new staticData();
                //De bedoeling is om de minor door te geven naar de klasse staticdata zodat er data opgevraagd kan worden van die beacon.
                //de data die dan opgevraagd is moet terug worden doorgegeven naar deze klasse.
                //De verschillende soorten types moeten wel nog bekeken worden ook moet er gekeken worden hoe we de gegevens dooorsturen vanaf de klasse staticData.

                //image = getResources().getIdentifier("next", "drawable",  getPackageName());
                //dataToDisplay.add(0,image);
                //typesOfDataToDisplay.add(0,"image");
                //image = getResources().getIdentifier("previous", "drawable",  getPackageName());
                //dataToDisplay.add(1,image);
                //typesOfDataToDisplay.add(1,"image");
//                text="u bent in de buurt van een estimote beacon";
//                dataToDisplay.add(0,text);
//                typesOfDataToDisplay.add(0,"text");
//                titleOfData.add(0,"test titel met tekst");
//                html="<html><head><title>htmltest</title></head><body><h1>html test</h1><br/><p>dit is de html test bij het estimote beacon</p></body></html>";
//                dataToDisplay.add(1,html);
//                typesOfDataToDisplay.add(1,"html");
//                titleOfData.add(1,"test met html (titel)");
//
//                dataToDisplay.add(2,"mTo8GiPQdPs");
//                typesOfDataToDisplay.add(2,"youtube");
//                titleOfData.add(2,"Werner de walvis");
                break;
            case 9:
                text="u bent in de buurt van beacon 9";
                dataToDisplay.add(0,text);
                typesOfDataToDisplay.add(0,"test titel met tekst");
                break;
        }
        //lijst toevoegen.
        returnList.add(0,dataToDisplay);
        returnList.add(1,typesOfDataToDisplay);
        returnList.add(2,titleOfData);

        //doorgeven van de lijst.
        return returnList;
    }

    public String getBeaconName(int minor, int major){
        String Name="";
        switch(minor){
            case 11559:
                Name="Estimote test informatiepunt";
                break;
            case 9:
                Name="Galaxy S5 test informatiepunt";
                break;
        }
        return Name;
    }
}
