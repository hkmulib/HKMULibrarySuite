package hk.edu.csids.bookquery;

import java.util.*;
import java.util.regex.*;

import hk.edu.csids.*;

import javax.xml.parsers.*;

import org.w3c.dom.*;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;

/*
 * This class accepts parameters Author, Title, Publisher, Publishing Year, Edition, Volume, and Institute.
 * Author and Title are mandatory.
 * For Publisher, Publishing Year, or Edition, at least one must be filled.
 * Volume & Institute are optional.
 * "-1" is the default code for note specifying edition/volume information.
 * The "latest" edition is coded '0', no remote query will occur and will result false.
 * Primo first will be consulted for bibliographical checking; then ILS will be consulted for item avaibility.
 * "config.txt" controls network paths for query
 */

public class PrimoQueryByNonISBN extends PrimoQuery {

	// For debugging only
	private String matchTitle = "";
	private String matchAuthor = "";
	private String matchPublisher = "";
	private String matchEdition = "";
	private String matchYear = "";

	public PrimoQueryByNonISBN() {
		super();
	} // end PrimoQueryByISBN()

	// Constructor with institute code.
	// Default global variables, which originally are defined in "config.txt",
	// are changed accordingly.
	// Default global variables include: INST_CODE, SOURCE_ID, and LOCAL_SCOPE.
	public PrimoQueryByNonISBN(String author, String title, String publisher, String year, String edition, String vol,
			String inst) {
		super(inst);
		queryBk = new BookItem();
		queryBk.setPublishYear(year);
		queryBk.setVolume(vol);
		queryBk.setEdition(edition);
		queryBk.setTitle(title);
		queryBk.setCreator(author);
		queryBk.setPublisher(publisher);
		query();
	} // end PrimoQueryByISBN()

	// Constructor without institute code.
	// Using the default global variables defined in "config.txt".
	// Default gloal variables include: INST_CODE, SOURCE_ID, and LOCAL_SCOPE.
	public PrimoQueryByNonISBN(String author, String title, String publisher, String year, String edition, String vol) {
		super();
		queryBk.setPublishYear(year);
		queryBk.setVolume(vol);
		queryBk.setEdition(edition);
		queryBk.setTitle(title);
		queryBk.setCreator(author);
		queryBk.setPublisher(publisher);
		queryBk.getPublisher();
		query();
	} // end PrimoQueryByISBN()

