package com.degree.bachelor.jane_doe.virtualcardboard;

import java.net.Inet4Address;

/**
 * Created by Jane-Doe on 5/15/2016.
 */
public class MessageDataContainer
        implements IHelloMessageData
{
    //IHelloMessageData
    private Inet4Address _address;
    private String _name;
    private int _port;

    @Override
    public String GetName() {
        return _name;
    }

    @Override
    public int GetPort() {
        return _port;
    }

    @Override
    public Inet4Address GetAddress() {
        return _address;
    }

    @Override
    public void SetName(String name) { _name = name; }

    @Override
    public void SetPort(int port) { _port = port; }

    @Override
    public void SetAddress(Inet4Address address) { _address = address; }
}

