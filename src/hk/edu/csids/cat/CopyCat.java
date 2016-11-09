package hk.edu.csids.cat;

import hk.edu.csids.*;
import hk.edu.csids.bookquery.*;
import java.io.*;
import java.util.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class CopyCat {

	protected Z3950QueryByISBN zq;
	protected List<String> isbns;
	protected List<String> failedIsbns;
	protected List<String> marcs;
	protected File outfile;
	protected GenStringHandling strHandle;
	protected CJKStringHandling cjkStrHandle;
	protected String convReport;
	protected ArrayList<String> IsbnArrayList = null;
	protected ArrayList<String> TitleArrayList = null;
	protected ArrayList<String> ILSRecNoArrayList = null;
	protected ArrayList<String> IsbnNotMatchArrayList = null;
	protected ArrayList<String> MarcArrayList = null;
	protected Map<String, Integer> instMatchCount = null;
	private MarcConversion mc;
	private String summaryTxt;

	public CopyCat() {
		clear();
	} // end CopyCat

	public CopyCat(File file, String str) {
		clear();
		try {
			FileInputStream is = new FileInputStream(file);
			readISBNandTitleSource(is);
			getMarc(str);
			getConvertedMarc(str);
		} // end try
		catch (Exception e) {
			System.out.println("CopyCat:CopyCat:" + e);
			e.printStackTrace();
		} // end catch
	} // end CopyCat

	public CopyCat(FileInputStream is, String str) {
		clear();
		try {
			readISBNandTitleSource(is);
			getMarc(str);
			getConvertedMarc(str);
		} // end try
		catch (Exception e) {
			System.out.println("CopyCat:CopyCat:");
			e.printStackTrace();
		} // catch
	} // end CopyCat()

	public void clear() {
		Config.init();
		zq = null;
		summaryTxt = "";
		strHandle = new GenStringHandling();
		cjkStrHandle = new CJKStringHandling();
		isbns = new ArrayList<String>();
		failedIsbns = new ArrayList<String>();
		mc = new MarcConversion();
	} // end clear()

	public void getConvertedMarc(String writePath) {
		try {
			writePath = writePath.replaceAll("\\\\", "/");
			if (!writePath.matches("/$")) {
				writePath += "/";
			} // end if
			String now = GenStringHandling.getToday();
			
			mc.setAddEqualSign(false);
			mc.convert(MarcArrayList, ILSRecNoArrayList);
			
			
			String path = writePath + now + "-outconvertRaw.mrc";
			File file = new File(path);
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			boolean nowrite = true;
			for (int i = 0; i < MarcArrayList.size(); i++) {
				String s = MarcArrayList.get(i);
				if (strHandle.hasSomething(s)) {
					MARC m = new MARC(s);
					s = m.getMarcRawStr();
					writer.write(s);
					nowrite = false;
				} // end if
			} // end for
			if (nowrite)
				writer.write("No record fetched");
			writer.close();
			
			mc.setAllConvFalse();
			mc.setAddEqualSign(true);
			mc.convert(MarcArrayList, ILSRecNoArrayList);
			
			path = writePath + now + "-outconvert.mrk";
			file = new File(path);
			writer = new BufferedWriter(new FileWriter(file));
			
			for (int i = 0; i < MarcArrayList.size(); i++) {
				String s = MarcArrayList.get(i);
				if (strHandle.hasSomething(s)) {
					writer.write(s);
					writer.write("\r\n\r\n");
					nowrite = false;
				} // end if
			} // end for
			if (nowrite)
				writer.write("No record fetched");
			writer.close();

			
			path = writePath + now + "-summaryStat.txt";
			file = new File(path);
			writer = new BufferedWriter(new FileWriter(file));
			writer.write(summaryTxt);
			
			writer.close();
		} // end try
		catch (Exception e) {
			System.out.println("CopyCat:getConvertedMarc():");
			e.printStackTrace();
		} // catch
	} // end getConvertedMarc()

	public void getMarc(String writePath) {
		int successRecNo = 0;
		String queryList = Config.VALUES.get("COPYCAT_QUERY_LIST");
		ArrayList<String> qArryList = new ArrayList<String>(Arrays.asList(queryList.split(",")));
		ArrayList<String> failedZ3950ServerList = new ArrayList<String>();
		writePath = writePath.replaceAll("\\\\", "/");
		if (!writePath.matches("/$")) {
			writePath += "/";
		} // end if
		if (MarcArrayList == null) {
			MarcArrayList = new ArrayList<String>();
			for (String s : IsbnArrayList) {
				MarcArrayList.add("");
			} // end for
			IsbnNotMatchArrayList = new ArrayList<String>();
			failedZ3950ServerList = obtainViableZ3950Server(qArryList);
			instMatchCount = new HashMap<String, Integer>();
			for (String s : qArryList) {
				instMatchCount.put(s, 0);
			} // end

			int j = 0;
			for (String q : qArryList) {
				j = 0;
				for (String s : IsbnArrayList) {

					ISBN isbn = new ISBN(s);

					if (!isbn.isValid())
						s = "";

					try {
						int excelid = j+2;
						if (strHandle.hasSomething(s)) {
							if (zq == null) {
								zq = new Z3950QueryByISBN(s, q);
							} else {
								zq.query(s, q);
							} // end if
							if (strHandle.hasSomething(zq.bk.marc.getMarcTag()) && !isBriefRecord(zq)
									&& matchTitle(j, zq) && zq.match() && !isCorruptedRecord(zq)) {
								String record = zq.getResult();
								String excelidStr = excelid + "";
								for(int i=4; i>0; i--){
									if(excelidStr.length()<i)
										excelidStr = "0" + excelidStr;
								} // end for
								record += "\nZ39    $a" + "EXCELID=" + excelid + "&Z3950LOCATE=" + q + "\n";
								++successRecNo;
								MarcArrayList.set(j, record);
								IsbnArrayList.set(j, "");
								instMatchCount.put(q, instMatchCount.get(q) + 1);
								System.out.println("ExcelID:" + excelid + ". MATCH: " + s + " (" + q + ")");
							} else {
								System.out.println("ExcelID:" + excelid + ". NOTMATCH: " + s + " (" + q + ")");
							} // end if
						} // if
					} // end try
					catch (Exception e) {
						System.out.println("CopyCat:getMarArrayList()");
						e.printStackTrace();
					} // end catch
					j++;
				} // end for
			} // end for
			j = 2;
			for (String s : IsbnArrayList) {
				if (strHandle.hasSomething(s)) {
					String id = j + "";
					if (id.length() == 1)
						id = "000" + id;
					if (id.length() == 2)
						id = "00" + id;
					if (id.length() == 3)
						id = "0" + id;
					
					String record = "EXCEL ROW ID: " + id + " NOT MATCH: " + s + ".\r\n";
					IsbnNotMatchArrayList.add(record);
				} // end if
				j++;
			} // end for
		} // end if

		String str = "Active Query List: " + qArryList.toString() + "\r\n";
		summaryTxt += str;
		str = "Filtering Tags: " + Config.VALUES.get("EXISTTAG_FOR_MATCHING") + "\r\n";
		summaryTxt += str;
		str = "Failed Z39.50 Server List: " + failedZ3950ServerList.toString() + "\r\n";
		summaryTxt += str;
		str = "Server match count:";
		for (String s : qArryList) {
			str += s + "(" + instMatchCount.get(s) + ") ";
		} // end for
		str += "\r\n";
		summaryTxt += str;
		str = "Titles Tried: " + IsbnArrayList.size() + "\r\n";
		summaryTxt += str;
		str = "Success Records: " + successRecNo + "\r\n";
		summaryTxt += str;
		str = "Failed Records: " + IsbnNotMatchArrayList.size() + "\r\n";
		summaryTxt += str;
		summaryTxt += "----------------------------" + "\r\n";
		
		for (String s : IsbnNotMatchArrayList) {
			if (strHandle.hasSomething(s))
				summaryTxt += s;
		} // end for

	} // getMarArrayList()

	public void readISBNandTitleSource(FileInputStream is) {
		try {

			if (IsbnArrayList == null) {
				IsbnArrayList = new ArrayList<String>();
				ILSRecNoArrayList = new ArrayList<String>();
				TitleArrayList = new ArrayList<String>();
				XSSFWorkbook wb = new XSSFWorkbook(is);
				XSSFSheet sheet = wb.getSheetAt(0);
				Iterator<org.apache.poi.ss.usermodel.Row> rowIterator = sheet.iterator();
				rowIterator.next();
				while (rowIterator.hasNext()) {
					org.apache.poi.ss.usermodel.Row row = rowIterator.next();
					// For each row, iterate through all the columns
					Iterator<Cell> cellIterator = row.cellIterator();

					while (cellIterator.hasNext()) {
						Cell cell = cellIterator.next();
						// Check the cell type and format accordingly

						if (cell.getColumnIndex() == 1) {
							String tempStr = "";
							switch (cell.getCellType()) {
							case Cell.CELL_TYPE_NUMERIC:
								Double d = cell.getNumericCellValue();
								tempStr = d.longValue() + "";
								break;
							case Cell.CELL_TYPE_STRING:
								tempStr = cell.getStringCellValue();
								break;
							} // switch
							ISBN isbn = new ISBN(tempStr);
							if (isbn.getIsbn() != null) {
								IsbnArrayList.add(isbn.getIsbn());
							} else {
								IsbnArrayList.add("NO ISBN");
							} // end if
						} // end if

						if (cell.getColumnIndex() == 0) {
							String tempStr = "";
							switch (cell.getCellType()) {
							case Cell.CELL_TYPE_NUMERIC:
								Double d = cell.getNumericCellValue();
								tempStr = d.longValue() + "";
								break;
							case Cell.CELL_TYPE_STRING:
								tempStr = cell.getStringCellValue();
								break;
							} // switch

							String regIDLenStr = Config.VALUES.get("TOT_DIGIT_RECORDID");
							if (regIDLenStr.matches("^\\d+$")) {
								int regIDLen = Integer.parseInt(regIDLenStr);
								while (tempStr.length() != regIDLen) {
									tempStr = "0" + tempStr;
								} // end while
							} // end if

							ILSRecNoArrayList.add(tempStr);
						} // end if

						if (cell.getColumnIndex() == 3) {
							String tempStr = "";
							switch (cell.getCellType()) {
							case Cell.CELL_TYPE_NUMERIC:
								Double d = cell.getNumericCellValue();
								tempStr = d.longValue() + "";
								break;
							case Cell.CELL_TYPE_STRING:
								tempStr = cell.getStringCellValue();
								break;
							} // switch
							TitleArrayList.add(tempStr);
						} // end if
					} // end while
				} // end whiles
				wb.close();
			} // end if
		} // end try

		catch (Exception e) {
			System.out.println("CopyCat:readISBNandTitleSource():");
			e.printStackTrace();
		} // end catch
	} // end loadIsbnList()

	public void printIsbnList() {
		for (String s : IsbnArrayList) {
			System.out.println(s);
		} // end for
	} // end printIsbnList()

	private boolean isCorruptedRecord(Z3950Query q) {

		String corruptRecordStr = Config.VALUES.get("CORRUPT_RECORD_STRING");

		if (corruptRecordStr.contains("NONE"))
			return false;

		String[] corruptRecordsStr = corruptRecordStr.split(",");

		String matchingRegExp = "";

		for (int i = 0; i < corruptRecordsStr.length; i++) {
			char[] espChars = { '.', '\'', '^', '$', '*', '+', '?', '(', ')', '[', '{', '}', '|' };
			String exp = corruptRecordsStr[i];
			for (int j = 0; j < espChars.length; j++) {
				if (espChars[j] != '\\') {
					exp = exp.replaceAll("\\" + espChars[j], "" + '\\' + '\\' + espChars[j]);
				} // end if
			} // end for
			exp = ".*" + exp;
			if (i != (corruptRecordsStr.length - 1)) {
				exp += ".*|";
			} else {
				exp += ".*";
			} // end if
			matchingRegExp += exp;
		} // end for

		try {
			BufferedReader bufReader = new BufferedReader(new StringReader(q.bk.marc.getMarcTag()));
			String line = "";
			while ((line = bufReader.readLine()) != null) {
				if (line.matches(matchingRegExp))
					return true;
			} // end while
		} // end try
		catch (Exception e) {
			System.out.println("CopyCat:isCorruptedRecord():");
			e.printStackTrace();
		} // end catch
		return false;
	} // end isCorruptedRecord()

	private boolean isBriefRecord(Z3950Query q) {
		try {
			String tagForMatchingStr = Config.VALUES.get("EXISTTAG_FOR_MATCHING");

			if (tagForMatchingStr.contains("ALL"))
				return false;

			String[] tagsForMatching = tagForMatchingStr.split(",");
			String matchingRegExp = "";
			for (int i = 0; i < tagsForMatching.length; i++) {
				String tag = tagsForMatching[i];
				String subfields = "";
				if (tag.length() == 3) {
					String exp = "";
					if (i != tagsForMatching.length - 1) {
						exp = "^" + tagsForMatching[i] + ".*|";
					} else if (i == tagsForMatching.length - 1) {
						exp = "^" + tagsForMatching[i] + ".*";
					} // end if
					matchingRegExp += exp;
				} else if (tag.length() > 3 && !tag.contains("_")) {
					subfields = tag.substring(3, tag.length());
					String exp = "^" + tag.substring(0, 3) + ".*";
					for (int j = 0; j < subfields.length(); j++) {
						exp += "\\$" + subfields.charAt(j) + ".*";
					} // end for
					if (i != tagsForMatching.length - 1) {
						exp += "|";
					} // end if
					matchingRegExp += exp;
				} else if (tag.length() > 3 && tag.contains("_")) {

					tag = tag.substring(0, 3) + " " + tag.substring(3, tag.length());
					tag = tag.replaceAll("X", "\\\\d");
					tag = tag.replaceAll("_", " ");

					String exp = "^" + tag + ".*";
					if (i != tagsForMatching.length - 1) {
						exp += "|";
					} // end if

					matchingRegExp += exp;

				} // end if
			} // end for

			BufferedReader bufReader = new BufferedReader(new StringReader(q.bk.marc.getMarcTag()));
			String line = "";
			while ((line = bufReader.readLine()) != null) {
				if (line.matches(matchingRegExp))
					return false;
			}
		} // end try
		catch (Exception e) {
			System.out.println("CopyCat:isBriefRecord():");
			e.printStackTrace();
		} // end catch
		return true;
	} // end isBriefRecord()

	private boolean matchTitle(int i, Z3950Query q) {
		String title = TitleArrayList.get(i).toLowerCase();
		if (strHandle.hasSomething(title) && strHandle.hasSomething(q.bk.marc.getMarcTag())) {
			BufferedReader bufReader = new BufferedReader(new StringReader(q.bk.marc.getMarcTag()));
			String line = "";
			try {
				while ((line = bufReader.readLine()) != null) {
					if (line.contains("245")) {

						line = line.replaceAll("^.*\\$a", "");
						line = line.replaceAll("\\$h.*", "");
						line = line.replaceAll("\\$c.*", "");
						line = line.replaceAll("\\$.", "");
						title = title.replaceAll("^.*\\$a", "");
						title = title.replaceAll("\\$h.*", "");
						title = title.replaceAll("\\$c.*", "");
						line = line.replaceAll("\\$.", "");
						title = strHandle.tidyString(title);
						title = strHandle.trimSpecialChars(title);
						line = line.toLowerCase();
						line = strHandle.tidyString(line);
						line = strHandle.trimSpecialChars(line);

						if (cjkStrHandle.isCJKString(title)) {
							line = cjkStrHandle.convertArabicToChineseNumber(line);
							line = cjkStrHandle.removeNonCJKChars(line);
							line = cjkStrHandle.standardizeVariantChineseFast(line);
							line = cjkStrHandle.standardizeVariantChineseForLooseMaching(line);
							line = cjkStrHandle.convertToSimpChinese(line);
							title = cjkStrHandle.convertArabicToChineseNumber(title);
							title = cjkStrHandle.removeNonCJKChars(title);
							title = cjkStrHandle.standardizeVariantChineseFast(title);
							title = cjkStrHandle.standardizeVariantChineseForLooseMaching(title);
							title = cjkStrHandle.convertToSimpChinese(title);
							title = title.replaceAll("第.版", "");
						} // end if
						line = strHandle.tidyString(line);
						title = strHandle.tidyString(title);
						if (strHandle.hasSomething(line) && strHandle.hasSomething(title)
								&& (line.contains(title) || title.contains(line))) {
							return true;
						} // end if

					} // end if
				} // end while
			} // end try
			catch (Exception e) {
				System.out.println("CopyCat:MatchTitle(); ");
				e.printStackTrace();
			} // catch

		} // end if
		return false;
	} // end matchTitle()

	private ArrayList<String> obtainViableZ3950Server(ArrayList<String> al) {
		ArrayList<String> os = new ArrayList<String>();
		for (Object s : al) {
			Z3950QueryByISBN zq = new Z3950QueryByISBN();
			if (!zq.testConnection(s.toString()))
				os.add(s.toString());
		} // end if

		for (Object s : os) {
			al.remove(s);
		} // end for
		return (ArrayList<String>) os;
	} // end obtainViableZ3950Server()
} // end class CopyCat