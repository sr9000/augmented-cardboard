package com.degree.bachelor.jane_doe.virtualcardboard.network;

import com.degree.bachelor.jane_doe.virtualcardboard.BinocularView;

import java.net.Inet4Address;

/**
 * Created by Jane-Doe on 5/15/2016.
 */
public class VCMessage {
    public enum Type {
        Hello,
        Ping,
        Mode,
        Settings
    }

    private Type _type;
    private MessageDataContainer _data = new MessageDataContainer();

    //use VCMessage.GetHelloMessage(...) instead constructor
    private VCMessage(){}

    public Type GetType() { return _type; }
    public MessageDataContainer GetData() { return _data; }

    public static VCMessage GetHelloMessage(Inet4Address address, int port, String name) {
        VCMessage ret = new VCMessage();
        ret._type = Type.Hello;
        IHelloMessageData iret = ret._data;

        iret.SetAddress(address);
        iret.SetPort(port);
        iret.SetName(name);

        return ret;
    }

    public static VCMessage GetSettingsMessage(BinocularView.BinocularInfo binocularInfo) {
        VCMessage ret = new VCMessage();
        ret._type = Type.Settings;
        ISettingsMessageData iret = ret._data;

        iret.SetFocusDistance(binocularInfo.focusDistance);
        iret.SetFocusVerticalCoordinate(binocularInfo.focusVerticalCoordinate);
        iret.SetSimpleViewHeight(binocularInfo.simpleViewHeight);
        iret.SetSimpleViewWidth(binocularInfo.simpleViewWidth);
        iret.SetMessageMission(MessageDataContainer._mission_inform);

        return ret;
    }

    public static VCMessage ParsePingMessage(byte[] bytes) {
        VCMessage ret = new VCMessage();
        ret._type = Type.Ping;
        IPingMessageData iret = ret._data;

        iret.ParsePingMessageData(bytes);
        return ret;
    }

    public static VCMessage ParseModeMessage(byte[] bytes) {
        VCMessage ret = new VCMessage();
        ret._type = Type.Mode;
        IModeMessageData iret = ret._data;

        iret.ParseModeMessageData(bytes);
        return ret;
    }

}
