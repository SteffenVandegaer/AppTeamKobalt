package com.example.a11302481.rondleidingappteamkobalt.Models;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 11302481 on 28/11/2017.
 */

public class Route implements Parcelable {
    private String routeName, routeDescription;
    private int routeId;
    private List beacons;
    private int progress;

    public Route(String Name, int id, String description, int progress){
        routeName=Name;
        routeId=id;
        routeDescription=description;
        beacons=new ArrayList<>();
        this.progress=progress;
    }

    private Route(Parcel in) {
        Bundle data=in.readBundle();
        routeName = data.getString("routeName");
        routeId=data.getInt("id");
        routeDescription=data.getString("routeDescription");
        beacons=data.getParcelableArrayList("beacons");
        progress=data.getInt("progress",0);
    }

    public int getId(){
        return routeId;
    }

    public int getProgress(){return progress;}

    public void setProgress(int progress){this.progress=progress;}

    public String getName(){
        return routeName;
    }

    public String getDescription(){
        return routeDescription;
    }

    public void addBeaconMinor(int minor){
        beacons.add(minor);
    }

    public void setBeaconList(List l){
        beacons=l;
    }

    public int countBeacons(){
        return beacons.size();
    }

    public int getBeaconMinor(int beaconId){
        return (int)beacons.get(beaconId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Bundle extras = new Bundle();
        extras.putInt("id", routeId);
        extras.putInt("progress",progress);
        extras.putString("routeName", routeName);
        extras.putString("routeDescription", routeDescription);
        extras.putParcelableArrayList("beacons",(ArrayList)beacons);
        dest.writeBundle(extras);
    }

    public static final Parcelable.Creator<Route> CREATOR = new Parcelable.Creator<Route>() {
        public Route createFromParcel(Parcel in) {
            return new Route(in);
        }

        public Route[] newArray(int size) {
            return new Route[size];
        }
    };
}
