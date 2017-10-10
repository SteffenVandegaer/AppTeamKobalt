package com.example.a11302481.rondleidingappteamkobalt;

import java.util.List;
import java.util.ArrayList;
import android.view.ContextThemeWrapper;
import java.util.List;
import android.content.Context;

/**
 * Created by 11302481 on 10/10/2017.
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
                major=1;
                break;
            case "Comenius":
                major=2;
                break;
            case "Diepenbeek":
                major=3;
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
            case 1:
                campusName="Clenardus";
                break;
            case 2:
                campusName="Comenius";
                break;
            case 3:
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

    public List getDataPerBeacon(int minor){
        List returnList;
        List dataToDisplay;
        List typesOfDataToDisplay;
        dataToDisplay= new ArrayList<>();
        typesOfDataToDisplay= new ArrayList<>();
        returnList=new ArrayList<>();

        int image;
        String text;
        String html;
        switch(minor){
            case 11559:
                //image = getResources().getIdentifier("next", "drawable",  getPackageName());
                //dataToDisplay.add(0,image);
                //typesOfDataToDisplay.add(0,"image");
                //image = getResources().getIdentifier("previous", "drawable",  getPackageName());
                //dataToDisplay.add(1,image);
                //typesOfDataToDisplay.add(1,"image");
                text="u bent in de buurt van een estimote beacon";
                dataToDisplay.add(0,text);
                typesOfDataToDisplay.add(0,"text");
                html="<html><head><title>htmltest</title></head><body><h1>html test</h1><br/><p>dit is de html test bij het estimote beacon</p></body></html>";
                dataToDisplay.add(1,html);
                typesOfDataToDisplay.add(1,"html");

                dataToDisplay.add(2,"mTo8GiPQdPs");
                typesOfDataToDisplay.add(2,"youtube");
                break;
            case 9:
                text="u bent in de buurt van beacon 9";
                dataToDisplay.add(0,text);
                typesOfDataToDisplay.add(0,"text");
                break;
        }
        returnList.add(0,dataToDisplay);
        returnList.add(1,typesOfDataToDisplay);

        return returnList;
    }

}
