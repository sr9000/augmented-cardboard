package com.degree.bachelor.jane_doe.virtualcardboard;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import static com.degree.bachelor.jane_doe.virtualcardboard.BinocularView.*;

//main activity
public class MainActivity extends Activity {

    //activity sets custom view
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new DrawView(this));
    }

    //custom view, manually control for drawing
    class DrawView extends SurfaceView implements SurfaceHolder.Callback {

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
        public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                   int height) {
            //hot changes
            bv.SetDisplaySizes(width, height);
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

        //special draw thread
        class DrawThread extends Thread {

            //work control variable
            private boolean _running = false;

            //surface holder to drawing on
            private SurfaceHolder _surfaceHolder;

            //paint for drawing
            private Paint pLeft, pRight;

            //Binocular
            private BinocularView.BinocularInfo _bv_info;

            //camDemo
            private CameraDemo _cam;

            //synch obj
            private Object lock = new Object();

            //get surface on thread create
            public DrawThread(@Nullable SurfaceHolder surfaceHolder, BinocularInfo bv_info, CameraDemo cam) {
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

            public void setHolder(SurfaceHolder holder) {
                _surfaceHolder = holder;
            }

            public void setRunning(boolean running) {
                _running = running;
                synchronized (lock) {
                    lock.notify();
                }
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
                    Bitmap captured = _cam.getCapturedBitmap();
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

        }//DrawThread

    }//Draw view

}//Main Activity