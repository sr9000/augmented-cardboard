package com.degree.bachelor.jane_doe.virtualcardboard.network;

/**
 * Created by Jane-Doe on 5/25/2016.
 */
public class MessageParser {
    public static VCMessage ParseMessage(byte[] bytes) {
        switch (((int)bytes[0])) {
            case VCMessageSignatures._signature_ping:
                return VCMessage.ParsePingMessage(bytes);
            case VCMessageSignatures._signature_mode:
                return VCMessage.ParseModeMessage(bytes);
        }
        return null;
    }
}
