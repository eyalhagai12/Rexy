package com.dji.rexy;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.widget.Button;
import android.widget.TextView;
import java.util.ArrayList;

public class AndroidSpeechRecognition {

    private SpeechRecognizer speechRecognizer;
    private TextView result;
    private Button micButton;
    private Context context;
    private FlightCommandUI UI_commands;
    private T2S speaker;
    private Parser parser;
    private Activity mainActivity;
    private boolean isRecording;
    private String language;
    final Intent speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
    public AndroidSpeechRecognition(TextView init_result, Context init_context, Button init_button, FlightCommandUI init_ui_commands, T2S init_speaker, Activity init_activity){
        this.context = init_context;
        this.result = init_result;
        this.micButton = init_button;
        this.UI_commands = init_ui_commands;
        this.speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this.context);
        this.speaker = init_speaker;
        this.mainActivity = init_activity;
        init_params();
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, this.language);
        init_listeners();
    }

    private void init_params(){
        this.isRecording = false;
        this.language = "he-IL";
        this.parser = new ParserMultiLanguage();
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
                int commandKey = -1;
                if (language.equalsIgnoreCase("en-US"))
                    commandKey = parser.parseCommandEnglish(data.get(0));
                else
                    commandKey = parser.parseCommandHebrew(data.get(0));
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
        speechRecognizer.startListening(speechRecognizerIntent);
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
                speaker.keyToSpeechHebrew(command_key);
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
