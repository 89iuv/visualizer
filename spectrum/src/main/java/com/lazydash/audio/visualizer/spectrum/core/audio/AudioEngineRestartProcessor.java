package com.lazydash.audio.visualizer.spectrum.core.audio;

import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import com.lazydash.audio.visualizer.spectrum.system.config.AppConfig;

public class AudioEngineRestartProcessor implements AudioProcessor {
    private String inputDevice = AppConfig.getInputDevice();
    private String outputDevice = AppConfig.getOutputDevice();
    private int sampleRate = AppConfig.getSampleRate();
    private int bufferSize = AppConfig.getBufferPadding();
    private int bufferOverlap = AppConfig.getBufferSize();

    private AudioEngine audioEngine;


    public AudioEngineRestartProcessor(AudioEngine audioEngine) {
        this.audioEngine = audioEngine;
    }

    @Override
    public boolean process(AudioEvent audioEvent) {
        if (isChangeDetected()) {
            // use another thread to restart the audio engine
            Thread thread = new Thread(() -> audioEngine.restart());
            thread.setDaemon(true);
            thread.start();
        }

        return true;
    }

    @Override
    public void processingFinished() {

    }

    private boolean isChangeDetected(){
        boolean isChanged = false;
        if (!inputDevice.equals(AppConfig.getInputDevice())){
            inputDevice = AppConfig.getInputDevice();
            isChanged = true;
        }

        if (!outputDevice.equals(AppConfig.getOutputDevice())){
            inputDevice = AppConfig.getOutputDevice();
            isChanged = true;
        }

        if (sampleRate != AppConfig.getSampleRate()) {
            sampleRate = AppConfig.getSampleRate();
            isChanged = true;
        }

        if (bufferSize != AppConfig.getBufferPadding()) {
            bufferSize = AppConfig.getBufferPadding();
            isChanged = true;
        }

        if (bufferOverlap != AppConfig.getBufferSize()) {
            bufferOverlap = AppConfig.getBufferSize();
            isChanged = true;
        }

        return isChanged;
    }
}
