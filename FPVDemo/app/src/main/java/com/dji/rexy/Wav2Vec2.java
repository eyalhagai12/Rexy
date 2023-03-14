package com.dji.rexy;

import android.content.Context;

import org.pytorch.IValue;
import org.pytorch.LiteModuleLoader;
import org.pytorch.Module;
import org.pytorch.Tensor;

import java.nio.FloatBuffer;

public class Wav2Vec2 implements SpeechModel{

    private Module model = null;
    private LogCustom log;
    public Wav2Vec2(String model_path, Context appContext, LogCustom init_log){
        this.log = init_log;
        try {
            this.model = LiteModuleLoader.load(assetFilePath(appContext, model_path));
        }
        catch(Exception e){
            this.log.setDebug(e.getMessage());
        }
    }

    @Override
    public String transcribe(float[] inputBuffer) {
        double[] wav2vec2input = new double[RECORDING_LENGTH];
        for (int n = 0; n < RECORDING_LENGTH; n++)
            wav2vec2input[n] = inputBuffer[n];

        FloatBuffer inTensorBuffer = Tensor.allocateFloatBuffer(RECORDING_LENGTH);
        for (double val : wav2vec2input)
            inTensorBuffer.put((float)val);

        Tensor inTensor = Tensor.fromBlob(inTensorBuffer, new long[]{1, RECORDING_LENGTH});
        final String result = model.forward(IValue.from(inTensor)).toStr();
        return result;
    }
}
