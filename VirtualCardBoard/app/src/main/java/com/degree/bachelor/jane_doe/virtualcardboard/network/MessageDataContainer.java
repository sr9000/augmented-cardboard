package com.degree.bachelor.jane_doe.virtualcardboard.network;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.Arrays;

/**
 * Created by Jane-Doe on 5/15/2016.
 */
public class MessageDataContainer
        implements IHelloMessageData
        , IPingMessageData
        , IModeMessageData
        , ISettingsMessageData
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

    //ISettingsMessageData
    private int _focusDistance, _focusVerticalCoordinate;
    private int _simpleViewHeight, _simpleViewWidth;
    private byte _messageMission;
    private int _remotePort;
    private Inet4Address _remoteAddress;

    public static final byte _mission_inform = 1;
    public static final byte _mission_request = 2;
    public static final byte _mission_assign = 4;

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

    //ISettingsMessageData
    @Override
    public void SetFocusDistance(int focusDistance) { _focusDistance = focusDistance; }

    @Override
    public void SetFocusVerticalCoordinate(int focusVerticalCoordinate) { _focusVerticalCoordinate = focusVerticalCoordinate; }

    @Override
    public void SetSimpleViewHeight(int simpleViewHeight) { _simpleViewHeight = simpleViewHeight; }

    @Override
    public void SetSimpleViewWidth(int simpleViewWidth) { _simpleViewWidth = simpleViewWidth; }

    @Override
    public void SetMessageMission(byte flags) { _messageMission = (byte)(flags & 0x7); }

    @Override
    public void SetRemotePort(int portNumber) { _remotePort = portNumber; }

    @Override
    public void SetRemoteAddress(Inet4Address address) { _remoteAddress = address; }

    @Override
    public int GetFocusDistance() { return _focusDistance; }

    @Override
    public int GetFocusVerticalCoordinate() { return _focusVerticalCoordinate; }

    @Override
    public int GetSimpleViewHeight() { return _simpleViewHeight; }

    @Override
    public int GetSimpleViewWidth() { return _simpleViewWidth; }

    @Override
    public int GetRemotePort() { return _remotePort; }

    @Override
    public Inet4Address GetRemoteAddress() { return _remoteAddress; }

    @Override
    public byte GetFlags() { return _messageMission; }

    private int _array_to_32bit_int(byte[] array, int offset) {
        int ret = 0;

        int multiplier = 1;
        for (int i = 0; i < 4; ++i)
        {
            ret += (((int)array[i + offset]) & 0xFF) * multiplier;
            multiplier *= 256;
        }

        return ret;
    }

    @Override
    public void ParseSettingsMessageData(byte[] bytes) {
        _messageMission = bytes[1];

        if ((_messageMission & (MessageDataContainer._mission_assign | MessageDataContainer._mission_inform)) != 0) {
            _focusDistance = _array_to_32bit_int(bytes, 2);
            _focusVerticalCoordinate = _array_to_32bit_int(bytes, 6);
            _simpleViewWidth = _array_to_32bit_int(bytes, 10);
            _simpleViewHeight = _array_to_32bit_int(bytes, 14);
        }

        if ((_messageMission & MessageDataContainer._mission_request) != 0) {
            byte[] addr = new byte[4];
            System.arraycopy(bytes, 18, addr, 0, 4);
            try {
                _remoteAddress = (Inet4Address) Inet4Address.getByAddress(addr);
            } catch (UnknownHostException ignored) {}

            _remotePort = (bytes[22] & 0xFF) + 256 * (bytes[23] & 0xFF);
        }
    }

}

