package com.degree.bachelor.jane_doe.virtualcardboard;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.ViewGroup;

import com.degree.bachelor.jane_doe.virtualcardboard.information.CallbackDialogOnClickListener;
import com.degree.bachelor.jane_doe.virtualcardboard.information.EmptyDialogOnClickListener;
import com.degree.bachelor.jane_doe.virtualcardboard.open_gl_renders.GlSurfaceHolder;

public class MainActivity extends Activity
{
    @Override
    protected void onCreate(Bundle bundleState) {
        super.onCreate(bundleState);

        InfoWindow.mainActivity = this;
        FatalErrorWindow.mainActivity = this;

        GlSurfaceHolder tHolder = new GlSurfaceHolder(this);
        setContentView(new DrawView(this, tHolder));

        getWindow().addContentView(tHolder.getView(), new ViewGroup.LayoutParams(1, 1));
    }

    public static class InfoWindow {
        private static final String _default = "Be sure that something happened ^_^";
        private static Activity mainActivity;

        ///@param msg - info message, can be null
        public static void Show(final Context context, final String msg, final Runnable callback)
        {
            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    _Show(context, msg, callback);
                }
            });
        }

        private static void _Show(Context context, String msg, Runnable callback)
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

    public static class FatalErrorWindow {
        private static class ExitImmediately implements Runnable {
            @Override
            public void run() {
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        }

        private static final ExitImmediately _exitImmediately = new ExitImmediately();
        private static final String _default = "Be sure that bad thing happened X_X";
        private static Activity mainActivity;

        ///@param msg - info message, can be null
        public static void Show(final Context context, final String msg)
        {
            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    _Show(context, msg);
                }
            });
        }

        private static void _Show(Context context, String msg)
        {
            if (msg == null) {
                msg = _default;
            }

            AlertDialog.Builder dialog = new AlertDialog.Builder(context);

            dialog.setTitle(FatalErrorWindow.class.getCanonicalName())
                    .setMessage(msg)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.ok, new CallbackDialogOnClickListener(_exitImmediately));

            dialog.show();
        }
    }
}
