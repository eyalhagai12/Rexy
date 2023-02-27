package com.dji.rexy;

import android.app.Activity;
import android.os.Build;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileWriter;
import com.opencsv.CSVWriter;

import org.jboss.netty.util.TimerTask;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import dji.common.battery.BatteryState;
import dji.common.flightcontroller.Attitude;
import dji.common.gimbal.CapabilityKey;
import dji.common.gimbal.GimbalState;
import dji.common.util.DJIParamMinMaxCapability;
import dji.sdk.battery.Battery;
import dji.sdk.flightcontroller.FlightController;
import dji.sdk.gimbal.Gimbal;


public class LogCustom  {

    private FileWriter file = null;
    private CSVWriter writer = null;
    private volatile String mode;
    private volatile String debug;
    private FlightController flightController;
    private Gimbal gimbal;
    private Battery battery;
    private Instant start_time;
    private Duration time_passed_sec;
    private Double lon, lat, tof;

    private volatile Double gimbal_pitch, gimbal_yaw, gimbal_roll, battery_remaining;

//    private FileOutputStream fos = null;

    public LogCustom(File filepath){
        try {
            // init the mode indicator
            mode = "Floor";
            // init the debug message
            debug = "debug";
            // init the timer
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                start_time = Instant.now();
            }
            // init drone params
            gimbal_pitch = 0.0;
            gimbal_yaw = 0.0;
            gimbal_roll = 0.0;
            battery_remaining = 0.0;
            // init Log file
            String filename = generateFileName();
            file = new FileWriter(new File(filepath, filename));
            writer = new CSVWriter(file);
            // Add columns to the csv file
            String[] columns = {"time", "ToF", "Yaw", "pitch", "roll", "Lat", "Lon", "Gimbal yaw", "Gimbal pitch", "Gimbal roll", "Baro","Battery", "OF", "Mode", "Debug"};
            writer.writeNext(columns, false);

        }
        catch(FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void write(){

        lat = flightController.getState().getAircraftLocation().getLatitude();
        lon = flightController.getState().getAircraftLocation().getLongitude();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            time_passed_sec = Duration.between(start_time, Instant.now());
        }

        Attitude vals = flightController.getState().getAttitude();
        tof = (double) flightController.getState().getUltrasonicHeightInMeters();

        String[] info =
            {       time_passed_sec.toString(),
                    Double.toString(tof),
                    Double.toString(vals.yaw),
                    Double.toString(vals.pitch),
                    Double.toString(vals.roll),
                    Double.toString(lat),
                    Double.toString(lon),
                    Double.toString(gimbal_yaw),
                    Double.toString(gimbal_pitch),
                    Double.toString(gimbal_roll),
                    "0",
                    Double.toString(battery_remaining),
                    "0",
                    mode,
                    debug};
        writer.writeNext(info, false);
    }

    public void close(){
        try{
            writer.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    public void setMode(String new_mode){
        try{
            mode = new_mode;
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void setDebug(String new_debug){
        try{
            debug = new_debug;
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void setController(FlightController controller){
        flightController = controller;
    }

    public void setGimbal(Gimbal new_gimbal){
        gimbal = new_gimbal;
    }

    public void setBattery(Battery new_battery){
        battery = new_battery;
    }

    public void initListeners(){
        /*
            This method initialize on-update callback functions for:
            Gimbal-params, battery value.
         */
        gimbal.setStateCallback(new GimbalState.Callback() {
            @Override
            public void onUpdate(@NonNull GimbalState gimbalState) {
                gimbal_pitch = (double) (-1 * gimbalState.getAttitudeInDegrees().getPitch());
                gimbal_yaw = (double) (gimbalState.getAttitudeInDegrees().getYaw());
                gimbal_roll = (double) (gimbalState.getAttitudeInDegrees().getRoll());
            }
        });

        battery.setStateCallback(new BatteryState.Callback() {
            @Override
            public void onUpdate(BatteryState batteryState) {
                battery_remaining = (double) batteryState.getLifetimeRemaining();
            }
        });
    }

    private String generateFileName(){
        String date = "filename";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            date = java.time.Clock.systemUTC().instant().toString();
        }

        return date + ".csv";
    }









}
