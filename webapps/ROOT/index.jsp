<%@ page import="hk.edu.csids.copycat.*,hk.edu.csids.*"%>
<%@ page import="hk.edu.csids.bookquery.*"%>
<%@ page import="java.io.*,java.util.*, javax.servlet.*" %>
<%@ page import="javax.servlet.http.*" %>
<%@ page import="org.yaz4j.*" %>
<%@ page import="org.apache.commons.fileupload.*" %>
<%@ page import="org.apache.commons.fileupload.disk.*" %>
<%@ page import="org.apache.commons.fileupload.servlet.*" %>
<%@ page import="org.apache.commons.io.output.*" %>
<html>
<head>
<title> CopyCAT Program for Library </title>
</head>
<%@include file="menu.jsp"%>
CopyCat Get MARCs
<hr>
<form enctype="multipart/form-data" method="post">
<input type="file" name="srcFile" id="srcFile">
<br>
<br>
<input type="submit" value="Get MARCs">
</form>

<%

	String contentType = request.getContentType();
	String savedFile = "";
	if ((contentType!=null && contentType.indexOf("multipart/form-data") >= 0)) {
		DiskFileItemFactory factory = new DiskFileItemFactory();
		String filePath = "/usr/local/LibraryRecordLocate/webapps/ROOT/requests";
		String reportsFilePath = "/usr/local/LibraryRecordLocate/webapps/ROOT/reports";
		factory.setRepository(new File(filePath));
		ServletFileUpload upload = new ServletFileUpload(factory);
		try{
			List fileItems = upload.parseRequest(request);
			Iterator i = fileItems.iterator();
			 while ( i.hasNext () ) 
		         {
		         	FileItem fi = (FileItem)i.next();
				if ( !fi.isFormField () ) {
		            		// Get the uploaded file parameters
					String now = GenStringHandling.getToday();
					String fieldName = fi.getFieldName();
					String fileName = fi.getName();
					String fileExt = fileName.replaceAll("^.*\\.", "");
					savedFile = now + "." + fileExt;
					boolean isInMemory = fi.isInMemory();
					long sizeInBytes = fi.getSize();
					if(!fileExt.equals("xlsx")){
						out.println("Wrong file format; must be .xlsx");
					} else {
						out.println("Start time: " + now + "<br>");
						File file = new File(filePath + "/" + savedFile);
						fi.write(file);
						out.println("Saved file: " + savedFile + "<br>");
						out.println("Report generation would take a quite long time.<br>");
						out.println("You may close this tab and check report later on the report page.<br>");
					} //end if
				} //end if
			}//end while
		} //end try

		catch(Exception e){e.printStackTrace();}
	} //end if


%>

        <script lanuage="javascript">
		var savedFile = "<%out.print(savedFile);%>";
                var xhttp${resultStatus.index} = new XMLHttpRequest();
                xhttp${resultStatus.index}.onreadystatechange = function() {
                        if (xhttp${resultStatus.index}.readyState == 4 && xhttp${resultStatus.index}.status == 200) {
				alert(xhttp${resultStatus.index}.responseText);
                        } //end if
                } //end function();
		if(savedFile!=""){
	                xhttp${resultStatus.index}.open("GET", "getmarcs.jsp?file=" + savedFile, true);
        	        xhttp${resultStatus.index}.send();
		} //end if
        </script>

</html>
