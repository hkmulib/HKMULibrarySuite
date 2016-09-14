package hk.edu.csids.bookquery;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.regex.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.yaz4j.*;

//In development
public abstract class Z3950Query extends Query {

	protected String result;
	protected String holdingXML;
	protected ResultSet resultSet;
	protected long resultCurrInd;
	protected byte[] resultBytes;
	protected String resultMarc;
	private String queryBase;
	private String lastQueryInst;
	private Connection z39c;
	private org.yaz4j.Query query;
	protected static int queryCount;

	abstract boolean query();

	public Z3950Query(String inst) {
		super(inst);
		lastQueryInst = null;
		clearQuery();
		setQueryBase();
	} // end Z3950QueryByISBN()

	public Z3950Query(String qStr, String inst) {
		super(inst);
		lastQueryInst = null;
		clearQuery();
		setQueryBase();
		queryStr = qStr;
		query();
		checkAva(queryBk.parseVolume());
	} // end Z3950QueryByISBN()

	protected void clearQuery() {
		super.clearQuery();
		result = "";
		resultSet = null;
		resultCurrInd = 0;
		resultBytes = null;
		query = null;
		holdingXML = "";
	} // end clearZ395Query()

	public Z3950Query() {
		super();
	} // end Z3950QueryByISBN()

	public boolean checkAva(int vol) {

		if (holdingXML == null)
			return false;

		DocumentBuilderFactory f;
		DocumentBuilder b;
		Document doc;
		try {
			int avaT = 0;
			f = DocumentBuilderFactory.newInstance();
			b = f.newDocumentBuilder();
			boolean start = false;
			BufferedReader bufReader = new BufferedReader(new StringReader(holdingXML));
			holdingXML = "";
			String line = "";
			boolean holding = false;
			while ((line = bufReader.readLine()) != null) {
				if (line.toLowerCase().contains("<holdings>")) {
					start = true;
					holding = true;
				} else if (line.toLowerCase().contains("</holdings>")) {
					start = false;
				} // end if
				if (start) {
					holdingXML += line;
				} // end if
			} // end while

			if (holding) {
				holdingXML = holdingXML + "</holdings>";
				InputSource is = new InputSource(new StringReader(holdingXML));
				doc = b.parse(is);
				nodesHoldings = doc.getElementsByTagName("holding");
				for (int i = 0; i < nodesHoldings.getLength(); i++) {
					nodesHolding = doc.getElementsByTagName("holding").item(i).getChildNodes();
					String callNo = getNodeValue("callNumber", nodesHolding);
					String volume = callNo;
					volume = volume.replaceAll("^.*v\\.", "");
					String status = getNodeValue("publicNote", nodesHolding);
					String localLocation = getNodeValue("localLocation", nodesHolding);
					bk.holdingInfo.add(new ArrayList<String>());

					bk.holdingInfo.get(i).add(Config.VALUES.get("INST_CODE"));
					bk.holdingInfo.get(i).add(localLocation);
					bk.holdingInfo.get(i).add(callNo);
					bk.holdingInfo.get(i).add(status);

					status = strHandle.normalizeString(status);
					if ((status.equals("NOTCHCKDOUT") || status.contains("AVAILABLE")) && vol < 1) {
						avaT++;
						ava = true;
						bib_no = 1;
					} else if (vol > 0) {
						if (vol == queryBk.parseVolume(volume)) {
							avaT++;
							ava = true;
							bib_no = 1;
						} // end if
					} // end if

					if (avaT > 0) {
						ext_itm_no = avaT;
						ava_itm_no = avaT;
					} // end if
				} // end for
				if (!ava) {
					ava = false;
					bib_no = 1;
					ext_itm_no = 0;
					ava_itm_no = 0;
				} // end if
				return true;
			} // end if
		} // end try
		catch (Exception e) {
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			String errStr = "Z3950Query:checkAva()" + errors.toString();
			System.out.println(errStr);
			errMsg = errStr;
		} // end catch
		return false;
	} // end checkAva

	protected void setBookInfo() {
		if (match && strHandle.hasSomething(result)) {
			try {
				String m = result;
				boolean start = false;
				BufferedReader bufReader = new BufferedReader(new StringReader(result));
				String line = null;
				while ((line = bufReader.readLine()) != null) {
					if (line.toLowerCase().contains("<bibliographicrecord>")) {
						m = "";
						start = true;
						continue;
					} else if (line.toLowerCase().contains("</bibliographicrecord>")) {
						start = false;
						break;
					} // end if

					if (start) {
						line = line.trim();
						if (strHandle.hasSomething(line))
							m += line + "\r";

						if (line.matches("^020.*"))
							bk.isbn.addIsbn(parseMARCLineValue(line).trim());

						if (line.matches("^245.*"))
							bk.setTitle(parseMARCLineValue(line).trim());

						if (line.matches("^100.*"))
							bk.setCreator(parseMARCLineValue(line).trim());

						if (line.matches("^260.*|^264.*"))
							bk.setPublisher(parseMARCLineValue(line).trim());

					} // end if
				} // end while
				m = m.trim();
			} // end try
			catch (Exception e) {
				StringWriter errors = new StringWriter();
				e.printStackTrace(new PrintWriter(errors));
				String errStr = "Z3950Query:setbookInfo()" + errors.toString();
				System.out.println(errStr);
				errMsg = errStr;
			}
		} // end if
	} // end setBookInfo()

	private String parseMARCLineValue(String str) {
		String out = "";
		Pattern pat = Pattern.compile("\\$a(.*)");
		Matcher mat = pat.matcher(str);
		if (mat.find()) {
			out += mat.group(1);
		} // end i

		return out;
	} // end parseMARCLine

