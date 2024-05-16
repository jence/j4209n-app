package jence.swing.app;

import javax.swing.JFrame;
import java.awt.GridBagLayout;
import javax.swing.JPanel;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Panel;
import java.awt.Toolkit;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JButton;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.management.modelmbean.ModelMBean;
import javax.print.attribute.AttributeSet;
import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.DocumentFilter.FilterBypass;
import javax.xml.crypto.Data;

import jence.jni.J4209N;
import jence.jni.Vcard;
import jence.swing.app.NfcApp.MessageType;
import jence.swt.app.Callback;

import javax.swing.JSpinner;
import javax.swing.JCheckBox;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.ref.Cleaner.Cleanable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;
import java.awt.event.ActionEvent;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpinnerNumberModel;
import javax.swing.JLayeredPane;
import java.awt.GridLayout;
import javax.swing.JScrollBar;
import javax.swing.JProgressBar;

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
	private JButton btnRawWrite_;

	private JTabbedPane tabFolder;
	private JPanel rawPanel_;
	private JPanel ndefPanel_;
	private JPanel emulatePanel_;
	private JLayeredPane tabFolderContainer_;
	private TransparentPanel loadingPanel_;
	private JLabel lblLoadingText_;
	private JProgressBar progressBar_;
	public static int progressValue_ = 0;

	private JTable table_;
	private DefaultTableModel NDEFModel = new DefaultTableModel();
	private DefaultTableModel NDEFFormattedModel = new DefaultTableModel();

	private LinkedList<String> NDEFcolumnNames = new LinkedList<String>();
	private ArrayList<Integer> NDEFcolumnWidth = new ArrayList<Integer>();
	public static ArrayList<Tuple<Integer, Integer>> editedValue = new ArrayList<>();

	private ArrayList<ArrayList<String>> NDEFData = new ArrayList<ArrayList<String>>();

	private JButton btnAuth_;
	private JCheckBox btnWritable_;
	private JTextField uid_;
	private JTextField textAuth_;
	private JTextField name_;
	private JTextField textNDEF_;
	private JTextField textBlocks_;
	private JTextField textBlockSize_;
	private JTable ndeftable_;
	private JTextField emulationUid_;
	public JTextField elapsed_;
	private JSpinner timeout_;
	public boolean emulate_ = false;

	private static JLabel status_;

	private JDialog authDialog;
	private JDialog NDEFDialog;

	private J4209N.KeyData keydata_ = new J4209N.KeyData(0); // full access
	private Timer timer_ = null;
	public static boolean scanError = false;

	public static <T extends JComponent> void setEnabled(boolean enabled, T... components) {
		for (T component : components) {
			component.setEnabled(enabled);
		}
	}

	private void setComponentsEnabled(boolean isEnabled, Component... components) {
		for (Component component : components) {
			setComponentEnabled(component, isEnabled);
		}
	}

	private void setComponentEnabled(Component component, boolean isEnabled) {
		component.setEnabled(isEnabled);
		if (component instanceof Container) {
			Component[] childComponents = ((Container) component).getComponents();
			for (Component childComponent : childComponents) {
				setComponentEnabled(childComponent, isEnabled);
			}
		}
	}

	private boolean portlist() {
		try {
			String[] ports = NfcApp.driver_.listPorts();
			comboPorts_.removeAllItems();
			status(ports.length + " Ports Found. " + " Complete Listing Available Ports.");
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
			NfcApp.prompt(NfcAppFrame.this, e.getMessage() + " Please check if the device is attached to an USB port.",
					"Warning", MessageType.WARNING);

		}
		return false;
	}

	private boolean disconnect() {
		try {
			NfcApp.driver_.close();
			return true;
		} catch (Exception e) {
			NfcApp.prompt(NfcAppFrame.this, e.getLocalizedMessage(), "Error", MessageType.ERROR);
		}
		return false;
	}

	public static void status(String text) {
		status_.setText(text);
	}

	private void initEmulation() {
		String uid = emulationUid_.getText();
		uid = "000000" + uid;
		uid = uid.substring(uid.length() - 6);
		emulationUid_.setText(uid);
		BigInteger bi = new BigInteger(uid, 16);
		int uidlen = bi.toByteArray().length;
		if (uidlen != 3) {
			NfcApp.prompt(NfcAppFrame.this,
					"Change the UID value so that the first byte is non zero. The total UID size must be 3 bytes.",
					"Warning", MessageType.WARNING);
			return;
		}
		try {
			NfcApp.driver_.emulateInit(bi.toByteArray(), btnWritable_.isSelected());
		} catch (Exception e) {
			NfcApp.prompt(NfcAppFrame.this, e.getLocalizedMessage(), "Error", MessageType.ERROR);
		}
	}

	private void stopEmulation() {
		try {
			NfcApp.driver_.emulateStop();
			timer_ = null;
			emulate_ = false;
		} catch (Exception e) {
			NfcApp.prompt(NfcAppFrame.this, e.getLocalizedMessage(), "Error", MessageType.ERROR);
		}
	}

	private void emulationResponse(int index, String msg) {
		if (index == 1) {
			NfcApp.prompt(NfcAppFrame.this, "Received NDEF write in Card Emulation.", "Response",
					MessageType.INFORMATION);
		}
		timer_.cancel();
		timer_ = null;
//		emulate_ = false; // TODO: should be enabled but not in swt
	}

	private void startEmulation() {
		try {
			timer_ = new Timer();
			final int seconds[] = { 0 };
			emulate_ = true;
			EmulationWorker worker = new EmulationWorker(this);
			worker.execute();

			int timeouts = Integer.parseInt(timeout_.getValue().toString());

			NfcApp.driver_.emulateStart(timeouts * 1000, new Callback() {

				@Override
				public void callback(int option, int index, String text) throws Exception {
					emulationResponse(index, text);
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
			NfcApp.prompt(NfcAppFrame.this, e.getLocalizedMessage(), "Error", MessageType.ERROR);
		}
	}

	private boolean readNDEF() {
		try {
			NDEFFormattedModel.setRowCount(0);
			ndeftable_.removeAll();
			NfcApp.driver_.sync();
			if (!NfcApp.driver_.isNDEF()) {
				NfcApp.prompt(this, "No NDEF record found or the card may not be NDEF formatted.", "Warning",
						MessageType.WARNING);
				return false;
			}
			int records = NfcApp.driver_.ndefRead();
			if (records > 0) {
				ndeftable_.removeAll();
				NDEFFormattedModel.setRowCount(0);
				for (int i = 0; i < records; i++) {
					J4209N.NdefRecord ndef = NfcApp.driver_.ndefGetRecord(i);
					String[] rowValue = { String.valueOf(i), ndef.id, ndef.type, ndef.encoding,
							new String(ndef.payload, "UTF-8") };
					NDEFFormattedModel.addRow(rowValue);
				}
				return true;
			} else {
				NfcApp.prompt(NfcAppFrame.this, "No NDEF records found", "Warning", MessageType.WARNING);
				return false;
			}
		} catch (Exception e) {
			NfcApp.prompt(NfcAppFrame.this,
					e.getLocalizedMessage() + " Please check if the device is attached to an USB port.", "Error",
					MessageType.ERROR);

		}
		return false;
	}

	private boolean ndefFormat() {
		try {
			if (!NfcApp.prompt(this, "All data in the current card will be erased. Are you sure?", "Confirmation",
					NfcApp.MessageType.CONFIRMATION)) {
				return false;
			}
			NfcApp.driver_.ndefFormat();
			NDEFFormattedModel.setRowCount(0);
			return true;
		} catch (Exception e) {
			NfcApp.prompt(this, e.getMessage(), "Error", MessageType.ERROR);
		}
		return false;
	}

	private boolean ndefErase() {
		try {
			if (!NfcApp.driver_.isNDEF()) {
				NfcApp.prompt(this, "The tag is not NDEF formatted.", "Warning", MessageType.WARNING);
				return false;
			}
			if (!NfcApp.prompt(this, "This operation erase all NDEF records. Do you want to proceed?", "Confirmation",
					MessageType.CONFIRMATION)) {
				return false;
			}
			NfcApp.driver_.ndefErase();
		} catch (Exception e) {
			NfcApp.prompt(this, e.getMessage(), "Error", MessageType.ERROR);
		}
		return false;
	}

	private boolean clean() {
		try {
			if (!NfcApp.prompt(NfcAppFrame.this,
					"This operation will reset all the data in the card. Do you want to proceed?", "Confirmation",
					MessageType.CONFIRMATION)) {
				return false;
			}
			NfcApp.driver_.format();
			ndeftable_.removeAll();
			NfcApp.prompt(NfcAppFrame.this,
					"Clean operation completed. Verify by rescanning the card/tag.? Most of the memory content will be zero. "
							+ "If some portion of the memory is not set to zero, try again.",
					"Information", MessageType.INFORMATION);
			status("Memory Cleaned");
		} catch (Exception e) {
			NfcApp.prompt(NfcAppFrame.this, e.getLocalizedMessage(), "Error", MessageType.ERROR);
		}
		return false;
	}

	private void rawWrite() {
		byte[] data;
		try {
			data = new byte[NfcApp.driver_.blocksize()];
			DefaultTableModel model = (DefaultTableModel) table_.getModel();
			for (Tuple<Integer, Integer> cell : editedValue) {
//				System.out.println("Row: " + cell.getRow() + ", Col: " + cell.getCol());
				for (int i = 2; i < data.length; i++) {
					String cellData = model.getValueAt(cell.getRow(), i).toString();
					data[i - 2] = (byte) Integer.parseInt(cellData, 16);
				}
				byte[] vdata;
				switch (NfcApp.driver_.type()) {
				case MIFARE_CLASSIC_1K:
				case MIFARE_CLASSIC_4K:
					// a sync operation is need to start from a known state
					NfcApp.driver_.sync();
					int startBlock = (cell.getRow() / 4) * 4;
					vdata = NfcApp.driver_.read(startBlock, false); // we
																	// have
																	// to
																	// read
																	// start
																	// block
					break;
				}
				boolean success = NfcApp.driver_.write(cell.getRow(), data, false); // write
																					// new
																					// data
				if (!success) {
					throw new Exception("Failed to write into block " + cell.getRow() + " using Key A");
				}

				vdata = NfcApp.driver_.read(cell.getRow(), false); // read same block to
																	// verify
				if (vdata == null) {
					throw new Exception("Failed to read block " + cell.getRow() + ". Read operation failed.");
				}
				boolean equal = Arrays.equals(data, vdata);
				if (!equal) {
					BigInteger a = new BigInteger(data);
					BigInteger b = new BigInteger(vdata);
					throw new Exception("Failed to write data into block " + cell.getRow() + " (Sector = "
							+ (cell.getRow() / 4) + ". Attempted to write data " + a.toString(16)
							+ " (hex) but instead found " + b.toString(16) + " (hex).");
				}

			}
//			 All writes were successful
			status("Write Successful......");
			editedValue.clear();
			table_.repaint();
		} catch (Exception e) {
			NfcApp.prompt(NfcAppFrame.this, e.getLocalizedMessage(), "Error", MessageType.ERROR);

		}
	}

	private void openNdefWriteDialog() {
		NDEFDialog = new NDEFDialog(NfcAppFrame.this);

		NDEFDialog.setVisible(true);
		NDEFDialog.setSize(200, 400);

	}

	public void saveToFile() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Save");
		fileChooser.setFileFilter(new FileNameExtensionFilter("NDEF Files", "ndef"));
		fileChooser.setAcceptAllFileFilterUsed(false);

		int returnValue = fileChooser.showSaveDialog(null);
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			File selectedFile = fileChooser.getSelectedFile();
			File file = selectedFile.getName().endsWith(".ndef") ? selectedFile
					: new File(selectedFile.getAbsolutePath() + ".ndef");
			if (file.exists()) {
				int choice = JOptionPane.showConfirmDialog(null, "File already exists. Do you want to overwrite it?",
						"Confirmation", JOptionPane.YES_NO_OPTION);
				if (choice != JOptionPane.YES_OPTION) {
					return; // User chose not to overwrite, so return
				}
			}
			DefaultTableModel model = (DefaultTableModel) table_.getModel();
			try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
				for (int i = 0; i < model.getRowCount(); i++) {
					for (int j = 2; j < model.getColumnCount(); j++) {
						Object value = model.getValueAt(i, j);
						if (value != null) {
							writer.write(value.toString());
							writer.write(' ');
							System.out.print(value + " ");
						}
					}
					System.out.println();
					writer.newLine();
				}
			} catch (IOException e) {
				e.printStackTrace();
				NfcApp.prompt(NfcAppFrame.this, e.getLocalizedMessage(), "Error", MessageType.ERROR);
			}
		}
	}

	private void dump() {
		editedValue.clear();
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
//				NDEFcolumnNames.add(0, "Page");
			default:
				NDEFcolumnNames.add(0, "Block");
			}

			int blockSize = NfcApp.driver_.blocksize();
			for (int i = 0; i < blockSize; i++) {
				NDEFcolumnNames.add("" + i);
				NDEFcolumnWidth.add(50);
				NfcAppFrame.progressValue_ = (i + 3 / blockSize) * 100;
			}

			DefaultTableModel model = new DefaultTableModel() {
				@Override
				public boolean isCellEditable(int row, int column) {

					boolean writable = false;
					try {
						switch (NfcApp.driver_.type()) {
						case MIFARE_CLASSIC_1K:
						case MIFARE_CLASSIC_4K:
							writable = true;
							break;
						default:
							writable = false;
							break;
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (writable) {
						// 0th col, 0th row, 1st row, sector trailer
						return column != 0 && column != 1 && row != 0 && ((row + 1) % 4) != 0;

					} else {
						return false;
					}
				}

			};
			model.addTableModelListener(new TableModelListener() {
				@Override
				public void tableChanged(TableModelEvent e) {
					int row = e.getFirstRow();
					int column = e.getColumn();
					if (row >= 0 && column >= 0) {
//						Object oldValue = model.getValueAt(row, column);
//						Object newValue = model.getValueAt(row, column);
//						System.out.println("Cell changed at row " + row + ", column " + column + ". Old value: "
//								+ oldValue + ", New value: " + newValue);
						editedValue.add(new Tuple<Integer, Integer>(row, column));
					}
				}
			});

			for (String name : NDEFcolumnNames) {
				model.addColumn(name);
//	            System.out.println(name);
			}

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

// we will try to read the entire card regardless
			int blockCount = NfcApp.driver_.blockcount();
			ArrayList<String> rowData = new ArrayList<String>();
			int rowNum = 0;

// setting cell renderer for different tag
			switch (NfcApp.driver_.type()) {
			case MIFARE_CLASSIC_1K:
//				break;
			case MIFARE_CLASSIC_4K:
				table_.setDefaultRenderer(Object.class, new CustomRenderer());
				break;
			default:
				DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
				table_.setDefaultRenderer(Object.class, renderer);
				break;
			}

			for (int i = 0; i < blockCount; i++) {
				rowData.add(String.valueOf(rowNum++));
				if (i == 0) { // first block is readonly
					rowData.add("Manufacturer Info");
				} else {
					switch (NfcApp.driver_.type()) {
					case MIFARE_CLASSIC_1K:
//						break;
					case MIFARE_CLASSIC_4K:
						if (i % 4 == 3) {
							rowData.add("Auth Keys");
						} else {
							rowData.add("Data");
						}
						break;
					default:
						rowData.add("-");
						break;
					}
				}

				if (ndef)
					data = NfcApp.driver_.ndefRead(i);
				else
					data = NfcApp.driver_.read(i, false);
//				System.out.println(i);

				if (data != null) {
					// System.out.println("Block:"+i+"\t"+NfcApp.driver_.toHex(data));
					for (int j = 0; j < NfcApp.driver_.blocksize(); j++) {
						String v = String.format("%02X", data[j]);
						rowData.add(v);
					}
				} else {
					for (int j = 0; j < NfcApp.driver_.blockcount(); j++) {
						rowData.add("-");
					}
				}
				model.addRow(rowData.toArray()); // Description
				rowData.clear();
				NDEFData.add(null);
			}
			table_.setModel(model);

			int i = 0;
			for (Integer width : NDEFcolumnWidth) {
				table_.getColumnModel().getColumn(i++).setPreferredWidth(width);
			}

			switch (NfcApp.driver_.type()) {
			case MIFARE_CLASSIC_1K:
			case MIFARE_CLASSIC_4K:
//				createEditableTable(true);
				table_.setEnabled(true);
				for (int columnIndex = 0; columnIndex < table_.getColumnCount(); columnIndex++) {
					// Set the LimitedCharacterCellEditor for each column
					table_.getColumnModel().getColumn(columnIndex)
							.setCellEditor(new LimitedCharacterCellEditor(2, "0123456789ABCDEFabcdef"));
				}

				break;
			case ULTRALIGHT:
				table_.setEnabled(false);
				break;
			case ULTRALIGHT_EV1:
				table_.setEnabled(false);
				break;
			case ULTRALIGHT_C:
				table_.setEnabled(false);
				break;
			case NTAG203:
				table_.setEnabled(false);
				break;
			case NTAG213:
				table_.setEnabled(false);
				break;
			case NTAG215:
				table_.setEnabled(false);
				break;
			case NTAG216:
				table_.setEnabled(true);
				for (int columnIndex = 0; columnIndex < table_.getColumnCount(); columnIndex++) {
					// Set the LimitedCharacterCellEditor for each column
					table_.getColumnModel().getColumn(columnIndex)
							.setCellEditor(new LimitedCharacterCellEditor(4, "0123456789ABCDEFabcdef"));
				}

				break;
			default:
				table_.setEnabled(false);
				break;
			}
		} catch (Exception e) {
			NfcApp.prompt(NfcAppFrame.this, e.getLocalizedMessage(), "Error", MessageType.ERROR);
		}
	}

	private boolean scan() {
		// Show loading panel immediately
		loadingPanel_.setVisible(true);
		table_.setVisible(false);
		setComponentsEnabled(false, rawPanel_, emulatePanel_, ndefPanel_);

		new Thread(new Runnable() {
			public void run() {
				try {
					tabFolder.setSelectedIndex(0);
					NfcApp.driver_.keys(keydata_.KeyA, keydata_.KeyB); // set default keys
					table_.removeAll();
					byte[] uid = NfcApp.driver_.scan(100);
					System.out.println("UID=" + NfcApp.driver_.toHex(uid));
					System.out.println("Type = " + NfcApp.driver_.type());
					uid_.setText(NfcApp.driver_.toHex(uid));
					name_.setText(NfcApp.driver_.name());
					textBlocks_.setText("" + NfcApp.driver_.blockcount());
					textBlockSize_.setText("" + NfcApp.driver_.blocksize());

					dump();

				} catch (Exception e) {
					NfcApp.prompt(NfcAppFrame.this, e.getMessage(), "Error", MessageType.ERROR);
					NfcAppFrame.scanError = true;
				} finally {
					// Hide loading panel after completion
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							loadingPanel_.setVisible(false);
							table_.setVisible(true);
							setComponentsEnabled(true, rawPanel_, emulatePanel_, ndefPanel_);

							try {
								switch (NfcApp.driver_.type()) {
								case MIFARE_CLASSIC_1K:
								case MIFARE_CLASSIC_4K:
									break;
								default:
									btnAuth_.setEnabled(false);
									btnRawWrite_.setEnabled(false);
									break;
								}
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}
					});

					if (!NfcAppFrame.scanError) {
						SwingUtilities.invokeLater(new Runnable() {

							@Override
							public void run() {
								setComponentsEnabled(true, rawPanel_, ndefPanel_, emulatePanel_);
								status("Scan completed.");
							}
						});

					} else {
						SwingUtilities.invokeLater(new Runnable() {

							@Override
							public void run() {
								setComponentsEnabled(false, rawPanel_, ndefPanel_, emulatePanel_);
								NfcAppFrame.scanError = false;
							}
						});

					}

				}
			}
		}).start();

		return true; // Assuming true is returned on success
	}

	private boolean connect() {
		try {
			NfcApp.driver_.open(comboPorts_.getSelectedItem().toString());
			return true;
		} catch (Exception e) {
			NfcApp.prompt(NfcAppFrame.this, e.getMessage() + " Could not connect to this port. Try another port.",
					"Warning", MessageType.WARNING);
		}
		return false;
	}

	public NfcAppFrame() {
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
					setEnabled(false, btnRefresh, btnConnect, btnAuth_, btnRawWrite_);
					if (!NfcAppFrame.scanError) {
						setComponentsEnabled(true, rawPanel_, emulatePanel_, ndefPanel_);
					}

					status("Connection was successful.");
				} else {
					status("Could not connect to the port, Try again.");
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
					setEnabled(false, btnDisconnect, btnScan, btnAuth_, btnRawWrite_);
					setComponentsEnabled(false, rawPanel_, emulatePanel_, ndefPanel_);
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
				scan();

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
		uid_.setBackground(new Color(255, 255, 255));
		uid_.setEditable(false);
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
		name_.setBackground(new Color(255, 255, 255));
		name_.setEditable(false);
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
		textNDEF_.setBackground(new Color(255, 255, 255));
		textNDEF_.setEditable(false);
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
		textAuth_.setBackground(new Color(255, 255, 255));
		textAuth_.setEditable(false);
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
		textBlocks_.setBackground(new Color(255, 255, 255));
		textBlocks_.setEditable(false);
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
		textBlockSize_.setBackground(new Color(255, 255, 255));
		textBlockSize_.setEditable(false);
		GridBagConstraints gbc_textBlockSize_ = new GridBagConstraints();
		gbc_textBlockSize_.fill = GridBagConstraints.HORIZONTAL;
		gbc_textBlockSize_.gridx = 5;
		gbc_textBlockSize_.gridy = 1;
		panelInfo.add(textBlockSize_, gbc_textBlockSize_);
		textBlockSize_.setColumns(10);

		tabFolderContainer_ = new JLayeredPane();
		tabFolderContainer_.setBorder(new EmptyBorder(4, 8, 8, 8));
		GridBagConstraints gbc_tabFolderContainer_ = new GridBagConstraints();
		gbc_tabFolderContainer_.insets = new Insets(0, 0, 5, 0);
		gbc_tabFolderContainer_.fill = GridBagConstraints.BOTH;
		gbc_tabFolderContainer_.gridx = 0;
		gbc_tabFolderContainer_.gridy = 2;
		getContentPane().add(tabFolderContainer_, gbc_tabFolderContainer_);
		GridBagLayout gbl_tabFolderContainer_ = new GridBagLayout();
		gbl_tabFolderContainer_.columnWidths = new int[] { 61, 0 };
		gbl_tabFolderContainer_.rowHeights = new int[] { 342, 0 };
		gbl_tabFolderContainer_.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_tabFolderContainer_.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
		tabFolderContainer_.setLayout(gbl_tabFolderContainer_);

		tabFolder = new JTabbedPane(JTabbedPane.TOP);
		GridBagConstraints gbc_tabFolder = new GridBagConstraints();
		tabFolderContainer_.setLayer(tabFolder, 1);
		gbc_tabFolder.fill = GridBagConstraints.BOTH;
		gbc_tabFolder.gridx = 0;
		gbc_tabFolder.gridy = 0;
		tabFolderContainer_.add(tabFolder, gbc_tabFolder);

		rawPanel_ = new JPanel();
		tabFolder.addTab("RAW", null, rawPanel_, null);
		GridBagLayout gbl_rawPanel_ = new GridBagLayout();
		gbl_rawPanel_.columnWidths = new int[] { 763, 0 };
		gbl_rawPanel_.rowHeights = new int[] { 64, 10, 0 };
		gbl_rawPanel_.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_rawPanel_.rowWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		rawPanel_.setLayout(gbl_rawPanel_);

		JPanel panel_3 = new JPanel();
		GridBagConstraints gbc_panel_3 = new GridBagConstraints();
		gbc_panel_3.anchor = GridBagConstraints.WEST;
		gbc_panel_3.insets = new Insets(0, 0, 5, 0);
		gbc_panel_3.gridx = 0;
		gbc_panel_3.gridy = 0;
		rawPanel_.add(panel_3, gbc_panel_3);

		btnRawWrite_ = new JButton("Raw Write");
		btnRawWrite_.setEnabled(false);
		btnRawWrite_.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				rawWrite();
			}
		});
		btnRawWrite_.setIcon(new ImageIcon(NfcAppFrame.class.getResource("/jence/icon/cardwrite.png")));
		panel_3.add(btnRawWrite_);

		btnAuth_ = new JButton("Auth");
		btnAuth_.setEnabled(false);
		btnAuth_.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				authDialog = new AuthFrame(NfcAppFrame.this);
				authDialog.setVisible(true);

