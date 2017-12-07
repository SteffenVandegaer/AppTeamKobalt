package com.example.a11302481.rondleidingappteamkobalt.Models;

import android.graphics.Bitmap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 11302481 on 7/12/2017.
 */

public class CampusModel {
    List campi;
    RetrieveDataFromApi dataSource;
    boolean allReceived=false;
    public CampusModel() {

    }
    public boolean loadData() throws JSONException{
        campi=new ArrayList();
        dataSource=new RetrieveDataFromApi("campus");
        dataSource.execute();
        JSONArray jA = dataSource.getData();
        long tStart = System.currentTimeMillis();
        while(jA==null&&((System.currentTimeMillis()-tStart)/1000<2)){
            jA = dataSource.getData();
        }

        if (jA == null || jA.length()==0){
            allReceived=true;
        }else{

            //de volledige data over gaan
            for(int i = 0; i < jA.length(); i++) {

                //per object de gegevens door geven.
                JSONObject jO = (JSONObject) jA.get(i);


                Campus campus=new Campus((int)jO.get("campus_id"),(int)jO.get("major"),(String)jO.get("name"));
                campi.add(campus);
            }
            allReceived=true;
        }
        return true;
    }

    public List getCampi(){
        while(!allReceived){

        }
        return campi;
    }

}
