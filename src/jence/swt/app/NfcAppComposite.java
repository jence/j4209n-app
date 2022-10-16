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

import java.io.FileOutputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import jence.jni.J4209N;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

/**
 * The application front end.
 * 
 * 
 * @author Ejaz Jamil
 * @version 1.0
 * 
 */
public class NfcAppComposite extends Composite {
	private Table table_;
	private Text name_;
	private Text uid_;
	private Combo comboPorts_;
	private Button btnConnect_;
	private Button btnDisconnect_;
	private Button btnRefresh_;
	private Button btnScan_;
	private Label lblStatus_;
	private Label lblBlocks;
	private Text textBlocks_;
	private Label lblBlockSizebyte;
	private Text textBlockSize_;
	private Group grpNdef;
	private Button btnNdefFormat_;
	private Button btnNdefRead_;
	private Button btnNdefWrite_;
	private Button btnNdefErase_;
	private Button btnNdefClean_;
	private Label lblNdef;
	private Text textNDEF_;
	private Label lblAuth;
	private Text textAuth_;
	private Button btnWrite_;
	private Composite composite_1;
	private Button btnAuth_;
	private TabFolder tabFolder;
	private TabItem tbtmRawData;
	private TabItem tbtmRaw;
	private Composite composite_2;
	private Table ndeftable_;
	private Composite composite_4;
	private TableColumn tblclmnId;
	private Label lblNewLabel;
	private TableColumn tblclmnType_;
	private Listener listener_;
	private TabItem tbtmEmulate;
	private Composite composite_5;
	private J4209N.KeyData keydata_ = new J4209N.KeyData(0); // full access
	private Label lblUid_1;
	private Text emulationUid_;
	private Button btnStart;
	private Button btnStop;
	private Label lblTimeout;
	private Spinner timeout_;
	private Button btnApply;
	private Button btnWritable_;
	private Composite composite_6;
	private Label lblTime;
	private Text elapsed_;
	private Timer timer_ = null;

	public static final String DOWNLOAD_PAGE = "https://jence.com/web/index.php?route=product/product&path=69_20_232&product_id=793";
	public static final String LATEST_VERSION_PAGE = "http://jence.com/downloads/version.properties";
	private Button btnSave_;

	private int prompt(String text, int style) {
		return NfcApp.prompt(this.getShell(), text, style);
	}

	private void status(String text) {
		lblStatus_.setText(text);
	}

	private void setEnabled(boolean state, Control... w) {
		for (int i = 0; i < w.length; i++) {
			w[i].setEnabled(state);
		}
	}

