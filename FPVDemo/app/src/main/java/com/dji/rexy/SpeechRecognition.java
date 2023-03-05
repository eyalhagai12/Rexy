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



}