	// Making multiple queries until reaches a match result.
	protected boolean query() {
		queryStr = new String(processQueryTitle(queryBk.getTitle()) + processQueryAuthor(queryBk.getCreator())
				+ processQueryPublisher(queryBk.getPublisher()));

		// if edition is 'lastest' (coded '0'), no query is performed; and
		// return false.
		if (queryBk.parseEdition() == 0) {
			return false;
		} // end if

		/*
		 * The following section adds edition info to the query string if
		 * edition no. is greater than one. By cataloging practice, for the
		 * first edition, probably there is NO input on the associated MARC
		 * field. Considering this, edition query string to Primo is NOT added
		 * if querying for the first edition or no edition is specified.
		 */
		if (queryBk.parseEdition() > 1) {
			queryStr += processQueryEdition(queryBk.parseEdition());
		} // end if

		/*
		 * Querying the Primo X-service; and invoking the matching processes
		 * (all done by remoteQuery()).
		 */
		if (strHandle.hasSomething(queryBk.getPublisher())) {
			if (remoteQuery(queryStr)) {
				match = true;
				setBookInfo();
				checkAVA(queryBk.parseVolume());
				return true;
			} else {
				match = false;
			} // end if
		} // end if

		/*
		 * For various reasons, there are possibilities that the first query
		 * fails while a match should be found. The follow work as remedy
		 * queries to ensure the accuracy.
		 */

		if (!match && strHandle.hasSomething(queryBk.getPublisher())) {
			queryStr = new String(
					processQueryPublisher(queryBk.getPublisher()) + processQueryAuthor(queryBk.getCreator()));
			if (remoteQuery(queryStr)) {
				match = true;
				setBookInfo();
				checkAVA(queryBk.parseVolume());
				return true;
			} else {
				match = false;
			} // end if
		} // end if

		if (!match) {
			queryStr = new String(processQueryTitle(queryBk.getTitle()) + processQueryAuthor(queryBk.getCreator()));
			if (remoteQuery(queryStr)) {
				match = true;
				setBookInfo();
				checkAVA(queryBk.parseVolume());
				return true;
			} else {
				match = false;
			} // end if
		} // end if

		if (!match) {
			queryStr = new String(processQueryTitle(queryBk.getTitle()));
			if (remoteQuery(queryStr)) {
				match = true;
				setBookInfo();
				checkAVA(queryBk.parseVolume());
				return true;
			} else {
				match = false;
			} // end if
		} // end if

		if (!match && strHandle.hasSomething(queryBk.getPublishYear())) {
			queryStr = new String(
					processQueryAuthor(queryBk.getCreator()) + processQueryYear(queryBk.getPublishYear()));

			if (remoteQuery(queryStr)) {
				match = true;
				setBookInfo();
				checkAVA(queryBk.parseVolume());
				return true;
			} else {
				match = false;
			} // end if
		} // end if

		if (!match) {
			queryStr = new String(
					processQueryAuthor(queryBk.getCreator()) + processQueryTitleShort(queryBk.getTitle()));
			if (remoteQuery(queryStr)) {
				match = true;
				setBookInfo();
				checkAVA(queryBk.parseVolume());
				return true;
			} else {
				match = false;
			} // end if
		} // end if

		// Additional query for Chinese titles

		if (!match && (cjkStrHandle.isCJKString(queryBk.getTitle()) || cjkStrHandle.isCJKString(queryBk.getCreator())
				|| cjkStrHandle.isCJKString(queryBk.getPublisher()))) {

			queryStr = new String(processQueryTitle(queryBk.getTitle()));
			if (remoteQuery(queryStr)) {
				match = true;
				setBookInfo();
				checkAVA(queryBk.parseVolume());
				return true;
			} else {
				match = false;
			} // end if

			if (!match && queryBk.parseEdition() != -1) {
				queryStr = new String(
						processQueryTitle(queryBk.getTitle()) + processQueryEdition2(queryBk.parseEdition()));
				if (remoteQuery(queryStr)) {
					match = true;
					setBookInfo();
					checkAVA(queryBk.parseVolume());
					return true;
				} else {
					match = false;
				} // end if

				if (!match && queryBk.parseEdition() != -1) {
					queryStr = new String(
							processQueryTitle(queryBk.getTitle()) + processQueryEdition3(queryBk.parseEdition()));
					if (remoteQuery(queryStr)) {
						match = true;
						setBookInfo();
						checkAVA(queryBk.parseVolume());
						return true;
					} else {
						match = false;
					} // end if
				} // end if
			} // end if
		} // end if

		// Additional check for ISO Document number in <search> <genernal> PNX
		// tag
		if (!match && !cjkStrHandle.isCJKString(queryBk.getTitle())) {
			queryStr = new String(processQueryTitleISODoc(queryBk.getTitle()));
			if (remoteQuery(queryStr)) {
				match = true;
				setBookInfo();
				checkAVA(queryBk.parseVolume());
				return true;
			} else {
				match = false;
				errMsg += "Query: No record found on Primo." + Config.QUERY_SETTING;
			} // end if
		} // end if
		return false;
	} // end query()

