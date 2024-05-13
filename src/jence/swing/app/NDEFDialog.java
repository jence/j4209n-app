package jence.swing.app;

import javax.swing.JDialog;
import java.awt.GridBagLayout;
import javax.swing.JPanel;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JEditorPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.Dimension;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.border.EmptyBorder;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.border.TitledBorder;


import jence.jni.J4209N;
import jence.jni.Vcard;
import jence.swing.app.NfcApp.MessageType;


public class NDEFDialog extends JDialog {

	private JComboBox comboVersion_;
	private JComboBox comboPrefix_;
	private JTextField textFirstName_;
	private JTextField textTelHome_;
	private JTextField textTitle_;
	private JTextField textNumber_;
	private JTextField textRegion_;
	private JTextField textPOBox_;
	private JTextField textLastName_;
	private JTextField textTelWork_;
	private JTextField textStreet_;
	private JTextField textZip_;
	private JTextField textUrl_;
	private JTextField textOtherNames_;
	private JTextField textEmail_;
	private JTextField textCompany_;
	private JTextField textLocality_;
	private JTextField textCountry_;

	private JRadioButton btnVcard_;
	private JPanel vCardData_;
	private JEditorPane text_;

	private int selection_;

	private final ButtonGroup buttonGroup = new ButtonGroup();

