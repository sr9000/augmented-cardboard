package com.degree.bachelor.jane_doe.virtualcardboard;

import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;

/**
 * Created by Jane-Doe on 4/16/2016.
 */
public class BinocularView {

    private int _focusDistance, _focusVerticalCoordinate;
    private int _focusViewHeight, _focusViewWidth;
    private int _displayHeight, _displayWidth;

    private Rect _leftViewFrom, _rightViewFrom;
    private Rect _leftViewWhere, _rightViewWhere;
    private int _leftCenterX, _leftCenterY;
    private int _rightCenterX, _rightCenterY;

    private Rect _adaptedLeftViewFrom, _adaptedRightViewFrom;//todo
    private Rect _adaptedLeftViewWhere, _adaptedRightViewWhere;

    private static class CalcAdaptedViewsHelper {
        static public Point transition(Point target, int tw, int th, int sw, int sh, double scale) {
            Point res = new Point();
            res.x = ((int)((target.x - tw / 2) * scale)) + sw / 2;
            res.y = ((int)((target.y - th / 2) * scale)) + sh / 2;
            return res;
        }
        static public PointF relative(Point abs, int l, int t, int r, int b) {
            PointF rel = new PointF();
            rel.x = ((float)(abs.x - l)) / ((float)(r - l));
            rel.y = ((float)(abs.y - t)) / ((float)(b - t));
            return rel;
        }
        static public PointF relative(PointF abs, float l, float t, float r, float b) {
            PointF rel = new PointF();
            rel.x = (abs.x - l) / (r - l);
            rel.y = (abs.y - t) / (b - t);
            return rel;
        }
        static public Rect correct(Rect abs, RectF rel, int ml, int mt, int mr, int mb) {
            Rect nabs = new Rect();
            nabs.left = Math.max(ml, (int)((abs.right - abs.left) * rel.left + abs.left));
            nabs.top = Math.max(mt, (int)((abs.bottom - abs.top) * rel.top + abs.top));
            nabs.right = Math.min(mr, (int)((abs.right - abs.left) * rel.right + abs.left));
            nabs.bottom = Math.min(mb, (int)((abs.bottom - abs.top) * rel.bottom + abs.top));
            return nabs;
        }
    }

