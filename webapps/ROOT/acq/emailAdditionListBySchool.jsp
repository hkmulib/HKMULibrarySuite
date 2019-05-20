<%@ page import="hk.edu.ouhk.lib.acq.*"%>
<%@ page import="java.io.*,java.util.*, javax.servlet.*"%>
<%@include file="menu.jsp"%>
<script language="javascript">
	function validateForm(form){
		if(form["selectedReport"].value == ""){
			alert("You must select a school report in the column 'Select Email Report!' of the above table.");
			return false;
		}
		return confirm("Warning!!! Do you really want to email the new addition list of the school " + form["selectedReport"].value +"?");
	}
</script>
<br>
<br>
<br>
<br>
<br>
<br>
<h3> <b> Email New Addition List by School (Last Month only) </b> </h3>
<%
	String cmd = request.getParameter("cmd");
	String inpwd = request.getParameter("pwd");
	if(inpwd==null)
		inpwd = "";
	String budtol = request.getParameter("budtol");
        String basedir = request.getServletContext().getRealPath("/") + "acq/";
        String webinfdir = request.getServletContext().getRealPath("/") + "/WEB-INF/classes/hk/edu/ouhk/lib/acq/";
        String pwdfile = basedir + "conf/sphinx";
        String budgetCodeFilePath = basedir + "conf/budgetCodes.txt";
        String budgetCodeWebFilePath = webinfdir + "budgetCodeConfig.txt";
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
			out.println("<b> <font color=red> WRONG PWD </font> </b> <script language=javascript> alert('WRONG Password') </script>");

		if(inpwd.equals(pwd) && cmd.equals("emailReport")){
			
			String selectedReport = request.getParameter("selectedReport");
			out.println("<b> The selected last month report of the school <b> <font color=red>" + selectedReport + "</font></b> is going to be emailed. Please check Mail later. </b>");
			String reportPath = request.getServletContext().getRealPath("/") + "acq/reports/newArrivalBySch/";
			FetchReportBySchoolAndDates acqRpt = new FetchReportBySchoolAndDates(selectedReport, "", "", reportPath, null);
			acqRpt.reportLastMonth();
			for (String reportFile : acqRpt.getReportFiles()) {
				EmailReportBySubject em = new EmailReportBySubject(reportPath + reportFile);
			}	
		}
	}

	
%>


<hr>
<b> <font color="red"> WARNING!!! </font> The selected report (last month) of the school will <font color="red">  be emailed IMMEDIATELY </font> to the email address(es) stated in the "Email RPT addr" column in the table below  after you clicked "Email Report!". </b>

<form id="emailForm" onsubmit="return validateForm(this)">
<table border=1 width=70% class='table table-hover'>
<thead> <tr> <td> No </td>  <td> School Code </td> <td> School Name </td> <td> Budget Codes </td> <td> Email RPT addr </td> <td> Select Email Report! </td> </tr></thead>
<%
                BufferedReader br = new BufferedReader(new FileReader(budgetCodeFilePath));
                String line = ""; 
                String sch[] = null;
		int count=0;
                while((line = br.readLine()) != null){
                        String arry[] = line.split("=");
                        String schStr = arry[0];
                        String codeStr = arry[1];
                        sch = schStr.split("@");
			String html = "<tr> <td> " + (count +1) + ". </td> <td>";
			html += sch[0] + "</td> <td> ";
			html +=  sch[1] + "</td> <td>";
			html +=  codeStr + "</td> <td>"; 
			html +=  arry[2] + "</td> <td>"; 
			html += "<input type='radio' name='selectedReport' value='" + sch[0] + "' </td> </tr>"; 
			out.println(html);
			count++;
                }

%>
</table>
<b> <font color="red"> WARNING!!! </font> The selected report (last month) of the school will <font color="red">  be emailed IMMEDIATELY </font> to the email address(es) stated in the "Email RPT addr" column in the table above after you clicked "Email Report!". </b>
<br><br>
Password (default: 123456) <input type=password name=pwd size=5>
<input type=hidden value=emailReport name=cmd>
<input type=submit value="Email Report!">
</form>
<br>
<b>