	private void callbackWrite(int option, int selection, String text) throws Exception {
		if (option == 1) {
			if (NfcApp.driver_.isNDEF()) {
				NfcApp.driver_.format();
				try {
					// formatting the second time should fail
					NfcApp.driver_.format();
//					status("FAILED to clean up the card. Please try again.");
					return;
				} catch (Exception e) {
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
			NfcApp.driver_.ndefAddUri("https://" + text);
//			status("https written successfully.");
			break; // https
		case 1:
			NfcApp.driver_.ndefAddUri("http://" + text);
//			status("http written successfully.");
			break; // http
		case 2:
			NfcApp.driver_.ndefAddText(text);
//			status("text written successfully.");
			break; // text
		case 3:
			NfcApp.driver_.ndefAddUri("tel://" + text);
//			status("tel written successfully.");
			break; // phone
		case 4:
			NfcApp.driver_.ndefAddUri("mailto://" + text);
//			status("email written successfully.");
			break; // email
		case 5:
			Vcard vcard = new Vcard(text);
			NfcApp.driver_.ndefAddVcard(vcard);
//			status("Vcard written successfully.");
			break; // vcard
		}
	}
	
//	private boolean readNDEF() {
//		try {
//			ndeftable_.removeAll();
//			NfcApp.driver_.sync();
//			if (!NfcApp.driver_.isNDEF()) {
//				prompt("No NDEF record found or the card may not be NDEF formatted.",
//						SWT.ICON_WARNING);
//				return false;
//			}
//			int records = NfcApp.driver_.ndefRead();
//			if (records > 0) {
//				ndeftable_.removeAll();
//				for (int i = 0; i < records; i++) {
//					J4209N.NdefRecord ndef = NfcApp.driver_.ndefGetRecord(i);
//					TableItem item = new TableItem(ndeftable_,
//							SWT.FULL_SELECTION | SWT.OK);
//					item.setText(0, "" + i);
//					item.setText(1, ndef.id);
//					item.setText(2, ndef.type);
//					item.setText(3, ndef.encoding);
//					item.setText(4, new String(ndef.payload, "UTF-8"));
//				}
//				return true;
//			} else {
//				prompt("No NDEF records found.", SWT.OK);
//				return false;
//			}
//		} catch (Exception e) {
//			prompt(e.getMessage()
//					+ " Please check if the device is attached to an USB port.",
//					SWT.ICON_WARNING);
//		}
//		return false;
//	}

	// Method to invoke the callback
	private void updateGui() {
		if (btnVcard_.isSelected()) {
			vCardData_.setVisible(true);
			text_.setVisible(false);
		} else {
			vCardData_.setVisible(false);
			text_.setVisible(true);
		}
	}

	public NDEFDialog(NfcAppFrame parent) {
		// TODO Auto-generated constructor stub
		super(parent, "NDEF Data", true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		this.setSize(801, 602);
		getContentPane().setPreferredSize(new Dimension(400, 400));
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 110, 677, 0 };
		gridBagLayout.rowHeights = new int[] { 200, 40, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 1.0, 0.0, Double.MIN_VALUE };
		getContentPane().setLayout(gridBagLayout);

		JPanel panel = new JPanel();
		panel.setBorder(new EmptyBorder(8, 8, 12, 8));
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.fill = GridBagConstraints.VERTICAL;
		gbc_panel.insets = new Insets(0, 0, 5, 5);
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 0;
		getContentPane().add(panel, gbc_panel);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[] { 47, 0 };
		gbl_panel.rowHeights = new int[] { 23, 0 };
		gbl_panel.columnWeights = new double[] { 0.0, Double.MIN_VALUE };
		gbl_panel.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
		panel.setLayout(gbl_panel);

		JPanel titlePanel = new JPanel();
		titlePanel.setBorder(new TitledBorder(null, "Record Type", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		GridBagConstraints gbc_titlePanel = new GridBagConstraints();
		gbc_titlePanel.fill = GridBagConstraints.BOTH;
		gbc_titlePanel.gridx = 0;
		gbc_titlePanel.gridy = 0;
		panel.add(titlePanel, gbc_titlePanel);
		GridBagLayout gbl_titlePanel = new GridBagLayout();
		gbl_titlePanel.columnWidths = new int[] { 47, 0 };
		gbl_titlePanel.rowHeights = new int[] { 23, 23, 0, 23, 0, 0, 0 };
		gbl_titlePanel.columnWeights = new double[] { 0.0, Double.MIN_VALUE };
		gbl_titlePanel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		titlePanel.setLayout(gbl_titlePanel);

		JRadioButton btnHttps = new JRadioButton("https://");
		btnHttps.setSelected(true);
		GridBagConstraints gbc_btnHttps = new GridBagConstraints();
		gbc_btnHttps.anchor = GridBagConstraints.WEST;
		gbc_btnHttps.insets = new Insets(0, 0, 5, 0);
		gbc_btnHttps.gridx = 0;
		gbc_btnHttps.gridy = 0;
		titlePanel.add(btnHttps, gbc_btnHttps);
		btnHttps.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				selection_ = 1;
				updateGui();
			}
		});
		buttonGroup.add(btnHttps);

		JRadioButton btnHttp = new JRadioButton("http://");
		GridBagConstraints gbc_btnHttp = new GridBagConstraints();
		gbc_btnHttp.anchor = GridBagConstraints.WEST;
		gbc_btnHttp.insets = new Insets(0, 0, 5, 0);
		gbc_btnHttp.gridx = 0;
		gbc_btnHttp.gridy = 1;
		titlePanel.add(btnHttp, gbc_btnHttp);
		btnHttp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selection_ = 2;
				updateGui();
			}
		});
		buttonGroup.add(btnHttp);

		JRadioButton btnText = new JRadioButton("Text");
		GridBagConstraints gbc_btnText = new GridBagConstraints();
		gbc_btnText.anchor = GridBagConstraints.WEST;
		gbc_btnText.insets = new Insets(0, 0, 5, 0);
		gbc_btnText.gridx = 0;
		gbc_btnText.gridy = 2;
		titlePanel.add(btnText, gbc_btnText);
		btnText.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selection_ = 3;
				updateGui();
			}
		});
		buttonGroup.add(btnText);

		JRadioButton btnPhoneNumber = new JRadioButton("Phone Num");
		GridBagConstraints gbc_btnPhoneNumber = new GridBagConstraints();
		gbc_btnPhoneNumber.anchor = GridBagConstraints.WEST;
		gbc_btnPhoneNumber.insets = new Insets(0, 0, 5, 0);
		gbc_btnPhoneNumber.gridx = 0;
		gbc_btnPhoneNumber.gridy = 3;
		titlePanel.add(btnPhoneNumber, gbc_btnPhoneNumber);
		btnPhoneNumber.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selection_ = 4;
				updateGui();
			}
		});
		buttonGroup.add(btnPhoneNumber);

		JRadioButton btnEmailAddress = new JRadioButton("Email Address");
		GridBagConstraints gbc_btnEmailAddress = new GridBagConstraints();
		gbc_btnEmailAddress.anchor = GridBagConstraints.WEST;
		gbc_btnEmailAddress.insets = new Insets(0, 0, 5, 0);
		gbc_btnEmailAddress.gridx = 0;
		gbc_btnEmailAddress.gridy = 4;
		titlePanel.add(btnEmailAddress, gbc_btnEmailAddress);
		btnEmailAddress.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selection_ = 5;
				updateGui();
			}
		});
		buttonGroup.add(btnEmailAddress);

		btnVcard_ = new JRadioButton("Vcard");
		GridBagConstraints gbc_btnVcard_ = new GridBagConstraints();
		gbc_btnVcard_.anchor = GridBagConstraints.WEST;
		gbc_btnVcard_.gridx = 0;
		gbc_btnVcard_.gridy = 5;
		titlePanel.add(btnVcard_, gbc_btnVcard_);
		btnVcard_.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selection_ = 6;
				updateGui();
			}
		});
		btnVcard_.setSelected(true);
		buttonGroup.add(btnVcard_);

		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new EmptyBorder(8, 8, 16, 8));
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.insets = new Insets(0, 0, 5, 0);
		gbc_panel_1.fill = GridBagConstraints.BOTH;
		gbc_panel_1.gridx = 1;
		gbc_panel_1.gridy = 0;
		getContentPane().add(panel_1, gbc_panel_1);
		GridBagLayout gbl_panel_1 = new GridBagLayout();
		gbl_panel_1.columnWidths = new int[] { 0, 0 };
		gbl_panel_1.rowHeights = new int[] { 300, 220, 0 };
		gbl_panel_1.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_panel_1.rowWeights = new double[] { 1.0, 1.0, Double.MIN_VALUE };
		panel_1.setLayout(gbl_panel_1);

		JPanel panel_2 = new JPanel();
		GridBagConstraints gbc_panel_2 = new GridBagConstraints();
		gbc_panel_2.insets = new Insets(0, 0, 5, 0);
		gbc_panel_2.fill = GridBagConstraints.BOTH;
		gbc_panel_2.gridx = 0;
		gbc_panel_2.gridy = 0;
		panel_1.add(panel_2, gbc_panel_2);
		GridBagLayout gbl_panel_2 = new GridBagLayout();
		gbl_panel_2.columnWidths = new int[] { 86, 0 };
		gbl_panel_2.rowHeights = new int[] { 280, 0 };
		gbl_panel_2.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_panel_2.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
		panel_2.setLayout(gbl_panel_2);

		text_ = new JEditorPane();
		GridBagConstraints gbc_text_ = new GridBagConstraints();
		gbc_text_.fill = GridBagConstraints.BOTH;
		gbc_text_.gridx = 0;
		gbc_text_.gridy = 0;
		panel_2.add(text_, gbc_text_);

		vCardData_ = new JPanel();
		vCardData_.setVisible(false);
		vCardData_.setBorder(new EmptyBorder(12, 4, 8, 4));
		GridBagConstraints gbc_vCardData_ = new GridBagConstraints();
		gbc_vCardData_.fill = GridBagConstraints.HORIZONTAL;
		gbc_vCardData_.gridx = 0;
		gbc_vCardData_.gridy = 1;
		panel_1.add(vCardData_, gbc_vCardData_);
		GridBagLayout gbl_vCardData_ = new GridBagLayout();
		gbl_vCardData_.columnWidths = new int[] { 0, 0, 0, 0, 0, 0, 0, 0 };
		gbl_vCardData_.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0 };
		gbl_vCardData_.columnWeights = new double[] { 0.0, 0.0, 1.0, 0.0, 1.0, 0.0, 2.0, Double.MIN_VALUE };
		gbl_vCardData_.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		vCardData_.setLayout(gbl_vCardData_);

		comboPrefix_ = new JComboBox();
		comboPrefix_.setModel(new DefaultComboBoxModel(new String[] { "Mr", "Mrs", "Ms" }));
		GridBagConstraints gbc_comboPrefix_ = new GridBagConstraints();
		gbc_comboPrefix_.insets = new Insets(0, 0, 5, 5);
		gbc_comboPrefix_.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboPrefix_.gridx = 0;
		gbc_comboPrefix_.gridy = 0;
		vCardData_.add(comboPrefix_, gbc_comboPrefix_);

		JLabel lblFirstName = new JLabel("First Name");
		GridBagConstraints gbc_lblFirstName = new GridBagConstraints();
		gbc_lblFirstName.anchor = GridBagConstraints.EAST;
		gbc_lblFirstName.insets = new Insets(0, 0, 5, 5);
		gbc_lblFirstName.gridx = 1;
		gbc_lblFirstName.gridy = 0;
		vCardData_.add(lblFirstName, gbc_lblFirstName);

		textFirstName_ = new JTextField();
		GridBagConstraints gbc_textFirstName_ = new GridBagConstraints();
		gbc_textFirstName_.insets = new Insets(0, 0, 5, 5);
		gbc_textFirstName_.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFirstName_.gridx = 2;
		gbc_textFirstName_.gridy = 0;
		vCardData_.add(textFirstName_, gbc_textFirstName_);
		textFirstName_.setColumns(10);

		JLabel lblLastName = new JLabel("Last Name");
		GridBagConstraints gbc_lblLastName = new GridBagConstraints();
		gbc_lblLastName.anchor = GridBagConstraints.EAST;
		gbc_lblLastName.insets = new Insets(0, 0, 5, 5);
		gbc_lblLastName.gridx = 3;
		gbc_lblLastName.gridy = 0;
		vCardData_.add(lblLastName, gbc_lblLastName);

		textLastName_ = new JTextField();
		GridBagConstraints gbc_textLastName_ = new GridBagConstraints();
		gbc_textLastName_.insets = new Insets(0, 0, 5, 5);
		gbc_textLastName_.fill = GridBagConstraints.HORIZONTAL;
		gbc_textLastName_.gridx = 4;
		gbc_textLastName_.gridy = 0;
		vCardData_.add(textLastName_, gbc_textLastName_);
		textLastName_.setColumns(10);

		JLabel lblOtherNames = new JLabel("Other Names");
		GridBagConstraints gbc_lblOtherNames = new GridBagConstraints();
		gbc_lblOtherNames.anchor = GridBagConstraints.EAST;
		gbc_lblOtherNames.insets = new Insets(0, 0, 5, 5);
		gbc_lblOtherNames.gridx = 5;
		gbc_lblOtherNames.gridy = 0;
		vCardData_.add(lblOtherNames, gbc_lblOtherNames);

		textOtherNames_ = new JTextField();
		GridBagConstraints gbc_textOtherNames_ = new GridBagConstraints();
		gbc_textOtherNames_.insets = new Insets(0, 0, 5, 0);
		gbc_textOtherNames_.fill = GridBagConstraints.HORIZONTAL;
		gbc_textOtherNames_.gridx = 6;
		gbc_textOtherNames_.gridy = 0;
		vCardData_.add(textOtherNames_, gbc_textOtherNames_);
		textOtherNames_.setColumns(10);

		comboVersion_ = new JComboBox();
		comboVersion_.setModel(new DefaultComboBoxModel(new String[] { "2.1", "3.0" }));
		GridBagConstraints gbc_comboVersion_ = new GridBagConstraints();
		gbc_comboVersion_.insets = new Insets(0, 0, 5, 5);
		gbc_comboVersion_.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboVersion_.gridx = 0;
		gbc_comboVersion_.gridy = 1;
		vCardData_.add(comboVersion_, gbc_comboVersion_);

		JLabel lblTelHome = new JLabel("Tel Home");
		GridBagConstraints gbc_lblTelHome = new GridBagConstraints();
		gbc_lblTelHome.anchor = GridBagConstraints.EAST;
		gbc_lblTelHome.insets = new Insets(0, 0, 5, 5);
		gbc_lblTelHome.gridx = 1;
		gbc_lblTelHome.gridy = 1;
		vCardData_.add(lblTelHome, gbc_lblTelHome);

		textTelHome_ = new JTextField();
		GridBagConstraints gbc_textTelHome_ = new GridBagConstraints();
		gbc_textTelHome_.insets = new Insets(0, 0, 5, 5);
		gbc_textTelHome_.fill = GridBagConstraints.HORIZONTAL;
		gbc_textTelHome_.gridx = 2;
		gbc_textTelHome_.gridy = 1;
		vCardData_.add(textTelHome_, gbc_textTelHome_);
		textTelHome_.setColumns(10);

		JLabel lblNewLabel_7 = new JLabel("Tel Work");
		GridBagConstraints gbc_lblNewLabel_7 = new GridBagConstraints();
		gbc_lblNewLabel_7.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_7.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_7.gridx = 3;
		gbc_lblNewLabel_7.gridy = 1;
		vCardData_.add(lblNewLabel_7, gbc_lblNewLabel_7);

		textTelWork_ = new JTextField();
		GridBagConstraints gbc_textTelWork_ = new GridBagConstraints();
		gbc_textTelWork_.insets = new Insets(0, 0, 5, 5);
		gbc_textTelWork_.fill = GridBagConstraints.HORIZONTAL;
		gbc_textTelWork_.gridx = 4;
		gbc_textTelWork_.gridy = 1;
		vCardData_.add(textTelWork_, gbc_textTelWork_);
		textTelWork_.setColumns(10);

		JLabel lblNewLabel_13 = new JLabel("Email");
		GridBagConstraints gbc_lblNewLabel_13 = new GridBagConstraints();
		gbc_lblNewLabel_13.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_13.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_13.gridx = 5;
		gbc_lblNewLabel_13.gridy = 1;
		vCardData_.add(lblNewLabel_13, gbc_lblNewLabel_13);

		textEmail_ = new JTextField();
		GridBagConstraints gbc_textEmail_ = new GridBagConstraints();
		gbc_textEmail_.insets = new Insets(0, 0, 5, 0);
		gbc_textEmail_.fill = GridBagConstraints.HORIZONTAL;
		gbc_textEmail_.gridx = 6;
		gbc_textEmail_.gridy = 1;
		vCardData_.add(textEmail_, gbc_textEmail_);
		textEmail_.setColumns(10);

		JButton btnDefault_ = new JButton("Default");
		btnDefault_.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				textFirstName_.setText("Ali");
				textLastName_.setText("Jamal");
				textOtherNames_.setText("Manik");
				textTelHome_.setText("012345678");
				textTelWork_.setText("123 456 7890");
				textTitle_.setText("Consultant");
				textCompany_.setText("Consulting Company");
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
		GridBagConstraints gbc_btnDefault_ = new GridBagConstraints();
		gbc_btnDefault_.insets = new Insets(0, 0, 5, 5);
		gbc_btnDefault_.gridx = 0;
		gbc_btnDefault_.gridy = 2;
		vCardData_.add(btnDefault_, gbc_btnDefault_);

		JLabel lblNewLabel_2 = new JLabel("Title");
		GridBagConstraints gbc_lblNewLabel_2 = new GridBagConstraints();
		gbc_lblNewLabel_2.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_2.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_2.gridx = 1;
		gbc_lblNewLabel_2.gridy = 2;
		vCardData_.add(lblNewLabel_2, gbc_lblNewLabel_2);

		textTitle_ = new JTextField();
		GridBagConstraints gbc_textTitle_ = new GridBagConstraints();
		gbc_textTitle_.gridwidth = 3;
		gbc_textTitle_.insets = new Insets(0, 0, 5, 5);
		gbc_textTitle_.fill = GridBagConstraints.HORIZONTAL;
		gbc_textTitle_.gridx = 2;
		gbc_textTitle_.gridy = 2;
		vCardData_.add(textTitle_, gbc_textTitle_);
		textTitle_.setColumns(10);

		JLabel lblNewLabel_14 = new JLabel("Company");
		GridBagConstraints gbc_lblNewLabel_14 = new GridBagConstraints();
		gbc_lblNewLabel_14.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_14.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_14.gridx = 5;
		gbc_lblNewLabel_14.gridy = 2;
		vCardData_.add(lblNewLabel_14, gbc_lblNewLabel_14);

		textCompany_ = new JTextField();
		GridBagConstraints gbc_textCompany_ = new GridBagConstraints();
		gbc_textCompany_.insets = new Insets(0, 0, 5, 0);
		gbc_textCompany_.fill = GridBagConstraints.HORIZONTAL;
		gbc_textCompany_.gridx = 6;
		gbc_textCompany_.gridy = 2;
		vCardData_.add(textCompany_, gbc_textCompany_);
		textCompany_.setColumns(10);

		JLabel lblNewLabel_3 = new JLabel("Number");
		GridBagConstraints gbc_lblNewLabel_3 = new GridBagConstraints();
		gbc_lblNewLabel_3.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_3.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_3.gridx = 1;
		gbc_lblNewLabel_3.gridy = 3;
		vCardData_.add(lblNewLabel_3, gbc_lblNewLabel_3);

		textNumber_ = new JTextField();
		GridBagConstraints gbc_textNumber_ = new GridBagConstraints();
		gbc_textNumber_.insets = new Insets(0, 0, 5, 5);
		gbc_textNumber_.fill = GridBagConstraints.HORIZONTAL;
		gbc_textNumber_.gridx = 2;
		gbc_textNumber_.gridy = 3;
		vCardData_.add(textNumber_, gbc_textNumber_);
		textNumber_.setColumns(10);

		JLabel lblNewLabel_9 = new JLabel("Street");
		GridBagConstraints gbc_lblNewLabel_9 = new GridBagConstraints();
		gbc_lblNewLabel_9.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_9.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_9.gridx = 3;
		gbc_lblNewLabel_9.gridy = 3;
		vCardData_.add(lblNewLabel_9, gbc_lblNewLabel_9);

		textStreet_ = new JTextField();
		GridBagConstraints gbc_textStreet_ = new GridBagConstraints();
		gbc_textStreet_.insets = new Insets(0, 0, 5, 5);
		gbc_textStreet_.fill = GridBagConstraints.HORIZONTAL;
		gbc_textStreet_.gridx = 4;
		gbc_textStreet_.gridy = 3;
		vCardData_.add(textStreet_, gbc_textStreet_);
		textStreet_.setColumns(10);

		JLabel lblNewLabel_15 = new JLabel("Locality");
		GridBagConstraints gbc_lblNewLabel_15 = new GridBagConstraints();
		gbc_lblNewLabel_15.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_15.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_15.gridx = 5;
		gbc_lblNewLabel_15.gridy = 3;
		vCardData_.add(lblNewLabel_15, gbc_lblNewLabel_15);

		textLocality_ = new JTextField();
		GridBagConstraints gbc_textLocality_ = new GridBagConstraints();
		gbc_textLocality_.insets = new Insets(0, 0, 5, 0);
		gbc_textLocality_.fill = GridBagConstraints.HORIZONTAL;
		gbc_textLocality_.gridx = 6;
		gbc_textLocality_.gridy = 3;
		vCardData_.add(textLocality_, gbc_textLocality_);
		textLocality_.setColumns(10);

		JLabel lblNewLabel_4 = new JLabel("Region");
		GridBagConstraints gbc_lblNewLabel_4 = new GridBagConstraints();
		gbc_lblNewLabel_4.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_4.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_4.gridx = 1;
		gbc_lblNewLabel_4.gridy = 4;
		vCardData_.add(lblNewLabel_4, gbc_lblNewLabel_4);

		textRegion_ = new JTextField();
		GridBagConstraints gbc_textRegion_ = new GridBagConstraints();
		gbc_textRegion_.insets = new Insets(0, 0, 5, 5);
		gbc_textRegion_.fill = GridBagConstraints.HORIZONTAL;
		gbc_textRegion_.gridx = 2;
		gbc_textRegion_.gridy = 4;
		vCardData_.add(textRegion_, gbc_textRegion_);
		textRegion_.setColumns(10);

		JLabel lblNewLabel_10 = new JLabel("Zip");
		GridBagConstraints gbc_lblNewLabel_10 = new GridBagConstraints();
		gbc_lblNewLabel_10.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_10.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_10.gridx = 3;
		gbc_lblNewLabel_10.gridy = 4;
		vCardData_.add(lblNewLabel_10, gbc_lblNewLabel_10);

		textZip_ = new JTextField();
		GridBagConstraints gbc_textZip_ = new GridBagConstraints();
		gbc_textZip_.insets = new Insets(0, 0, 5, 5);
		gbc_textZip_.fill = GridBagConstraints.HORIZONTAL;
		gbc_textZip_.gridx = 4;
		gbc_textZip_.gridy = 4;
		vCardData_.add(textZip_, gbc_textZip_);
		textZip_.setColumns(10);

		JLabel lblNewLabel_16 = new JLabel("Country");
		GridBagConstraints gbc_lblNewLabel_16 = new GridBagConstraints();
		gbc_lblNewLabel_16.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_16.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_16.gridx = 5;
		gbc_lblNewLabel_16.gridy = 4;
		vCardData_.add(lblNewLabel_16, gbc_lblNewLabel_16);

		textCountry_ = new JTextField();
		GridBagConstraints gbc_textCountry_ = new GridBagConstraints();
		gbc_textCountry_.fill = GridBagConstraints.HORIZONTAL;
		gbc_textCountry_.insets = new Insets(0, 0, 5, 0);
		gbc_textCountry_.gridx = 6;
		gbc_textCountry_.gridy = 4;
		vCardData_.add(textCountry_, gbc_textCountry_);
		textCountry_.setColumns(10);

		JLabel lblNewLabel_5 = new JLabel("PO Box");
		GridBagConstraints gbc_lblNewLabel_5 = new GridBagConstraints();
		gbc_lblNewLabel_5.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_5.insets = new Insets(0, 0, 0, 5);
		gbc_lblNewLabel_5.gridx = 1;
		gbc_lblNewLabel_5.gridy = 5;
		vCardData_.add(lblNewLabel_5, gbc_lblNewLabel_5);

		textPOBox_ = new JTextField();
		GridBagConstraints gbc_textPOBox_ = new GridBagConstraints();
		gbc_textPOBox_.insets = new Insets(0, 0, 0, 5);
		gbc_textPOBox_.fill = GridBagConstraints.HORIZONTAL;
		gbc_textPOBox_.gridx = 2;
		gbc_textPOBox_.gridy = 5;
		vCardData_.add(textPOBox_, gbc_textPOBox_);
		textPOBox_.setColumns(10);

		JLabel lblNewLabel_11 = new JLabel("Website");
		GridBagConstraints gbc_lblNewLabel_11 = new GridBagConstraints();
		gbc_lblNewLabel_11.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_11.insets = new Insets(0, 0, 0, 5);
		gbc_lblNewLabel_11.gridx = 3;
		gbc_lblNewLabel_11.gridy = 5;
		vCardData_.add(lblNewLabel_11, gbc_lblNewLabel_11);

		textUrl_ = new JTextField();
		GridBagConstraints gbc_textUrl_ = new GridBagConstraints();
		gbc_textUrl_.fill = GridBagConstraints.HORIZONTAL;
		gbc_textUrl_.gridwidth = 3;
		gbc_textUrl_.insets = new Insets(0, 0, 0, 5);
		gbc_textUrl_.gridx = 4;
		gbc_textUrl_.gridy = 5;
		vCardData_.add(textUrl_, gbc_textUrl_);
		textUrl_.setColumns(10);

		JPanel panel_4 = new JPanel();
		panel_4.setBorder(new EmptyBorder(0, 8, 0, 8));
		GridBagConstraints gbc_panel_4 = new GridBagConstraints();
		gbc_panel_4.gridwidth = 2;
		gbc_panel_4.fill = GridBagConstraints.BOTH;
		gbc_panel_4.gridx = 0;
		gbc_panel_4.gridy = 1;
		getContentPane().add(panel_4, gbc_panel_4);
		GridBagLayout gbl_panel_4 = new GridBagLayout();
		gbl_panel_4.columnWidths = new int[] { 0, 0, 0 };
		gbl_panel_4.rowHeights = new int[] { 65, 0 };
		gbl_panel_4.columnWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		gbl_panel_4.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
		panel_4.setLayout(gbl_panel_4);

		JButton btnWrite_ = new JButton("Write");
		btnWrite_.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				ndefWrite(false);
			}
		});
		btnWrite_.setIcon(new ImageIcon(NDEFDialog.class.getResource("/jence/icon/write.png")));
		GridBagConstraints gbc_btnWrite_ = new GridBagConstraints();
		gbc_btnWrite_.insets = new Insets(0, 0, 0, 5);
		gbc_btnWrite_.gridx = 0;
		gbc_btnWrite_.gridy = 0;
		panel_4.add(btnWrite_, gbc_btnWrite_);

		JButton btnEraseWrite_ = new JButton("Erase + Write");
		btnEraseWrite_.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ndefWrite(true);

			}
		});
		btnEraseWrite_.setIcon(new ImageIcon(NDEFDialog.class.getResource("/jence/icon/cardwrite.png")));
		GridBagConstraints gbc_btnEraseWrite_ = new GridBagConstraints();
		gbc_btnEraseWrite_.gridx = 1;
		gbc_btnEraseWrite_.gridy = 0;
		panel_4.add(btnEraseWrite_, gbc_btnEraseWrite_);

		this.setLocationRelativeTo(parent);

