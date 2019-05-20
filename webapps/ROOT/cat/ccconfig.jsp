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
<h3> <b> Z39.50 Configuration</b> </h3>
<%
	String cmd = request.getParameter("cmd");
	String inpwd = request.getParameter("pwd");
	if(inpwd==null)
		inpwd = "";
	String tol = request.getParameter("tol");
        String basedir = request.getServletContext().getRealPath("/") + "cat/";
        String webinfdir = request.getServletContext().getRealPath("/") + "/WEB-INF/classes/hk/edu/ouhk/lib/cat/";
        String pwdfile = basedir + "conf/sphinx";
        String budgetCodeFilePath = basedir + "conf/ccconfig.txt";
        String budgetCodeWebFilePath = webinfdir + "config.txt";
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

		if(inpwd.equals(pwd) && cmd.equals("update")){
			String configUpdate = "";
			int t=Integer.parseInt(tol);
			for(int i=0;i<t;i++){
				String configName = request.getParameter("configname_" + i);
				String configValue = request.getParameter(configName + i);
				String configDes = request.getParameter("des" + configName + i);
				configUpdate += configName + "=" +configValue + "=" + configDes; 
				if(i<=t-1)
					configUpdate += "\n";
			}
                        File file = new File(budgetCodeFilePath);
                        file.delete();
                        Writer objWriter = new BufferedWriter(new FileWriter(budgetCodeFilePath));
                        objWriter.write(configUpdate);
                        objWriter.flush();
                        objWriter.close(); 
                        file = new File(budgetCodeWebFilePath);
                        objWriter = new BufferedWriter(new FileWriter(budgetCodeWebFilePath));
                        objWriter.write(configUpdate);
                        objWriter.flush();
                        objWriter.close(); 
			out.println("<b> <font color=red> Configuration Updated </font> </b>");
		}
	}

	
%>

<hr>
<form>
<table border=1 class="table table-hover">
<thead>
<tr> <td> <b> Config <b> </td>  <td> <b> Value </b> </td> <td> <b> Remark </b> </td> </tr>
</thead>
<%
                BufferedReader br = new BufferedReader(new FileReader(budgetCodeFilePath));
                String line = ""; 
                String sch[] = null;
		int count=0;
                while((line = br.readLine()) != null){
                        String arry[] = line.split("=");
			String html = "";
			if(!arry[0].contains("DEFAULT_")){
				html = "<tr> <td> " + arry[0] + ". </td> <td>";
				html += "<input name=" + arry[0] + count + " size=50% type=text value='" + arry[1] + "'>";
				html += "<input type=hidden name=configname_" + count + " value='" + arry[0] + "'> </td> <td> ";
				html += "<input type=hidden name=des" + arry[0] + count + " value='" + arry[2] + "'>";
				html += arry[2] + "</td> </tr>";
			}

			out.println(html);
			count++;
                }

%>
</table>

Password (default: 123456) <input type=password name=pwd size=5>
<input type=hidden value=update name=cmd>
<input type=hidden name=tol value="<%=count%>">
<input type=submit value=update>
</form>
</body>
</html>
