using System.Collections.Generic;
using System.Linq;

namespace VirtualCardBoardClient
{
    public class Message2BytesComposer
    {
        protected delegate byte[] SpecifiedPacketComposer(MessageDataContainer msgDataContainer);

        protected static Dictionary<Message.MessageType, SpecifiedPacketComposer> MessageType2Delegate = 
            new Dictionary<Message.MessageType, SpecifiedPacketComposer>
            {
                { Message.MessageType.Hello, MessageDataContainer.ComposeMethods.ComposeEmptyMessageBytes }
                , { Message.MessageType.Ping, MessageDataContainer.ComposeMethods.ComposePingMessageBytes }
                , { Message.MessageType.Mode, MessageDataContainer.ComposeMethods.ComposeModeMessageBytes }
                , { Message.MessageType.Settings, MessageDataContainer.ComposeMethods.ComposeSettingsBytes }
            };

        protected static Dictionary<Message.MessageType, byte> MessageType2Byte = 
            new Dictionary<Message.MessageType, byte>
            {
                { Message.MessageType.Hello, 0 }
                , { Message.MessageType.Ping, 1 }
                , { Message.MessageType.Mode, 2 }
                , { Message.MessageType.Settings, 3 }
            };

        public static byte[] ComposeMessageBytes(Message msg)
        {
            if (msg.Type == Message.MessageType.Empty)
            {
                return null;
            }

            return
                new [] { MessageType2Byte[msg.Type]}
                .Concat(MessageType2Delegate[msg.Type](msg.Data))
                .ToArray();
        }
    }
}
