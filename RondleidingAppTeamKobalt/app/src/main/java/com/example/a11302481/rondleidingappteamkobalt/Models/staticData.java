package com.example.a11302481.rondleidingappteamkobalt.Models;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by jensie on 15-10-2017.
 *
 * Deze klasse verkrijgt al de statische data van een beacon.
 */

public class staticData extends AsyncTask<Object, Object, JSONArray> {

    //bevat de data.
    private String data = "";
    int minor = 0,major;
    JSONArray jA = null;

    @Override
    public JSONArray doInBackground(Object... voids) {
        try {

            GetLink link = new GetLink();
            //url opvragen Vaste URL.
            URL retrievedLink = link.verkrijgLink();

            //link aanvullen.
            URL fullLink = new URL( retrievedLink + "GET/beacon/" + major + "/staticData/" + minor);
            //Connectie openen (starten).
            HttpURLConnection connection = (HttpURLConnection) fullLink.openConnection();

            //lezen/schrijven van data.
            InputStream stream = connection.getInputStream();
            //Gaat de data lezen van de stream.
            BufferedReader buffer = new BufferedReader(new InputStreamReader(stream));

            //lezen van de lijnen in het bestand.
            String line = "";
            while (line != null) {
                //lezen van een lijn.
                line = buffer.readLine();
                //data toevoegen aan data.
                data += line;
            }

            //data in de vorm van json alles in de array zetten.
            jA = new JSONArray(data);

        } catch (MalformedURLException e) {
            //Als de url niet klopt van het protocol.
//            dataParsed = "Er is een fout opgetreden met de URL: " + e.getMessage();
            e.printStackTrace();
        } catch (IOException e) {
            //Als de URL niet klopt of andere fouten.
//            dataParsed = "Er is een fout opgetreden: " + e.getMessage();
            e.printStackTrace();
        } catch (JSONException e) {
            //fout met de JSON conversie.
//            dataParsed = "Er is een fout opgetreden met de JSON conversie: " + e.getMessage();
            e.printStackTrace();
        }
        return null;
    }

    //minor zetten die we doorkrijgen.
    Void setMinor(int setMinorFromClass){
        //verkrijgen van de minor van de andere klasse.
        minor = setMinorFromClass;
        return null;
    }

    Void setMajor(int setMajorFromClass){
        //verkrijgen van de minor van de andere klasse.
        major = setMajorFromClass;
        return null;
    }

    //verkrijgen van de data.
    JSONArray getData(){
        return jA;
    }
}
