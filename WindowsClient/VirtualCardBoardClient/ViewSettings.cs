using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Net;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace VirtualCardBoardClient
{
    public partial class ViewSettings : Form
    {
        protected volatile bool IsGoingBack;
        protected volatile bool IsAlreadyClosed;

        protected Message DeviceHelloMessage;
        protected VirtualCardBoardInterface CardBoardInterface;

        private ViewSettings()
        {
        }
        public ViewSettings(Message deviceHelloMessage, VirtualCardBoardInterface cardBoardInterface)
        {
            InitializeComponent();
            IsGoingBack = false;
            IsAlreadyClosed = false;

            CardBoardInterface = cardBoardInterface;
            DeviceHelloMessage = deviceHelloMessage;
            {
                if (DeviceHelloMessage.Type == Message.MessageType.Hello)
                {
                    IHelloMessageData iData = DeviceHelloMessage.Data;
                    textBoxDeviceName.Text = iData.GetName();
                }
                else
                {
                    throw new Exception("DeviceHelloMessage.Type must be Message.MessageType.Hello instead \"" + DeviceHelloMessage.Type + "\"!");
                }
            }
        }

        private void ViewSettings_FormClosing(object sender, FormClosingEventArgs e)
        {
            if (IsAlreadyClosed)
            {
                return;
            }
            IsAlreadyClosed = true;
            if (!IsGoingBack)
            {
                Owner.Close();
            }
        }

        private void buttonBack_Click(object sender, EventArgs e)
        {
            Owner.Show();
            Owner.SetBounds(Bounds.X, Bounds.Y, Bounds.Width, Bounds.Height);
            IsGoingBack = true;
            Close();
        }

        private void buttonPing_Click(object sender, EventArgs e)
        {
            var helloMessageData = (IHelloMessageData)(DeviceHelloMessage.Data);
            var remoteAddress = new IPEndPoint(helloMessageData.GetAdress(), helloMessageData.GetPort());

            var msg = Message.CreatePingMessage();
            CardBoardInterface.WriteDataBytes(Message2BytesComposer.ComposeMessageBytes(msg), remoteAddress);
        }
    }
}
