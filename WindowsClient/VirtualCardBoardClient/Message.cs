using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace VirtualCardBoardClient
{
    public class Message
    {
        public enum MessageType
        {
            Hello, Ping, Empty
            , Mode
            , SettRq, SettRp
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
                .SetData(MessageDataContainer.ParseMethods.ParseHelloMessage(packet));
        }

        public static Message CreatePingMessage()
        {
            return new Message()
                .SetType(MessageType.Ping)
                .SetData(MessageDataContainer.CreateMethods.CreatePingMessageData());
        }

        public static Message CreateModeMessage(MessageDataContainer.ModeType mode)
        {
            return new Message()
                .SetType(MessageType.Mode)
                .SetData(MessageDataContainer.CreateMethods.CreateModeMessageData(mode));
        }
    }
}
