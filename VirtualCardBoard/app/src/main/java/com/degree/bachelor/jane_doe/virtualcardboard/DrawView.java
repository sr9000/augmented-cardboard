package com.degree.bachelor.jane_doe.virtualcardboard;

import android.content.Context;
import android.os.Vibrator;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.degree.bachelor.jane_doe.virtualcardboard.network.PcInterface;

/**
 * Created by Jane-Doe on 4/24/2016.
 */
public class DrawView extends SurfaceView implements
        SurfaceHolder.Callback
{
    private DrawThread _drawThread;

    private Context _context;
    private PcInterface _pcInterface;
    private VirtualCardBoardState _virtualCardBoardState;
    private GestureDetector _gestureDetector;

    //says to surfaceholder that
    //DrawView control surface events
    public DrawView(Context context) {
        super(context);
        getHolder().addCallback(this);

        _context = context;
        _virtualCardBoardState = new VirtualCardBoardState(_context, this);
        _pcInterface = new PcInterface(_context, _virtualCardBoardState);

        _drawThread = new DrawThread();
        _drawThread.start();

        _gestureDetector = new GestureDetector(
                _context
                , new GestureDoubleTapListener(
                    new Runnable() { @Override public void run() { _SetModeToNoPic(); } }
                    , new Runnable() { @Override public void run() { _LongClickAction(); } } )
        );
    }

    private void _SetModeToNoPic() {
        _virtualCardBoardState.SetMode(VirtualCardBoardState.Mode.NoPic);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        //hot changes
        _drawThread.SetRunning(false);
        _virtualCardBoardState.UpdateSurfaceSizes(width, height);

        _drawThread.SetObjects(holder, _virtualCardBoardState);

        _drawThread.SetRunning(true);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        _virtualCardBoardState.InitSurfaceSizes(holder.getSurfaceFrame().width(), holder.getSurfaceFrame().height());

        _drawThread.SetObjects(holder, _virtualCardBoardState);
        _drawThread.SetRunning(true);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        _drawThread.SetRunning(false);
        _virtualCardBoardState.StopProcesses();
    }

    private void _LongClickAction() {
        getHandler().post(new Runnable() {
            @Override
            public void run() {
                Vibrator _vibrator = (Vibrator) _context.getSystemService(Context.VIBRATOR_SERVICE);
                _vibrator.vibrate(new long[]{10, 100, 90, 100}, -1);
                _pcInterface.SendBroadcastWelcomeSignal();
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        return _gestureDetector.onTouchEvent(e);
    }
}
