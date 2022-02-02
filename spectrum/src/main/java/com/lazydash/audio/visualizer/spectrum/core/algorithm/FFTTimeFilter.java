package com.lazydash.audio.visualizer.spectrum.core.algorithm;

import com.lazydash.audio.visualizer.spectrum.system.config.AppConfig;

import java.util.LinkedList;
import java.util.Queue;

public class FFTTimeFilter {
    private Queue<double[]> historyAmps = new LinkedList<>();


    public double[] filter(double[] amps) {
        int timeFilterSize = AppConfig.timeFilterSize;

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

        return switch (AppConfig.smoothnessType) {
            case "WMA" -> filterWma(amps);
            case "EMA" -> filterEma(amps);
            default -> filterSma(amps);
        };

    }

    private double[] filterSma(double[] amps) {
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

    private double[] filterEma(double[] amps) {
        double[] filtered = new double[amps.length];
        for (int i = 0; i<amps.length; i++){
            double nominator = 0;
            double denominator = 0;

            int exp = 1;
            for (double[] currentHistoryAmps : historyAmps){
                nominator = nominator + (currentHistoryAmps[i] * Math.pow(exp, exp));
                denominator = denominator + Math.pow(exp, exp);
                exp++;
            }

            filtered[i] = nominator / denominator;
        }

        return filtered;
    }

    private double[] filterWma(double[] amps) {
        double[] filtered = new double[amps.length];
        for (int i = 0; i<amps.length; i++){
            double nominator = 0;
            double denominator = 0;

            int weight = 1;
            for (double[] currentHistoryAmps : historyAmps){
                nominator = nominator + (currentHistoryAmps[i] * weight);
                denominator = denominator + weight;
                weight++;
            }

            filtered[i] = nominator / denominator;
        }

        return filtered;
    }

}
