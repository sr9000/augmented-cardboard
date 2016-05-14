using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.NetworkInformation;
using System.Text;
using System.Threading.Tasks;

namespace VirtualCardBoardClient
{
    public class MessageDataContainer
        : IHelloMessageData
    {
        //IHelloMessageData
        protected IPAddress Address;
        protected int Port;

        public static MessageDataContainer ParseHelloMessage(byte[] packet)
        {
            if (packet.Length < 7)
            {
                return null;
            }

            return new MessageDataContainer
            {
                Address = new IPAddress(packet.Skip(1).Take(4).ToArray()),
                Port = packet[5] + 256*packet[6]
            };
        }

        public IPAddress GetAdress()
        {
            return Address;
        }

        public int GetPort()
        {
            return Port;
        }
    }

    public interface IHelloMessageData
    {
        IPAddress GetAdress();
        int GetPort();
    }
}
