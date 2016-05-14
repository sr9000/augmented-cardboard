using System;
using System.Collections.Generic;
using System.Linq;
using System.Security.Cryptography.X509Certificates;
using System.Text;
using System.Threading.Tasks;

namespace VirtualCardBoardClient
{
    public class MessageParser
    {
        protected delegate Message SpecifiedPacketParser (byte[] packet);

        protected static SpecifiedPacketParser[] ParserMethods = { Message.HelloParserMethod };

        public class Message
        {
            public enum MessageType
            {
                Hello, Empty
            }

            public MessageType Type { get; protected set; }

            public MessageDataContainer Data { get; protected set; }

            public bool HasData()
            {
                return (Data != null);
            }

            protected Message SetData(MessageDataContainer data)
            {
                Data = data;
                return this;
            }

            protected Message SetType(MessageType type)
            {
                Type = type;
                return this;
            }

            public static Message EmptyParserMethod(byte[] packet)
            {
                return new Message().SetType(MessageType.Empty);
            }
            
            public static Message HelloParserMethod(byte[] packet)
            {
                return new Message()
                    .SetType(MessageType.Hello)
                    .SetData(MessageDataContainer.ParseHelloMessage(packet));
            }
            
        }

        public static Message Parse(byte[] packet)
        {
            if (packet[0] < ParserMethods.Length)
            {
                return ParserMethods[packet[0]](packet);
            }

            return Message.EmptyParserMethod(packet);
        }
        
    }
}
