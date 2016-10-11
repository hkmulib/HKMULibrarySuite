package hk.edu.csids;

import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.text.SimpleDateFormat;
import java.util.Date;


public class GenStringHandling {

	private CJKStringHandling cjkStrHandle = new CJKStringHandling();
	
	public String removeAccents(String text) {
		return text == null ? null
				: Normalizer.normalize(text, Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
	} // removeAccents()

	public String trimSpecialChars(String str) {
		if (str == null) {
			return "";
		} // end if
		String[] specChars = { "\\=", "\\-", "\\^", "#", "/", "@", "%", "&", "~", "[\\s|\t]and[\\s|\t]",
				"[\\s|\t]or[\\s|\t]" };

		String[] specChars2 = { "《", "》", "〈", "〉", ",", "「", "」", "\"", "？", "‘", "’", "‚", "“", "”", "†", "‡", "‰",
				"‹", "›", "♠", "♣", "♥", "♥", "♦", "‾", "←", "↑", "→", "↓", "™", "\\+", "\\*", "'", "\\.", "\\\\",
				"\\+", "\\?", "\\[", "\\]", "\\$", "\\(", "\\)", "\\{", "\\}", "\\!", "\\<", "\\>", "\\|", "。", "、",
				":", "・" };

		for (int i = 0; i < specChars.length; i++) {
			str = str.replaceAll(specChars[i], " ");
		} // end for

		for (int i = 0; i < specChars2.length; i++) {
			str = str.replaceAll(specChars2[i], "");
		} // end for

		str = str.replaceAll("^\\s{1,}|\\s{1,}$|^\t{1,}|\t${1,}", "");
		str = str.replaceAll("\\s{2,}|\t{2,}", " ");
		str = str.trim();
		return str;
	} // end trimSpecialChars()
	
	public String trimSpaces(String str){
		str = str.replaceAll("\\n|\\t", "");
		return str;
	} //end trimSpace()
	
	public String extractLast5Words(String str){
		
		if(!hasSomething(str))
			return "";
		
		if(cjkStrHandle.isCJKString(str)){
			str = cjkStrHandle.removeNonCJKChars(str);
			if(str.length()<5)
				return str;
			String s = "";
			for(int i=str.length()-5; i<str.length(); i++){
				s+=str.charAt(i);
			} //end for
			return s;
		} else {
			String[] arry = str.split("\\s|\\t");
			if(arry.length>5){
				str = "";
				for(int i=arry.length-6; i<arry.length; i++){
					str += arry[i] + " ";
				} //end for
				str = str.trim();
				return str;
				 
			} //end if
		} //end if
		
		return str;
	} //end extractLast5Chars()

	public String tidyString(String str) {
		if (str == null) {
			return "";
		} // end if
		str = str.toLowerCase();
		str = trimSpecialChars(str);
		str = removeAccents(str);
		// remove articles
		str = str.replaceAll("^a |^the |^an |^le |^la |^ |^die |^der |^das " + "|^el |\"|&quot;|quot|&apos|apos|;", "");
		str = str.replaceAll("&| & | and | but |  |\\/|\\-", " ");
		return str;
	} // end generalStringProcess()

	public String normalizeString(String str) {
		if (str == null) {
			return "";
		} // end if
		str = str.toUpperCase();
		str = str.replace(" ", "");
		str = str.replace("\"", "");
		str = str.replace("/", "");
		str = str.replace("-", "");
		return str;
	} // end normalizeString()

	public String extractNumeric(String str) {
		if (str.contains("-")) {
			str = str.replaceAll("[^0-9]", "");
			str = "-" + str;
		} else {
			str = str.replaceAll("[^0-9]", "");
		} // end if
		return str;
	} // extractNumeric

	public boolean hasSomething(Object o) {
		if (o == null || o.toString().equals("")) {
			return false;
		} // end if
		return true;
	} // end hasSomething();
	
	public static String escapeRegExpReservedChars(String str){
		char[] espChars = {'.', '\'', '^', '$', '*', '+', '?', '(', ')', '[', '{', '}', '|' };
		for(int j=0; j<espChars.length; j++){
			if(espChars[j]!= '\\'){
				str = str.replaceAll("\\" + espChars[j], ""+ '\\' + '\\' + espChars[j]);
			} //end if
		} //end for
		return str;
	} //end escapeRegExpReservedChars()
	
	public static String getToday(){
        Date today = new Date();
        SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        return formatter.format(today);
	} //end getToday()

} // end class