    //adapt real size of source bitmap to _focusViewWidth & _focusViewHeight sizes
    public void CalcAdaptedViews(int width, int height) {
        double targetRatio = ((double)_focusViewWidth)/((double)_focusViewHeight);
        double srcRation = ((double)width)/((double)height);

        //calc scale
        double scale = 0;
        if (targetRatio > srcRation)
            scale = ((double)height)/((double)_focusViewHeight);
        else
            scale = ((double)width)/((double)_focusViewWidth);

        //convert coordinates of "from" in source bitmap notation
        Rect leftFromAtSrc = new Rect();
        {
            Point p1 = CalcAdaptedViewsHelper.transition(new Point(_leftViewFrom.left, _leftViewFrom.top)
                    ,_focusViewWidth, _focusViewHeight, width, height, scale);
            Point p2 = CalcAdaptedViewsHelper.transition(new Point(_leftViewFrom.right, _leftViewFrom.bottom)
                    ,_focusViewWidth, _focusViewHeight, width, height, scale);
            leftFromAtSrc.set(p1.x, p1.y, p2.x, p2.y);
        }
        Rect rightFromAtSrc = new Rect();
        {
            Point p1 = CalcAdaptedViewsHelper.transition(new Point(_rightViewFrom.left, _rightViewFrom.top)
                    , _focusViewWidth, _focusViewHeight, width, height, scale);
            Point p2 = CalcAdaptedViewsHelper.transition(new Point(_rightViewFrom.right, _rightViewFrom.bottom)
                    , _focusViewWidth, _focusViewHeight, width, height, scale);
            rightFromAtSrc.set(p1.x, p1.y, p2.x, p2.y);
        }

        //calculate relative coordinates
        RectF leftRelFromAtSrc = new RectF();
        {
            PointF p1 = CalcAdaptedViewsHelper.relative(new Point(leftFromAtSrc.left, leftFromAtSrc.top)
                    , 0, 0, _focusViewWidth, _focusViewHeight);
            PointF p2 = CalcAdaptedViewsHelper.relative(new Point(leftFromAtSrc.right, leftFromAtSrc.bottom)
                    , 0, 0, _focusViewWidth, _focusViewHeight);
            leftRelFromAtSrc.set(p1.x, p1.y, p2.x, p2.y);
        }
        RectF rightRelFromAtSrc = new RectF();
        {
            PointF p1 = CalcAdaptedViewsHelper.relative(new Point(rightFromAtSrc.left, rightFromAtSrc.top)
                    , 0, 0, _focusViewWidth, _focusViewHeight);
            PointF p2 = CalcAdaptedViewsHelper.relative(new Point(rightFromAtSrc.right, rightFromAtSrc.bottom)
                    , 0, 0, _focusViewWidth, _focusViewHeight);
            rightRelFromAtSrc.set(p1.x, p1.y, p2.x, p2.y);
        }

        //norm relatuive coordinats
        RectF correctLeftRelFromAtSrc = new RectF(
                Math.max(0.0f, leftRelFromAtSrc.left)
                , Math.max(0.0f, leftRelFromAtSrc.top)
                , Math.min(1.0f, leftRelFromAtSrc.right)
                , Math.min(1.0f, leftRelFromAtSrc.bottom)
        );
        RectF correctRightRelFromAtSrc = new RectF(
                Math.max(0.0f, rightRelFromAtSrc.left)
                , Math.max(0.0f, rightRelFromAtSrc.top)
                , Math.min(1.0f, rightRelFromAtSrc.right)
                , Math.min(1.0f, rightRelFromAtSrc.bottom)
        );

        //find correction coordinates
        RectF leftCorrectProportion = new RectF();
        {
            PointF p1 = CalcAdaptedViewsHelper.relative(new PointF(correctLeftRelFromAtSrc.left, correctLeftRelFromAtSrc.top)
                    , leftRelFromAtSrc.left, leftRelFromAtSrc.top
                    , leftRelFromAtSrc.right, leftRelFromAtSrc.bottom);
            PointF p2 = CalcAdaptedViewsHelper.relative(new PointF(correctLeftRelFromAtSrc.right, correctLeftRelFromAtSrc.bottom)
                    , leftRelFromAtSrc.left, leftRelFromAtSrc.top
                    , leftRelFromAtSrc.right, leftRelFromAtSrc.bottom);
            leftCorrectProportion.set(p1.x, p1.y, p2.x, p2.y);
        }
        RectF rightCorrectProportion = new RectF();
        {
            PointF p1 = CalcAdaptedViewsHelper.relative(new PointF(correctRightRelFromAtSrc.left, correctRightRelFromAtSrc.top)
                    , rightRelFromAtSrc.left, rightRelFromAtSrc.top
                    , rightRelFromAtSrc.right, rightRelFromAtSrc.bottom);
            PointF p2 = CalcAdaptedViewsHelper.relative(new PointF(correctRightRelFromAtSrc.right, correctRightRelFromAtSrc.bottom)
                    , rightRelFromAtSrc.left, rightRelFromAtSrc.top
                    , rightRelFromAtSrc.right, rightRelFromAtSrc.bottom);
            rightCorrectProportion.set(p1.x, p1.y, p2.x, p2.y);
        }

        //correct all coordinates
        _adaptedLeftViewFrom = CalcAdaptedViewsHelper.correct(leftFromAtSrc, leftCorrectProportion, 0, 0, _focusViewWidth, _focusViewHeight);
        _adaptedLeftViewWhere = CalcAdaptedViewsHelper.correct(_leftViewWhere, leftCorrectProportion, 0, 0, _displayWidth, _displayHeight);

        _adaptedRightViewFrom = CalcAdaptedViewsHelper.correct(rightFromAtSrc, rightCorrectProportion, 0, 0, _focusViewWidth, _focusViewHeight);
        _adaptedRightViewWhere = CalcAdaptedViewsHelper.correct(_rightViewWhere, rightCorrectProportion, 0, 0, _displayWidth, _displayHeight);
    }

    public class BinocularInfo {
        public int eyeViewHeight, eyeViewWidth;
        public int simpleViewHeight, simpleViewWidth;

