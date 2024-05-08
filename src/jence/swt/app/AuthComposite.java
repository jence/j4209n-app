/**
 * MIT LICENSE
 * 
 * Copyright � 2021 Jence, Ejaz Jamil.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation 
 * files (the �Software�), to deal in the Software without restriction, including without limitation the rights to use, copy, 
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the 
 * Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED �AS IS�, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE 
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR 
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR 
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE. 
 * 
 */
package jence.swt.app;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

import jence.jni.J4209N;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

/**
 * NDEF Write Dialog.
 * 
 * @author Ejaz Jamil
 * @version 1.0
 */
public class AuthComposite extends Composite {
	private Button btnWrite;
	private Label label_4;
	private Group grpReccordType_;
	private Composite composite_;
	private int selection_;
	private Callback callback_;
	private Table table_1;
	private TableColumn col1_;
	private TableColumn col2_;
	private TableColumn col3_;
	private TableItem tableItem;
	private TableItem tableItem_1;
	private TableItem tableItem_2;
	private TableItem tableItem_3;
	private TableItem tableItem_4;
	private TableItem tableItem_5;
	private TableColumn col0_;
	private TableItem tableItem_6;
	private TableItem tableItem_7;
	private Label lblKeyA;
	private Text asciiA_;
	private Label lblKeyB;
	private Text asciiB_;
	private Text hexA_;
	private Label lblHex;
	private Label lblAscii;
	private Text hexB_;
	private Label lblHex_1;
	private Label lblAscii_1;
	private Text trailerbits_;
	private Button btnSave;
	private Button btnLoad;
	private Label lblData;
	private Text data_;
	private Label lblAccessBitsgenerated;
	private Text accessbits_;
	private Button block0_;
	private Button block1_;
	private Button block2_;
	private Button trailer_;
	private Button btnDefault_A;
	private Button btnDefault_B;
	private Map auth_ = new Properties();
	private Button btnDefaultKey;
	private Composite composite;
	private Label lblTrailerData;
	private J4209N.KeyData key_ = new J4209N.KeyData(0);
	private Button btnUse;

	private byte[] hex2bytes(String hex, int arraySize) {
		if (hex.length() % 2 != 0)
			hex = "0" + hex; // make the length even;
		byte[] bytes = new byte[arraySize];
		for(int i=0;i<hex.length()/2;i++) {
			if (i == bytes.length)
				break;
			String h = hex.substring(i, i+2);
			int n = Integer.parseInt(h, 16);
			bytes[i] = (byte)n;
		}
		return bytes;
	}
	
