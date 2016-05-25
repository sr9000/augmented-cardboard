package com.degree.bachelor.jane_doe.virtualcardboard.network;

/**
 * Created by Jane-Doe on 5/25/2016.
 */
public interface IModeMessageData {
    void ParseModeMessageData(byte[] bytes);
    MessageDataContainer.ModeType GetMode();
}
