using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading.Tasks;

namespace VirtualCardBoardClient
{
    public class Sender
    {
        protected Socket OutputSocket;

        public Sender()
        {
            OutputSocket = new Socket(IPAddress.Any.AddressFamily, SocketType.Dgram, ProtocolType.Udp);
            OutputSocket.Bind(new IPEndPoint(IPAddress.Any, 0));
        }

        public Sender Write(byte[] bytes, IPEndPoint remoteAddress)
        {
            OutputSocket.SendTo(bytes, remoteAddress);
            return this;
        }

        ~Sender()
        {
            OutputSocket.Close();
        }
    }
}