	private void loadSectorTrailerSettings(int block) {
		table_1.removeAll();

		if (block == 3) {
			col0_.setText("Access Bits");
			col1_.setText("Key A");
			col2_.setText("Access Bits Property");
			col3_.setText("Key B");

			tableItem = new TableItem(table_1, SWT.NONE);
			tableItem.setText(new String[] { "000", "Write with Key A",
					"Read with Key A", "R/W with Key A" });

			tableItem_4 = new TableItem(table_1, SWT.NONE);
			tableItem_4.setText(new String[] { "001", "Write with Key A",
					"R/W with Key A", "R/W with Key A" });

			tableItem_1 = new TableItem(table_1, SWT.NONE);
			tableItem_1.setText(new String[] { "010", "Cannot R/W",
					"Read with Key A", "Read with Key A" });

			tableItem_5 = new TableItem(table_1, SWT.NONE);
			tableItem_5.setText(new String[] { "011", "Write with Key B",
					"Read with Key A, R/W with Key B", "Write with Key B" });

			tableItem_2 = new TableItem(table_1, SWT.NONE);
			tableItem_2.setText(new String[] { "100", "Write with Key B",
					"Read with Key A or B", "Write with Key B" });

			tableItem_6 = new TableItem(table_1, SWT.NONE);
			tableItem_6.setText(new String[] { "101", "Cannot R/W",
					"Read with Key A, R/W with Key B", "Cannot R/W" });

			tableItem_3 = new TableItem(table_1, SWT.NONE);
			tableItem_3.setText(new String[] { "110", "Cannot R/W",
					"Read with Key A or B", "Cannot R/W" });

			tableItem_7 = new TableItem(table_1, SWT.NONE);
			tableItem_7.setText(new String[] { "111", "Cannot R/W",
					"Read with Key A or B", "Cannot R/W" });
		} else {
			col0_.setText("Access Bits");
			col1_.setText("Access Condition");
			col2_.setText("Increment");
			col3_.setText("Decr, Transfer, Restore");

			tableItem = new TableItem(table_1, SWT.NONE);
			tableItem.setText(new String[] { "000", "R/W with Key A | B",
					"Key A | B", "Key A | B" });

			tableItem_2 = new TableItem(table_1, SWT.NONE);
			tableItem_2.setText(new String[] { "001", "Read with Key A | B",
					"-", "Key A | B" });

			tableItem_4 = new TableItem(table_1, SWT.NONE);
			tableItem_4.setText(new String[] { "010", "Read with Key A | B",
					"-", "-" });

			tableItem_6 = new TableItem(table_1, SWT.NONE);
			tableItem_6.setText(new String[] { "011", "R/W with Key B", "-",
					"-" });

			tableItem_1 = new TableItem(table_1, SWT.NONE);
			tableItem_1.setText(new String[] { "100",
					"R/W with Key B, Read with Key A", "-", "-" });

			tableItem_3 = new TableItem(table_1, SWT.NONE);
			tableItem_3.setText(new String[] { "101", "Read with Key B", "-",
					"-" });

			tableItem_5 = new TableItem(table_1, SWT.NONE);
			tableItem_5.setText(new String[] { "110",
					"R/W with Key B, Read with Key A", "Key B", "Key A | B" });

			tableItem_7 = new TableItem(table_1, SWT.NONE);
			tableItem_7.setText(new String[] { "111", "-", "-", "-" });
		}

		loadSelection();
		table_1.getSelection()[0].setImage(SWTResourceManager.getImage(
				NfcAppComposite.class, "/jence/icon/checkbox16.png"));
		updateAccessBits();
	}

	private byte[] generateAccessBits(byte block0, byte block1, byte block2,
			byte block3, int data) {
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

		byte[] accessbits = { (byte) byte6, (byte) byte7, (byte) byte8,
				(byte) byte9 };

		return accessbits;
	}

	private void saveFile() {
		FileDialog fd = new FileDialog(this.getShell(), SWT.SAVE);
		fd.setText("Save");
		fd.setFilterPath("");
		String[] filterExt = { "*.j4210n", "*.*" };
		fd.setFilterExtensions(filterExt);
		String filename = fd.open();
		// System.out.println(selected);
		String warning = " IMPORTANT: Keep this file in a safe "
				+ "place and do not\n distribute. The file contains sensitive password information.";
		try {
			auth_.clear();
			auth_.put("type", NfcApp.driver_.type().name());
			auth_.put("trailer", trailerbits_.getText());
			((Properties) auth_).store(new FileOutputStream(filename), warning);
		} catch (Exception e) {
			e.printStackTrace();
			NfcApp.prompt(this.getShell(), e.getLocalizedMessage(),
					SWT.ICON_WARNING | SWT.OK);
		}
	}

