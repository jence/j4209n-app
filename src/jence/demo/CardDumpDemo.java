/**
 * 
 */
package jence.demo;

import java.util.Scanner;

import jence.jni.J4209N;

/**
 * @author soalib
 *
 */
public class CardDumpDemo {
	private static J4209N nfc = new J4209N();

	/**
	 * 
	 */
	public CardDumpDemo() {
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String COM = null; //"com2";
		try {
			System.out.println("Usage: j4210n <comport>");
			System.out.println("OR, enter J4210N reader to an USB port then provide the COM port at which the reader is found.");
			Scanner scanner = new Scanner(System.in);
			if (args.length > 0) {
				COM = args[0];
			}
			if (COM == null) {
				System.out.println("Please enter COM port (e.g., COM4):");
				COM = scanner.nextLine();
				COM = COM.trim();
			}
			
			nfc.open(COM);
			System.out.println("Connect to the reader: SUCCESS");
			boolean stop = false;
			do {
				System.out.println("Place a card on the device and press ENTER. (Press x+ENTER to stop)");
				String line = scanner.nextLine();
				if (line.indexOf('x') >= 0)
					break;
				byte[] uid = nfc.scan(100);
				System.out.println("UID="+nfc.toHex(uid));
				System.out.println("Type = "+nfc.type());
				
				int block = 0;
				byte[] data = null;
				nfc.keys(null, null); // use default keys of the card
				do {
					data = nfc.read(block++, false);
					if (data != null)
						System.out.println("Block:"+block+"\t"+nfc.toHex(data));
				} while(data != null);
			} while(true);
			nfc.close();
		} catch (Exception e) {
			System.err.println(e.getLocalizedMessage());
			//e.printStackTrace();
		}
		System.out.println("Program EXITED.");
	}
}

