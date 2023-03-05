package com.dji.rexy;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import org.pytorch.IValue;
import org.pytorch.LiteModuleLoader;
import org.pytorch.Module;
import org.pytorch.Tensor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.FloatBuffer;
import java.util.List;

public class SpeechRecognition {

    private Context context;
    private Module module = null;
    private final static int REQUEST_RECORD_AUDIO = 13;
    private final static int AUDIO_LEN_IN_SECOND = 6;
    private final static int SAMPLE_RATE = 16000;
    private final static int RECORDING_LENGTH = SAMPLE_RATE * AUDIO_LEN_IN_SECOND;

    public SpeechRecognition(Context new_context){
        this.context = new_context;
    }

    public String assetFilePath(Context context, String assetName) {
        File file = new File(context.getFilesDir(), assetName);
        if (file.exists() && file.length() > 0) {
            return file.getAbsolutePath();
        }

        try (InputStream is = context.getAssets().open(assetName)) {
            try (OutputStream os = new FileOutputStream(file)) {
                byte[] buffer = new byte[4 * 1024];
                int read;
                while ((read = is.read(buffer)) != -1) {
                    os.write(buffer, 0, read);
                }
                os.flush();
            }
            return file.getAbsolutePath();
        } catch (IOException e) {
            Log.e("Speech Recognition", assetName + ": " + e.getLocalizedMessage());
        }
        return null;
    }

    public String recognize(float[] floatInputBuffer)  {
        if (module == null) {
            module = LiteModuleLoader.load(assetFilePath(context, "wav2vec2.ptl"));
        }

        double wav2vecinput[] = new double[RECORDING_LENGTH];
        for (int n = 0; n < RECORDING_LENGTH; n++)
            wav2vecinput[n] = floatInputBuffer[n];

        FloatBuffer inTensorBuffer = Tensor.allocateFloatBuffer(RECORDING_LENGTH);
        for (double val : wav2vecinput)
            inTensorBuffer.put((float)val);

        Tensor inTensor = Tensor.fromBlob(inTensorBuffer, new long[]{1, RECORDING_LENGTH});
        final String result = module.forward(IValue.from(inTensor)).toStr();
        return result;
    }

    public int parseCommand(String command){

        String[] parts_of_command = command.split(" ");

        // TakeOff section
        if (parts_of_command[0].equalsIgnoreCase("takeoff")){
            // Fully understood, user commands a Takeoff
            return 0;
        }
        else if (parts_of_command[0].equalsIgnoreCase("take")){
            if (parts_of_command[1].equalsIgnoreCase("off")){
                // Partly understood, user commands a Takeoff
                return 0;
            }
            else{
                // The command isn't clear, will ask for a clarification from the user.
                return -1;
            }
        }

        // Land section
        if (parts_of_command[0].equalsIgnoreCase("land")){
            return 1;
        }

        // Forward section
        if (parts_of_command[0].equalsIgnoreCase("forward")){
            // Fully understood, the user commands moving Forward
            return 2;
        }
        else if (parts_of_command[0].equalsIgnoreCase("for")){
            if (parts_of_command[1].equalsIgnoreCase("ward")){
                // Partly understood, the user commands moving Forward
                return 2;
            }
            else{
                // The command isn't clear, asking for clarifications.
                return -1;
            }
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
            else{
                // The command isn't clear, asking for clarifications.
                return -1;
            }
        }

        // Left turn section
        if (parts_of_command[0].equalsIgnoreCase("left")){
            // Fully understood, the user commands a left turn
            return 4;
        }

        // Right turn section
        if (parts_of_command[0].equalsIgnoreCase("right")){
            // Fully understood, the user commands a right turn
            return 5;
        }

        // Yaw turn section
        if (parts_of_command[0].equalsIgnoreCase("yaw")){
            if (parts_of_command[1].equalsIgnoreCase("left")){
                // Fully understood, the user commands a Yaw left turn
                return 6;
            }
            else if (parts_of_command[1].equalsIgnoreCase("right")){
                // Fully understood, the user commands a Yaw right turn
                return 7;
            }
            else{
                // command isn't clear, asking the user for clarifications
                return -1;
            }
        }

        // Up section
        if (parts_of_command[0].equalsIgnoreCase("up")){
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

        // ask for clarification if the command isn't clear.
        return -1;



    }


}
