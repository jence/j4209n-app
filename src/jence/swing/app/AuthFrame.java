package jence.swing.app;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.JRadioButton;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;

import jence.jni.J4209N;
import jence.swing.app.NfcApp.MessageType;

import javax.swing.ButtonGroup;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;

import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.DropMode;
import java.awt.Color;
import java.awt.Component;

import javax.swing.border.LineBorder;
import java.awt.Dimension;

public class AuthFrame extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField asciiA_;
	private JTextField hexA_;
	private JTextField asciiB_;
	private JTextField hexB_;
	private JTable table;

	private Object[][] tableData = {

	};

	// Column names
	private String[] columnNames = { "Access Bits", "Key A", "Access Bit Property", "Key B" };

	private AuthTableModel model = new AuthTableModel(table, tableData, columnNames);
	private final ButtonGroup blockGroup_ = new ButtonGroup();
	private JTextField data_;
	private JTextField accessbits_;
	private JTextField trailerbits_;
	private JRadioButton block0_;
	private JRadioButton block1_;
	private JRadioButton block2_;
	private JRadioButton trailer_;
	private J4209N.KeyData key_ = new J4209N.KeyData(0);
	private Map auth_ = new Properties();

	private String ascii2hex(String str) {
		try {
			str = "\0\0\0\0\0\0" + str;
			byte[] t = str.getBytes("UTF-8");
			String text = "";
			for (int i = 0; i < t.length; i++) {
				text += String.format("%02X", t[i]);
			}
			return text.substring(text.length() - 12);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	private String getHex2Ascii(String hex) {
		BigInteger bd = new BigInteger(hex, 16);
		byte[] b = bd.toByteArray();
		String ascii = "";
		for (int i = 0; i < b.length; i++) {
			if (b[i] > (byte) 32 && b[i] < (byte) 127) {
				ascii += (char) b[i];
			} else {
				return ascii;
			}
		}
		return ascii;
	}

	private void parseSectorTrailer(String trailer) {
		int n = trailer.indexOf(' ');
		String keyA = trailer.substring(0, n).trim();
		int n1 = trailer.indexOf(' ', n + 1);
		String accessbits = trailer.substring(n, n1).trim();
		String keyB = trailer.substring(n1).trim();

		asciiA_.setText(getHex2Ascii(keyA));
		asciiB_.setText(getHex2Ascii(keyB));
		hexA_.setText(keyA);
		hexB_.setText(keyB);
		accessbits_.setText(accessbits);
		data_.setText(accessbits.substring(6));

//		updateRadioFromAccessBits(accessbits);
	}

	private byte[] hex2bytes(String hex, int arraySize) {
		if (hex.length() % 2 != 0)
			hex = "0" + hex; // make the length even;
		byte[] bytes = new byte[arraySize];
		for (int i = 0; i < hex.length() / 2; i++) {
			if (i == bytes.length)
				break;
			String h = hex.substring(i, i + 2);
			int n = Integer.parseInt(h, 16);
			bytes[i] = (byte) n;
		}
		return bytes;
	}
//	private void updateRadioFromAccessBits(String accessbits) {
//		BigInteger bi = new BigInteger(accessbits, 16);
//		// b0 = 100, b1 = 101, b2 = 110, b3 = 111
//		byte[] b = bi.toByteArray();
//		// 1111 1010 1100
//		// int byte6 = b[3] & 0xFF;
//		int byte7 = b[1] & 0xFF; // 0xF0;
//		int byte8 = b[2] & 0xFF; // 0xAC;
//
//		int c1 = (byte7) >> 4;
//		int c2 = (byte8 & 0x0F);
//		int c3 = (byte8 >> 4);
//
//		int block0 = ((c1 & 0x01) << 2) | ((c2 & 0x01) << 1) | (c3 & 0x01);
//		int block1 = ((c1 & 0x02) << 1) | ((c2 & 0x02) << 0)
//				| ((c3 & 0x02) >> 1);
//		int block2 = ((c1 & 0x04) << 0) | ((c2 & 0x04) >> 1)
//				| ((c3 & 0x04) >> 2);
//		int trailer = ((c1 & 0x08) >> 1) | ((c2 & 0x08) >> 2)
//				| ((c3 & 0x08) >> 3);
//
//		block0_.setData(new Integer(block0));
//		block1_.setData(new Integer(block1));
//		block2_.setData(new Integer(block2));
//		trailer_.setData(new Integer(trailer));
//
//		loadSectorTrailerSettings(0);
//	}
//

	private void useKey() {
		String message = "Use have choosen to use this key for subsequent read and write. "
				+ "If your tag does not use this key, then you should write this key to the tag first before "
				+ "subsequent read or write. ";

		boolean result = NfcApp.prompt(this, message, "Confirmation", NfcApp.MessageType.CONFIRMATION);
		if (result) {
			try {
				// Assuming keys() method is a static method in NfcApp class
				NfcApp.driver_.keys(key_.KeyA, key_.KeyB);
				// Assuming this is a reference to a JFrame or JDialog
				this.dispose();
			} catch (Exception e) {
				NfcApp.prompt(this, e.getLocalizedMessage(), "Error", NfcApp.MessageType.ERROR);
			}
		}
	}



    private void saveFile() throws Exception {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save");
        fileChooser.setFileFilter(new FileNameExtensionFilter("J4210N Files (*.j4210n)", "j4210n"));
        
        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            String filename = fileChooser.getSelectedFile().getAbsolutePath();
            if (!filename.endsWith(".j4210n")) {
                filename += ".j4210n";
            }
            String warning = " IMPORTANT: Keep this file in a safe "
                    + "place and do not\n distribute. The file contains sensitive password information.";
            try (FileOutputStream fos = new FileOutputStream(filename)) {
                auth_.clear();
                auth_.put("type", NfcApp.driver_.type().name());
                auth_.put("trailer", trailerbits_.getText());
                ((Properties) auth_).store(fos, warning);
                JOptionPane.showMessageDialog(this, "File saved successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, e.getLocalizedMessage(), "Error", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private Map<String, String> propertiesToMap(Properties properties) {
        Map<String, String> map = new HashMap<>();
        for (String key : properties.stringPropertyNames()) {
            map.put(key, properties.getProperty(key));
        }
        return map;
    }

    private Map<String, String> loadFile() throws HeadlessException, Exception {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Open");
        fileChooser.setFileFilter(new FileNameExtensionFilter("J4210N Files (*.j4210n)", "j4210n"));

        int userSelection = fileChooser.showOpenDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            String selected = fileChooser.getSelectedFile().getAbsolutePath();
            Properties properties = new Properties();
            try (FileInputStream fis = new FileInputStream(selected)) {
                properties.load(fis);
                String type = properties.getProperty("type");
                if (!type.equalsIgnoreCase(NfcApp.driver_.type().name())) {
                    JOptionPane.showMessageDialog(this, "The tag under scan is of type " + NfcApp.driver_.type()
                            + " but the Auth is for type " + type + ".", "Warning", JOptionPane.WARNING_MESSAGE);
                    return null;
                }
                return propertiesToMap(properties);
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, e.getLocalizedMessage(), "Error", JOptionPane.WARNING_MESSAGE);
            }
        }
        return new HashMap<>(); // Return an empty map if file loading fails
    }
    
	private void writeAuth(boolean defaultKwy) {
		String warning = "You are about to change authentication by changing keys and access conditions.";
		warning += " This will apply to all the sectors of this tag.";
		warning += " Some access conditions may make the tag unreadable. Are you sure you want to perform this operation?";

		if (!NfcApp.prompt(this, warning, "Warning", NfcApp.MessageType.CONFIRMATION)) {
			return;
		}

		int block;
		String hexdata = hexA_.getText() + accessbits_.getText() + hexB_.getText();
		if (defaultKwy) {
			hexdata = "FFFFFFFFFFFF" + "FF078069" + "FFFFFFFFFFFF"; // This is the default key of sector trailer of a
																	// new tag (MIFARE CLASSIC)
		}
		BigInteger bi = new BigInteger(hexdata, 16);
		byte[] data = bi.toByteArray();
		try {
			String totalSectors = (String) auth_.get("sectors");
			int n = 0;
			try {
				n = Integer.parseInt(totalSectors);
			} catch (Throwable t) {
				NfcApp.prompt(this, t.getLocalizedMessage() + ". Sector information could not be loaded.", "Error",
						MessageType.ERROR);
				return;
			}
			for (int i = 0; i < n; i++) {
				byte[] rdata = NfcApp.driver_.read(i * 4, false);
				if (rdata == null) {
					// authentication failed.
					throw new Exception("Authentication Failed.");
				}
				block = i * 4 + 3;
				NfcApp.driver_.write(block, data, false);
			}
			System.out.println("data = " + hexdata);
			NfcApp.prompt(this, "Successfully wrote sector trailer.", "Success", MessageType.INFORMATION);
		} catch (Exception e) {
			NfcApp.prompt(this, e.getLocalizedMessage(), "Error", MessageType.ERROR);
		}
	}

	private void loadSelection() {
		int selection = 0;
		if (table.getSelectedRow() < -1) {
			return;
		}
		if (block0_.isSelected()) {
			selection = (Integer) block0_.getClientProperty("selection");
		} else if (block1_.isSelected()) {
			selection = (Integer) block1_.getClientProperty("selection");
		} else if (block2_.isSelected()) {
			selection = (Integer) block2_.getClientProperty("selection");
		} else if (trailer_.isSelected()) {
			selection = (Integer) trailer_.getClientProperty("selection");
		}
		table.setRowSelectionInterval(selection, selection);
	}

	private void saveSelection() {
		int selection = table.getSelectedRow();
		if (selection == -1) {
			selection = 0;
		}
		if (block0_.isSelected()) {
			block0_.putClientProperty("selection", selection);
		} else if (block1_.isSelected()) {
			block1_.putClientProperty("selection", selection);
		} else if (block2_.isSelected()) {
			block2_.putClientProperty("selection", selection);
		} else if (trailer_.isSelected()) {
			trailer_.putClientProperty("selection", selection);
		}
	}

	private void updateAccessBits() {
		if (data_.getText().trim().length() == 0)
			data_.setText("00");
		int data = Integer.parseInt(data_.getText(), 16);

		byte b0 = (byte) ((Integer) block0_.getClientProperty("selection")).byteValue();
		byte b1 = (byte) ((Integer) block1_.getClientProperty("selection")).byteValue();
		byte b2 = (byte) ((Integer) block2_.getClientProperty("selection")).byteValue();
		byte b3 = (byte) ((Integer) trailer_.getClientProperty("selection")).byteValue();

		byte[] accessbits = generateAccessBits(b0, b1, b2, b3, data);
		String z = "";
		for (int i = 0; i < accessbits.length; i++) {
			z += String.format("%02X", accessbits[i]);
		}
		accessbits_.setText(z);

		trailerbits_.setText(hexA_.getText() + " " + accessbits_.getText() + " " + hexB_.getText());

		key_ = new J4209N.KeyData(b3);
		key_.KeyA = hex2bytes(hexA_.getText(), 6);
		key_.KeyB = hex2bytes(hexB_.getText(), 6);

	}

	private void loadSectorTrailerSettings(int block) {
		model.setRowCount(0); // Clear table

		if (block == 3) {
			model.addRow(new Object[] { "000", "Write with Key A", "Read with Key A", "R/W with Key A" });
			model.addRow(new Object[] { "001", "Write with Key A", "R/W with Key A", "R/W with Key A" });
			model.addRow(new Object[] { "010", "Cannot R/W", "Read with Key A", "Read with Key A" });
			model.addRow(
					new Object[] { "011", "Write with Key B", "Read with Key A, R/W with Key B", "Write with Key B" });
			model.addRow(new Object[] { "100", "Write with Key B", "Read with Key A or B", "Write with Key B" });
			model.addRow(new Object[] { "101", "Cannot R/W", "Read with Key A, R/W with Key B", "Cannot R/W" });
			model.addRow(new Object[] { "110", "Cannot R/W", "Read with Key A or B", "Cannot R/W" });
			model.addRow(new Object[] { "111", "Cannot R/W", "Read with Key A or B", "Cannot R/W" });
		} else {
			model.addRow(new Object[] { "000", "R/W with Key A | B", "Key A | B", "Key A | B" });
			model.addRow(new Object[] { "001", "Read with Key A | B", "-", "Key A | B" });
			model.addRow(new Object[] { "010", "Read with Key A | B", "-", "-" });
			model.addRow(new Object[] { "011", "R/W with Key B", "-", "-" });
			model.addRow(new Object[] { "100", "R/W with Key B, Read with Key A", "-", "-" });
			model.addRow(new Object[] { "101", "Read with Key B", "-", "-" });
			model.addRow(new Object[] { "110", "R/W with Key B, Read with Key A", "Key B", "Key A | B" });
			model.addRow(new Object[] { "111", "-", "-", "-" });
		}
		table.setModel(model);

		loadSelection();
//		table_1.getSelection()[0].setImage(SWTResourceManager.getImage(
//				NfcAppComposite.class, "/jence/icon/checkbox16.png")); // TODO: custom class needed too complicated for nwo 

		updateAccessBits();
	}

	private byte[] generateAccessBits(byte block0, byte block1, byte block2, byte block3, int data) {
		int c1_0 = (block0 & 0x04) >> 2;
		int c2_0 = (block0 & 0x02) >> 1;
		int c3_0 = (block0 & 0x01);

		int c1_1 = (block1 & 0x04) >> 2;
		int c2_1 = (block1 & 0x02) >> 1;
		int c3_1 = block1 & 0x01;

		int c1_2 = (block2 & 0x04) >> 2;
		int c2_2 = (block2 & 0x02) >> 1;
		int c3_2 = block2 & 0x01;

		int c1_3 = (block3 & 0x04) >> 2;
		int c2_3 = (block3 & 0x02) >> 1;
		int c3_3 = block3 & 0x01;

		int c1 = 8 * c1_3 + 4 * c1_2 + 2 * c1_1 + c1_0;
		int c2 = 8 * c2_3 + 4 * c2_2 + 2 * c2_1 + c2_0;
		int c3 = 8 * c3_3 + 4 * c3_2 + 2 * c3_1 + c3_0;

		int byte7 = (c1 << 4) | (~c3 & 0x0F);
		int byte8 = (c3 << 4) | c2;
		int byte6 = ((~c2 & 0x0F) << 4) | (~c1 & 0x0F);
		int byte9 = (byte) data;

		byte[] accessbits = { (byte) byte6, (byte) byte7, (byte) byte8, (byte) byte9 };

		return accessbits;
	}

//	private void updateAccessBits() {
//		if (data_.getText().trim().length() == 0)
//			data_.setText("00");
//		int data = Integer.parseInt(data_.getText(), 16);
//
//		byte b0 = (byte) ((Integer) block0_.getData()).byteValue();
//		byte b1 = (byte) ((Integer) block1_.getData()).byteValue();
//		byte b2 = (byte) ((Integer) block2_.getData()).byteValue();
//		byte b3 = (byte) ((Integer) trailer_.getData()).byteValue();
//
//		byte[] accessbits = generateAccessBits(b0, b1, b2, b3, data);
//		String z = "";
//		for (int i = 0; i < accessbits.length; i++) {
//			z += String.format("%02X", accessbits[i]);
//		}
//		accessbits_.setText(z);
//
//		trailerbits_.setText(hexA_.getText() + " " + accessbits_.getText()
//				+ " " + hexB_.getText());
//
//		key_ = new J4209N.KeyData(b3);
//		key_.KeyA = hex2bytes(hexA_.getText(), 6);
//		key_.KeyB = hex2bytes(hexB_.getText(), 6);
//
//	}

	public AuthFrame(NfcAppFrame parent) {

		super(parent, "Auth", true);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 434, 0 };
		gridBagLayout.rowHeights = new int[] { 10, 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE };
		getContentPane().setLayout(gridBagLayout);

		JPanel panelKey = new JPanel();
		panelKey.setBorder(new EmptyBorder(8, 10, 8, 10));
		GridBagConstraints gbc_panelKey = new GridBagConstraints();
		gbc_panelKey.fill = GridBagConstraints.BOTH;
		gbc_panelKey.insets = new Insets(0, 0, 5, 0);
		gbc_panelKey.gridx = 0;
		gbc_panelKey.gridy = 0;
		getContentPane().add(panelKey, gbc_panelKey);
		GridBagLayout gbl_panelKey = new GridBagLayout();
		gbl_panelKey.columnWidths = new int[] { 0, 0, 0, 0, 0, 0, 0 };
		gbl_panelKey.rowHeights = new int[] { 0, 0, 0 };
		gbl_panelKey.columnWeights = new double[] { 0.0, 0.0, 1.0, 1.0, 1.0, 0.0, Double.MIN_VALUE };
		gbl_panelKey.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		panelKey.setLayout(gbl_panelKey);

		JLabel lblNewLabel = new JLabel("Key A");
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 0;
		panelKey.add(lblNewLabel, gbc_lblNewLabel);

		JLabel lblNewLabel_1 = new JLabel("ASCII");
		GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
		gbc_lblNewLabel_1.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_1.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_1.gridx = 1;
		gbc_lblNewLabel_1.gridy = 0;
		panelKey.add(lblNewLabel_1, gbc_lblNewLabel_1);

		asciiA_ = new JTextField(6);
		asciiA_.setDocument(new JTextFieldLimit(6));

		asciiA_.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				// Text inserted
				SwingUtilities.invokeLater(() -> {
					hexA_.setText(ascii2hex(asciiA_.getText()));
					updateAccessBits();
				});

			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				// Text removed
				SwingUtilities.invokeLater(() -> {
					hexA_.setText(ascii2hex(asciiA_.getText()));
					updateAccessBits();

				});

			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				// Style change (not applicable for plain text components)
			}
		});

		GridBagConstraints gbc_asciiA_ = new GridBagConstraints();
		gbc_asciiA_.fill = GridBagConstraints.HORIZONTAL;
		gbc_asciiA_.insets = new Insets(0, 0, 5, 5);
		gbc_asciiA_.gridx = 2;
		gbc_asciiA_.gridy = 0;
		panelKey.add(asciiA_, gbc_asciiA_);

		JLabel lblNewLabel_2 = new JLabel("Hex");
		GridBagConstraints gbc_lblNewLabel_2 = new GridBagConstraints();
		gbc_lblNewLabel_2.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_2.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_2.gridx = 3;
		gbc_lblNewLabel_2.gridy = 0;
		panelKey.add(lblNewLabel_2, gbc_lblNewLabel_2);

		hexA_ = new JTextField();
		hexA_.setText("FFFFFFFFFFFF");
		GridBagConstraints gbc_hexA_ = new GridBagConstraints();
		gbc_hexA_.insets = new Insets(0, 0, 5, 5);
		gbc_hexA_.fill = GridBagConstraints.HORIZONTAL;
		gbc_hexA_.gridx = 4;
		gbc_hexA_.gridy = 0;
		panelKey.add(hexA_, gbc_hexA_);
		hexA_.setColumns(10);

		JButton btnDefault_A = new JButton("Default");
		btnDefault_A.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				hexA_.setText("FFFFFFFFFFFF");
				asciiA_.setText("");
				updateAccessBits();
			}
		});

		btnDefault_A.setIcon(new ImageIcon(AuthFrame.class.getResource("/jence/icon/default16.png")));
		GridBagConstraints gbc_btnDefault_A = new GridBagConstraints();
		gbc_btnDefault_A.insets = new Insets(0, 0, 5, 0);
		gbc_btnDefault_A.gridx = 5;
		gbc_btnDefault_A.gridy = 0;
		panelKey.add(btnDefault_A, gbc_btnDefault_A);

		JLabel lblNewLabel_3 = new JLabel("Key B");
		GridBagConstraints gbc_lblNewLabel_3 = new GridBagConstraints();
		gbc_lblNewLabel_3.insets = new Insets(0, 0, 0, 5);
		gbc_lblNewLabel_3.gridx = 0;
		gbc_lblNewLabel_3.gridy = 1;
		panelKey.add(lblNewLabel_3, gbc_lblNewLabel_3);

		JLabel lblNewLabel_4 = new JLabel("ASCII");
		GridBagConstraints gbc_lblNewLabel_4 = new GridBagConstraints();
		gbc_lblNewLabel_4.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_4.insets = new Insets(0, 0, 0, 5);
		gbc_lblNewLabel_4.gridx = 1;
		gbc_lblNewLabel_4.gridy = 1;
		panelKey.add(lblNewLabel_4, gbc_lblNewLabel_4);

		asciiB_ = new JTextField(6);
		asciiB_.setDocument(new JTextFieldLimit(6));

		asciiB_.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				// Text inserted

				SwingUtilities.invokeLater(() -> {
					hexB_.setText(ascii2hex(asciiB_.getText()));
					updateAccessBits();

				});

			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				// Text removed
				SwingUtilities.invokeLater(() -> {
					hexB_.setText(ascii2hex(asciiB_.getText()));
					updateAccessBits();
				});

			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				// Style change (not applicable for plain text components)
			}
		});

		GridBagConstraints gbc_asciiB_ = new GridBagConstraints();
		gbc_asciiB_.insets = new Insets(0, 0, 0, 5);
		gbc_asciiB_.fill = GridBagConstraints.HORIZONTAL;
		gbc_asciiB_.gridx = 2;
		gbc_asciiB_.gridy = 1;
		panelKey.add(asciiB_, gbc_asciiB_);
		asciiB_.setColumns(10);

		JLabel lblNewLabel_5 = new JLabel("Hex");
		GridBagConstraints gbc_lblNewLabel_5 = new GridBagConstraints();
		gbc_lblNewLabel_5.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_5.insets = new Insets(0, 0, 0, 5);
		gbc_lblNewLabel_5.gridx = 3;
		gbc_lblNewLabel_5.gridy = 1;
		panelKey.add(lblNewLabel_5, gbc_lblNewLabel_5);

		hexB_ = new JTextField();
		hexB_.setText("FFFFFFFFFFFF");
		GridBagConstraints gbc_hexB_ = new GridBagConstraints();
		gbc_hexB_.insets = new Insets(0, 0, 0, 5);
		gbc_hexB_.fill = GridBagConstraints.HORIZONTAL;
		gbc_hexB_.gridx = 4;
		gbc_hexB_.gridy = 1;
		panelKey.add(hexB_, gbc_hexB_);
		hexB_.setColumns(10);

		JButton btnDefault_B = new JButton("Default");
		btnDefault_B.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				hexB_.setText("FFFFFFFFFFFF");
				updateAccessBits();

			}
		});
		btnDefault_B.setIcon(new ImageIcon(AuthFrame.class.getResource("/jence/icon/default16.png")));
		GridBagConstraints gbc_btnDefault_B = new GridBagConstraints();
		gbc_btnDefault_B.gridx = 5;
		gbc_btnDefault_B.gridy = 1;
		panelKey.add(btnDefault_B, gbc_btnDefault_B);

		JPanel panelTable = new JPanel();
		panelTable.setBorder(new EmptyBorder(0, 10, 8, 10));
		panelTable.setBackground(new Color(240, 240, 240));
		GridBagConstraints gbc_panelTable = new GridBagConstraints();
		gbc_panelTable.insets = new Insets(0, 0, 5, 0);
		gbc_panelTable.fill = GridBagConstraints.BOTH;
		gbc_panelTable.gridx = 0;
		gbc_panelTable.gridy = 1;
		getContentPane().add(panelTable, gbc_panelTable);
		GridBagLayout gbl_panelTable = new GridBagLayout();
		gbl_panelTable.rowHeights = new int[] { 0 };
		gbl_panelTable.columnWeights = new double[] { 1.0 };
		gbl_panelTable.rowWeights = new double[] { 1.0 };
		panelTable.setLayout(gbl_panelTable);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportBorder(new LineBorder(new Color(0, 0, 0)));
		scrollPane.setBackground(new Color(0, 0, 255));
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 0;
		panelTable.add(scrollPane, gbc_scrollPane);

		table = new JTable();
		table.setPreferredScrollableViewportSize(new Dimension(450, 150));

		// Data initialization
		Object[][] data = {

		};

		// Column names
		String[] columnNames = { "Access Bits", "Key A", "Access Bit Property", "Key B" };

		// Create table model
		model = new AuthTableModel(table, data, columnNames) {
			Class[] columnTypes = { String.class, String.class, String.class, String.class };

			@Override
			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}
		};

		table.setModel(model);

		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
