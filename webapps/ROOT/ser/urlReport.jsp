<%@ page import="java.io.*,java.util.*, javax.servlet.*,java.sql.*"%>
<%@ page import="hk.edu.ouhk.lib.ser.*,hk.edu.ouhk.lib.*,hk.edu.ouhk.lib.bookquery.*"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@include file="menu.jsp"%>
<br>
<br>
<br>
<br>
<br>
<br>
<b> Download the Excel report (it would take a lot of time to load, depending on the report size.): </b><div id=reportFile><img src=/img/loading.gif width=100></div>
<script language=javascript>
	var xhttp = new XMLHttpRequest();
		 xhttp.onreadystatechange = function() {
			if (this.readyState == 4 && this.status == 200) {
				document.getElementById("reportFile").innerHTML =
				this.responseText;
			}
			if (this.status == 408 || this.status==504) {
				document.getElementById("reportFile").innerHTML = "Report failed. Pls the contact system support.";
			}
		};
	xhttp.open("GET", "urlReportForAJAX.jsp", true);
	xhttp.send();
</script>
</body>
</html>
