﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading;
using System.Threading.Tasks;

namespace VirtualCardBoardClient
{
    class Listener
    {
        //42 count
        protected static int[] Ports = { 48654, 48670, 48683, 48696, 48699
                                         , 48702, 48739, 48755, 48773, 48780
                                         , 48787, 48798, 48811, 48825, 48830
                                         , 48834, 48836, 48841, 48842, 48856
                                         , 48861, 48865, 48869, 48872, 48911
                                         , 48947, 48949, 48975, 48978, 49030
                                         , 49049, 49058, 49074, 49081, 49089
                                         , 49100, 49107, 49121, 49123, 49129
                                         , 49134, 49135 };
        protected Socket InputSocket;
        protected Object ReaderSynchronizator = new Object();

        public bool IsStarted()
        {
            return (InputSocket == null);
        }

        public Listener Start()
        {
            InputSocket = new Socket(IPAddress.Any.AddressFamily, SocketType.Dgram, ProtocolType.Udp);

            foreach (var port in Ports)
            {
                try
                {
                    InputSocket.Bind(new IPEndPoint(IPAddress.Any, port));
                    return this;
                }
                catch
                {
                    //catch all bad usecases
                }
            }

            //if unsccessful to open sokect
            return CloseSocket();
        }

        public Listener Stop()
        {
            if (!IsStarted())
            {
                return this;
            }

            return CloseSocket();
        }

        protected Listener CloseSocket()
        {
            InputSocket.Close();
            InputSocket = null;
            return this;
        }

        public byte[] Read()
        {
            lock (ReaderSynchronizator)
            {
                try
                {
                    byte[] ret = new byte[10240]; //10 KiB
                    int lengthRecieved = InputSocket.Receive(ret);
                    return ret.Take(lengthRecieved).ToArray();
                }
                catch
                {
                    return new List<byte>().ToArray();//return an empty array
                }
            }
        }
    }
}