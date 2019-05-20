import java.util.Arrays;

import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.commons.validator.routines.UrlValidator;

public class TestString {
	public static void main(String[] args) {
		String url = "http://www.lib.ouhk.edu.hk/cgi-bin/redirect.cgi?url=http://www.aspresolver.com/aspresolver.asp?CTIV;534927";
		
		UrlValidator urlValidator = new UrlValidator();
		System.out.println(urlValidator.isValid(url));
	
		String str = "$x0001-8899";
		str=str.replace("-", "");
		System.out.println(str);
		str = str.trim();
		String str2 = new String(str);
		if (str2.matches("^[0-9]") || !str2.matches(".*[0-9].*") || str2.matches("^\\..*")
				|| (!str2.matches("[a-zA-Z].*[\\s|\\t].*") && !str2.matches("[a-zA-Z].*\\..*")) )
			str = null;

		if (str != null && !str.matches("^[a-zA-Z].*\\..*")) {
			System.out.println("Before changed Call # " + str);
			str = str.replaceAll("^([a-zA-Z].*?)[\\s|\\t](.*)", "$1 .$2");
			System.out.println("CHANGED Call # " + str);
		}

		System.out.println("FINAL Call # " + str);
	}
}
