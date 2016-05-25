package com.degree.bachelor.jane_doe.virtualcardboard.network;

/**
 * Created by Jane-Doe on 5/15/2016.
 */
public class MessageComposer {
    public static byte[] ComposeMessage(VCMessage msg)
    {
        switch (msg.GetType())
        {
            case Hello:
                return ComposeHelloMessage(msg);
        }
        return new byte[]{(byte)VCMessageSignatures._signature_error};
    }

    private static byte[] ComposeHelloMessage(VCMessage msg)
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
        ret[0] = (byte)(VCMessageSignatures._signature_hello);

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

    private static byte[] ComposeSettingsMessage(VCMessage msg)
    {
        ISettingsMessageData idata = msg.GetData();
        int totalCount =
                1    //_signature_settings
                + 1  //message mission(flags)
                + 4  //focusDist
                + 4  //focusVert
                + 4  //simpleWidth
                + 4  //simpleHeight
                + 4  //inet4address //[nulls]
                + 2; //port         //[nulls]
        //todo:
        //create array
        byte[] ret = new byte[totalCount];

        //assign signature
        ret[0] = (byte)(VCMessageSignatures._signature_hello);

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
