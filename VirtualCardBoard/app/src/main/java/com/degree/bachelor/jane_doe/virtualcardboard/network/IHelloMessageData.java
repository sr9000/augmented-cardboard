package com.degree.bachelor.jane_doe.virtualcardboard.network;

import java.net.Inet4Address;

/**
 * Created by Jane-Doe on 5/15/2016.
 */
public interface IHelloMessageData {
    String GetName();
    int GetPort();
    Inet4Address GetAddress();

    void SetName(String name);
    void SetPort(int port);
    void SetAddress(Inet4Address address);
}
