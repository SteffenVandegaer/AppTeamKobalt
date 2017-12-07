package com.example.a11302481.rondleidingappteamkobalt.Models;

/**
 * Created by 11302481 on 7/12/2017.
 */

public class Campus {
    int id, major;
    String naam;

    public Campus(int id, int major, String naam){
        this.id=id;
        this.major=major;
        this.naam=naam;
    }

    public int getId(){
        return id;
    }

    public int getMajor(){
        return major;
    }

    public String getNaam(){
        return naam;
    }
}