	private Map loadFile() {
		FileDialog fd = new FileDialog(this.getShell(), SWT.OPEN);
		fd.setText("Open");
		fd.setFilterPath("");
		String[] filterExt = { "*.j4210n", "*.*" };
		fd.setFilterExtensions(filterExt);
		String selected = fd.open();
		System.out.println(selected);
		Properties p = new Properties();
		try {
			p.clear();
			p.load(new FileInputStream(selected));
			// verify if the card type match
			String type = p.getProperty("type");
			if (!type.equalsIgnoreCase(NfcApp.driver_.type().name())) {
				NfcApp.prompt(this.getShell(), "The tag under scan is of type "
						+ NfcApp.driver_.type() + " but the Auth is for type "
						+ type + ".", SWT.ICON_WARNING | SWT.OK);
				return null;
			}
			return p;
		} catch (Exception e) {
			NfcApp.prompt(this.getShell(), e.getLocalizedMessage(),
					SWT.ICON_WARNING | SWT.OK);
		}
		return null;
	}

	private void updateAccessBits() {
		if (data_.getText().trim().length() == 0)
			data_.setText("00");
		int data = Integer.parseInt(data_.getText(), 16);

		byte b0 = (byte) ((Integer) block0_.getData()).byteValue();
		byte b1 = (byte) ((Integer) block1_.getData()).byteValue();
		byte b2 = (byte) ((Integer) block2_.getData()).byteValue();
		byte b3 = (byte) ((Integer) trailer_.getData()).byteValue();

		byte[] accessbits = generateAccessBits(b0, b1, b2, b3, data);
		String z = "";
		for (int i = 0; i < accessbits.length; i++) {
			z += String.format("%02X", accessbits[i]);
		}
		accessbits_.setText(z);

		trailerbits_.setText(hexA_.getText() + " " + accessbits_.getText()
				+ " " + hexB_.getText());

		key_ = new J4209N.KeyData(b3);
		key_.KeyA = hex2bytes(hexA_.getText(), 6);
		key_.KeyB = hex2bytes(hexB_.getText(), 6);

	}

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

	private void loadSelection() {
		int selection = 0;
		if (block0_.getSelection()) {
			selection = (Integer) block0_.getData();
		} else if (block1_.getSelection()) {
			selection = (Integer) block1_.getData();
		} else if (block2_.getSelection()) {
			selection = (Integer) block2_.getData();
		} else if (trailer_.getSelection()) {
			selection = (Integer) trailer_.getData();
		}
		table_1.setSelection(selection);
	}

	private void saveSelection() {
		int selection = table_1.getSelectionIndex();
		if (selection == -1) {
			table_1.setSelection(0);
		}
		if (block0_.getSelection()) {
			block0_.setData(selection);
		} else if (block1_.getSelection()) {
			block1_.setData(selection);
		} else if (block2_.getSelection()) {
			block2_.setData(selection);
		} else if (trailer_.getSelection()) {
			trailer_.setData(selection);
		}
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
		
		updateRadioFromAccessBits(accessbits);
	}

	private void updateRadioFromAccessBits(String accessbits) {
		BigInteger bi = new BigInteger(accessbits, 16);
		// b0 = 100, b1 = 101, b2 = 110, b3 = 111
		byte[] b = bi.toByteArray();
		// 1111 1010 1100
		// int byte6 = b[3] & 0xFF;
		int byte7 = b[1] & 0xFF; // 0xF0;
		int byte8 = b[2] & 0xFF; // 0xAC;

		int c1 = (byte7) >> 4;
		int c2 = (byte8 & 0x0F);
		int c3 = (byte8 >> 4);

		int block0 = ((c1 & 0x01) << 2) | ((c2 & 0x01) << 1) | (c3 & 0x01);
		int block1 = ((c1 & 0x02) << 1) | ((c2 & 0x02) << 0)
				| ((c3 & 0x02) >> 1);
		int block2 = ((c1 & 0x04) << 0) | ((c2 & 0x04) >> 1)
				| ((c3 & 0x04) >> 2);
		int trailer = ((c1 & 0x08) >> 1) | ((c2 & 0x08) >> 2)
				| ((c3 & 0x08) >> 3);

		block0_.setData(new Integer(block0));
		block1_.setData(new Integer(block1));
		block2_.setData(new Integer(block2));
		trailer_.setData(new Integer(trailer));

		loadSectorTrailerSettings(0);
	}