//				for (int i = 0; i < table_1.getItemCount(); i++) {
//					table_1.getItem(i).setImage((Image) null);
//				}
//				TableItem item = (TableItem) arg0.item;
//
//				item.setImage(SWTResourceManager.getImage(
//						NfcAppComposite.class, "/jence/icon/checkbox16.png"));
//				arg0.doit = true;
//				saveSelection();
//				updateAccessBits();

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
		rawPanel_.add(scrollPane, gbc_scrollPane);

		table_ = new JTable(new DefaultTableModel(new Object[][] {}, new String[] {}));
		table_.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table_.getTableHeader().setReorderingAllowed(false);
		scrollPane.setViewportView(table_);

		ndefPanel_ = new JPanel();
		tabFolder.addTab("NDEF", null, ndefPanel_, null);
		GridBagLayout gbl_ndefPanel_ = new GridBagLayout();
		gbl_ndefPanel_.columnWidths = new int[] { 0, 0 };
		gbl_ndefPanel_.rowHeights = new int[] { 86, 0, 0 };
		gbl_ndefPanel_.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_ndefPanel_.rowWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		ndefPanel_.setLayout(gbl_ndefPanel_);

		JPanel panel_4 = new JPanel();
		GridBagConstraints gbc_panel_4 = new GridBagConstraints();
		gbc_panel_4.anchor = GridBagConstraints.NORTHWEST;
		gbc_panel_4.insets = new Insets(0, 0, 5, 0);
		gbc_panel_4.gridx = 0;
		gbc_panel_4.gridy = 0;
		ndefPanel_.add(panel_4, gbc_panel_4);

		JPanel panel_5 = new JPanel();
		panel_5.setBorder(new TitledBorder(null, "NDEF", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_4.add(panel_5);

		JButton btnFormat = new JButton("Format");
		btnFormat.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				NfcApp.prompt(NfcAppFrame.this,
						"For format operation to work successfully, first scan the card, then hit this button.",
						"Information", NfcApp.MessageType.INFORMATION);
				if (ndefFormat()) {
					NfcApp.prompt(NfcAppFrame.this, "Format complete", "Information", NfcApp.MessageType.INFORMATION);
				}

			}
		});
		btnFormat.setMargin(new Insets(2, 8, 2, 8));
		btnFormat.setIcon(new ImageIcon(NfcAppFrame.class.getResource("/jence/icon/format.png")));
		panel_5.add(btnFormat);

		JButton btnClean = new JButton("Clean");
		btnClean.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				clean();
			}
		});
		btnClean.setMargin(new Insets(2, 8, 2, 8));
		btnClean.setIcon(new ImageIcon(NfcAppFrame.class.getResource("/jence/icon/clean.png")));
		panel_5.add(btnClean);

		JButton btnWrite = new JButton("Write");
		btnWrite.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openNdefWriteDialog();
			}
		});
		btnWrite.setMargin(new Insets(2, 8, 2, 8));
		btnWrite.setIcon(new ImageIcon(NfcAppFrame.class.getResource("/jence/icon/write.png")));
		panel_5.add(btnWrite);

		JButton btnRead = new JButton("Read");
		btnRead.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				readNDEF();

			}
		});
		btnRead.setMargin(new Insets(2, 8, 2, 8));
		btnRead.setIcon(new ImageIcon(NfcAppFrame.class.getResource("/jence/icon/read.png")));
		panel_5.add(btnRead);

		JButton btnSave = new JButton("Save");
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				saveToFile();
			}
		});
		btnSave.setMargin(new Insets(2, 8, 2, 8));
		btnSave.setIcon(new ImageIcon(NfcAppFrame.class.getResource("/jence/icon/save.png")));
		panel_5.add(btnSave);

		JButton btnErase = new JButton("Erase");
		btnErase.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ndefErase();
			}
		});
		btnErase.setMargin(new Insets(2, 8, 2, 8));
		btnErase.setIcon(new ImageIcon(NfcAppFrame.class.getResource("/jence/icon/erase.png")));
		panel_5.add(btnErase);

		JScrollPane scrollPane_1 = new JScrollPane();
		GridBagConstraints gbc_scrollPane_1 = new GridBagConstraints();
		gbc_scrollPane_1.fill = GridBagConstraints.BOTH;
		gbc_scrollPane_1.gridx = 0;
		gbc_scrollPane_1.gridy = 1;
		ndefPanel_.add(scrollPane_1, gbc_scrollPane_1);

		ndeftable_ = new JTable();
		String[] NDEFFormattedModelColName = { "Index", "ID", "Type", "Encoding", "Data" };
		for (String col : NDEFFormattedModelColName) {
			NDEFFormattedModel.addColumn(col);
		}
		ndeftable_.setModel(NDEFFormattedModel);
		ndeftable_.setEnabled(false);
		ndeftable_.getTableHeader().setReorderingAllowed(false);
		scrollPane_1.setViewportView(ndeftable_);

		emulatePanel_ = new JPanel();
		emulatePanel_.setBorder(new EmptyBorder(8, 8, 0, 8));
		tabFolder.addTab("EMULATE", null, emulatePanel_, null);
		GridBagLayout gbl_emulatePanel_ = new GridBagLayout();
		gbl_emulatePanel_.columnWidths = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		gbl_emulatePanel_.rowHeights = new int[] { 0, 0, 0 };
		gbl_emulatePanel_.columnWeights = new double[] { 0.0, 1.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE };
		gbl_emulatePanel_.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		emulatePanel_.setLayout(gbl_emulatePanel_);

		JLabel lblUID = new JLabel("UID");
		GridBagConstraints gbc_lblUID = new GridBagConstraints();
		gbc_lblUID.anchor = GridBagConstraints.EAST;
		gbc_lblUID.insets = new Insets(0, 0, 5, 5);
		gbc_lblUID.gridx = 0;
		gbc_lblUID.gridy = 0;
		emulatePanel_.add(lblUID, gbc_lblUID);

		emulationUid_ = new JTextField(6);
		emulationUid_.setDocument(new JTextFieldLimit(6));
		emulationUid_.setText("12BA89");

		GridBagConstraints gbc_emulationUid_ = new GridBagConstraints();
		gbc_emulationUid_.insets = new Insets(0, 0, 5, 5);
		gbc_emulationUid_.fill = GridBagConstraints.HORIZONTAL;
		gbc_emulationUid_.gridx = 1;
		gbc_emulationUid_.gridy = 0;
		emulatePanel_.add(emulationUid_, gbc_emulationUid_);
		emulationUid_.setColumns(10);

		JLabel lblTimeout = new JLabel("Timeout (sec)");
		GridBagConstraints gbc_lblTimeout = new GridBagConstraints();
		gbc_lblTimeout.insets = new Insets(0, 0, 5, 5);
		gbc_lblTimeout.gridx = 2;
		gbc_lblTimeout.gridy = 0;
		emulatePanel_.add(lblTimeout, gbc_lblTimeout);

		timeout_ = new JSpinner();
		timeout_.setModel(new SpinnerNumberModel(60, 0, 30000, 1));
		GridBagConstraints gbc_timeout_ = new GridBagConstraints();
		gbc_timeout_.insets = new Insets(0, 0, 5, 5);
		gbc_timeout_.gridx = 3;
		gbc_timeout_.gridy = 0;
		emulatePanel_.add(timeout_, gbc_timeout_);

		btnWritable_ = new JCheckBox("Writable");
		GridBagConstraints gbc_btnWritable_ = new GridBagConstraints();
		gbc_btnWritable_.insets = new Insets(0, 0, 5, 5);
		gbc_btnWritable_.gridx = 4;
		gbc_btnWritable_.gridy = 0;
		emulatePanel_.add(btnWritable_, gbc_btnWritable_);

		JButton btnApply_ = new JButton("Apply");
		btnApply_.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				initEmulation();
			}
		});
		btnApply_.setMargin(new Insets(2, 8, 2, 8));
		btnApply_.setIcon(new ImageIcon(NfcAppFrame.class.getResource("/jence/icon/cardwrite.png")));
		GridBagConstraints gbc_btnApply_ = new GridBagConstraints();
		gbc_btnApply_.anchor = GridBagConstraints.EAST;
		gbc_btnApply_.insets = new Insets(0, 0, 5, 5);
		gbc_btnApply_.gridx = 5;
		gbc_btnApply_.gridy = 0;
		emulatePanel_.add(btnApply_, gbc_btnApply_);

		JButton btnStart_ = new JButton("Start");
		btnStart_.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				startEmulation();
			}
		});
		btnStart_.setMargin(new Insets(2, 8, 2, 8));
		btnStart_.setIcon(new ImageIcon(NfcAppFrame.class.getResource("/jence/icon/emulate.png")));
		GridBagConstraints gbc_btnStart_ = new GridBagConstraints();
		gbc_btnStart_.insets = new Insets(0, 0, 5, 5);
		gbc_btnStart_.gridx = 6;
		gbc_btnStart_.gridy = 0;
		emulatePanel_.add(btnStart_, gbc_btnStart_);

		JButton btnStop_ = new JButton("Stop");
		btnStop_.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				stopEmulation();
			}
		});
		btnStop_.setMargin(new Insets(2, 8, 2, 8));
		btnStop_.setIcon(new ImageIcon(NfcAppFrame.class.getResource("/jence/icon/stop.png")));
		GridBagConstraints gbc_btnStop_ = new GridBagConstraints();
		gbc_btnStop_.insets = new Insets(0, 0, 5, 0);
		gbc_btnStop_.gridx = 7;
		gbc_btnStop_.gridy = 0;
		emulatePanel_.add(btnStop_, gbc_btnStop_);

		JLabel lblTime = new JLabel("Time");
		GridBagConstraints gbc_lblTime = new GridBagConstraints();
		gbc_lblTime.anchor = GridBagConstraints.EAST;
		gbc_lblTime.insets = new Insets(0, 0, 0, 5);
		gbc_lblTime.gridx = 0;
		gbc_lblTime.gridy = 1;
		emulatePanel_.add(lblTime, gbc_lblTime);

		elapsed_ = new JTextField();
		elapsed_.setText("0");
		GridBagConstraints gbc_elapsed_ = new GridBagConstraints();
		gbc_elapsed_.insets = new Insets(0, 0, 0, 5);
		gbc_elapsed_.fill = GridBagConstraints.HORIZONTAL;
		gbc_elapsed_.gridx = 1;
		gbc_elapsed_.gridy = 1;
		emulatePanel_.add(elapsed_, gbc_elapsed_);
		elapsed_.setColumns(10);

		loadingPanel_ = new TransparentPanel();
		loadingPanel_.setVisible(false);
		loadingPanel_.setForeground(new Color(0, 0, 0));
		tabFolderContainer_.setLayer(loadingPanel_, 100);
		loadingPanel_.setBackground(new Color(244, 244, 244));
		GridBagConstraints gbc_loadingPanel_ = new GridBagConstraints();

		gbc_loadingPanel_.fill = GridBagConstraints.BOTH;
		gbc_loadingPanel_.gridx = 0;
		gbc_loadingPanel_.gridy = 0;
		GridBagLayout gbl_loadingPanel_ = new GridBagLayout();
		gbl_loadingPanel_.columnWidths = new int[] { 146, 0 };
		gbl_loadingPanel_.rowHeights = new int[] { 0, 0, 0 };
		gbl_loadingPanel_.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_loadingPanel_.rowWeights = new double[] { 1.0, 1.0, Double.MIN_VALUE };
		loadingPanel_.setLayout(gbl_loadingPanel_);

		tabFolderContainer_.add(loadingPanel_, gbc_loadingPanel_);

		progressBar_ = new JProgressBar();
		progressBar_.setPreferredSize(new Dimension(250, 25));
		progressBar_.setFont(new Font("Tahoma", Font.PLAIN, 16));
		progressBar_.setIndeterminate(true);
		GridBagConstraints gbc_progressBar_ = new GridBagConstraints();
		gbc_progressBar_.anchor = GridBagConstraints.SOUTH;
		gbc_progressBar_.insets = new Insets(0, 0, 5, 0);
		gbc_progressBar_.gridx = 0;
		gbc_progressBar_.gridy = 0;
		loadingPanel_.add(progressBar_, gbc_progressBar_);

		lblLoadingText_ = new JLabel("Loading Memory Content");
		lblLoadingText_.setFont(new Font("Tahoma", Font.PLAIN, 16));
		GridBagConstraints gbc_lblLoadingText_ = new GridBagConstraints();
		gbc_lblLoadingText_.anchor = GridBagConstraints.NORTH;
		gbc_lblLoadingText_.gridx = 0;
		gbc_lblLoadingText_.gridy = 1;
		loadingPanel_.add(lblLoadingText_, gbc_lblLoadingText_);

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
		gbl_panelFooter.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
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

		setComponentsEnabled(false, rawPanel_, emulatePanel_, ndefPanel_);

		this.setSize(798, 565);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);

	}

}

