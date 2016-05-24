package com.degree.bachelor.jane_doe.virtualcardboard.network;

import android.content.Context;

import com.degree.bachelor.jane_doe.virtualcardboard.LoyalError;
import com.degree.bachelor.jane_doe.virtualcardboard.MainActivity;
import com.degree.bachelor.jane_doe.virtualcardboard.PausableThread;
import com.degree.bachelor.jane_doe.virtualcardboard.information.FatalErrorException;
import com.degree.bachelor.jane_doe.virtualcardboard.information.InfoException;
import com.degree.bachelor.jane_doe.virtualcardboard.information.WiFiManagerException;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.SocketException;
import java.net.SocketTimeoutException;

/**
 * Created by Jane-Doe on 5/23/2016.
 */
public class RequestListenerThread extends PausableThread {
    private static final int _max_packet_length = 1048576; //1MiB
    private static final int _receive_timeout = 1000; //1sec

    private static final String _noSocket = "Fatal error with code name \"Orange-Cow\". Please report it to developer!";
    private static final String _wifiNetworkUnstable = "Error with code name \"Purple-Car\". Maybe Wi-Fi network is unstable. Please swap network or contact with your network administrator. Or you can report it to developer!";

    private static class _ReceiveErrorManager implements Runnable {
        private static final int _max_IOExceptions = 5;
        private static final String _ioError = "Many errors with code name \"Cold-Winter\" happened, when waiting data from PC.";

        private LoyalError _errorsInformer;

        public _ReceiveErrorManager() {
            _errorsInformer = new LoyalError(_max_IOExceptions, _ioError);
        }

        public void Ouch() throws InfoException {
            _errorsInformer.Ouch();
        }

        @Override
        public void run() {
            _errorsInformer.Catch();
        }
    }

    private byte[] _packetBytes = new byte[_max_packet_length];
    private DatagramPacket _packet;
    private DatagramSocket _socket;

    private Context _context;
    private _ReceiveErrorManager _receiveErrorManager = new _ReceiveErrorManager();
    private IWiFiManager _wifiManager;

    private volatile Inet4Address _wifiLocal;
    private volatile Inet4Address _wifiNet;
    private volatile int _wifiPort;
    private final Object syncSocket = new Object();

    public RequestListenerThread(Context context, IWiFiManager wifiManager) {
        _context = context;
        _wifiManager = wifiManager;
        _packet = new DatagramPacket(_packetBytes, _packetBytes.length);

        synchronized (syncSocket) {
            _wifiLocal = null;
            _wifiNet = null;
            _wifiPort = -1;
        }

        _socket = null;
    }

    private void _CheckAddress() throws WiFiManagerException, FatalErrorException {
        Inet4Address wifiLocal = _wifiManager.GetAddress();
        Inet4Address wifiNet = _wifiManager.GetNet();
        if (_wifiLocal == wifiLocal && _wifiNet == wifiNet) { return; }

        synchronized (syncSocket) {
            _wifiLocal = null;
            _wifiNet = null;
            _wifiPort = -1;
        }

        if (_socket != null) {
            _socket.close();
            _socket = null;
        }

        try {
            _socket = new DatagramSocket(0, wifiLocal);
        } catch (SocketException e) {
            throw new FatalErrorException(_noSocket);
        }

        synchronized (syncSocket) {
            _wifiLocal = wifiLocal;
            _wifiNet = wifiNet;
            _wifiPort = _socket.getLocalPort();
        }
    }

    public Inet4Address GetAddress() throws WiFiManagerException {
        synchronized (syncSocket) {
            if (_wifiLocal == null) {
                throw new WiFiManagerException(_wifiNetworkUnstable);
            }
        }
        return _wifiLocal;
    }

    public int GetPort() throws WiFiManagerException {
        synchronized (syncSocket) {
            if (_wifiPort < 0) {
                throw new WiFiManagerException(_wifiNetworkUnstable);
            }
        }
        return _wifiPort;
    }

    @Override
    protected void ProcessBody() {
        boolean doOuch = false;

        try {
            _CheckAddress();
        } catch (WiFiManagerException e) {
            Thread.yield();
            return;
        } catch (FatalErrorException e) {
            MainActivity.FatalErrorWindow.Show(_context, e.getMessage());
        }

        try {
            _socket.setSoTimeout(_receive_timeout);
            _socket.receive(_packet);
        } catch (SocketTimeoutException e) {
            //just skip cause it is expected behavior
        } catch (IOException e) {
            doOuch = true;
        }

        try {
            if (doOuch) { _receiveErrorManager.Ouch(); }
        } catch (InfoException e) {
            MainActivity.InfoWindow.Show(_context, e.getMessage(), _receiveErrorManager);
        }
    }
}
