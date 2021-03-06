package com.degree.bachelor.jane_doe.virtualcardboard.open_gl_renders;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.view.View;

/**
 * Created by Jane-Doe on 5/28/2016.
 */
public class GlSurfaceHolder {
    private GLSurfaceView _surfaceView;

    private SceneRenderer _renderer; // borrow this interface

    public GlSurfaceHolder(Context context) {
        _renderer = new SceneRenderer(1, 1, this);

        _surfaceView = new GLSurfaceView(context);
        _surfaceView.getHolder().setFormat(PixelFormat.RGBA_8888);

        _surfaceView.setEGLConfigChooser(8, 8, 8, 8, 0, 0);

        _surfaceView.setRenderer(_renderer);

        _surfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }

    public ISceneRendererManager getSceneRendererManager() {
        return _renderer;
    }

    public Bitmap getBitmap() {
        return _renderer.getBitmap();
    }

    public View getView() {
        return _surfaceView;
    }
}
