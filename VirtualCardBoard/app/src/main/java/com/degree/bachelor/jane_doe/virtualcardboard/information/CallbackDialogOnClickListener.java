package com.degree.bachelor.jane_doe.virtualcardboard.information;

import android.content.DialogInterface;

/**
 * Created by Jane-Doe on 5/23/2016.
 */
public class CallbackDialogOnClickListener
        implements DialogInterface.OnClickListener
{
    private static class EmptyRunnable implements Runnable {
        @Override
        public void run() {}
    }
    private static final Runnable _emptyCallback = new EmptyRunnable();

    private Runnable _callback;

    public CallbackDialogOnClickListener(Runnable callback) {
        if (callback == null) {
            _callback = _emptyCallback;
        } else {
            _callback = callback;
        }
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        _callback.run();
    }
}

