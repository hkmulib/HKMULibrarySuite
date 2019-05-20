<%@ page import="java.io.*,java.util.*, javax.servlet.*"%>
<%@ page import="hk.edu.ouhk.lib.ser.*,hk.edu.ouhk.lib.*"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%
	try {

		String reportdir = request.getServletContext().getRealPath("/") + "ser/reports/urlReports/";
		String webReportdir = "/ser/reports/urlReports/";
		FetchURLReport rpt = new FetchURLReport(reportdir, null);
		rpt.fetchReport();
                out.println("<a href=" + webReportdir + "/" + rpt.getReportFile() + "> " + rpt.getReportFile() + " </a>");
	} //end try

	catch (Exception e) {
		out.println("<br>");
		out.println("<br>");
		e.printStackTrace(new java.io.PrintWriter(out));
	}
%>
