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
        public StartForm()
        {
            InitializeComponent();
        }

        protected void LisenCycle()
        {
            const int waitPeriod = 1000; //1 sec
            while (true)
            {
                byte[] packetBytes = CardBoardInterface.ReadDataBytes(waitPeriod);
                if (packetBytes.Length > 0)
                {
                    //TODO: parse message
                }
                try
                {
                    Thread.Sleep(0);
                }
                catch (ThreadInterruptedException)
                {
                    return;
                }
            }
        }

        private void MainForm_Load(object sender, EventArgs e)
        {

        }

        private void groupBox1_Enter(object sender, EventArgs e)
        {

        }

        private void buttonFromStart_Click(object sender, EventArgs e)
        {

        }
    }
}
