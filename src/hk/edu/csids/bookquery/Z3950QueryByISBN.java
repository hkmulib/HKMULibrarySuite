package hk.edu.csids.bookquery;

import hk.edu.csids.BookItem;

//In development
public class Z3950QueryByISBN extends Z3950Query {

	public Z3950QueryByISBN(String inst) {
		super(inst);
	} // end Z3950QueryByISBN()

	public Z3950QueryByISBN(String isbnStr, String inst) {
		super(inst);
		queryBk = new BookItem(isbnStr);
		queryBk.isbn.setIsbn(isbnStr);
		query();
	} // end Z3950QueryByISBN()

	public Z3950QueryByISBN() {
		super();
	} // end Z3950QueryByISBN()

	public boolean query(String isbn, String inst) {
		clearQuery();
		Config.init(inst);
		setQueryBase();
		queryBk.isbn.setIsbn(isbn);
		return query();
	} // end if

	public boolean query() {
		queryCount++;
		if (!strHandle.hasSomething(queryBk.isbn.getIsbn())) {
			queryStr = "";
			return false;
		} else {
			if (!queryBk.isbn.isValid()) {
				queryStr = "";
				return false;
			} // end if
		} // end if

		queryStr = "@attr 1=7 @attr 2=3 " + queryBk.isbn.getIsbn();
		if (remoteQuery(queryStr)) {
			match = true;
			checkAva(-1);
			setBookInfo();
			return true;
		} else {
			// try again
			if(remoteQuery(queryStr)){
				match = true;
				checkAva(-1);
				setBookInfo();
				return true;
			} //end if
		} //end if
		return false;
	} // end query()

} // end class Z3950QueryByISBN
