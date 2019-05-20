package hk.edu.ouhk.lib;

import java.io.Writer;

public abstract class Report {

	protected String reportPath;
	protected String now;
	protected String reportFile;
	protected Writer wr;
	
	public Report() {
		reportPath = "";
		this.wr = null;
		this.now = StringHandling.getToday();
	}

	public Report(String reportPath, Writer wr) {
		setReportPath(reportPath);
		this.wr = wr;
		this.now = StringHandling.getToday();
	}

	protected void setReportPath(String reportPath) {
		if (reportPath == null)
			reportPath = "";
		else
			this.reportPath = reportPath;
	}

	protected void setReportFile(String reportFile) {
		if (reportFile == null)
			reportFile = "";
		else
			this.reportFile = reportFile;
	}

	/**
	 * Getting the report file path which is set in the constructor
	 * 
	 * @return The report file path.
	 */
	public String getReportPath() {
		return reportPath;
	}

	/**
	 * Getting the report file name which is ready only after the method
	 * 'fetchReport()' is called.
	 * 
	 * @return The generated file name.
	 */
	public String getReportFile() {
		return reportFile;
	}
	
	/*
	 * Getting the report generation time.
	 */
	public String getReportTime() {
		return now;
	}

}
