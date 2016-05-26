using System;
using System.CodeDom;
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
        public const string StatusWaiting = " [Wainting]";
        public const string StatusReady = " [Ready]";

        protected volatile bool IsGoingBack;
        protected volatile bool IsAlreadyClosed;

        public Message DeviceHelloMessage;
        protected VirtualCardBoardInterface CardBoardInterface;

        public string DeviceStatus;
        public Object SyncStatus = new Object();

        private ViewSettings()
        {
        }

        public void UpdateDeviceStatus(string devName, string status)
        {
            textBoxDeviceName.Text = devName + status;
        }

        public ViewSettings(Message deviceHelloMessage, VirtualCardBoardInterface cardBoardInterface)
        {
            lock (SyncStatus)
            {
                InitializeComponent();
                IsGoingBack = false;
                IsAlreadyClosed = false;
                DeviceStatus = StatusReady;

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
                        throw new Exception("DeviceHelloMessage.Type must be Message.MessageType.Hello instead \"" +
                                            DeviceHelloMessage.Type + "\"!");
                    }
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
            lock (SyncStatus)
            {
                _SendModeNoPic();

                Owner.Show();
                Owner.SetBounds(Bounds.X, Bounds.Y, Bounds.Width, Bounds.Height);
                IsGoingBack = true;
                Close();
            }
        }

        private void buttonPing_Click(object sender, EventArgs e)
        {
            lock (SyncStatus)
            {
                var helloMessageData = (IHelloMessageData) (DeviceHelloMessage.Data);
                var remoteAddress = new IPEndPoint(helloMessageData.GetAdress(), helloMessageData.GetPort());

                var msg = Message.CreatePingMessage();
                CardBoardInterface.WriteDataBytes(Message2BytesComposer.ComposeMessageBytes(msg), remoteAddress);

                _SendModeSettings();
                _SendSettingsRequet();
            }
        }

        private void _SendModeSettings()
        {
            IHelloMessageData idata = DeviceHelloMessage.Data;
            var msg = Message.CreateModeMessage(MessageDataContainer.ModeType.Settings);

            CardBoardInterface.WriteDataBytes(
                Message2BytesComposer.ComposeMessageBytes(msg)
                , new IPEndPoint(idata.GetAdress(), idata.GetPort()));
        }

        private void _SendModeNoPic()
        {
            IHelloMessageData idata = DeviceHelloMessage.Data;
            var msg = Message.CreateModeMessage(MessageDataContainer.ModeType.NoPic);

            CardBoardInterface.WriteDataBytes(
                Message2BytesComposer.ComposeMessageBytes(msg)
                , new IPEndPoint(idata.GetAdress(), idata.GetPort()));
        }

        private void _SendModePic()
        {
            IHelloMessageData idata = DeviceHelloMessage.Data;
            var msg = Message.CreateModeMessage(MessageDataContainer.ModeType.Pic);

            CardBoardInterface.WriteDataBytes(
                Message2BytesComposer.ComposeMessageBytes(msg)
                , new IPEndPoint(idata.GetAdress(), idata.GetPort()));
        }

        private void _SendSettingsRequet()
        {
            
            Message msg = Message.CreateSettingsMessage(
                MessageDataContainer.MissionRequest, 0, 0, 0, 0
                , CardBoardInterface.GetServerAddress(), CardBoardInterface.GetServerPort());
            IHelloMessageData idata = DeviceHelloMessage.Data;

            CardBoardInterface.WriteDataBytes(
                Message2BytesComposer.ComposeMessageBytes(msg)
                , new IPEndPoint(idata.GetAdress(), idata.GetPort()));

            DeviceStatus = StatusWaiting;
            UpdateDeviceStatus(idata.GetName(), DeviceStatus);
            
        }

        private void ViewSettings_Load(object sender, EventArgs e)
        {
            lock (SyncStatus)
            {
                _SendSettingsRequet();
            }
        }

        public void UpdateBinocularParams(int focusDist, int focusVert, int width, int height)
        {
            numericUpDownEyesDistance.Value = focusDist;
            numericUpDownVerticalPosition.Value = focusVert;
            numericUpDownWidth.Value = width;
            numericUpDownHeigh.Value = height;
        }

        protected void _SendSettingsRequestAssign()
        {
            Message msg = Message.CreateSettingsMessage(
                MessageDataContainer.MissionRequest & MessageDataContainer.MissionAssign
                , (int) numericUpDownEyesDistance.Value
                , (int) numericUpDownVerticalPosition.Value
                , (int) numericUpDownWidth.Value
                , (int) numericUpDownHeigh.Value
                , CardBoardInterface.GetServerAddress(), CardBoardInterface.GetServerPort());
            IHelloMessageData idata = DeviceHelloMessage.Data;

            CardBoardInterface.WriteDataBytes(
                Message2BytesComposer.ComposeMessageBytes(msg)
                , new IPEndPoint(idata.GetAdress(), idata.GetPort()));

            DeviceStatus = StatusWaiting;
            UpdateDeviceStatus(idata.GetName(), DeviceStatus);
        }

        private void numericUpDownEyesDistance_ValueChanged(object sender, EventArgs e)
        {
            lock (SyncStatus)
            {
                _SendSettingsRequestAssign();
            }
        }

        private void numericUpDownVerticalPosition_Click(object sender, EventArgs e)
        {

        }

        private void numericUpDownWidth_ValueChanged(object sender, EventArgs e)
        {
            lock (SyncStatus)
            {
                _SendSettingsRequestAssign();
            }
        }

        private void numericUpDownVerticalPosition_ValueChanged(object sender, EventArgs e)
        {
            lock (SyncStatus)
            {
                _SendSettingsRequestAssign();
            }
        }

        private void numericUpDownHeigh_ValueChanged(object sender, EventArgs e)
        {
            lock (SyncStatus)
            {
                _SendSettingsRequestAssign();
            }
        }
    }
}
