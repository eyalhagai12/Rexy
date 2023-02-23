package com.dji.rexy;

import android.app.Activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;



public class Log extends Activity {

    private File file;
    private FileOutputStream fos = null;
    private String filepath = "LOG";



    public Log(){
        try {
            String filename = generateFileName();
            file = new File(getExternalFilesDir(filepath), filename);
            fos = new FileOutputStream(file);
            fos.write("File Created - Login successfully!".getBytes());
        }
        catch(FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void write(String log_info){
        try{
            fos.write(log_info.getBytes());
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public void close(){
        try{
            fos.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }

    }

    private String generateFileName(){
        String date = "filename";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            date = java.time.Clock.systemUTC().toString();
        }

        return date + ".txt";
    }









}
