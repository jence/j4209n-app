/**
 * MIT LICENSE
 * 
 * Copyright © 2021 Jence, Ejaz Jamil.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation 
 * files (the “Software”), to deal in the Software without restriction, including without limitation the rights to use, copy, 
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the 
 * Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE 
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR 
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR 
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE. 
 * 
 */
package jence.swt.app;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

/**
 * NDEF Write Dialog.
 * 
 * @author Ejaz Jamil
 * @version 1.0
 */
public class NDEFComposite extends Composite {
	private Label lblStatus_;
	private Button btnWrite;
	private Label label_4;
	private Group grpReccordType_;
	private Text text_;
	private Composite composite_;
	private int selection_;
	private Callback callback_;

	public NDEFComposite(Composite arg0, int arg1) {
		super(arg0, arg1);
		composite_ = this;
		composite_.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));
		composite_.setLayout(new GridLayout(3, false));

		grpReccordType_ = new Group(this, SWT.NONE);
		grpReccordType_.setLayout(new GridLayout(1, false));
		grpReccordType_.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false,
				false, 1, 1));
		grpReccordType_.setText("Record Type");

		final Button btnHttps = new Button(grpReccordType_, SWT.RADIO);
		btnHttps.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				selection_ = 0;
			}
		});
		btnHttps.setText("https://");

		Button btnHttp = new Button(grpReccordType_, SWT.RADIO);
		btnHttp.setText("http://");
		btnHttp.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				selection_ = 1;
			}
		});

		final Button btnText = new Button(grpReccordType_, SWT.RADIO);
		btnText.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				selection_ = 2;
			}
		});
		btnHttps.setSelection(true);
		btnText.setText("Text");

		final Button btnPhoneNum = new Button(grpReccordType_, SWT.RADIO);
		btnPhoneNum.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				selection_ = 3;
			}
		});
		btnPhoneNum.setText("Phone Num");

		final Button btnEmailAddress = new Button(grpReccordType_, SWT.RADIO);
		btnEmailAddress.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				selection_ = 4;
			}
		});
		btnEmailAddress.setText("Email Address");

		final Button btnVcard = new Button(grpReccordType_, SWT.RADIO);
		btnVcard.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				selection_ = 5;
			}
		});
		btnVcard.setEnabled(false);
		btnVcard.setText("VCard");

		text_ = new Text(this, SWT.BORDER | SWT.MULTI);
		text_.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));

		label_4 = new Label(this, SWT.BORDER | SWT.SEPARATOR | SWT.HORIZONTAL);
		label_4.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false,
				3, 1));

		btnWrite = new Button(this, SWT.NONE);
		btnWrite.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				ndefWrite(false);
			}
		});
		btnWrite.setImage(SWTResourceManager.getImage(NDEFComposite.class,
				"/jence/icon/write.png"));
		btnWrite.setToolTipText("Write NDEF data.");
		btnWrite.setText("Write");

		Button btnEraseWrite_ = new Button(this, SWT.NONE);
		btnEraseWrite_
				.setToolTipText("This operation will erase previous content and write a new record. If the card was not NDEF formatted, it will be formatted.");
		btnEraseWrite_.setImage(SWTResourceManager.getImage(
				NDEFComposite.class, "/jence/icon/cardwrite.png"));
		btnEraseWrite_.setText("Erase + Write");
		new Label(this, SWT.NONE);
		btnEraseWrite_.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				ndefWrite(true);
			}
		});

		lblStatus_ = new Label(this, SWT.NONE);
		lblStatus_.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 3, 1));

		this.getShell().pack();
	}

	public void callback(Callback runnable) {
		callback_ = runnable;
	}

	private void ndefWrite(final boolean eraseWrite) {
		String text = text_.getText().trim();
		if (text.length() == 0) {
			NfcApp.prompt(this.getShell(),
					"There are no text to write. Please provide a valid text.",
					SWT.OK | SWT.ICON_WARNING);
			return;
		}
		if (callback_ != null) {
			try {
				callback_.callback((eraseWrite)?1:0, selection_, text);
				this.getShell().dispose();

			} catch (Exception e) {
				NfcApp.prompt(this.getShell(), e.getLocalizedMessage(), SWT.OK
						| SWT.ICON_WARNING);
			}
		}
	}
	
}