//				if (e.getClickCount() == 2) { // Check for double-click
//					
//				}

//				for (int i = 0; i < table_1.getItemCount(); i++) {
//					table_1.getItem(i).setImage((Image) null);
//				}
//				TableItem item = (TableItem) arg0.item;
//
//				item.setImage(SWTResourceManager.getImage(
//						NfcAppComposite.class, "/jence/icon/checkbox16.png"));
//				arg0.doit = true;
				saveSelection();
				updateAccessBits();

			}
		});

		scrollPane.setViewportView(table);

		JPanel panel_4 = new JPanel();
		panel_4.setBorder(new EmptyBorder(0, 8, 8, 8));
		GridBagConstraints gbc_panel_4 = new GridBagConstraints();
		gbc_panel_4.insets = new Insets(0, 0, 5, 0);
		gbc_panel_4.fill = GridBagConstraints.BOTH;
		gbc_panel_4.gridx = 0;
		gbc_panel_4.gridy = 2;
		getContentPane().add(panel_4, gbc_panel_4);
		GridBagLayout gbl_panel_4 = new GridBagLayout();
		gbl_panel_4.columnWidths = new int[] { 235, 0, 0 };
		gbl_panel_4.rowHeights = new int[] { 73, 0 };
		gbl_panel_4.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		gbl_panel_4.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
		panel_4.setLayout(gbl_panel_4);

		JPanel panel_2 = new JPanel();
		panel_2.setBorder(new TitledBorder(null, "Blocks", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		GridBagConstraints gbc_panel_2 = new GridBagConstraints();
		gbc_panel_2.insets = new Insets(0, 0, 0, 5);
		gbc_panel_2.fill = GridBagConstraints.BOTH;
		gbc_panel_2.gridx = 0;
		gbc_panel_2.gridy = 0;
		panel_4.add(panel_2, gbc_panel_2);
		GridBagLayout gbl_panel_2 = new GridBagLayout();
		gbl_panel_2.columnWidths = new int[] { 0, 0, 0 };
		gbl_panel_2.rowHeights = new int[] { 0, 0, 0 };
		gbl_panel_2.columnWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		gbl_panel_2.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		panel_2.setLayout(gbl_panel_2);

		block0_ = new JRadioButton("Block 0");
		block0_.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loadSectorTrailerSettings(0);
			}
		});
		block0_.setSelected(true);
		blockGroup_.add(block0_);
		GridBagConstraints gbc_block0_ = new GridBagConstraints();
		gbc_block0_.insets = new Insets(0, 0, 5, 5);
		gbc_block0_.gridx = 0;
		gbc_block0_.gridy = 0;
		panel_2.add(block0_, gbc_block0_);

		block2_ = new JRadioButton("Block 2");
		block2_.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loadSectorTrailerSettings(2);
			}
		});
		blockGroup_.add(block2_);
		GridBagConstraints gbc_block2_ = new GridBagConstraints();
		gbc_block2_.anchor = GridBagConstraints.WEST;
		gbc_block2_.insets = new Insets(0, 0, 5, 0);
		gbc_block2_.gridx = 1;
		gbc_block2_.gridy = 0;
		panel_2.add(block2_, gbc_block2_);

		block1_ = new JRadioButton("Block 1");
		block1_.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loadSectorTrailerSettings(1);
			}
		});
		blockGroup_.add(block1_);
		GridBagConstraints gbc_block1_ = new GridBagConstraints();
		gbc_block1_.insets = new Insets(0, 0, 0, 5);
		gbc_block1_.gridx = 0;
		gbc_block1_.gridy = 1;
		panel_2.add(block1_, gbc_block1_);

		trailer_ = new JRadioButton("Block 3 (Sector Trailer)");
		trailer_.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loadSectorTrailerSettings(3);
			}
		});
		blockGroup_.add(trailer_);
		GridBagConstraints gbc_trailer_ = new GridBagConstraints();
		gbc_trailer_.gridx = 1;
		gbc_trailer_.gridy = 1;
		panel_2.add(trailer_, gbc_trailer_);

		JPanel panel_3 = new JPanel();
		GridBagConstraints gbc_panel_3 = new GridBagConstraints();
		gbc_panel_3.anchor = GridBagConstraints.EAST;
		gbc_panel_3.gridx = 1;
		gbc_panel_3.gridy = 0;
		panel_4.add(panel_3, gbc_panel_3);

		JButton btnNewButton_2 = new JButton("Save");
		btnNewButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					saveFile();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		btnNewButton_2.setMargin(new Insets(2, 8, 2, 8));
		btnNewButton_2.setIcon(new ImageIcon(AuthFrame.class.getResource("/jence/icon/save.png")));
		panel_3.add(btnNewButton_2);

		JButton btnNewButton_3 = new JButton("Load");
		btnNewButton_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				Map map = null;
				try {
					map = loadFile();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				if (map != null) {
					auth_ = map;
					String sectors = auth_.get("sectors").toString(); // no such thing as sectors
					int s = Integer.parseInt(sectors);
					String trailer = "";
					trailer = (String) auth_.get("sector0".toString());
					parseSectorTrailer(trailer);
				}

			}
		});
		btnNewButton_3.setMargin(new Insets(2, 8, 2, 8));
		btnNewButton_3.setIcon(new ImageIcon(AuthFrame.class.getResource("/jence/icon/load.png")));
		panel_3.add(btnNewButton_3);

		JButton btnNewButton_4 = new JButton("Use");
		btnNewButton_4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				useKey();
			}
		});
		btnNewButton_4.setMargin(new Insets(2, 14, 2, 8));
		btnNewButton_4.setIcon(new ImageIcon(AuthFrame.class.getResource("/jence/icon/cardread.png")));
		panel_3.add(btnNewButton_4);

		JButton btnNewButton_5 = new JButton("Write");
		btnNewButton_5.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				writeAuth(false);
			}
		});
		btnNewButton_5.setMargin(new Insets(2, 8, 2, 8));
		btnNewButton_5.setIcon(new ImageIcon(AuthFrame.class.getResource("/jence/icon/write.png")));
		panel_3.add(btnNewButton_5);

		JButton btnNewButton_6 = new JButton("Default");
		btnNewButton_6.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				writeAuth(true);
			}
		});
		btnNewButton_6.setMargin(new Insets(2, 8, 2, 8));
		btnNewButton_6.setIcon(new ImageIcon(AuthFrame.class.getResource("/jence/icon/erase.png")));
		panel_3.add(btnNewButton_6);

		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new EmptyBorder(4, 10, 12, 10));
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.fill = GridBagConstraints.BOTH;
		gbc_panel_1.gridx = 0;
		gbc_panel_1.gridy = 3;
		getContentPane().add(panel_1, gbc_panel_1);
		GridBagLayout gbl_panel_1 = new GridBagLayout();
		gbl_panel_1.columnWidths = new int[] { 0, 0, 0, 0, 0, 0 };
		gbl_panel_1.rowHeights = new int[] { 0, 0, 0 };
		gbl_panel_1.columnWeights = new double[] { 1.0, 0.0, 1.0, 0.0, 1.0, Double.MIN_VALUE };
		gbl_panel_1.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		panel_1.setLayout(gbl_panel_1);

		JLabel lblNewLabel_6 = new JLabel("Data");
		GridBagConstraints gbc_lblNewLabel_6 = new GridBagConstraints();
		gbc_lblNewLabel_6.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_6.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_6.gridx = 1;
		gbc_lblNewLabel_6.gridy = 0;
		panel_1.add(lblNewLabel_6, gbc_lblNewLabel_6);

		data_ = new JTextField(2);
		data_.setDocument(new JHexField(2));
		data_.setText("00");

		data_.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				// Text inserted
				SwingUtilities.invokeLater(() -> {
					updateAccessBits();
				});

			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				// Text removed
				SwingUtilities.invokeLater(() -> {
//					updateAccessBits();

				});

			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				// Style change (not applicable for plain text components)
			}
		});

		GridBagConstraints gbc_data_ = new GridBagConstraints();
		gbc_data_.insets = new Insets(0, 0, 5, 5);
		gbc_data_.fill = GridBagConstraints.HORIZONTAL;
		gbc_data_.gridx = 2;
		gbc_data_.gridy = 0;
		panel_1.add(data_, gbc_data_);
		data_.setColumns(10);

		JLabel lblNewLabel_8 = new JLabel("Access Bit (Generated)");
		GridBagConstraints gbc_lblNewLabel_8 = new GridBagConstraints();
		gbc_lblNewLabel_8.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_8.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_8.gridx = 3;
		gbc_lblNewLabel_8.gridy = 0;
		panel_1.add(lblNewLabel_8, gbc_lblNewLabel_8);

		accessbits_ = new JTextField();
		GridBagConstraints gbc_accessbits_ = new GridBagConstraints();
		gbc_accessbits_.insets = new Insets(0, 0, 5, 0);
		gbc_accessbits_.fill = GridBagConstraints.HORIZONTAL;
		gbc_accessbits_.gridx = 4;
		gbc_accessbits_.gridy = 0;
		panel_1.add(accessbits_, gbc_accessbits_);
		accessbits_.setColumns(10);

		JLabel lblNewLabel_7 = new JLabel("Trailer Data");
		GridBagConstraints gbc_lblNewLabel_7 = new GridBagConstraints();
		gbc_lblNewLabel_7.gridwidth = 2;
		gbc_lblNewLabel_7.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_7.insets = new Insets(0, 0, 0, 5);
		gbc_lblNewLabel_7.gridx = 1;
		gbc_lblNewLabel_7.gridy = 1;
		panel_1.add(lblNewLabel_7, gbc_lblNewLabel_7);

		trailerbits_ = new JTextField();
		trailerbits_.setText("FFFFFFFFFFFF  FFFFFFFFFFFF");
		GridBagConstraints gbc_trailerbits_ = new GridBagConstraints();
		gbc_trailerbits_.gridwidth = 2;
		gbc_trailerbits_.fill = GridBagConstraints.HORIZONTAL;
		gbc_trailerbits_.gridx = 3;
		gbc_trailerbits_.gridy = 1;
		panel_1.add(trailerbits_, gbc_trailerbits_);
		trailerbits_.setColumns(10);
