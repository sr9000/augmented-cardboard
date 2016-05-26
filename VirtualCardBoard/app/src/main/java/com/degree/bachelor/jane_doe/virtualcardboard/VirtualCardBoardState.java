package com.degree.bachelor.jane_doe.virtualcardboard;

import android.content.Context;
import android.os.Vibrator;
import android.view.View;

import com.degree.bachelor.jane_doe.virtualcardboard.network.IModeMessageData;
import com.degree.bachelor.jane_doe.virtualcardboard.network.ISettingsMessageData;
import com.degree.bachelor.jane_doe.virtualcardboard.network.MessageDataContainer;
import com.degree.bachelor.jane_doe.virtualcardboard.network.PcInterface;
import com.degree.bachelor.jane_doe.virtualcardboard.network.VCMessage;

/**
 * Created by Jane-Doe on 5/25/2016.
 */
public class VirtualCardBoardState {
    private final float proportionFocusDistance = 2.0f/3.0f, proportionVerticalCoordinate = 0.5f;

    private View _view;
    private Context _context;
    private volatile CameraDemo _cameraDemo;
    private volatile BinocularView _binocularView;
    private volatile BinocularView.BinocularInfo _binocularInfo;
    private volatile PcInterface _pcInterface;

    public volatile Mode _virtualCardBoardMode;
    private final Object _syncMode = new Object();

    public enum Mode {
        NoPic, Settings, Pic
    }

    private VirtualCardBoardState(){}

    public VirtualCardBoardState(Context context, View view, PcInterface pcInterface) {
        _view = view;
        _context = context;
        _pcInterface = pcInterface;
        _virtualCardBoardMode = Mode.Settings;

        _binocularView = new BinocularView(0, 0);
        _binocularInfo = _binocularView.GetBinocularInfo();
        _cameraDemo = new CameraDemo();
    }

    private void _UpdateBinocularParams(int focusDistance, int focusVerticalPosition, int simpleWidth, int simpleHeight) {
        synchronized (_syncMode) {
            if (_virtualCardBoardMode != Mode.Settings) {
                _cameraDemo.StopPreview();
            }

            _binocularView.SetCustomBinocularParams(focusDistance
                    , focusVerticalPosition
                    , simpleWidth
                    , simpleHeight);
            _binocularInfo = _binocularView.GetBinocularInfo();

            if (_virtualCardBoardMode != Mode.Settings) {
                _EnableCamera();
            }
        }
    }

    private void _EnableCamera() {
        BinocularView.BinocularInfo info = _binocularView.GetBinocularInfo();
        _cameraDemo.StartPreview(info.simpleViewWidth, info.simpleViewHeight);
        _binocularView.CalcAdaptedViews(_cameraDemo.GetWidth(), _cameraDemo.GetHeight());
        _binocularInfo = _binocularView.GetBinocularInfo();
    }

    public CameraDemo GetCameraDemo() {
        return _cameraDemo;
    }

    public BinocularView.BinocularInfo GetBinocularInfo() {
        return _binocularInfo;
    }

    public void StopProcesses() {
        synchronized (_syncMode) {
            _cameraDemo.StopPreview();
        }
    }

    public void InitSurfaceSizes(int width, int height) {
        int focusDistance = ((int) (width * proportionFocusDistance));
        int verticalCoordinate = ((int) (height * proportionVerticalCoordinate));

        _binocularView.SetDisplaySizes(width, height);
        _binocularView.SetCustomBinocularParams(focusDistance, verticalCoordinate, width, height);
        _binocularInfo = _binocularView.GetBinocularInfo();

        synchronized (_syncMode) {
            if (_virtualCardBoardMode != Mode.Settings) {
                _EnableCamera();
            }
        }
    }

    public void UpdateSurfaceSizes(int width, int height) {
        synchronized (_syncMode) {
            if (_virtualCardBoardMode != Mode.Settings) {
                _cameraDemo.StopPreview();
            }

            _binocularView.SetDisplaySizes(width, height);
            _binocularView.SetCustomBinocularParams(_binocularInfo.focusDistance
                    , _binocularInfo.focusVerticalCoordinate
                    , _binocularInfo.simpleViewWidth
                    , _binocularInfo.simpleViewHeight);
            _binocularInfo = _binocularView.GetBinocularInfo();

            if (_virtualCardBoardMode != Mode.Settings) {
                _EnableCamera();
            }
        }
    }

    public VirtualCardBoardState SetMode(Mode newMode) {
        synchronized (_syncMode) {
            _virtualCardBoardMode = newMode;

            if (_virtualCardBoardMode == Mode.Settings) {
                _cameraDemo.StopPreview();
            } else {
                _EnableCamera();
            }
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
            case Settings:
                SettingsProceed(msg.GetData());
                break;
        }
    }

    private void SettingsProceed(ISettingsMessageData data) {
        if ((data.GetFlags() & MessageDataContainer._mission_inform) != 0) {
            //skip
        }
        if ((data.GetFlags() & MessageDataContainer._mission_assign) != 0) {
            _UpdateBinocularParams(data.GetFocusDistance()
                , data.GetFocusVerticalCoordinate()
                , data.GetSimpleViewWidth()
                , data.GetSimpleViewHeight());
        }
        if ((data.GetFlags() & MessageDataContainer._mission_request) != 0) {
            _pcInterface.SendVCMessage(VCMessage.GetSettingsMessage(GetBinocularInfo())
                , data.GetRemoteAddress()
                , data.GetRemotePort());
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
