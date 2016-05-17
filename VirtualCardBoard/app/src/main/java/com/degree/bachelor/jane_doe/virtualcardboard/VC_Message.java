package com.degree.bachelor.jane_doe.virtualcardboard;

import java.net.Inet4Address;

/**
 * Created by Jane-Doe on 5/15/2016.
 */
public class VC_Message {
    public enum Type {
        Hello
    }

    private Type _type;
    private MessageDataContainer _data = new MessageDataContainer();

    public Type GetType() { return _type; }
    public MessageDataContainer GetData() { return _data; }

    public static VC_Message GetHelloMessage(Inet4Address address, int port, String name) {
        VC_Message ret = new VC_Message();
        ret._type = Type.Hello;
        IHelloMessageData iret = ret._data;

        iret.SetAddress(address);
        iret.SetPort(port);
        iret.SetName(name);

        return ret;
    }

}
