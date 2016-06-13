package com.degree.bachelor.jane_doe.virtualcardboard.open_gl_renders;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

/**
 * Created by Jane-Doe on 6/5/2016.
 */
public interface ISceneRendererManager {
    interface GlSetupRunnable {
        void run(GL11 gl, int width, int height, float vangle);
    }

    interface GlOnDrawRunnable {
        void run(GL11 gl);
    }

    ISceneRendererManager SetupGl(int width, int height, float vangle);

    ISceneRendererManager DrawingOn();

    ISceneRendererManager DrawingOff();

    ISceneRendererManager SetGlMethods(GlSetupRunnable glViewSetup, GlOnDrawRunnable glDraw);
}
