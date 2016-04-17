package com.degree.bachelor.jane_doe.virtualcardboard;

/**
 * Created by Jane-Doe on 4/16/2016.
 */
public class BinocularView {
    private int _displayHeight, _displayWidth;

    private int _focusDistance, _focusVerticalCoordinate;
    private int _focusViewHeight, _focusViewWidth;

    private int _targetViewHeight, _targetViewWidth;

    BinocularView(int displayWidth, int displayHeight)
    {
        _displayHeight = displayHeight;
        _displayWidth = displayWidth;

        SetDefaultBinocularParams();
    }

    void SetDisplaySizes(int displayWidth, int displayHeight)
    {
        _displayHeight = displayHeight;
        _displayWidth = displayWidth;

        SetDefaultBinocularParams();
    }

    void SetDefaultBinocularParams()
    {
        _focusDistance = _displayWidth / 2;
        _focusVerticalCoordinate = _displayHeight / 2;

        _focusViewWidth = _displayWidth / 2;
        _focusViewHeight = _displayHeight;
    }

    private static int BoundValue(int min, int val, int max)
    {
        return Math.min(Math.max(val, min), max);
    }

    private void VerificateBinocularParams()
    {
        _focusDistance = BoundValue(0, _displayWidth, _focusDistance);
        _focusVerticalCoordinate = BoundValue(0, _focusVerticalCoordinate, _displayHeight);

        _focusViewHeight = BoundValue(0, _focusViewHeight,
                Math.min(_focusVerticalCoordinate, _displayHeight - _focusVerticalCoordinate));
        _focusViewWidth = BoundValue(0, _focusViewWidth,
                Math.max(_focusDistance, _displayWidth - _focusDistance));
    }
}
