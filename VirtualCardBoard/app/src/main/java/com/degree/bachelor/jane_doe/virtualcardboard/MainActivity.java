package com.degree.bachelor.jane_doe.virtualcardboard;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

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

        //says to surfaceholder that
        //DrawView control surface events
        public DrawView(Context context) {
            super(context);
            getHolder().addCallback(this);
        }

        //overrided surface events
        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                   int height) {

        }

        //create thread on create view
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            drawThread = new DrawThread(getHolder());
            drawThread.setRunning(true);
            drawThread.start();
        }

        //stopped thread when surface is destroyed
        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            boolean retry = true;
            drawThread.setRunning(false);
            while (retry) {
                try {
                    drawThread.join();
                    retry = false;
                } catch (InterruptedException e) {
                }
            }
        }

        //special draw thread
        class DrawThread extends Thread {

            //work control variable
            private boolean running = false;

            //surface holder to drawing on
            private SurfaceHolder surfaceHolder;

            //paint for drawing
            private Paint pLeft, pRight;

            //get surface on thread create
            public DrawThread(SurfaceHolder surfaceHolder) {
                this.surfaceHolder = surfaceHolder;
                pLeft = new Paint();
                pLeft.setStyle(Paint.Style.FILL);
                pLeft.setColor(Color.GREEN);

                pRight = new Paint();
                pRight.setStyle(Paint.Style.FILL);
                pRight.setColor(Color.RED);
            }

            //enable cycle redrawing
            public void setRunning(boolean running) {
                this.running = running;
            }

            //on start thread
            @Override
            public void run() {
                Canvas canvas;
                while (running) {
                    canvas = null;
                    try {
                        canvas = surfaceHolder.lockCanvas(null);
                        if (canvas == null)
                            continue;
                        //draw action here
                        canvas.drawRect(0, 0, canvas.getWidth()/2, canvas.getHeight(), pLeft);
                        canvas.drawRect(canvas.getWidth()/2, 0, canvas.getWidth(), canvas.getHeight(), pRight);
                    } finally {
                        if (canvas != null) {
                            surfaceHolder.unlockCanvasAndPost(canvas);
                        }
                    }
                }
            }

        }//DrawThread

    }//Draw view

}//Main Activity