	public AuthComposite(Composite arg0, int arg1) {
		super(arg0, arg1);
		composite_ = this;
		composite_.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));
		composite_.setLayout(new GridLayout(6, false));

		lblKeyA = new Label(this, SWT.NONE);
		lblKeyA.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false,
				1, 1));
		lblKeyA.setText("Key A");

		lblAscii = new Label(this, SWT.NONE);
		lblAscii.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		lblAscii.setText("ASCII");

		asciiA_ = new Text(this, SWT.BORDER);
		asciiA_.setToolTipText("Type any ASCII character of length 6");
		asciiA_.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));
		asciiA_.setTextLimit(6);
		asciiA_.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent arg0) {
				hexA_.setText(ascii2hex(asciiA_.getText()));
				updateAccessBits();
			}
		});

		lblHex = new Label(this, SWT.NONE);
		lblHex.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false,
				1, 1));
		lblHex.setText("Hex");

		hexA_ = new Text(this, SWT.BORDER);
		hexA_.setText("FFFFFFFFFFFF");
		hexA_.setToolTipText("write an HEX character.");
		hexA_.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1,
				1));
		NfcApp.hexKeyListener(hexA_);
		hexA_.setTextLimit(12);
		hexA_.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent arg0) {
				updateAccessBits();
			}
		});

		btnDefault_A = new Button(this, SWT.NONE);
		btnDefault_A.setImage(SWTResourceManager.getImage(AuthComposite.class,
				"/jence/icon/default16.png"));
		btnDefault_A.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				hexA_.setText("FFFFFFFFFFFF");
				updateAccessBits();
			}
		});
		btnDefault_A.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));
		btnDefault_A.setText("Default");

		lblKeyB = new Label(this, SWT.NONE);
		lblKeyB.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false,
				1, 1));
		lblKeyB.setText("Key B");

		lblAscii_1 = new Label(this, SWT.NONE);
		lblAscii_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		lblAscii_1.setText("ASCII");

		asciiB_ = new Text(this, SWT.BORDER);
		asciiB_.setToolTipText("Type any ASCII character of length 6");
		asciiB_.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));
		asciiB_.setTextLimit(6);
		asciiB_.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent arg0) {
				String hex = "";
				String keyB = asciiB_.getText();
				if (keyB.length() == 0) {
					hex = "FFFFFFFFFFFF";
				} else {
					hex = ascii2hex(keyB);
				}
				hexB_.setText(hex);
			}
		});

		lblHex_1 = new Label(this, SWT.NONE);
		lblHex_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		lblHex_1.setText("Hex");

		hexB_ = new Text(this, SWT.BORDER);
		hexB_.setText("FFFFFFFFFFFF");
		hexB_.setToolTipText("write an HEX character.");
		hexB_.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1,
				1));
		NfcApp.hexKeyListener(hexB_);
		hexB_.setTextLimit(12);
		hexB_.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent arg0) {
				updateAccessBits();
			}
		});

		btnDefault_B = new Button(this, SWT.NONE);
		btnDefault_B.setImage(SWTResourceManager.getImage(AuthComposite.class,
				"/jence/icon/default16.png"));
		btnDefault_B.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				hexB_.setText("FFFFFFFFFFFF");
				updateAccessBits();
			}
		});
		btnDefault_B.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));
		btnDefault_B.setText("Default");

		table_1 = new Table(this, SWT.BORDER | SWT.FULL_SELECTION);
		table_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 6, 1));
		table_1.setHeaderVisible(true);
		table_1.setLinesVisible(true);
		table_1.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {

			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				for (int i = 0; i < table_1.getItemCount(); i++) {
					table_1.getItem(i).setImage((Image) null);
				}
				TableItem item = (TableItem) arg0.item;

				item.setImage(SWTResourceManager.getImage(
						NfcAppComposite.class, "/jence/icon/checkbox16.png"));
				arg0.doit = true;
				saveSelection();
				updateAccessBits();
			}
		});

		col0_ = new TableColumn(table_1, SWT.NONE);
		col0_.setWidth(70);
		col0_.setText("Access Bits");

		col1_ = new TableColumn(table_1, SWT.NONE);
		col1_.setWidth(131);
		col1_.setText("Key A");

		col2_ = new TableColumn(table_1, SWT.NONE);
		col2_.setWidth(203);
		col2_.setText("Access Bits Property");

		col3_ = new TableColumn(table_1, SWT.NONE);
		col3_.setWidth(130);
		col3_.setText("Key B");

		grpReccordType_ = new Group(this, SWT.NONE);
		grpReccordType_.setLayout(new GridLayout(2, false));
		grpReccordType_.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false,
				false, 3, 1));
		grpReccordType_.setText("Blocks");

		block0_ = new Button(grpReccordType_, SWT.RADIO);
		block0_.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false,
				1, 1));
		block0_.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				loadSectorTrailerSettings(0);
			}
		});
		block0_.setSelection(true);
		block0_.setText("Block 0");

		block2_ = new Button(grpReccordType_, SWT.RADIO);
		block2_.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				loadSectorTrailerSettings(2);
			}
		});
		block2_.setText("Block 2");

		block1_ = new Button(grpReccordType_, SWT.RADIO);
		block1_.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				loadSectorTrailerSettings(1);
			}
		});
		block1_.setText("Block 1");

		trailer_ = new Button(grpReccordType_, SWT.RADIO);
		trailer_.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				loadSectorTrailerSettings(3);
			}
		});
		trailer_.setText("Block 3 (Sector Trailer)");

		block0_.setData(0);
		block1_.setData(0);
		block2_.setData(0);
		trailer_.setData(0);

		composite = new Composite(this, SWT.NONE);
		composite.setLayout(new GridLayout(5, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 3, 1));

		btnSave = new Button(composite, SWT.NONE);
		btnSave.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				saveFile();
			}
		});
		btnSave.setImage(SWTResourceManager.getImage(AuthComposite.class,
				"/jence/icon/save.png"));
		btnSave.setText("Save");

		btnLoad = new Button(composite, SWT.NONE);
		btnLoad.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				Map map = loadFile();
				if (map != null) {
					auth_ = map;
					String sectors = auth_.get("sectors").toString();
					int s = Integer.parseInt(sectors);
					String trailer = "";
					trailer = (String) auth_.get("sector0".toString());
					parseSectorTrailer(trailer);
				}
			}
		});
		btnLoad.setImage(SWTResourceManager.getImage(AuthComposite.class,
				"/jence/icon/load.png"));
		btnLoad.setText("Load");
		
		btnUse = new Button(composite, SWT.NONE);
		btnUse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				useKey();
			}
		});
		btnUse.setToolTipText("Use these keys for all subsequent read/write operations.");
		btnUse.setImage(SWTResourceManager.getImage(AuthComposite.class, "/jence/icon/cardread.png"));
		btnUse.setText("Use");

		btnWrite = new Button(composite, SWT.NONE);
		btnWrite.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				writeAuth(false);
			}
		});
		btnWrite.setImage(SWTResourceManager.getImage(AuthComposite.class,
				"/jence/icon/write.png"));
		btnWrite.setToolTipText("Write NDEF data.");
		btnWrite.setText("Write");
		
				btnDefaultKey = new Button(composite, SWT.NONE);
				btnDefaultKey.setToolTipText("This operation will remove the keys programed before and replace with the default key setting.");
				btnDefaultKey.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent arg0) {
						writeAuth(true);
					}
				});
				btnDefaultKey.setImage(SWTResourceManager.getImage(AuthComposite.class,
						"/jence/icon/default.png"));
				btnDefaultKey.setText("Default");

		label_4 = new Label(this, SWT.BORDER | SWT.SEPARATOR | SWT.HORIZONTAL);
		label_4.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false,
				6, 1));
		new Label(this, SWT.NONE);
		new Label(this, SWT.NONE);

		lblData = new Label(this, SWT.NONE);
		lblData.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false,
				1, 1));
		lblData.setText("Data");

		data_ = new Text(this, SWT.BORDER);
		data_.setText("00");
		data_.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1,
				1));
		data_.setTextLimit(2);
		NfcApp.hexKeyListener(data_);

		lblAccessBitsgenerated = new Label(this, SWT.NONE);
		lblAccessBitsgenerated.setLayoutData(new GridData(SWT.RIGHT,
				SWT.CENTER, false, false, 1, 1));
		lblAccessBitsgenerated.setText("Access Bits (Generated)");

		accessbits_ = new Text(this, SWT.BORDER | SWT.READ_ONLY);
		accessbits_.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		new Label(this, SWT.NONE);
		new Label(this, SWT.NONE);
		new Label(this, SWT.NONE);

		lblTrailerData = new Label(this, SWT.NONE);
		lblTrailerData.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		lblTrailerData.setText("Trailer Data");

		trailerbits_ = new Text(this, SWT.BORDER | SWT.READ_ONLY);
		trailerbits_.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 2, 1));
		loadSectorTrailerSettings(0);

