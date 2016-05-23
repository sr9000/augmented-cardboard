package com.degree.bachelor.jane_doe.virtualcardboard;

/**
 * Created by Jane-Doe on 5/23/2016.
 */
public abstract class PausableThread extends Thread {
    private volatile boolean _running = false;
    private final Object pauseLocker = new Object();

    protected abstract void ProcessBody();

    @Override
    public void run() {
        while (true) {
            synchronized (pauseLocker) {
                if (!_running) {
                    try {
                        pauseLocker.wait();
                    } catch (InterruptedException e) {
                        return;
                    }
                    continue;
                }
            }

            ProcessBody();
        }
    }

    public void SetRunning(boolean running) {
        synchronized (pauseLocker) {
            _running = running;
            pauseLocker.notify();
        }
    }
}
