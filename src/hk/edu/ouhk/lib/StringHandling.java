package hk.edu.ouhk.lib;

import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * 
 * String conversion.
 * 
 * @author Wai-yan NG
 * @author wwyng@ouhk.edu.hk
 * @version 1.0
 * @since Jan 29, 2019
 */
public class StringHandling {
	public static String removeAccents(String text) {
		return text == null ? null
				: Normalizer.normalize(text, Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
	} // removeAccents()

	public static String removeFirstArticles(String str) {
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
	} // end removeFirstArticles();

	public static String removeArticles(String str) {
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
	} // end removeArticles

	public static String trimSpecialChars(String str) {
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

	public static String trimNewLineChar(String str) {
		str = str.replaceAll("\\n|\\t", "");
		return str;
	} // end NewLineChar()

	public static String trimSpace(String str) {
		str = str.replaceAll(" |\\s", "");
		return str;
	} // end trimSpace()

	public static String extractLast5Words(String str) {

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

	public static String tidyString(String str) {
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

	public static String normalizeString(String str) {
		if (str == null) {
			return "";
		} // end if
		str = str.toUpperCase();
		str = str.replaceAll(" |\\s|\\t", "");
		str = str.replace("\"", "");
		str = str.replace("/", "");
		str = str.replace("-", "");
		return str;
	} // end normalizeString()

	public static String trimAll(String str) {
		str = normalizeString(str);
		str = tidyString(str);
		str = trimSpecialChars(str);
		str = trimSpace(str);
		return str;
	}

	public static String extract3LongestAdjententKeywords(String str) {

		String result = "";
		str = str.trim();
		String[] strs = str.split(" |\\s|\\t");

		if (strs.length < 4 || strs.length == 4)
			return str;

		int longestIndex = -1;
		for (int i = 0; i < strs.length; i++) {
			if (longestIndex < strs[i].length())
				longestIndex = i;
		} // end for

		if (longestIndex == strs.length - 1) {
			result = strs[longestIndex - 2] + " " + strs[longestIndex - 1] + " " + strs[longestIndex];
		} else {
			result = strs[longestIndex] + " " + strs[longestIndex + 1] + " " + strs[longestIndex + 2];
		} // end if

		return result;
	} // end extract3LongestAdjententKeywords

	public static boolean containWords(String str1, String str2) {
		str1 = str1.trim();
		str2 = str2.trim();

		String[] str2s = str2.split(" |\\t|\\s");
		int matchCount = 0;
		for (int i = 0; i < str2s.length; i++) {
			if (str1.contains(str2s[i]))
				matchCount++;
		} // end for

		if (str2s.length == matchCount)
			return true;

		return false;
	} // end containWords()

	public static String extractNumeric(String str) {
		if (str.contains("-")) {
			str = str.replaceAll("[^0-9]", "");
			str = "-" + str;
		} else {
			str = str.replaceAll("[^0-9]", "");
		} // end if
		return str;
	} // extractNumeric

	public static String trimNumeric(String str) {
		if (str.contains("-")) {
			str = str.replaceAll("[0-9]", "");
			str = "-" + str;
		} else {
			str = str.replaceAll("[0-9]", "");
		} // end if
		return str;
	} // trimNumeric

	public static boolean hasSomething(Object o) {
		if (o == null || o.toString().trim().equals("")) {
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

	public static String getMonthByNum(String num) {
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("01", "Jan");
		hm.put("1", "Jan");
		hm.put("02", "Feb");
		hm.put("2", "Feb");
		hm.put("03", "Mar");
		hm.put("3", "Mar");
		hm.put("04", "Apr");
		hm.put("4", "Apr");
		hm.put("05", "May");
		hm.put("5", "May");
		hm.put("06", "Jun");
		hm.put("6", "Jun");
		hm.put("07", "Jul");
		hm.put("7", "Jul");
		hm.put("08", "Aug");
		hm.put("8", "Aug");
		hm.put("09", "Sep");
		hm.put("9", "Sep");
		hm.put("10", "Oct");
		hm.put("11", "Nov");
		hm.put("12", "Dec");
		String out = hm.get(num);
		if (out == null)
			out = "";
		return out;
	}

} // end class