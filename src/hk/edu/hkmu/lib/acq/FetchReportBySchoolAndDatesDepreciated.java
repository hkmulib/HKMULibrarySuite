package hk.edu.ouhk.lib.acq;

import java.util.*;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.*;
import org.apache.poi.common.usermodel.HyperlinkType;
import hk.edu.ouhk.lib.StringHandling;
import java.io.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * OUHK In-house Library Tool - Fetch new arrival title report by School and
 * date range from ARC and Aleph Oracle tables.
 * 
 * @author Wai-yan NG
 * @author wwyng@ouhk.edu.hk
 * @version 1.0
 * @since 8 Mar 2019
 */

public class FetchReportBySchoolAndDatesDepreciated {
	private String sql;
	private String startDate;
	private String endDate;
	private String schCode;
	private String school;
	private String[] codes;
	private String writePath;
	private String outputHTML;
	private boolean monthReport;
	private Writer wr;
	private ArrayList<String> reportFiles;
	private Set<String> resultSet;
	private HashMap<String, String[]> result;
	private java.util.Date now;
	private StringHandling strHandle;

	/**
	 * Initiating the class instance
	 * 
	 * @param wr The Writer object which is used to output the HTML report. The
	 *           object is normally the out object from JSP the framework.
	 * 
	 */
	private void init(Writer wr) {
		Config.init();
		startDate = null;
		endDate = null;
		codes = null;
		sql = null;
		school = null;
		schCode = null;
		writePath = null;
		outputHTML = "";
		monthReport = false;
		now = new java.util.Date();
		reportFiles = new ArrayList<String>();
		resultSet = new TreeSet<String>();
		result = new HashMap<String, String[]>();
		strHandle = new StringHandling();
		if (wr == null)
			this.wr = null;
		else
			this.wr = wr;
	}

	/**
	 * Preparing for reports; you may run reportThisMonth() or reportLastMonth()
	 * right after this.
	 * 
	 * @param schCode   The School code of a particular school, defined in the file
	 *                  1st column in the form [schCode@School Name] in
	 *                  budgetCode.txt under the class folder hk.edu.ouhk.lib.acq.
	 * 
	 * @param writePath The full folder path to store the generated Excel report; be
	 *                  sure the program has writing privilege of the path.
	 * 
	 * @param wr        For writing the report in HTML format out to an IO, normally
	 *                  used by JSP the istance javax.servlet.jsp.JspWriter.
	 */
	public FetchReportBySchoolAndDatesDepreciated(String schCode, String writePath, Writer wr) {
		init(wr);
		setSchCode(schCode);
		school = Config.BUDGET_CODES.get(schCode)[0].toString();
		codes = (String[]) Config.BUDGET_CODES.get(schCode)[1];
		setWritePath(writePath);
	}

	/**
	 * Prepare for report; fetchReport() can be called right after this.
	 * 
	 * @param schCode   The School code of a particular school, defined in the file
	 *                  1st column in the form [schCode@School Name] in
	 *                  budgetCodeConfig.txt.
	 * 
	 * @param startDate In the format YYYYMMDD, as the starting date range of the
	 *                  report.
	 * 
	 * @param endDate   In the format YYYYMMDD, as the ending date range of the
	 *                  report.
	 * 
	 * @param writePath The full folder path to store the generated Excel report; be
	 *                  sure the program has writing privilege of the path.
	 * 
	 * @param wr        For writing the report in HTML format out to an IO, normally
	 *                  used by JSP the instance javax.servlet.jsp.JspWriter.
	 */
	public FetchReportBySchoolAndDatesDepreciated(String schCode, String startDate, String endDate, String writePath, Writer wr) {
		init(wr);
		setStartDate(startDate);
		setEndDate(endDate);
		setSchCode(schCode);
		school = Config.BUDGET_CODES.get(schCode)[0].toString();
		codes = (String[]) Config.BUDGET_CODES.get(schCode)[1];
		setWritePath(writePath);
	}

	/**
	 * 
	 * Set the report Writer class; the report is in HTML format.
	 * 
	 * @param wr A Writer class for writing out the HTML report.
	 */
	public void setWriter(Writer wr) {
		this.wr = wr;
	}

