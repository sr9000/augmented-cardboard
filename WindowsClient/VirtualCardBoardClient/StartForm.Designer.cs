namespace VirtualCardBoardClient
{
    partial class StartForm
    {
        /// <summary>
        /// Required designer variable.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        /// Clean up any resources being used.
        /// </summary>
        /// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Windows Form Designer generated code

        /// <summary>
        /// Required method for Designer support - do not modify
        /// the contents of this method with the code editor.
        /// </summary>
        private void InitializeComponent()
        {
            this.listBoxVirtualCardboardDevices = new System.Windows.Forms.ListBox();
            this.groupBox1 = new System.Windows.Forms.GroupBox();
            this.buttonFromStart = new System.Windows.Forms.Button();
            this.groupBox1.SuspendLayout();
            this.SuspendLayout();
            // 
            // listBoxVirtualCardboardDevices
            // 
            this.listBoxVirtualCardboardDevices.Anchor = ((System.Windows.Forms.AnchorStyles)((((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom) 
            | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
            this.listBoxVirtualCardboardDevices.FormattingEnabled = true;
            this.listBoxVirtualCardboardDevices.Location = new System.Drawing.Point(6, 19);
            this.listBoxVirtualCardboardDevices.Name = "listBoxVirtualCardboardDevices";
            this.listBoxVirtualCardboardDevices.Size = new System.Drawing.Size(411, 290);
            this.listBoxVirtualCardboardDevices.TabIndex = 0;
            this.listBoxVirtualCardboardDevices.SelectedIndexChanged += new System.EventHandler(this.listBoxVirtualCardboardDevices_SelectedIndexChanged);
            // 
            // groupBox1
            // 
            this.groupBox1.Anchor = ((System.Windows.Forms.AnchorStyles)((((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom) 
            | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
            this.groupBox1.Controls.Add(this.listBoxVirtualCardboardDevices);
            this.groupBox1.Location = new System.Drawing.Point(13, 13);
            this.groupBox1.Name = "groupBox1";
            this.groupBox1.Size = new System.Drawing.Size(417, 312);
            this.groupBox1.TabIndex = 1;
            this.groupBox1.TabStop = false;
            this.groupBox1.Text = "Active Virtual Card Boards";
            this.groupBox1.Enter += new System.EventHandler(this.groupBox1_Enter);
            // 
            // buttonFromStart
            // 
            this.buttonFromStart.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Right)));
            this.buttonFromStart.Location = new System.Drawing.Point(355, 331);
            this.buttonFromStart.Name = "buttonFromStart";
            this.buttonFromStart.Size = new System.Drawing.Size(75, 23);
            this.buttonFromStart.TabIndex = 2;
            this.buttonFromStart.Text = "Next>";
            this.buttonFromStart.UseVisualStyleBackColor = true;
            this.buttonFromStart.Click += new System.EventHandler(this.buttonFromStart_Click);
            // 
            // StartForm
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(442, 373);
            this.Controls.Add(this.buttonFromStart);
            this.Controls.Add(this.groupBox1);
            this.MinimumSize = new System.Drawing.Size(300, 300);
            this.Name = "StartForm";
            this.Text = "Virtual Cardboard Client";
            this.FormClosing += new System.Windows.Forms.FormClosingEventHandler(this.StartForm_FormClosing);
            this.Load += new System.EventHandler(this.MainForm_Load);
            this.groupBox1.ResumeLayout(false);
            this.ResumeLayout(false);

        }

        #endregion

        private System.Windows.Forms.ListBox listBoxVirtualCardboardDevices;
        private System.Windows.Forms.GroupBox groupBox1;
        private System.Windows.Forms.Button buttonFromStart;
    }
}

