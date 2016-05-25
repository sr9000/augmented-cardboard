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
            case Settings:
                return ComposeSettingsMessage(msg);
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

    private static void _assign_32bit_int_to_array(byte[] array, int offset, int value) {
        array[offset]     = (byte)(value % 256); //div 256^0
        array[offset + 1] = (byte)((value / 256) % 256); //div 256^1
        array[offset + 2] = (byte)((value / 65536) % 256); //div 256^2
        array[offset + 3] = (byte)((value / 16777216) % 256); //div 256^3
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

        //create array
        byte[] ret = new byte[totalCount];

        //assign signature
        ret[0] = (byte)(VCMessageSignatures._signature_settings);

        //assign flags
        ret[1] = idata.GetFlags();

        if ((idata.GetFlags() & (MessageDataContainer._mission_assign | MessageDataContainer._mission_inform)) != 0) {
            _assign_32bit_int_to_array(ret, 2, idata.GetFocusDistance());
            _assign_32bit_int_to_array(ret, 6, idata.GetFocusVerticalCoordinate());
            _assign_32bit_int_to_array(ret, 10, idata.GetSimpleViewWidth());
            _assign_32bit_int_to_array(ret, 14, idata.GetSimpleViewHeight());
        }

        if ((idata.GetFlags() & MessageDataContainer._mission_request) != 0) {
            System.arraycopy(idata.GetRemoteAddress().getAddress(), 0, ret, 18, 4);
            ret[22] = (byte)(idata.GetRemotePort() % 256);
            ret[23] = (byte)(idata.GetRemotePort() / 256);
        }

        //return packet
        return ret;
    }
}
