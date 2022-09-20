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

/**
 * The application entry point.
 * 
 * @author Ejaz Jamil
 * @version 1.0
 *
 */
import jence.jni.J4210N;

import org.eclipse.swt.*;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

public class NfcApp {
	
	public static J4210N driver_ = new J4210N();
	public static final String VERSION = "1.7";
	private static Shell app_;
	private static Display display_;
	
	public static int prompt(Shell shell, String text, int style) {
		MessageBox messageBox = new MessageBox(shell, style);	
		if ((style & SWT.ICON_WARNING) > 0) messageBox.setText("Warning");
		if ((style & SWT.ABORT) > 0) messageBox.setText("Alert");
		if ((style & SWT.ICON_ERROR) > 0) messageBox.setText("Error");

		messageBox.setMessage(text);
		return messageBox.open();
	}
	
	public static void center(Shell shell) {
		Monitor primary = display_.getPrimaryMonitor ();
		Rectangle bounds = primary.getBounds ();
		Rectangle rect = shell.getBounds ();
		int x = bounds.x + (bounds.width - rect.width) / 2;
		int y = bounds.y + (bounds.height - rect.height) / 2;
		shell.setLocation (x, y);
	}
	
	public static void main(String[] args) {
		display_ = new Display();
		app_ = new Shell(display_);
		app_.setSize(800, 600);
		app_.setText("Jence NFC App");
		app_.setLayout(new GridLayout());
		
		Composite composite = new NfcAppComposite(app_, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		composite.setLayout(new GridLayout(4, false));

		//shlJencenfcapp_.pack();
		center(app_);
		app_.open();
		while (!app_.isDisposed()) {
			if (!display_.readAndDispatch())
				display_.sleep();
		}
		display_.dispose();
	}

	public static final void hexKeyListener(final Text control) {
		control.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
				boolean letter = (Character.isLetter(e.character));
				if (Character.isDigit(e.character))
					e.doit = true;
				else if (letter) {
					if (e.character >= 'A' && e.character <= 'F')
						e.doit = true;
					else if (e.character >= 'a' && e.character <= 'f') {
						e.doit = true;
					} else
						e.doit = false;
				} else if (e.character == '\b')
					e.doit = false;
			}
	
			public void keyReleased(KeyEvent e) {
			}
		});
		control.addVerifyListener(new VerifyListener() {
			public void verifyText(VerifyEvent e) {
				e.text = e.text.toUpperCase();
				e.doit = true;
			}
		});
	}

}

