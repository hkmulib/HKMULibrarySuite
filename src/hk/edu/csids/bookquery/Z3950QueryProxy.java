package hk.edu.csids.bookquery;

//In development
public class Z3950QueryProxy extends Z3950Query {

	public Z3950QueryProxy(String qStr, String inst) {
		super(inst);
		queryStr = qStr;
		if (query()) {
			checkAva(-1);
		} // end if
	} // end Z3950QueryByISBN()

	public Z3950QueryProxy() {
		super();
	} // end Z3950QueryByISBN()

	public boolean query() {
		return remoteQuery(queryStr);
	} // end query()

} // end class Z3950QueryByISBN
