package com.degree.bachelor.jane_doe.virtualcardboard.network;

import com.degree.bachelor.jane_doe.virtualcardboard.network.IHelloMessageData;
import com.degree.bachelor.jane_doe.virtualcardboard.network.VC_Message;

/**
 * Created by Jane-Doe on 5/15/2016.
 */
public class MessageComposer {
    private static final int _signature_error = 255;
    private static final int _signature_hello = 0;

    public static byte[] ComposeMessage(VC_Message msg)
    {
        switch (msg.GetType())
        {
            case Hello:
                return ComposeHelloMessage(msg);
        }
        return new byte[]{(byte)_signature_error};
    }

    private static byte[] ComposeHelloMessage(VC_Message msg)
    {
        IHelloMessageData idata = msg.GetData();
        int totalCount =
                1   //_signature_hello
                + 4 //ipv4 address
                + 2 //port number
                + idata.GetName().getBytes().length + 1; //device name
        //create array
        byte[] ret = new byte[totalCount];

        //assign signature
        ret[0] = _signature_hello;

        //copy ipv4 address
        System.arraycopy(idata.GetAddress().getAddress(), 0, ret, 1, 4);

        //copy port number
        ret[5] = (byte)(idata.GetPort() % 256);
        ret[6] = (byte)(idata.GetPort() / 256);

        //copy device name
        System.arraycopy(idata.GetName().getBytes(), 0, ret, 7, totalCount - 7 - 1);

        //assign null terminate symbol
        ret[totalCount - 1] = 0;

        //return packet
        return ret;
    }
}
