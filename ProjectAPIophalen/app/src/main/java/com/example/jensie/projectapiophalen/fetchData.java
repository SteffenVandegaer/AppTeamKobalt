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
 * Created by jensie on 7-10-2017.
 *
 * Deze class gaat er voor zorgen dat we al de data van alle beacons krijgen.
 */
//We gaan hier async werken in een andere klasse wegens dat anders onze app zou blijven hangen.
public class fetchData extends AsyncTask<Void, Void, Void> {

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
            URL fullLink = new URL( retrievedLink + "/testje");
            //Connectie openen (starten).
            HttpURLConnection connection = (HttpURLConnection) fullLink.openConnection();


//Voor als we URL zelf meegeven.
//            //url die we gaan gebruiken.
//            URL url = new URL("http://projectbeacons.co.nf/testje");
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

            //data in de vorm van json alles in de array zetten.
            JSONArray jA = new JSONArray(data);

            for(int i = 0; i < jA.length(); i++){
                //ieder object in de data gaan we in het JSONObject steken.
                JSONObject jO = (JSONObject) jA.get(i);
                //op een mooie manier laten zien dat we de data gaan binnenhalen.
                //Je kan ook een lijst gebruiken.
                singleParsed = "beacon ID: " + jO.get("beacon_id") + "\n" +
                               "location: " + jO.get("location_ln") + "\n" +
                               "description: " + jO.get("description_txt") + "\n";

                dataParsed += singleParsed + "\n";

            }

        } catch (MalformedURLException e) {
            //Als de url niet klopt van het protocol.
            dataParsed = "Er is een fout opgetreden met de URL: " + e.getMessage();
            e.printStackTrace();
        } catch (IOException e) {
            //Als de URL niet klopt of andere fouten.
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
