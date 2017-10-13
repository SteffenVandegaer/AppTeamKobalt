package com.example.a11302481.rondleidingappteamkobalt.Scanner;
import com.example.a11302481.rondleidingappteamkobalt.Models.Beacon;
import com.example.a11302481.rondleidingappteamkobalt.Controllers.SearchingActivity;

/**
 * Created by 11302481 on 13/10/2017.
 */

public interface OnScanListener {

    /**
     * Invokes when the scan is stopped.
     */
    void onScanStopped();

    /**
     * Invokes when the scan is stopped.
     */
    void onScanStarted();

    /**
     * Invokes when a beacon is found.
     */
    void onBeaconFound(Beacon beacon);
}
