package com.degree.bachelor.jane_doe.virtualcardboard.information;

/**
 * Created by Jane-Doe on 5/21/2016.
 */
public class InfoException extends Exception {
    private static final String _defaultInfo = "If you see it you can smile and know that I like YOU ^__^";

    private String _info;

    private InfoException(){}

    public InfoException(String info) {
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
