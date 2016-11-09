package hk.edu.csids;

import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GenStringHandling {
	public String removeAccents(String text) {
		return text == null ? null
				: Normalizer.normalize(text, Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
	} // removeAccents()
	
	public String removeFirstArticles(String str){
		str = str.replaceAll("^a ", "");
		str = str.replaceAll("^an ", "");
		str = str.replaceAll("^the ", "");
		str = str.replaceAll("^la ", "");
		str = str.replaceAll("^les ", "");
		str = str.replaceAll("^die ", "");
		str = str.replaceAll("^der ", "");
		str = str.replaceAll("^das ", "");
		str = str.replaceAll("^eine ", "");
		return str;
	} //end removeFirstArticles();
	
	public String removeArticles(String str){
		str = str.replaceAll("^a ", "");
		str = str.replaceAll("^an ", "");
		str = str.replaceAll("^the ", "");
		str = str.replaceAll("^la ", "");
		str = str.replaceAll("^les ", "");
		str = str.replaceAll("^die ", "");
		str = str.replaceAll("^der ", "");
		str = str.replaceAll("^das ", "");
		str = str.replaceAll("^eine ", "");
		str = str.replaceAll(" a$", "");
		str = str.replaceAll(" an$", "");
		str = str.replaceAll(" the$", "");
		str = str.replaceAll(" la$", "");
		str = str.replaceAll(" les$", "");
		str = str.replaceAll(" die$", "");
		str = str.replaceAll(" die$", "");
		str = str.replaceAll(" der$", "");
		str = str.replaceAll(" das$", "");
		str = str.replaceAll(" a ", " ");
		str = str.replaceAll(" an ", " ");
		str = str.replaceAll(" the ", " ");
		str = str.replaceAll(" la ", " ");
		str = str.replaceAll(" les ", " ");
		str = str.replaceAll(" die ", " ");
		str = str.replaceAll(" der ", " ");
		str = str.replaceAll(" das ", " ");
		str = str.replaceAll(" eine ", " ");
		return str;
	} //end removeArticles

	public String trimSpecialChars(String str) {
		if (str == null) {
			return "";
		} // end if
		String[] specChars = { "\\=", "\\-", "\\^", "#", "/", "@", "%", "&", "~", "[\\s|\t]or[\\s|\t]" };

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

	public String trimNewLineChar(String str) {
		str = str.replaceAll("\\n|\\t", "");
		return str;
	} // end NewLineChar()
	
	public String trimSpace(String str) {
		str = str.replaceAll(" |\\s", "");
		return str;
	} // end trimSpace()

	public String extractLast5Words(String str) {

		if (!hasSomething(str))
			return "";

		if (CJKStringHandling.isCJKString(str)) {
			str = CJKStringHandling.removeNonCJKChars(str);
			if (str.length() < 5)
				return str;
			String s = "";
			for (int i = str.length() - 5; i < str.length(); i++) {
				s += str.charAt(i);
			} // end for
			return s;
		} else {
			String[] arry = str.split("\\s|\\t");
			if (arry.length > 5) {
				str = "";
				for (int i = arry.length - 6; i < arry.length; i++) {
					str += arry[i] + " ";
				} // end for
				str = str.trim();
				return str;

			} // end if
		} // end if

		return str;
	} // end extractLast5Chars()

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
	
	public String extract4LongestAdjententKeywords(String str){
		
		String result = "";
		str = str.trim();
		String[] strs = str.split(" |\\s|\\t");
		
		if(strs.length<4 || strs.length == 4)
			return str;
		
		int longestIndex = -1;
		for(int i=0; i<strs.length; i++){
			if(longestIndex < strs[i].length())
				longestIndex = i;
		} //end for
		
		if(longestIndex == strs.length-1){
			result = strs[longestIndex-3] + " "  + strs[longestIndex-2] + " "  + strs[longestIndex-1] + " " + strs[longestIndex]; 
		} else {
			result = strs[longestIndex] + " " + strs[longestIndex+1]  + " " + strs[longestIndex+2]  + " " + strs[longestIndex+3]; 
		} //end if
		
		return result;
	} //end extract4LongestAdjententKeywords
	
	public boolean containWords(String str1, String str2){
		str1 = str1.trim();
		str2 = str2.trim();
		
		String[] str2s = str2.split(" |\\t|\\s");
		int matchCount = 0;
		for(int i=0; i<str2s.length; i++){
			if(str1.contains(str2s[i]))
				matchCount++;
		} //end for
		
		if(str2s.length == matchCount)
			return true;
		
		return false;
	} //end containWords()

	public String extractNumeric(String str) {
		if (str.contains("-")) {
			str = str.replaceAll("[^0-9]", "");
			str = "-" + str;
		} else {
			str = str.replaceAll("[^0-9]", "");
		} // end if
		return str;
	} // extractNumeric
	
	public String trimNumeric(String str) {
		if (str.contains("-")) {
			str = str.replaceAll("[0-9]", "");
			str = "-" + str;
		} else {
			str = str.replaceAll("[0-9]", "");
		} // end if
		return str;
	} // trimNumeric

	public boolean hasSomething(Object o) {
		if (o == null || o.toString().equals("")) {
			return false;
		} // end if
		return true;
	} // end hasSomething();

	public static String escapeRegExpReservedChars(String str) {
		char[] espChars = { '.', '\'', '^', '$', '*', '+', '?', '(', ')', '[', '{', '}', '|' };
		for (int j = 0; j < espChars.length; j++) {
			if (espChars[j] != '\\') {
				str = str.replaceAll("\\" + espChars[j], "" + '\\' + '\\' + espChars[j]);
			} // end if
		} // end for
		return str;
	} // end escapeRegExpReservedChars()

	public static String getToday() {
		Date today = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
		return formatter.format(today);
	} // end getToday()

} // end class