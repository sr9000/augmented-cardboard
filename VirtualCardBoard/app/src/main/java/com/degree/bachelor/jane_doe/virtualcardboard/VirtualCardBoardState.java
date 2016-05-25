package com.degree.bachelor.jane_doe.virtualcardboard;

import android.content.Context;
import android.os.Vibrator;
import android.view.View;

import com.degree.bachelor.jane_doe.virtualcardboard.network.VCMessage;

/**
 * Created by Jane-Doe on 5/25/2016.
 */
public class VirtualCardBoardState {
    private View _view;
    private Context _context;

    private VirtualCardBoardState(){}

    public VirtualCardBoardState(Context context, View view) {
        _view = view;
        _context = context;
    }

    public void ProceedRequestMessage(VCMessage msg) {
        if (msg == null) {
            return;
        }

        switch (msg.GetType()) {
            case Ping:
                PingVibrateResponse();
                break;
        }
    }

    private void PingVibrateResponse() {
        _view.getHandler().post(new Runnable() {
            @Override
            public void run() {
                Vibrator _vibrator = (Vibrator) _context.getSystemService(Context.VIBRATOR_SERVICE);
                _vibrator.vibrate(new long[]{10, 500}, -1);
            }
        });
    }

}
