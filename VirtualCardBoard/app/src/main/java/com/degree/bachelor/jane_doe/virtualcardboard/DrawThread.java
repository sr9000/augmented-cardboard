package com.degree.bachelor.jane_doe.virtualcardboard;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.SurfaceHolder;

/**
 * Created by Jane-Doe on 4/24/2016.
 */
//special draw thread
public class DrawThread extends PausableThread {
    private volatile SurfaceHolder _surfaceHolder;
    private volatile VirtualCardBoardState _virtualCardBoardState;

    private final Object changeLocker = new Object();

    private Paint _color1, _color2;

    public DrawThread()
    {
        _color1 = new Paint();
        _color2 = new Paint();

        _color1.setStyle(Paint.Style.FILL);
        _color2.setStyle(Paint.Style.FILL);

        _color1.setColor(Color.GREEN);
        _color2.setColor(Color.CYAN);
    }

    @Override
    protected void ProcessBody() {
        BinocularView.BinocularInfo binocularInfo = _virtualCardBoardState.GetBinocularInfo();
        CameraDemo cameraDemo = _virtualCardBoardState.GetCameraDemo();

        synchronized (changeLocker) {
            if (_surfaceHolder == null
                    || cameraDemo == null
                    || binocularInfo == null)
                return;

            Canvas canvas = null;

            try {
                //draw action here
                switch (_virtualCardBoardState.GetMode()) {
                    case NoPic: {
                        if (!cameraDemo.IsStarted()) break;

                        canvas = _surfaceHolder.lockCanvas();
                        if (canvas == null) break;
                        canvas.drawColor(Color.BLACK);

                        Bitmap captured = null;
                        captured = cameraDemo.GetCapturedBitmap();
                        if (captured == null) break;

                        canvas.drawBitmap(captured
                                , binocularInfo.adaptedLeftViewFrom
                                , binocularInfo.adaptedLeftViewWhere
                                , null);
                        canvas.drawBitmap(captured
                                , binocularInfo.adaptedRightViewFrom
                                , binocularInfo.adaptedRightViewWhere
                                , null);
                        break;
                    }
                    case Settings: {
                        canvas = _surfaceHolder.lockCanvas();
                        if (canvas == null) return;
                        canvas.drawColor(Color.BLACK);

                        //todo fix
                        canvas.drawRect(binocularInfo.leftViewWhere, _color1);
                        canvas.drawRect(binocularInfo.rightViewWhere, _color1);

                        canvas.drawRect(new Rect(
                                binocularInfo.leftViewWhere.left
                                , binocularInfo.leftCenterY
                                , binocularInfo.leftViewWhere.left + binocularInfo.leftCenterX
                                , binocularInfo.leftViewWhere.bottom
                        ), _color2);
                        canvas.drawRect(new Rect(
                                binocularInfo.leftViewWhere.left + binocularInfo.leftCenterX
                                , binocularInfo.leftViewWhere.top
                                , binocularInfo.leftViewWhere.right
                                , binocularInfo.leftCenterY
                        ), _color2);
                        canvas.drawRect(new Rect(
                                binocularInfo.rightViewWhere.left
                                , binocularInfo.rightCenterY
                                , binocularInfo.rightViewWhere.left + binocularInfo.rightCenterX
                                , binocularInfo.rightViewWhere.bottom
                        ), _color2);
                        canvas.drawRect(new Rect(
                                binocularInfo.rightViewWhere.left + binocularInfo.rightCenterX
                                , binocularInfo.rightViewWhere.top
                                , binocularInfo.rightViewWhere.right
                                , binocularInfo.rightCenterY
                        ), _color2);
                        break;
                    }
                    case Pic: {
                        if (!cameraDemo.IsStarted()) break;

                        canvas = _surfaceHolder.lockCanvas();
                        if (canvas == null) break;
                        canvas.drawColor(Color.BLACK);

                        Bitmap captured = null;
                        captured = cameraDemo.GetCapturedBitmap();
                        if (captured == null) break;

                        canvas.drawBitmap(captured
                                , binocularInfo.adaptedLeftViewFrom
                                , binocularInfo.adaptedLeftViewWhere
                                , null);
                        canvas.drawBitmap(captured
                                , binocularInfo.adaptedRightViewFrom
                                , binocularInfo.adaptedRightViewWhere
                                , null);
                        break;
                    }
                }

            } finally {
                if (canvas != null) {
                    _surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }

    public void SetObjects(SurfaceHolder holder, VirtualCardBoardState virtualCardBoardState) {
        synchronized (changeLocker) {
            _surfaceHolder = holder;
            _virtualCardBoardState = virtualCardBoardState;
        }
    }
}
