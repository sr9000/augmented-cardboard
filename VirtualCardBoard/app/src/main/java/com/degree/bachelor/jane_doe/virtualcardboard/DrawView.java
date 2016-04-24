package com.degree.bachelor.jane_doe.virtualcardboard;

import android.content.Context;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by Jane-Doe on 4/24/2016.
 */
public class DrawView extends SurfaceView implements SurfaceHolder.Callback {

    //use thread for redraw activity
    private DrawThread drawThread;
    private CameraDemo cam;
    private BinocularView bv;
    private BinocularView.BinocularInfo bv_info;

    //says to surfaceholder that
    //DrawView control surface events
    public DrawView(Context context) {
        super(context);
        getHolder().addCallback(this);

        bv_info = new BinocularView.BinocularInfo();
        cam = new CameraDemo();
        bv = new BinocularView(getHolder().getSurfaceFrame().width(), getHolder().getSurfaceFrame().height());
        drawThread = new DrawThread(null, bv_info, cam);
        drawThread.start();
    }

    //overrided surface events
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        //hot changes
        bv.SetDisplaySizes(width, height);
        bv.SetCustomBinocularParams(2*width/3, height/2,1000, 1000);
        cam.StopPreview();
        cam.StartPreview(bv_info.simpleViewWidth, bv_info.simpleViewHeight);

        bv.CalcAdaptedViews(cam.getWidth(), cam.getHeight());
        bv_info.ImportFrom(bv.getBinocularInfo());

        drawThread.setHolder(holder);
        drawThread.setRunning(true);
    }

    //create thread on create view
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        bv.SetDisplaySizes(holder.getSurfaceFrame().width(), holder.getSurfaceFrame().height());
        bv.SetCustomBinocularParams(2*holder.getSurfaceFrame().width()/3, holder.getSurfaceFrame().height()/2,1000, 1000);
        cam.StartPreview(bv_info.simpleViewWidth, bv_info.simpleViewHeight);

        bv.CalcAdaptedViews(cam.getWidth(), cam.getHeight());
        bv_info.ImportFrom(bv.getBinocularInfo());

        drawThread.setHolder(holder);
        drawThread.setRunning(true);
    }

    //stopped thread when surface is destroyed
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        cam.StopPreview();
        drawThread.setRunning(false);
    }
}
