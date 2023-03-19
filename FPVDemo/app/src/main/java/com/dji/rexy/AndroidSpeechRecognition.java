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
import android.widget.ImageButton;
import android.widget.TextView;
import java.util.ArrayList;

public class AndroidSpeechRecognition {

    private SpeechRecognizer speechRecognizer;
//    private TextView result;
    private ImageButton micButton;
    private Context context;
    private FlightCommandUI UI_commands;
    private T2S speaker;
    private Activity mainActivity;
    private boolean isRecording;
    private String language;
    final Intent speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
    public AndroidSpeechRecognition(Context init_context, ImageButton init_button, FlightCommandUI init_ui_commands, T2S init_speaker, Activity init_activity){
        this.context = init_context;
//        this.result = init_result;
        this.micButton = init_button;
        this.UI_commands = init_ui_commands;
        this.speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this.context);
        this.speaker = init_speaker;
        this.mainActivity = init_activity;
        this.isRecording = false;
        this.language = "he-IL";
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, this.language);
        init_listeners();
    }


    private void init_listeners(){
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {
//                micButton.setText(new String("Record"));
            }

            @Override
            public void onBeginningOfSpeech() {
//                micButton.setText(new String("Listening..."));
                micButton.setImageResource(R.drawable.recording_icon);
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
//                micButton.setText(new String("Record"));
                micButton.setImageResource(R.drawable.record_icon);
                ArrayList<String> data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
//                result.setText(data.get(0));
                int commandKey = -1;
                if (language.equalsIgnoreCase("en-US"))
                    commandKey = parseCommandEnglish(data.get(0));
                else
                    commandKey = parseCommandHebrew(data.get(0));
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

    private int parseCommandHebrew(String command) {
        String[] parts_of_command = command.split(" ");

        // TakeOff section
        if (list_contain(parts_of_command, "המראה") || list_contain(parts_of_command, "תמריא")){
            // Fully understood, user commands a Takeoff
            return 0;
        }

        // Land section
        if (list_contain(parts_of_command, "נחיתה") || list_contain(parts_of_command, "תנחת")){
            // Fully understood, user commands a Land
            return 1;
        }

        // Forward section
        if (list_contain(parts_of_command,"קדימה") || list_contain(parts_of_command, "ישר")){
            // Fully understood, the user commands moving Forward
            return 2;
        }

        // Backward section
        if (parts_of_command[0].equalsIgnoreCase("אחורה")){
            // Fully understood, the user commands moving Backward
            return 3;
        }

        // Left turn section
        if (list_contain(parts_of_command, "שמאלה") || list_contain(parts_of_command, "שמאל")){
            // Fully understood, the user commands a left turn
            return 4;
        }

        // Right turn section
        if (list_contain(parts_of_command,"ימינה") || list_contain(parts_of_command,"ימין")){
            // Fully understood, the user commands a right turn
            return 5;
        }

        // Yaw turn section
        if (list_contain(parts_of_command,"תסתובב") || list_contain(parts_of_command,"סתובב") || list_contain(parts_of_command,"סיבוב")){
            if (list_contain(parts_of_command,"שמאלה")){
                // Fully understood, the user commands a Yaw left turn
                return 6;
            }
            else if (list_contain(parts_of_command,"ימינה")){
                // Fully understood, the user commands a Yaw right turn
                return 7;
            }
            else{
                // command isn't clear, asking the user for clarifications
                return -1;
            }
        }

        // Up section
        if (list_contain(parts_of_command,"למעלה") || list_contain(parts_of_command, "תעלה")){
            // Fully understood, the user commands a right turn
            return 8;
        }

        // Down section
        if (list_contain(parts_of_command,"למטה") || list_contain(parts_of_command,"תרד")){
            // Fully understood, the user commands a right turn
            return 9;
        }

        // Stop section
        if (list_contain(parts_of_command,"תעצור") || list_contain(parts_of_command,"עצור")){
            // Fully understood, the user commands a right turn
            return 10;
        }

        // speed section
        if (list_contain(parts_of_command,"תמהר") || list_contain(parts_of_command,"מהר")){
            return 11;
        }

        // slow section
        if (list_contain(parts_of_command,"תאט") || list_contain(parts_of_command,"לאט")){
            return 12;
        }

        return -1;


    }

    public void recognition(){
        speechRecognizer.startListening(speechRecognizerIntent);
    }

    public int parseCommandEnglish(String command){

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
        if (list_contain(parts_of_command,"go") || list_contain(parts_of_command, "forward")){
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
        if (list_contain(parts_of_command,"down")){
            // Fully understood, the user commands a right turn
            return 9;
        }

        // Stop section
        if (list_contain(parts_of_command,"stop")){
            // Fully understood, the user commands a right turn
            return 10;
        }

        // speed section
        if (list_contain(parts_of_command,"speed") || list_contain(parts_of_command,"spid")){
            return 11;
        }

        // slow section
        if (list_contain(parts_of_command,"slow") || list_contain(parts_of_command,"snow")){
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