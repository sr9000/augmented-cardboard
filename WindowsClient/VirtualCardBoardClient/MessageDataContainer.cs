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
        , ISettingsMessageData
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

        //ISettings
        public const byte MissionInform = 1;
        public const byte MissionRequest = 2;
        public const byte MissionAssign = 4;

        protected int FocusDistance, FocusVerticalCoordinate;
        protected int SimpleViewHeight, SimpleViewWidth;
        protected byte MessageMission;
        protected int RemotePort;
        protected IPAddress RemoteAddress;


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

            private static int _array_to_32bit_int(byte[] array, int offset)
            {
                int ret = 0;

                int multiplier = 1;
                for (int i = 0; i < 4; ++i)
                {
                    ret += (((int)array[i + offset]) & 0xFF) * multiplier;
                    multiplier *= 256;
                }

                return ret;
            }
            public static MessageDataContainer ParseSettingsMessage(byte[] packet)
            {
                MessageDataContainer data = new MessageDataContainer();

                data.MessageMission = packet[1];

                if ((data.MessageMission & (MissionAssign | MissionInform)) != 0)
                {
                    data.FocusDistance = _array_to_32bit_int(packet, 2);
                    data.FocusVerticalCoordinate = _array_to_32bit_int(packet, 6);
                    data.SimpleViewWidth = _array_to_32bit_int(packet, 10);
                    data.SimpleViewHeight = _array_to_32bit_int(packet, 14);
                }

                if ((data.MessageMission & MissionRequest) != 0)
                {
                    byte[] addr = new byte[4];
                    Array.Copy(packet, 18, addr, 0, 4);
                    data.RemoteAddress = new IPAddress(addr);

                    data.RemotePort = (packet[22] & 0xFF) + 256 * (packet[23] & 0xFF);
                }

                return data;
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

            public static byte[] ComposeModeMessageBytes(MessageDataContainer msgDataContainer)
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

            private static void _assign_32bit_int_to_array(byte[] array, int offset, int value)
            {
                array[offset] = (byte)(value % 256); //div 256^0
                array[offset + 1] = (byte)((value / 256) % 256); //div 256^1
                array[offset + 2] = (byte)((value / 65536) % 256); //div 256^2
                array[offset + 3] = (byte)((value / 16777216) % 256); //div 256^3
            }
            public static byte[] ComposeSettingsBytes(MessageDataContainer msgDataContainer)
            {
                ISettingsMessageData idata = msgDataContainer;
                int totalCount =
                    + 1 //message mission(flags)
                    + 4 //focusDist
                    + 4 //focusVert
                    + 4 //simpleWidth
                    + 4 //simpleHeight
                    + 4 //inet4address //[nulls]
                    + 2; //port         //[nulls]

                //create array
                byte[] ret = new byte[totalCount];

                //assign flags
                ret[0] = idata.GetFlags();

                if ((idata.GetFlags() & (MissionAssign | MissionInform)) !=
                    0)
                {
                    _assign_32bit_int_to_array(ret, 1, idata.GetFocusDistance());
                    _assign_32bit_int_to_array(ret, 5, idata.GetFocusVerticalCoordinate());
                    _assign_32bit_int_to_array(ret, 9, idata.GetSimpleViewWidth());
                    _assign_32bit_int_to_array(ret, 13, idata.GetSimpleViewHeight());
                }

                if ((idata.GetFlags() & MissionRequest) != 0)
                {
                    Array.Copy(idata.GetRemoteAddress().GetAddressBytes(), 0, ret, 17, 4);
                    ret[21] = (byte) (idata.GetRemotePort()%256);
                    ret[22] = (byte) (idata.GetRemotePort()/256);
                }

                //return packet
                return ret;
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

            public static MessageDataContainer CreateSettingsMessageData(
                byte flags, int focusDist, int focusVertPos, int width, int height, IPAddress address, int port)
            {
                return new MessageDataContainer
                {
                    FocusDistance = focusDist,
                    FocusVerticalCoordinate = focusVertPos,
                    SimpleViewWidth = width,
                    SimpleViewHeight = height,
                    RemoteAddress = address,
                    RemotePort = port,
                    MessageMission = (byte)(0x7 & flags)
                };
            }
        }

        //IHello
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

        //IMode
        public ModeType GetModeType()
        {
            return Mode;
        }

        //ISettings
        public int GetFocusDistance()
        {
            return FocusDistance;
        }

        public int GetFocusVerticalCoordinate()
        {
            return FocusVerticalCoordinate;
        }

        public int GetSimpleViewHeight()
        {
            return SimpleViewHeight;
        }

        public int GetSimpleViewWidth()
        {
            return SimpleViewWidth;
        }

        public int GetRemotePort()
        {
            return RemotePort;
        }

        public IPAddress GetRemoteAddress()
        {
            return RemoteAddress;
        }

        public byte GetFlags()
        {
            return MessageMission;
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

    public interface ISettingsMessageData
    {
        /*void SetFocusDistance(int focusDistance);
        void SetFocusVerticalCoordinate(int focusVerticalCoordinate);
        void SetSimpleViewHeight(int simpleViewHeight);
        void SetSimpleViewWidth(int simpleViewWidth);
        void SetMessageMission(byte flags);
        void SetRemotePort(int portNumber);
        void SetRemoteAddress(IPAddress address);*/

        int GetFocusDistance();
        int GetFocusVerticalCoordinate();
        int GetSimpleViewHeight();
        int GetSimpleViewWidth();
        int GetRemotePort();
        IPAddress GetRemoteAddress();
        byte GetFlags();
    }
}
