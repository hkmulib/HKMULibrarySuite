<%@ page import="java.io.*,java.util.*, javax.servlet.*" %>
<%@ page import="javax.servlet.http.*" %>
<%
	String cmd = request.getParameter("cmd");
	String file = request.getParameter("file");
	String filedir = request.getServletContext().getRealPath("/") + "copycat/requests";
	File f = new File(filedir + "/" + file);
	if(cmd!=null && cmd.contains("delete")){
		f.delete();
		out.println("File '" + file + "' deleted <br>");
	} //end 
%>
<html>
<head>
<title> CopyCAT Requests </title>
</head>
<%@include file="menu.jsp"%>
CopyCAT Requests
<hr>
<%
	File directory = new File(filedir);
	File[] list = directory.listFiles();
        Arrays.sort(list, Collections.reverseOrder());
	if(list!=null){
	for(int i=0;i <list.length; i++){
		if(!list[i].getName().contains("sample")){
			out.println("<a href='requests/" + list[i].getName() + "' target='_blank'>" + list[i].getName() + "</a>");
			out.println(" <a href='?cmd=delete&file=" + list[i].getName() + "'> [delete] </a> <br>");
		} //end if
	} //end for
	}
%>
</html>
