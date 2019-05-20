package hk.edu.ouhk.lib.ser;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.*;
import org.apache.commons.validator.routines.UrlValidator;
import org.apache.poi.common.usermodel.HyperlinkType;
import hk.edu.ouhk.lib.StringHandling;
import hk.edu.ouhk.lib.*;
import java.io.*;
import java.sql.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * OUHK In-house Library Tool - Fetch in subscription journal and database with
 * access URLs from Aleph Oracle tables.
 * 
 * @author Wai-yan NG
 * @author wwyng@ouhk.edu.hk
 * @version 1.1 - Added HyperLink in Excel cells and freeze the header in the
 *          Excel report.
 * @since Mar 19, 2019
 */

public class FetchURLReport extends Report {
	private String outputHTML = "";
	private String now = StringHandling.getToday();
	private String materialStr = "";

	private void init() {
		Config.init();
		materialStr = StringHandling.trimSpace(Config.VALUES.get("URLITEMTYPE"));
		materialStr = materialStr.replaceAll(",$", "");
		materialStr = materialStr.replace(",,", ",");
		reportFile = "SER-URL-REPORT-" + now + "-" + materialStr.replace(",", "-") + ".xlsx";
	}

	/**
	 * Constructor of the class.
	 * 
	 * @param reportPath The place the Excel report is saved, must have appropriate
	 *                   permission to write into the folder.
	 */
	public FetchURLReport(String reportPath) {
		super(reportPath, null);
		init();
	}

	/*
	 * Constructor of the class.
	 * 
	 * @param reportPath The place the Excel report is saved, must have appropriate
	 * permission to write into the folder.
	 * 
	 * @param wr The Writer object for writing out the HTML report. Give it a null
	 * value if no need to write out HTML report.
	 */
	public FetchURLReport(String reportPath, Writer wr) {
		super(reportPath, wr);
		init();
	}

