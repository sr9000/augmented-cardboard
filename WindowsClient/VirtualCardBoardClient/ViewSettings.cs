using System;
using System.CodeDom;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Net;
using System.Text;
using System.Threading;
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

        public ClientMessage DeviceHelloMessage;
        protected VirtualCardBoardInterface CardBoardInterface;

        public volatile string DeviceStatus;
        public volatile Object SyncStatus = new Object();

        protected volatile bool IsChangeEventBlocked;
        protected volatile Object SyncNumericsUpdater = new Object();

        private ViewSettings()
        {
        }

        private void testf(string devName, string status)
        {
            textBoxDeviceName.Text = devName + status;
        }

        public void UpdateDeviceStatus(string devName, string status)
        {
            BeginInvoke(new MethodInvoker(delegate
            {
                testf(devName, status);
                //textBoxDeviceName.Text = devName + status;
            }));
        }

        public ViewSettings(ClientMessage deviceHelloMessage, VirtualCardBoardInterface cardBoardInterface)
        {
            lock (SyncStatus)
            {
                InitializeComponent();
                IsGoingBack = false;
                IsAlreadyClosed = false;
                IsChangeEventBlocked = false;
                DeviceStatus = StatusReady;

                CardBoardInterface = cardBoardInterface;
                DeviceHelloMessage = deviceHelloMessage;
                {
                    if (DeviceHelloMessage.RecievedMessage.Type == Message.MessageType.Hello)
                    {
                        IHelloMessageData iData = DeviceHelloMessage.RecievedMessage.Data;
                    }
                    else
                    {
                        throw new Exception("DeviceHelloMessage.Type must be Message.MessageType.Hello instead \"" +
                                            DeviceHelloMessage.RecievedMessage.Type + "\"!");
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
                var helloMessageData = (IHelloMessageData) (DeviceHelloMessage.RecievedMessage.Data);
                var remoteAddress = new IPEndPoint(helloMessageData.GetAdress(), helloMessageData.GetPort());

                var msg = Message.CreatePingMessage();
                CardBoardInterface.WriteDataBytes(Message2BytesComposer.ComposeMessageBytes(msg), remoteAddress);

                _SendModeSettings();
                _SendSettingsRequet();
            }
        }

        private void _SendModeSettings()
        {
            IHelloMessageData idata = DeviceHelloMessage.RecievedMessage.Data;
            var msg = Message.CreateModeMessage(MessageDataContainer.ModeType.Settings);

            CardBoardInterface.WriteDataBytes(
                Message2BytesComposer.ComposeMessageBytes(msg)
                , new IPEndPoint(idata.GetAdress(), idata.GetPort()));
        }

        private void _SendModeNoPic()
        {
            IHelloMessageData idata = DeviceHelloMessage.RecievedMessage.Data;
            var msg = Message.CreateModeMessage(MessageDataContainer.ModeType.NoPic);

            CardBoardInterface.WriteDataBytes(
                Message2BytesComposer.ComposeMessageBytes(msg)
                , new IPEndPoint(idata.GetAdress(), idata.GetPort()));
        }

        private void _SendModePic()
        {
            IHelloMessageData idata = DeviceHelloMessage.RecievedMessage.Data;
            var msg = Message.CreateModeMessage(MessageDataContainer.ModeType.Pic);

            CardBoardInterface.WriteDataBytes(
                Message2BytesComposer.ComposeMessageBytes(msg)
                , new IPEndPoint(idata.GetAdress(), idata.GetPort()));
        }

        private void _SendSettingsRequet()
        {
            
            Message msg = Message.CreateSettingsMessage(
                MessageDataContainer.MissionRequest, 0, 0, 0, 0
                , DeviceHelloMessage.LocalEnpPoint.Address, CardBoardInterface.GetServerPort());
            IHelloMessageData idata = DeviceHelloMessage.RecievedMessage.Data;

            CardBoardInterface.WriteDataBytes(
                Message2BytesComposer.ComposeMessageBytes(msg)
                , new IPEndPoint(idata.GetAdress(), idata.GetPort()));

            DeviceStatus = StatusWaiting;
            UpdateDeviceStatus(idata.GetName(), DeviceStatus);
            
        }

        private void ViewSettings_Load(object sender, EventArgs e)
        {
            IHelloMessageData iData = DeviceHelloMessage.RecievedMessage.Data;
            lock (SyncStatus)
            {
                UpdateDeviceStatus(iData.GetName(), DeviceStatus);

                numericUpDownEyesDistance.Maximum = Decimal.MaxValue;
                numericUpDownHeigh.Maximum = Decimal.MaxValue;
                numericUpDownVerticalPosition.Maximum = Decimal.MaxValue;
                numericUpDownWidth.Maximum = Decimal.MaxValue;
                _SendModeSettings();
                _SendSettingsRequet();
            }
        }

        public void UpdateBinocularParams(int focusDist, int focusVert, int width, int height)
        {
            BeginInvoke (new MethodInvoker(delegate
            {
                lock (SyncNumericsUpdater)
                {
                    IsChangeEventBlocked = true;

                    numericUpDownEyesDistance.Value = focusDist;
                    numericUpDownVerticalPosition.Value = focusVert;
                    numericUpDownWidth.Value = width;
                    numericUpDownHeigh.Value = height;

                    IsChangeEventBlocked = false;
                }
            }));
        }

        protected void _SendSettingsRequestAssign()
        {
            Message msg = Message.CreateSettingsMessage(
                MessageDataContainer.MissionRequest | MessageDataContainer.MissionAssign
                , (int) numericUpDownEyesDistance.Value
                , (int) numericUpDownVerticalPosition.Value
                , (int) numericUpDownWidth.Value
                , (int) numericUpDownHeigh.Value
                , DeviceHelloMessage.LocalEnpPoint.Address, CardBoardInterface.GetServerPort());
            IHelloMessageData idata = DeviceHelloMessage.RecievedMessage.Data;

            CardBoardInterface.WriteDataBytes(
                Message2BytesComposer.ComposeMessageBytes(msg)
                , new IPEndPoint(idata.GetAdress(), idata.GetPort()));

            DeviceStatus = StatusWaiting;
            UpdateDeviceStatus(idata.GetName(), DeviceStatus);
        }

        private void numericUpDownEyesDistance_ValueChanged(object sender, EventArgs e)
        {
            if (IsChangeEventBlocked) return;
            lock (SyncStatus)
            {
                //if (DeviceStatus == StatusReady) 
                    _SendSettingsRequestAssign();
            }
        }

        private void numericUpDownVerticalPosition_Click(object sender, EventArgs e)
        {

        }

        private void numericUpDownWidth_ValueChanged(object sender, EventArgs e)
        {
            if (IsChangeEventBlocked) return;
            lock (SyncStatus)
            {
                //if (DeviceStatus == StatusReady) 
                    _SendSettingsRequestAssign();
            }
        }

        private void numericUpDownVerticalPosition_ValueChanged(object sender, EventArgs e)
        {
            if (IsChangeEventBlocked) return;
            lock (SyncStatus)
            {
                //if (DeviceStatus == StatusReady) 
                    _SendSettingsRequestAssign();
            }
        }

        private void numericUpDownHeigh_ValueChanged(object sender, EventArgs e)
        {
            if (IsChangeEventBlocked) return;
            lock (SyncStatus)
            {
                //if (DeviceStatus == StatusReady) 
                    _SendSettingsRequestAssign();
            }
        }
    }
}
