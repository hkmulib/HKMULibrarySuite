package hk.edu.csids.bookquery;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;

import hk.edu.csids.BookItem;

//In Development
public class Z3950QueryByNonISBN extends Z3950Query {

	public Z3950QueryByNonISBN(String author, String title, String publisher, String year, String edition, String vol,
			String inst) {
		super(inst);
		clearQuery();
		queryBk = new BookItem();
		queryBk.setPublishYear(year);
		queryBk.setVolume(vol);
		queryBk.setEdition(edition);
		queryBk.setTitle(title);
		queryBk.setCreator(author);
		queryBk.setPublisher(publisher);
		if (query()) {
			checkAva(queryBk.parseVolume());
		} // end query()
	} // Z3950QueryByNonISBN()

	public Z3950QueryByNonISBN() {
		super();
	} // Z3950QueryByNonISBN()

	public boolean query(String author, String title, String publisher, String year, String edition, String vol,
			String inst) {
		clearQuery();
		Config.init(inst);
		queryBk.setTitle(title);
		queryBk.setPublisher(publisher);
		queryBk.setPublishYear(year);
		queryBk.setEdition(edition);
		queryBk.setVolume(vol);
		queryBk.setCreator(author);
		return query();
	} // end query()

	public boolean query() {
		String qt = queryBk.getTitle();

		if (!strHandle.hasSomething(qt)) {
			return false;
		} // end if

		if (cjkStrHandle.isCJKString(qt)) {
			qt = cjkStrHandle.removeNonCJKChars(qt);
		} // end if

		String qtc = "";
		String qc = queryBk.getCreator();
		String qcc = "";
		String inst = Config.VALUES.get("INST_CODE");
		String eaccSys = Config.VALUES.get("EACC_SYSTEM_" + inst);
		boolean isEACC = false;
		if(strHandle.hasSomething(eaccSys) && eaccSys.contains("YES"))
			isEACC = true;

		if (result != null && cjkStrHandle.isCJKString(qt) && isEACC) {
			int iterateNo = qt.length();

			if (iterateNo > 7) {
				iterateNo = 7;
			} // end if

			for (int i = 0; i < iterateNo; i++) {
				qtc += "{" + cjkStrHandle.UnicodeCharToEACCNumber(qt.charAt(i)) + "}";
			} // end for
			qt = qtc;
		} // end if

		if (result != null && cjkStrHandle.isCJKString(qc)  && isEACC) {
			for (int i = 0; i < qc.length(); i++) {
				qcc += "{" + cjkStrHandle.UnicodeCharToEACCNumber(qc.charAt(i)) + "}";
			} // end for
			qc = qcc;
		} // end if

		int noOfCri = 0;
		queryStr = "@attr 1=4 @attr 4=2 \"" + qt + "\" ";

		if (!queryBk.getCreator().equals("")) {
			queryStr += "@attr 1=1035 @attr 4=1 \"" + qc + "\" ";
			noOfCri++;
		} // end if

		if (strHandle.hasSomething(queryBk.getPublishYear())) {
			queryStr += "@attr 1=31 " + queryBk.getPublishYear();
			noOfCri++;
		} // end if

		for (int i = 0; i < noOfCri; i++) {
			queryStr = "@and " + queryStr;
		} // end for

		if (remoteQuery(queryStr)) {

			if (matchPublisher() || matchEdition()) {
				match = true;
				setBookInfo();
				return true;
			} // end if

			for (int i = 0; i < resultSet.getHitCount(); i++) {
				if (!nextRecord()) {
					result = "";
					resultBytes = null;
					return false;
				} else {
					if (matchPublisher() || matchEdition()) {
						match = true;
						setBookInfo();
						return true;
					} // end if
				} // end if
			} // end for
		} // end if

		result = "";
		resultBytes = null;
		return false;
	} // end query()

	private boolean matchEdition() {

		if (!strHandle.hasSomething(queryBk.getEdition())) {
			return false;
		} // end if

		try {
			BufferedReader bufReader = new BufferedReader(new StringReader(result));
			String line = null;
			while ((line = bufReader.readLine()) != null) {
				if (line.toLowerCase().matches("^250.*")) {
					line = line.replaceAll("^250.*\\$a", "");
					if (queryBk.parseEdition(line) == queryBk.parseEdition()) {
						return true;
					} // end if
				} // end if
			} // end while
		} // end try
		catch (Exception e) {
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			String errStr = "Z3950QueryByNonISBN:matchEdition()" + errors.toString();
			System.out.println(errStr);
			errMsg = errStr;
		} //end catch()

		return false;
	} // end matchEdition()

	private boolean matchPublisher() {
		String qPub = queryBk.removeCommonPublisherWording();
		if (!strHandle.hasSomething(qPub)) {
			return false;
		} // end if

		try {
			BufferedReader bufReader = new BufferedReader(new StringReader(result));
			String line = null;

			while ((line = bufReader.readLine()) != null) {

				if (line.toLowerCase().matches(".*260.*|.*264.*")) {

					line = line.replaceAll("^260.*\\$a|^264.*\\$a", "").toLowerCase();
					if (line.contains(queryBk.removeCommonPublisherWording().toLowerCase())) {
						return true;
					} // end if
				} // end if
			} // end while
		} // end try
		
		catch (Exception e) {
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			String errStr = "Z3950QueryByNonISBN:matchPublisher()" + errors.toString();
			System.out.println(errStr);
			errMsg = errStr;
		}
		return false;
	} // end matchPublisher()

}
