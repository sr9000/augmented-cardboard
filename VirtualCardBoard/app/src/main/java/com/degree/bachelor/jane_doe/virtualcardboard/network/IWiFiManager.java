package com.degree.bachelor.jane_doe.virtualcardboard.network;

import com.degree.bachelor.jane_doe.virtualcardboard.information.WiFiManagerException;

import java.net.Inet4Address;

/**
 * Created by Jane-Doe on 5/23/2016.
 */
public interface IWiFiManager {
    Inet4Address GetAddress() throws WiFiManagerException;
    Inet4Address GetBroadcast() throws WiFiManagerException;
    Inet4Address GetNet() throws WiFiManagerException;
}
