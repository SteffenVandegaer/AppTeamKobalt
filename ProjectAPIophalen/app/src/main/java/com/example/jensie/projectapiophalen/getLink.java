package com.example.jensie.projectapiophalen;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by jensie on 8-10-2017.
 */

public class getLink {

    public URL verkrijgLink(){//// TODO: 8-10-2017  

        //url die we gaan gebruiken.
        try {
            URL url = new URL("http://projectbeacons.co.nf/");
            return url;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
