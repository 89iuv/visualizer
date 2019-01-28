package com.lazydash.audio.visualiser.core.audio;

import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import com.lazydash.audio.visualiser.system.config.AppConfig;

public class AudioEngineRestartProcessor implements AudioProcessor {
    private String inputDevice = AppConfig.getInputDevice();
    private int sampleRate = AppConfig.getSampleRate();
    private int bufferSize = AppConfig.getBufferSize();
    private int bufferOverlap = AppConfig.getBufferOverlap();

    private TarsosAudioEngine tarsosAudioEngine;


    public AudioEngineRestartProcessor(TarsosAudioEngine tarsosAudioEngine) {
        this.tarsosAudioEngine = tarsosAudioEngine;
    }

    @Override
    public boolean process(AudioEvent audioEvent) {
        if (isChangeDetected()) {
            // use another thread to restart the audio engine
            Thread thread = new Thread(() -> tarsosAudioEngine.restart());
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

        if (sampleRate != AppConfig.getSampleRate()) {
            sampleRate = AppConfig.getSampleRate();
            isChanged = true;
        }

        if (bufferSize != AppConfig.getBufferSize()) {
            bufferSize = AppConfig.getBufferSize();
            isChanged = true;
        }

        if (bufferOverlap != AppConfig.getBufferOverlap()) {
            bufferOverlap = AppConfig.getBufferOverlap();
            isChanged = true;
        }

        return isChanged;
    }
}
