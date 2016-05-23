package com.degree.bachelor.jane_doe.virtualcardboard.information;

import android.content.DialogInterface;

/**
 * Created by Jane-Doe on 5/21/2016.
 */
public class EmptyDialogOnClickListener
    //implements DialogInterface.OnClickListener
{
    private static class _EmptyDialogOnClickListener
            implements DialogInterface.OnClickListener
    {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            //do nothing
        }
    }

    private static final _EmptyDialogOnClickListener _emptyDialogOnClickListener = new _EmptyDialogOnClickListener();

    public static DialogInterface.OnClickListener GetOne() {
        return _emptyDialogOnClickListener;
    }
}