        public int leftCenterX, leftCenterY;
        public int rightCenterX, rightCenterY;
    }

    public BinocularInfo getBinocularInfo() {
        BinocularInfo info = new BinocularInfo();

        info.simpleViewHeight = _focusViewHeight;
        info.simpleViewWidth = _focusViewWidth;

        info.eyeViewHeight = _leftViewFrom.height();
        info.eyeViewWidth = _leftViewFrom.width();

        info.rightCenterX = _rightCenterX;
        info.rightCenterY = _rightCenterY;

        info.leftCenterX = _leftCenterX;
        info.leftCenterY = _leftCenterY;

        return info;
    }

    public BinocularView(int displayWidth, int displayHeight) {
        _displayHeight = displayHeight;
        _displayWidth = displayWidth;

        SetDefaultBinocularParams();
    }

    public void SetDisplaySizes(int displayWidth, int displayHeight) {
        _displayHeight = displayHeight;
        _displayWidth = displayWidth;

        SetDefaultBinocularParams();
    }

    public void SetDefaultBinocularParams() {
        _focusDistance = _displayWidth / 2;
        _focusVerticalCoordinate = _displayHeight / 2;

        _focusViewWidth = _displayWidth / 2;
        _focusViewHeight = _displayHeight;

        CalcRectangles();
    }

    public void SetCustomBinocularParams(int focusDistance, int focusVerticalCoordinate, int focusViewWidth, int focusViewHeight) {
        _focusDistance = focusDistance;
        _focusVerticalCoordinate = focusVerticalCoordinate;

        _focusViewWidth = focusViewWidth;
        _focusViewHeight = focusViewHeight;

        CalcRectangles();
    }

    private static int BoundValue(int min, int val, int max) {
        return Math.min(Math.max(val, min), max);
    }

    private void VerificateBinocularParams() {
        _focusDistance = BoundValue(0, _focusDistance, _displayWidth);
        _focusVerticalCoordinate = BoundValue(0, _focusVerticalCoordinate, _displayHeight);

        _focusViewHeight = BoundValue(0, _focusViewHeight,
                Math.min(_focusVerticalCoordinate, _displayHeight - _focusVerticalCoordinate));
        _focusViewWidth = BoundValue(0, _focusViewWidth,
                Math.max(_focusDistance, _displayWidth - _focusDistance));
    }

    private void CalcRectangles() {
        VerificateBinocularParams();
        
        _leftCenterX = Math.min(_focusViewWidth / 2, (_displayWidth - _focusDistance) / 2);
        _rightCenterX = Math.min(_focusViewWidth / 2, _focusDistance / 2);

        _leftCenterY = _rightCenterY =_focusViewHeight / 2;

        _leftViewFrom = new Rect(
                (_focusViewWidth / 2) - _leftCenterX //left
                , 0 //top
                , (_focusViewWidth / 2) + Math.min(_focusViewWidth / 2, _focusDistance / 2) //right
                , _focusViewHeight); //bottom

        _rightViewFrom = new Rect(
                (_focusViewWidth / 2) - _rightCenterX //left
                , 0 //top
                , (_focusViewWidth / 2) + Math.min(_focusViewWidth / 2, (_displayWidth - _focusDistance) / 2) //right
                , _focusViewHeight); //bottom

        _leftViewWhere = new Rect(
                (_displayWidth - _focusDistance) / 2 - _leftCenterX //left
                , _focusVerticalCoordinate - _focusViewHeight / 2 //top
                , (_displayWidth - _focusDistance) / 2 + Math.min(_focusViewWidth / 2, _focusDistance / 2) //right
                , _focusVerticalCoordinate + _focusViewHeight / 2); //bottom

        _rightViewWhere = new Rect(
                ((_displayWidth + _focusDistance) / 2) - _rightCenterX //left
                , _focusVerticalCoordinate - _focusViewHeight / 2 //top
                , (_displayWidth + _focusDistance) / 2 + Math.min(_focusViewWidth / 2, (_displayWidth - _focusDistance) / 2) //right
                , _focusVerticalCoordinate + _focusViewHeight / 2); //bottom
    }
}
