package com.example.jensie.projectapiophalen;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by jensie on 8-10-2017.
 *
 * Deze klasse gaat specifieke statische data opvragen van een bepaalde beacon.
 * Nu nog hardcoded.
 */

public class fetchspecificData extends AsyncTask<Void, Void, Void> {

    //bevat de data.
    private String data = "";
    private String dataParsed = "";
    private String singleParsed = "";

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            getLink link = new getLink();
            //url opvragen Vaste URL.
            URL retrievedLink = link.verkrijgLink();
            //link aanvullen.
            URL fullLink = new URL( retrievedLink + "/specifiek");
            //Connectie openen (starten).
            HttpURLConnection connection = (HttpURLConnection) fullLink.openConnection();


//Voor als we URL zelf meegeven.
//            //link waar we data van halen.
//            URL url = new URL("http://projectbeacons.co.nf/specifiek");
//
//            //connectie openen (starten).
//            HttpURLConnection connection = (HttpURLConnection) url.openConnection();


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

            //We krijgen enkel één object aan en geen array daarom doen we dit maar één keer.
            JSONObject jO = new JSONObject(data);
            //op een mooie manier laten zien dat we de data gaan binnenhalen.
            //Je kan ook een lijst gebruiken.
            singleParsed = "metatype: " + jO.get("metatype_sn") + "\n" +
                    "content: " + jO.get("content_txt") + "\n";

            dataParsed += singleParsed + "\n";

            //}

        } catch (MalformedURLException e) {
            //Als de url niet klopt van het protocol.
            dataParsed = "Er is een fout opgetreden met de URL: " + e.getMessage();
            e.printStackTrace();
        } catch (IOException e) {
            //Als de URL niet klopt.
            dataParsed = "Er is een fout opgetreden: " + e.getMessage();
            e.printStackTrace();
        } catch (JSONException e) {
            //fout met de JSON conversie.
            dataParsed = "Er is een fout opgetreden met de JSON conversie: " + e.getMessage();
            e.printStackTrace();
        }

        return null;
    }

    //connectie met de UI.
    @Override
    protected void onPostExecute(Void aVoid){
        super.onPostExecute(aVoid);
        //doorgeven aan de User.
        APIActivity.dataTextView.setText(dataParsed);
    }

}
