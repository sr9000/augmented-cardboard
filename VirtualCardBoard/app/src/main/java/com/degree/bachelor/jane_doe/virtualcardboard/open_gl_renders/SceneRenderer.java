package com.degree.bachelor.jane_doe.virtualcardboard.open_gl_renders;

import android.graphics.Bitmap;
import android.opengl.GLSurfaceView;
import android.view.ViewGroup;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Jane-Doe on 5/28/2016.
 */
public class SceneRenderer implements GLSurfaceView.Renderer, ISceneRendererManager {
    private volatile Bitmap _bitmap;
    private final Object _syncBitmap = new Object();

    private volatile int _width = 0, _height = 1;
    private volatile boolean _isNeedSetupGl = false;
    private volatile boolean _isDrawing = false;
    private volatile GlSurfaceHolder _glSurfaceHolder;
    private final Object _syncSetup = new Object();

    private GlSetupRunnable _glSetupRunnable;
    private GlOnDrawRunnable _glOnDrawRunnable;
    private final Object _syncMethods = new Object();

    private SceneRenderer(){}

    public SceneRenderer(int width, int height, GlSurfaceHolder glSurfaceHolder) {
        _glSetupRunnable = null;
        _glOnDrawRunnable = null;
        _glSurfaceHolder = glSurfaceHolder;
    }

    @Override
    public ISceneRendererManager SetupGl(int width, int height) {
        synchronized (_syncSetup) {
            _width = width;
            _height = (height > 0)? height : 1;

            ViewGroup.LayoutParams params = _glSurfaceHolder.getView().getLayoutParams();
            params.width = width;
            params.height = height;
            _glSurfaceHolder.getView().setLayoutParams(params);

            _isNeedSetupGl = true;
        }
        return this;
    }

    @Override
    public ISceneRendererManager DrawingOn() {
        synchronized (_syncSetup) {
            _isDrawing = true;
        }
        return this;
    }

    @Override
    public ISceneRendererManager DrawingOff() {
        synchronized (_syncSetup) {
            _isDrawing = false;
        }
        return this;
    }

    @Override
    public ISceneRendererManager SetGlMethods(GlSetupRunnable glViewSetup, GlOnDrawRunnable glDraw) {
        synchronized (_syncMethods) {
            _glSetupRunnable = glViewSetup;
            _glOnDrawRunnable = glDraw;
        }
        return this;
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) { }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) { }

    private void _convertToBitmap(GL10 mGL) {
        byte[] arr = new byte[4*_width*_height];
        Buffer ib = ByteBuffer.wrap(arr).order(ByteOrder.nativeOrder());
        //IntBuffer ibt = IntBuffer.allocate(mWidth*mHeight);
        mGL.glFinish();
        int err = mGL.glGetError();

        mGL.glReadPixels(0, 0, _width, _height , GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, ib);

        // Convert upside down mirror-reversed image to right-side up normal image.
        //for (int i = 0; i < mHeight; i++) {
        //    for (int j = 0; j < mWidth; j++) {
        //        ibt.put((mHeight-i-1)*mWidth + j, ib.get(i*mWidth + j));
        //    }
        //}

        synchronized (_syncBitmap) {
            _bitmap.copyPixelsFromBuffer(ib);
        }
    }

    public Bitmap getBitmap() {
        synchronized (_syncBitmap) {
            if (_bitmap == null) return null;
            return Bitmap.createBitmap(_bitmap);
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        synchronized (_syncMethods) {
            synchronized (_syncSetup) {
                if (_isNeedSetupGl) {
                    if (_glSetupRunnable != null) {
                        _glSetupRunnable.run(gl, _width, _height);
                    }
                    synchronized (_syncBitmap) {
                        _bitmap = Bitmap.createBitmap(_width, _height, Bitmap.Config.ARGB_8888);
                    }
                    _isNeedSetupGl = false;
                }

                if (_isDrawing) {
                    if (_glOnDrawRunnable != null) {
                        _glOnDrawRunnable.run(gl);
                        _convertToBitmap(gl);
                    }
                }
            }
        }
    }
}
