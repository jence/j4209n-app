package jence.swing.app;

import java.awt.Component;
import java.lang.module.ModuleDescriptor.Version;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import jence.jni.J4209N;

public class NfcApp {
	public static J4209N driver_ = new J4209N();
	public static final double VERSION = 2.0;
	
    public enum MessageType {
        WARNING,
        ERROR,
        CONFIRMATION,
        INFORMATION,
        ABORT
    }

    public static boolean prompt(Component parent, String text, String heading, MessageType messageType) {
        JLabel label = new JLabel("<html><body style='width: 350px;'>" + text + "</body></html>");

        int optionType = JOptionPane.DEFAULT_OPTION;
        switch (messageType) {
            case WARNING:
                JOptionPane.showMessageDialog(parent, label, heading, JOptionPane.WARNING_MESSAGE);
                return true;
            case ERROR:
                JOptionPane.showMessageDialog(parent, label, heading, JOptionPane.ERROR_MESSAGE);
                return true;
            case CONFIRMATION:
                optionType = JOptionPane.YES_NO_OPTION;
                break;
            case INFORMATION:
                JOptionPane.showMessageDialog(parent, label, heading, JOptionPane.INFORMATION_MESSAGE);
                return true;
            case ABORT:
                JOptionPane.showMessageDialog(parent, label, heading, JOptionPane.ERROR_MESSAGE);
                System.exit(1);
        }

        int confirmationResult = JOptionPane.showConfirmDialog(null, label, heading, optionType);
        return (confirmationResult == JOptionPane.YES_OPTION);
    }


	
	public static void main(String[] args) {
		new NfcAppFrame();
	}




}
