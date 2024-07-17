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

import jence.jni.Vcard;

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
import org.eclipse.swt.widgets.Combo;

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
	private Text textFirstName_;
	private Text textLastName_;
	private Text textOtherNames_;
	private Text textTelHome_;
	private Text textTelWork_;
	private Text textEmail_;
	private Text textCompany_;
	private Text textPOBox_;
	private Text textNumber_;
	private Button btnVcard_;
	private Composite vcarddata_;
	private Combo comboPrefix_;
	private Label lblTitle;
	private Text textTitle_;
	private Label lblNewLabel;
	private Text textStreet_;
	private Label lblNewLabel_1;
	private Text textLocality_;
	private Label lblRegion;
	private Text textRegion_;
	private Label lblZip;
	private Text textZip_;
	private Label lblCountry;
	private Text textCountry_;
	private Button btnDefault;
	private Combo comboVersion_;
	private Label lblWebsite;
	private Text textUrl_;
	
	private void updateGui() {
		if (btnVcard_.getSelection()) {
			vcarddata_.setVisible(true);
			text_.setVisible(false);
		} else {
			vcarddata_.setVisible(false);
			text_.setVisible(true);
		}
	}

	public NDEFComposite(Composite arg0, int arg1) {
		super(arg0, arg1);
		composite_ = this;
		composite_.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));
		setLayout(new GridLayout(3, false));

		grpReccordType_ = new Group(this, SWT.NONE);
		grpReccordType_.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false, 1, 2));
		grpReccordType_.setLayout(new GridLayout(1, false));
		grpReccordType_.setText("Record Type");

		final Button btnHttps = new Button(grpReccordType_, SWT.RADIO);
		btnHttps.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				selection_ = 0;
				updateGui();
			}
		});
		btnHttps.setText("https://");

		Button btnHttp = new Button(grpReccordType_, SWT.RADIO);
		btnHttp.setText("http://");
		btnHttp.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				selection_ = 1;
				updateGui();
			}
		});

		final Button btnText = new Button(grpReccordType_, SWT.RADIO);
		btnText.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				selection_ = 2;
				updateGui();
			}
		});
		btnHttps.setSelection(true);
		btnText.setText("Text");

		final Button btnPhoneNum = new Button(grpReccordType_, SWT.RADIO);
		btnPhoneNum.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				selection_ = 3;
				updateGui();
			}
		});
		btnPhoneNum.setText("Phone Num");

		final Button btnEmailAddress = new Button(grpReccordType_, SWT.RADIO);
		btnEmailAddress.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				selection_ = 4;
				updateGui();
			}
		});
		btnEmailAddress.setText("Email Address");

		btnVcard_ = new Button(grpReccordType_, SWT.RADIO);
		btnVcard_.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				selection_ = 5;
				updateGui();
			}
		});
		btnVcard_.setText("VCard");

		text_ = new Text(this, SWT.BORDER | SWT.MULTI);
		text_.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		
		vcarddata_ = new Composite(this, SWT.NONE);
		vcarddata_.setLayout(new GridLayout(7, false));
		vcarddata_.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 2, 1));
		
		comboPrefix_ = new Combo(vcarddata_, SWT.READ_ONLY);
		comboPrefix_.setItems(new String[] {"Mr", "Mrs", "Ms"});
		comboPrefix_.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		comboPrefix_.select(0);
		
		Label lblFirstName = new Label(vcarddata_, SWT.NONE);
		lblFirstName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblFirstName.setText("First Name");
		
		textFirstName_ = new Text(vcarddata_, SWT.BORDER);
		textFirstName_.setTextLimit(16);
		textFirstName_.setToolTipText("First Name must be provided (max 16 char).");
		textFirstName_.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblLastName = new Label(vcarddata_, SWT.NONE);
		lblLastName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblLastName.setText("Last Name");
		
		textLastName_ = new Text(vcarddata_, SWT.BORDER);
		textLastName_.setTextLimit(16);
		textLastName_.setToolTipText("Last Name must be provided (max 16 char).");
		textLastName_.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblOtherNames = new Label(vcarddata_, SWT.NONE);
		lblOtherNames.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblOtherNames.setText("Other Names");
		
		textOtherNames_ = new Text(vcarddata_, SWT.BORDER);
		textOtherNames_.setTextLimit(16);
		textOtherNames_.setToolTipText("Optional (max 16 char)");
		textOtherNames_.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		comboVersion_ = new Combo(vcarddata_, SWT.READ_ONLY);
		comboVersion_.setItems(new String[] {"2.1", "3.0"});
		comboVersion_.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		comboVersion_.select(0);
		
		Label lblTelHome = new Label(vcarddata_, SWT.NONE);
		lblTelHome.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblTelHome.setText("Tel Home");
		
		textTelHome_ = new Text(vcarddata_, SWT.BORDER);
		textTelHome_.setTextLimit(16);
		textTelHome_.setToolTipText("Optional - Home telephone (max 16 char)");
		textTelHome_.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblTelWork = new Label(vcarddata_, SWT.NONE);
		lblTelWork.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblTelWork.setText("Tel Work");
		
		textTelWork_ = new Text(vcarddata_, SWT.BORDER);
		textTelWork_.setTextLimit(16);
		textTelWork_.setToolTipText("Optional - Work Telephone (max 16 char)");
		textTelWork_.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblEmail = new Label(vcarddata_, SWT.NONE);
		lblEmail.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblEmail.setText("Email");
		
		textEmail_ = new Text(vcarddata_, SWT.BORDER);
		textEmail_.setTextLimit(20);
		textEmail_.setToolTipText("Email must be provided (max 20 char)");
		textEmail_.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		btnDefault = new Button(vcarddata_, SWT.NONE);
		btnDefault.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				textFirstName_.setText("Ali");
				textLastName_.setText("Jamal");
				textOtherNames_.setText("Manik");
				textTelHome_.setText("012345678");
				textTelWork_.setText("123 456 7890");
				textTitle_.setText("Consultant");
				textCompany_.setText("Consulting Co.");
				textNumber_.setText("11");
				textStreet_.setText("Narrow Road");
				textLocality_.setText("Uttara");
				textRegion_.setText("Dhaka");
				textZip_.setText("1211");
				textCountry_.setText("Bangladesh");
				textPOBox_.setText("n/a");
				textEmail_.setText("jence@jence.com");
				textUrl_.setText("https://jence.com");
			}
		});
		btnDefault.setText("Default");
		
		lblTitle = new Label(vcarddata_, SWT.NONE);
		lblTitle.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblTitle.setText("Title");
		
		textTitle_ = new Text(vcarddata_, SWT.BORDER);
		textTitle_.setTextLimit(20);
		textTitle_.setToolTipText("Job Title or other title (max 20 char)");
		textTitle_.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		
		Label lblCompany = new Label(vcarddata_, SWT.NONE);
		lblCompany.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblCompany.setText("Company");
		
		textCompany_ = new Text(vcarddata_, SWT.BORDER);
		textCompany_.setTextLimit(16);
		textCompany_.setToolTipText("Optional - Company Name (max 16 char)");
		textCompany_.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(vcarddata_, SWT.NONE);
		
		Label lblNumber = new Label(vcarddata_, SWT.NONE);
		lblNumber.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNumber.setText("Number");
		
		textNumber_ = new Text(vcarddata_, SWT.BORDER);
		textNumber_.setTextLimit(10);
		textNumber_.setToolTipText("House Number (max 10 char)");
		textNumber_.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		lblNewLabel = new Label(vcarddata_, SWT.NONE);
		lblNewLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel.setText("Street");
		
		textStreet_ = new Text(vcarddata_, SWT.BORDER);
		textStreet_.setTextLimit(24);
		textStreet_.setToolTipText("Street Address (max 24 char)");
		textStreet_.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		lblNewLabel_1 = new Label(vcarddata_, SWT.NONE);
		lblNewLabel_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_1.setText("Locality");
		
		textLocality_ = new Text(vcarddata_, SWT.BORDER);
		textLocality_.setTextLimit(16);
		textLocality_.setToolTipText("City, Suburb, etc. (max 16 char)");
		textLocality_.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(vcarddata_, SWT.NONE);
		
		lblRegion = new Label(vcarddata_, SWT.NONE);
		lblRegion.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblRegion.setText("Region");
		
		textRegion_ = new Text(vcarddata_, SWT.BORDER);
		textRegion_.setTextLimit(16);
		textRegion_.setToolTipText("City, County, District (max 16 char)");
		textRegion_.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		lblZip = new Label(vcarddata_, SWT.NONE);
		lblZip.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblZip.setText("Zip");
		
		textZip_ = new Text(vcarddata_, SWT.BORDER);
		textZip_.setTextLimit(10);
		textZip_.setToolTipText("Alpha numeric zip (max 10 char)");
		textZip_.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		lblCountry = new Label(vcarddata_, SWT.NONE);
		lblCountry.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblCountry.setText("Country");
		
		textCountry_ = new Text(vcarddata_, SWT.BORDER);
		textCountry_.setTextLimit(16);
		textCountry_.setToolTipText("Country name (max 16 char)");
		textCountry_.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(vcarddata_, SWT.NONE);
		
		Label lblPoBox = new Label(vcarddata_, SWT.NONE);
		lblPoBox.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblPoBox.setText("PO Box");
		
		textPOBox_ = new Text(vcarddata_, SWT.BORDER);
		textPOBox_.setTextLimit(10);
		textPOBox_.setToolTipText("Optional - PO Box Number (max 10 char)");
		textPOBox_.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		lblWebsite = new Label(vcarddata_, SWT.NONE);
		lblWebsite.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblWebsite.setText("Website");
		
		textUrl_ = new Text(vcarddata_, SWT.BORDER);
		textUrl_.setTextLimit(20);
		textUrl_.setToolTipText("Website URL. (max 20 char)");
		textUrl_.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));

		label_4 = new Label(this, SWT.BORDER | SWT.SEPARATOR | SWT.HORIZONTAL);
		label_4.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 3, 1));

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
		btnEraseWrite_.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				ndefWrite(true);
			}
		});
		new Label(this, SWT.NONE);

		lblStatus_ = new Label(this, SWT.NONE);
		lblStatus_.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));
		
		updateGui();
		
		this.getShell().pack();
	}

	public void callback(Callback runnable) {
		callback_ = runnable;
	}
	
	private void checkBlank(Text... text) throws Exception {
		for(Text t : text) {
			if (t.getText().trim().length() == 0)
				throw new Exception(t.getToolTipText());
		}
	}

	private void ndefWrite(final boolean eraseWrite) {
		String text = text_.getText().trim();
		if (btnVcard_.getSelection()) {
			try {
				checkBlank(textFirstName_, textLastName_, textEmail_);
			} catch(Exception e) {
				NfcApp.prompt(this.getShell(),
						e.getLocalizedMessage(),
						SWT.OK | SWT.ICON_WARNING);
				return;
			}
		} else if (text.length() == 0) {
			NfcApp.prompt(this.getShell(),
					"There are no text to write. Please provide a valid text.",
					SWT.OK | SWT.ICON_WARNING);
			return;
		}
		if (callback_ != null) {
			try {
				if (btnVcard_.getSelection()) {
					Vcard vcard = new Vcard();
					
					vcard.version_ = comboVersion_.getText();
					vcard.name_.FirstName = textFirstName_.getText();
					vcard.name_.LastName = textLastName_.getText();
					vcard.name_.Prefix = comboPrefix_.getText();
					vcard.title_ = textTitle_.getText();
					vcard.org_ = textCompany_.getText();
					vcard.email_ = textEmail_.getText();
					vcard.url_ = textUrl_.getText();
					
					vcard.hphone_.Type = "Home";
					vcard.hphone_.Voice = textTelHome_.getText();
					
					vcard.wphone_.Type = "Work";
					vcard.wphone_.Voice = textTelWork_.getText();

					vcard.haddress_.Type = "Home";
					vcard.haddress_.Number = textNumber_.getText();
					vcard.haddress_.Street = textStreet_.getText();
					vcard.haddress_.Locality = textLocality_.getText();
					vcard.haddress_.Region = textRegion_.getText();
					vcard.haddress_.Zip = textZip_.getText();
					vcard.haddress_.Country = textCountry_.getText();
					vcard.haddress_.PObox = textPOBox_.getText();
					
					text = vcard.toVcard();
				}
				callback_.callback((eraseWrite)?1:0, selection_, text);
				this.getShell().dispose();

			} catch (Exception e) {
				NfcApp.prompt(this.getShell(), e.getLocalizedMessage(), SWT.OK
						| SWT.ICON_WARNING);
			}
		}
	}
	
}

