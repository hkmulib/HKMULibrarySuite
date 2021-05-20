import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.commons.validator.routines.UrlValidator;

import hk.edu.hkmu.lib.StringHandling;

public class TestString {
	public static void main(String[] args) {

		String str = "0008FMT  LBK0030LDR  L00000nam^^22^^^^^^i^45000015001  L0004310380022005  L20200423185221.00024006  Lm^^^^^o^^d^|^^^^^^0020007  Lcr^cnu||||||||0046008  L150921s2016^^^^paua^^^^sb^^^^001^0^eng^d0019010  L$$a20150367910051020  L$$a9781451131611$$qhardcover$$qalkaline paper0048020  L$$a1451131615$$qhardcover$$qalkaline paper0040020  L$$a9781469831244$$qelectronic book0035035  L$$ab57290325-852julac_network0030035  L$$a(julac-retro)162384140061040  L$$aDNLM/DLC$$erda$$beng$$cDLC$$dYDX$$dNLM$$dYDXCP$$dVRC002605004L$$aRD563$$b.M56 2016010224500L$$aMinimally invasive foot and ankle surgery /$$ceditors, Eric M. Bluman, Christopher P. Chiodo.0051264 1L$$aPhiladelphia :$$bWolters Kluwer,$$c[2016].0015264 4L$$c©20160080300  L$$a1 online resource (xviii, 214 pages) :$$billustrations (chiefly color).0032336  L$$atext$$btxt$$2rdacontent0032337  L$$acomputer$$bc$$2rdamedia0042338  L$$aonline resource$$bcr$$2rdacarrier0025347  L$$atext file$$bHTML0024347  L$$atext file$$bPDF0055504  L$$aIncludes bibliographical references and index.0073533  L$$aElectronic reproduction.$$bFarmington Hills, MI :$$cThomson Gale0024650 0L$$aFoot$$xSurgery.0025650 0L$$aAnkle$$xSurgery.0048650 0L$$aFoot$$xWounds and injuries\r\n"
				+ "$$xSurgery.0047650 0L$$aAnkle$$xWounds and injuries$$xSurgery.0032650 0L$$aLaparoscopic surgery.\r\n"
				+ "00347001 L$$aBluman, Eric M.,$$eeditor00417001 L$$aChiodo, Christopher P.,$$eeditor00467102 L$$aThomson Gale (Firm),$$eebook provider016885640L$$uhttp://www.lib.ouhk.edu.hk/cgi-bin/redirect.cgi?url=https://link.gale.com/apps/pub/7IUK/GVRL?sid=gale_marc&u=ouhk_eref$$zView full text here (via Thomson Gale)0032CAT  L$$c20200420$$lLCL52$$h17390045CAT  L$$aBATCH$$b00$$c20200420$$lLCL52$$h17390044CAT  L$$aryan$$b00$$c20200420$$lOUL01$$h17410046CAT  L$$aLCWONG$$b02$$c20200423$$lOUL01$$h17500046CAT  L$$aLCWONG$$b02$$c20200423$$lOUL01$$h18520031SID  L$$aCC$$dLCWONG$$e20200423\r\n"
				+ "";

		Pattern p = Pattern.compile(str);

		Matcher m = p.matcher(".*700..L.*");

		if (m.find())
			System.out.println("YES find 700");

		if (str.matches("700"))
			System.out.println("YES matches 700");

		if (str.contains("7001 L"))
			System.out.println("YES contains 700");
		
		System.out.println(StringHandling.getMonthByNum(2));

	}
	
}