	// Called by query(), to fetch remote Primo PNX records.
	@Override
	protected boolean remoteQuery(String qstr){
		if (queryBk.getPublishYear() == null || queryBk.getPublishYear().equals("")) {
			if (queryBk.getPublisher() == null || queryBk.getPublisher().equals("")) {
				if (queryBk.parseEdition() == -1 && queryBk.parseVolume() == -1) {
					return false;
				} // end if
			} // end if
		} // end if

		DocumentBuilderFactory f;
		DocumentBuilder b;
		Document doc;
		try {
			f = DocumentBuilderFactory.newInstance();
			b = f.newDocumentBuilder();
			URL url = new URL(Config.PRIMO_X_BASE + qstr);

			URLConnection con = url.openConnection();
			con.setConnectTimeout(3000);
			doc = b.parse(con.getInputStream());
			queryStr = Config.PRIMO_X_BASE + qstr;
			// System.out.println(Config.PRIMO_X_BASE + qstr);

			nodesRecord = doc.getElementsByTagName("record");

			/*
			 * After fetched a XML doc, store necessary tags from the XMLs for
			 * further matching.
			 */

			for (int i = 0; i < nodesRecord.getLength(); i++) {
				nodesControl = doc.getElementsByTagName("control").item(i).getChildNodes();
				nodesDisplay = doc.getElementsByTagName("display").item(i).getChildNodes();
				nodesLink = doc.getElementsByTagName("links").item(i).getChildNodes();
				nodesSearch = doc.getElementsByTagName("search").item(i).getChildNodes();
				nodesDelivery = doc.getElementsByTagName("delivery").item(i).getChildNodes();
				nodesFacet = doc.getElementsByTagName("facets").item(i).getChildNodes();

				// Return true if the query item is of ISO doc no. and of
				// published by ISO
				if (matchIsoPublisher() && matchIsoDocNo()) {
					
					return true;
				} // end if

				if (!strHandle.hasSomething(queryBk.getCreator())) {
					if (matchTitle() && matchPublisher() && matchYear()) {
						return true;
					} // end if
				} // end if

				if (matchTitle() && matchAuthor()) {

					if ((matchEdition() && matchPublisher() && matchYear())) {
						return true;
					} else if (!strHandle.hasSomething(queryBk.getPublisher()) && matchYear()
							&& strHandle.hasSomething(queryBk.getPublishYear())) {
						return true;
					} else if (matchEdition() && queryBk.parseEdition() > 1) {
						return true;
					} else {
						/*
						 * As Primo X-service only shows the first FRBRed
						 * record, check the rest records involving the same
						 * frbrgroupid.
						 */
						String frbrid = "";
						frbrid = getNodeValue("frbrgroupid", nodesFacet);
						String frbr_qstr = qstr + "&query=facet_frbrgroupid,exact," + frbrid;
						// System.out.println("FRBR:" + Config.PRIMO_X_BASE +
						// frbr_qstr);
						Document doc2 = b.parse(Config.PRIMO_X_BASE + frbr_qstr);
						nodesFrbrRecord = doc2.getElementsByTagName("record");

						for (int j = 0; j < nodesFrbrRecord.getLength(); j++) {

							nodesControl = doc2.getElementsByTagName("control").item(j).getChildNodes();
							nodesDisplay = doc2.getElementsByTagName("display").item(j).getChildNodes();
							nodesLink = doc2.getElementsByTagName("links").item(j).getChildNodes();
							nodesSearch = doc2.getElementsByTagName("search").item(j).getChildNodes();
							nodesDelivery = doc2.getElementsByTagName("delivery").item(j).getChildNodes();
							nodesFacet = doc2.getElementsByTagName("facets").item(j).getChildNodes();

							if (!strHandle.hasSomething(queryBk.getPublishYear()) && queryBk.parseEdition() == -1
									&& !strHandle.hasSomething(queryBk.getPublisher())) {
								return false;
							} // end if

							if (matchEdition() && matchTitle() && matchAuthor() && matchPublisher() && matchYear()) {
								return true;
							} else if (matchEdition() && matchTitle() && matchAuthor() && matchYear()
									&& !strHandle.hasSomething(queryBk.getPublisher())) {
								return true;
							} // end if
						} // end for
					} // end if
				} // end if
			} // end for
		} // end try

		catch (Exception e) {
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			String errStr = "PrimoQueryByNonISBN:remoteQuery()" + errors.toString();
			System.out.println(errStr);
			errMsg = errStr;
		}
		return false;
	} // end remoteQuery()

	private String processAuthor(String str) {
		str = strHandle.tidyString(str);
		return str;
	} // end processAuthor()

	private String processTitle(String str) {
		str = strHandle.tidyString(str);
		// triming off any non-character symbols
		str = str.replaceAll("\\s|:|：|、|,|:|\"", "");

		// remove any space if the title is in Chinese
		CJKStringHandling ch = new CJKStringHandling(str);
		if (ch.isCJK()) {
			str = str.replaceAll("\\s", "");
			ch.standardizeVariantChineseFast();
			ch.standardizeVariantChineseForLooseMaching();
			ch.convertToSimpChinese();
			str = ch.convertToSimpChinese();
		} // end if
			// System.out.println("ProcessTitle(): " + str);
		return str;
	} // end processTitle

