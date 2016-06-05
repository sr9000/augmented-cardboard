package com.degree.bachelor.jane_doe.virtualcardboard.open_gl_renders;

import android.graphics.Bitmap;
import android.opengl.GLSurfaceView.Renderer;


/**
 * Created by Jane-Doe on 5/28/2016.
 */
public class GlScene  {
    private volatile ScenePixelBuffer _scenePixelBuffer;
    private volatile boolean _isStarted;
    private volatile Renderer _renderer;

    public GlScene() {
        _scenePixelBuffer = null;

        _renderer = new SceneRenderer();
        _isStarted = false;
    }

    public boolean IsStarted() {
        return _isStarted;
    }

    public Bitmap GetRenderedBitmap() {
        if (!_isStarted) return null;
        return _scenePixelBuffer.getBitmap();
    }

    public void StartPreview(int width, int height, float fova) {
        //todo:
        _scenePixelBuffer = new ScenePixelBuffer(width, height, _renderer);
        //_scenePixelBuffer.setRenderer(_renderer);
        _isStarted = true;
    }

    public void StopPreview() {
        //todo:
        _isStarted = false;
        _scenePixelBuffer.Stop();
        _scenePixelBuffer = null;
    }

    public void PrepareTexture() {
        //todo: implement after
    }
}
