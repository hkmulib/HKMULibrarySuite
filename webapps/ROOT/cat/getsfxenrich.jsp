<%@ page import="hk.edu.ouhk.lib.cat.*,hk.edu.ouhk.lib.*,hk.edu.ouhk.lib.bookquery.*"%>
<%@ page import="java.io.*,java.util.*"%>
CopyCAT Program for Library
<%
	try {
		String file = request.getParameter("file");
		String cmd = request.getParameter("cmd");
		String requestFilePath = request.getServletContext().getRealPath("/") + "cat/requests/sfxenrich/";
		String reportFilePath = request.getServletContext().getRealPath("/") + "cat/reports/sfxenrich/";
		out.println(requestFilePath);
		out.println(reportFilePath);
		if(cmd != null && cmd.equals("restart")){
	CopyCatSFX cc = new CopyCatSFX(null, reportFilePath, file);
		}
		if (file != null && file.contains(".xml-marc")) {
	File f = new File(requestFilePath + file);
	CopyCatSFX cc = new CopyCatSFX(f, reportFilePath);
	String now = StringHandling.getToday();
	out.println("Completed file: " + file);
	out.println("End time: " + now);
	out.println(reportFilePath);
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