	private boolean matchAuthor() {
		
		String a = strHandle.removeAccents(queryBk.getCreator());
		
		if(!strHandle.hasSomething(a))
			return false;
		
		String cPnx = getNodeValue("creator", nodesDisplay) + getNodeValue("contributor", nodesDisplay);

		cPnx = processAuthor(cPnx).replaceAll("\\s", "");
		CJKStringHandling chA = new CJKStringHandling(a);
		CJKStringHandling chCPnx = new CJKStringHandling(cPnx);

		if (chA.isCJK()) {
			chA.standardizeVariantChineseFast();
			chA.standardizeVariantChineseForLooseMaching();
			a = chA.convertToSimpChinese();
		} // end if

		if (chCPnx.isCJK()) {
			chCPnx.standardizeVariantChineseFast();
			chCPnx.standardizeVariantChineseForLooseMaching();
			cPnx = chCPnx.convertToSimpChinese();
		} // end if

		String[] auKeys = a.split(" ");

		for (int i = 0; i < auKeys.length; i++) {
			String key = auKeys[i].toLowerCase();
			if (cPnx.contains(key) && key.length() > 1) {
				matchAuthor = "MATCH AUTHOR: cPnx: " + cPnx + " key: " + key;
				// System.out.println(matchAuthor);
				return true;
			} // end if
		} // end for
		matchAuthor = "NO MATCH AUTHOR: cPnx: " + cPnx + " a: " + a;
		// System.out.println(matchAuthor);
		return false;
	} // end matchAuthor()

	private boolean matchIsoPublisher() {
		String p = queryBk.getPublisher().toLowerCase();
		p = strHandle.trimSpecialChars(p);
		if (p.equals("iso") || p.contains("international organization for standardization")) {
			return true;
		} // end if

		return false;
	} // end matchIsoPublisher()

	private boolean matchPublisher() {
		String p = strHandle.tidyString(queryBk.removeCommonPublisherWording());
		String pPnx = strHandle
				.tidyString(queryBk.removeCommonPublisherWording(getNodeValue("publisher", nodesDisplay)));
		String[] pubKeys;
		CJKStringHandling chP = new CJKStringHandling(p);
		CJKStringHandling chPnx = new CJKStringHandling(pPnx);
		if (chP.isCJK() || chPnx.isCJK()) {
			p = chP.convertToSimpChinese();
			pPnx = chPnx.convertToSimpChinese();
		} // end if

		int len = 0;
		if (chPnx.isCJK()) {
			len -= 1;
			pubKeys = new String[pPnx.length()];
			for (int i = 0; i < pPnx.length(); i++) {
				pubKeys[i] = String.valueOf(pPnx.charAt(i));
				if (i < pPnx.length() - 1) {
					pubKeys[i] += String.valueOf(pPnx.charAt(i + 1));
				} // end if
			} // end for
		} else {
			pubKeys = pPnx.split(" ");
		} // end if

		len += pubKeys.length;

		for (int i = 0; i < len; i++) {
			String key = pubKeys[i].toLowerCase();
			if (p.toLowerCase().contains(key)) {
				matchPublisher = "PUB MATCH: PNX - " + pPnx + " p:" + p;
				// System.out.println(matchPublisher);
				return true;
			} // end if
		} // end for
		matchPublisher = "NO PUB MATCH: PNX - " + pPnx + " p:" + p;
		// System.out.println(matchPublisher);
		return false;
	} // end matchPublisher()

	private boolean matchTitle() {
		String t = processTitle(queryBk.getTitle());
		String tPnx = processTitle(getNodeValue("title", nodesDisplay).toLowerCase());
		CJKStringHandling chT = new CJKStringHandling(t);
		CJKStringHandling chTPnx = new CJKStringHandling(tPnx);
		if (chT.isCJK()) {
			chT.standardizeVariantChineseFast();
			chT.standardizeVariantChineseForLooseMaching();
			t = chT.convertToSimpChinese();
		} // end if
		if (chTPnx.isCJK()) {
			chTPnx.standardizeVariantChineseFast();
			chT.standardizeVariantChineseForLooseMaching();
			tPnx = chTPnx.convertToSimpChinese();
		} // end if
		ArrayList<String> atl = getNodeValues("alttitle", nodesSearch);

		if (tPnx.contains(t)) {
			matchTitle = "MATCH TITLE: tPnx: " + tPnx + " t:" + t + ":";
			// System.out.println(matchTitle);
			return true;
		} // end if

		for (int i = 0; i < atl.size(); i++) {
			String t3 = processTitle(atl.get(i));
			if (t3.contains(t)) {
				matchTitle = "MATCH TITLE: t: " + t + " t3:" + t3;
				// System.out.println(matchTitle);
				return true;
			} // end if
		} // end for
		matchTitle = "NO MATCH TITLE: tPnx: " + tPnx + " t:" + t;
		// System.out.println(matchTitle);
		return false;
	} // matchTitle()

