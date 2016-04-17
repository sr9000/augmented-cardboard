package com.degree.bachelor.jane_doe.virtualcardboard;

import android.graphics.Rect;

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

    private static int BoundValue(int min, int val, int max) {
        return Math.min(Math.max(val, min), max);
    }

    private void VerificateBinocularParams() {
        _focusDistance = BoundValue(0, _displayWidth, _focusDistance);
        _focusVerticalCoordinate = BoundValue(0, _focusVerticalCoordinate, _displayHeight);

        _focusViewHeight = BoundValue(0, _focusViewHeight,
                Math.min(_focusVerticalCoordinate, _displayHeight - _focusVerticalCoordinate));
        _focusViewWidth = BoundValue(0, _focusViewWidth,
                Math.max(_focusDistance, _displayWidth - _focusDistance));
    }

    private void CalcRectangles() {
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
