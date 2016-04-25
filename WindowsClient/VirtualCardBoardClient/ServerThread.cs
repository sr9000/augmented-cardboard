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
    class ServerThread
    {
        private VirtualCardBoardClient.Server.ServerCallback _callback;
        private Socket _s;

        private Thread _work;

        private byte[] _buffer;
        private EndPoint _endPoint;

        public ServerThread(Socket s, VirtualCardBoardClient.Server.ServerCallback callback)
        {
            _s = s;
            _callback = callback;
            _buffer = new byte[10240]; //10KiB
            _work = new Thread(new ThreadStart( delegate() {
                try
                {
                    Proceed();
                }
                catch (Exception)
                {
                    //call server resume callback
                    return;
                }
                }));
        }

        public void AllStop()
        {

        }

        private void Proceed()
        {
            _s.ReceiveFrom(_buffer, ref _endPoint);
        }

        private void Resume()
        {

        }
    }
}
