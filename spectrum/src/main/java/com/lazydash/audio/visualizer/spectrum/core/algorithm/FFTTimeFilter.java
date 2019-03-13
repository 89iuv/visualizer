package com.lazydash.audio.visualizer.spectrum.core.algorithm;

import com.lazydash.audio.visualizer.spectrum.system.config.AppConfig;

import java.util.LinkedList;
import java.util.Queue;

public class FFTTimeFilter {
    private Queue<double[]> historyAmps = new LinkedList<>();

    public double[] filter(double[] amps) {
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

        double[] filtered = new double[amps.length];
        for (int i = 0; i<amps.length; i++){
            double sumTimeFilteredAmp = 0;
            for (double[] currentHistoryAmps : historyAmps){
                sumTimeFilteredAmp = sumTimeFilteredAmp + currentHistoryAmps[i];
            }
            filtered[i] = sumTimeFilteredAmp / historyAmps.size();
        }

        return filtered;
    }

}
