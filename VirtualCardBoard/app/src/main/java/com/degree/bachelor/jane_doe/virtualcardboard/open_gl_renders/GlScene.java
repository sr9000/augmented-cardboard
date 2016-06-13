package com.degree.bachelor.jane_doe.virtualcardboard.open_gl_renders;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLU;
import android.opengl.GLUtils;

import com.degree.bachelor.jane_doe.virtualcardboard.R;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL11;
import javax.microedition.khronos.opengles.GL11;


/**
 * Created by Jane-Doe on 5/28/2016.
 */
public class GlScene  {
    private GlSurfaceHolder _glSurfaceHolder;
    private boolean _isStarted;

    public GlScene(GlSurfaceHolder glSurfaceHolder, Activity activity) {
        _glSurfaceHolder = glSurfaceHolder;
        _isStarted = false;
        _glSurfaceHolder.getSceneRendererManager().SetGlMethods(new _glSetupView(), new _glOnDraw(activity));
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
        public void run(GL11 gl, int width, int height) {
            gl.glViewport(0, 0, width, height);     //Reset The Current Viewport
            gl.glMatrixMode(GL11.GL_PROJECTION);    //Select The Projection Matrix
            gl.glLoadIdentity();                    //Reset The Projection Matrix

            //Calculate The Aspect Ratio Of The Window
            GLU.gluPerspective(gl, 45.0f, (float)width / (float)height, 0.1f, 100.0f);

            gl.glMatrixMode(GL11.GL_MODELVIEW);     //Select The Modelview Matrix
            gl.glLoadIdentity();                    //Reset The Modelview Matrix*/
        }
    }

    private class _glOnDraw implements ISceneRendererManager.GlOnDrawRunnable, SensorEventListener {

        private volatile Sensor _orientation;
        private volatile GlCameraMath _camMath;
        private volatile boolean _isSet = false;
        private volatile Bitmap _algorithm;
        private volatile int[] textures;
        private volatile int mTextureId = -1;
        private volatile boolean _isTexInit = false;

        private volatile float[] vector = new float[3];
        private volatile float[] center = new float[3];
        private volatile float[] up = new float[3];

        public _glOnDraw(Activity activity) {
            _algorithm = BitmapFactory.decodeResource(activity.getResources(), R.drawable.gcdtrp2rs);
            int w = _algorithm.getWidth();
            int h = _algorithm.getHeight();
            SensorManager sensorManager = (SensorManager)activity.getSystemService(Context.SENSOR_SERVICE);
            _orientation = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
            _camMath = new GlCameraMath();
            sensorManager.registerListener(this, _orientation, SensorManager.SENSOR_DELAY_UI);
        }

        @Override
        public void run(GL11 gl) {
            int err;
            if (!_isTexInit) {
                err = gl.glGetError();
                gl.glEnable(GL11.GL_TEXTURE_2D);
                err = gl.glGetError();
                textures = new int[1];
                gl.glGenTextures(1, textures, 0);
                err = gl.glGetError();
                mTextureId = textures[0];

                gl.glBindTexture(GL11.GL_TEXTURE_2D, mTextureId);
                err = gl.glGetError();
                // Create Nearest Filtered Texture
                gl.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER,
                        GL11.GL_LINEAR);
                err = gl.glGetError();
                gl.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER,
                        GL11.GL_LINEAR);
                err = gl.glGetError();

