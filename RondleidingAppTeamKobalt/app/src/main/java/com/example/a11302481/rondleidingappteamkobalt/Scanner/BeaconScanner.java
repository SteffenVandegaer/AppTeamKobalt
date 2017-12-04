package com.example.a11302481.rondleidingappteamkobalt.Scanner;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.os.Handler;

import com.example.a11302481.rondleidingappteamkobalt.Models.Beacon;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Steffen on 17/10/2017.
 */

public class BeaconScanner {


    // scanner to get bluetooth devices
    private BluetoothLeScanner scanner;
    private BluetoothAdapter adapter;

    // boolean to indicate if adapter is scanning
    private boolean scanning;
    // instance to handle threading



    private int majorToFind,minorTofind=-1,maxDistance=100;

    private static List<Beacon> foundBeacons, beaconsTosSend;

    public BeaconScanner(BluetoothAdapter adapter) {
        // create instances of fields
        foundBeacons=new ArrayList<>();
        this.adapter = adapter;
        majorToFind=-1;

        // set scanner and handler
        scanner = adapter.getBluetoothLeScanner();


    }

    public BeaconScanner(BluetoothAdapter adapter, int majorToFind, int maxDistance) {
        // create instances of fields
        foundBeacons=new ArrayList<>();
        this.adapter = adapter;
        this.majorToFind=majorToFind;
        this.maxDistance=maxDistance;
        // set scanner and handler
        scanner = adapter.getBluetoothLeScanner();

    }

    public BeaconScanner(BluetoothAdapter adapter, int majorToFind, int minorToFind, int maxDistance) {
        // create instances of fields
        foundBeacons=new ArrayList<>();
        this.adapter = adapter;
        this.majorToFind=majorToFind;
        this.maxDistance=maxDistance;
        this.minorTofind=minorToFind;
        // set scanner and handler
        scanner = adapter.getBluetoothLeScanner();

    }

    public BeaconScanner(BluetoothAdapter adapter, int majorToFind) {
        // create instances of fields
        foundBeacons=new ArrayList<>();
        this.adapter = adapter;
        this.majorToFind=majorToFind;
        // set scanner and handler
        scanner = adapter.getBluetoothLeScanner();

    }

    // callback when scanned
    private ScanCallback scanCallback =(
            new ScanCallback() {
                @Override
                public void onScanResult(final int callbackType, final ScanResult result) {
                    // create beacon from scan result (if result is no beacon, null is returned)
                    //final Beacon scannedBeacon = Beacon.createBeaconFromScanResult(result, majorToFind);
                    ScanRecord scanRecord = result.getScanRecord();
                    if (scanRecord != null) {
                        byte[] bytesScanRecord = result.getScanRecord().getBytes();
                        int startByte;
                        boolean isBeacon = false;

                        for (startByte = 2; startByte <= 5; startByte++) {
                            if (((int) bytesScanRecord[startByte + 2] & 0xff) == 0x02 && //Identifies an iBeacon
                                    ((int) bytesScanRecord[startByte + 3] & 0xff) == 0x15) { //Identifies correct data length
                                // It's a beacon!!
                                isBeacon = true;
                                break;
                            }
                        }
                        int foundMinor = (bytesScanRecord[startByte + 22] & 0xff) * 0x100 + (bytesScanRecord[startByte + 23] & 0xff);

                        if (isBeacon){
                            int foundMajor=(bytesScanRecord[startByte + 20] & 0xff) * 0x100 + (bytesScanRecord[startByte + 21] & 0xff);
                            boolean validBeacon;
                            if(majorToFind==-1){
                                validBeacon=true;
                            }else{

                                if(foundMajor==majorToFind){
                                    if(minorTofind==-1){
                                        validBeacon=true;
                                    }else{
                                        if(foundMinor==minorTofind){
                                            validBeacon=true;
                                        }else{
                                            validBeacon=true;
                                        }
                                    }

                                }else{
                                    validBeacon=false;
                                }
                            }

                            if(validBeacon){

                                // set bluetoothDevice
                                //scannedBeacon.bluetoothDevice = result.getDevice();

                                double rssi;
                                int txPower,major, minor;
                                // set rssi
                                rssi = result.getRssi();
                                // set power (sort of batterypower???)
                                txPower = bytesScanRecord[startByte + 24];

                                // major
                                major = (bytesScanRecord[startByte + 20] & 0xff) * 0x100 + (bytesScanRecord[startByte + 21] & 0xff);
                                // minor
                                minor = (bytesScanRecord[startByte + 22] & 0xff) * 0x100 + (bytesScanRecord[startByte + 23] & 0xff);

                                Beacon scannedBeacon = new Beacon(major, minor, rssi, txPower);
                                int teller=0;
                                boolean notReplaced=true;
                                if(scannedBeacon.getAccuracy()<maxDistance) {
                                    if (foundBeacons != null) {
                                        for (Beacon b : foundBeacons) {
                                            if ((b.getMajor() == major) && (b.getMinor() == minor)) {
                                                if (b.getAccuracy() > scannedBeacon.getAccuracy()) {
                                                    foundBeacons.set(teller,scannedBeacon);
                                                }
                                                notReplaced = false;
                                            }
                                            teller++;
                                        }
                                        if (notReplaced) {
                                            foundBeacons.add(scannedBeacon);
                                        }
                                    } else {
                                        foundBeacons = new ArrayList<>();
                                        foundBeacons.add(scannedBeacon);
                                    }
                                }

                            }

                        }
                    }

                }
            });



    public List getFoundBeacons(){

        beaconsTosSend=foundBeacons;

        if(beaconsTosSend==null){
            List<Integer> none=new ArrayList<Integer>();
            none.add(0);
            return none;
        }else{
            foundBeacons=null;
            return beaconsTosSend;
        }


    }




    /**
     * Starts the scanner.
     * If the adapter is null or not enabled, the scan will not start.
     * Listeners are notified when the scanner starts.
     *
     * @return whether the scan started
     */
    public boolean start() {
        // check if adapter exists and is enabled
        if (adapter == null || !adapter.isEnabled()) {
            return false;
        }

        // start the scan
        scanning = true;
        scanner.startScan(scanCallback);

        return true;
    }

    /**
     * Stops the scanner.
     * Listeners are notified when the scanner stops.
     */
    public void stop() {
        // stop scanning
        scanning = false;
        scanner.stopScan(scanCallback);

    }

    /* ------------------------- GETTERS ------------------------- */

    /**
     * @return the scanner state.
     */
    public boolean isScanning() {
        // return the scanner state
        return scanning;
    }
}
