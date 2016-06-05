package com.degree.bachelor.jane_doe.virtualcardboard.open_gl_renders;

/**
 * Created by Jane-Doe on 5/28/2016.
 */
public class GlCameraMath {
    private float _xq0, _yq0, _zq0, _a;
    public void SetUpCenter(float xq0, float yq0, float zq0)
    {
        _xq0 = xq0;
        _yq0 = yq0;
        _zq0 = zq0;
        _a = (float)Math.sqrt(Math.max(0.0f, 1.0f - _xq0*_xq0 - _yq0*_yq0 - _zq0*_zq0));
    }

    ///non transformed vectors
    ///eye coords    = 0,0,0
    ///center coords = 0,0,1
    ///up coords     = 0,1,0
    public void GetTransformedCenterAndUpVectors(float[] qc, float[] outCenter, float[] outUp)
    {
        float xqc = qc[0];
        float yqc = qc[1];
        float zqc = qc[2];
        float b = (float)Math.sqrt(Math.max(0.0f, 1.0f - xqc*xqc - yqc*yqc - zqc*zqc));

        float w = _a*b + _xq0*xqc + _yq0*yqc + _zq0*zqc;

        float x = _a*xqc - b*_xq0 - yqc*_zq0 + zqc*_yq0;
        float y = _a*yqc - b*_yq0 - zqc*_xq0 + xqc*_zq0;
        float z = _a*zqc - b*_zq0 - xqc*_yq0 + yqc*_xq0;

        outCenter[0] = 2.0f * (x*z + y*w);
        outCenter[1] = 2.0f * (y*z - x*w);
        outCenter[2] = z*z + w*w - x*x - y*y;

        outUp[0] = 2.0f * (x*y - z*w);
        outUp[1] = y*y + w*w - x*x - z*z;
        outUp[2] = 2.0f * (x*w + y*z);
    }
}
