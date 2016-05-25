package com.degree.bachelor.jane_doe.virtualcardboard.network;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Build;

import com.degree.bachelor.jane_doe.virtualcardboard.MainActivity;
import com.degree.bachelor.jane_doe.virtualcardboard.VirtualCardBoardState;
import com.degree.bachelor.jane_doe.virtualcardboard.information.FatalErrorException;
import com.degree.bachelor.jane_doe.virtualcardboard.information.WiFiManagerException;

import java.net.Inet4Address;
import java.net.UnknownHostException;

/**
 * Created by Jane-Doe on 5/21/2016.
 */
public class PcInterface
    implements IWiFiManager
{
    private static final String _impossibleError = "Fatal error with code name \"Red-Lake\". Please report it to developer!";
    private static final String _wifiDisabled = "To start you need enable wifi and connect to wifi network :-)";

    public static final byte[] Secret = new byte[]
            { (byte)207, (byte)219, (byte)43,  (byte)202
            , (byte)53,  (byte)226, (byte)172, (byte)160
            , (byte)100, (byte)227, (byte)145, (byte)120
            , (byte)187, (byte)99,  (byte)170, (byte)225};

    private Context _context;
    private RequestListenerThread _listener;
    private WifiManager _wifiManager;

    private PcInterface(){}

    public PcInterface(Context context, VirtualCardBoardState virtualCardBoardState) {
        _context = context;
        _wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        _listener = new RequestListenerThread(_context, this, virtualCardBoardState);

        _listener.start();
        _listener.SetRunning(true);
    }

    public PcInterface SendBroadcastWelcomeSignal() {
        Thread worker = new Thread(new Runnable() {
            @Override
            public void run() {
                _SendBroadcastWelcomeSignal();
            }
        });
        worker.start();
        return this;
    }

    private static String _GetDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return model;
        } else {
            return manufacturer + " " + model;
        }
    }

    private void _SendBroadcastWelcomeSignal() {
        VCMessage msg;
        try {
            msg = VCMessage.GetHelloMessage(
                    _listener.GetAddress()
                    , _listener.GetPort()
                    , _GetDeviceName());
        } catch (WiFiManagerException e) {
            MainActivity.InfoWindow.Show(_context, e.getMessage(), null);
            return;
        }

        try {
            BroadcastSender.SendMessage(this, msg);
        } catch (FatalErrorException e) {
            MainActivity.FatalErrorWindow.Show(_context, e.getMessage());
        } catch (WiFiManagerException e) {
            MainActivity.InfoWindow.Show(_context, e.getMessage(), null);
        }
    }

    private static Inet4Address _int2Inet4Address(int address) throws FatalErrorException {
        byte[] quads = new byte[4];
        for (int k = 0; k < 4; k++) {
            quads[k] = (byte) ((address >> k * 8) & 0xFF);
        }

        Inet4Address ret;
        try {
            ret = (Inet4Address) Inet4Address.getByAddress(quads);
        } catch (UnknownHostException e) {
            throw new FatalErrorException(_impossibleError);
        }

        return ret;
    }

    @Override
    public Inet4Address GetAddress() throws WiFiManagerException {
        if (_wifiManager.getWifiState() != WifiManager.WIFI_STATE_ENABLED) {
            throw new WiFiManagerException(_wifiDisabled);
        }

        DhcpInfo dhcp = _wifiManager.getDhcpInfo();
        try {
            return _int2Inet4Address(dhcp.ipAddress);
        } catch (FatalErrorException e) {
            throw new WiFiManagerException(e.getMessage());
        }
    }

    @Override
    public Inet4Address GetBroadcast() throws WiFiManagerException {
        if (_wifiManager.getWifiState() != WifiManager.WIFI_STATE_ENABLED) {
            throw new WiFiManagerException(_wifiDisabled);
        }

        DhcpInfo dhcp = _wifiManager.getDhcpInfo();
        try {
            return _int2Inet4Address((dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask);
        } catch (FatalErrorException e) {
            throw new WiFiManagerException(e.getMessage());
        }
    }

    @Override
    public Inet4Address GetNet() throws WiFiManagerException {
        if (_wifiManager.getWifiState() != WifiManager.WIFI_STATE_ENABLED) {
            throw new WiFiManagerException(_wifiDisabled);
        }

        DhcpInfo dhcp = _wifiManager.getDhcpInfo();
        try {
            return _int2Inet4Address(dhcp.ipAddress & dhcp.netmask);
        } catch (FatalErrorException e) {
            throw new WiFiManagerException(e.getMessage());
        }
    }
}
