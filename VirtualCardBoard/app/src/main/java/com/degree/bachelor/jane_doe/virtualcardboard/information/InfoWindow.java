package com.degree.bachelor.jane_doe.virtualcardboard.information;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;

/**
 * Created by Jane-Doe on 5/21/2016.
 */
public class InfoWindow {
    private static final String _default = "Be sure that something happened ^_^";

    ///@param msg - info message, can be null
    public static void Show(Context context, String msg, Runnable callback)
    {
        if (msg == null) {
            msg = _default;
        }

        Dialog.OnClickListener onClickListener;
        if (callback == null) {
            onClickListener = EmptyDialogOnClickListener.GetOne();
        } else {
            onClickListener = new CallbackDialogOnClickListener(callback);
        }

        AlertDialog.Builder dialog = new AlertDialog.Builder(context);

        dialog.setTitle(InfoWindow.class.getCanonicalName())
            .setMessage(msg)
            .setIcon(android.R.drawable.ic_dialog_info)
            .setPositiveButton(android.R.string.ok, onClickListener);

        dialog.show();
    }
}