	/**
	 * Set the writePath
	 * 
	 * @param writePath writePath is the path to write the Excel report. The running
	 *                  of this program must have relevant writing privilege of the
	 *                  path.
	 */
	public void setWritePath(String writePath) {
		if (writePath == null)
			writePath = "";
		this.writePath = writePath;
	}

	/**
	 * Set the schCode
	 * 
	 * @param schCode schCode is the key relating to the budget codes, that each
	 *                school holds certain budgets, for report generation. The
	 *                schCode is defined in the configuration file "budgetCodes.txt"
	 *                of this program.
	 */
	public void setSchCode(String schCode) {
		if (schCode == null)
			schCode = "";
		this.schCode = schCode;
	}

	/**
	 * Set the startDate
	 * 
	 * @param startDate startDate is the starting date range of the target report.
	 */
	public void setStartDate(String startDate) {
		if (startDate == null)
			startDate = "";
		this.startDate = startDate;
	}

	/**
	 * Set the endDate
	 * 
	 * @param endDate endDate is the ending date range of the target report.
	 */
	public void setEndDate(String endDate) {
		if (endDate == null)
			endDate = "";
		this.endDate = endDate;
	}

	public String getSchCode() {
		return schCode;
	}

	public String getSchool() {
		return school;
	}

	public String[] getBudgetCodes() {
		return codes;
	}

	public String getStartDate() {
		return startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	/**
	 * Return the files name of the generated reports.
	 * 
	 * @return The report file names; should have only one in the array list.
	 */
	public ArrayList<String> getReportFiles() {
		return reportFiles;
	}

	/**
	 * Using the current set school code and generate the associated report of the
	 * current month
	 */
	public void reportThisMonth() {
		if (schCode == null || schCode == "")
			return;

		Config.init();
		java.util.Date date = now;
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH) + 1;
		int day = cal.get(Calendar.DAY_OF_MONTH);
		String m = "";
		String d = "";
		if (month < 10)
			m = "0" + month;
		else
			m = "" + month;
		if (day < 10)
			d = "0" + day;
		else
			d = "" + day;
		startDate = year + "" + m + "01";
		endDate = year + "" + m + "" + d;
		fetchReport();
	}

