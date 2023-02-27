package jence.jni;

import java.io.IOException;
import java.io.StringReader;
import java.util.Properties;

/*
	BEGIN:VCARD\n
	VERSION:3.0\n	
	N:<LASTNAME>; <FIRSTNAME>; <ADDITIONAL NAME>; <NAME PREFIX(Mr.,Mrs.)>; <NAME SUFFIX>\n
	FN:<NAME PREFIX(Mr.,Mrs.)> <FIRSTNAME> <ADDITIONAL NAME> <LASTNAME> <NAME SUFFIX>\n	
	ORG:<company>\n
	TITLE:<title>\n
	
	PHOTO;VALUE#URI;TYPE#GIF:<uri>\n
	
	TEL;TYPE#WORK,VOICE:(xyz) abc-defg\n
	TEL;TYPE#HOME,VOICE:(xyz) abc-defg\n
	ADR;TYPE#WORK,PREF:;;<addr1>;<cty>;<st>;<zip>;<country>\n
	LABEL;TYPE#WORK,PREF:100 Waters Edge\nBaytown\, LA 30314\nUnited States of America\n
	ADR;TYPE#HOME:;;42 Plantation St.;Baytown;LA;30314;United States of America\n
	LABEL;TYPE#HOME:42 Plantation St.\nBaytown\, LA 30314\nUnited States of America\m
	
	EMAIL:forrestgump@example.com\n
	REV:2008-04-24T19:52:43Z\n
	END:VCARD
*/
public class Vcard {
	public class Name {
		public String FirstName = "";
		public String LastName = "";
		public String OtherNames = "";
		public String Prefix = "";
		public String Suffix = "";
		public String FullName = "";
		
		String toVcard() {
			if (FirstName == "" || LastName == "")
				return "";
			StringBuffer b = new StringBuffer("N:"+LastName+";"+FirstName+";"+OtherNames+";"+Prefix+";"+Suffix+"\n");
			b.append("FN:"+Prefix+" "+FirstName+" "+OtherNames+" "+LastName+" "+Suffix+"\n");
			return b.toString();
		}
	}
	
	public class Phone {
		public String Type = "";
		public String Voice = "";
		
		String toVcard() {
			if (Type == null || Voice == null)
				return "";
			StringBuffer b;
			if (version_.startsWith("3"))
				b = new StringBuffer("TEL;TYPE#"+Type.toUpperCase()+",VOICE:"+Voice+"\n");
			else
				b = new StringBuffer("TEL;"+Type.toUpperCase()+";VOICE:"+Voice+"\n");
			return  b.toString();
		}
	}
	
	public class Address {
		public String Type = "";
		public String PObox = "";
		public String Number = "";
		public String Street = "";
		public String Locality = "";		
		public String Region = ""; // or State
		public String Zip = "";
		public String Country = "";

		Address(String type) {
			Type = type;
		}

		// ADR;TYPE=home:;;123 Main St.;Springfield;IL;12345;USA
		String toVcard() {
			if (Type == "")
				return "";
			StringBuffer b;
			if (version_.startsWith("3"))
				b = new StringBuffer("ADR;TYPE#"+Type.toUpperCase()+":"+PObox+";"+Number+";"+Street+";"+Locality+";"+Region+";"+Zip+";"+Country+"\n");
			else
				b = new StringBuffer("ADR;"+Type.toUpperCase()+":"+PObox+";"+Number+";"+Street+";"+Locality+";"+Region+";"+Zip+";"+Country+"\n");
			return b.toString();
		}
	}

	public Name name_ = new Name();
	public Address haddress_ = new Address("HOME");
	public Address waddress_ = new Address("WORK");
	public Phone hphone_ = new Phone();
	public Phone wphone_ = new Phone();
	public String title_ = "";
	public String org_ = "";
	public String email_ = "";
	public String url_ = "";
	public String version_ = "3.0";

