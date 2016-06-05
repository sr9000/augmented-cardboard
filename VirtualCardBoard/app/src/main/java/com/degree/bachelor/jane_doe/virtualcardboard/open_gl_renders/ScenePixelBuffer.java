package com.degree.bachelor.jane_doe.virtualcardboard.open_gl_renders;

import android.graphics.Bitmap;
import android.opengl.GLSurfaceView.Renderer;
import android.util.Log;

import com.degree.bachelor.jane_doe.virtualcardboard.PausableThread;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import javax.microedition.khronos.opengles.GL10;

import static javax.microedition.khronos.egl.EGL10.*;

/**
 * Created by Jane-Doe on 5/28/2016.
 */
public class ScenePixelBuffer {
    private volatile PausableThread _drawer;
    private volatile Bitmap _bitmap;
    private final Object _syncBitmap = new Object();


    public ScenePixelBuffer(final int width, final int height, final Renderer renderer) {
        _drawer = new PausableThread() {
            final static String TAG = "ScenePixelBuffer";
            final static boolean LIST_CONFIGS = false;

            Renderer mRenderer; // borrow this interface
            int mWidth, mHeight;


            EGL10 mEGL;
            EGLDisplay mEGLDisplay;
            EGLConfig[] mEGLConfigs;
            EGLConfig mEGLConfig;
            EGLContext mEGLContext;
            EGLSurface mEGLSurface;
            GL10 mGL;

            String mThreadOwner;

            private EGLConfig chooseConfig() {
                int[] attribList = new int[] {
                        EGL_LUMINANCE_SIZE, 0,
                        EGL_DEPTH_SIZE, 0,
                        EGL_STENCIL_SIZE, 0,
                        EGL_RED_SIZE, 8,
                        EGL_GREEN_SIZE, 8,
                        EGL_BLUE_SIZE, 8,
                        EGL_ALPHA_SIZE, 8,
                        EGL_COLOR_BUFFER_TYPE, EGL_RGB_BUFFER,
                        EGL_BUFFER_SIZE, 32,
                        EGL_SURFACE_TYPE, EGL_PBUFFER_BIT,
                        EGL_NONE
                };

                // No error checking performed, minimum required code to elucidate logic
                // Expand on this logic to be more selective in choosing a configuration
                int[] numConfig = new int[1];
                mEGL.eglChooseConfig(mEGLDisplay, attribList, null, 0, numConfig);//get total count of matches configurations
                int configSize = numConfig[0];
                mEGLConfigs = new EGLConfig[configSize];
                mEGL.eglChooseConfig(mEGLDisplay, attribList, mEGLConfigs, configSize, numConfig);

                if (true) {
                    listConfig();
                }

                return mEGLConfigs[0];  // Best match is probably the first configuration
            }

            private void listConfig() {
                //Log.i(TAG, "Config List {");

                for (EGLConfig config : mEGLConfigs) {
                    int d, s, r, g, b, a;

                    // Expand on this logic to dump other attributes
                    d = getConfigAttrib(config, EGL_DEPTH_SIZE);
                    s = getConfigAttrib(config, EGL_STENCIL_SIZE);
                    r = getConfigAttrib(config, EGL_RED_SIZE);
                    g = getConfigAttrib(config, EGL_GREEN_SIZE);
                    b = getConfigAttrib(config, EGL_BLUE_SIZE);
                    a = getConfigAttrib(config, EGL_ALPHA_SIZE);
                    Log.w(TAG, "    <d,s,r,g,b,a> = <" + d + "," + s + "," +
                            r + "," + g + "," + b + "," + a + ">");
                }

                // Log.i(TAG, "}");
            }

            private int getConfigAttrib(EGLConfig config, int attribute) {
                int[] value = new int[1];
                return mEGL.eglGetConfigAttrib(mEGLDisplay, config,
                        attribute, value)? value[0] : 0;
            }

            private void convertToBitmap() {
                if (!Thread.currentThread().getName().equals(mThreadOwner)) {
                    //Log.e(TAG, "getBitmap: This thread does not own the OpenGL context.");
                    return;
                }

                Buffer ib = ByteBuffer.allocateDirect(4*mWidth*mHeight).order(ByteOrder.nativeOrder());
                //IntBuffer ibt = IntBuffer.allocate(mWidth*mHeight);
                mGL.glFinish();
                mEGL.eglWaitGL();
                int err = mGL.glGetError();

                mGL.glReadPixels(0, 0, mWidth, mHeight , GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, ib);

                // Convert upside down mirror-reversed image to right-side up normal image.
                //for (int i = 0; i < mHeight; i++) {
                //    for (int j = 0; j < mWidth; j++) {
                //        ibt.put((mHeight-i-1)*mWidth + j, ib.get(i*mWidth + j));
                //    }
                //}

                synchronized (_syncBitmap) {
                    _bitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
                    _bitmap.copyPixelsFromBuffer(ib);
                }
            }

            @Override
            protected void InitProcess() {
                mWidth = width;
                mHeight = height;
                mRenderer = renderer;

                int[] version = new int[2];
                int[] attribList = new int[] {
                        EGL_WIDTH, mWidth,
                        EGL_HEIGHT, mHeight,
                        EGL_NONE
                };

                // No error checking performed, minimum required code to elucidate logic
                mEGL = (EGL10) EGLContext.getEGL();
                EGLContext currContext = mEGL.eglGetCurrentContext();
                EGLDisplay currDisplay = mEGL.eglGetCurrentDisplay();
                EGLSurface currSurface = mEGL.eglGetCurrentSurface(EGL_READ);
                int error = mEGL.eglGetError();
                mEGLDisplay = mEGL.eglGetDisplay(EGL_DEFAULT_DISPLAY);
                error = mEGL.eglGetError();
                mEGL.eglInitialize(mEGLDisplay, version);
                error = mEGL.eglGetError();
                mEGLConfig = chooseConfig(); // Choosing a config is a little more complicated
                error = mEGL.eglGetError();
                mEGLContext = mEGL.eglCreateContext(mEGLDisplay, mEGLConfig, EGL_NO_CONTEXT, null);
                error = mEGL.eglGetError();
                mEGLSurface = mEGL.eglCreatePbufferSurface(mEGLDisplay, mEGLConfig,  attribList);
                error = mEGL.eglGetError();
                currContext = mEGL.eglGetCurrentContext();
                currDisplay = mEGL.eglGetCurrentDisplay();
                currSurface = mEGL.eglGetCurrentSurface(EGL_READ);
                error = mEGL.eglGetError();
                boolean res = mEGL.eglMakeCurrent(mEGLDisplay, mEGLSurface, mEGLSurface, mEGLContext);
                error = mEGL.eglGetError();
                currContext = mEGL.eglGetCurrentContext();
                currDisplay = mEGL.eglGetCurrentDisplay();
                currSurface = mEGL.eglGetCurrentSurface(EGL_READ);
                error = mEGL.eglGetError();
                mGL = (GL10) mEGLContext.getGL();
                error = mEGL.eglGetError();

                // Record thread owner of OpenGL context
                mThreadOwner = Thread.currentThread().getName();

                // Call the renderer initialization routines
                mRenderer.onSurfaceCreated(mGL, mEGLConfig);
                mRenderer.onSurfaceChanged(mGL, mWidth, mHeight);


            }

            @Override
            protected void ProcessBody() {
                // Call the renderer draw routine
                mRenderer.onDrawFrame(mGL);
                convertToBitmap();
                Thread.yield();
            }

        };

        _drawer.start();
        _drawer.SetRunning(true);
    }

    public Bitmap getBitmap() {
        synchronized (_syncBitmap) {
            if (_bitmap == null) return null;
            return Bitmap.createBitmap(_bitmap);
        }
    }

    public void Stop() {
        _drawer.SetRunning(false);
        _drawer.interrupt();
    }
}
