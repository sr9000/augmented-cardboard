package com.degree.bachelor.jane_doe.virtualcardboard;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.media.Image;
import android.opengl.GLES10;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.TextureView;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Jane-Doe on 4/18/2016.
 */
public class CameraDemo implements Camera.PreviewCallback {
    private boolean isNormalOpened;
    private boolean isNormalConfigured;

    private boolean isNeededFreeTextures;

    private static final int MAGIC_TEXTURE_ID = 10;

    private Camera cam;
    
    private byte gBuffer[];
    private int textureBuffer[];
    private int gBufferSize;

    private SurfaceTexture camTexture;
    private Bitmap bitmap;
    private final Boolean bitmapFieldSynchronization = Boolean.valueOf(true);

    ByteArrayOutputStream baos;

    private int _width, _height;

    public int getWidth() {
        return _width;
    }

    public int getHeight() {
        return _height;
    }

    public CameraDemo() {
        isNormalOpened = false;
        isNormalConfigured = false;
        isNeededFreeTextures = false;
        camTexture = null;
        bitmap = null;
        gBuffer = null;
        cam = null;
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
        if (isNormalOpened && isNormalConfigured) {
            baos = new ByteArrayOutputStream();
            cam.startPreview();
        } else {
            StopPreview();
        }
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
        camTexture = null;
        textureBuffer = null;
        bitmap = null;
        gBuffer = null;

        baos = null;
    }

    public Bitmap getCapturedBitmap() {
        /*Bitmap retBitmap;
        synchronized (bitmapFieldSynchronization) {
            retBitmap = Bitmap.createBitmap(bitmap);
        }
        return retBitmap;*/
        return bitmap;
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
        }
        params.setPreviewFpsRange(bestRange[Camera.Parameters.PREVIEW_FPS_MIN_INDEX], bestRange[Camera.Parameters.PREVIEW_FPS_MAX_INDEX]);

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
        }
        params.setPreviewSize(bestSize.width, bestSize.height);
        _width = bestSize.width;
        _height = bestSize.height;

        params.setPreviewFormat(ImageFormat.NV21);

        cam.setParameters(params);

        {//bind texture
            camTexture = new SurfaceTexture(MAGIC_TEXTURE_ID);

            isNeededFreeTextures = true;
        }

        cam.setPreviewTexture(camTexture);

        bitmap = Bitmap.createBitmap(bestSize.width, bestSize.height, Bitmap.Config.ARGB_8888);

        textureBuffer = new int[_width * _height];
        gBufferSize = (_width * _height * (ImageFormat.getBitsPerPixel(params.getPreviewFormat())) + 7) / 8;
        gBuffer = new byte[gBufferSize];

        cam.addCallbackBuffer(gBuffer);
        cam.setPreviewCallbackWithBuffer(this);

        isNormalConfigured = true;
    }

    static private int yuv2rgb(byte yValue, byte uValue, byte vValue) {
        int iyValue = yValue & 0xFF;
        int iuValue = uValue & 0xFF;
        int ivValue = vValue & 0xFF;
        int rTmp = Math.max(0, Math.min(255,((int)(iyValue + (1.370705 * (ivValue-128))))));
        int gTmp = Math.max(0, Math.min(255,((int)(iyValue - (0.698001 * (ivValue-128)) - (0.337633 * (iuValue-128))))));
        int bTmp = Math.max(0, Math.min(255,((int)(iyValue + (1.732446 * (iuValue-128))))));
        return Color.argb(255, rTmp, gTmp, bTmp);
    }

    @Override
    public void onPreviewFrame(byte[] bytes, Camera camera) {
        YuvImage yuvImage = new YuvImage(bytes, ImageFormat.NV21, _width, _height, null);
        baos.reset();
        yuvImage.compressToJpeg(new Rect(0, 0, _width, _height), 80, baos);
        byte[] streamBuffer = baos.toByteArray();
        BitmapFactory.Options bitmapFatoryOptions = new BitmapFactory.Options();
        bitmapFatoryOptions.inPreferredConfig = Bitmap.Config.RGB_565;

        synchronized (bitmapFieldSynchronization) {
            bitmap = BitmapFactory.decodeByteArray(streamBuffer, 0, streamBuffer.length, bitmapFatoryOptions);
        }
        camera.addCallbackBuffer(gBuffer);
    }
}
