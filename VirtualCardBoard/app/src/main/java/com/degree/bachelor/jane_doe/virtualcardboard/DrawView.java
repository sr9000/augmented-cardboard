package com.degree.bachelor.jane_doe.virtualcardboard;

import android.content.Context;
import android.os.Vibrator;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.net.Inet4Address;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Created by Jane-Doe on 4/24/2016.
 */
public class DrawView extends SurfaceView implements
        SurfaceHolder.Callback
        , View.OnLongClickListener
{
    private DrawThread _drawThread;
    private CameraDemo _cameraDemo;
    private BinocularView _binocularView;
    private Context _context;

    private final float proportionFocusDistance = 2.0f/3.0f, proportionVerticalCoordinate = 0.5f;

    //says to surfaceholder that
    //DrawView control surface events
    public DrawView(Context context) {
        super(context);
        getHolder().addCallback(this);
        _context = context;

        _binocularView = new BinocularView(0, 0);
        _cameraDemo = new CameraDemo();
        _drawThread = new DrawThread();
        _drawThread.start();

        setOnLongClickListener(this);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        //hot changes
        _drawThread.SetRunning(false);
        _cameraDemo.StopPreview();

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

    @Override
    public boolean onLongClick(View view) {
        //TODO: send broadcast
        getHandler().post(new Runnable() {
            @Override
            public void run() {
                Vibrator _vibrator = (Vibrator) _context.getSystemService(Context.VIBRATOR_SERVICE);
                _vibrator.vibrate(new long[]{10, 100, 90, 100}, -1);
                Thread worker = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            new BroadcastSender(_context)
                                    .SendMessage(VC_Message
                                            .GetHelloMessage(
                                                    (Inet4Address)Inet4Address
                                                            .getByAddress(
                                                                new byte[]{1, 2, 3, 4})
                                                    , 1234
                                                    , "Hello World!!!"
                                            ));
                        } catch (SocketException e) {
                            e.printStackTrace();
                        } catch (UnknownHostException e) {
                            e.printStackTrace();
                        }
                    }
                });
                worker.start();
            }
        });
        return true;
    }
}
