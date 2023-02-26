package com.dji.rexy;

import android.app.Activity;

import java.io.File;
import java.io.FileWriter;
import com.opencsv.CSVWriter;

import org.jboss.netty.util.TimerTask;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import dji.sdk.flightcontroller.FlightController;


public class LogCustom  {

    private FileWriter file = null;
    private CSVWriter writer = null;
    private volatile String mode;
    private volatile String debug;
    private FlightController flightController;
//    private FileOutputStream fos = null;

    public LogCustom(File filepath){
        try {
//            file = new File(filepath, filename);
//            fos = new FileOutputStream(file);
//            fos.write("File Created - Login successfully!\n".getBytes());
            // init the mode indicator
            mode = "Floor";
            // init the debug message
            debug = "debug";
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
        //            fos.write((log_info + "\n").getBytes());
        String[] info = {"0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", mode, debug};
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

    private String generateFileName(){
        String date = "filename";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            date = java.time.Clock.systemUTC().instant().toString();
        }

        return date + ".csv";
    }









}
