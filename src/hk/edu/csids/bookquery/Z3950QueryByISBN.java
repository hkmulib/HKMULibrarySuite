package hk.edu.csids.bookquery;

import java.io.PrintWriter;
import java.io.StringWriter;

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
		if (!strHandle.hasSomething(queryBk.isbn.getOriginalIsbn())) {
			queryStr = "";
			return false;
		} // end if

		try {
			queryStr = "@attr 1=7 @attr 2=3 " + queryBk.isbn.getOriginalIsbn();
			if (remoteQuery(queryStr)) {
				match = true;
				setBookInfo();			
				while(!checkAva(-1) && nextRecord()){
					copyNextRecToCurrentRec();
					setBookInfo();
				} //end while
				
				return true;
			} //end if
		} //end try
		catch (Exception e){
			try{
				if(remoteQuery(queryStr)){
					setBookInfo();
					match = true;
					checkAva(-1);
					return true;
				} //end if
			} //end try
			catch(Exception e2){
				StringWriter errors = new StringWriter();
				e.printStackTrace(new PrintWriter(errors));
				String errStr = "Z3950Query:query()" + errors.toString();
				errMsg = errStr;
				System.out.println(errStr);	
			} //end catch
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			String errStr = "Z3950Query:query()" + errors.toString();
			errMsg = errStr;
			System.out.println(errStr);
		} //end catch
		return false;
	} // end query()

} // end class Z3950QueryByISBN
