package com.dji.rexy;

import android.content.Context;

import org.pytorch.IValue;
import org.pytorch.LiteModuleLoader;
import org.pytorch.Module;
import org.pytorch.Tensor;

import java.nio.FloatBuffer;

public class Wav2Vec2 implements SpeechModel{

    private Module model = null;

    public Wav2Vec2(String model_path, Context appContext){
        this.model  = LiteModuleLoader.load(assetFilePath(appContext, model_path));
    }

    @Override
    public String transcribe(float[] inputBuffer) {
        double wav2vecinput[] = new double[RECORDING_LENGTH];
        for (int n = 0; n < RECORDING_LENGTH; n++)
            wav2vecinput[n] = inputBuffer[n];

        FloatBuffer inTensorBuffer = Tensor.allocateFloatBuffer(RECORDING_LENGTH);
        for (double val : wav2vecinput)
            inTensorBuffer.put((float)val);

        Tensor inTensor = Tensor.fromBlob(inTensorBuffer, new long[]{1, RECORDING_LENGTH});
        final String result = model.forward(IValue.from(inTensor)).toStr();
        return result;
    }
}
