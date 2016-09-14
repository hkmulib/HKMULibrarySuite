import java.nio.charset.Charset;

import hk.edu.csids.*;

public class TestChineseHandling
{
	public static void main(String[] args) {
		String str = "{123456}¬aæ¨æ´å¹³ 10¬aå°å±±åºä¸ç¶»æ¾çç«ç° :-bæ¥éµåä»çæå­¸ä¸ç /¬cæ¨æ´å¹³è 30-aæ¥éµåä»çæå­¸ä¸ç   ¬aç¬¬ 1 ç   ¬aåäº¬ :¬bä½å®¶åºçç¤¾,-c2006.   ¬a11, 2, 379 p. ;¬c21 cm.  0¬aèºç£ä½å®¶ç ç©¶å¢æ¸ ;¬v3.   ¬aæ¸ç®";
		
		CJKStringHandling ch = new CJKStringHandling();
		
		String strcon = new String(ch.EACCtoUnicodeChar(str.getBytes()));
		
		
		System.out.println(strcon + "HI");
		
	}
}