	/**
	 * Fetching the report in Excel and HTML format. The Excel report is saved in
	 * 'reprotPath' which is set by creating the class instance; while HTML report
	 * can be get using the 'getOutputHTML()' method. The report file name, which is
	 * generated with the time stamp invoking report generation, can be get by the
	 * method 'getReportFile()'.
	 */
	public void fetchReport() {

		Connection conAleph = null;
		Statement stmt = null;
		Statement stmt2 = null;
		ResultSet rs2 = null;

		System.setProperty("org.apache.poi.util.POILogger", "org.apache.poi.util.NullLogger");

		try {

			String z30_material_query = "(";

			// Get the material types defined in "config.txt" via the class Config
			String[] materialType = materialStr.split(",");
			for (int i = 0; i < materialType.length; i++) {
				z30_material_query += "Z30_MATERIAL = '" + materialType[i] + "'";
				if (i != materialType.length - 1) {
					z30_material_query += " or ";
				}
			}
			z30_material_query += ")";

			Class.forName("oracle.jdbc.driver.OracleDriver");
			conAleph = DriverManager.getConnection("jdbc:oracle:thin:@aleph.lib.ouhk.edu.hk:1521:aleph22", "oul01",
					"oul01");

			// The 1st SQL statement for fetching necessary title information with the
			// material types specified.
			String sql = "select UNIQUE(z13_rec_key), z13_title from oul50.z30, oul50.z13 where Z30_ITEM_PROCESS_STATUS = 'WB' and "
					+ z30_material_query + " and z13_rec_key = SUBSTR(z30_rec_key,1,9) order by z13_rec_key";

			stmt = conAleph.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			int count = 1;

			SXSSFWorkbook workbook = new SXSSFWorkbook(100);
			SXSSFSheet sheet = workbook.createSheet(materialStr + " URL RPT " + now);
			sheet.createFreezePane(0, 2);

			Row headerRow = sheet.createRow(0);
			Cell cell = headerRow.createCell(0);
			Font headerFont = workbook.createFont();
			headerFont.setBold(true);
			headerFont.setFontHeightInPoints((short) 12);
			CellStyle headerCellStyle = workbook.createCellStyle();
			headerCellStyle.setFont(headerFont);
			cell.setCellStyle(headerCellStyle);
			cell.setCellValue(materialStr + " URL Report. Generated time: " + now);

			headerRow = sheet.createRow(1);
			cell = headerRow.createCell(0);
			cell.setCellValue("No.");
			cell.setCellStyle(headerCellStyle);

			cell = headerRow.createCell(1);
			cell.setCellValue("BIB#");
			cell.setCellStyle(headerCellStyle);

			cell = headerRow.createCell(2);

			cell.setCellValue("Title");
			cell.setCellStyle(headerCellStyle);

			cell = headerRow.createCell(3);
			cell.setCellValue("URL");
			cell.setCellStyle(headerCellStyle);

			cell = headerRow.createCell(4);
			cell.setCellValue("YOC");
			cell.setCellStyle(headerCellStyle);

			outputHTML = "Report generated time: " + now + "<br>";
			outputHTML += "<h3> <div align=center> SER URL Report " + now + " </h3> <br>";
			outputHTML += "<table id='reportTable' class='table table-hover sortable'> <thead> <tr> <td> No. </td> <td> BIB # </td> <td> Title </td> <td>"
					+ " URL </td> <td> YOC </td> </tr> </thead>\n";
			outputHTML += "<tbody>";

			if (wr != null) {
				wr.write(outputHTML);
				wr.flush();
			}
			outputHTML = "";
			CellStyle hrefStyle = null;
			hrefStyle = workbook.createCellStyle();
			Font hrefFont = workbook.createFont();
			hrefFont.setUnderline(Font.U_SINGLE);
			hrefFont.setColor(IndexedColors.BLUE.getIndex());
			hrefStyle.setFont(hrefFont);
			Hyperlink href = workbook.getCreationHelper().createHyperlink(HyperlinkType.URL);

			while (rs.next()) {
				String rec_key = rs.getString("Z13_REC_KEY");
				String title = rs.getString("Z13_TITLE");

				// The 2nd SQL statement, for fetching the URLs from the raw data (Z00_DATA) of
				// a title from Aleph Oracle.
				String sql2 = "select * from z00 where z00_doc_number = '" + rec_key + "'";
				stmt2 = conAleph.createStatement();
				rs2 = stmt2.executeQuery(sql2);
				rs2.next();

				String data = rs2.getString("Z00_DATA");
				Pattern pattern = Pattern.compile("856..L(\\$\\$3.*)\\$\\$u");
				Matcher matcher = pattern.matcher(data);
				String subfield3 = "";
				if (matcher.find()) {
					subfield3 = matcher.group(1);

				}

				data = data.replace(subfield3, "");
				subfield3 = subfield3.replace("$$3", "");
				subfield3 = subfield3 + " ";

				String urls[] = data.split("856..L\\$\\$u");
				String yocs[] = new String[urls.length];
				for (int i = 0; i < urls.length; i++) {
					urls[i] = urls[i].trim();
					urls[i] = urls[i].replaceAll("^.*85640L\\$u", "");
					urls[i] = urls[i].replaceAll("^.*\\$\\$u", "");
					urls[i] = urls[i].replaceAll("CAT  L.*", "");
					yocs[i] = urls[i].replaceAll("^.*\\$\\$z", "");
					yocs[i] = yocs[i].replaceAll("^.*\\$\\$y", "");
					yocs[i] = yocs[i].replaceAll("\\$\\$a", "");
					yocs[i] = yocs[i].replaceAll("\\$\\$b", "");
					yocs[i] = yocs[i].replaceAll("\\$\\$c", "");
					yocs[i] = yocs[i].replaceAll("0013992.*", "");
					yocs[i] = yocs[i].replaceAll("0013993.*", "");
					yocs[i] = yocs[i].replaceAll("0084949.*", "");
					yocs[i] = yocs[i].replaceAll("0045.*", "");
					yocs[i] = yocs[i].replaceAll("0024993.*", "");
					yocs[i] = yocs[i].replaceAll("0046.*", "");
					yocs[i] = yocs[i].replaceAll("0032.*", "");
					yocs[i] = yocs[i].replaceAll("0027994.*", "");
					yocs[i] = yocs[i].replaceAll("0030993.*", "");
					yocs[i] = yocs[i].replaceAll("\\d\\d\\d\\d\\d\\d\\d.*", "");
					urls[i] = urls[i].replaceAll("\\$\\$z.*", "");

					if (yocs[i].contains("here")) {
						yocs[i] = yocs[i].replaceAll("here.*", "");
						yocs[i] += "here";
						yocs[i] = subfield3 + yocs[i];
					}
				}

				rs2.close();

				Row row = sheet.createRow(count + 1);
				row.createCell(0).setCellValue(count);
				row.createCell(1).setCellValue(rec_key);
				row.createCell(2).setCellValue(title);

				if (yocs.length > 1)
					row.createCell(4).setCellValue(yocs[1]);
				else
					row.createCell(4).setCellValue("");
				if (urls.length > 1)
					row.createCell(3).setCellValue(urls[1]);
				else
					row.createCell(3).setCellValue("");

				if (urls.length > 1) {
					urls[1] = urls[1].replaceAll(" ", "%20");
					row.getCell(3).setCellStyle(hrefStyle);
				}
				count++;

				String reportLine = "<tr> <td>#</td><td>" + rec_key + "</td><td>" + title + "</td>";

				if (yocs.length > 1 && urls.length > 1)
					reportLine += "<td> <a href='" + urls[1] + "' target='_blank'> " + urls[1] + "</a></td><td>"
							+ yocs[1] + "</td>\n";
				else
					reportLine += "<td> <a href='" + "" + "' target='_blank'> " + "" + "</a></td><td>" + "" + "</td>\n";

				if (wr != null) {
					wr.write(reportLine);
					wr.flush();
				}

				reportLine = "";

				for (int j = 2; j < urls.length; j++)
					if (!urls[j].contains("aleph.lib.ouhk.edu.hk")) {

						row = sheet.createRow(count + 1);
						row.createCell(0).setCellValue(count);
						row.createCell(1).setCellValue(rec_key);
						row.createCell(2).setCellValue(title);
						if (yocs.length > 1)
							row.createCell(4).setCellValue(yocs[j]);
						else
							row.createCell(4).setCellValue("");
						if (urls.length > 1) {

							row.createCell(3).setCellValue(urls[j]);
							/*
							 * href.setAddress(urls[j]); row.getCell(3).setHyperlink(href);
							 */
							row.getCell(3).setCellStyle(hrefStyle);

						} else {
							row.createCell(3).setCellValue("");
						}
						count++;

						reportLine += "<tr> <td>#</td><td>" + rec_key + "</td><td>" + title + "</td>";

						if (urls.length > 1 && yocs.length > 1)
							reportLine += "<td> <a href='" + urls[j] + "' target='_blank'> " + urls[j]
									+ "</a></td> <td>" + yocs[j] + "</td>\n";
						else
							reportLine += "<td> <a href='" + "" + "' target='_blank'> " + "" + "</a></td> <td>" + ""
									+ "</td>\n";
						if (wr != null) {
							wr.write(reportLine);
							wr.flush();
						}

						reportLine = "";
					}

			}

			outputHTML = "</tr></tbody></table>";

			if (wr != null) {
				wr.write(outputHTML);
				wr.flush();
			}
			outputHTML = "";

			sheet.trackAllColumnsForAutoSizing();
			sheet.autoSizeColumn(1);
			sheet.autoSizeColumn(2);
			sheet.autoSizeColumn(4);
			sheet.autoSizeColumn(3);

			FileOutputStream fileOut = new FileOutputStream(reportPath + reportFile);
			workbook.write(fileOut);
			workbook.dispose();
			workbook.close();
			fileOut.close();

			rs.close();
			rs2.close();
			stmt.close();
			conAleph.close();
		}

		catch (Exception e) {
			e.printStackTrace();
		}
	}
}