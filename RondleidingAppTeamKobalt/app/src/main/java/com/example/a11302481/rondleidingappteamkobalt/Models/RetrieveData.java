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
                major=3000;
                break;
            case "Comenius":
                major=2000;
                break;
            case "Diepenbeek":
                major=1000;
                break;
            case "Gasthuisberg":
                major=4000;
                break;
            case "Hertogstraat":
                major=5000;
                break;
            case "LiZa":
                major=6000;
                break;
            case "Oude Luikerbaan":
                major=7000;
                break;
            case "Proximus":
                major=8000;
                break;
            case "Sociale School":
                major=9000;
                break;
        }
        return major;
    }

    public String getCampusName(int major){
        String campusName="";
        //major per campus toewijzen aan de hand van de gemaakte keuze in spinner (zal later met data uit de database vervangen worden)
        switch(major){
            case 3000:
                campusName="Clenardus";
                break;
            case 2000:
                campusName="Comenius";
                break;
            case 1000:
                campusName="Diepenbeek";
                break;
            case 4000:
                campusName="Gasthuisberg";
                break;
            case 5000:
                campusName="Hertogstraat";
                break;
            case 6000:
                campusName="LiZa";
                break;
            case 7000:
                campusName="Oude Luikerbaan";
                break;
            case 8000:
                campusName="Proximus";
                break;
            case 9000:
                campusName="Sociale School";
                break;
            default:
                campusName="Clenardus";
                break;
        }
        return campusName;
    }

    public List getDataPerBeacon(int minor, int major) throws JSONException {
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
        String html, title;
        String youtube;

        //class instantie
        staticData staticData = new staticData();

        //minor doorgeven zodat men specifieke data kan verkrijgen van een beacon.
        staticData.setMinor(minor);

        staticData.setMajor(major);

        //uitvoeren van de klasse en data verkrijgen in de klasse zelf.
        staticData.execute();

        //data verkrijgen.
        JSONArray jA = staticData.getData();
        long tStart = System.currentTimeMillis();
        while(jA==null&&((System.currentTimeMillis()-tStart)/1000<2)){
            jA = staticData.getData();
        }

        if (jA == null){

            title = "Geen content";
            text = "Deze beacon bevat nog geen content";
            dataToDisplay.add(0,text);
            typesOfDataToDisplay.add(0,"text");
            titleOfData.add(0, title);

        }else{

            //de volledige data over gaan
            for(int i = 0; i < jA.length(); i++) {

                //per object de gegevens door geven.
                JSONObject jO = (JSONObject) jA.get(i);

                //type aanvragen
                String type = (String) jO.get("metatype_sn");

                //kijken welk type en doorgeven.
                switch (type) {
                    case "text":
                        title = (String) jO.get("title_sn");
                        text = (String) jO.get("content_txt");
                        dataToDisplay.add(i, text);
                        typesOfDataToDisplay.add(i, "text");
                        titleOfData.add(i, title);

                        break;

                    case "youtube":
                        title = (String) jO.get("title_sn");
                        youtube = (String) jO.get("content_txt");
                        dataToDisplay.add(i,youtube);
                        typesOfDataToDisplay.add(i, "youtube");
                        titleOfData.add(i, title);

                        break;

                    case "html":
                        title = (String) jO.get("title_sn");
                        html = (String) jO.get("content_txt");
                        dataToDisplay.add(i, html);
                        typesOfDataToDisplay.add(i, "html");
                        titleOfData.add(i, title);

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

    public List getRoutesWithBeacon(int major, int minor){
        /*todo
        * connectie met api om routes op te halen.*/
        List RoutesLijst;
        List RouteDetails;
        RoutesLijst=new ArrayList<>();

        switch(minor){
            case 11559:
                RouteDetails=new ArrayList<>();
                RouteDetails.add(0,"test route");
                RouteDetails.add(1,"test route bij informatiepunt 11559");
                RouteDetails.add(2,"inpikken in de route");
                RoutesLijst.add(RouteDetails);
                RouteDetails=new ArrayList<>();
                RouteDetails.add(0,"test route 1");
                RouteDetails.add(1,"test route 3 bij informatiepunt 11559");
                RouteDetails.add(2,"inpikken in de route");
                RoutesLijst.add(RouteDetails);
                RouteDetails=new ArrayList<>();
                RouteDetails.add(0,"test route 2");
                RouteDetails.add(1,"test route 3 bij informatiepunt 11559");
                RouteDetails.add(2,"inpikken in de route");
                RoutesLijst.add(RouteDetails);
                RouteDetails=new ArrayList<>();
                RouteDetails.add(0,"test route 3");
                RouteDetails.add(1,"test route 3 bij informatiepunt 11559");
                RouteDetails.add(2,"");
                RoutesLijst.add(RouteDetails);
                break;
            case 59394:
                RouteDetails=new ArrayList<>();
                RouteDetails.add(0,"test route 1");
                RouteDetails.add(1,"test route 3 bij informatiepunt 59394");
                RouteDetails.add(2,"");
                RoutesLijst.add(RouteDetails);
                RouteDetails=new ArrayList<>();
                RouteDetails.add(0,"test route 2");
                RouteDetails.add(1,"test route 3 bij informatiepunt 59394");
                RouteDetails.add(2,"inpikken in de route");
                RoutesLijst.add(RouteDetails);
                RouteDetails=new ArrayList<>();
                RouteDetails.add(0,"test route 3");
                RouteDetails.add(1,"test route 3 bij informatiepunt 59394");
                RouteDetails.add(2,"inpikken in de route");
                RoutesLijst.add(RouteDetails);
                break;
            default:
                RouteDetails=new ArrayList<>();
                RouteDetails.add(0,"Geen routes gevonden");
                RouteDetails.add(1,"Geen routes gevonden");
                RouteDetails.add(2,"");
                RoutesLijst.add(RouteDetails);

        }
        return RoutesLijst;
    }
}
