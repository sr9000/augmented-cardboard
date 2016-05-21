using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace VirtualCardBoardClient
{
    public partial class StartForm : Form
    {
        protected VirtualCardBoardInterface CardBoardInterface = new VirtualCardBoardInterface();
        protected Thread VirtualCardBoardsListener;
        protected List<MessageParser.Message> VirtualCardboardDevicesList = new List<MessageParser.Message>();

        public StartForm()
        {
            InitializeComponent();
        }

        protected void LisenCycle()
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
                byte[] packetBytes = CardBoardInterface.ReadDataBytes(waitPeriod);
                if (packetBytes.Length > 0)
                {
                    var msg = MessageParser.Parse(packetBytes);
                    if (!msg.HasData())
                    {
                        continue;
                    }

                    bool isAlreadyContainDevice = false;
                    IHelloMessageData iNewData = msg.Data;
                    foreach (var deviceMessage in VirtualCardboardDevicesList)
                    {
                        IHelloMessageData iData = deviceMessage.Data;
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
                        VirtualCardboardDevicesList.Add(msg);
                        string name = ((IHelloMessageData) msg.Data).GetName();
                        Invoke(new MethodInvoker(delegate()
                        {
                            listBoxVirtualCardboardDevices.Items.Add(name);
                        }));
                    }
                }
            }
        }

        private void MainForm_Load(object sender, EventArgs e)
        {
            VirtualCardBoardsListener = new Thread(LisenCycle);
            VirtualCardBoardsListener.Start();
        }

        private void groupBox1_Enter(object sender, EventArgs e)
        {

        }

        private void buttonFromStart_Click(object sender, EventArgs e)
        {

        }

        private void StartForm_FormClosing(object sender, FormClosingEventArgs e)
        {
            VirtualCardBoardsListener.Interrupt();
            VirtualCardBoardsListener.Join();
        }
    }
}
