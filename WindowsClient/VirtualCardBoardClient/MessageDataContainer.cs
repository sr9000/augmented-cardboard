using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.NetworkInformation;
using System.Security.Cryptography.X509Certificates;
using System.Text;
using System.Threading.Tasks;

namespace VirtualCardBoardClient
{
    public class MessageDataContainer
        : IHelloMessageData
        , IPingMessageData
        , IModeMessageData
    {
        //IHelloMessageData
        protected IPAddress Address;
        protected int Port;
        protected string Name;

        //IModeName
        public enum ModeType
        {
            Pic, NoPic, Settings
        }
        private const byte ModeTypePicCode = 0;
        private const byte ModeTypeNoPicCode = 1;
        private const byte ModeTypeSettingsCode = 2;

        protected ModeType Mode;

        public static class ParseMethods
        {
            public static MessageDataContainer ParseHelloMessage(byte[] packet)
            {
                if (packet.Length < 8)
                {
                    return null;
                }

                return new MessageDataContainer
                {
                    Address = new IPAddress(packet.Skip(1).Take(4).ToArray()),
                    Port = packet[5] + 256*packet[6],
                    Name =
                        (packet[7] == 0)
                            ? "Default VCB"
                            : packet.Skip(7).TakeWhile(x => x != 0).Aggregate("", (a, b) => a + (char) b)
                };
            }
        }

        public static class ComposeMethods
        {
            public static byte[] ComposePingMessageBytes(MessageDataContainer msgDataContainer)
            {
                return new byte[] {0}; //return zero byte
            }

            public static byte[] ComposeEmptyMessageBytes(MessageDataContainer msgDataContainer)
            {
                return new byte[] {0};
            }

            internal static byte[] ComposeModeMessageBytes(MessageDataContainer msgDataContainer)
            {
                switch (((IModeMessageData)msgDataContainer).GetModeType())
                {
                    case ModeType.Pic:
                        return new[] {ModeTypePicCode};
                    case ModeType.NoPic:
                        return new[] {ModeTypeNoPicCode};
                    case ModeType.Settings:
                        return new[] {ModeTypeSettingsCode};
                    default:
                        throw new ArgumentOutOfRangeException("msgDataContainer");
                }
            }
        }

        public static class CreateMethods
        {
            public static MessageDataContainer CreatePingMessageData()
            {
                return new MessageDataContainer();
            }

            public static MessageDataContainer CreateModeMessageData(ModeType mode)
            {
                return new MessageDataContainer
                {
                    Mode = mode
                };
            }
        }

        public IPAddress GetAdress()
        {
            return Address;
        }

        public int GetPort()
        {
            return Port;
        }

        public string GetName()
        {
            return Name;
        }

        public ModeType GetModeType()
        {
            return Mode;
        }
    }

    public interface IHelloMessageData
    {
        IPAddress GetAdress();
        int GetPort();
        string GetName();
    }

    public interface IPingMessageData
    {
    }


    public interface IModeMessageData
    {
        MessageDataContainer.ModeType GetModeType();
    }
}