	private boolean matchIsoDocNo() {
		String t = queryBk.getTitle();
		String t2;
		t = t.replace(" ", "");
		t.toLowerCase();
		ArrayList<String> atl = getNodeValues("general", nodesSearch);
		for (int i = 0; i < atl.size(); i++) {
			t2 = atl.get(i).replace(" ", "").toLowerCase();
			if (t2.contains(t2)) {
				return true;
			} // endif
		} // end for
		return false;
	} // end matchIsoDocNo()

	private boolean matchEdition() {
		String e = getNodeValue("edition", nodesDisplay);
		if (queryBk.parseEdition() == -1) {
			return true;
		} else {
			if (e == null || e.equals("")) {
				e = "-1";
			} // end if
			if (queryBk.parseEdition() == queryBk.parseEdition(e)) {
				matchEdition += " MATCH EDITION ";
				matchEdition += " edition: " + queryBk.parseEdition();
				matchEdition += " e: " + e;
				// System.out.println(matchEdition);
				return true;
			} // end if
		} // end if
		matchEdition += " NOT MATCH EDITION ";
		matchEdition += " edition: " + queryBk.parseEdition();
		matchEdition += " e: " + e;
		// System.out.println(matchEdition);
		return false;
	} // end matchEdition()

	private boolean matchYear() {
		String cPnx = getNodeValue("creationdate", nodesDisplay);
		if (queryBk.getPublishYear() == null) {
			matchYear = "MATCH YEAR: PNX -" + cPnx + " year - " + queryBk.getPublishYear();
			// System.out.println(matchYear);
			return true;
		} // end if
		if (cPnx.contains(queryBk.getPublishYear())) {
			matchYear = "MATCH YEAR: PNX -" + cPnx + " year - " + queryBk.getPublishYear();
			// System.out.println(matchYear);
			return true;
		} // end if
		matchYear = "NO MATCH YEAR: PNX -" + cPnx + " year - " + queryBk.getPublishYear();
		// System.out.println(matchYear);
		return false;
	} // end matchYear()

	private String processQueryEdition(double i) {
		String str = "";
		String noStr = "";
		int i2 = (int) i;
		switch (i2) {
		case 2:
			noStr = "nd";
			break;
		case 3:
			noStr = "rd";
			break;
		default:
			noStr = "th";
		} // end switch
		str += "&query=" + Config.VALUES.get("PRIMO_SEARCHFIELD_EDITION") + "general,contains," + i2 + noStr + "%20ed";

		if (cjkStrHandle.isCJKString(queryBk.getTitle())) {
			str = "&query=" + Config.VALUES.get("PRIMO_SEARCHFIELD_EDITION") + ",contains," + i2 + "版";
		} // end if
		return str;
	} // end processQueryEdition()

	private String processQueryEdition2(double i) {
		if (i == 1) {
			return "";
		} // end if

		String str = "&query=" + Config.VALUES.get("PRIMO_SEARCHFIELD_EDITION") + ",contains,第" + (int) i;

		String[] chiEd = { "一", "二", "三", "四", "五", "六", "七", "八", "九", "十", "十一", "十二", "十三", "十四", "十五", "十六", "十七",
				"十八", "十九", "廿", "廿一", "廿二", "廿三", "廿四", "廿五", "廿六", "廿七", "廿八", "廿九", "卅" };

		for (int j = chiEd.length; 1 < j; j--) {
			str = str.replaceAll(Integer.toString(j), chiEd[j - 1]);
		} // end for

		str += "版";

		return str;
	} // end processQueryEdition2()

