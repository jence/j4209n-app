package jence.swing.app;

import javax.swing.JFrame;
import java.awt.GridBagLayout;
import javax.swing.JPanel;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JButton;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.ImageIcon;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import jence.jni.J4209N;
import jence.swt.app.NfcApp;

import javax.swing.JSpinner;
import javax.swing.JCheckBox;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.LinkedList;
import java.awt.event.ActionEvent;
import javax.swing.ScrollPaneConstants;

public class NfcAppFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JComboBox<String> comboPorts_;
	private JButton btnRefresh;
	private JButton btnConnect;
	private JButton btnDisconnect;
	private JButton btnScan;

	private JTabbedPane tabFolder;
	private JTable table_;
	private DefaultTableModel NDEFModel = new DefaultTableModel();
	private LinkedList<String> NDEFcolumnNames = new LinkedList<String>();
	private ArrayList<Integer> NDEFcolumnWidth = new ArrayList<Integer>();

	private ArrayList<ArrayList<String>> NDEFData = new ArrayList<ArrayList<String>>();

	private JButton btnAuth_;

	private JTextField uid_;
	private JTextField textAuth_;
	private JTextField name_;
	private JTextField textNDEF_;
	private JTextField textBlocks_;
	private JTextField textBlockSize_;
	private JTable table_1;
	private JTextField textField;
	private JTextField textField_1;

	private JLabel status_;

	private JDialog authDialog;

	private J4209N.KeyData keydata_ = new J4209N.KeyData(0); // full access

	private void status(String text) {
		status_.setText(text);
	}

	private boolean portlist() {
		try {
			String[] ports = NfcApp.driver_.listPorts();
			comboPorts_.removeAllItems();
			for (int i = 0; i < ports.length; i++) {
				comboPorts_.addItem(ports[i]);
			}
			if (ports.length > 0) {
				comboPorts_.setSelectedIndex(0);
			} else {
				return false;
			}
			return true;
		} catch (Exception e) {
//			prompt(e.getMessage()
//					+ " Please check if the device is attached to an USB port.",
//					SWT.ICON_WARNING);
		}
		return false;
	}

	private boolean disconnect() {
		try {
			NfcApp.driver_.close();
			return true;
		} catch (Exception e) {
//			prompt(e.getMessage(), SWT.ICON_WARNING);
		}
		return false;
	}

	public static <T extends JComponent> void setEnabled(boolean enabled, T... components) {
		for (T component : components) {
			component.setEnabled(enabled);
		}
	}

	private void dump() {
		try {
			// remove all rows and columns
			while (NDEFModel.getRowCount() > 0) {
				NDEFModel.removeRow(0);
			}

			// Remove all columns
			while (NDEFModel.getColumnCount() > 0) {
				NDEFModel.setColumnCount(NDEFModel.getColumnCount() - 1); // Reduce the column count by 1 until there
																			// are no more columns left
			}

			byte[] data = null;
			NfcApp.driver_.keys(null, null); // use default keys of the card

			NDEFcolumnNames.clear();
			NDEFData.clear();
			NDEFcolumnWidth.clear();

//			NDEFcolumnNames.add(0,""); //to be replaced later
			NDEFcolumnWidth.add(100);

			NDEFcolumnNames.add("Description");
			NDEFcolumnWidth.add(200);

			switch (NfcApp.driver_.type()) {
			case ULTRALIGHT:
			case ULTRALIGHT_C:
			case ULTRALIGHT_EV1:
			case NTAG203:
			case NTAG213:
			case NTAG215:
			case NTAG216:
			case NTAG424:
				NDEFcolumnNames.add(0, "Page");
			default:
				NDEFcolumnNames.add(0, "Block");
			}

			int blockSize = NfcApp.driver_.blocksize();
			for (int i = 0; i < blockSize; i++) {
//				TableColumn tableColumn = new TableColumn(table_, SWT.NONE);
//				tableColumn.setWidth(50);
//				tableColumn.setAlignment(SWT.CENTER);
//				tableColumn.setText("" + i);
				NDEFcolumnNames.add("" + i);
				NDEFcolumnWidth.add(50);
			}

			DefaultTableModel model = new DefaultTableModel();
			for (String name : NDEFcolumnNames) {
				model.addColumn(name);
//	            System.out.println(name);
			}
//	        for (ArrayList<Object> row : NDEFData) {
//	            model.addRow(row.toArray());
//	        }

			boolean ndef = false;
//			// check if this tag is NDEF
//			// block zero should work fine
			data = NfcApp.driver_.read(0, false);
			if (data == null) {
				NfcApp.driver_.scan(1); // we must rescan card
				// try a ndef read
				data = NfcApp.driver_.ndefRead(0);
				if (data != null) {
					ndef = true;
					textNDEF_.setText("NDEF");
					textAuth_.setText("NDEF");
				} else {
					NfcApp.driver_.scan(1); // we must rescan card again
					textNDEF_.setText("PROTECTED");
					textAuth_.setText("UNKNOWN");
				}
			} else {
				textNDEF_.setText("STANDARD");
				textAuth_.setText("DEFAULT");
			}
//
//			// we will try to read the entire card regardless
			int blockCount = NfcApp.driver_.blockcount();
			ArrayList<String> rowData = new ArrayList<String>();
			int rowNum = 0;
			
			
			for (int i = 0; i < blockCount; i++) {
				rowData.add(String.valueOf(rowNum++));
//				rowData.add("test");

				if (i == 0) { // first block is readonly

////				item.setBackground(Display.getDefault().getSystemColor(
////						SWT.COLOR_GRAY));
//				item.setText(1, "Manufacturer Info");
					rowData.add("Manufacturer Info");
				} else {
					switch (NfcApp.driver_.type()) {
					case MIFARE_CLASSIC_1K:
//						break;
					case MIFARE_CLASSIC_4K:
						table_.setDefaultRenderer(Object.class, new CustomRenderer());
						if (i % 4 == 3) {
//						item.setBackground(Display.getDefault()
////								.getSystemColor(SWT.COLOR_YELLOW));
//						item.setText(1, "Auth Keys");
							rowData.add("Auth Keys");
						} else {
//							item.setText(1, "Data");
							rowData.add("Data");
						}
					default:
						break;
					}
				}
//			if (data != null) {
//				// System.out.println("Block:"+i+"\t"+NfcApp.driver_.toHex(data));
//				for (int j = 0; j < NfcApp.driver_.blocksize(); j++) {
//					String v = String.format("%02X", data[j]);
//					item.setText(j + 2, v);
//				}
//			} else {
//				for (int j = 0; j < NfcApp.driver_.blockcount(); j++) {
//					item.setText(j + 2, "-");
//				}
//			}

				if (ndef)
					data = NfcApp.driver_.ndefRead(i);
				else
					data = NfcApp.driver_.read(i, false);
				System.out.println(i);

				if (data != null) {
					// System.out.println("Block:"+i+"\t"+NfcApp.driver_.toHex(data));
					for (int j = 0; j < NfcApp.driver_.blocksize(); j++) {
						String v = String.format("%02X", data[j]);
//						item.setText(j + 2, v);
						rowData.add(v);
					}
				} else {
					for (int j = 0; j < NfcApp.driver_.blockcount(); j++) {
//						item.setText(j + 2, "-");
					}
				}
//				rowData.add(String.valueOf(rowNum)); // ROW NUMBER
//				if ((i + 1) % blockSize == 0) {
				// means next cell is on the next row first col
				// starts from 1
				model.addRow(rowData.toArray()); // Description
				rowData.clear();

//				}

//				TableItem item = new TableItem(table_, SWT.NONE);
//				item.setText(0, "" + i);
				NDEFData.add(null);
			}
			table_.setModel(model);
//			// table_.pack();
//			// this.getShell().pack();
//			switch (NfcApp.driver_.type()) {
//			case MIFARE_CLASSIC_1K:
//			case MIFARE_CLASSIC_4K:
//				createEditableTable(table_);
//				break;
//			case ULTRALIGHT:
//			case ULTRALIGHT_EV1:
//			case ULTRALIGHT_C:
//			case NTAG203:
//			case NTAG213:
//			case NTAG215:
//			case NTAG216:
//				createEditableTable(table_);
//				break;
//			default:
//				if (listener_ != null)
//					table_.removeListener(SWT.MouseDown, listener_);
//				break;
//			}
		} catch (Exception e) {
//			prompt(e.getMessage(), SWT.ICON_WARNING);
			e.printStackTrace();
		}
	}

	private boolean scan() {
		try {
			tabFolder.setSelectedIndex(0);
			NfcApp.driver_.keys(keydata_.KeyA, keydata_.KeyB); // set default
																// keys
			table_.removeAll();
			byte[] uid = NfcApp.driver_.scan(100);
			System.out.println("UID=" + NfcApp.driver_.toHex(uid));
			System.out.println("Type = " + NfcApp.driver_.type());
			uid_.setText(NfcApp.driver_.toHex(uid));
			name_.setText(NfcApp.driver_.name());
			textBlocks_.setText("" + NfcApp.driver_.blockcount());
			textBlockSize_.setText("" + NfcApp.driver_.blocksize());

			dump();

			J4209N.CardType t = NfcApp.driver_.type();
			switch (t) {
			case MIFARE_CLASSIC_1K:
			case MIFARE_CLASSIC_4K:
				btnAuth_.setEnabled(true);
				break;
			default:
				btnAuth_.setEnabled(false);
				break;
			}
			return true;
		} catch (Exception e) {
//			prompt(e.getMessage(), SWT.ICON_WARNING | SWT.OK);
		}
		return false;
	}

	private boolean connect() {
		try {
			NfcApp.driver_.open(comboPorts_.getSelectedItem().toString());
			return true;
		} catch (Exception e) {
//			prompt(e.getMessage()
//					+ " Could not connect to this port. Try another port.",
//					SWT.ICON_WARNING);
		}
		return false;
	}

	public NfcAppFrame() {
		// TODO Auto-generated constructor stub
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 63, 0 };
		gridBagLayout.rowHeights = new int[] { 50, 0, 342, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE };
		getContentPane().setLayout(gridBagLayout);

		JPanel head = new JPanel();
		head.setBorder(new EmptyBorder(10, 8, 8, 8));
		GridBagConstraints gbc_head = new GridBagConstraints();
		gbc_head.insets = new Insets(0, 0, 5, 0);
		gbc_head.fill = GridBagConstraints.HORIZONTAL;
		gbc_head.gridx = 0;
		gbc_head.gridy = 0;
		getContentPane().add(head, gbc_head);
		GridBagLayout gbl_head = new GridBagLayout();
		gbl_head.columnWidths = new int[] { 0, 155, 0, 0, 0, 0, 0, 0 };
		gbl_head.rowHeights = new int[] { 0, 0 };
		gbl_head.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE };
		gbl_head.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
		head.setLayout(gbl_head);

		JLabel lblPort = new JLabel("Port");
		lblPort.setIcon(new ImageIcon(NfcAppFrame.class.getResource("/jence/icon/usb16.png")));
		GridBagConstraints gbc_lblPort = new GridBagConstraints();
		gbc_lblPort.anchor = GridBagConstraints.EAST;
		gbc_lblPort.insets = new Insets(0, 0, 0, 5);
		gbc_lblPort.gridx = 0;
		gbc_lblPort.gridy = 0;
		head.add(lblPort, gbc_lblPort);

		comboPorts_ = new JComboBox();
		GridBagConstraints gbc_comboPorts_ = new GridBagConstraints();
		gbc_comboPorts_.insets = new Insets(0, 0, 0, 5);
		gbc_comboPorts_.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboPorts_.gridx = 1;
		gbc_comboPorts_.gridy = 0;
		head.add(comboPorts_, gbc_comboPorts_);

		btnRefresh = new JButton("Refresh");
		btnRefresh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if (portlist()) {
					btnConnect.setEnabled(true);
					status("Completed listing available ports.");
				}
			}
		});
		btnRefresh.setIcon(new ImageIcon(NfcAppFrame.class.getResource("/jence/icon/usb.png")));
		GridBagConstraints gbc_btnRefresh = new GridBagConstraints();
		gbc_btnRefresh.insets = new Insets(0, 0, 0, 5);
		gbc_btnRefresh.gridx = 2;
		gbc_btnRefresh.gridy = 0;
		head.add(btnRefresh, gbc_btnRefresh);

		btnConnect = new JButton("Connect");
		btnConnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if (connect()) {
					setEnabled(true, btnDisconnect, btnScan);
					setEnabled(false, btnRefresh, btnConnect);
					status("Connection was successful.");
				}

			}
		});
		btnConnect.setEnabled(false);
		btnConnect.setIcon(new ImageIcon(NfcAppFrame.class.getResource("/jence/icon/connect.png")));
		GridBagConstraints gbc_btnConnect = new GridBagConstraints();
		gbc_btnConnect.insets = new Insets(0, 0, 0, 5);
		gbc_btnConnect.gridx = 3;
		gbc_btnConnect.gridy = 0;
		head.add(btnConnect, gbc_btnConnect);

		btnDisconnect = new JButton("Disconnect");
		btnDisconnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if (disconnect()) {
					setEnabled(true, btnRefresh, btnConnect);
					setEnabled(false, btnDisconnect, btnScan);
					status("Disconnected.");
				}

			}
		});
		btnDisconnect.setIcon(new ImageIcon(NfcAppFrame.class.getResource("/jence/icon/disconnect.png")));
		btnDisconnect.setEnabled(false);
		GridBagConstraints gbc_btnDisconnect = new GridBagConstraints();
		gbc_btnDisconnect.insets = new Insets(0, 0, 0, 5);
		gbc_btnDisconnect.gridx = 4;
		gbc_btnDisconnect.gridy = 0;
		head.add(btnDisconnect, gbc_btnDisconnect);

		btnScan = new JButton("Scan");
		btnScan.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (scan()) {
					// setEnabled(true,);
					status("Scan completed.");
				}

			}
		});
		btnScan.setEnabled(false);
		btnScan.setIcon(new ImageIcon(NfcAppFrame.class.getResource("/jence/icon/scan.png")));
		GridBagConstraints gbc_btnScan = new GridBagConstraints();
		gbc_btnScan.insets = new Insets(0, 0, 0, 5);
		gbc_btnScan.gridx = 5;
		gbc_btnScan.gridy = 0;
		head.add(btnScan, gbc_btnScan);

		JPanel panelInfo = new JPanel();
		panelInfo.setBorder(new EmptyBorder(4, 8, 8, 8));
		GridBagConstraints gbc_panelInfo = new GridBagConstraints();
		gbc_panelInfo.insets = new Insets(0, 0, 5, 0);
		gbc_panelInfo.fill = GridBagConstraints.BOTH;
		gbc_panelInfo.gridx = 0;
		gbc_panelInfo.gridy = 1;
		getContentPane().add(panelInfo, gbc_panelInfo);
		GridBagLayout gbl_panelInfo = new GridBagLayout();
		gbl_panelInfo.columnWidths = new int[] { 0, 0, 0, 0, 0, 0, 0 };
		gbl_panelInfo.rowHeights = new int[] { 0, 0, 0 };
		gbl_panelInfo.columnWeights = new double[] { 0.0, 0.0, 0.0, 1.0, 0.0, 1.0, Double.MIN_VALUE };
		gbl_panelInfo.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		panelInfo.setLayout(gbl_panelInfo);

		JLabel lblNewLabel = new JLabel("UID");
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 0;
		panelInfo.add(lblNewLabel, gbc_lblNewLabel);

		uid_ = new JTextField();
		GridBagConstraints gbc_uid_ = new GridBagConstraints();
		gbc_uid_.insets = new Insets(0, 0, 5, 5);
		gbc_uid_.fill = GridBagConstraints.HORIZONTAL;
		gbc_uid_.gridx = 1;
		gbc_uid_.gridy = 0;
		panelInfo.add(uid_, gbc_uid_);
		uid_.setColumns(10);

		JLabel lblNewLabel_2 = new JLabel("Card Type");
		GridBagConstraints gbc_lblNewLabel_2 = new GridBagConstraints();
		gbc_lblNewLabel_2.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_2.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_2.gridx = 2;
		gbc_lblNewLabel_2.gridy = 0;
		panelInfo.add(lblNewLabel_2, gbc_lblNewLabel_2);

		name_ = new JTextField();
		GridBagConstraints gbc_name_ = new GridBagConstraints();
		gbc_name_.insets = new Insets(0, 0, 5, 5);
		gbc_name_.fill = GridBagConstraints.HORIZONTAL;
		gbc_name_.gridx = 3;
		gbc_name_.gridy = 0;
		panelInfo.add(name_, gbc_name_);
		name_.setColumns(10);

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
		tabFolderContainer.setBorder(new EmptyBorder(4, 8, 8, 8));
		GridBagConstraints gbc_tabFolderContainer = new GridBagConstraints();
		gbc_tabFolderContainer.insets = new Insets(0, 0, 5, 0);
		gbc_tabFolderContainer.fill = GridBagConstraints.BOTH;
		gbc_tabFolderContainer.gridx = 0;
		gbc_tabFolderContainer.gridy = 2;
		getContentPane().add(tabFolderContainer, gbc_tabFolderContainer);
		GridBagLayout gbl_tabFolderContainer = new GridBagLayout();
		gbl_tabFolderContainer.columnWidths = new int[] { 61, 0 };
		gbl_tabFolderContainer.rowHeights = new int[] { 70, 0 };
		gbl_tabFolderContainer.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_tabFolderContainer.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
		tabFolderContainer.setLayout(gbl_tabFolderContainer);

		tabFolder = new JTabbedPane(JTabbedPane.TOP);
		GridBagConstraints gbc_tabFolder = new GridBagConstraints();
		gbc_tabFolder.fill = GridBagConstraints.BOTH;
		gbc_tabFolder.gridx = 0;
		gbc_tabFolder.gridy = 0;
		tabFolderContainer.add(tabFolder, gbc_tabFolder);

		JPanel panel = new JPanel();
		tabFolder.addTab("RAW", null, panel, null);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[] { 763, 0 };
		gbl_panel.rowHeights = new int[] { 64, 10, 0 };
		gbl_panel.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_panel.rowWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		panel.setLayout(gbl_panel);

		JPanel panel_3 = new JPanel();
		GridBagConstraints gbc_panel_3 = new GridBagConstraints();
		gbc_panel_3.anchor = GridBagConstraints.WEST;
		gbc_panel_3.insets = new Insets(0, 0, 5, 0);
		gbc_panel_3.gridx = 0;
		gbc_panel_3.gridy = 0;
		panel.add(panel_3, gbc_panel_3);

		JButton btnNewButton = new JButton("Raw Write");
		btnNewButton.setIcon(new ImageIcon(NfcAppFrame.class.getResource("/jence/icon/cardwrite.png")));
		panel_3.add(btnNewButton);

		btnAuth_ = new JButton("Auth");
		btnAuth_.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				authDialog = new AuthFrame(NfcAppFrame.this);
				authDialog.setVisible(true);
			}
		});
		btnAuth_.setIcon(new ImageIcon(NfcAppFrame.class.getResource("/jence/icon/key.png")));
		panel_3.add(btnAuth_);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBackground(new Color(240, 240, 240));
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 1;
		panel.add(scrollPane, gbc_scrollPane);

