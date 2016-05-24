package com.degree.bachelor.jane_doe.virtualcardboard.information;

/**
 * Created by Jane-Doe on 5/21/2016.
 */
public class WiFiManagerException extends Exception {
    private static final String _defaultInfo = "Error with code name \"Suit-Sugar\". Please report it to developer!";

    private String _info;

    private WiFiManagerException(){}

    public WiFiManagerException(String info) {
        if (info == null) {
            _info = _defaultInfo;
        } else {
            _info = info;
        }
    }

    @Override
    public String getMessage() {
        return _info;
    }
}
