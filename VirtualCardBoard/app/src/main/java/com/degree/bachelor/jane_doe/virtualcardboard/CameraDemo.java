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
        int b, g, r;
        int t1, t2, t3;
        int p1, p2, p3;
        int o1, o2, o3;
        int s1, s2, s3;

        // i1-i4 along Y and the final pixels
        // i5,i6 along pixels U and V
        int i1 = 0, i2 = 1
            , i3 = width, i4 = width+1
            , i5 = offset, i6 = offset+1;
        for(; i1 < size; i1+=2, i2+=2, i3+=2, i4+=2, i5+=2, i6+=2) {
            y1 = nv21Buffer[i1  ]&0xff;
            y2 = nv21Buffer[i2]&0xff;
            y3 = nv21Buffer[i3]&0xff;
            y4 = nv21Buffer[i4]&0xff;

            //NV21
            v = (nv21Buffer[i5]&0xff) - 128;
            u = (nv21Buffer[i6]&0xff) - 128;

            //YV12
            //u = data[offset+k  ]&0xff - 128;
            //v = data[offset+k + size/4]&0xff - 128;

            {//inline packed convertYUVtoABGRpacked
                r = (int)(1.6f*v);//(int)(1.772f*v);
                g = (int)(0.3f*v + 0.6f*u);//(int)(0.344f*v + 0.714f*u);
                b = (int)(1.3f*u);//(int)(1.402f*u);

                abgrBuffer[i1] = 0xff000000
                        | (((t1=(y1 + b))>255? 255 : t1 < 0 ? 0 : t1)<<16)
                        | (((t2=(y1 - g))>255? 255 : t2 < 0 ? 0 : t2)<<8)
                        | ((t3=(y1 + r))>255? 255 : t3 < 0 ? 0 : t3);
                abgrBuffer[i2] = 0xff000000
                        | (((p1=(y1 + b))>255? 255 : p1 < 0 ? 0 : p1)<<16)
                        | (((p2=(y1 - g))>255? 255 : p2 < 0 ? 0 : p2)<<8)
                        | ((p3=(y1 + r))>255? 255 : p3 < 0 ? 0 : p3);
                abgrBuffer[i3] = 0xff000000
                        | (((o1=(y1 + b))>255? 255 : o1 < 0 ? 0 : o1)<<16)
                        | (((o2=(y1 - g))>255? 255 : o2 < 0 ? 0 : o2)<<8)
                        | ((o3=(y1 + r))>255? 255 : o3 < 0 ? 0 : o3);
                abgrBuffer[i4] = 0xff000000
                        | (((s1=(y1 + b))>255? 255 : s1 < 0 ? 0 : s1)<<16)
                        | (((s2=(y1 - g))>255? 255 : s2 < 0 ? 0 : s2)<<8)
                        | ((s3=(y1 + r))>255? 255 : s3 < 0 ? 0 : s3);
            }

            if (i1!=0 && (i1+2)%width==0) {
                i1 += width;
                i2 += width;
                i3 += width;
                i4 += width;
            }
        }
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
