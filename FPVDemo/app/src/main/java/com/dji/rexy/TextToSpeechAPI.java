package com.dji.rexy;
import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;

public class TextToSpeechAPI implements T2S{

    private TextToSpeech speaker;
    private Context appContext;
    private String language;

    public TextToSpeechAPI(Context init_appContext){
        this.appContext = init_appContext;
        this.language = "he-IL";
        speaker = new TextToSpeech(this.appContext, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    speaker.setLanguage(Locale.forLanguageTag(language));
                }
                else{
                    Log.e("T2S", "Text to Speech engine initialization failed");
                }
            }
        });
    }

    public void speak(String textToSpeak){
        this.speaker.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null, "utteranceId");
    }

    public void keyToSpeech(int commandKey){
        switch (commandKey){
            case 0:
                // Takeoff
                speak("Performing Takeoff");
                break;
            case 1:
                // Land
                speak("Performing Land");
                break;
            case 2:
                // Forward
                speak("Moving Forward");
                break;
            case 3:
                // Backward
                speak("Moving Backward");
                break;
            case 4:
                // Turn left
                speak("Moving Left");
                break;
            case 5:
                // Turn right
                speak("Moving Right");
                break;
            case 6:
                // Yaw left
                speak("Turning Left");
                break;
            case 7:
                // Yaw right
                speak("Turning Right");
                break;
            case 8:
                // Up
                speak("Going Up");
                break;
            case 9:
                // Down
                speak("Going Down");
                break;
            case 10:
                // Stop
                speak("Stopping");
                break;
            case 11:
                speak("Speeding");
                break;
            case 12:
                speak("Slowing down");
                break;
            case -1:
                speak("Can't understand your request");
                break;

        }
    }
    public void keyToSpeechHebrew(int commandKey){
        switch (commandKey){
            case 0:
                // Takeoff
                speak("מבצע המראה");
                break;
            case 1:
                // Land
                speak("מבצע נחיתה");
                break;
            case 2:
                // Forward
                speak("טס קדימה");
                break;
            case 3:
                // Backward
                speak("טס אחורה");
                break;
            case 4:
                // Turn left
                speak("טס שמאלה");
                break;
            case 5:
                // Turn right
                speak("טס ימינה");
                break;
            case 6:
                // Yaw left
                speak("מסתובב שמאלה");
                break;
            case 7:
                // Yaw right
                speak("מסתובב ימינה");
                break;
            case 8:
                // Up
                speak("עולה למעלה");
                break;
            case 9:
                // Down
                speak("יורד למטה");
                break;
            case 10:
                // Stop
                speak("עוצר");
                break;
            case 11:
                speak("מאיץ");
                break;
            case 12:
                speak("מאט");
                break;
            case -1:
                speak("לא ניתן להבין את הפקודה");
                break;

        }
    }

}
