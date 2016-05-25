using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Text;
using System.Threading.Tasks;

namespace VirtualCardBoardClient
{
    public class VirtualCardBoardInterface
    {
        protected Listener AndroidListener = new Listener();
        protected Sender AndroidSender = new Sender();
        protected byte[] Secret = {207, 219, 43, 202, 53, 226, 172, 160, 100, 227, 145, 120, 187, 99, 170, 225};

        public VirtualCardBoardInterface()
        {
            AndroidListener.Start();
            if (!AndroidListener.IsStarted())
            {
                throw new Exception("Vsyo propalo, shef, vsyo propalo!!!");
            }
        }

        public byte[] ReadDataBytes(int timeWaitMilliseconds = 0)
        {
            var rawData = AndroidListener.Read(timeWaitMilliseconds);
            if (rawData.Length < (Secret.Length + 4))
            {
                return new List<byte>().ToArray();
            }
            bool isPassSecret = rawData.Take(Secret.Length).Zip(Secret, (a, b) => a == b).All(x => x);
            var data = rawData.Skip(Secret.Length);
            if (!isPassSecret)
            {
                return new List<byte>().ToArray();
            }
            int length;
            {
                //parse length
                length = 0;
                var rawLength = data.Take(4);
                int multiplier = 1;
                foreach (var i in rawLength)
                {
                    length += i * multiplier;
                    multiplier *= 256;
                }
            }
            var clearData = data.Skip(4).ToArray();
            if (clearData.Length > length || length < 0)
            {
                return new List<byte>().ToArray();
            }
            return clearData.Take(length).ToArray();
        }

        public VirtualCardBoardInterface WriteDataBytes(byte[] dataBytes, IPEndPoint remoteAddress)
        {
            byte[] bytes = new byte[dataBytes.Length + Secret.Length + 4];

            Array.Copy(Secret, bytes, Secret.Length);
            {//length
                int len = dataBytes.Length;
                for (int i = 0; i < 4; ++i)
                {
                    byte b = (byte) ((len%256) & 0xFF);
                    bytes[Secret.Length + i] = b;
                    len /= 256;
                }
            }
            Array.Copy(dataBytes, 0, bytes, Secret.Length + 4, dataBytes.Length);

            AndroidSender.Write(bytes, remoteAddress);
            return this;
        }
    }
}