//		table.setRowSelectionInterval(0, 0); // select a row so that no null pointer occurs at fetching
//        table.setColumnSelectionInterval(0, 0);

		block0_.putClientProperty("selection", 0);
		block1_.putClientProperty("selection", 0);
		block2_.putClientProperty("selection", 0);
		trailer_.putClientProperty("selection", 0);

		loadSectorTrailerSettings(0);

//		setAlwaysOnTop(true); 
		this.pack();
		this.setLocationRelativeTo(parent);
	}
}

class JTextFieldLimit extends PlainDocument {
	private int limit;

	JTextFieldLimit(int limit) {
		super();
		this.limit = limit;
	}

	public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
		if (str == null) {
			return;
		}

		if ((getLength() + str.length()) <= limit) {
			super.insertString(offset, str, attr);
		}
	}

	public void replace(int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
		if (text != null) {
		}
		super.replace(offset, length, text, attrs);
	}

}

class JHexField extends PlainDocument {
	private int limit;

	JHexField(int limit) {
		super();
		this.limit = limit;
	}

	public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
		if (str == null) {
			return;
		}

		StringBuilder sb = new StringBuilder();
		for (char c : str.toCharArray()) {
			if (isHexChar(c) && (getLength() + sb.length()) < limit) {
				sb.append(Character.toUpperCase(c));
			}
		}