	/**
	 * Using the current set school code and generate the associated report of the
	 * last month
	 */
	public void reportLastMonth() {
	

		monthReport = true;

		if (schCode == null || schCode == "")
			return;

		java.util.Date date = new java.util.Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH) + 1;
		int startMonth = month - 1;
		if (startMonth < 1)
			startMonth = 12;
		String m = "";
		if (startMonth < 10)
			m = "0" + startMonth;
		else
			m = "" + startMonth;
		int startYear = year;
		if (month == 1)
			startYear--;
		startDate = startYear + "" + m + "01";
		LocalDate convertedDate = LocalDate.parse(startDate, DateTimeFormatter.ofPattern("yyyyMMdd"));
		convertedDate = convertedDate.withDayOfMonth(convertedDate.getMonth().length(convertedDate.isLeapYear()));
		endDate = convertedDate.toString();
		endDate = endDate.replaceAll("-", "");
		fetchReport();
	}

	/**
	 * Using the current set schCode, startDate, endDate, and report path; and
	 * generate the associated report.
	 */
	public void fetchReport() {

		if (codes == null || startDate == null || endDate == null) {
			return;
		}

		Connection con = null;
		Connection conAleph = null;
		Statement stmt = null;
		ResultSet rset = null;
		Statement stmt2 = null;
		ResultSet rset2 = null;
		System.setProperty("org.apache.poi.util.POILogger", "org.apache.poi.util.NullLogger");
		SXSSFWorkbook workbook;

		workbook = new SXSSFWorkbook(100);

		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			con = DriverManager.getConnection("jdbc:oracle:thin:@arc4.lib.ouhk.edu.hk:1521:arc4", "arc", "arc");

			String sql = "select distinct ORIGIN_DWH_FACT_ORDERS.ORDER_NUMBER Order_Number, ORIGIN_DWH_DIM_BIB_INFO.DOC_NUMBER BIBNO, ORIGIN_DWH_DIM_BIB_INFO.TITLE Title, ORIGIN_DWH_DIM_BIB_INFO.AUTHOR Author, ";
			sql += "ORIGIN_DWH_DIM_BIB_INFO.PUBLISHER Publisher, ORIGIN_DWH_FACT_ORDERS.CALL_NUMBER_DESC1 Primary_Call_No_Desc, T3.MATERIAL_TYPE_DESC Order_Material_Type, ";
			sql += "substr(ORIGIN_DWH_DIM_BIB_INFO.DESC4, 241, 8) SID_Date, ORIGIN_DWH_DIM_BUDGET_TREE.BUDGET_NUMBER Budget_Number ";
			sql += "from (((OULR0.DWH_FACT_ORDERS ORIGIN_DWH_FACT_ORDERS INNER JOIN OULR0.DWH_DIM_ORDER_MATERIAL_TYPE T3 on ORIGIN_DWH_FACT_ORDERS.ORDER_MATERIAL_TYPE=T3.MATERIAL_TYPE_ID) ";
			sql += "INNER JOIN OULR0.DWH_DIM_ORDER_STATUS ORIGIN_DWH_DIM_ORDER_STATUS on ORIGIN_DWH_FACT_ORDERS.ORDER_STATUSID=ORIGIN_DWH_DIM_ORDER_STATUS.ORDER_STATUS_ID) ";
			sql += "INNER JOIN OULR0.DWH_DIM_BIB_INFO ORIGIN_DWH_DIM_BIB_INFO on ORIGIN_DWH_FACT_ORDERS.DOC_NUMBER=ORIGIN_DWH_DIM_BIB_INFO.DOC_NUMBER) ";
			sql += "LEFT OUTER JOIN (OULR0.DWH_FACT_BUDGET_TRANSACT T6 INNER JOIN OULR0.DWH_DIM_BUDGET_TREE ORIGIN_DWH_DIM_BUDGET_TREE ";
			sql += "on T6.BUDGET_NUMBER=ORIGIN_DWH_DIM_BUDGET_TREE.BUDGET_NUMBER) on ORIGIN_DWH_FACT_ORDERS.DOC_NUMBER=T6.DOC_NUMBER and ORIGIN_DWH_FACT_ORDERS.SEQUENCE=T6.DOC_SEQUENCE ";
			sql += "where substr(ORIGIN_DWH_DIM_BIB_INFO.DESC4, 241, 8) between '" + startDate + "' and '" + endDate
					+ "' and ";
			sql += "( ";

			for (int i = 0; i < codes.length; i++) {
				if (i == 0)
					sql += "ORIGIN_DWH_DIM_BUDGET_TREE.BUDGET_NUMBER like '" + codes[i] + "%' ";
				else
					sql += "or ORIGIN_DWH_DIM_BUDGET_TREE.BUDGET_NUMBER like '" + codes[i] + "%' ";
			}
			sql += ") ";
			sql += "and ORIGIN_DWH_DIM_ORDER_STATUS.ORDER_STATUS_ID not  in ('VC', 'LC')  order by Primary_Call_No_Desc asc nulls last, Title asc nulls last";

			stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			int total = 1;

			String startMonth = startDate.substring(4, 6).trim();
			String endMonth = endDate.substring(4, 6).trim();
			String startDay = startDate.substring(6, 8).trim();
			String endDay = endDate.substring(6, 8).trim();
			String startYear = startDate.substring(0, 4).trim();
			String endYear = endDate.substring(0, 4).trim();

			if (startMonth.equals(endMonth) && startYear.equals(endYear)) {
				endMonth = "";
				endYear = "";
			}

			startMonth = StringHandling.getMonthByNum(startMonth);
			endMonth = StringHandling.getMonthByNum(endMonth);
			if (!endMonth.equals(""))
				endMonth = " - " + endMonth;

			SXSSFSheet sheet = workbook.createSheet(startMonth + " " + startYear + endMonth + " " + endYear);
			sheet.createFreezePane(0, 3);

			CellStyle hrefStyle = workbook.createCellStyle();
			Font hrefFont = workbook.createFont();
			hrefFont.setUnderline(Font.U_SINGLE);
			hrefFont.setColor(IndexedColors.BLUE.getIndex());
			hrefStyle.setFont(hrefFont);

			CellStyle backgroundCellstyle = workbook.createCellStyle();
			backgroundCellstyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());

			Font headerFont = workbook.createFont();
			headerFont.setBold(true);
			headerFont.setFontHeightInPoints((short) 12);
			CellStyle headerCellStyle = workbook.createCellStyle();
			headerCellStyle.setFont(headerFont);

			Row headerRow = sheet.createRow(0);
			Cell cell = headerRow.createCell(0);
			cell.setCellStyle(headerCellStyle);
			cell.setCellValue(Config.VALUES.get("NEWARRIVALEXCELHEADER") + " " + school + " (" + schCode + ")");
			endMonth = endDate.substring(4, 6).trim();
			headerRow = sheet.createRow(1);
			cell = headerRow.createCell(0);

			endMonth = endMonth.replace("-", "").trim();
			endMonth = StringHandling.getMonthByNum(endMonth);
			endYear = endDate.substring(0, 4).trim();
			cell.setCellValue(Config.VALUES.get("NEWARRIVALEXCELHEADER2") + startDay + " " + startMonth + " "
					+ startYear + " - " + endDay + " " + endMonth + " " + endYear);
			headerCellStyle.setFont(headerFont);
			cell.setCellStyle(headerCellStyle);

			headerRow = sheet.createRow(2);
			cell = headerRow.createCell(0);
			cell.setCellValue("No.");
			cell.setCellStyle(headerCellStyle);

			cell = headerRow.createCell(1);
			cell.setCellValue("LIB ref#");
			cell.setCellStyle(headerCellStyle);

			cell = headerRow.createCell(2);
			cell.setCellValue("Title");
			cell.setCellStyle(headerCellStyle);

			cell = headerRow.createCell(3);
			cell.setCellValue("Link to the Item");
			cell.setCellStyle(headerCellStyle);

			cell = headerRow.createCell(4);
			cell.setCellValue("Author");
			cell.setCellStyle(headerCellStyle);

			cell = headerRow.createCell(5);
			cell.setCellValue("Publisher");
			cell.setCellStyle(headerCellStyle);

			cell = headerRow.createCell(6);
			cell.setCellValue("Call #");
			cell.setCellStyle(headerCellStyle);

			cell = headerRow.createCell(7);
			cell.setCellValue("Order Material Type");
			cell.setCellStyle(headerCellStyle);

			/*
			 * Remove SID & budget # column in Excel report according to ACQ's requirement.
			 * 20190126 cell = headerRow.createCell(7); cell.setCellValue("SID Date");
			 * cell.setCellStyle(headerCellStyle);
			 * 
			 * cell = headerRow.createCell(8); cell.setCellValue("Budget #");
			 * cell.setCellStyle(headerCellStyle);
			 * 
			 */

			outputHTML = "";
			outputHTML = now + "<br>";
			outputHTML += "<h3><div align=center> New arrival list for <font color=blue>" + school
					+ "</font> between <font color=blue>" + startDate + " and " + endDate + "</font></div></h3> <br>";
			outputHTML += "<table id='reportTable' class='table table-hover sortable'> <thead> <tr> <td> No. </td> <td width=5%> LIB ref# </td> <td> Title </td> <td> Author </td> <td> Publisher </td> <td> Call # </td> <td> Order Material Type </td> <td> SID Date </td> <td> Budget # </td> </tr> </thead>\n";
			outputHTML += "<tbody>";

			if (wr != null) {
				wr.write(outputHTML);
				wr.flush();
			}
			outputHTML = "";

			while (rs.next()) {
				String sid = rs.getString("SID_DATE");
				String title = rs.getString("TITLE");
				title = title.replace("/", "");
				String author = rs.getString("AUTHOR");
				if (author == null)
					author = "";
				String publisher = rs.getString("PUBLISHER");
				String callno = rs.getString("PRIMARY_CALL_NO_DESC");
				if (callno == null)
					callno = "";
				callno = callno.replaceAll("\\$\\$h|\\$\\$i", "");
				String orderType = rs.getString("ORDER_MATERIAL_TYPE");
				String orderNo = rs.getString("ORDER_NUMBER");
				String budgetNo = rs.getString("BUDGET_NUMBER");
				String bibno = rs.getString("BIBNO");

				String stringKey = "";

				for (int i = 0; i <= 10 - bibno.length(); i++) {
					bibno = "0" + bibno;
				}

				// If title, author, or publisher has reached the string length 100, which
				// assumes the actual title/author/publisher has more than 100 characters (As
				// ARC's Oracle set the string 100 char in maximum), this program will fetch the
				// complete string from Aleph's Oracle Z00 table which is the raw data.
				if ((title.length() > 72 || author.length() > 72 || publisher.length() > 72) || author.equals("")) {
					conAleph = DriverManager.getConnection("jdbc:oracle:thin:@aleph.lib.ouhk.edu.hk:1521:aleph22",
							"oul01", "oul01");
					String sql2 = "select Z00_DATA from Z00 where Z00.Z00_DOC_NUMBER='" + bibno + "'";
					stmt2 = conAleph.createStatement();
					ResultSet rs2 = stmt2.executeQuery(sql2);
					rs2.next();

					if (rs2.isFirst()) {
						String z00_data = "";
						try {
							z00_data = rs2.getString("Z00_DATA");
							if (title.length() > 72) {
								title = z00_data.replaceAll("^.*245..L\\$\\$a", "");
								title = title.replaceAll("264..L.*", "");
								title = title.replaceAll("\\/.*?$", "");
								title = title.replaceAll("\\$\\$a|\\$\\$b|\\$\\$c|\\$\\$d|\\$\\$n", "");
								title = title.replaceAll("\\d\\d\\d\\d$", "");
							}

							if (author.length() > 72 || author.equals("")) {

								if (!z00_data.matches(".*700..L\\$\\$a.*") && !z00_data.matches(".*710..L\\$\\$a.*")
										&& !z00_data.matches("^.*?111..L\\$\\$a.*")) {
									author = "";
									z00_data = "";
								}

								author = z00_data.replaceAll("^.*?111..L\\$\\$a", "");

								if (z00_data.matches(".*700..L\\$\\$a.*")) {
									author = author.replaceAll("^.*?700..L\\$\\$a", "");
									author = author.replaceAll("710..L.*", "");
								} else if (z00_data.matches(".*710..L\\$\\$a.*")) {
									author = z00_data.replaceAll("^.*?710..L\\$\\$a", "");
								} else if (!(z00_data.matches(".*700..L\\$\\$a.*")
										&& z00_data.matches(".*710..L\\$\\$a.*"))
										&& z00_data.matches("^.*?111..L\\$\\$a.*"))
									author = author.replaceAll("245..L\\$\\$a.*", "");

								author = author.replaceAll("970..L\\$\\$.*", "");
								author = author.replaceAll("830..L\\$\\$a.*", "");
								author = author.replaceAll("856..L\\$\\$u.*", "");
								author = author.replaceAll("CAT.*", "");

								author = author.replaceAll("\\d\\d\\d\\d$", "");
								author = author.replaceAll("\\$\\$a|\\$\\$b|\\$\\$c|\\$\\$d|\\$\\$n|\\$\\$e|\\$q", "");
								author = author.replaceAll("....700..L", " ");
								author = author.replaceAll("....710..L", " ");
							}

							if (publisher.length() > 72) {
								publisher = z00_data.replaceAll("^.*?264..L\\$\\$a", "");
								publisher = publisher.replaceAll("264..L.*", "");
								publisher = publisher.replaceAll("300..L.*", "");
								publisher = publisher
										.replaceAll("\\$\\$a|\\$\\$b|\\$\\$c|\\$\\$d|\\$\\$n|\\$\\$t|\\$|\\$q", "");
								publisher = publisher.replaceAll("\\d\\d\\d\\d$", "");

							}
						} catch (java.sql.SQLException e) {
							e.printStackTrace();
						}
					}
					rs2.close();
					stmt2.close();
					conAleph.close();
				}

				stringKey += strHandle.trimAll(title).toLowerCase();
				stringKey += strHandle.trimAll(author).toLowerCase();
				stringKey += strHandle.trimAll(publisher).toLowerCase();

				// Combine "hardback" and "paperback" into the type "Print".
				if (orderType.toUpperCase().equals("HARDBACK") || orderType.toUpperCase().equals("PAPERBACK"))
					orderType = "Print";
				stringKey += strHandle.trimAll(orderType.toUpperCase().trim());

				if (!resultSet.contains(stringKey)) {
					resultSet.add(stringKey);
					String strArry[] = { orderNo, title, author, publisher, callno, orderType, sid, budgetNo,
							total + "", bibno };
					result.put(stringKey, strArry);
				} else {
					String strArry[] = result.get(stringKey);
					strArry[0] += " / " + orderNo;
					if (strArry[4] == null)
						strArry[4] = callno;
					result.put(stringKey, strArry);
				}

				total++;
			}

			resultSet = null;
			rs.close();
			stmt.close();
			con.close();

			ArrayList<String> al = new ArrayList<String>();

			for (String key : result.keySet()) {

				String callno = "";

				try {
					callno = result.get(key)[4].toLowerCase();
				} catch (Exception e) {
					callno = "";
				}

				al.add(callno + "^^" + key);
			}

			Collections.sort(al);

			int count = 0;

			for (String str : al) {
				str = str.replaceAll("^.*\\^\\^", "");

				Row row = sheet.createRow(count + 3);
				String tmp = result.get(str)[0];
				row.createCell(0).setCellValue(count + 1);
				row.createCell(1).setCellValue(tmp);

				tmp = result.get(str)[1];
				row.createCell(2).setCellValue(tmp);

				tmp = result.get(str)[9];
				String URL = Config.VALUES.get("PRIMOFULLVIEWURLFORM").replace("^", "=") + tmp;
				Hyperlink href = workbook.getCreationHelper().createHyperlink(HyperlinkType.URL);
				href.setAddress(URL);
				row.createCell(3).setCellValue("click here");
				row.getCell(3).setHyperlink(href);
				row.getCell(3).setCellStyle(hrefStyle);

				tmp = result.get(str)[2];
				if (tmp == null)
					tmp = "";
				row.createCell(4).setCellValue(tmp);

				tmp = result.get(str)[3];
				if (tmp == null)
					tmp = "";
				row.createCell(5).setCellValue(tmp);

				tmp = result.get(str)[4];
				if (tmp == null)
					tmp = "";
				row.createCell(6).setCellValue(tmp);

				tmp = result.get(str)[5];
				row.createCell(7).setCellValue(tmp);

				tmp = null;

				String reportLine = "<tr> <td>#</td><td>" + result.get(str)[0] + "</td><td>" + result.get(str)[1]
						+ "</td><td>" + result.get(str)[2] + "</td> <td>" + "<a href='" + URL
						+ "'> click here </a> </td> <td>" + result.get(str)[3] + "</td><td>" + result.get(str)[4]
						+ "</td> <td>" + result.get(str)[5] + "</td> <td>" + result.get(str)[6] + "</td><td>"
						+ result.get(str)[7] + "</td></tr>\n";

				if (wr != null) {
					wr.write(reportLine);
					wr.flush();
				}

				count++;

				if ((count % 2) == 0) {
					row.setRowStyle(backgroundCellstyle);
				}
			}

			outputHTML = "</tbody></table>";
			if (wr != null) {
				wr.write(outputHTML);
				wr.flush();
			}

			sheet.trackAllColumnsForAutoSizing();
			sheet.autoSizeColumn(2);
			sheet.autoSizeColumn(3);
			sheet.autoSizeColumn(4);

			String now = StringHandling.getToday();
			String filename = schCode + "-" + startDate + "-" + endDate + "-(Created-" + now + ").xlsx";
			FileOutputStream fileOut = new FileOutputStream(writePath + "/" + filename);
			workbook.write(fileOut);

			reportFiles.add(filename);
			sheet = null;
			workbook.dispose();
			workbook.close();
			fileOut.close();
		}

		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
