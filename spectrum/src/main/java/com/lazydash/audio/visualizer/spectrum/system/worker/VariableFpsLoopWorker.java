package com.lazydash.audio.visualizer.spectrum.system.worker;

import java.util.concurrent.locks.LockSupport;

public abstract class VariableFpsLoopWorker {
    private boolean running = false;
    private boolean isFinished = false;
    private Thread thread;

    public abstract int getTargetFps();

    public abstract void run();

    public void start() {
        if (!isFinished) {
            running = true;
            thread = new Thread(() -> {
                while (running) {
                   long loopStart = System.nanoTime();

                    run();

                    long loopEnd = System.nanoTime();
                    long deltaLoopTime = loopEnd - loopStart;

                    long sleepNs = Math.round(((1000d / getTargetFps()) * 1000000) - (deltaLoopTime));
                    if (sleepNs < 0) {
                        sleepNs = 0;
                    }

                    LockSupport.parkNanos(sleepNs);
                }
            });
            thread.setDaemon(true);
            thread.start();
        }
    }

    public void finish() {
        running = false;
        isFinished = true;
        thread = null;
    }
}
