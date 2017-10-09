package com.example.jensie.projectapiophalen;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by jensie on 8-10-2017.
 *
 * In deze klasse kan men de url aanpassen van de hosting.
 */

public class getLink {

    public URL verkrijgLink(){

        //url die we gaan gebruiken.
        try {
            //men kan hier de url aanpassen als de hosting veranderd.
            URL url = new URL("http://projectbeacons.co.nf/");
            return url;
            //moest er een fout zijn met de URL.
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
