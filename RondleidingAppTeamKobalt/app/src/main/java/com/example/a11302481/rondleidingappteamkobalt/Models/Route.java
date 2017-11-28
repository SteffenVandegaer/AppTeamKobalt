package com.example.a11302481.rondleidingappteamkobalt.Models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 11302481 on 28/11/2017.
 */

public class Route {
    private String routeName, routeDescription;
    private int routeId;
    private List beacons;

    public Route(String Name, int id, String description){
        routeName=Name;
        routeId=id;
        routeDescription=description;
        beacons=new ArrayList<>();
    }

    public void addBeacon(Beacon beacon){
        beacons.add(beacon);
    }

    public int countBeacons(){
        return beacons.size();
    }

    public Beacon getBeacon(int beaconId){
        return (Beacon)beacons.get(beaconId);
    }
}
