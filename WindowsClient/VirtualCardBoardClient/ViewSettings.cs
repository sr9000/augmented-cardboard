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

        protected string DeviceStatus;

        private ViewSettings()
        {
        }

        protected void UpdateDeviceStatus(string devName, string status)
        {
            textBoxDeviceName.Text = devName + " [" + status + "]";
        }

        public ViewSettings(Message deviceHelloMessage, VirtualCardBoardInterface cardBoardInterface)
        {
            InitializeComponent();
            IsGoingBack = false;
            IsAlreadyClosed = false;
            DeviceStatus = "Waiting";

            CardBoardInterface = cardBoardInterface;
            DeviceHelloMessage = deviceHelloMessage;
            {
                if (DeviceHelloMessage.Type == Message.MessageType.Hello)
                {
                    IHelloMessageData iData = DeviceHelloMessage.Data;
                    UpdateDeviceStatus(iData.GetName(), DeviceStatus);
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

            Message msg;

            msg = Message.CreatePingMessage();
            CardBoardInterface.WriteDataBytes(Message2BytesComposer.ComposeMessageBytes(msg), remoteAddress);

            msg = Message.CreateModeMessage(MessageDataContainer.ModeType.Settings);
            CardBoardInterface.WriteDataBytes(Message2BytesComposer.ComposeMessageBytes(msg), remoteAddress);
        }
    }
}
