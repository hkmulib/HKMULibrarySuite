<%@ page import="hk.edu.csids.cat.*,hk.edu.csids.*"%>
<%@ page import="hk.edu.csids.bookquery.*"%>
<%@ page import="java.io.*,java.util.*"%>
CopyCAT Program for Library
<%
	try {
		String file = request.getParameter("file");
		String requestFilePath = request.getServletContext().getRealPath("/") + "copycat/requests";
		String reportFilePath = request.getServletContext().getRealPath("/") + "copycat/reports";
		out.println(requestFilePath);
		out.println(reportFilePath);
		if (file != null && file.contains(".xlsx")) {
			File f = new File(requestFilePath + "/" + file);
			CopyCat cc = new CopyCat(f, reportFilePath);
			String now = GenStringHandling.getToday();
			out.println("Completed file: " + file);
			out.println("End time: " + now);
		} else {
			out.println("invalid file.");
		} //end if
	} //end try

	catch (Exception e) {
		e.printStackTrace();
	}
%>
