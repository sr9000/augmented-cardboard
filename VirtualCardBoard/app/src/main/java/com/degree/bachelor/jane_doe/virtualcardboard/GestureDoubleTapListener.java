package com.degree.bachelor.jane_doe.virtualcardboard;

import android.view.GestureDetector;
import android.view.MotionEvent;

/**
 * Created by Jane-Doe on 5/25/2016.
 */
public class GestureDoubleTapListener extends GestureDetector.SimpleOnGestureListener {
    private Runnable _doubleTapCallback;
    private Runnable _longPressCallback;

    private GestureDoubleTapListener(){}

    public GestureDoubleTapListener(Runnable doubleTapCallback, Runnable longPressCallback) {
        if (doubleTapCallback == null) {
            //do nothing
            _doubleTapCallback = new Runnable() {
                @Override
                public void run() { }
            };
        } else {
            _doubleTapCallback = doubleTapCallback;
        }

        if (longPressCallback == null) {
            //do nothing
            _longPressCallback = new Runnable() {
                @Override
                public void run() { }
            };
        } else {
            _longPressCallback = longPressCallback;
        }
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        _doubleTapCallback.run();
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        _longPressCallback.run();
    }
}
