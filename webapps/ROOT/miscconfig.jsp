<%@ page import="hk.edu.csids.copycat.*,hk.edu.csids.*"%>
<%@ page import="hk.edu.csids.bookquery.*"%>
<%@ page import="java.io.*,java.util.*, javax.servlet.*" %>
<%@ page import="javax.servlet.http.*" %>
<%@ page import="org.apache.commons.fileupload.*" %>
<%@ page import="org.apache.commons.fileupload.disk.*" %>
<%@ page import="org.apache.commons.fileupload.servlet.*" %>
<%@ page import="org.apache.commons.io.output.*" %>
<%
	String pwdfile = request.getServletContext().getRealPath("/") + "/conf/sphinx";
	String pwd = "&klsldfkj2356";
	try{
	BufferedReader br = new BufferedReader(new FileReader(pwdfile));
	pwd = br.readLine();
	pwd = pwd.trim();
	} //end try
	catch(Exception e){}
%>
<html>
<head>
<title> CopyCAT Program for Library </title>
</head>
<%@include file="menu.jsp"%>
CopyCat Trad Chin Corr Table
<br>
Day 1 Table:
<a href="/conf/chineseAI_default.txt" target="_blank"> chineseAI_default.txt </a>
<br>
Current Table:
<a href="/conf/chineseAI.txt" target="_blank"> chineseAI.txt </a>
<hr>
CopyCat Config 
<br>
Day 1 Table:
<a href="/conf/ccconfig_default.txt" target="_blank"> ccconfig_default.txt </a>
<br>
Current Table:
<a href="/conf/ccconfig.txt" target="_blank"> ccconfig.txt </a>
<hr>
BookQuery Config 
<br>
Day 1 Table:
<a href="/conf/bqconfig_default.txt" target="_blank"> bqconfig_default.txt </a>
<br>
Current Table:
<a href="/conf/bqconfig.txt" target="_blank"> bqconfig.txt </a>
<hr>
<form enctype="multipart/form-data" method="post">
Current Password: <input type="password" name="pass"> (default pwd "123456"; pls don't change it at the moment)
<br>
<input type="file" name="srcFile" id="srcFile">
<br>
<input type="submit" value="Update Config">
<br>
<br>
<br>
Change Password:<br>
<input type="password" name="newpass" id="newpass">
</form>
<%
	boolean login = false;
	String contentType = request.getContentType();
	if ((contentType!=null && contentType.indexOf("multipart/form-data") >= 0)) {
		DiskFileItemFactory factory = new DiskFileItemFactory();
		String filePath = "/usr/local/LibraryRecordLocate/webapps/ROOT/WEB-INF/classes/hk/edu/csids";
		String filePath2 = "/usr/local/LibraryRecordLocate/webapps/ROOT/conf"; 
		factory.setRepository(new File(filePath));
		ServletFileUpload upload = new ServletFileUpload(factory);

		try{
			List fileItems = upload.parseRequest(request);
			Iterator i = fileItems.iterator();
			 while ( i.hasNext () ) 
		         {
		         	FileItem fi = (FileItem)i.next();
				String fieldName = fi.getFieldName();
				if(fieldName.equals("pass")){
					String p = fi.getString();
					p = p.trim();
					if(p.equals(pwd)){
						login = true;
					} //end if
				} //end if
				if(login && fieldName.equals("newpass") && fi.getString().length()>0){
					File file = new File(pwdfile);
					file.delete();
					Writer objWriter = new BufferedWriter(new FileWriter(file));
					objWriter.write(fi.getString());
					objWriter.flush();
					objWriter.close();
				} //end if
				if ( !fi.isFormField () ) {
		            		// Get the uploaded file parameters
					String now = GenStringHandling.getToday();
					String fileName = fi.getName();
					String fileExt = fileName.replaceAll("^.*\\.", "");
					String savedFile = now + "." + fileExt;
					boolean isInMemory = fi.isInMemory();
					long sizeInBytes = fi.getSize();
					if(login){
						if(fileName.equals("chineseAI.txt")){
							File file = new File(filePath + "/chineseAI.txt");
							fi.write(file);
							file = new File(filePath2 + "/chineseAI.txt");
							fi.write(file);
							out.println(fileName + ":config file saved.<br>");
						} else if(fileName.equals("ccconfig.txt")){
							File file = new File(filePath + "/copycat/config.txt");
							fi.write(file);
							file = new File(filePath2 + "/ccconfig.txt");
							fi.write(file);
							out.println(fileName+":config file saved.<br>");
							hk.edu.csids.copycat.Config.init();
						} else if(fileName.equals("bqconfig.txt")){
							File file = new File(filePath + "/bookquery/config.txt");
							fi.write(file);
							file = new File(filePath2 + "/bqconfig.txt");
							fi.write(file);
							out.println(fileName+":config file saved.<br>");
						} else {
							out.println("Unknown config file: " + fileName);
						} //end if
					} else {
						out.println("Wrong password.");
					} //end if
				} //end if
			}//end while
		} //end try

		catch(Exception e){e.printStackTrace();}
	} //end if
%>

</html>
