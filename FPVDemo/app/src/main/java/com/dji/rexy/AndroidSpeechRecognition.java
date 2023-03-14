package com.dji.rexy;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.widget.Button;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Locale;

public class AndroidSpeechRecognition {

    private SpeechRecognizer speechRecognizer;
    private TextView result;
    private Button micButton;
    private Context context;
    private FlightCommandUI UI_commands;
    private T2S speaker;
    private Activity mainActivity;
    private boolean isRecording;
    final Intent speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
    public AndroidSpeechRecognition(TextView init_result, Context init_context, Button init_button, FlightCommandUI init_ui_commands, T2S init_speaker, Activity init_activity){
        this.context = init_context;
        this.result = init_result;
        this.micButton = init_button;
        this.UI_commands = init_ui_commands;
        this.speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this.context);
        this.speaker = init_speaker;
        this.mainActivity = init_activity;
        this.isRecording = false;
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        init_listeners();
    }

    private void init_listeners(){
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {
                micButton.setText(new String("Record"));
            }

            @Override
            public void onBeginningOfSpeech() {
                micButton.setText(new String("Listening..."));
            }

            @Override
            public void onRmsChanged(float v) {

            }

            @Override
            public void onBufferReceived(byte[] bytes) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int i) {

            }

            @Override
            public void onResults(Bundle bundle) {
                micButton.setText(new String("Record"));
                ArrayList<String> data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                result.setText(data.get(0));
                int commandKey = parseCommand(data.get(0));
                voice_command_execute(commandKey);
            }

            @Override
            public void onPartialResults(Bundle bundle) {

            }

            @Override
            public void onEvent(int i, Bundle bundle) {

            }
        });
    }

    public void recognition(){
//        if (!isRecording){
//            speechRecognizer.startListening(speechRecognizerIntent);
//            isRecording = true;
//        }
//        else{
//            speechRecognizer.stopListening();
//            isRecording = false;
//        }
        speechRecognizer.startListening(speechRecognizerIntent);
    }

    public int parseCommand(String command){

        String[] parts_of_command = command.split(" ");


        // TakeOff section
        if (list_contain(parts_of_command, "takeoff")){
            // Fully understood, user commands a Takeoff
            return 0;
        }
        else if (list_contain(parts_of_command, "take")){
            if (list_contain(parts_of_command, "off")){
                // Partly understood, user commands a Takeoff
                return 0;
            }
            else{
                // The command isn't clear, will ask for a clarification from the user.
                return -1;
            }
        }

        // Land section
        if (list_contain(parts_of_command,"land")){
            return 1;
        }

        // Forward section
        if (list_contain(parts_of_command,"go")){
            // Fully understood, the user commands moving Forward
            return 2;
        }

        // Backward section
        if (parts_of_command[0].equalsIgnoreCase("backward")){
            // Fully understood, the user commands moving Backward
            return 3;
        }
        else if (parts_of_command[0].equalsIgnoreCase("back")){
            if (parts_of_command[1].equalsIgnoreCase("ward")){
                // Partly understood, the user commands moving Backward
                return 3;
            }
        } else if (list_contain(parts_of_command, "back")) {
            return 3;
        }

        // Left turn section
        if (list_contain(parts_of_command, "left")){
            // Fully understood, the user commands a left turn
            return 4;
        }

        // Right turn section
        if (list_contain(parts_of_command,"right")){
            // Fully understood, the user commands a right turn
            return 5;
        } else if (list_contain(parts_of_command, "light")) {
            return 5;
        }

        // Yaw turn section
        if (list_contain(parts_of_command,"spin")){
            if (list_contain(parts_of_command,"left")){
                // Fully understood, the user commands a Yaw left turn
                return 6;
            }
            else if (list_contain(parts_of_command,"right") || list_contain(parts_of_command, "light")){
                // Fully understood, the user commands a Yaw right turn
                return 7;
            }
            else{
                // command isn't clear, asking the user for clarifications
                return -1;
            }
        }

        // Up section
        if (list_contain(parts_of_command,"up") || list_contain(parts_of_command, "increase")
                || list_contain(parts_of_command, "icrease")){
            // Fully understood, the user commands a right turn
            return 8;
        }

        // Down section
        if (parts_of_command[0].equalsIgnoreCase("down")){
            // Fully understood, the user commands a right turn
            return 9;
        }

        // Stop section
        if (parts_of_command[0].equalsIgnoreCase("stop")){
            // Fully understood, the user commands a right turn
            return 10;
        }

        // speed section
        if (parts_of_command[0].equalsIgnoreCase("speed") || parts_of_command[0].equalsIgnoreCase("spid")){
            return 11;
        }

        // slow section
        if (parts_of_command[0].equalsIgnoreCase("slow") || parts_of_command[0].equalsIgnoreCase("snow")){
            return 12;
        }

        // ask for clarification if the command isn't clear.
        return -1;


    }

    private boolean list_contain(String[] lst, String target){
        for (String str : lst){
            if (str.equalsIgnoreCase(target)){
                return true;
            }
        }
        return false;
    }

    private boolean voice_command_execute(int command_key){
        /*
            This method parse the voice command.
            If the command key is -1, it will return false as the
            command isn't clear.
            Else, it will perform the desired command and will return
            true if the command executed successfully.
         */
        this.mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                speaker.keyToSpeech(command_key);
            }
        });
        // perform the command:
        switch (command_key){
            case -1:
                // return false, the command isn't clear!
                return false;
            case 0:
                // Takeoff
                UI_commands.takeoff();
                break;
            case 1:
                // Land
                UI_commands.land();
                break;
            case 2:
                // Forward
                UI_commands.forward();
                break;
            case 3:
                // Backward
                UI_commands.backward();
                break;
            case 4:
                // Turn left
                UI_commands.turn_left();
                break;
            case 5:
                // Turn right
                UI_commands.turn_right();
                break;
            case 6:
                // Yaw left
                UI_commands.yaw_left();
                break;
            case 7:
                // Yaw right
                UI_commands.yaw_right();
                break;
            case 8:
                // Up
                UI_commands.up();
                break;
            case 9:
                // Down
                UI_commands.down();
                break;
            case 10:
                // Stop
                UI_commands.stop();
                break;
            case 11:
                UI_commands.speedUp();
                break;
            case 12:
                UI_commands.slowDown();
                break;
            default:
                // Command not clear!
                return false;
        }
        return true;
    }


}
