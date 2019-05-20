<%@include file="menu.jsp"%>
<br>
<br>
<br>
<br>
<br>
<h3> <b> I.  Help for CopyCat Program: </b> </h3>
<hr>
<br> <b> To configure: </b>
<ol>
	<br>
	<li>Go to the <a href="ccconfig.jsp"> config page </a>. This allows you to configure query.
	</li>
</ol>

<hr>

<b> To generate reports: </b>
<br>
<br> &nbsp;&nbsp;&nbsp;&nbsp;

	The CopyCAT program only acccepts Excel (.xlxs) file format.
		The Excel columns must be in the following order:
		Bib#,ISBN,ISBN,Title. Sample: <a href="docs/copycat-request-sample.xlsx"  target="_blank">
			copycat-request-sample.xlsx </a>

<hr>

<b> To obtain reports: </b>
<br>
<br> &nbsp;&nbsp;&nbsp;&nbsp;
	Reports are on the <a href="report.jsp"> report </a> page,
		reports start with the date-time of generations in form
		yyyy-mm-dd-hh-mm-ss. The most noteworthy report is
		"xxx-outconvert.txt" which is the MARC tags be further processed for
		importing into an ILS. "xxx-outconvertRaw.txt" (the raw MARC format)
		is the same as "xxx-outconvert.txt" except it can be imported directly
		into an ILS. "xxx-summayStat.txt" is a quick summary (stat) of report
		generation.
<br>
<br>
<hr color="black" size=10>
<br>
<h3> <b> II.  Help for SFX Enrichment Program: </b> </h3>
<hr>
<br> <b> To configure: </b>
	<br>
	<li>Go to the <a href="ccconfig.jsp"> config page </a>. This allows you to configure query.
	</li>
<br>
<hr>
<b> To operate and generate reports: </b>
<br>
<br>
<br> &nbsp;&nbsp;&nbsp;&nbsp;
		See the manual <a href="docs/SFXCallNumberEnrichmentManual.docx" target="_blank"> here </a> for the operation flow.
		The SFX enrich program accepts only SFX exported file in XML. Sample: <a href="docs/sfx-enrich-sample.xml-marc" target="_blank"> sfx-enrich-sample.xml-marc </a>
<hr>

<b> To remove SFX local call no in batch: </b>
<br>
<br> &nbsp;&nbsp;&nbsp;&nbsp;
		See the manual <a href="docs/HowtoRemoveLocalCallNoInBatch.docx" target="_blank"> here </a> for the operation flow.
<hr>

<b> To obtain reports: </b>
<br>
<br> &nbsp;&nbsp;&nbsp;&nbsp;
	Reports are on the <a href="report.jsp"> report </a> page.
<br> &nbsp;&nbsp;&nbsp;&nbsp;
		yyyy-mm-dd-hh-mm-ss-SFXout.txt is the output file which can be directly import into SFX.
<br> &nbsp;&nbsp;&nbsp;&nbsp;
		yyyy-mm-dd-hh-mm-ss-SFXout.txt is the output file which can be directly import into SFX.
<br> &nbsp;&nbsp;&nbsp;&nbsp;
		yyyy-mm-dd-hh-mm-ss-SFXNotObtained.txt is the report file records all unobtainable SFX objects.
<hr>

</html>