	private String processQueryEdition3(double i) {
		String str = "&query=" + Config.VALUES.get("PRIMO_SEARCHFIELD_EDITION") + ",contains," + (int) i;

		String[] chiEd = { "一", "二", "三", "四", "五", "六", "七", "八", "九", "十", "十一", "十二", "十三", "十四", "十五", "十六", "十七",
				"十八", "十九", "廿", "廿一", "廿二", "廿三", "廿四", "廿五", "廿六", "廿七", "廿八", "廿九", "卅" };

		for (int j = chiEd.length; 1 < j; j--) {
			str = str.replaceAll(Integer.toString(j), chiEd[j - 1]);
		} // end for

		str += "版";

		return str;
	} // end processQueryEdition2()

	private String processQueryPublisher(String str) {
		if (!strHandle.hasSomething(str)) {
			return "";
		} // end if
		String queryPublisher = "";
		str = queryBk.removeCommonPublisherWording(str);
		String[] sArry = str.split(" ");

		/*
		 * "lsr02" is the local search field defined by OUHK as publisher This
		 * may need a change for various reason
		 */
		queryPublisher += "&query=" + Config.VALUES.get("PRIMO_SEARCHFIELD_PUBLISHER") + ",contains," + sArry[0];

		return queryPublisher;
	} // end processQueryPublisher()

	private String processQueryAuthor(String str) {
		if (!strHandle.hasSomething(str)) {
			return "";
		} // end if
		String[] sArry;
		String queryAuthor = "";
		str = strHandle.tidyString(str);
		sArry = str.split(" ");
		queryAuthor = "&query=" + Config.VALUES.get("PRIMO_SEARCHFIELD_AUTHOR") + ",contains," + sArry[0];
		return queryAuthor;
	} // end processQueryAuthor()

	private String processQueryTitleISODoc(String t) {

		if (!strHandle.hasSomething(t)) {
			return "";
		} // end if

		t = t.toLowerCase();
		t = t.replace("/", "%2F");
		t = t.replace(" ", "%20");
		t = t.replace(":", "%3A");

		return "&query=general,contains,reference&query=general,contains," + t;
	} // end processQueryTitleISODoc()

	private String processQueryTitle(String t) {

		if (!strHandle.hasSomething(t)) {
			return "";
		} // end if

		t = strHandle.tidyString(t);
		t = t.replaceAll("\\d+dst|\\d+nd|\\d+rd|\\d+th|" + "|[0-9]s$|[0-9]s |[0-9]th |[0-9]st|[0-9]nd |[0-9]rd"
				+ "|\\.|^\\s+|,|\\?|:|'s|\\+|\\#|\\!|\\*|\\\"", "");
		t = t.replaceAll("\\w*\\d\\w*", " ");
		t = t.replaceAll("\\s\\s", " ");
		t = t.trim();

		CJKStringHandling ch = new CJKStringHandling(t);
		if (ch.isCJK()) {
			t = t.replaceAll("[a-z]|[A-Z]|\\s|[0-9]", "");
			t = t.replaceAll("\\s", "");
			ch = new CJKStringHandling(t);
			t = ch.standardizeVariantChineseFast();
			t = ch.standardizeVariantChineseForLooseMaching();
		} // end if

		String[] tArr = t.split(" ");
		String query = "";
		if (tArr.length == 1) {
			query = "&query=title,contains," + t;
		} // end if

		for (int i = 0; i < tArr.length; i++) {
			query += "&query=title,contains," + tArr[i] + "*";
		} // end for

		// System.out.println("processQueryTitle(): " + query);
		return query;
	} // end processQueryTitle()

	private String processQueryTitleShort(String t) {
		if (!strHandle.hasSomething(t)) {
			return "";
		} // end if
		String[] tArr = t.split(" ");
		String t2 = "";
		if (tArr.length == 1) {
			return "&query=title,contains," + t;
		} // end if

		if (tArr.length >= 2) {
			for (int i = 0; i < 2; i++) {
				t2 += "&query=title,contains," + tArr[i] + "*";
			} // end for
		} else {
			t2 += "&query=title,contains," + tArr[0] + "*";
		} // end if
		return t2;
	} // end processQueryTitleShort()

	private String processQueryYear(String str) {
		if (!strHandle.hasSomething(str)) {
			return "";
		} // end if
		if (str == null || str == "") {
			return "";
		} // end if
		Pattern pat = Pattern.compile("(\\d\\d\\d\\d)");
		Matcher mat = pat.matcher(str);
		if (mat.find()) {
			return "&query=cdate,contains," + mat.group(1);
		} // end if
		return "";
	} // end processQueryYear

} // end class PrimoQueryByISBN