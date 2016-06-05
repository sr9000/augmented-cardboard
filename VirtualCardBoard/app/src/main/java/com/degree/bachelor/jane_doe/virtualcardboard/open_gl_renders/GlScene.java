package com.degree.bachelor.jane_doe.virtualcardboard.open_gl_renders;

import android.graphics.Bitmap;
import android.opengl.GLU;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;


/**
 * Created by Jane-Doe on 5/28/2016.
 */
public class GlScene  {
    private volatile GlSurfaceHolder _glSurfaceHolder;
    private volatile boolean _isStarted;

    public GlScene(GlSurfaceHolder glSurfaceHolder) {
        _glSurfaceHolder = glSurfaceHolder;
        _isStarted = false;
        _glSurfaceHolder.getSceneRendererManager().SetGlMethods(new _glSetupView(), new _glOnDraw());
    }

    public boolean IsStarted() {
        return _isStarted;
    }

    public Bitmap GetRenderedBitmap() {
        if (!_isStarted) return null;
        return _glSurfaceHolder.getBitmap();
    }

    public void StartPreview(int width, int height, float fova) {
        //todo: fova
        _glSurfaceHolder.getSceneRendererManager().SetupGl(width, height).DrawingOn();
        //_glSurfaceHolder.setRenderer(_renderer);
        _isStarted = true;
    }

    public void StopPreview() {
        _isStarted = false;
        _glSurfaceHolder.getSceneRendererManager().DrawingOff();
    }

    public void PrepareTexture() {
        //todo: implement after
    }

    private class _glSetupView implements ISceneRendererManager.GlSetupRunnable {

        @Override
        public void run(GL10 gl, int width, int height) {
            gl.glViewport(0, 0, width, height);     //Reset The Current Viewport
            gl.glMatrixMode(GL10.GL_PROJECTION);    //Select The Projection Matrix
            gl.glLoadIdentity();                    //Reset The Projection Matrix

            //Calculate The Aspect Ratio Of The Window
            GLU.gluPerspective(gl, 45.0f, (float)width / (float)height, 0.1f, 100.0f);

            gl.glMatrixMode(GL10.GL_MODELVIEW);     //Select The Modelview Matrix
            gl.glLoadIdentity();                    //Reset The Modelview Matrix*/
        }
    }

    private class _glOnDraw implements ISceneRendererManager.GlOnDrawRunnable {

        @Override
        public void run(GL10 gl) {
            // clear Screen and Depth Buffer
            gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

            // Reset the Modelview Matrix
            gl.glLoadIdentity();
            // Drawing
            gl.glTranslatef(0.0f, 0.0f, -5.0f);     // move 5 units INTO the screen
            // is the same as moving the camera 5 units away

            {// Draw the triangle
                FloatBuffer vertexBuffer;   // buffer holding the vertices
                float vertices[] = {
                        -0.5f, -0.5f, 0.0f,        // V1 - first vertex (x,y,z)
                        0.5f, -0.5f, 0.0f,        // V2 - second vertex
                        0.0f, 0.5f, 0.0f         // V3 - third vertex
                };
                // a float has 4 bytes so we allocate for each coordinate 4 bytes
                ByteBuffer vertexByteBuffer = ByteBuffer.allocateDirect(vertices.length * 4);
                vertexByteBuffer.order(ByteOrder.nativeOrder());

                // allocates the memory from the byte buffer
                vertexBuffer = vertexByteBuffer.asFloatBuffer();

                // fill the vertexBuffer with the vertices
                vertexBuffer.put(vertices);

                // set the cursor position to the beginning of the buffer
                vertexBuffer.position(0);

                gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
                // set the colour for the triangle
                gl.glColor4f(0.0f, 1.0f, 0.0f, 0.5f);
                // Point to our vertex buffer
                gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
                // Draw the vertices as triangle strip
                gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, vertices.length / 3);
                //Disable the client state before leaving
                gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
            }
        }
    }
}
