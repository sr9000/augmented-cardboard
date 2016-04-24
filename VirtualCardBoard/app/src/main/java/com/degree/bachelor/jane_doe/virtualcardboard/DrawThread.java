package com.degree.bachelor.jane_doe.virtualcardboard;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.SurfaceHolder;

/**
 * Created by Jane-Doe on 4/24/2016.
 */
//special draw thread
public class DrawThread extends Thread {
    private boolean _running = false;

    private SurfaceHolder _surfaceHolder;
    private CameraDemo _cameraDemo;
    private BinocularView.BinocularInfo _binocularInfo;

    private final Object pauseLocker = new Object();
    private final Object changeLocker = new Object();

    public DrawThread() {}

    @Override
    public void run() {
        //thread drawing cycle
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

            synchronized (changeLocker) {
                if (_surfaceHolder == null
                        || _binocularInfo == null
                        || _cameraDemo == null)
                    continue;

                if (!_cameraDemo.IsStarted()) continue;

                Bitmap captured = null;
                Canvas canvas = null;

                captured = _cameraDemo.GetCapturedBitmap();
                if (captured == null) continue;

                try {
                    canvas = _surfaceHolder.lockCanvas();
                    if (canvas == null) continue;

                    //draw action here
                    canvas.drawBitmap(captured, _binocularInfo.adaptedLeftViewFrom, _binocularInfo.adaptedLeftViewWhere, null);
                    canvas.drawBitmap(captured, _binocularInfo.adaptedRightViewFrom, _binocularInfo.adaptedRightViewWhere, null);

                } finally {
                    if (canvas != null) {
                        _surfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }
            }
        }
    }

    public void SetObjects(SurfaceHolder holder, CameraDemo cameraDemo, BinocularView.BinocularInfo binocularInfo) {
        synchronized (changeLocker) {
            _cameraDemo = cameraDemo;
            _surfaceHolder = holder;
            _binocularInfo = binocularInfo;
        }
    }

    public void SetRunning(boolean running) {
        synchronized (pauseLocker) {
            _running = running;
            pauseLocker.notify();
        }
    }
}
