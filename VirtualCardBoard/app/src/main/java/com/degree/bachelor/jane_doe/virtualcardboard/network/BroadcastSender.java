package com.degree.bachelor.jane_doe.virtualcardboard.network;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;

import com.degree.bachelor.jane_doe.virtualcardboard.information.ManualException;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Created by Jane-Doe on 5/15/2016.
 */
public class BroadcastSender {
    private static final String _deepInet4Address = "Fatal error with code name \"Inet4Address-D\". Please report it to developer!";
    private static final String _spoiledSocket = "Fatal error with code name \"Socket-SP\". Please report it to developer!";
    private static final String _badSocket = "Fatal error with code name \"Socket-BD\". Please report it to developer!";

    private static final byte[] _secret = new byte[]
            { (byte)207, (byte)219, (byte)43,  (byte)202
            , (byte)53,  (byte)226, (byte)172, (byte)160
            , (byte)100, (byte)227, (byte)145, (byte)120
            , (byte)187, (byte)99,  (byte)170, (byte)225};
    private static final int[] _ports = new int[]
            { 48654, 48670, 48683, 48696, 48699
            , 48702, 48739, 48755, 48773, 48780
            , 48787, 48798, 48811, 48825, 48830
            , 48834, 48836, 48841, 48842, 48856
            , 48861, 48865, 48869, 48872, 48911
            , 48947, 48949, 48975, 48978, 49030
            , 49049, 49058, 49074, 49081, 49089
            , 49100, 49107, 49121, 49123, 49129
            , 49134, 49135 };

    public static void SendMessage(Context context, VC_Message msg) throws ManualException {
        byte[] msgBytes = MessageComposer.ComposeMessage(msg);
        int totalCount =
                _secret.length //secret length
                + 4     //actual data length
                + msgBytes.length;  //message actual data
        byte[] data = new byte[totalCount];

        //copy secret
        System.arraycopy(_secret, 0, data, 0, _secret.length);

        //set length
        data[_secret.length]     = (byte)(msgBytes.length % 256); //div 256^0
        data[_secret.length + 1] = (byte)((msgBytes.length / 256) % 256); //div 256^1
        data[_secret.length + 2] = (byte)((msgBytes.length / 65536) % 256); //div 256^2
        data[_secret.length + 3] = (byte)((msgBytes.length / 16777216) % 256); //div 256^3

        //set actual data
        System.arraycopy(msgBytes, 0, data, _secret.length + 4, msgBytes.length);

        //create wifiManager for getting broadcast address
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        //create packet
        DatagramPacket sendPacket = new DatagramPacket(data, data.length, getBroadcastAddress(wifiManager), 0);

        //create socket
        DatagramSocket socket;
        try {
            socket = new DatagramSocket();
            socket.setBroadcast(true);
        } catch (SocketException e) {
            throw new ManualException(_badSocket);
        }

        //send packets
        try {
            for (int portNumber : _ports) {
                sendPacket.setPort(portNumber);
                socket.send(sendPacket);
            }
        } catch (IOException e) {
            throw new ManualException(_spoiledSocket);
        }
    }

    private static InetAddress getBroadcastAddress(WifiManager wifiManager) throws ManualException {
        DhcpInfo dhcp = wifiManager.getDhcpInfo();
        int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;

        byte[] quads = new byte[4];
        for (int k = 0; k < 4; k++) {
            quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
        }

        InetAddress ret;
        try {
            ret = InetAddress.getByAddress(quads);
        } catch (UnknownHostException e) {
            throw new ManualException(_deepInet4Address);
        }

        return ret;
    }
}
