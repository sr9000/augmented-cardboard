package com.degree.bachelor.jane_doe.virtualcardboard;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.hardware.Camera;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.List;

/**
 * Created by Jane-Doe on 4/18/2016.
 */
public class CameraDemo {
    private boolean isNormalOpened;
    private boolean isPreviewed;

    private Camera cam;
    private Context baseContext;
    private SurfaceView surfaceView;
    private Bitmap bitmap;

    CameraDemo(Context context) {
        baseContext = context;
        surfaceView = new SurfaceView(baseContext);
        isNormalOpened = false;
        isPreviewed = false;
    }

    private void GetCameraInstance(){
        cam = null;
        try {
            // attempt to get a Camera instance
            cam = Camera.open();
        }
        catch (Exception e) {
            // Camera is not available (in use or does not exist)
        }
    }

    public void OpenCamera()
    {
        GetCameraInstance();
        isNormalOpened = !(cam == null);
    }

    public void CloseCamera() {
        cam.release();
        isPreviewed = false;
        isNormalOpened = false;
        cam = null;
        {//UNset bitmap for canvas
            Canvas canvas = surfaceView.getHolder().lockCanvas();
            canvas.setBitmap(null);
            surfaceView.getHolder().unlockCanvasAndPost(canvas);
        }
        bitmap = null;
    }

    public boolean IsNormalOpened() {
        return isNormalOpened;
    }

    public boolean IsPrviewed() {
        return isPreviewed;
    }

    public Bitmap GetCapturedBitmap() {
        return bitmap;
    }

    public void startPreview(int width, int height)
    {
        if (!isNormalOpened) return;

        Camera.Parameters params = cam.getParameters();

        int[] bestRange;
        {//set best fps range
            List<int[]> fpsRanges = params.getSupportedPreviewFpsRange();
            bestRange = fpsRanges.get(0);
            for (int[] range : fpsRanges)
            {
                if (range[Camera.Parameters.PREVIEW_FPS_MIN_INDEX] > bestRange[Camera.Parameters.PREVIEW_FPS_MIN_INDEX])
                    bestRange = range;
            }
        }
        params.setPreviewFpsRange(bestRange[Camera.Parameters.PREVIEW_FPS_MIN_INDEX], bestRange[Camera.Parameters.PREVIEW_FPS_MAX_INDEX]);

        Camera.Size bestSize;
        {//set enough size
            double targetRatio = ((double)width) / ((double)height);
            List<Camera.Size> sizes = params.getSupportedPreviewSizes();
            bestSize = sizes.get(0);
            double bestSizeRatio = ((double)bestSize.width) / ((double)bestSize.height);
            double bestSizeScale = (bestSizeRatio > targetRatio)? ((double)width) / ((double)bestSize.width) : ((double)height) / ((double)bestSize.height);
            double bestSizeSquare = bestSizeScale * bestSizeScale * ((double)bestSize.width) * ((double)bestSize.height);

            for (Camera.Size size : sizes) {
                double sizeRatio = ((double)size.width) / ((double)size.height);
                double sizeScale = (sizeRatio > targetRatio)? ((double)width) / ((double)size.width) : ((double)height) / ((double)size.height);
                double sizeSquare = sizeScale * sizeScale * ((double)size.width) * ((double)size.height);

                //main condition
                if (Math.abs(sizeSquare - bestSizeSquare) / Math.max(sizeSquare, bestSizeSquare) < 0.01) {
                    //if squares are similar
                    if (bestSizeScale < 1.01) {
                        //if sizes big enough select lowest size
                        if (sizeScale > bestSizeScale && sizeScale < 1.01) {
                            bestSize = size;
                            bestSizeScale = sizeScale;
                            bestSizeSquare = sizeSquare;
                        }
                    } else if (sizeScale < bestSizeScale) {
                        //select biggest source sizes
                        bestSize = size;
                        bestSizeScale = sizeScale;
                        bestSizeSquare = sizeSquare;
                    }
                }
                else if (sizeSquare > bestSizeSquare) {
                    //select best square
                    bestSize = size;
                    bestSizeScale = sizeScale;
                    bestSizeSquare = sizeSquare;
                }
            }
        }
        params.setPreviewSize(bestSize.width, bestSize.height);

        cam.setParameters(params);

        bitmap = Bitmap.createBitmap(bestSize.width, bestSize.height, Bitmap.Config.ARGB_8888);
        {//set bitmap for canvas
            Canvas canvas = surfaceView.getHolder().lockCanvas();
            canvas.setBitmap(bitmap);
            surfaceView.getHolder().unlockCanvasAndPost(canvas);
        }

        try {
            cam.setPreviewDisplay(surfaceView.getHolder());
        } catch (IOException e) {
            //UNset bitmap for canvas
            Canvas canvas = surfaceView.getHolder().lockCanvas();
            canvas.setBitmap(null);
            surfaceView.getHolder().unlockCanvasAndPost(canvas);
            bitmap = null;
        }

        cam.startPreview();

        isPreviewed = true;
    }
}
