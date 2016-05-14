using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace VirtualCardBoardClient
{
    class VirtualCardBoardInterface
    {
        protected Listener AndroidListener = new Listener();
        protected byte[] Secret = {207, 219, 43, 202, 53, 226, 172, 160, 100, 227, 145, 120, 187, 99, 170, 225};

        public VirtualCardBoardInterface()
        {
            AndroidListener.Start();
            if (!AndroidListener.IsStarted())
            {
                throw new Exception("Vsyo propalo, shef, vsyo propalo!!!");
            }
        }

        public byte[] ReadDataBytes()
        {
            var rawData = AndroidListener.Read();
            if (rawData.Length < 20)
            {
                return new List<byte>().ToArray();
            }
            bool isPassSecret = rawData.Take(16).Zip(Secret, (a, b) => a == b).All(x => x);
            var data = rawData.Skip(16);
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

    }
}
