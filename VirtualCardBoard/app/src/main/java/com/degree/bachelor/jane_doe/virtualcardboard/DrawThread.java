package com.degree.bachelor.jane_doe.virtualcardboard;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.view.SurfaceHolder;

/**
 * Created by Jane-Doe on 4/24/2016.
 */
//special draw thread
public class DrawThread extends Thread {
    private boolean _running = false;

    private SurfaceHolder _surfaceHolder;
    private BinocularView.BinocularInfo _bv_info;
    private CameraDemo _cam;

    private Object lock = new Object();

    private Paint pLeft, pRight;

    //get surface on thread create
    public DrawThread(@Nullable SurfaceHolder surfaceHolder, BinocularView.BinocularInfo bv_info, CameraDemo cam) {
        _bv_info = bv_info;
        _cam = cam;
        this._surfaceHolder = surfaceHolder;
        pLeft = new Paint();
        pLeft.setStyle(Paint.Style.FILL);
        pLeft.setColor(Color.GREEN);

        pRight = new Paint();
        pRight.setStyle(Paint.Style.FILL);
        pRight.setColor(Color.RED);
    }

    //on start thread
    @Override
    public void run() {
        Canvas canvas;
        while (true) {
            if (!_running) {
                synchronized (lock) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        return;
                    }
                }
                continue;
            }

            if (_surfaceHolder == null)
                continue;
            Bitmap captured = _cam.GetCapturedBitmap();
            if (captured == null)
                continue;
            canvas = null;
            try {
                canvas = _surfaceHolder.lockCanvas(null);
                if (canvas == null)
                    continue;
                //draw action here
                canvas.drawBitmap(captured, _bv_info.adaptedLeftViewFrom, _bv_info.adaptedLeftViewWhere, null);
                canvas.drawBitmap(captured, _bv_info.adaptedRightViewFrom, _bv_info.adaptedRightViewWhere, null);

            } finally {
                if (canvas != null) {
                    _surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }

    public void SetHolder(SurfaceHolder holder) {
        _surfaceHolder = holder;
    }

    public void SetRunning(boolean running) {
        _running = running;
        synchronized (lock) {
            lock.notify();
        }
    }
}
