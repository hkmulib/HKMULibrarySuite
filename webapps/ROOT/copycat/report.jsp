<%@ page import="java.io.*,java.util.*, javax.servlet.*" %>
<%@ page import="javax.servlet.http.*" %>
<%
	String cmd = request.getParameter("cmd");
	String file = request.getParameter("file");
	String filedir = request.getServletContext().getRealPath("/") + "copycat/reports";
	File f = new File(filedir + "/" + file);
	if(cmd!=null && cmd.contains("delete")){
		f.delete();
		out.println("File '" + file + "' deleted <br>");
	} //end 
%>
<html>
<head>
<title> CopyCAT Reports </title>
</head>
<%@include file="menu.jsp"%>
CopyCAT Reports
<hr>
<%
	File directory = new File(filedir);
	File[] list = directory.listFiles();
	Arrays.sort(list, Collections.reverseOrder());
	String lastFileHeader = "";
	for(int i=0;i <list.length; i++){
		String nowFileHeader = list[i].getName();
		nowFileHeader = nowFileHeader.replaceAll("-s.*$", "");
		nowFileHeader = nowFileHeader.replaceAll("-o.*$", "");
		nowFileHeader = nowFileHeader.trim();
		nowFileHeader = nowFileHeader.replaceAll("-..$", "");
		if(!lastFileHeader.equals(nowFileHeader))
			out.println("<hr> Report: " + nowFileHeader + "<br>");
		out.println("<a href='reports/" + list[i].getName() + "' target='_blank'>" + list[i].getName() + "</a>");
		out.println(" <a href='?cmd=delete&file=" + list[i].getName() + "'> [delete] </a> <br>");
		lastFileHeader = list[i].getName();
		lastFileHeader = lastFileHeader.replaceAll("-s.*$", "");
		lastFileHeader = lastFileHeader.replaceAll("-o.*$", "");
		lastFileHeader = lastFileHeader.trim();
		lastFileHeader = lastFileHeader.replaceAll("-..$", "");
	} //end for

%>
</html>
