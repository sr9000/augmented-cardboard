package com.degree.bachelor.jane_doe.virtualcardboard;

import android.content.Context;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by Jane-Doe on 4/24/2016.
 */
public class DrawView extends SurfaceView implements SurfaceHolder.Callback {
    private DrawThread _drawThread;
    private CameraDemo _cameraDemo;
    private BinocularView _binocularView;

    private final float proportionFocusDistance = 2.0f/3.0f, proportionVerticalCoordinate = 0.5f;

    //says to surfaceholder that
    //DrawView control surface events
    public DrawView(Context context) {
        super(context);
        getHolder().addCallback(this);

        _binocularView = new BinocularView(0, 0);
        _cameraDemo = new CameraDemo();
        _drawThread = new DrawThread();
        _drawThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        //hot changes
        _binocularView.SetDisplaySizes(width, height);
        _binocularView.SetCustomBinocularParams(((int)(width*proportionFocusDistance)), ((int)(height*proportionVerticalCoordinate)), width, height);
        BinocularView.BinocularInfo info = _binocularView.GetBinocularInfo();
        _cameraDemo.StopPreview();
        _cameraDemo.StartPreview(info.simpleViewWidth, info.simpleViewHeight);

        _binocularView.CalcAdaptedViews(_cameraDemo.GetWidth(), _cameraDemo.GetHeight());
        info = _binocularView.GetBinocularInfo();

        _drawThread.SetObjects(holder, _cameraDemo, info);
        _drawThread.SetRunning(true);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        int width = holder.getSurfaceFrame().width();
        int height = holder.getSurfaceFrame().height();
        _binocularView.SetDisplaySizes(width, height);
        _binocularView.SetCustomBinocularParams(((int)(width*proportionFocusDistance)), ((int)(height*proportionVerticalCoordinate)), width, height);
        BinocularView.BinocularInfo info = _binocularView.GetBinocularInfo();
        _cameraDemo.StartPreview(info.simpleViewWidth, info.simpleViewHeight);

        _binocularView.CalcAdaptedViews(_cameraDemo.GetWidth(), _cameraDemo.GetHeight());
        info = _binocularView.GetBinocularInfo();

        _drawThread.SetObjects(holder, _cameraDemo, info);
        _drawThread.SetRunning(true);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        _drawThread.SetRunning(false);
        _cameraDemo.StopPreview();
    }
}
