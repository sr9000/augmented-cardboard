package com.degree.bachelor.jane_doe.virtualcardboard.network;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;

import com.degree.bachelor.jane_doe.virtualcardboard.LoyalError;
import com.degree.bachelor.jane_doe.virtualcardboard.PausableThread;
import com.degree.bachelor.jane_doe.virtualcardboard.information.InfoWindow;
import com.degree.bachelor.jane_doe.virtualcardboard.information.ManualException;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/**
 * Created by Jane-Doe on 5/23/2016.
 */
public class RequestListenerThread extends PausableThread {
    private static final int _max_packet_length = 1048576; //1MiB
    private static final int _receive_timeout = 1000; //1sec
    private static final int _max_IOExceptions = 5;

    private static final String _noSocket = "Fatal error with code name \"No Server\". Please report it to developer!";
    private static final String _noAddress = "Fatal error with code name \"Inet4Address-Server\". Please report it to developer!";
    private static final String _ioError = "Many errors happened, when waiting data from PC.";

    private byte[] _packetBytes = new byte[_max_packet_length];
    private DatagramPacket _packet;
    private DatagramSocket _socket;
    private LoyalError _errorsInformer;
    private Context _context;
    private Runnable _catch;

    public RequestListenerThread(Context context) throws ManualException {
        _context = context;
        _packet = new DatagramPacket(_packetBytes, _packetBytes.length);
        _errorsInformer = new LoyalError(_max_IOExceptions, _ioError);

        _catch = new Runnable() {
            @Override
            public void run() {
                _errorsInformer.Catch();
            }
        };

        try {
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            _socket = new DatagramSocket(0, getLocalAddress(wifiManager));
            _socket.setSoTimeout(_receive_timeout);
        } catch (SocketException e) {
            throw new ManualException(_noSocket);
        }

    }

    public Inet4Address GetAddress() {
        return (Inet4Address)_socket.getLocalAddress();
    }

    public int GetPort() {
        return _socket.getLocalPort();
    }

    private static InetAddress getLocalAddress(WifiManager wifiManager) throws ManualException {
        DhcpInfo dhcp = wifiManager.getDhcpInfo();
        int broadcast = dhcp.ipAddress;

        byte[] quads = new byte[4];
        for (int k = 0; k < 4; k++) {
            quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
        }

        InetAddress ret;
        try {
            ret = InetAddress.getByAddress(quads);
        } catch (UnknownHostException e) {
            throw new ManualException(_noAddress);
        }

        return ret;
    }

    @Override
    protected void ProcessBody() {
        try {
            _socket.receive(_packet);
        } catch (SocketTimeoutException e) {
            //t skip cause it is expected behavior
        }
        catch (IOException e) {
            try {
                //count exceptions
                _errorsInformer.Ouch();
            } catch (ManualException e1) {
                //exceptions limit exceeded
                InfoWindow.Show(_context, e1.getMessage(), _catch);
            }
        }
    }
}
