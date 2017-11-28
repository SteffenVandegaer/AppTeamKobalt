package com.example.a11302481.rondleidingappteamkobalt.Models;

import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

/**
 * Created by 11302481 on 13/10/2017.
 */

public class Beacon implements Parcelable{
    private int minor,major;
    private double rssi, accuracy=100;
    private int txPower;

    public Beacon(Parcel in) {
        Bundle extra=in.readBundle();
        minor=extra.getInt("minor");
        major=extra.getInt("major");
        rssi=extra.getDouble("rssi");
        accuracy=extra.getDouble("accuracy");
        txPower=extra.getInt("txPower");
    }

    public static Beacon createBeaconFromScanResult(@NonNull final ScanResult result, int majorToFind) {
        // get the scan record
        ScanRecord scanRecord = result.getScanRecord();
        // if scan record doesn't exist
        // => return null
        if (scanRecord == null) {
            return null;
        }

        // get data from scan record
        byte[] bytesScanRecord = result.getScanRecord().getBytes();

        // byte at which the beacon data starts
        int startByte;
        // bool to indicate whether result is a beacon
        boolean isBeacon = false;

        //find out if device is a beacon
        for (startByte = 2; startByte <= 5; startByte++) {
            if (((int) bytesScanRecord[startByte + 2] & 0xff) == 0x02 && //Identifies an iBeacon
                    ((int) bytesScanRecord[startByte + 3] & 0xff) == 0x15) { //Identifies correct data length
                // It's a beacon!!
                isBeacon = true;
                break;
            }
        }

        // if result is a Beacon
        // => create one from the data.
        if (isBeacon){
            int foundMajor=(bytesScanRecord[startByte + 20] & 0xff) * 0x100 + (bytesScanRecord[startByte + 21] & 0xff);
            boolean validBeacon;
            if(majorToFind==-1){
                validBeacon=true;
            }else{

                if(foundMajor==majorToFind){
                    validBeacon=true;
                }else{
                    validBeacon=false;
                }
            }

            if(validBeacon){
                // create Beacon
                Beacon scannedBeacon = new Beacon();
                // set bluetoothDevice
                //scannedBeacon.bluetoothDevice = result.getDevice();

                // set rssi
                scannedBeacon.rssi = result.getRssi();
                // set power (sort of batterypower???)
                scannedBeacon.txPower = bytesScanRecord[startByte + 24];

                // major
                scannedBeacon.major = (bytesScanRecord[startByte + 20] & 0xff) * 0x100 + (bytesScanRecord[startByte + 21] & 0xff);
                // minor
                scannedBeacon.minor = (bytesScanRecord[startByte + 22] & 0xff) * 0x100 + (bytesScanRecord[startByte + 23] & 0xff);

                scannedBeacon.calculateAccuracy();
                // return created Beacon
                return scannedBeacon;
            }else{
                return null;
            }

        }

        // This ain't no beacon!! => return null
        return null;
    }
    public Beacon(){}

    public Beacon(int major, int minor, double rssi, int txPower){
        this.major=major;
        this.minor=minor;
        this.rssi=rssi;
        this.txPower=txPower;

        calculateAccuracy();
    }

    public int getMajor(){
        return major;
    }

    public int getMinor(){
        return minor;
    }

    public double getAccuracy(){
        return accuracy;
    }

    private void calculateAccuracy(){
        if (rssi == 0) {
            accuracy=100;
        }else{
            double ratio = rssi*1.0/txPower;
            if (ratio < 1.0) {
                accuracy= Math.pow(ratio,10);
            }
            else {
                accuracy =  (0.89976)*Math.pow(ratio,7.7095) + 0.111;
            }
        }

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Bundle extras = new Bundle();
        extras.putInt("major", major);
        extras.putInt("minor", minor);
        extras.putDouble("rssi", rssi);
        extras.putDouble("accuracy",accuracy);
        extras.putInt("txPower",txPower);
        dest.writeBundle(extras);
    }

    public static final Parcelable.Creator<Beacon> CREATOR = new Parcelable.Creator<Beacon>() {
        public Beacon createFromParcel(Parcel in) {
            return new Beacon(in);
        }

        public Beacon[] newArray(int size) {
            return new Beacon[size];
        }
    };

}