//		JTable table_ = new JTable();
//		inventory_.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

//		inventory_.addMouseListener(new MouseAdapter() {
//			@Override
//			public void mouseClicked(MouseEvent e) {
//				if (e.getClickCount() == 2) { // Check for double-click
//					getMemoryDetail();
//				}
//			}
//		});
//		table_.setBorder(new MatteBorder(0, 0, 0, 0, (Color) UIManager.getColor("Button.light")));
//		table.setAutoCreateRowSorter(true); // no idea why
//		model = new DefaultTableModel(new Object[][] {},
//				new String[] { "Index", "EPC", "LEN", "ANT", "Times", "RSSI", "" }) {
//			Class[] columnTypes = new Class[] { Object.class, Object.class, Object.class, Object.class, Object.class,
//					Object.class, String.class };
//
//			public Class getColumnClass(int columnIndex) {
//				return columnTypes[columnIndex];
//			}
//
//			boolean[] columnEditables = new boolean[] { false, false, false, false, false, false, false };
//
//			public boolean isCellEditable(int row, int column) {
//				return columnEditables[column];
//			}
//		};
//
//		inventory_.setModel(model);
//		inventory_.getColumnModel().getColumn(5).setResizable(false);
//
//		inventory_.getColumnModel().getColumn(0).setPreferredWidth(40);
//		inventory_.getColumnModel().getColumn(1).setPreferredWidth(250);
//		inventory_.getColumnModel().getColumn(2).setPreferredWidth(40);
//		inventory_.getColumnModel().getColumn(3).setPreferredWidth(40);
//		inventory_.getColumnModel().getColumn(4).setPreferredWidth(40);
//		inventory_.getColumnModel().getColumn(5).setPreferredWidth(40);
//		inventory_.getColumnModel().getColumn(6).setPreferredWidth(40);
//
//		inventory_.getColumnModel().getColumn(0).setMaxWidth(100);
//		inventory_.getColumnModel().getColumn(1).setMaxWidth(350);
//		inventory_.getColumnModel().getColumn(2).setMaxWidth(150);
//		inventory_.getColumnModel().getColumn(3).setMaxWidth(150);
//		inventory_.getColumnModel().getColumn(4).setMaxWidth(150);
//		inventory_.getColumnModel().getColumn(5).setMaxWidth(150);
//		inventory_.getColumnModel().getColumn(6).setMaxWidth(850);
		// TODO: increase column width

		// Set the horizontal alignment to center
