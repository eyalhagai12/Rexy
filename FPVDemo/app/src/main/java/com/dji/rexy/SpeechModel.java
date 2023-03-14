package com.dji.rexy;

import org.pytorch.Module;

public interface SpeechModel {
    Module model = null;

    public String transcribe(float[] inputBuffer);

}