//		this.setSize();
	}

	private void checkBlank(JTextField... fields) throws Exception {
		for (JTextField field : fields) {
			if (field.getText().trim().isEmpty()) {
				throw new Exception(field.getToolTipText());
			}
		}
	}

	private void ndefWrite(final boolean eraseWrite) {
		String text = text_.getText().trim();
		if (btnVcard_.isSelected()) {
			try {
				checkBlank(textFirstName_, textLastName_, textEmail_);
			} catch (Exception e) {
				NfcApp.prompt(this, e.getLocalizedMessage(), "Error", MessageType.ERROR);
				return;
			}
		} else if (text.length() == 0) {
			NfcApp.prompt(this, "There are no text to write. Please provide a valid text.", "Error", MessageType.ERROR);
			return;
		}
		if (true) {
			try {
				if (btnVcard_.isSelected()) {
					Vcard vcard = new Vcard();

					vcard.version_ = comboVersion_.getSelectedItem().toString();
					vcard.name_.FirstName = textFirstName_.getText();
					vcard.name_.LastName = textLastName_.getText();
					vcard.name_.Prefix = comboPrefix_.getSelectedItem().toString();
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
//					System.out.println("from callback");
				}
//				callback_.callback(, selection_, text);
//				callback((eraseWrite) ? 1 : 0, selection_, text);
				try {
					callbackWrite((eraseWrite) ? 1 : 0, selection_, text);
				} catch (Exception e) {
					System.out.println(e.getLocalizedMessage());
				}

//				this.getShell().dispose();
				this.dispose();

			} catch (Exception e) {
				NfcApp.prompt(this, e.getLocalizedMessage(), "Error", MessageType.WARNING);
			}
		}

	}

}
