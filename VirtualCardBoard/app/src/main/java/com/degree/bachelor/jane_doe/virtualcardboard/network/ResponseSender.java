package com.degree.bachelor.jane_doe.virtualcardboard.network;

import com.degree.bachelor.jane_doe.virtualcardboard.information.FatalErrorException;
import com.degree.bachelor.jane_doe.virtualcardboard.information.WiFiManagerException;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.SocketException;

/**
 * Created by Jane-Doe on 5/23/2016.
 */
public class ResponseSender {
    private static final String _badSocket = "Fatal error with code name \"Windy-Weather\". Please report it to developer!";
    private DatagramSocket _socket;

    public ResponseSender() throws FatalErrorException {
        try {
            _socket = new DatagramSocket();
        } catch (SocketException e) {
            throw new FatalErrorException(_badSocket);
        }
    }

    public void SendMessage(VCMessage msg, Inet4Address address, int port) {
        byte[] msgBytes = MessageComposer.ComposeMessage(msg);
        int totalCount =
                PcInterface.Secret.length //secret length
                        + 4     //actual data length
                        + msgBytes.length;  //message actual data
        byte[] data = new byte[totalCount];

        //copy secret
        System.arraycopy(PcInterface.Secret, 0, data, 0, PcInterface.Secret.length);

        //set length
        data[PcInterface.Secret.length]     = (byte)(msgBytes.length % 256); //div 256^0
        data[PcInterface.Secret.length + 1] = (byte)((msgBytes.length / 256) % 256); //div 256^1
        data[PcInterface.Secret.length + 2] = (byte)((msgBytes.length / 65536) % 256); //div 256^2
        data[PcInterface.Secret.length + 3] = (byte)((msgBytes.length / 16777216) % 256); //div 256^3

        //set actual data
        System.arraycopy(msgBytes, 0, data, PcInterface.Secret.length + 4, msgBytes.length);

        //create packet
        DatagramPacket sendPacket = new DatagramPacket(data, data.length, address, port);

        //send packets
        try { _socket.send(sendPacket); } catch (IOException ignored) { }
    }

}
