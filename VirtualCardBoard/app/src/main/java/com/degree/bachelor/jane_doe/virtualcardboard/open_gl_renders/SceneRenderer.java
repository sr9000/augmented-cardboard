package com.degree.bachelor.jane_doe.virtualcardboard.open_gl_renders;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.ViewGroup;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

/**
 * Created by Jane-Doe on 5/28/2016.
 */
public class SceneRenderer implements GLSurfaceView.Renderer, ISceneRendererManager {
    private volatile Bitmap _bitmap;
    private volatile Bitmap _retBitmap;
    private volatile Canvas _canvas4RetBitmap;
    private volatile Buffer _buffer;
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
        _width = width;
        _height = (height > 0)? height : 1;

        ViewGroup.LayoutParams params = _glSurfaceHolder.getView().getLayoutParams();
        params.width = width;
        params.height = height;
        _glSurfaceHolder.getView().setLayoutParams(params);

        _isNeedSetupGl = true;
        return this;
    }

    @Override
    public ISceneRendererManager DrawingOn() {
        _isDrawing = true;
        return this;
    }

    @Override
    public ISceneRendererManager DrawingOff() {
        _isDrawing = false;
        return this;
    }

    @Override
    public ISceneRendererManager SetGlMethods(GlSetupRunnable glViewSetup, GlOnDrawRunnable glDraw) {
        _glSetupRunnable = glViewSetup;
        _glOnDrawRunnable = glDraw;
        return this;
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) { }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) { }

    public Bitmap getBitmap() {
        return _bitmap;
    }

    @Override
    public void onDrawFrame(GL10 _gl) {
        GL11 gl = _gl instanceof GL11 ? ((GL11) _gl) : null;
        if (gl == null) {
            return;
        }
        if (_isNeedSetupGl) {
            if (_glSetupRunnable != null) {
                _glSetupRunnable.run(gl, _width, _height);
            }
            _bitmap = Bitmap.createBitmap(_width, _height, Bitmap.Config.ARGB_8888);
            _buffer = ByteBuffer.allocate(4*_width*_height).order(ByteOrder.nativeOrder());
            _isNeedSetupGl = false;
        }

        if (_isDrawing && _glOnDrawRunnable != null) {
            _glOnDrawRunnable.run(gl);

            _buffer.position(0);
            gl.glReadPixels(0, 0, _width, _height , GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, _buffer);
            _bitmap.copyPixelsFromBuffer(_buffer);
        }
    }
}
