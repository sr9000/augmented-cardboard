package com.degree.bachelor.jane_doe.virtualcardboard;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.SurfaceHolder;

/**
 * Created by Jane-Doe on 4/24/2016.
 */
//special draw thread
public class DrawThread extends PausableThread {
    private volatile SurfaceHolder _surfaceHolder;
    private volatile CameraDemo _cameraDemo;
    private volatile BinocularView.BinocularInfo _binocularInfo;

    private final Object changeLocker = new Object();

    public DrawThread() {}

    @Override
    protected void ProcessBody() {
        synchronized (changeLocker) {
            if (_surfaceHolder == null
                    || _binocularInfo == null
                    || _cameraDemo == null)
                return;

            if (!_cameraDemo.IsStarted()) return;

            Bitmap captured = null;
            Canvas canvas = null;

            captured = _cameraDemo.GetCapturedBitmap();
            if (captured == null) return;

            try {
                canvas = _surfaceHolder.lockCanvas();
                if (canvas == null) return;

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

    public void SetObjects(SurfaceHolder holder, CameraDemo cameraDemo, BinocularView.BinocularInfo binocularInfo) {
        synchronized (changeLocker) {
            _cameraDemo = cameraDemo;
            _surfaceHolder = holder;
            _binocularInfo = binocularInfo;
        }
    }
}
