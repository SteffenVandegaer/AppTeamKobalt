package com.example.a11302481.rondleidingappteamkobalt.Models;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by jensie on 15-10-2017.
 *
 * In deze klasse kan men de url aanpassen van de hosting.
 */

public class GetLink {
    public URL verkrijgLink(){

        //url die we gaan gebruiken.
        try {
            //men kan hier de url aanpassen als de hosting veranderd.
            URL url = new URL("http://startworx.be/api/");
            return url;
            //moest er een fout zijn met de URL.
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
