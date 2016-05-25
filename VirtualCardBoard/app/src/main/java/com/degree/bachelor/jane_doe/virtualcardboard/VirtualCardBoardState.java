package com.degree.bachelor.jane_doe.virtualcardboard;

import android.content.Context;
import android.os.Vibrator;
import android.view.View;

import com.degree.bachelor.jane_doe.virtualcardboard.network.IModeMessageData;
import com.degree.bachelor.jane_doe.virtualcardboard.network.VCMessage;

/**
 * Created by Jane-Doe on 5/25/2016.
 */
public class VirtualCardBoardState {
    private View _view;
    private Context _context;

    public volatile Mode _virtualCardBoardMode;
    private final Object _syncMode = new Object();

    private VirtualCardBoardState(){}

    public enum Mode {
        NoPic, Settings, Pic
    }

    public VirtualCardBoardState(Context context, View view) {
        _view = view;
        _context = context;
        _virtualCardBoardMode = Mode.Settings;
    }

    public VirtualCardBoardState SetMode(Mode newMode) {
        synchronized (_syncMode) {
            _virtualCardBoardMode = newMode;
        }
        return this;
    }

    public Mode GetMode() {
        return _virtualCardBoardMode;
    }

    public void ProceedRequestMessage(VCMessage msg) {
        if (msg == null) {
            return;
        }

        switch (msg.GetType()) {
            case Ping:
                PingVibrateResponse();
                break;
            case Mode:
                ModeSetFromMessage(msg.GetData());
                break;
        }
    }

    private void ModeSetFromMessage(IModeMessageData idata) {
        switch (idata.GetMode()) {
            case Pic:
                SetMode(Mode.Pic);
                break;
            case NoPic:
                SetMode(Mode.NoPic);
                break;
            case Settings:
                SetMode(Mode.Settings);
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
