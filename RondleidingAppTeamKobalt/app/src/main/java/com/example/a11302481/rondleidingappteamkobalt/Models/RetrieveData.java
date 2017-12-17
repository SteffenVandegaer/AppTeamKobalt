package com.example.a11302481.rondleidingappteamkobalt.Models;

import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.RequiresApi;

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
    private CampusModel campusModel;

    public RetrieveData() {
        campusModel = new CampusModel();
    }

    public String[] getAllCampi(){
        try{
            campusModel.loadData();
        }catch(JSONException e){
            e.printStackTrace();
        }

        List campi=campusModel.getCampi();
        String[] campusArray=new String[campi.size()];
        int teller=0;
        for (Object o:campi) {
            Campus campus=(Campus)o;
            campusArray[teller]=campus.getNaam();
            teller++;
        }

        return campusArray;
    }

    public int getCampusId(String campusName){
        int major=campusModel.getCampusMajor(campusName);
        //major per campus toewijzen aan de hand van de gemaakte keuze in spinnen (zal later met data uit de database vervangen worden)

        return major;
    }

    public String getCampusName(int major){
        String campusName=campusModel.getCampusName(major);
        //major per campus toewijzen aan de hand van de gemaakte keuze in spinner (zal later met data uit de database vervangen worden)

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

        String image;
        String text;
        String html, title;
        String youtube;

        //class instantie
        RetrieveDataFromApi dataSource=new RetrieveDataFromApi("beacons/"+minor+'/'+major);

        //uitvoeren van de klasse en data verkrijgen in de klasse zelf.
        dataSource.execute();

        //data verkrijgen.
        JSONArray jA = dataSource.getData();
        long tStart = System.currentTimeMillis();
        while(jA==null&&((System.currentTimeMillis()-tStart)/1000<2)){
            jA = dataSource.getData();
        }

        if (jA == null || jA.length()==0){

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

                String type="";

                if(jO.get("html").toString()!="null"){
                    type="html";
                }else{
                    if(jO.get("url").toString()!="null"){
                        type="youtube";
                    }else{
                        if(jO.get("text").toString()!="null"){
                            type="text";
                        }else{
                            if(jO.get("image").toString()!="null"){
                                type="image";
                            }
                        }
                    }
                }

                //kijken welk type en doorgeven.
                switch (type) {
                    case "text":
                        title = (String) jO.get("title");
                        text = (String) jO.get("text");
                        dataToDisplay.add(i, text);
                        typesOfDataToDisplay.add(i, "text");
                        titleOfData.add(i, title);

                        break;

                    case "youtube":
                        title = (String) jO.get("title");

                        youtube = (String) jO.get("url");
                        String[] parts = youtube.split("v=");
                        youtube=parts[1];
                        dataToDisplay.add(i,youtube);
                        typesOfDataToDisplay.add(i, "youtube");
                        titleOfData.add(i, title);

                        break;

                    case "html":
                        title = (String) jO.get("title");
                        html = (String) jO.get("html");
                        dataToDisplay.add(i, html);
                        typesOfDataToDisplay.add(i, "html");
                        titleOfData.add(i, title);

                        break;

                    case "image":
                        title = (String) jO.get("title");
                        image = (String) jO.get("image");
                        dataToDisplay.add(i,image);
                        typesOfDataToDisplay.add(i,"image");
                        titleOfData.add(i, title);

                        break;
                    default:
                        title = (String) jO.get("title_sn");
                        text = (String) jO.get("content_txt");
                        dataToDisplay.add(i, text);
                        typesOfDataToDisplay.add(i, "text");
                        titleOfData.add(i, title);

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

    public List getDataPerBeaconInRoute(int minor, int major, int routeId, int sequence) throws JSONException {
        List returnList;
        List dataToDisplay;
        List typesOfDataToDisplay;
        List titleOfData;
        dataToDisplay= new ArrayList<>();
        typesOfDataToDisplay= new ArrayList<>();
        returnList=new ArrayList<>();
        titleOfData=new ArrayList<>();
        int staticTotal=0;
        String image;
        String text;
        String html, title;
        String youtube;
        RetrieveDataFromApi dataSource;
        JSONArray jA;
        long tStart;



        //class instantie
        dataSource=new RetrieveDataFromApi("beacons/"+minor+'/'+major);

        //uitvoeren van de klasse en data verkrijgen in de klasse zelf.
        dataSource.execute();

        //data verkrijgen.
        jA = dataSource.getData();
        tStart = System.currentTimeMillis();
        while(jA==null&&((System.currentTimeMillis()-tStart)/1000<2)){
            jA = dataSource.getData();
        }

        if (jA == null || jA.length()==0){

        }else{

            //de volledige data over gaan
            for(int i = 0; i < jA.length(); i++) {

                //per object de gegevens door geven.
                JSONObject jO = (JSONObject) jA.get(i);

                //type aanvragen

                String type="";

                if(jO.get("html").toString()!="null"){
                    type="html";
                }else{
                    if(jO.get("url").toString()!="null"){
                        type="youtube";
                    }else{
                        if(jO.get("text").toString()!="null"){
                            type="text";
                        }else{
                            if(jO.get("image").toString()!="null"){
                                type="image";
                            }
                        }
                    }
                }

                //kijken welk type en doorgeven.
                switch (type) {
                    case "text":
                        title = (String) jO.get("title");
                        text = (String) jO.get("text");
                        dataToDisplay.add(i, text);
                        typesOfDataToDisplay.add(i, "text");
                        titleOfData.add(i, title);

                        break;

                    case "youtube":
                        title = (String) jO.get("title");

                        youtube = (String) jO.get("url");
                        String[] parts = youtube.split("v=");
                        youtube=parts[1];
                        dataToDisplay.add(i,youtube);
                        typesOfDataToDisplay.add(i, "youtube");
                        titleOfData.add(i, title);

                        break;

                    case "html":
                        title = (String) jO.get("title");
                        html = (String) jO.get("html");
                        dataToDisplay.add(i, html);
                        typesOfDataToDisplay.add(i, "html");
                        titleOfData.add(i, title);

                        break;

                    case "image":
                        title = (String) jO.get("title");
                        image = (String) jO.get("image");
                        dataToDisplay.add(i,image);
                        typesOfDataToDisplay.add(i,"image");
                        titleOfData.add(i, title);

                        break;
                    default:
                        title = (String) jO.get("title_sn");
                        text = (String) jO.get("content_txt");
                        dataToDisplay.add(i, text);
                        typesOfDataToDisplay.add(i, "text");
                        titleOfData.add(i, title);

                }
                staticTotal=i;
            }

        }

        staticTotal++;
        dataToDisplay.add(staticTotal, "Separator");
        typesOfDataToDisplay.add(staticTotal, "text");
        titleOfData.add(staticTotal, "Separator");
        dataSource=new RetrieveDataFromApi("routen/content/"+routeId+"/"+sequence);

        //uitvoeren van de klasse en data verkrijgen in de klasse zelf.
        dataSource.execute();
        staticTotal++;

        //data verkrijgen.
        jA=null;
        jA = dataSource.getData();
        tStart = System.currentTimeMillis();
        while(jA==null&&((System.currentTimeMillis()-tStart)/1000<2)){
            jA = dataSource.getData();
        }

        if (jA == null || jA.length()==0){


        }else{

            //de volledige data over gaan

            for(int i =0; i < jA.length(); i++) {

                //per object de gegevens door geven.
                JSONObject jO = (JSONObject) jA.get(i);

                //type aanvragen

                String type="";

                if(jO.get("html").toString()!="null"){
                    type="html";
                }else{
                    if(jO.get("url").toString()!="null"){
                        type="youtube";
                    }else{
                        if(jO.get("text").toString()!="null"){
                            type="text";
                        }else{
                            if(jO.get("image").toString()!="null"){
                                type="image";
                            }
                        }
                    }
                }

                //kijken welk type en doorgeven.
                switch (type) {
                    case "text":
                        title = (String) jO.get("title");
                        text = (String) jO.get("text");
                        dataToDisplay.add(i+staticTotal, text);
                        typesOfDataToDisplay.add(i+staticTotal, "text");
                        titleOfData.add(i+staticTotal, title);

                        break;

                    case "youtube":
                        title = (String) jO.get("title");

                        youtube = (String) jO.get("url");
                        String[] parts = youtube.split("v=");
                        youtube=parts[1];
                        dataToDisplay.add(i+staticTotal,youtube);
                        typesOfDataToDisplay.add(i+staticTotal, "youtube");
                        titleOfData.add(i+staticTotal, title);

                        break;

                    case "html":
                        title = (String) jO.get("title");
                        html = (String) jO.get("html");
                        dataToDisplay.add(i+staticTotal,html);
                        typesOfDataToDisplay.add(i+staticTotal,"html");
                        titleOfData.add(i+staticTotal,title);

                        break;

                    case "image":
                        title = (String) jO.get("title");
                        image = (String) jO.get("image");
                        dataToDisplay.add(i+staticTotal,image);
                        typesOfDataToDisplay.add(i+staticTotal,"image");
                        titleOfData.add(i+staticTotal, title);

                        break;
                    default:
                        title = (String) jO.get("title_sn");
                        text = (String) jO.get("content_txt");
                        dataToDisplay.add(i+staticTotal, text);
                        typesOfDataToDisplay.add(i+staticTotal, "text");
                        titleOfData.add(i+staticTotal, title);

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

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    public List getBeaconName(int minor, int major) throws JSONException {
        List beaconInfo;
        beaconInfo=new ArrayList<>();

        RetrieveDataFromApi dataSource=new RetrieveDataFromApi("beacon/"+minor+"/"+major);

        //uitvoeren van de klasse en data verkrijgen in de klasse zelf.
        dataSource.execute();

        JSONArray jA = dataSource.getData();
        long tStart = System.currentTimeMillis();
        while(jA==null&&((System.currentTimeMillis()-tStart)/1000<2)){
            jA = dataSource.getData();
        }

        if (jA != null){

            //de volledige data over gaan
            for(int i = 0; i < jA.length(); i++) {

                //per object de gegevens door geven.
                JSONObject jO = (JSONObject) jA.get(i);

                //type aanvragen

                //kijken welk type en doorgeven.
                beaconInfo.add((String)jO.get("name"));
                beaconInfo.add((String)jO.get("location"));
            }

        }
        return beaconInfo;
    }

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    private List getBeaconsInRoute(int routeID) throws JSONException {
        List beaconLijst;
        beaconLijst=new ArrayList<>();

        RetrieveDataFromApi dataSource=new RetrieveDataFromApi("routen/"+routeID);

        //uitvoeren van de klasse en data verkrijgen in de klasse zelf.
        dataSource.execute();

        //data verkrijgen.
        JSONArray jA = dataSource.getData();
        long tStart = System.currentTimeMillis();
        while(jA==null&&((System.currentTimeMillis()-tStart)/1000<2)){
            jA = dataSource.getData();
        }

        if (jA != null){

            //de volledige data over gaan
            for(int i = 0; i < jA.length(); i++) {

                //per object de gegevens door geven.
                JSONObject jO = (JSONObject) jA.get(i);

                //type aanvragen

                //kijken welk type en doorgeven.
                beaconLijst.add((int)jO.get("minor"));
            }

        }
        return beaconLijst;
    }

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    public List getRoutesWithBeacon(int major, int minor) throws JSONException {
        /*todo
        * connectie met api om routes op te halen.*/
        List RoutesLijst;
        List RouteDetails;

        RoutesLijst=new ArrayList<>();

        RetrieveDataFromApi dataSource=new RetrieveDataFromApi("routen/"+minor+'/'+major);

        //uitvoeren van de klasse en data verkrijgen in de klasse zelf.
        dataSource.execute();

        //data verkrijgen.
        JSONArray jA = dataSource.getData();
        long tStart = System.currentTimeMillis();
        while(jA==null&&((System.currentTimeMillis()-tStart)/1000<2)){
            jA = dataSource.getData();
        }

        if (jA == null){
            RouteDetails=new ArrayList<>();
            RouteDetails.add(0,"Geen routes gevonden");
            RouteDetails.add(1,"Geen routes gevonden");
            RouteDetails.add(2,"");
            Route route=new Route((String)RouteDetails.get(1),0,(String)RouteDetails.get(2),0);
            RoutesLijst.add(route);

        }else{

            //de volledige data over gaan
            for(int i = 0; i < jA.length(); i++) {

                //per object de gegevens door geven.
                JSONObject jO = (JSONObject) jA.get(i);

                //type aanvragen

                //kijken welk type en doorgeven.

                Route route=new Route((String)jO.get("name"),(int)jO.get("route_id"),(String)jO.get("description"),(int)jO.get("sequence"));
                route.setBeaconList(getBeaconsInRoute(route.getId()));
                RoutesLijst.add(route);
            }

        }

        return RoutesLijst;
    }
}
