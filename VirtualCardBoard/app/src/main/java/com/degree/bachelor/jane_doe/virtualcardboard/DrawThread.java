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
    private volatile CameraDemo _cameraDemo;
    private volatile BinocularView.BinocularInfo _binocularInfo;
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
                switch (_virtualCardBoardState.GetMode()) {
                    case NoPic:
                        canvas.drawBitmap(captured, _binocularInfo.adaptedLeftViewFrom, _binocularInfo.adaptedLeftViewWhere, null);
                        canvas.drawBitmap(captured, _binocularInfo.adaptedRightViewFrom, _binocularInfo.adaptedRightViewWhere, null);
                        break;
                    case Settings:
                        canvas.drawColor(Color.BLACK);

                        canvas.drawRect(_binocularInfo.adaptedLeftViewWhere, _color1);
                        canvas.drawRect(_binocularInfo.adaptedRightViewWhere, _color1);

                        canvas.drawRect(new Rect(
                                _binocularInfo.adaptedLeftViewWhere.left
                                , _binocularInfo.leftCenterY
                                , _binocularInfo.adaptedLeftViewWhere.left + _binocularInfo.leftCenterX
                                , _binocularInfo.adaptedLeftViewWhere.bottom
                                ), _color2);
                        canvas.drawRect(new Rect(
                                _binocularInfo.adaptedLeftViewWhere.left + _binocularInfo.leftCenterX
                                , _binocularInfo.adaptedLeftViewWhere.top
                                , _binocularInfo.adaptedLeftViewWhere.right
                                , _binocularInfo.leftCenterY
                        ), _color2);
                        canvas.drawRect(new Rect(
                                _binocularInfo.adaptedRightViewWhere.left
                                , _binocularInfo.rightCenterY
                                , _binocularInfo.adaptedRightViewWhere.left + _binocularInfo.rightCenterX
                                , _binocularInfo.adaptedRightViewWhere.bottom
                        ), _color2);
                        canvas.drawRect(new Rect(
                                _binocularInfo.adaptedRightViewWhere.left + _binocularInfo.rightCenterX
                                , _binocularInfo.adaptedRightViewWhere.top
                                , _binocularInfo.adaptedRightViewWhere.right
                                , _binocularInfo.rightCenterY
                        ), _color2);
                        break;
                    case Pic:
                        canvas.drawBitmap(captured, _binocularInfo.adaptedLeftViewFrom, _binocularInfo.adaptedLeftViewWhere, null);
                        canvas.drawBitmap(captured, _binocularInfo.adaptedRightViewFrom, _binocularInfo.adaptedRightViewWhere, null);
                        break;
                }

            } finally {
                if (canvas != null) {
                    _surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }

    public void SetObjects(SurfaceHolder holder, CameraDemo cameraDemo, BinocularView.BinocularInfo binocularInfo, VirtualCardBoardState virtualCardBoardState) {
        synchronized (changeLocker) {
            _cameraDemo = cameraDemo;
            _surfaceHolder = holder;
            _binocularInfo = binocularInfo;
            _virtualCardBoardState = virtualCardBoardState;
        }
    }
}
