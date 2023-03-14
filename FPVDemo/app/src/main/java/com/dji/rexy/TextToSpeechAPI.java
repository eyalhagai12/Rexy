package com.dji.rexy;
import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;

public class TextToSpeechAPI {

    private TextToSpeech speaker;
    private Context appContext;

    public TextToSpeechAPI(Context init_appContext){
        this.appContext = init_appContext;
        speaker = new TextToSpeech(this.appContext, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    speaker.setLanguage(Locale.US);
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

        }
    }
}
