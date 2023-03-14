package com.dji.rexy;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;


public class SpeechRecognition {

    private Context context;
    private SpeechModel model;
    private LogCustom log;
    public SpeechRecognition(Context new_context, LogCustom init_log){
        this.context = new_context;
        this.log = init_log;
        this.model = new Wav2Vec2("wav2vec2_large.ptl", this.context, this.log);
    }

    public String recognize(float[] floatInputBuffer)  {
        return this.model.transcribe(floatInputBuffer);
    }

    public float[] recordAudio(){
        int bufferSize = AudioRecord.getMinBufferSize(this.model.SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        AudioRecord record = new AudioRecord(MediaRecorder.AudioSource.DEFAULT, this.model.SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT,
                bufferSize);

        if (record.getState() != AudioRecord.STATE_INITIALIZED) {
            return null;
        }

        record.startRecording();
        log.setDebug("Started recording");
        long shortsRead = 0;
        int recordingOffset = 0;
        short[] audioBuffer = new short[bufferSize / 2];
        short[] recordingBuffer = new short[this.model.RECORDING_LENGTH];

        while (shortsRead < this.model.RECORDING_LENGTH) {
            int numberOfShort = record.read(audioBuffer, 0, audioBuffer.length);
            shortsRead += numberOfShort;
            System.arraycopy(audioBuffer, 0, recordingBuffer, recordingOffset, numberOfShort);
            recordingOffset += numberOfShort;
        }

        record.stop();
        record.release();

        float[] floatInputBuffer = new float[this.model.RECORDING_LENGTH];

        // feed in float values between -1.0f and 1.0f by dividing the signed 16-bit inputs.
        for (int i = 0; i < this.model.RECORDING_LENGTH; ++i) {
            floatInputBuffer[i] = recordingBuffer[i] / (float)Short.MAX_VALUE;
        }
        return floatInputBuffer;
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


}
