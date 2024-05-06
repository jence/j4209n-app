package jence.swing.app;

import javax.swing.JFrame;
import java.awt.GridBagLayout;
import javax.swing.JPanel;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.border.EmptyBorder;
import javax.swing.ImageIcon;
import javax.swing.JTextField;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import java.awt.Dimension;

public class NfcAppFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField uid;
	private JTextField textAuth_;
	private JTextField name;
	private JTextField textNDEF_;
	private JTextField textBlocks_;
	private JTextField textBlockSize_;

	public NfcAppFrame() {
		// TODO Auto-generated constructor stub
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{63, 0};
		gridBagLayout.rowHeights = new int[]{50, 0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 1.0, 1.0, 0.0, Double.MIN_VALUE};
		getContentPane().setLayout(gridBagLayout);
		
		JPanel head = new JPanel();
		head.setBorder(new EmptyBorder(10, 10, 10, 10));
		GridBagConstraints gbc_head = new GridBagConstraints();
		gbc_head.insets = new Insets(0, 0, 5, 0);
		gbc_head.fill = GridBagConstraints.HORIZONTAL;
		gbc_head.gridx = 0;
		gbc_head.gridy = 0;
		getContentPane().add(head, gbc_head);
		GridBagLayout gbl_head = new GridBagLayout();
		gbl_head.columnWidths = new int[]{0, 155, 0, 0, 0, 0, 0};
		gbl_head.rowHeights = new int[]{0, 0};
		gbl_head.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		gbl_head.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		head.setLayout(gbl_head);
		
		JLabel lblPort = new JLabel("Port");
		lblPort.setIcon(new ImageIcon(NfcAppFrame.class.getResource("/jence/icon/usb16.png")));
		GridBagConstraints gbc_lblPort = new GridBagConstraints();
		gbc_lblPort.anchor = GridBagConstraints.EAST;
		gbc_lblPort.insets = new Insets(0, 0, 0, 5);
		gbc_lblPort.gridx = 0;
		gbc_lblPort.gridy = 0;
		head.add(lblPort, gbc_lblPort);
		
		JComboBox comboPorts_ = new JComboBox();
		GridBagConstraints gbc_comboPorts_ = new GridBagConstraints();
		gbc_comboPorts_.insets = new Insets(0, 0, 0, 5);
		gbc_comboPorts_.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboPorts_.gridx = 1;
		gbc_comboPorts_.gridy = 0;
		head.add(comboPorts_, gbc_comboPorts_);
		
		JButton btnRefresh = new JButton("Refresh");
		btnRefresh.setIcon(new ImageIcon(NfcAppFrame.class.getResource("/jence/icon/usb.png")));
		GridBagConstraints gbc_btnRefresh = new GridBagConstraints();
		gbc_btnRefresh.insets = new Insets(0, 0, 0, 5);
		gbc_btnRefresh.gridx = 2;
		gbc_btnRefresh.gridy = 0;
		head.add(btnRefresh, gbc_btnRefresh);
		
		JButton btnDisconnect = new JButton("Connect");
		btnDisconnect.setIcon(new ImageIcon(NfcAppFrame.class.getResource("/jence/icon/connect.png")));
		GridBagConstraints gbc_btnDisconnect = new GridBagConstraints();
		gbc_btnDisconnect.insets = new Insets(0, 0, 0, 5);
		gbc_btnDisconnect.gridx = 3;
		gbc_btnDisconnect.gridy = 0;
		head.add(btnDisconnect, gbc_btnDisconnect);
		
		JButton btnScan = new JButton("Scan");
		btnScan.setIcon(new ImageIcon(NfcAppFrame.class.getResource("/jence/icon/scan.png")));
		GridBagConstraints gbc_btnScan = new GridBagConstraints();
		gbc_btnScan.insets = new Insets(0, 0, 0, 5);
		gbc_btnScan.gridx = 4;
		gbc_btnScan.gridy = 0;
		head.add(btnScan, gbc_btnScan);
		
		JPanel panelInfo = new JPanel();
		panelInfo.setBorder(new EmptyBorder(5, 10, 10, 10));
		GridBagConstraints gbc_panelInfo = new GridBagConstraints();
		gbc_panelInfo.insets = new Insets(0, 0, 5, 0);
		gbc_panelInfo.fill = GridBagConstraints.BOTH;
		gbc_panelInfo.gridx = 0;
		gbc_panelInfo.gridy = 1;
		getContentPane().add(panelInfo, gbc_panelInfo);
		GridBagLayout gbl_panelInfo = new GridBagLayout();
		gbl_panelInfo.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0};
		gbl_panelInfo.rowHeights = new int[]{0, 0, 0};
		gbl_panelInfo.columnWeights = new double[]{0.0, 0.0, 0.0, 1.0, 0.0, 1.0, Double.MIN_VALUE};
		gbl_panelInfo.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		panelInfo.setLayout(gbl_panelInfo);
		
		JLabel lblNewLabel = new JLabel("UID");
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 0;
		panelInfo.add(lblNewLabel, gbc_lblNewLabel);
		
		uid = new JTextField();
		GridBagConstraints gbc_uid = new GridBagConstraints();
		gbc_uid.insets = new Insets(0, 0, 5, 5);
		gbc_uid.fill = GridBagConstraints.HORIZONTAL;
		gbc_uid.gridx = 1;
		gbc_uid.gridy = 0;
		panelInfo.add(uid, gbc_uid);
		uid.setColumns(10);
		
		JLabel lblNewLabel_2 = new JLabel("Card Type");
		GridBagConstraints gbc_lblNewLabel_2 = new GridBagConstraints();
		gbc_lblNewLabel_2.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_2.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_2.gridx = 2;
		gbc_lblNewLabel_2.gridy = 0;
		panelInfo.add(lblNewLabel_2, gbc_lblNewLabel_2);
		
		name = new JTextField();
		GridBagConstraints gbc_name = new GridBagConstraints();
		gbc_name.insets = new Insets(0, 0, 5, 5);
		gbc_name.fill = GridBagConstraints.HORIZONTAL;
		gbc_name.gridx = 3;
		gbc_name.gridy = 0;
		panelInfo.add(name, gbc_name);
		name.setColumns(10);
		
		JLabel lblNewLabel_3 = new JLabel("Format");
		GridBagConstraints gbc_lblNewLabel_3 = new GridBagConstraints();
		gbc_lblNewLabel_3.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_3.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_3.gridx = 4;
		gbc_lblNewLabel_3.gridy = 0;
		panelInfo.add(lblNewLabel_3, gbc_lblNewLabel_3);
		
		textNDEF_ = new JTextField();
		GridBagConstraints gbc_textNDEF_ = new GridBagConstraints();
		gbc_textNDEF_.insets = new Insets(0, 0, 5, 0);
		gbc_textNDEF_.fill = GridBagConstraints.HORIZONTAL;
		gbc_textNDEF_.gridx = 5;
		gbc_textNDEF_.gridy = 0;
		panelInfo.add(textNDEF_, gbc_textNDEF_);
		textNDEF_.setColumns(10);
		
		JLabel lblNewLabel_1 = new JLabel("Auth Type");
		GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
		gbc_lblNewLabel_1.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_1.insets = new Insets(0, 0, 0, 5);
		gbc_lblNewLabel_1.gridx = 0;
		gbc_lblNewLabel_1.gridy = 1;
		panelInfo.add(lblNewLabel_1, gbc_lblNewLabel_1);
		
		textAuth_ = new JTextField();
		GridBagConstraints gbc_textAuth_ = new GridBagConstraints();
		gbc_textAuth_.insets = new Insets(0, 0, 0, 5);
		gbc_textAuth_.fill = GridBagConstraints.HORIZONTAL;
		gbc_textAuth_.gridx = 1;
		gbc_textAuth_.gridy = 1;
		panelInfo.add(textAuth_, gbc_textAuth_);
		textAuth_.setColumns(10);
		
		JLabel lblNewLabel_4 = new JLabel("Blocks");
		GridBagConstraints gbc_lblNewLabel_4 = new GridBagConstraints();
		gbc_lblNewLabel_4.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_4.insets = new Insets(0, 0, 0, 5);
		gbc_lblNewLabel_4.gridx = 2;
		gbc_lblNewLabel_4.gridy = 1;
		panelInfo.add(lblNewLabel_4, gbc_lblNewLabel_4);
		
		textBlocks_ = new JTextField();
		GridBagConstraints gbc_textBlocks_ = new GridBagConstraints();
		gbc_textBlocks_.anchor = GridBagConstraints.WEST;
		gbc_textBlocks_.insets = new Insets(0, 0, 0, 5);
		gbc_textBlocks_.gridx = 3;
		gbc_textBlocks_.gridy = 1;
		panelInfo.add(textBlocks_, gbc_textBlocks_);
		textBlocks_.setColumns(10);
		
		JLabel lblNewLabel_5 = new JLabel("Block Size (byte)");
		GridBagConstraints gbc_lblNewLabel_5 = new GridBagConstraints();
		gbc_lblNewLabel_5.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_5.insets = new Insets(0, 0, 0, 5);
		gbc_lblNewLabel_5.gridx = 4;
		gbc_lblNewLabel_5.gridy = 1;
		panelInfo.add(lblNewLabel_5, gbc_lblNewLabel_5);
		
		textBlockSize_ = new JTextField();
		GridBagConstraints gbc_textBlockSize_ = new GridBagConstraints();
		gbc_textBlockSize_.fill = GridBagConstraints.HORIZONTAL;
		gbc_textBlockSize_.gridx = 5;
		gbc_textBlockSize_.gridy = 1;
		panelInfo.add(textBlockSize_, gbc_textBlockSize_);
		textBlockSize_.setColumns(10);
		
		JPanel tabFolderContainer = new JPanel();
		GridBagConstraints gbc_tabFolderContainer = new GridBagConstraints();
		gbc_tabFolderContainer.insets = new Insets(0, 0, 5, 0);
		gbc_tabFolderContainer.fill = GridBagConstraints.BOTH;
		gbc_tabFolderContainer.gridx = 0;
		gbc_tabFolderContainer.gridy = 2;
		getContentPane().add(tabFolderContainer, gbc_tabFolderContainer);
		GridBagLayout gbl_tabFolderContainer = new GridBagLayout();
		gbl_tabFolderContainer.columnWidths = new int[]{61, 0};
		gbl_tabFolderContainer.rowHeights = new int[]{70, 0};
		gbl_tabFolderContainer.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_tabFolderContainer.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		tabFolderContainer.setLayout(gbl_tabFolderContainer);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		GridBagConstraints gbc_tabbedPane = new GridBagConstraints();
		gbc_tabbedPane.fill = GridBagConstraints.HORIZONTAL;
		gbc_tabbedPane.anchor = GridBagConstraints.NORTH;
		gbc_tabbedPane.gridx = 0;
		gbc_tabbedPane.gridy = 0;
		tabFolderContainer.add(tabbedPane, gbc_tabbedPane);
		
		JPanel panel = new JPanel();
		tabbedPane.addTab("RAW", null, panel, null);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]{600, 0};
		gbl_panel.rowHeights = new int[]{40, 0, 0};
		gbl_panel.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{1.0, 1.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);
		
		JPanel panel_3 = new JPanel();
		GridBagConstraints gbc_panel_3 = new GridBagConstraints();
		gbc_panel_3.insets = new Insets(0, 0, 5, 0);
		gbc_panel_3.fill = GridBagConstraints.BOTH;
		gbc_panel_3.gridx = 0;
		gbc_panel_3.gridy = 0;
		panel.add(panel_3, gbc_panel_3);
		
		JPanel panel_4 = new JPanel();
		GridBagConstraints gbc_panel_4 = new GridBagConstraints();
		gbc_panel_4.fill = GridBagConstraints.BOTH;
		gbc_panel_4.gridx = 0;
		gbc_panel_4.gridy = 1;
		panel.add(panel_4, gbc_panel_4);
		
		JPanel panel_1 = new JPanel();
		tabbedPane.addTab("NDEF", null, panel_1, null);
		
		JPanel panel_2 = new JPanel();
		tabbedPane.addTab("EMULATE", null, panel_2, null);
		
		JPanel panelFooter = new JPanel();
		GridBagConstraints gbc_panelFooter = new GridBagConstraints();
		gbc_panelFooter.insets = new Insets(0, 0, 5, 0);
		gbc_panelFooter.fill = GridBagConstraints.BOTH;
		gbc_panelFooter.gridx = 0;
		gbc_panelFooter.gridy = 3;
		getContentPane().add(panelFooter, gbc_panelFooter);
		
		
		this.setSize(800,600);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);

	}

}
