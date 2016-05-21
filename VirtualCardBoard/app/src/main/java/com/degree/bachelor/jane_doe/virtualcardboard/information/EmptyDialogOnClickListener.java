package com.degree.bachelor.jane_doe.virtualcardboard.information;

import android.content.DialogInterface;

/**
 * Created by Jane-Doe on 5/21/2016.
 */
public class EmptyDialogOnClickListener
    implements DialogInterface.OnClickListener
{
    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        //do nothing
    }

    public static EmptyDialogOnClickListener GetOne() {
        return new EmptyDialogOnClickListener();
    }
}