//		this.getShell().pack();
	}

	public void callback(Callback runnable) {
		callback_ = runnable;
	}

	private void writeAuth(boolean defaultKwy) {
		String warning = "You are about to change authentication by changing keys and access conditions.";
		warning += " This will apply to all the sectors of this tag.";
		warning += " Some access conditions may make the tag unreadable. Are you sure you want to perform this operation?";
		if (NfcApp.prompt(this.getShell(), warning, SWT.OK | SWT.ICON_WARNING
				| SWT.CANCEL) == SWT.CANCEL) {
			return;
		}
		
		int block;
		String hexdata = hexA_.getText() + accessbits_.getText()
				+ hexB_.getText();
		if (defaultKwy) {
			hexdata = "FFFFFFFFFFFF"+"FF078069"+"FFFFFFFFFFFF"; // This is the default key of sector trailer of a new tag (MIFARE CLASSIC)
		}
		BigInteger bi = new BigInteger(hexdata, 16);
		byte[] data = bi.toByteArray();
		try {
			String totalSectors = (String) auth_.get("sectors");
			int n = 0;
			try {
				n = Integer.parseInt(totalSectors);
			} catch (Throwable t) {
				NfcApp.prompt(this.getShell(), t.getLocalizedMessage()
						+ ". Sector information could not be loaded.",
						SWT.OK | SWT.ICON_WARNING);
				return;
			}
			for (int i = 0; i < n; i++) {
				byte[] rdata = NfcApp.driver_.read(i*4, false);
				if (rdata == null) {
					// authentication failed.
					throw new Exception("Authentication Failed.");
				}
				block = i * 4 + 3;
				NfcApp.driver_.write(block, data, false);
			}
			System.out.println("data = " + hexdata);
			NfcApp.prompt(this.getShell(),
					"Successfully wrote sector trailer.", SWT.OK);
		} catch (Exception e) {
			NfcApp.prompt(this.getShell(), e.getLocalizedMessage(), SWT.OK
					| SWT.ICON_WARNING);
		}
	}
	
	private void useKey() {
		if (NfcApp.prompt(this.getShell(), "Use have choosen to use this key for subsequent read and write. " +
				"If your tag does not use this key, then you should write this key to the tag first before " +
				"subsequent read or write. ", SWT.OK | SWT.CANCEL) == SWT.OK) {
			try {
				NfcApp.driver_.keys(key_.KeyA, key_.KeyB);
				this.getShell().dispose();
			} catch (Exception e) {
				NfcApp.prompt(this.getShell(), e.getLocalizedMessage(), SWT.OK | SWT.ICON_WARNING);
			}
		}
	}
}
