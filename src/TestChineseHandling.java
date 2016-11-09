import java.nio.charset.Charset;

import hk.edu.csids.*;

public class TestChineseHandling
{
	public static void main(String[] args) {
		String str = "880    $6500-07/$1$a{215946}{215426}: {213034}{27376f}{215744}{69242d}{69244e}{692539}{69256d}{692526}{692126}{69255c}{69213c}{692548}.";
		CJKStringHandling ch = new CJKStringHandling();
		String strcon = new String(ch.EACCtoUnicode(str.getBytes()) );
		System.out.println(strcon);
		
		String str2 = "五零五";
		System.out.println(CJKStringHandling.convertChineseToArabicNumber(str2));
		
		String str3 = "880    $6260-03/$1$a¥_¨Ê :$b¤H¥Á¤å¾Ç¥Xª©ªÀ,$c1985.";
		System.out.println(CJKStringHandling.big5ToUnicode(str3));
		
		String str4 = "Shek Mun Campus Library";
		GenStringHandling strHandle = new GenStringHandling();
		System.out.println(strHandle.normalizeString(str4));
		
	}
}