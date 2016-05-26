package com.degree.bachelor.jane_doe.virtualcardboard.network;

import java.net.Inet4Address;

/**
 * Created by Jane-Doe on 5/26/2016.
 */
public interface ISettingsMessageData {
    void SetFocusDistance(int focusDistance);
    void SetFocusVerticalCoordinate(int focusVerticalCoordinate);
    void SetSimpleViewHeight(int simpleViewHeight);
    void SetSimpleViewWidth(int simpleViewWidth);
    void SetMessageMission(byte flags);
    void SetRemotePort(int portNumber);
    void SetRemoteAddress(Inet4Address address);

    int GetFocusDistance();
    int GetFocusVerticalCoordinate();
    int GetSimpleViewHeight();
    int GetSimpleViewWidth();
    int GetRemotePort();
    Inet4Address GetRemoteAddress();
    byte GetFlags();

    void ParseSettingsMessageData(byte[] bytes);
}