		super.insertString(offset, sb.toString(), attr);
	}

	public void replace(int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
		if (text != null) {
			StringBuilder sb = new StringBuilder();
			for (char c : text.toCharArray()) {
				if (isHexChar(c) && (getLength() + sb.length()) < limit) {
					sb.append(Character.toUpperCase(c));
				}
			}
			super.replace(offset, length, sb.toString(), attrs);
		}
	}

	private boolean isHexChar(char c) {
		return (c >= '0' && c <= '9') || (c >= 'A' && c <= 'F') || (c >= 'a' && c <= 'f');
	}
}

class AuthTableModel extends DefaultTableModel {

	private JTable table;

	public AuthTableModel(JTable table, Object[][] data, Object[] columnNames) {
		super(data, columnNames);
	}

	@Override
	public void setValueAt(Object aValue, int row, int column) {
		super.setValueAt(aValue, row, column);
		// Check if the row is selected
		try {
			table.getSelectedRow();
		} catch (Exception e) {
			// TODO: handle exception
			return;
		}
		if (row == table.getSelectedRow()) {
			// Set icon for the selected row
			// Here you would load the icon from a file or resource
			// For demonstration, I'm using an empty ImageIcon
			ImageIcon icon = new ImageIcon(NfcAppFrame.class.getResource("/jence/icon/checkbox16.png"));
			// Set icon for the selected row
			// Assuming the icon is set in the first column
			super.setValueAt(icon, row, 0);
		}
	}

}
