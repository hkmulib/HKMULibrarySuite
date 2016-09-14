package hk.edu.csids;

import org.apache.commons.validator.routines.ISBNValidator;
import java.util.*;

public class ISBN {
	private String oriIsbn;
	private String isbn;
	private List<String> isbns;
	private String isbn10;
	private String isbn13;
	private boolean validity;
	private ISBNValidator isbnVal;
	private GenStringHandling strHandle;

	public ISBN() {
		oriIsbn = "";
		isbn = "";
		isbn10 = "";
		isbn13 = "";
		validity = false;
		isbnVal = new ISBNValidator();
		isbns = new ArrayList<String>();
		strHandle = new GenStringHandling();
	} // end ISBN

	public ISBN(String str) {
		oriIsbn = new String(str);
		isbn = new String(str);
		isbn10 = "";
		isbn13 = "";
		validity = false;
		isbnVal = new ISBNValidator();
		isbns = new ArrayList<String>();
		strHandle = new GenStringHandling();
		setIsbn(str);
	} // end ISBN

	public void setIsbn(String str) {
		isbn = str;
		isbn = isbn.replaceAll("-|ISBN|\t|:|\\(.*\\)| ", "");
		isbn = isbn.replaceAll("[^0-9|X|x]", "");
		isbn = isbn.toUpperCase();
		if (isbnVal.isValidISBN10(isbn)) {
			isbn10 = new String(isbn);
			isbn13 = new String(isbnVal.convertToISBN13(isbn10));
			validity = true;
		} else if (isbnVal.isValidISBN13(isbn)) {
			isbn10 = null;
			isbn13 = new String(isbn);
			validity = true;
		} else {
			isbn13 = null;
			isbn10 = null;
			isbn = null;
			validity = false;
		} // end if
	} // end setIsbn()

	public void addIsbn(String str) {
		isbns.add(str);
	} // end Isbn()

	public String getIsbn10() {
		if (isbn10 == null && oriIsbn != null) {
			return "Invalid ISBN " + oriIsbn.replaceAll("[^0-9|X|x]", "");
		} else if (isbn10 == null) {
			return "";
		} // end if
		return isbn10;
	} // end getIsbn10()

	public String getIsbn() {
		if (isbn == null && oriIsbn != null) {
			return "Invalid ISBN " + oriIsbn.replaceAll("[^0-9|X|x]", "");
		} // end if

		if (strHandle.hasSomething(isbn))
			return isbn;

		if (strHandle.hasSomething(isbn13))
			return isbn13;

		if (strHandle.hasSomething(isbn10))
			return isbn10;

		return "";

	} // end getIsbn()

	public String getOriIsbn() {
		if (oriIsbn == null) {
			return "";
		} // end if
		return oriIsbn;
	} // end getOriIsbn()

	public String getIsbn13() {
		if (isbn13 == null) {
			return "Invalid ISBN " + oriIsbn.replaceAll("[^0-9|X|x]", "");
		} // end if
		return isbn13;
	} // end getIsbn13()

	public boolean getValidity() {
		return validity;
	} // end getValidState

	public String getIsbns() {
		String r = "";
		for (String s : isbns) {
			r += s + ",";
		} // end for
		return r;
	} // end getIsbns()

	public boolean isValid() {
		return validity;
	} // end isValid()

} // end class ChkBkByISBN()