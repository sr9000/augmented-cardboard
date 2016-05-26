using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.NetworkInformation;
using System.Net.Sockets;
using System.Text;
using System.Threading;
using System.Threading.Tasks;

namespace VirtualCardBoardClient
{
    public class Listener
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

        /*public IPAddress GetAddress()
        {
            var ipEndPoint = InputSocket.LocalEndPoint as IPEndPoint;
            if (ipEndPoint != null)
            {
                return ipEndPoint.Address;
            }
            return null;
        }*/

        public int GetPort()
        {
            var ipEndPoint = InputSocket.LocalEndPoint as IPEndPoint;
            if (ipEndPoint != null)
            {
                return ipEndPoint.Port;
            }
            return 0;
        }

        public bool IsStarted()
        {
            return (InputSocket != null);
        }

        public Listener Start()
        {
            InputSocket = new Socket(IPAddress.Any.AddressFamily, SocketType.Dgram, ProtocolType.Udp);

            foreach (var port in Ports)
            {
                try
                {
                    InputSocket.Bind(new IPEndPoint(IPAddress.Any, port));
                    InputSocket.EnableBroadcast = true;
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

        public ClientBytes Read(int timeWaitMilliseconds = 0)
        {
            lock (ReaderSynchronizator)
            {
                try
                {
                    InputSocket.ReceiveTimeout = timeWaitMilliseconds;
                    byte[] ret = new byte[10240]; //10 KiB
                    EndPoint remouteEndPoint = new IPEndPoint(IPAddress.Any, 0);
                    int lengthRecieved = InputSocket.ReceiveFrom(ret, ref remouteEndPoint);

                    IPEndPoint localEndPoint = null;
                    foreach (var networkInterface in NetworkInterface.GetAllNetworkInterfaces())
                    {
                        foreach (var ipAddressInformation in networkInterface.GetIPProperties().UnicastAddresses)
                        {
                            if (ipAddressInformation.Address.AddressFamily == System.Net.Sockets.AddressFamily.InterNetwork)
                            {
                                var mask = ipAddressInformation.IPv4Mask.GetAddressBytes();
                                var subnet1 = ((IPEndPoint) remouteEndPoint).Address.GetAddressBytes()
                                    .Zip(mask, (a, b) => a & b);
                                var subnet2 = ipAddressInformation.Address.GetAddressBytes()
                                    .Zip(mask, (a, b) => a & b);
                                if (subnet1.Zip(subnet2, (a, b) => a == b).All(x => x))
                                {
                                    localEndPoint = new IPEndPoint(ipAddressInformation.Address, ((IPEndPoint)InputSocket.LocalEndPoint).Port);
                                }
                            }
                        }
                    }
                    
                    return new ClientBytes()
                    {
                        PacketBytes = ret.Take(lengthRecieved).ToArray()
                        , LocalEndPoint = localEndPoint
                    };
                }
                catch
                {
                    return new ClientBytes()
                    {
                        PacketBytes = new List<byte>().ToArray()
                    };
                }
            }
        }
    }
}
