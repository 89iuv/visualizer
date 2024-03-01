package com.lazydash.audio.visualizer.core.audio;

import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import com.lazydash.audio.visualizer.system.config.AppConfig;

public class AudioEngineRestartProcessor implements AudioProcessor {
    private String inputDevice = AppConfig.inputDevice;
    private int sampleRate = AppConfig.sampleRate;
    private int audioWindowSize = AppConfig.audioWindowSize;
    private int audioWindowNumber = AppConfig.audioWindowNumber;

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
        if (!inputDevice.equals(AppConfig.inputDevice)){
            inputDevice = AppConfig.inputDevice;
            isChanged = true;
        }

        if (sampleRate != AppConfig.sampleRate) {
            sampleRate = AppConfig.sampleRate;
            isChanged = true;
        }

        if (audioWindowSize != AppConfig.audioWindowSize) {
            audioWindowSize = AppConfig.audioWindowSize;
            isChanged = true;
        }

        if (audioWindowNumber != AppConfig.audioWindowNumber) {
            audioWindowNumber = AppConfig.audioWindowNumber;
            isChanged = true;
        }

        return isChanged;
    }
}
