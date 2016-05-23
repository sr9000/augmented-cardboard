package com.degree.bachelor.jane_doe.virtualcardboard.network;

import android.content.Context;

import com.degree.bachelor.jane_doe.virtualcardboard.information.ErrorWindow;
import com.degree.bachelor.jane_doe.virtualcardboard.information.InfoWindow;
import com.degree.bachelor.jane_doe.virtualcardboard.information.ManualException;

import java.net.Inet4Address;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Created by Jane-Doe on 5/21/2016.
 */
public class PcInterface {
    private static final String _impossibleError = "Fatal error with code name \"Inet4Address-WTF\". Please report it to developer!";

    private Context _context;
    private RequestListenerThread _listener;

    private PcInterface(){}

    public PcInterface(Context context)
    {
        _context = context;
        try {
            _listener = new RequestListenerThread(_context);
        } catch (ManualException e) {
            ErrorWindow.Show(_context, e.getMessage());
        }
    }

    public PcInterface SendBroadcastWelcomeSignal() {
        Thread worker = new Thread(new Runnable() {
            @Override
            public void run() {
                _SendBroadcastWelcomeSignal();
            }
        });
        worker.start();
        return this;
    }

    private void _SendBroadcastWelcomeSignal()
    {
        VC_Message msg;
        msg = VC_Message.GetHelloMessage(
                _listener.GetAddress()
                , _listener.GetPort()
                , "Hello World!!!");

        try {
            BroadcastSender.SendMessage(_context, msg);
        } catch (ManualException e) {
            ErrorWindow.Show(_context, e.getMessage());
            return;
        }
    }
}
