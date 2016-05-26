using System;
using System.Collections.Generic;
using System.Net;
using System.Threading;
using System.Windows.Forms;
using VirtualCardBoardClient.Properties;

namespace VirtualCardBoardClient
{
    public partial class StartForm : Form
    {
        protected volatile VirtualCardBoardInterface CardBoardInterface = new VirtualCardBoardInterface();
        protected Thread VirtualCardBoardsListener;
        protected volatile List<ClientMessage> VirtualCardboardDevicesList = new List<ClientMessage>();

        protected volatile ViewSettings ViewSettingsWindow = null;
        protected volatile Object ViewSettingsSynchronizator = new Object();

        public StartForm()
        {
            InitializeComponent();
        }

        protected void HelloMessageProceed(Message msg, IPEndPoint localEndPoint)
        {
            bool isAlreadyContainDevice = false;
            IHelloMessageData iNewData = msg.Data;
            foreach (var deviceMessage in VirtualCardboardDevicesList)
            {
                IHelloMessageData iData = deviceMessage.RecievedMessage.Data;
                if (iData.GetName() == iNewData.GetName()
                    && iData.GetAdress().Equals(iNewData.GetAdress())
                    && iData.GetPort() == iNewData.GetPort())
                {
                    isAlreadyContainDevice = true;
                    break;
                }
            }

            if (!isAlreadyContainDevice)
            {
                VirtualCardboardDevicesList.Add(
                    new ClientMessage()
                    {
                        RecievedMessage = msg
                        , LocalEnpPoint = localEndPoint
                    });
                string name = ((IHelloMessageData)msg.Data).GetName();
                Invoke(new MethodInvoker(delegate()
                {
                    listBoxVirtualCardboardDevices.Items.Add(name);
                }));
            }
        }

        protected void SettingsMessageProceed(Message msg)
        {
            lock (ViewSettingsSynchronizator)
            {
                if (ViewSettingsWindow == null) return;

                lock (ViewSettingsWindow.SyncStatus)
                {
                    ISettingsMessageData idata = msg.Data;

                    if ((idata.GetFlags() & MessageDataContainer.MissionInform) != 0)
                    {
                        ViewSettingsWindow.UpdateBinocularParams(
                            idata.GetFocusDistance()
                            , idata.GetFocusVerticalCoordinate()
                            , idata.GetSimpleViewWidth()
                            , idata.GetSimpleViewHeight()
                            );
                    }

                    ViewSettingsWindow.DeviceStatus = ViewSettings.StatusReady;
                    ViewSettingsWindow.UpdateDeviceStatus(
                        ((IHelloMessageData)ViewSettingsWindow.DeviceHelloMessage.RecievedMessage.Data).GetName()
                        , ViewSettingsWindow.DeviceStatus);
                }
            }
        }

        protected void ListenCycle()
        {
            const int waitPeriod = 1000; //1 sec
            while (true)
            {
                //check Interrupted
                try
                {
                    Thread.Sleep(0);
                }
                catch (ThreadInterruptedException)
                {
                    return;
                }

                //cycle body
                var packet = CardBoardInterface.ReadDataBytes(waitPeriod);
                byte[] packetBytes = packet.PacketBytes;
                if (packetBytes.Length > 0)
                {
                    var msg = MessageParser.Parse(packetBytes);
                    if (!msg.HasData())
                    {
                        continue;
                    }

                    switch (msg.Type)
                    {
                        case Message.MessageType.Hello:
                            HelloMessageProceed(msg, packet.LocalEndPoint);
                            break;
                        case Message.MessageType.Ping:
                            break;
                        case Message.MessageType.Empty:
                            break;
                        case Message.MessageType.Settings:
                            SettingsMessageProceed(msg);
                            break;
                        default:
                            throw new ArgumentOutOfRangeException("Unproceeded message ith type \"" + msg.Type + "\"");
                    }
                }
            }
        }

        private void MainForm_Load(object sender, EventArgs e)
        {
            VirtualCardBoardsListener = new Thread(ListenCycle);
            VirtualCardBoardsListener.Start();
        }

        private void groupBox1_Enter(object sender, EventArgs e)
        {
        }

        private void buttonFromStart_Click(object sender, EventArgs e)
        {
            if (listBoxVirtualCardboardDevices.SelectedItem == null)
            {
                MessageBox.Show(Resources.StartForm_buttonFromStart_Click_Device_Not_Selected);
                return;
            }
            int selectedIndex = listBoxVirtualCardboardDevices.SelectedIndex;
            lock (ViewSettingsSynchronizator)
            {
                ViewSettingsWindow = new ViewSettings(VirtualCardboardDevicesList[selectedIndex], CardBoardInterface);
                ViewSettingsWindow.Show(this);
                ViewSettingsWindow.SetBounds(Bounds.X, Bounds.Y, Bounds.Width, Bounds.Height);
                ViewSettingsWindow.FormClosing += (o, args) =>
                {
                    lock (ViewSettingsSynchronizator)
                    {
                        ViewSettingsWindow = null;
                    }
                };
            }
            Hide();
        }

        private void StartForm_FormClosing(object sender, FormClosingEventArgs e)
        {
            VirtualCardBoardsListener.Interrupt();
            VirtualCardBoardsListener.Join();
        }

        private void listBoxVirtualCardboardDevices_SelectedIndexChanged(object sender, EventArgs e)
        {
            if (listBoxVirtualCardboardDevices.Items.Count <= 0) return;

            int selectedIndex = listBoxVirtualCardboardDevices.SelectedIndex;

            var helloMessageData = (IHelloMessageData) (VirtualCardboardDevicesList[selectedIndex].RecievedMessage.Data);
            var remoteAddress = new IPEndPoint(helloMessageData.GetAdress(), helloMessageData.GetPort());

            var msg = Message.CreatePingMessage();
            CardBoardInterface.WriteDataBytes(Message2BytesComposer.ComposeMessageBytes(msg), remoteAddress);
        }
    }
}
