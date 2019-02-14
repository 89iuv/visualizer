package com.lazydash.audio.spectrum.core.algorithm;

import com.lazydash.audio.spectrum.system.config.AppConfig;

import java.util.LinkedList;
import java.util.Queue;

public class FFTTimeFilter {
    private Queue<float[]> historyAmps = new LinkedList<>();

    public float[] filter(float[] amps) {
        int timeFilterSize = AppConfig.getTimeFilterSize();

        if (timeFilterSize < 2) {
            return amps;
        }

        if (historyAmps.peek() != null && historyAmps.peek().length != amps.length) {
            historyAmps.clear();
        }

        if (historyAmps.size() < timeFilterSize) {
            historyAmps.offer(amps);
            return amps;
        }

        while (historyAmps.size() > timeFilterSize) {
            historyAmps.poll();
        }

        historyAmps.poll();
        historyAmps.offer(amps);

        float[] filtered = new float[amps.length];
        for (int i = 0; i<amps.length; i++){
            float sumTimeFilteredAmp = 0;
            for (float[] currentHistoryAmps : historyAmps){
                sumTimeFilteredAmp = sumTimeFilteredAmp + currentHistoryAmps[i];
            }
            filtered[i] = sumTimeFilteredAmp / historyAmps.size();
        }

        return filtered;
    }

}
