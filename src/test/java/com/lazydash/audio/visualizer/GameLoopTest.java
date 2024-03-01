package com.lazydash.audio.visualizer;

import java.util.concurrent.locks.LockSupport;

public class GameLoopTest {

    public static void main(String[] args) throws InterruptedException {

        LoopWorker loopWorker = new LoopWorker(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(Math.round(Math.random() * (500d / 60d)));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, 60);
        loopWorker.start();

        Thread.sleep(30 * 1000);

        loopWorker.setTargetFps(30);

        Thread.sleep(30 * 1000);

        loopWorker.finish();
        loopWorker.start();

        Thread.sleep(2 * 1000);

    }

    public static class LoopWorker {
        private boolean running = false;
        private boolean isFinished = false;
        private Thread thread;
        private Runnable runnable;

        private int targetFps = 60;

        public LoopWorker(Runnable runnable, int targetFps) {
            this.runnable = runnable;
            this.targetFps = targetFps;
        }

        public int getTargetFps() {
            return targetFps;
        }

        public void setTargetFps(int targetFps) {
            this.targetFps = targetFps;
        }

        public void start() {
            if (!isFinished) {
                running = true;
                thread = new Thread(() -> {
                    long oldTime = System.nanoTime();
                    while (running) {
                        long newTime = System.nanoTime();
                        long deltaTime = newTime - oldTime;
                        oldTime = newTime;
                        System.out.println(String.format("%.2f", deltaTime / 1000000d));

                        long loopStart = System.nanoTime();

                        runnable.run();

                        long loopEnd = System.nanoTime();
                        long deltaLoopTime = loopEnd - loopStart;

                        long sleepNs = Math.round(((1000d / targetFps) * 1000000) - (deltaLoopTime));
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
            runnable = null;
        }
    }
}