	public Vcard(String vcard) {
		System.out.println(vcard);
		int n = vcard.indexOf("BEGIN:VCARD\n");
		String s = vcard.substring(n);
		final Properties p = new Properties();
	    try {
			p.load(new StringReader(s));
			System.out.println(p);

			version_ = p.getProperty("VERSION");
			
			// get names
			String name = p.getProperty("N");
			String[] names = name.split(";");
			if (names.length >= 1)
				name_.LastName = names[0];
			if (names.length >= 2)
				name_.FirstName = names[1];
			if (names.length >= 3)
				name_.OtherNames = names[2];
			if (names.length >= 4)
				name_.Prefix = names[3];
			name_.FullName = p.getProperty("FN");
			
			// get org, title
			org_ = p.getProperty("ORG");
			title_ = p.getProperty("TITLE");
			email_ = p.getProperty("EMAIL");
			url_ = p.getProperty("URL");
			
			//phone_.Type 
			if (version_.startsWith("3")) {
				String wp = p.getProperty("TEL;TYPE#WORK,VOICE");
				wphone_.Type = "WORK";
				wphone_.Voice = wp;
				String hp = p.getProperty("TEL;TYPE#HOME,VOICE");
				hphone_.Type = "HOME";
				hphone_.Voice = hp;
				
				// "ADR;"+Type.toUpperCase()+":"+PObox+";"+Number+";"+Street+";"+Locality+";"+Region+";"+Zip+";"+Country+"\n"
				String adr = p.getProperty("ADR;TYPE#HOME");
				String[] adrs = adr.split(";");
				if (adrs.length >= 1) {
					haddress_.PObox = adrs[0];
				}
				if (adrs.length >= 2) {
					haddress_.Number = adrs[1];
				}
				if (adrs.length >= 3) {
					haddress_.Street = adrs[2];
				}
				if (adrs.length >= 4) {
					haddress_.Locality = adrs[3];
				}
				if (adrs.length >= 5) {
					haddress_.Region = adrs[4];
				}
				if (adrs.length >= 6) {
					haddress_.Zip = adrs[5];
				}
				if (adrs.length >= 7) {
					haddress_.Country = adrs[6];
				}
			} else if (version_.startsWith("2")) {
				String wp = p.getProperty("TEL;WORK;VOICE");
				wphone_.Type = "WORK";
				wphone_.Voice = wp;
				String hp = p.getProperty("TEL;HOME;VOICE");
				hphone_.Type = "HOME";
				hphone_.Voice = hp;
				
				// "ADR;"+Type.toUpperCase()+":"+PObox+";"+Number+";"+Street+";"+Locality+";"+Region+";"+Zip+";"+Country+"\n"
				String adr = p.getProperty("ADR;HOME");
				String[] adrs = adr.split(";");
				if (adrs.length >= 1) {
					haddress_.PObox = adrs[0];
				}
				if (adrs.length >= 2) {
					haddress_.Number = adrs[1];
				}
				if (adrs.length >= 3) {
					haddress_.Street = adrs[2];
				}
				if (adrs.length >= 4) {
					haddress_.Locality = adrs[3];
				}
				if (adrs.length >= 5) {
					haddress_.Region = adrs[4];
				}
				if (adrs.length >= 6) {
					haddress_.Zip = adrs[5];
				}
				if (adrs.length >= 7) {
					haddress_.Country = adrs[6];
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Vcard(){}

	void addAddress(Address address) {
		haddress_ = address;
	}

	void addPhone(Phone phone) {
		hphone_ = phone;
	}

	void addName(Name name) {
		name_ = name;
	}

	public String toVcard() {
		StringBuffer b = new StringBuffer("BEGIN:VCARD\nVERSION:"+version_+"\n");
		
		b.append(name_.toVcard());
		b.append("TITLE:"+title_+"\n");
		b.append("ORG:"+org_+"\n");
		b.append("EMAIL:"+email_+"\n");
		b.append("URL:"+url_+"\n");
		b.append(haddress_.toVcard());
		b.append(waddress_.toVcard());
		b.append(hphone_.toVcard());
		b.append(wphone_.toVcard());
		
		if (version_.startsWith("2"))
			b.append("REV:20080424T195243Z\n");
		else if (version_.startsWith("3"))
			b.append("REV:2008-04-24T19:52:43Z\n");
		b.append("END:VCARD\n");
		return b.toString();
	}
}
