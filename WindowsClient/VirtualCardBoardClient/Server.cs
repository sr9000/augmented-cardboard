using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading;
using System.Threading.Tasks;

namespace VirtualCardBoardClient
{
    class Server
    {
        //42 count
        private static int[] Ports = { 48654, 48670, 48683, 48696, 48699
                                         , 48702, 48739, 48755, 48773, 48780
                                         , 48787, 48798, 48811, 48825, 48830
                                         , 48834, 48836, 48841, 48842, 48856
                                         , 48861, 48865, 48869, 48872, 48911
                                         , 48947, 48949, 48975, 48978, 49030
                                         , 49049, 49058, 49074, 49081, 49089
                                         , 49100, 49107, 49121, 49123, 49129
                                         , 49134, 49135 };
        private static int SUntilCount = 10;
        private static int SUntilTime = 1000;//millisecs

        private Socket _s;
        private bool _isStarted;
        private bool _isShouldBeStarted;
        private ServerThread _serverThread;

        public delegate void ServerCallback(byte[] buffer, IPEndPoint endPoint);
        public delegate void ServerResume(ref Socket serverThreadSocket);

        private ServerCallback _savedCallback;

        public Server()
        {
            _s = null;
            _isStarted = false;
            _isShouldBeStarted = false;
            _savedCallback = null;
            _serverThread = null;
        }

        public bool IsStarted()
        {
            return _isStarted;
        }

        public void StartServer(ServerCallback callback)
        {
            if (IsStarted()) return;
            _savedCallback = callback;
            _Start();
        }

        private void _Start()
        {
            _s = new Socket(IPAddress.Any.AddressFamily, SocketType.Dgram, ProtocolType.Udp);
            foreach (var port in Ports)
            {
                try
                {
                    _s.Bind(new IPEndPoint(IPAddress.Any, port));
                    _isStarted = true;
                    _isShouldBeStarted = true;
                    return;
                }
                catch
                {
                    //catch all exceptions
                }
            }

            _s.Close();
            _s = null;
        }

        public void StopServer()
        {
            _isShouldBeStarted = false;
            if (!IsStarted()) return;

            _Stop();
        }

        private void _Stop()
        {
            _serverThread.AllStop();
            _serverThread = null;

            if (_s != null)
            {
                _s.Close();
                _s = null;
            }
            _isStarted = false;
            _savedCallback = null;
        }

        private void Resume()
        {
            int untilCount = 0;
            int untilTime = 0;
            do
            {
                if (untilCount > 0)
                    Thread.Sleep(untilTime);
                if (!_isShouldBeStarted) return;
                StopServer();
                StartServer(_savedCallback);
                untilCount++;
                untilTime += untilTime + SUntilTime;
            } while (!IsStarted() && (untilCount < SUntilCount));

            if (untilCount >= SUntilCount)
            {
                throw new Exception("Fatal server crash. Check ethernet interface.");
            }
        }
    }
}
