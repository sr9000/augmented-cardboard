package com.degree.bachelor.jane_doe.virtualcardboard.network;

/**
 * Created by Jane-Doe on 5/26/2016.
 */
public interface ISettingsMessageData {
    void SetFocusDistance(int focusDistance);
    void SetFocusVerticalCoordinate(int focusVerticalCoordinate);
    void SetSimpleViewHeight(int simpleViewHeight);
    void SetSimpleViewWidth(int simpleViewWidth);

    int GetFocusDistance();
    int GetFocusVerticalCoordinate();
    int GetSimpleViewHeight();
    int GetSimpleViewWidth();
}
