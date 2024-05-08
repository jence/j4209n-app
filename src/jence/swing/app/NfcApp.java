package jence.swing.app;

import java.lang.module.ModuleDescriptor.Version;

import jence.jni.J4209N;

public class NfcApp {
	public static J4209N driver_ = new J4209N();
	public static final double VERSION = 2.0;
	
	public static void main(String[] args) {
		new NfcAppFrame();
	}
}
