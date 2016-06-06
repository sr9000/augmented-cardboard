package com.degree.bachelor.jane_doe.virtualcardboard.open_gl_renders;

/**
 * Created by Jane-Doe on 5/28/2016.
 */
public class GlCameraMath {
    private float a, x0, y0, z0;
    public void SetUpCenter(float[] q0)
    {
        x0 = q0[0];
        y0 = q0[1];
        z0 = q0[2];
        a  = (float) Math.sqrt(Math.max(0f, 1f - x0*x0 - y0*y0 - z0*z0));
    }

    ///non transformed vectors
    ///eye coords    = 0,0,0
    ///center coords = 0,0,1
    ///up coords     = 0,1,0
    public void GetTransformedCenterAndUpVectors(float[] qc, float[] outCenter, float[] outUp)
    {
        float xc = qc[0];
        float yc = qc[1];
        float zc = qc[2];
        float b  = (float) Math.sqrt(Math.max(0f, 1f - xc*xc - yc*yc - zc*zc));

        float p0, i0, j0, k0;

        p0 =   a*b + x0*xc + y0*yc + z0*zc;
        i0 = -b*x0 +  a*xc + yc*z0 - y0*zc;
        j0 = -b*y0 +  a*yc - xc*z0 + x0*zc;
        k0 = xc*y0 - x0*yc -  b*z0 +  a*zc;

        float pc, ic, jc, kc;

        pc = a*p0 + i0*x0 + j0*y0 + k0*z0;
        ic = a*i0 - p0*x0 - k0*y0 + j0*z0;
        jc = a*j0 + k0*x0 - p0*y0 - i0*z0;
        kc = a*k0 - j0*x0 + i0*y0 - p0*z0;

        float w, x, y, z;
        w = pc*a - ic*x0 - jc*y0 - kc*z0;
        x = ic*a + pc*x0 - kc*y0 + jc*z0;
        y = jc*a + kc*x0 + pc*y0 - ic*z0;
        z = kc*a - jc*x0 + ic*y0 + pc*z0;

        outCenter[0] = 2.0f * (x*z + y*w);
        outCenter[1] = 2.0f * (y*z - x*w);
        outCenter[2] = z*z + w*w - x*x - y*y;

        outUp[0] = 2.0f * (x*y - z*w);
        outUp[1] = y*y + w*w - x*x - z*z;
        outUp[2] = 2.0f * (x*w + y*z);
    }
}
