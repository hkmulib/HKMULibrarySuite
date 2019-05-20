<%@ page import="hk.edu.ouhk.lib.cat.*,hk.edu.ouhk.lib.*,hk.edu.ouhk.lib.bookquery.*"%>
<%@ page import="java.io.*,java.util.*"%>
CopyCAT Program for Library
<%
	try {
		String file = request.getParameter("file");
		String requestFilePath = request.getServletContext().getRealPath("/") + "cat/requests/copycat";
		String reportFilePath = request.getServletContext().getRealPath("/") + "cat/reports/copycat";
		out.println(requestFilePath);
		out.println(reportFilePath);
		if (file != null && file.contains(".xlsx")) {
	File f = new File(requestFilePath + "/" + file);
	CopyCatExcel cc = new CopyCatExcel(f, reportFilePath);
	String now = StringHandling.getToday();
	out.println("Completed file: " + file);
	out.println("End time: " + now);
		} else {
	out.println("invalid file.");
		} //end if
	} //end try

	catch (Exception e) {
                out.println("<br>");
                out.println("<br>");
                e.printStackTrace(new java.io.PrintWriter(out));
	}
%>
