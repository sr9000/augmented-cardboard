package com.degree.bachelor.jane_doe.virtualcardboard.open_gl_renders;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Jane-Doe on 6/5/2016.
 */
public interface ISceneRendererManager {
    interface GlSetupRunnable {
        void run(GL10 gl, int width, int height);
    }

    interface GlOnDrawRunnable {
        void run(GL10 gl);
    }

    ISceneRendererManager SetupGl(int width, int height);

    ISceneRendererManager DrawingOn();

    ISceneRendererManager DrawingOff();

    ISceneRendererManager SetGlMethods(GlSetupRunnable glViewSetup, GlOnDrawRunnable glDraw);
}