class CustomRenderer extends DefaultTableCellRenderer {
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		Font boldFont = new Font(table.getFont().getName(), Font.BOLD, table.getFont().getSize());

		// Check if the row number is divisible by 3
		if ((row + 1) % 4 == 0 && row != 0) {
			cell.setBackground(Color.YELLOW); // Change background color for rows divisible by 3
		} else if (row == 0) {
			cell.setBackground(Color.LIGHT_GRAY); // Change background color for rows divisible by 3

		} else {
			cell.setBackground(Color.WHITE); // Reset background color for other rows
		}

		for (Tuple<Integer, Integer> tuple : NfcAppFrame.editedValue) {
			if (tuple.getRow() == row && tuple.getCol() == column) {
				setFont(boldFont);
				cell.setBackground(Color.decode("#b5daf7"));
			}
		}

		return cell;
	}
}

class EmulationWorker extends SwingWorker<Void, Void> {
	private NfcAppFrame parent;

	public EmulationWorker(NfcAppFrame parent) {
		this.parent = parent;
	}

	@Override
	protected Void doInBackground() throws Exception {
		parent.elapsed_.setText("" + 0);
		final int seconds[] = { 0 };
		while (parent.emulate_) {
			parent.elapsed_.setText("" + seconds[0]++);
			Thread.sleep(1000);
		}
		return null;
	}

}

//Custom class for row col rawEdit value
class Tuple<A, B> {
	private final A row;
	private final B col;

	public Tuple(A row, B col) {
		this.row = row;
		this.col = col;
	}

	public A getRow() {
		return row;
	}

	public B getCol() {
		return col;
	}
}

class TransparentPanel extends JPanel {
	public TransparentPanel() {
		setOpaque(false); // Set panel's opacity to false
	}

	@Override
	protected void paintComponent(Graphics g) {
		// Paint a transparent background
		g.setColor(new Color(0, 153, 255, 100)); // Transparent color
		g.fillRect(0, 0, getWidth(), getHeight()); // Fill the panel with transparent color
		super.paintComponent(g); // Call super method to paint other components
	}

	@Override
	protected void paintChildren(Graphics g) {
		// Paint the children components
		Graphics2D g2d = (Graphics2D) g.create();
		g2d.setComposite(AlphaComposite.SrcOver.derive(1f)); // Set full opacity
		super.paintChildren(g2d); // Call super method to paint children components
		g2d.dispose();
	}

}
