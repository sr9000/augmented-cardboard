package com.degree.bachelor.jane_doe.virtualcardboard.information;

import android.app.AlertDialog;
import android.content.Context;

/**
 * Created by Jane-Doe on 5/21/2016.
 */
public class ErrorWindow {
    private static class ExitImmediately implements Runnable {
        @Override
        public void run() {
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

    private static final ExitImmediately _exitImmediately = new ExitImmediately();
    private static final String _default = "Be sure that bad thing happened X_X";

    ///@param msg - info message, can be null
    public static void Show(Context context, String msg)
    {
        if (msg == null) {
            msg = _default;
        }

        AlertDialog.Builder dialog = new AlertDialog.Builder(context);

        dialog.setTitle(ErrorWindow.class.getCanonicalName())
                .setMessage(msg)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.ok, new CallbackDialogOnClickListener(_exitImmediately));

        dialog.show();
    }
}
