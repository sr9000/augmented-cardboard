using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace VirtualCardBoardClient
{
    public partial class ViewSettings : Form
    {
        private volatile bool isGoingBack;
        private volatile bool isAlreadyClosed;
        public ViewSettings()
        {
            InitializeComponent();
            isGoingBack = false;
            isAlreadyClosed = false;
        }

        private void ViewSettings_FormClosing(object sender, FormClosingEventArgs e)
        {
            if (isAlreadyClosed)
            {
                return;
            }
            isAlreadyClosed = true;
            if (!isGoingBack)
            {
                Owner.Close();
            }
        }

        private void button2_Click(object sender, EventArgs e)
        {
            Owner.Show();
            Owner.SetBounds(Bounds.X, Bounds.Y, Bounds.Width, Bounds.Height);
            isGoingBack = true;
            Close();
        }
    }
}
