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

        protected static SpecifiedPacketParser[] ParserMethods =
        {
            Message.HelloParserMethod      //Hello
            , Message.EmptyParserMethod    //Ping
            , Message.EmptyParserMethod    //Mode
            , Message.SettingsParserMethod //Settings
            //Empty
        };

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