//		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
//		centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
//		inventory_.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
//		inventory_.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
//		inventory_.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
//		inventory_.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
//		inventory_.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);

//		inventory_.setEnabled(false);
//		inventory_.getTableHeader().setReorderingAllowed(false);
////        TableRowSorter<TableModel> sorter = new TableRowSorter<>(model);
//		inventory_.setRowSorter(null);

		table_ = new JTable(new DefaultTableModel(new Object[][] {}, new String[] {}));
		table_.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		scrollPane.setViewportView(table_);

		JPanel panel_1 = new JPanel();
		tabFolder.addTab("NDEF", null, panel_1, null);
		GridBagLayout gbl_panel_1 = new GridBagLayout();
		gbl_panel_1.columnWidths = new int[] { 0, 0 };
		gbl_panel_1.rowHeights = new int[] { 86, 0, 0 };
		gbl_panel_1.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_panel_1.rowWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		panel_1.setLayout(gbl_panel_1);

		JPanel panel_4 = new JPanel();
		GridBagConstraints gbc_panel_4 = new GridBagConstraints();
		gbc_panel_4.anchor = GridBagConstraints.NORTHWEST;
		gbc_panel_4.insets = new Insets(0, 0, 5, 0);
		gbc_panel_4.gridx = 0;
		gbc_panel_4.gridy = 0;
		panel_1.add(panel_4, gbc_panel_4);

		JPanel panel_5 = new JPanel();
		panel_5.setBorder(new TitledBorder(null, "NDEF", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_4.add(panel_5);

		JButton btnNewButton_2 = new JButton("Format");
		btnNewButton_2.setMargin(new Insets(2, 8, 2, 8));
		btnNewButton_2.setIcon(new ImageIcon(NfcAppFrame.class.getResource("/jence/icon/format.png")));
		panel_5.add(btnNewButton_2);

		JButton btnNewButton_3 = new JButton("Clean");
		btnNewButton_3.setMargin(new Insets(2, 8, 2, 8));
		btnNewButton_3.setIcon(new ImageIcon(NfcAppFrame.class.getResource("/jence/icon/clean.png")));
		panel_5.add(btnNewButton_3);

		JButton btnNewButton_4 = new JButton("Write");
		btnNewButton_4.setMargin(new Insets(2, 8, 2, 8));
		btnNewButton_4.setIcon(new ImageIcon(NfcAppFrame.class.getResource("/jence/icon/write.png")));
		panel_5.add(btnNewButton_4);

		JButton btnNewButton_5 = new JButton("Read");
		btnNewButton_5.setMargin(new Insets(2, 8, 2, 8));
		btnNewButton_5.setIcon(new ImageIcon(NfcAppFrame.class.getResource("/jence/icon/read.png")));
		panel_5.add(btnNewButton_5);

		JButton btnNewButton_6 = new JButton("Save");
		btnNewButton_6.setMargin(new Insets(2, 8, 2, 8));
		btnNewButton_6.setIcon(new ImageIcon(NfcAppFrame.class.getResource("/jence/icon/save.png")));
		panel_5.add(btnNewButton_6);

		JButton btnNewButton_7 = new JButton("Erase");
		btnNewButton_7.setMargin(new Insets(2, 8, 2, 8));
		btnNewButton_7.setIcon(new ImageIcon(NfcAppFrame.class.getResource("/jence/icon/erase.png")));
		panel_5.add(btnNewButton_7);

		JScrollPane scrollPane_1 = new JScrollPane();
		GridBagConstraints gbc_scrollPane_1 = new GridBagConstraints();
		gbc_scrollPane_1.fill = GridBagConstraints.BOTH;
		gbc_scrollPane_1.gridx = 0;
		gbc_scrollPane_1.gridy = 1;
		panel_1.add(scrollPane_1, gbc_scrollPane_1);

		table_1 = new JTable();
		scrollPane_1.setViewportView(table_1);

		JPanel panel_2 = new JPanel();
		panel_2.setBorder(new EmptyBorder(8, 8, 0, 8));
		tabFolder.addTab("EMULATE", null, panel_2, null);
		GridBagLayout gbl_panel_2 = new GridBagLayout();
		gbl_panel_2.columnWidths = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		gbl_panel_2.rowHeights = new int[] { 0, 0, 0 };
		gbl_panel_2.columnWeights = new double[] { 0.0, 1.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE };
		gbl_panel_2.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		panel_2.setLayout(gbl_panel_2);

		JLabel lblNewLabel_6 = new JLabel("UID");
		GridBagConstraints gbc_lblNewLabel_6 = new GridBagConstraints();
		gbc_lblNewLabel_6.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_6.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_6.gridx = 0;
		gbc_lblNewLabel_6.gridy = 0;
		panel_2.add(lblNewLabel_6, gbc_lblNewLabel_6);

		textField = new JTextField();
		GridBagConstraints gbc_textField = new GridBagConstraints();
		gbc_textField.insets = new Insets(0, 0, 5, 5);
		gbc_textField.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField.gridx = 1;
		gbc_textField.gridy = 0;
		panel_2.add(textField, gbc_textField);
		textField.setColumns(10);

		JLabel lblNewLabel_7 = new JLabel("Timeout (sec)");
		GridBagConstraints gbc_lblNewLabel_7 = new GridBagConstraints();
		gbc_lblNewLabel_7.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_7.gridx = 2;
		gbc_lblNewLabel_7.gridy = 0;
		panel_2.add(lblNewLabel_7, gbc_lblNewLabel_7);

		JSpinner spinner = new JSpinner();
		GridBagConstraints gbc_spinner = new GridBagConstraints();
		gbc_spinner.insets = new Insets(0, 0, 5, 5);
		gbc_spinner.gridx = 3;
		gbc_spinner.gridy = 0;
		panel_2.add(spinner, gbc_spinner);

		JCheckBox chckbxNewCheckBox = new JCheckBox("Writable");
		GridBagConstraints gbc_chckbxNewCheckBox = new GridBagConstraints();
		gbc_chckbxNewCheckBox.insets = new Insets(0, 0, 5, 5);
		gbc_chckbxNewCheckBox.gridx = 4;
		gbc_chckbxNewCheckBox.gridy = 0;
		panel_2.add(chckbxNewCheckBox, gbc_chckbxNewCheckBox);

		JButton btnNewButton_8 = new JButton("Apply");
		btnNewButton_8.setMargin(new Insets(2, 8, 2, 8));
		btnNewButton_8.setIcon(new ImageIcon(NfcAppFrame.class.getResource("/jence/icon/cardwrite.png")));
		GridBagConstraints gbc_btnNewButton_8 = new GridBagConstraints();
		gbc_btnNewButton_8.anchor = GridBagConstraints.EAST;
		gbc_btnNewButton_8.insets = new Insets(0, 0, 5, 5);
		gbc_btnNewButton_8.gridx = 5;
		gbc_btnNewButton_8.gridy = 0;
		panel_2.add(btnNewButton_8, gbc_btnNewButton_8);

		JButton btnNewButton_9 = new JButton("Start");
		btnNewButton_9.setMargin(new Insets(2, 8, 2, 8));
		btnNewButton_9.setIcon(new ImageIcon(NfcAppFrame.class.getResource("/jence/icon/emulate.png")));
		GridBagConstraints gbc_btnNewButton_9 = new GridBagConstraints();
		gbc_btnNewButton_9.insets = new Insets(0, 0, 5, 5);
		gbc_btnNewButton_9.gridx = 6;
		gbc_btnNewButton_9.gridy = 0;
		panel_2.add(btnNewButton_9, gbc_btnNewButton_9);

		JButton btnNewButton_10 = new JButton("Stop");
		btnNewButton_10.setMargin(new Insets(2, 8, 2, 8));
		btnNewButton_10.setIcon(new ImageIcon(NfcAppFrame.class.getResource("/jence/icon/stop.png")));
		GridBagConstraints gbc_btnNewButton_10 = new GridBagConstraints();
		gbc_btnNewButton_10.insets = new Insets(0, 0, 5, 0);
		gbc_btnNewButton_10.gridx = 7;
		gbc_btnNewButton_10.gridy = 0;
		panel_2.add(btnNewButton_10, gbc_btnNewButton_10);

		JLabel lblNewLabel_8 = new JLabel("Time");
		GridBagConstraints gbc_lblNewLabel_8 = new GridBagConstraints();
		gbc_lblNewLabel_8.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_8.insets = new Insets(0, 0, 0, 5);
		gbc_lblNewLabel_8.gridx = 0;
		gbc_lblNewLabel_8.gridy = 1;
		panel_2.add(lblNewLabel_8, gbc_lblNewLabel_8);

		textField_1 = new JTextField();
		GridBagConstraints gbc_textField_1 = new GridBagConstraints();
		gbc_textField_1.insets = new Insets(0, 0, 0, 5);
		gbc_textField_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_1.gridx = 1;
		gbc_textField_1.gridy = 1;
		panel_2.add(textField_1, gbc_textField_1);
		textField_1.setColumns(10);

		JPanel panelFooter = new JPanel();
		panelFooter.setBorder(new EmptyBorder(0, 8, 4, 8));
		GridBagConstraints gbc_panelFooter = new GridBagConstraints();
		gbc_panelFooter.fill = GridBagConstraints.BOTH;
		gbc_panelFooter.gridx = 0;
		gbc_panelFooter.gridy = 3;
		getContentPane().add(panelFooter, gbc_panelFooter);
		GridBagLayout gbl_panelFooter = new GridBagLayout();
		gbl_panelFooter.columnWidths = new int[] { 384, 1, 0 };
		gbl_panelFooter.rowHeights = new int[] { 42, 0 };
		gbl_panelFooter.columnWeights = new double[] { 1.0, 1.0, Double.MIN_VALUE };
		gbl_panelFooter.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
		panelFooter.setLayout(gbl_panelFooter);

		JPanel panelStatus = new JPanel();
		GridBagConstraints gbc_panelStatus = new GridBagConstraints();
		gbc_panelStatus.anchor = GridBagConstraints.WEST;
		gbc_panelStatus.insets = new Insets(0, 0, 0, 5);
		gbc_panelStatus.gridx = 0;
		gbc_panelStatus.gridy = 0;
		panelFooter.add(panelStatus, gbc_panelStatus);

		status_ = new JLabel("");
		status_.setBorder(null);
		panelStatus.add(status_);

		JPanel panel_7 = new JPanel();
		GridBagConstraints gbc_panel_7 = new GridBagConstraints();
		gbc_panel_7.anchor = GridBagConstraints.EAST;
		gbc_panel_7.gridx = 1;
		gbc_panel_7.gridy = 0;
		panelFooter.add(panel_7, gbc_panel_7);

		JLabel lblVersion = new JLabel();
		lblVersion
				.setText("Library Version : " + NfcApp.driver_.LibraryVersion() + " | " + "Version: " + NfcApp.VERSION);
		panel_7.add(lblVersion);

		this.setSize(798, 565);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);

	}

}


class CustomRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        // Check if the row number is divisible by 3
        if ((row ) % 3 == 0 && row !=0 ) {
            cell.setBackground(Color.YELLOW); // Change background color for rows divisible by 3
        }else if(row ==0){
        	cell.setBackground(Color.LIGHT_GRAY); // Change background color for rows divisible by 3

        }
        
        else {
            cell.setBackground(Color.WHITE); // Reset background color for other rows
        }

        return cell;
    }
}