	public String getResult() {
		return result;
	} // getResult()

	public byte[] getResultBytes() {
		return resultBytes;
	} // end getResultBytes()

	protected boolean nextRecord() {

		resultCurrInd++;
		if (resultSet.getHitCount() < resultCurrInd + 1 || resultSet == null) {
			z39c.close();
			return false;
		} else {
			try {
				org.yaz4j.Record rec = resultSet.getRecord(resultCurrInd);
				if (rec != null) {
					resultBytes = rec.get("raw");
					bk.marc.setMarcRaw(resultBytes);
					resultBytes = bk.marc.getMarcTagRaw();
					result = bk.marc.getMarcTag();
					holdingXML = rec.render();
					return true;
				} else {
					result = "";
				} // end if
			} // end try
			catch (Exception e) {
				StringWriter errors = new StringWriter();
				e.printStackTrace(new PrintWriter(errors));
				String errStr = "Z3950Query:nextRecord()" + errors.toString();
				System.out.println(errStr);
				errMsg = errStr;
			}
		} // end if
		return false;
	} // end nextRecord()

	protected boolean remoteQuery(String queryStr) {
		
		if (queryBase == null) {
			return false;
		} // end if
		
		
		if (z39c == null) {
			try {
				z39c = new Connection(queryBase, 0);
				z39c.connect();
				z39c.setSyntax("opac");
				lastQueryInst = Config.VALUES.get("INST_CODE");
			} // end try

			catch (Exception e) {
				StringWriter errors = new StringWriter();
				e.printStackTrace(new PrintWriter(errors));
				String errStr = "Z3950Query:remoteQuery()" + errors.toString();
				errMsg = errStr;
			} //end catch
		
		} else if (!Config.VALUES.get("INST_CODE").equals(lastQueryInst)
					|| queryCount % 400 == 0) {
			try {
				queryCount = 0;
				z39c.close();
				Thread.sleep(3000);
				z39c = new Connection(queryBase, 0);
				z39c.connect();
				z39c.setSyntax("opac");
				lastQueryInst = Config.VALUES.get("INST_CODE");
			} // end try

			catch (Exception e) {
				StringWriter errors = new StringWriter();
				e.printStackTrace(new PrintWriter(errors));
				String errStr = "Z3950Query:remoteQuery()" + errors.toString();
				System.out.println(errStr);
				errMsg = errStr;
			} //end catch
		} // end if

		org.yaz4j.Record rec = null;
		try {
			query = new org.yaz4j.PrefixQuery(new String(queryStr));
			resultSet = z39c.search(query);
			rec = resultSet.getRecord(0);

			if (rec != null) {
				resultBytes = rec.get("raw");
				bk.marc.setMarcRaw(resultBytes);
				resultBytes = bk.marc.getMarcTagRaw();
				result = bk.marc.getMarcTag();
				holdingXML = rec.render();
			} else {
				result = "";
			} // end if
		} // end try

		catch (Exception e) {
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			String errStr = "Z3950Query:remoteQuery()" + errors.toString();
			System.out.println(errStr);
			errMsg = errStr;
			// Probably due to connection error. Try to connection again,
			// the caller of this function would try again.
			try {
				z39c.close();
				z39c = new Connection(queryBase, 0);
				z39c.connect();
				z39c.setSyntax("opac");
			} // end try
			catch (Exception e2) {
			}
			return false;
		} // end catch

		if (strHandle.hasSomething(result)) {
			return true;
		} // end if

		return false;
	} // end remoteQuery()

	void setQueryBase() {
		queryBase = Config.VALUES.get("Z3950_SERVER") + ":" + Config.VALUES.get("Z3950_PORT") + "/"
				+ Config.VALUES.get("Z3950_BASE");
	} // end setServerAddr()

	public String getQueryBase() {
		return queryBase;
	} // end getQueryBase()

	public boolean testConnection(String inst) {
		Config.init(inst);
		setQueryBase();

		class TestZ3950 implements Runnable {
			private String queryBase;
			private boolean isViable;

			TestZ3950(String str) {
				queryBase = str;
				isViable = false;
			} // end TesetZ3950

			public void run() {
				Connection z39c = new Connection(queryBase, 0);
				try {
					z39c.connect();
					String qStr = "@attr 1=4 philosophy";
					org.yaz4j.Query query = new org.yaz4j.PrefixQuery(new String(qStr));
					ResultSet resultSet = z39c.search(query);
					Record rec = resultSet.getRecord(0);
					if (rec == null)
						isViable = false;
					z39c.close();
				} // end try
				catch (Exception e) {
					StringWriter errors = new StringWriter();
					e.printStackTrace(new PrintWriter(errors));
					String errStr = "Z3950Query:testConnection()" + errors.toString();
					errMsg = errStr;
					isViable = false;
				} // end catch
				isViable = true;
			} // end run()

			public boolean isViable() {
				return isViable;
			} // end isViable()
		} // end class TestZ3950

		TestZ3950 tz = new TestZ3950(getQueryBase());

		ExecutorService executor = Executors.newFixedThreadPool(1);
		Future<?> future = executor.submit(tz);
		executor.shutdown();
		try {
			future.get(5, TimeUnit.SECONDS);
		} // end try
		catch (Exception e) {
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			String errStr = "Z3950Query:testConnection()" + errors.toString();
			errMsg = errStr;
			return false;
		} // end catch
		try {
			if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
				executor.shutdownNow();
				return false;
			} // end if
		} // end try
		catch (Exception e) {
			return false;
		}

		return tz.isViable();
	} // end TestConnection()
} // end class Z3950QueryByISBN
