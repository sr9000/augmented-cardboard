package com.degree.bachelor.jane_doe.virtualcardboard;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**test implementation
 * copy paste into Binocularview
 class Point {
 public Point(){}
 public Point(int X, int Y) {
 x = X;
 y = Y;
 }
 public int x, y;
 }
 class PointF {
 public PointF(){}
 public PointF(float X, float Y) {
 x = X;
 y = Y;
 }
 public float x, y;
 }
 class Rect {
 public Rect(){}
 public Rect(Rect r) {
 left = r.left;
 top = r.top;
 right = r.right;
 bottom = r.bottom;
 }
 public Rect(int Left, int Top, int Right, int Bottom) {
 left = Left;
 top = Top;
 right = Right;
 bottom = Bottom;
 }
 public int left, top, right, bottom;
 public void set(int Left, int Top, int Right, int Bottom) {
 left = Left;
 top = Top;
 right = Right;
 bottom = Bottom;
 }
 }
 class RectF {
 public RectF(){}
 public RectF(RectF r) {
 left = r.left;
 top = r.top;
 right = r.right;
 bottom = r.bottom;
 }
 public RectF(float Left, float Top, float Right, float Bottom) {
 left = Left;
 top = Top;
 right = Right;
 bottom = Bottom;
 }
 public float left, top, right, bottom;
 public void set(float Left, float Top, float Right, float Bottom) {
 left = Left;
 top = Top;
 right = Right;
 bottom = Bottom;
 }
 }
 */


/**
 * Created by Jane-Doe on 4/18/2016.
 */
public class BinocularViewTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void binocular() throws Exception {
        BinocularView bv = new BinocularView(960, 480);

        bv.SetCustomBinocularParams(600, 300, 600, 360);
        bv.CalcAdaptedViews(600, 400);

        BinocularView.BinocularInfo info = bv.GetBinocularInfo();

        assertEquals(4, 2 + 2);

        bv.SetCustomBinocularParams(600, 300, 500, 300);
        bv.CalcAdaptedViews(120, 240);

        BinocularView.BinocularInfo info2 = bv.GetBinocularInfo();

        assertEquals(4, 2 + 2);

        bv.SetCustomBinocularParams(600, 300, 500, 300);
        bv.CalcAdaptedViews(1980, 300);

        BinocularView.BinocularInfo info3 = bv.GetBinocularInfo();

        assertEquals(4, 2 + 2);
    }
}

