<%@ page import="hk.edu.csids.cat.*,hk.edu.csids.*"%>
<%@ page import="hk.edu.csids.bookquery.*"%>
<%@ page import="java.sql.*"%>
<%@ page import="java.util.*"%>
<%@ page import="java.text.*"%>
<%@ page import="java.io.*,java.util.*, javax.servlet.*"%>

<%@include file="menu.jsp"%>
<br>
<br>
<br>
<br>
<br>
<br>
<h3> <b> Change Password of CAT Tool </b></h3>
<%
	String cmd = request.getParameter("cmd");
	String inpwd = request.getParameter("pwd");
	if(inpwd==null)
		inpwd = "";
	String budtol = request.getParameter("budtol");
        String basedir = request.getServletContext().getRealPath("/") + "cat/";
        String pwdfile = basedir + "conf/sphinx";
        String pwd = "&klsldfkj2356";
        try {
                BufferedReader br = new BufferedReader(new FileReader(pwdfile));
                pwd = br.readLine();
                pwd = pwd.trim();
        } //end try
        catch (Exception e) {
        }
	if(cmd!=null){
		if(!inpwd.equals(pwd))
			out.println("<b> <font color=red> WRONG PWD </font> </b>");
	}
        if(inpwd.equals(pwd) && cmd.equals("updatepwd")){
                        String newpwd = request.getParameter("newpwd");
                        File file = new File(pwdfile);
                        file.delete();
                        Writer objWriter = new BufferedWriter(new FileWriter(pwdfile));
                        objWriter.write(newpwd);
                        objWriter.flush();
                        objWriter.close();
			out.println("<b> <font color=red> Password Changed </font> </b>");
         }

	
%>


<hr>
<form>
Password (default: 123456) <input type=password name=pwd size=5>
<br>
Change Password <input type=password name=newpwd size=5>
<input type=hidden value=updatepwd name=cmd>
<input type=submit value="Update Password">
</form>
<hr>
</body>
</html>
