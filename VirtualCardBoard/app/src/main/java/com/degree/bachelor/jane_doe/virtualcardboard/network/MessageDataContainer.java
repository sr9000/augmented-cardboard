package com.degree.bachelor.jane_doe.virtualcardboard.network;

import java.net.Inet4Address;

/**
 * Created by Jane-Doe on 5/15/2016.
 */
public class MessageDataContainer
        implements IHelloMessageData
        , IPingMessageData
        , IModeMessageData
{
    //IHelloMessageData
    private Inet4Address _address;
    private String _name;
    private int _port;

    //IModeMessageData
    public enum ModeType {
        Pic, NoPic, Settings
    }
    public static final byte _mode_message_pic = 0;
    public static final byte _mode_message_no_pic = 1;
    public static final byte _mode_message_settings = 2;

    private ModeType _mode;

    //IHelloMessageData
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

    //IPingMessageData
    @Override
    public void ParsePingMessageData(byte[] bytes) {}

    //IModeMessageData
    @Override
    public void ParseModeMessageData(byte[] bytes) {
        switch (bytes[0]) {
            case _mode_message_pic:
                _mode = ModeType.Pic;
                break;
            case _mode_message_no_pic:
                _mode = ModeType.NoPic;
                break;
            case _mode_message_settings:
                _mode = ModeType.Settings;
                break;
        }
    }

    @Override
    public ModeType GetMode() {
        return _mode;
    }

}