                // Different possible texture parameters, e.g. GL11.GL_CLAMP_TO_EDGE
                gl.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S,
                        GL11.GL_CLAMP_TO_EDGE);
                err = gl.glGetError();
                gl.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T,
                        GL11.GL_CLAMP_TO_EDGE);
                err = gl.glGetError();

                gl.glTexEnvf(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_REPLACE);
                err = gl.glGetError();
                // Use the Android GLUtils to specify a two-dimensional texture image
                // from our bitmap
                GLUtils.texImage2D(GL11.GL_TEXTURE_2D, 0, _algorithm, 0);
                err = gl.glGetError();
                gl.glFlush();
                err = gl.glGetError();

                _algorithm.recycle();
                _isTexInit = true;
            }
            // clear Screen and Depth Buffer
            gl.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
            err = gl.glGetError();

            // Reset the Modelview Matrix
            gl.glLoadIdentity();
            err = gl.glGetError();
            // Drawing
            //gl.glTranslatef(0.0f, 0.0f, -5.0f);     // move 5 units INTO the screen
            // is the same as moving the camera 5 units away
            //synchronized (_synvVector) {

            //}
            GLU.gluLookAt(gl, 0f, 0f, 0f, center[0], center[1], center[2], up[0], up[1], up[2]);
            err = gl.glGetError();



            {// Draw the triangle
                FloatBuffer vertexBuffer;   // buffer holding the vertices
                float vertices[] = {
                          -1f,   -1f, 5f,        // V1 - first vertex (x,y,z)
                         1.5f,   -1f, 5f,        // V2 - second vertex
                         1.5f,  2.0f, 5f         // V3 - third vertex
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

                FloatBuffer texBuffer;   // buffer holding the vertices
                float texices[] = {
                        0.0f,  0.0f,        // V1 - first vertex (x,y,z)
                        0.0f,  1.0f,        // V2 - second vertex
                        1.0f,  1.0f         // V3 - third vertex
                };
                // a float has 4 bytes so we allocate for each coordinate 4 bytes
                ByteBuffer texByteBuffer = ByteBuffer.allocateDirect(texices.length * 4);
                texByteBuffer.order(ByteOrder.nativeOrder());
                // allocates the memory from the byte buffer
                texBuffer = texByteBuffer.asFloatBuffer();
                // fill the vertexBuffer with the vertices
                texBuffer.put(texices);
                // set the cursor position to the beginning of the buffer
                texBuffer.position(0);

                ShortBuffer indexBuffer;   // buffer holding the vertices
                short indices[] = {
                        2,        // V1 - first vertex (x,y,z)
                        1,        // V2 - second vertex
                        0         // V3 - third vertex
                };
                // a float has 4 bytes so we allocate for each coordinate 4 bytes
                ByteBuffer indexByteBuffer = ByteBuffer.allocateDirect(vertices.length * 2);
                indexByteBuffer.order(ByteOrder.nativeOrder());
                // allocates the memory from the byte buffer
                indexBuffer = indexByteBuffer.asShortBuffer();
                // fill the vertexBuffer with the vertices
                indexBuffer.put(indices);
                // set the cursor position to the beginning of the buffer
                indexBuffer.position(0);

                // Counter-clockwise winding.
                gl.glFrontFace(GL11.GL_CCW);
                err = gl.glGetError();
                gl.glEnable(GL11.GL_CULL_FACE);
                err = gl.glGetError();
                gl.glCullFace(GL11.GL_BACK);
                err = gl.glGetError();
                gl.glEnable(GL11.GL_TEXTURE_2D);
                err = gl.glGetError();

                // Tell OpenGL to enable the use of UV coordinates.
                gl.glEnableClientState(GL11.GL_VERTEX_ARRAY);
                err = gl.glGetError();
                gl.glVertexPointer(3, GL11.GL_FLOAT, 0, vertexBuffer); //Important
                err = gl.glGetError();
                gl.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
                err = gl.glGetError();
                gl.glTexCoordPointer(2, GL11.GL_FLOAT, 0, texBuffer); //Important
                err = gl.glGetError();


                gl.glBindTexture(GL11.GL_TEXTURE_2D, mTextureId);
                gl.glTexEnvf(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_REPLACE);

                gl.glDrawElements(GL11.GL_TRIANGLES, indices.length,
                        GL11.GL_UNSIGNED_SHORT, indexBuffer);

                gl.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
                gl.glDisableClientState(GL11.GL_VERTEX_ARRAY);
                gl.glDisable(GL11.GL_CULL_FACE);
            }
        }

        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            //vector[0] = sensorEvent.values[1];
            //vector[1] = -sensorEvent.values[0];
            //vector[2] = -sensorEvent.values[2];
            if (_isSet) {
                _camMath.GetTransformedCenterAndUpVectors(sensorEvent.values, center, up);
            } else {
                _camMath.SetUpCenter(sensorEvent.values);
                _isSet = true;
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) { }
    }
}
