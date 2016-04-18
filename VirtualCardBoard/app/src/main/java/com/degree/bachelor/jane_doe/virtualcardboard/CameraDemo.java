package com.degree.bachelor.jane_doe.virtualcardboard;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLES10;
import android.opengl.GLES20;
import android.view.Surface;
import android.view.TextureView;


import java.io.IOException;
import java.util.List;

/**
 * Created by Jane-Doe on 4/18/2016.
 */
public class CameraDemo {
    private boolean isNormalOpened;
    private boolean isNormalConfigured;

    private boolean isNeededFreeTextures;

    private Camera cam;
    private int[] glTexture;
    private SurfaceTexture camTexture, specTexture;
    private TextureView camTextureView, specTextureView;

    public CameraDemo(Context context) {
        isNormalOpened = false;
        isNormalConfigured = false;
        isNeededFreeTextures = false;
        glTexture = null;
        camTexture = null;
        specTexture = null;
        cam = null;

        camTextureView = new TextureView(context);
        specTextureView = new TextureView(context);
    }

    private void GetCameraInstance() {
        cam = null;
        try {
            // attempt to get a Camera instance
            cam = Camera.open();
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
        }
    }

    private void OpenCamera() {
        GetCameraInstance();
        isNormalOpened = !(cam == null);
    }

    public void StartPreview(int width, int height) {
        OpenCamera();
        try {
            Configure(width, height);
        } catch (IOException e) {
            isNormalConfigured = false;
        }
        if (isNormalOpened && isNormalConfigured)
            cam.startPreview();
        else
            StopPreview();
    }

    public void StopPreview() {
        if (isNormalOpened) {
            cam.stopPreview();
            cam.release();
        }
        if (isNeededFreeTextures)
            FreeTextures();
    }

    public boolean IsStarted() {
        return isNormalOpened && isNormalConfigured;
    }

    private void FreeTextures() {
        camTexture.detachFromGLContext();
        specTexture.detachFromGLContext();
        GLES20.glDeleteTextures(2, glTexture, 0);
    }

    class TextureAdapter implements SurfaceTexture.OnFrameAvailableListener {

        private SurfaceTexture _targetTexture;
        private Surface _targetSurface;

        TextureAdapter(SurfaceTexture targetTexture) {
            _targetTexture = targetTexture;
            _targetSurface = new Surface(_targetTexture);
        }

        @Override
        public void onFrameAvailable(SurfaceTexture surfaceTexture) {
            surfaceTexture.updateTexImage();//todo try to delete it
            //todo implementation
            Canvas canvas = _targetSurface.lockCanvas(null);
            GLES20.glDr
        }
    }

    private void Configure(int width, int height) throws IOException {
        if (!isNormalOpened) return;


        Camera.Parameters params = cam.getParameters();

        int[] bestRange;
        {//set best fps range
            List<int[]> fpsRanges = params.getSupportedPreviewFpsRange();
            bestRange = fpsRanges.get(0);
            for (int[] range : fpsRanges) {
                if (range[Camera.Parameters.PREVIEW_FPS_MIN_INDEX] > bestRange[Camera.Parameters.PREVIEW_FPS_MIN_INDEX])
                    bestRange = range;
            }
            params.setPreviewFpsRange(bestRange[Camera.Parameters.PREVIEW_FPS_MIN_INDEX], bestRange[Camera.Parameters.PREVIEW_FPS_MAX_INDEX]);
        }

        Camera.Size bestSize;
        {//set enough size
            double targetRatio = ((double) width) / ((double) height);
            List<Camera.Size> sizes = params.getSupportedPreviewSizes();
            bestSize = sizes.get(0);
            double bestSizeRatio = ((double) bestSize.width) / ((double) bestSize.height);
            double bestSizeScale = (bestSizeRatio > targetRatio) ? ((double) width) / ((double) bestSize.width) : ((double) height) / ((double) bestSize.height);
            double bestSizeSquare = bestSizeScale * bestSizeScale * ((double) bestSize.width) * ((double) bestSize.height);

            for (Camera.Size size : sizes) {
                double sizeRatio = ((double) size.width) / ((double) size.height);
                double sizeScale = (sizeRatio > targetRatio) ? ((double) width) / ((double) size.width) : ((double) height) / ((double) size.height);
                double sizeSquare = sizeScale * sizeScale * ((double) size.width) * ((double) size.height);

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
                } else if (sizeSquare > bestSizeSquare) {
                    //select best square
                    bestSize = size;
                    bestSizeScale = sizeScale;
                    bestSizeSquare = sizeSquare;
                }
            }
            params.setPreviewSize(bestSize.width, bestSize.height);
        }

        cam.setParameters(params);

        {//bind texture
            glTexture = new int[2];
            GLES20.glGenTextures(2, glTexture, 0);
            //GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, glTexture[0]);
            //GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, glTexture[1]);

            camTexture = new SurfaceTexture(glTexture[0]);
            camTexture.setDefaultBufferSize(bestSize.width, bestSize.height);

            specTexture = new SurfaceTexture(glTexture[1]);
            specTexture.setDefaultBufferSize(width, height);

            camTexture.setOnFrameAvailableListener(new TextureAdapter(specTexture));

            isNeededFreeTextures = true;
        }

        cam.setPreviewTexture(camTexture);
        isNormalConfigured = true;
    }
}
