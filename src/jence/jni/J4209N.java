/**
 * 
 */
package jence.jni;

import java.util.Arrays;

import jence.swt.app.Callback;

/**
 * @author soalib
 *
 */
public class J4209N {
	public enum CardType {
        // ULTRALIGHT FAMILY
        ULTRALIGHT,
        ULTRALIGHT_EV1,
        ULTRALIGHT_C,
        NTAG213,
        NTAG215,
        NTAG216,
        NTAG203,
        NTAG424,

        // MIFARE FAMILY
        MIFARE_CLASSIC_1K,
        MIFARE_CLASSIC_4K,
        
        // DESFIRE FAMILY
        MIFARE_DESFIRE_2K,
        MIFARE_DESFIRE_4K,
        MIFARE_DESFIRE_8K,

        // NOT IMPLEMENTED
        FELICA_LITE,
        FELICA_LITE_S,

        ISO7816,

        UNKNOWN
	};
	
	public static class NdefRecord {
		public String type;
		public String encoding;
		public String id;
		public byte[] payload;
	}
	
	public static class KeyData {
		public byte KeyA[] = {(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff};
		public byte KeyB[] = {(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff};
		public boolean ReadOnly = false;
		public boolean UseA = true;
		public boolean UseB = true;
		public int Access = 0;
		
		/**
		 * Get access condition from access bits.
		 * @param access
		 */
		public KeyData(int access) {
			switch(access) {
			case 0: ReadOnly = false; UseA = UseB = true; Access = 0; break;
			case 1: case 2: ReadOnly = true; UseA = true; UseB = true; Access = 1; break;
			case 3: case 4: case 6: ReadOnly = false; UseA = false; UseB = true; Access = 4; break;
			case 5: ReadOnly = true; UseB = true; Access = 5; break;
			case 7: ReadOnly = false; UseA = UseB = false; Access = 7; break;
			}
		}
	}

	static {
		System.loadLibrary("j4209n"); // Load native library hello.dll (Windows)
										// or libhello.so (Unixes)
										// at runtime
										// This library contains a native method
										// called sayHello()
	}

	// Connectivity
	private native int  AvailablePorts(byte[] ports);
	private native byte OpenPort(byte[] port);
	private native void ClosePort();
	private native int  Version();

	// Raw Card Operations
	private native byte Scan(byte[] uid, byte[] size, byte retries);
	private native byte Keys(byte[] keyA, byte[] keyB);
	private native byte Read(int block, byte[] data, byte[] size, boolean keyB);
	private native byte Write(int block, byte[]  data, byte size, boolean keyB);
	private native byte CardType();
	private native byte CardName(byte[] name);
	private native int  BlockCount();
	private native int  BlockSize();
	private native byte Format();
	private native byte UserMemory(int[] start, int[] end);
	private native byte Sync();
	private native void LastError(byte[] err);

	// NDEF operations
	private native byte IsNdef();
	private native byte NdefFormat();
	private native byte NdefAddUri(byte[] uri);
	private native byte NdefAddText(byte[] text);
	private native byte NdefErase();
	private native int  NdefRead();
	private native byte NdefReadBlock(int block, byte[] data, int[] size);
	private native byte NdefGetRecord(int i, byte[] type, byte[] id, byte[] encoding, byte[] payload, int[] size);

	// Card Emulation operations
	private native byte EmulateInit(byte[] uid, int buffsize, byte writeable);
	private native byte EmulateStart(int timeout);
	private native void EmulateStop();
	
	private Callback callback_ = null;
	private Thread T_;
	private boolean loaded_ = false;
	private int dllver_ = 0;
	
	private void check() throws Exception {
		if (loaded_)
			return;
		try {
			dllver_ = Version();
			loaded_ = true;
		} catch(Throwable e) {
			loaded_ = false;
		}
	}
	
	private String getNullTerminatedString(byte[] str) {
		StringBuffer b = new StringBuffer();
		for(int i=0;i<str.length;i++) {
			if (str[i] == 0)
				break;
			b.append((char)str[i]);
		}
		return b.toString();
	}
	
	private byte[] createNullTerminatedString(String z) {
		z += "\0";
		try {
		return z.getBytes("UTF-8");
		} catch(Exception e) {}
		return null;
	}
	
	public String LibraryVersion() {
		int v = Version();
		return v/100 + "." + (v%100);
	}
	
	/**
	 * 
	 */
	public J4209N() {
	}
	
	public String[] listPorts() throws Exception {
		check();
		byte[] ports = new byte[256*8];
		int n = AvailablePorts(ports);
		String[] s = new String[n];
		byte[] port = new byte[8];
		for(int i=0;i<n;i++) {
			port = Arrays.copyOfRange(ports, i*8, i*8+8);
			s[i] = new String(port, "UTF-8").trim();
		}
		return s;
	}
	
	/**
	 * Performs formatting of the tag clearing all data to zeros
	 * where possible. 
	 * 
	 * @throws Exception
	 */
	public void format() throws Exception {
		check();
		if (Format() != 1) {
			throw new Exception(error());
		}
	}
	
	public int blockcount() throws Exception{
		check();
		return BlockCount();
	}
	
	public int blocksize() throws Exception{
		check();
		return BlockSize();
	}
	
	public void ndefFormat() throws Exception {
		check();
		if (NdefFormat() == 0) {
			throw new Exception(error());
		}
	}
	
	public String error() throws Exception {
		byte[] buffer = new byte[128];
		LastError(buffer);
		return getNullTerminatedString(buffer);
	}
	
	public void ndefAddUri(String uri) throws Exception {
		check();
		byte[] data = createNullTerminatedString(uri);
		if (NdefAddUri(data) == 0) {
			throw new Exception("Failed to add NDEF record: URI addition failed.");
		}
	}

	public void ndefAddText(String text) throws Exception {
		check();
		byte[] data = createNullTerminatedString(text);
		if (NdefAddText(data) == 0) {
			throw new Exception("Failed to add NDEF record: TEXT addition failed.");
		}
	}

	public void ndefErase() throws Exception {
		check();
		if (NdefErase() == 0) {
			throw new Exception("Failed to erase NDEF records.");
		}
	}

	public int ndefRead() throws Exception {
		check();
		int n = NdefRead();
		if (n < 0) {
			throw new Exception("Failed to read NDEF record.");
		}
		return n;
	}

	public byte[] ndefRead(int block) throws Exception {
		check();
		byte[] data = new byte[64];
		int[] size = {data.length};
		byte n = NdefReadBlock(block, data, size);
		if (n == 0) {
			return null;
		}
		return Arrays.copyOf(data, size[0]);
	}

	public NdefRecord ndefGetRecord(int i) throws Exception {
		check();
		byte[] type = new byte[128];
		byte[] id = new byte[128];
		byte[] encoding = new byte[128];
		byte[] payload = new byte[512];
		int[] size = {payload.length};
		byte response = NdefGetRecord(i, type, id, encoding, payload, size);
		if (response == 0) {
			throw new Exception("Failed to read NDEF record at index "+i+".");
		}
		NdefRecord ndef = new NdefRecord();
		ndef.id = getNullTerminatedString(id);
		ndef.type = getNullTerminatedString(type);
		ndef.encoding = getNullTerminatedString(encoding);
		ndef.payload = Arrays.copyOf(payload, size[0]);
		
		return ndef;
	}

	/**
	 * Open the reader at the Com Port. The COM port may be found by opening the Device Manager
	 * in Windows.
	 * 
	 * @param comPort com port string, for example "COM31", etc.
	 * @throws Exception
	 */
	public void open(String comPort) throws Exception {
		check();
		comPort += "\0";
		byte[] port = comPort.getBytes("UTF-8");
		byte ok = OpenPort(port);
		if (ok == 0) 
			throw new Exception("Failed to connect to J4210N reader.");
	}

	/**
	 * Closes the com port, if opened.
	 */
	public void close() throws Exception {
		check();
		ClosePort();
	}

	/**
	 * Convert data into hex.
	 * 
	 * @param data data as byte array.
	 * @return hex string.
	 */
	public String toHex(byte[] data) {
		StringBuffer b = new StringBuffer();
		for(int i=0;i<data.length;i++) {
			String st = String.format("%02X", data[i]);
			b.append(st);
		}
		return b.toString().toUpperCase();
	}
	
	public void sync() throws Exception {
		byte success = Sync();
		if (success == 0) {
			throw new Exception("Failed to sync the tag.");
		}
	}

	/**
	 * Scans for a card. If found, returns it's UID.
	 * 
	 * @param retries number of tries to scan for card.
	 * @return the UID of the card.
	 * @throws Exception
	 */
	public byte[] scan(int retries) throws Exception {
		check();
		byte[] uid = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
		byte[] size = { 0 };
		byte ok = Scan(uid, size, (byte)retries);
		if (ok == 0)
			throw new Exception("No Card found.");
		uid = Arrays.copyOf(uid, size[0]);
		return uid;
	}

	/**
	 * Set keys for memory access. For a new card, use <code>null</code>
	 * for both the keys. Mifare cards have two type of keys.
	 * 
	 * @param keyA a key, defined by the card manufacturer. For Mifare 1K, this is 6 byte.
	 * @param keyB a key, defined by the card manufacturer. For Mifare 1K, this is 6 byte.
	 * @throws Exception
	 */
	public void keys(byte[] keyA, byte[] keyB) throws Exception {
		check();
		byte ok = Keys(keyA, keyB);
		if (ok == 0)
			throw new Exception("No Card found.");
	}

	/**
	 * Reads a block. If keyB is to be used instead of keyA, provide <code>true</code>.
	 * 
	 * @param block block number or page number.
	 * @param keyB which key to use indicator.
	 * @return array of read bytes.
	 * @throws Exception
	 */
	public byte[] read(int block, boolean keyB) throws Exception {
		check();
		byte[] data = new byte[32];
		byte[] size = {0};
		byte ok = Read(block, data, size, keyB);
		if (ok == 0)
			return null;
		data = Arrays.copyOf(data, size[0]);
		return data;
	}

	/**
	 * Writes data to a block. The data array must equal the size of the block.
	 * For Ultralight data size is 4 bytes. For Mifare, data size is 16 bytes.
	 * 
	 * @param block block or page index.
	 * @param data data to be written. The array size must equal the block size.
	 * @param keyB which key to use indicator.
	 * @return <true> if written successfully.
	 * @throws Exception
	 */
	public boolean write(int block, byte[] data, boolean keyB) throws Exception {
		check();
		byte ok = Write(block, data, (byte)data.length, keyB);
		if (ok == 0)
			return false;
		return true;
	}
	
	/**
	 * Returns name of the Card. The card names are in the enum CardType.
	 * @return card name.
	 */
	public String name() throws Exception {
		check();
		byte[] n = new byte[128];
		byte found = CardName(n);
		if (found == 0)
			return null;
		try {
			return getNullTerminatedString(n);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Card type, corresponds to one of the entry in CardType enum.
	 * 
	 * @return CardType enum entry.
	 */
	public CardType type() throws Exception {
		check();
		byte t = CardType();
		return CardType.values()[t];
	}
	
	public boolean isNDEF() throws Exception {
		check();
		byte b = IsNdef();
		return b == 1;
	}
	
	public int[] usrmem() throws Exception {
		check();
		int[] start = {0};
		int[] end = {0};
		byte success = UserMemory(start, end);
		if (success > 0) {
			return new int[]{start[0],end[0]};
		}
		throw new Exception("User Memory Information caused an error.");
	}
	
	public void emulateInit(byte[] uid, boolean writeable) throws Exception {
		check();
		if (uid.length != 3) {
			throw new Exception("UID length must equal to 3 in card emulation mode.");
		}
		byte success = EmulateInit(uid, 128, writeable ? (byte)1 : 0);
		if (success != 1) {
			throw new Exception("Card emulation failed due to unknown reason.");
		}
	}
	
	public void emulateStart(final int timeoutms, Callback callback) throws Exception {
		check();
		callback_ = callback;
		T_ = new Thread(new Runnable(){

			@Override
			public void run() {
				System.out.println("Emulation Thread Started.");
				byte success = EmulateStart(timeoutms);
				try {
					if (success == (byte)1) {
						callback_.callback(0, 1, "NDEF Written");
					} else {
						callback_.callback(0, 0, "Timeout");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				System.out.println("Emulation Thread Terminated.");
			}});
		T_.start();
	}
	
	public void emulateStop() throws Exception {
		check();
		if (T_ != null)
			T_.interrupt();
		EmulateStop();
		System.out.println("Emulation Stopped.");
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		J4209N nfc = new J4209N();
		try {
			String[] ports = nfc.listPorts();
			nfc.open("com2");
			byte[] uid = nfc.scan(100);
			if (uid == null) {
				System.out.println("No UID found.");
				return;
			}
			//System.out.println("Card Name: "+nfc.type());
			System.out.println(nfc.toHex(uid));
			nfc.keys(null, null); // use default keys of the card
			int block = 0;
			byte[] data = nfc.read(block, false);
			System.out.println("Block:"+block+"="+nfc.toHex(data));
			
			block = 5;
			String newdata = "Helo";
			nfc.write(block, newdata.getBytes("UTF-8"), false);
			data = nfc.read(block, false);
			System.out.println("Block:"+block+"="+new String(data, "UTF-8"));
			
			nfc.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
