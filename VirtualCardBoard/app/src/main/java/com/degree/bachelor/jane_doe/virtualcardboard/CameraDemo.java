package com.degree.bachelor.jane_doe.virtualcardboard;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.graphics.YuvImage;
import android.hardware.Camera;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.IntBuffer;
import java.util.List;

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

    private SurfaceTexture camTexture;
    private Bitmap bitmap;

    int abgrBuffer[];

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
        abgrBuffer = null;
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
        bitmap = null;
        gBuffer = null;

        //baos = null;
        abgrBuffer = null;

        isNormalOpened = false;
        isNormalConfigured = false;
    }

    public Bitmap getCapturedBitmap() {
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
            double bestSizeSquare = Math.min(1.0, bestSizeScale) * Math.min(1.0, bestSizeScale) * ((double) bestSize.width) * ((double) bestSize.height);

            for (Camera.Size size : sizes) {
                double sizeRatio = ((double) size.width) / ((double) size.height);
                double sizeScale = (sizeRatio > targetRatio) ? ((double) width) / ((double) size.width) : ((double) height) / ((double) size.height);
                double sizeSquare = Math.min(1.0, sizeScale) * Math.min(1.0, sizeScale) * ((double) size.width) * ((double) size.height);

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
        gBuffer = new byte[(_width * _height * (ImageFormat.getBitsPerPixel(params.getPreviewFormat())) + 7) / 8];
        abgrBuffer = new int[_width * _height];

        cam.addCallbackBuffer(gBuffer);
        cam.setPreviewCallbackWithBuffer(this);

        isNormalConfigured = true;
    }

    //BGR!!!
    public static void convertYUV420_NV21toABGR8888(int[] abgrBuffer, byte [] nv21Buffer, int width, int height) {
        int size = width*height;
        int offset = size;
        int u, v, y1, y2, y3, y4;

        // i along Y and the final pixels
        // k along pixels U and V
        for(int i=0, k=0; i < size; i+=2, k+=2) {
            y1 = nv21Buffer[i  ]&0xff;
            y2 = nv21Buffer[i+1]&0xff;
            y3 = nv21Buffer[width+i  ]&0xff;
            y4 = nv21Buffer[width+i+1]&0xff;

            //NV21
            v = nv21Buffer[offset+k  ]&0xff;
            u = nv21Buffer[offset+k+1]&0xff;

            //YV12
            //u = data[offset+k  ]&0xff;
            //v = data[offset+k + size/4]&0xff;

            v = v-128;
            u = u-128;

            abgrBuffer[i  ] = convertYUVtoABGR(y1, u, v);
            abgrBuffer[i+1] = convertYUVtoABGR(y2, u, v);
            abgrBuffer[width+i  ] = convertYUVtoABGR(y3, u, v);
            abgrBuffer[width+i+1] = convertYUVtoABGR(y4, u, v);

            if (i!=0 && (i+2)%width==0)
                i += width;
        }
    }

    private static int convertYUVtoABGR(int y, int u, int v) {
        int r = y + (int)(1.772f*v);
        int g = y - (int)(0.344f*v + 0.714f*u);
        int b = y + (int)(1.402f*u);
        r = r>255? 255 : r<0 ? 0 : r;
        g = g>255? 255 : g<0 ? 0 : g;
        b = b>255? 255 : b<0 ? 0 : b;
        return 0xff000000 | (b<<16) | (g<<8) | r;
    }

    @Override
    public void onPreviewFrame(byte[] bytes, Camera camera) {
        if (!IsStarted()) return;

        //software decoding
        convertYUV420_NV21toABGR8888(abgrBuffer, bytes, _width, _height);
        camera.addCallbackBuffer(gBuffer);
        bitmap.copyPixelsFromBuffer(IntBuffer.wrap(abgrBuffer));
    }
}