	private void checkVersion() {
		try {
			URL url = new URL(LATEST_VERSION_PAGE);
			URLConnection con = url.openConnection();
			InputStream stream = con.getInputStream();
			Properties properties = new Properties();
			properties.load(stream);
			String version = properties.getProperty("J4210N");
			if (version.compareTo(NfcApp.VERSION) > 0) {
				if (NfcApp
						.prompt(getShell(),
								"New version "
										+ version
										+ " found. You can download the latest version by clicking the OK button.",
								SWT.OK | SWT.CANCEL) == SWT.OK) {
					java.awt.Desktop.getDesktop()
							.browse(new URI(DOWNLOAD_PAGE));
				}
			}
			//System.out.println(properties);
			stream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void createEditableTable(final Table table) {
		final Color COLOR_ORANGE = Display.getDefault().getSystemColor(
				SWT.COLOR_GRAY);
		FontData fd = new FontData();
		fd.setStyle(SWT.BOLD);
		final Font FONT_BOLD = new Font(Display.getDefault(), fd);
		final TableEditor editor = new TableEditor(table);
		editor.horizontalAlignment = SWT.LEFT;
		editor.grabHorizontal = true;
		if (listener_ != null)
			table.removeListener(SWT.MouseDown, listener_);
		table.addListener(SWT.MouseDown, listener_ = new Listener() {
			public void handleEvent(Event event) {
				Rectangle clientArea = table.getClientArea();
				Point pt = new Point(event.x, event.y);
				int index = table.getTopIndex();
				while (index < table.getItemCount()) {
					boolean visible = false;
					final TableItem item = table.getItem(index);
					for (int i = 0; i < table.getColumnCount(); i++) {
						Rectangle rect = item.getBounds(i);
						if (rect.contains(pt)) {
							final int column = i;
							if (column < 2)
								continue; // thse are read only type
							// do not allow to edit sector trailer
							if (index % 4 == 3)
								continue;
							final Text text = new Text(table, SWT.CENTER);
							text.setBackground(COLOR_ORANGE);
							text.setTextLimit(2);
							NfcApp.hexKeyListener(text);
							// text.setFont(FONT_BOLD);
							Listener textListener = new Listener() {
								public void handleEvent(final Event e) {
									switch (e.type) {
									case SWT.FocusOut:
										String oldText = item.getText(column);
										String newText = text.getText();
										if (newText.length() == 0) {
											newText = oldText;
										}
										if (newText.length() == 1)
											newText = "0" + newText;
										item.setText(column, newText);
										if (!oldText.equalsIgnoreCase(newText)) {
											item.setData(column + "",
													oldText.toUpperCase());
											item.setFont(column, FONT_BOLD);
										}
										text.dispose();
										break;
									case SWT.Traverse:
										switch (e.detail) {
										case SWT.TRAVERSE_RETURN:
											item.setText(column, text.getText());
											// FALL THROUGH
										case SWT.TRAVERSE_ESCAPE:
											text.dispose();
											e.doit = false;
										}
										break;
									}
								}
							};
							text.addListener(SWT.FocusOut, textListener);
							text.addListener(SWT.Traverse, textListener);
							editor.setEditor(text, item, i);
							text.setText(item.getText(i));
							text.selectAll();
							text.setFocus();
							return;
						}
						if (!visible && rect.intersects(clientArea)) {
							visible = true;
						}
					}
					if (!visible)
						return;
					index++;
				}
			}
		});
	}

	public NfcAppComposite(Composite arg0, int arg1) {
		super(arg0, arg1);
		Composite composite = this;
		composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));
		composite.setLayout(new GridLayout(4, false));

		composite_1 = new Composite(this, SWT.NONE);
		composite_1.setLayout(new GridLayout(6, false));
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 3, 1));

		Label lblPort = new Label(composite_1, SWT.NONE);
		lblPort.setImage(SWTResourceManager.getImage(NfcAppComposite.class,
				"/jence/icon/usb.png"));
		lblPort.setText("Port");

		comboPorts_ = new Combo(composite_1, SWT.READ_ONLY);

		btnRefresh_ = new Button(composite_1, SWT.NONE);
		btnRefresh_.setImage(SWTResourceManager.getImage(NfcAppComposite.class,
				"/jence/icon/usb.png"));
		btnRefresh_.setToolTipText("Refresh available serial ports.");
		btnRefresh_.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				if (portlist()) {
					btnConnect_.setEnabled(true);
					status("Completed listing available ports.");
				}
			}
		});
		btnRefresh_.setText("Refresh");

		btnConnect_ = new Button(composite_1, SWT.NONE);
		btnConnect_.setImage(SWTResourceManager.getImage(NfcAppComposite.class,
				"/jence/icon/connect.png"));
		btnConnect_.setEnabled(false);
		btnConnect_.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				if (connect()) {
					setEnabled(true, btnDisconnect_, btnScan_);
					setEnabled(false, btnRefresh_, btnConnect_);
					status("Connection was successful.");
				}
			}
		});
		btnConnect_.setText("Connect");

		btnDisconnect_ = new Button(composite_1, SWT.NONE);
		btnDisconnect_.setImage(SWTResourceManager.getImage(
				NfcAppComposite.class, "/jence/icon/disconnect.png"));
		btnDisconnect_.setEnabled(false);
		btnDisconnect_.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				if (disconnect()) {
					setEnabled(true, btnRefresh_, btnConnect_);
					setEnabled(false, btnDisconnect_, btnScan_);
					status("Disconnected.");
				}
			}
		});
		btnDisconnect_.setText("Disconnect");

		btnScan_ = new Button(composite_1, SWT.NONE);
		btnScan_.setImage(SWTResourceManager.getImage(NfcAppComposite.class,
				"/jence/icon/scan.png"));
		btnScan_.setEnabled(false);
		btnScan_.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				if (scan()) {
					// setEnabled(true,);
					status("Scan completed.");
				}
			}
		});
		btnScan_.setText("Scan");
		new Label(this, SWT.NONE);

		Composite composite_10 = new Composite(this, SWT.NONE);
		composite_10.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 3, 1));
		composite_10.setLayout(new GridLayout(10, false));

		Label lblUid = new Label(composite_10, SWT.NONE);
		lblUid.setText("UID");

		uid_ = new Text(composite_10, SWT.BORDER | SWT.READ_ONLY);
		uid_.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 5,
				1));
		uid_.setToolTipText("Unique ID of the tag.");

		lblNewLabel = new Label(composite_10, SWT.NONE);
		lblNewLabel.setSize(54, 15);
		lblNewLabel.setText("Card Type");

		name_ = new Text(composite_10, SWT.BORDER | SWT.READ_ONLY);
		name_.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1,
				1));
		name_.setSize(212, 21);
		name_.setToolTipText("Type of detected tag.");

		lblNdef = new Label(composite_10, SWT.NONE);
		lblNdef.setText("Format");

		textNDEF_ = new Text(composite_10, SWT.BORDER | SWT.READ_ONLY);

		lblAuth = new Label(composite_10, SWT.NONE);
		lblAuth.setText("Auth Type");

		textAuth_ = new Text(composite_10, SWT.BORDER | SWT.READ_ONLY);
		textAuth_.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 2, 1));
		new Label(composite_10, SWT.NONE);
		new Label(composite_10, SWT.NONE);

		lblBlocks = new Label(composite_10, SWT.NONE);
		lblBlocks.setText("Blocks");

		textBlocks_ = new Text(composite_10, SWT.BORDER | SWT.READ_ONLY);
		textBlocks_.setToolTipText("Number of Blocks or Pages in the tag.");
		new Label(composite_10, SWT.NONE);

		lblBlockSizebyte = new Label(composite_10, SWT.NONE);
		lblBlockSizebyte.setText("Block Size (byte) ");

		textBlockSize_ = new Text(composite_10, SWT.BORDER | SWT.READ_ONLY);
		textBlockSize_.setToolTipText("Number of bytes in a block or page.");
		new Label(this, SWT.NONE);

		tabFolder = new TabFolder(this, SWT.NONE);
		tabFolder.setSelection(0);
		tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3,
				1));

		tbtmRaw = new TabItem(tabFolder, SWT.NONE);
		tbtmRaw.setText("RAW");

		Composite composite_3 = new Composite(tabFolder, SWT.NONE);
		tbtmRaw.setControl(composite_3);
		composite_3.setLayout(new GridLayout(3, true));

		composite_4 = new Composite(composite_3, SWT.NONE);
		composite_4.setLayout(new GridLayout(2, false));
		composite_4.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false,
				3, 1));

		btnWrite_ = new Button(composite_4, SWT.NONE);
		btnWrite_.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				rawWrite();
			}
		});
		btnWrite_.setToolTipText("Select a row to Write raw data.");
		btnWrite_.setImage(SWTResourceManager.getImage(NfcAppComposite.class,
				"/jence/icon/cardwrite.png"));
		btnWrite_.setText("Raw Write");

		btnAuth_ = new Button(composite_4, SWT.NONE);
		btnAuth_.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				openAuthDialog();
			}
		});
		btnAuth_.setToolTipText("Select a row to set Auth mode for the row. (MIFARE cards only)");
		btnAuth_.setImage(SWTResourceManager.getImage(NfcAppComposite.class,
				"/jence/icon/key.png"));
		btnAuth_.setText("Auth");

		table_ = new Table(composite_3, SWT.BORDER | SWT.MULTI);
		table_.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));
		table_.setLinesVisible(true);
		table_.setHeaderVisible(true);

		tbtmRawData = new TabItem(tabFolder, SWT.NONE);
		tbtmRawData.setText("NDEF");

		composite_2 = new Composite(tabFolder, SWT.NONE);
		tbtmRawData.setControl(composite_2);
		composite_2.setLayout(new GridLayout(1, false));

		grpNdef = new Group(composite_2, SWT.NONE);
		grpNdef.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false,
				1, 1));
		grpNdef.setText("NDEF");
		grpNdef.setLayout(new GridLayout(7, false));

		btnNdefFormat_ = new Button(grpNdef, SWT.NONE);
		btnNdefFormat_.setImage(SWTResourceManager.getImage(
				NfcAppComposite.class, "/jence/icon/format.png"));
		btnNdefFormat_.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				if (ndefFormat()) {
					prompt("Format complete.", SWT.OK);
				}
			}
		});
		btnNdefFormat_.setToolTipText("Formats a new tag with NDEF.");
		btnNdefFormat_.setText("Format");
		
				btnNdefClean_ = new Button(grpNdef, SWT.NONE);
				btnNdefClean_.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent arg0) {
						clean();
					}
				});
				btnNdefClean_.setImage(SWTResourceManager.getImage(
						NfcAppComposite.class, "/jence/icon/clean.png"));
				btnNdefClean_
						.setToolTipText("Clean NDEF and returns to default format.");
				btnNdefClean_.setText("Clean");
		
				btnNdefWrite_ = new Button(grpNdef, SWT.NONE);
				btnNdefWrite_.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent arg0) {
						openNdefWriteDialog();
					}
				});
				btnNdefWrite_.setImage(SWTResourceManager.getImage(
						NfcAppComposite.class, "/jence/icon/write.png"));
				btnNdefWrite_.setToolTipText("Writes an new NDEF record.");
				btnNdefWrite_.setText("Write");

		btnNdefRead_ = new Button(grpNdef, SWT.NONE);
		btnNdefRead_.setImage(SWTResourceManager.getImage(
				NfcAppComposite.class, "/jence/icon/read.png"));
		btnNdefRead_.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				readNDEF();
			}
		});
		btnNdefRead_.setToolTipText("Reads all NDEF records.");
		btnNdefRead_.setText("Read");
		new Label(grpNdef, SWT.NONE);
		
		btnSave_ = new Button(grpNdef, SWT.NONE);
		btnSave_.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				saveToFile();
			}
		});
		btnSave_.setImage(SWTResourceManager.getImage(NfcAppComposite.class, "/jence/icon/save.png"));
		btnSave_.setText("Save");
		
				btnNdefErase_ = new Button(grpNdef, SWT.NONE);
				btnNdefErase_.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent arg0) {
						ndefErase();
					}
				});
				btnNdefErase_.setImage(SWTResourceManager.getImage(
						NfcAppComposite.class, "/jence/icon/erase.png"));
				btnNdefErase_.setToolTipText("Erases all NDEF records.");
				btnNdefErase_.setText("Erase");

		ndeftable_ = new Table(composite_2, SWT.BORDER | SWT.FULL_SELECTION);
		ndeftable_.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true,
				1, 1));
		ndeftable_.setHeaderVisible(true);
		ndeftable_.setLinesVisible(true);

		TableColumn tblclmnIndex = new TableColumn(ndeftable_, SWT.NONE);
		tblclmnIndex.setWidth(100);
		tblclmnIndex.setText("Index");

		tblclmnId = new TableColumn(ndeftable_, SWT.NONE);
		tblclmnId.setWidth(100);
		tblclmnId.setText("ID");

		tblclmnType_ = new TableColumn(ndeftable_, SWT.NONE);
		tblclmnType_.setWidth(100);
		tblclmnType_.setText("Type");

		TableColumn tblclmnEncoding = new TableColumn(ndeftable_, SWT.NONE);
		tblclmnEncoding.setWidth(100);
		tblclmnEncoding.setText("Encoding");

		TableColumn tblclmnData = new TableColumn(ndeftable_, SWT.NONE);
		tblclmnData.setWidth(391);
		tblclmnData.setText("Data");

		tbtmEmulate = new TabItem(tabFolder, SWT.NONE);
		tbtmEmulate.setText("EMULATE");

		composite_5 = new Composite(tabFolder, SWT.NONE);
		tbtmEmulate.setControl(composite_5);
		composite_5.setLayout(new GridLayout(8, false));

		lblUid_1 = new Label(composite_5, SWT.NONE);
		lblUid_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		lblUid_1.setText("UID");

		emulationUid_ = new Text(composite_5, SWT.BORDER);
		emulationUid_.setText("12BA89");
		emulationUid_.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true,
				false, 1, 1));
		emulationUid_.setTextLimit(6);
		NfcApp.hexKeyListener(emulationUid_);

		lblTimeout = new Label(composite_5, SWT.NONE);
		lblTimeout.setText("Timeout (sec)");

		timeout_ = new Spinner(composite_5, SWT.BORDER);
		timeout_.setMaximum(3600);
		timeout_.setSelection(60);

		btnWritable_ = new Button(composite_5, SWT.CHECK);
		btnWritable_.setText("Writeable");

		btnApply = new Button(composite_5, SWT.NONE);
		btnApply.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				initEmulation();
			}
		});
		btnApply.setImage(SWTResourceManager.getImage(NfcAppComposite.class,
				"/jence/icon/cardwrite.png"));
		btnApply.setToolTipText("Initializes emulation.");
		btnApply.setText("Apply");

		btnStart = new Button(composite_5, SWT.NONE);
		btnStart.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				startEmulation();
			}
		});
		btnStart.setImage(SWTResourceManager.getImage(NfcAppComposite.class,
				"/jence/icon/emulate.png"));
		btnStart.setText("Start");

		btnStop = new Button(composite_5, SWT.NONE);
		btnStop.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				stopEmulation();
			}
		});
		btnStop.setImage(SWTResourceManager.getImage(NfcAppComposite.class,
				"/jence/icon/stop.png"));
		btnStop.setText("Stop");

		lblTime = new Label(composite_5, SWT.NONE);
		lblTime.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false,
				1, 1));
		lblTime.setText("Time");

		elapsed_ = new Text(composite_5, SWT.BORDER);
		elapsed_.setText("0");
		elapsed_.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));
		new Label(composite_5, SWT.NONE);
		new Label(composite_5, SWT.NONE);
		new Label(composite_5, SWT.NONE);
		new Label(composite_5, SWT.NONE);
		new Label(composite_5, SWT.NONE);
		new Label(composite_5, SWT.NONE);
		new Label(this, SWT.NONE);

		composite_6 = new Composite(this, SWT.NONE);
		composite_6.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 3, 1));
		composite_6.setLayout(new GridLayout(3, false));

		lblStatus_ = new Label(composite_6, SWT.NONE);
		lblStatus_.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 2, 1));
		lblStatus_.setSize(0, 15);

		Label lblVersion_ = new Label(composite_6, SWT.NONE);
		lblVersion_.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		lblVersion_.setBounds(0, 0, 55, 15);
		lblVersion_.setText("Library Version : "+NfcApp.driver_.LibraryVersion()+" | "+"Version: " + NfcApp.VERSION);
		new Label(this, SWT.NONE);

		// this.pack();
		checkVersion();
	}

	private void stopEmulation() {
		try {
			NfcApp.driver_.emulateStop();
			timer_ = null;
		} catch (Exception e) {
			NfcApp.prompt(NfcAppComposite.this.getShell(),
					e.getLocalizedMessage(), SWT.OK | SWT.ICON_WARNING);
		}
	}

	private void emulationResponse(int index, String msg) {
		if (index == 1) {
			NfcApp.prompt(this.getShell(),
					"Received NDEF write in Card Emulation.", SWT.OK);
		}
		timer_.cancel();
		timer_ = null;
	}

	private void startEmulation() {
		try {
			timer_ = new Timer();
			timer_.schedule(new TimerTask() {
				final int seconds[] = { 0 };

				@Override
				public void run() {
					NfcAppComposite.this.getDisplay().asyncExec(new Runnable() {

						@Override
						public void run() {
							elapsed_.setText("" + seconds[0]++);
						}
					});
				}
			}, 0, 1000);
			int timeouts = Integer.parseInt(timeout_.getText());
			NfcApp.driver_.emulateStart(timeouts * 1000, new Callback() {

				@Override
				public void callback(int option, int index, String text) throws Exception {
					emulationResponse(index, text);
				}
			});
		} catch (Exception e) {
			NfcApp.prompt(this.getShell(), e.getLocalizedMessage(), SWT.OK
					| SWT.ICON_WARNING);
		}
	}

	private void initEmulation() {
		String uid = emulationUid_.getText();
		uid = "000000" + uid;
		uid = uid.substring(uid.length() - 6);
		emulationUid_.setText(uid);
		BigInteger bi = new BigInteger(uid, 16);
		int uidlen = bi.toByteArray().length;
		if (uidlen != 3) {
			NfcApp.prompt(
					this.getShell(),
					"Change the UID value so that the first byte is non zero. The total UID size must be 3 bytes.",
					SWT.OK | SWT.ICON_WARNING);
			return;
		}
		try {
			NfcApp.driver_.emulateInit(bi.toByteArray(),
					btnWritable_.getSelection());
		} catch (Exception e) {
			NfcApp.prompt(this.getShell(), e.getLocalizedMessage(), SWT.OK
					| SWT.ICON_WARNING);
		}
	}

	private boolean disconnect() {
		try {
			NfcApp.driver_.close();
			return true;
		} catch (Exception e) {
			prompt(e.getMessage(), SWT.ICON_WARNING);
		}
		return false;
	}

	private boolean connect() {
		try {
			NfcApp.driver_.open(comboPorts_.getText());
			return true;
		} catch (Exception e) {
			prompt(e.getMessage()
					+ " Could not connect to this port. Try another port.",
					SWT.ICON_WARNING);
		}
		return false;
	}

	private boolean scan() {
		try {
			tabFolder.setSelection(0);
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
			prompt(e.getMessage(), SWT.ICON_WARNING | SWT.OK);
		}
		return false;
	}

	private boolean portlist() {
		try {
			String[] ports = NfcApp.driver_.listPorts();
			comboPorts_.removeAll();
			for (int i = 0; i < ports.length; i++) {
				comboPorts_.add(ports[i]);
			}
			if (ports.length > 0) {
				comboPorts_.select(0);
			} else {
				return false;
			}
			return true;
		} catch (Exception e) {
			prompt(e.getMessage()
					+ " Please check if the device is attached to an USB port.",
					SWT.ICON_WARNING);
		}
		return false;
	}

	private boolean readNDEF() {
		try {
			ndeftable_.removeAll();
			NfcApp.driver_.sync();
			if (!NfcApp.driver_.isNDEF()) {
				prompt("No NDEF record found or the card may not be NDEF formatted.",
						SWT.ICON_WARNING);
				return false;
			}
			int records = NfcApp.driver_.ndefRead();
			if (records > 0) {
				ndeftable_.removeAll();
				for (int i = 0; i < records; i++) {
					J4209N.NdefRecord ndef = NfcApp.driver_.ndefGetRecord(i);
					TableItem item = new TableItem(ndeftable_,
							SWT.FULL_SELECTION | SWT.OK);
					item.setText(0, "" + i);
					item.setText(1, ndef.id);
					item.setText(2, ndef.type);
					item.setText(3, ndef.encoding);
					item.setText(4, new String(ndef.payload, "UTF-8"));
				}
				return true;
			} else {
				prompt("No NDEF records found.", SWT.OK);
				return false;
			}
		} catch (Exception e) {
			prompt(e.getMessage()
					+ " Please check if the device is attached to an USB port.",
					SWT.ICON_WARNING);
		}
		return false;
	}

	private boolean ndefFormat() {
		try {
			if (prompt(
					"All data in the current card will be erased. Are you sure?",
					SWT.OK | SWT.ICON_WARNING | SWT.CANCEL) == SWT.CANCEL) {
				return false;
			}
			NfcApp.driver_.ndefFormat();
			ndeftable_.removeAll();
			return true;
		} catch (Exception e) {
			prompt(e.getMessage(), SWT.ICON_WARNING);
		}
		return false;
	}

	private void dump() {
		try {
			// remove all rows and columns
			while (table_.getColumnCount() != 0) {
				table_.getColumn(0).dispose();
			}
			;
			table_.removeAll();

			byte[] data = null;
			NfcApp.driver_.keys(null, null); // use default keys of the card

			TableColumn clPage = new TableColumn(table_, SWT.NONE);
			clPage.setWidth(100);
			TableColumn clDesc = new TableColumn(table_, SWT.NONE);
			clDesc.setWidth(100);
			clDesc.setText("Description");

			switch (NfcApp.driver_.type()) {
			case ULTRALIGHT:
			case ULTRALIGHT_C:
			case ULTRALIGHT_EV1:
			case NTAG203:
			case NTAG213:
			case NTAG215:
			case NTAG216:
			case NTAG424:
				clPage.setText("Page");
			default:
				clPage.setText("Block");
			}

			for (int i = 0; i < NfcApp.driver_.blocksize(); i++) {
				TableColumn tableColumn = new TableColumn(table_, SWT.NONE);
				tableColumn.setWidth(50);
				tableColumn.setAlignment(SWT.CENTER);
				tableColumn.setText("" + i);
			}

			boolean ndef = false;
			// check if this tag is NDEF
			// block zero should work fine
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
			for (int i = 0; i < NfcApp.driver_.blockcount(); i++) {
				if (ndef)
					data = NfcApp.driver_.ndefRead(i);
				else
					data = NfcApp.driver_.read(i, false);

				TableItem item = new TableItem(table_, SWT.NONE);
				item.setText(0, "" + i);
				if (i == 0) { // first block is readonly
					item.setBackground(Display.getDefault().getSystemColor(
							SWT.COLOR_GRAY));
					item.setText(1, "Manufacturer Info");
				} else {
					switch (NfcApp.driver_.type()) {
					case MIFARE_CLASSIC_1K:
					case MIFARE_CLASSIC_4K:
						if (i % 4 == 3) {
							item.setBackground(Display.getDefault()
									.getSystemColor(SWT.COLOR_YELLOW));
							item.setText(1, "Auth Keys");
						} else {
							item.setText(1, "Data");
						}
					}
				}
				if (data != null) {
					// System.out.println("Block:"+i+"\t"+NfcApp.driver_.toHex(data));
					for (int j = 0; j < NfcApp.driver_.blocksize(); j++) {
						String v = String.format("%02X", data[j]);
						item.setText(j + 2, v);
					}
				} else {
					for (int j = 0; j < NfcApp.driver_.blockcount(); j++) {
						item.setText(j + 2, "-");
					}
				}
			}
			// table_.pack();
			// this.getShell().pack();
			switch (NfcApp.driver_.type()) {
			case MIFARE_CLASSIC_1K:
			case MIFARE_CLASSIC_4K:
				createEditableTable(table_);
				break;
			case ULTRALIGHT:
			case ULTRALIGHT_EV1:
			case ULTRALIGHT_C:
			case NTAG203:
			case NTAG213:
			case NTAG215:
			case NTAG216:
				createEditableTable(table_);
				break;
			default:
				if (listener_ != null)
					table_.removeListener(SWT.MouseDown, listener_);
				break;
			}
		} catch (Exception e) {
			prompt(e.getMessage(), SWT.ICON_WARNING);
		}
	}

	private boolean ndefErase() {
		try {
			if (!NfcApp.driver_.isNDEF()) {
				prompt("The tag is not NDEF formatted.", SWT.ICON_WARNING);
				return false;
			}
			if (prompt(
					"This operation erase all NDEF records. Do you want to proceed?",
					SWT.ICON_WARNING | SWT.OK | SWT.CANCEL) == SWT.CANCEL) {
				return false;
			}
			NfcApp.driver_.ndefErase();
		} catch (Exception e) {
			prompt(e.getMessage(), SWT.ICON_WARNING);
		}
		return false;
	}

	private boolean clean() {
		try {
			if (prompt(
					"This operation will reset all the data in the card. Do you want to proceed?",
					SWT.ICON_WARNING | SWT.OK | SWT.CANCEL) == SWT.CANCEL) {
				return false;
			}
			NfcApp.driver_.format();
			ndeftable_.removeAll();
			prompt("Clean operation completed. Verify by rescanning the card/tag.?",
					SWT.OK );
		} catch (Exception e) {
			prompt(e.getMessage(), SWT.ICON_WARNING);
		}
		return false;
	}

	private void openNdefWriteDialog() {
		Shell dialog = new Shell(this.getShell(), SWT.DIALOG_TRIM
				| SWT.APPLICATION_MODAL);
		NDEFComposite composite = new NDEFComposite(dialog, SWT.NONE);
		dialog.setLayout(new GridLayout(1, true));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		dialog.setBounds(new Rectangle(0, 0, 600, 400));

		composite.callback(new Callback() {
			@Override
			public void callback(int option, int selection, String text) throws Exception {
				if (option == 1) {
					if (NfcApp.driver_.isNDEF()) {
						NfcApp.driver_.format();
						try {
							// formatting the second time should fail
							NfcApp.driver_.format();
							status("FAILED to clean up the card. Please try again.");
							return;
						} catch(Exception e) {
							NfcApp.driver_.sync();
							NfcApp.driver_.ndefFormat();
						}
					} else {
						NfcApp.driver_.ndefFormat();
					}
				} else {
					if (!NfcApp.driver_.isNDEF()) {
						NfcApp.driver_.ndefFormat();
					}
				}
				switch (selection) {
				case 0:
					NfcApp.driver_.ndefAddUri("https://"+text);
					status("https written successfully.");
					break; // https
				case 1:
					NfcApp.driver_.ndefAddUri("http://"+text);
					status("http written successfully.");
					break; // http
				case 2:
					NfcApp.driver_.ndefAddText(text);
					status("text written successfully.");
					break; // text
				case 3:
					NfcApp.driver_.ndefAddUri("tel://"+text);
					status("tel written successfully.");
					break; // phone
				case 4:
					NfcApp.driver_.ndefAddUri("mailto://"+text);
					status("email written successfully.");
					break; // email
				}
			}
		});

		NfcApp.center(dialog);
		dialog.open();
	}

	private void openAuthDialog() {
		Shell dialog = new Shell(this.getShell(), SWT.DIALOG_TRIM
				| SWT.APPLICATION_MODAL);
		AuthComposite composite = new AuthComposite(dialog, SWT.NONE);
		dialog.setLayout(new GridLayout(1, true));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		dialog.setBounds(new Rectangle(0, 0, 600, 400));

		composite.callback(new Callback() {
			@Override
			public void callback(int option, int selection, String text) throws Exception {
			}
		});

		NfcApp.center(dialog);
		dialog.open();
	}

	private void rawWrite() {
		byte[] data;
		try {
			data = new byte[NfcApp.driver_.blocksize()];
			int[] range = NfcApp.driver_.usrmem();
			for (int i = range[0]; i < range[1]; i++) {
				switch (NfcApp.driver_.type()) {
				case MIFARE_CLASSIC_1K:
				case MIFARE_CLASSIC_4K:
					if (i % 4 == 3)
						continue; // trailer block is not user memory (MIFARE
									// CLASSIC ONLY)
					break;
				}
				TableItem item = table_.getItem(i);
				boolean hasData = false;
				final int offset = 2;
				for (int j = 0; j < data.length; j++) {
					int jj = j + offset;
					String newData = item.getText(jj);
					String oldData = (String) item.getData(jj + "");
					if (oldData != null) {
						hasData = true;
					}
					data[j] = (byte) Integer.parseInt(newData, 16);
				}
				if (hasData) {
					byte[] vdata;
					switch (NfcApp.driver_.type()) {
					case MIFARE_CLASSIC_1K:
					case MIFARE_CLASSIC_4K:
						// a sync operation is need to start from a known state
						NfcApp.driver_.sync();
						int startBlock = (i / 4) * 4;
						vdata = NfcApp.driver_.read(startBlock, false); // we
																		// have
																		// to
																		// read
																		// start
																		// block
						break;
					}
					boolean success = NfcApp.driver_.write(i, data, false); // write
																			// new
																			// data
					if (!success) {
						throw new Exception("Failed to write into block " + i
								+ " using Key A");
					}
					vdata = NfcApp.driver_.read(i, false); // read same block to
															// verify
					if (vdata == null) {
						throw new Exception("Failed to read block " + i
								+ ". Read operation failed.");
					}
					boolean equal = Arrays.equals(data, vdata);
					if (!equal) {
						BigInteger a = new BigInteger(data);
						BigInteger b = new BigInteger(vdata);
						throw new Exception("Failed to write data into block "
								+ i + " (Sector = " + (i / 4)
								+ ". Attempted to write data " + a.toString(16)
								+ " (hex) but instead found " + b.toString(16)
								+ " (hex).");
					}
				}
			}
			// All writes were successful, so remove all the old data
			for (int i = 0; i < table_.getItemCount(); i++) {
				TableItem item = table_.getItem(i);
				for (int j = 0; j < table_.getColumnCount(); j++) {
					String oldData = (String) item.getData(j + "");
					if (oldData != null) {
						item.setData(j + "", null); // clear stored data
						item.setFont(j, null);
					}
				}
			}
		} catch (Exception e) {
			NfcApp.prompt(this.getShell(), e.getLocalizedMessage(), SWT.OK
					| SWT.ICON_WARNING);
			return;
		}
		
	}

	public void saveToFile() {
		FileDialog fd = new FileDialog(this.getShell(), SWT.SAVE);
		fd.setText("Save");
		fd.setFilterPath("");
		String[] filterExt = { "*.ndef", "*.*" };
		fd.setFilterExtensions(filterExt);
		String filename = fd.open();
		try {
			FileOutputStream fos = new FileOutputStream(filename);
			for(int i=0;i<table_.getItemCount();i++) {
				TableItem item = table_.getItem(i);
				for(int j=2;j<table_.getColumnCount();j++) {
					String v = item.getText(j);
					fos.write(v.getBytes());
					fos.write(' ');
					System.out.print(v+" ");
				}
				System.out.println();
				fos.write('\n');
			}
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
			NfcApp.prompt(this.getShell(), e.getLocalizedMessage(),
					SWT.ICON_WARNING | SWT.OK);
		}
	}
}
