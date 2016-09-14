<%@include file="menu.jsp"%>
<html>
<head><title> Help </title </head>
Help file for CopyCat Program
<hr>
<br>
To configure:
<ol>
<br>
<li>
Go to the <a href="miscconfig.jsp"> config page </a>. This allows you to configure 3 files: 1. chineseAI.txt, 2. ccconfig.txt, and 3. bqconfig.txt. You may also find the out-of-the-box config files in the form "*_default.txt".
</li>
<br>
<li>
It is important to note that if the config files corrupted; the whole programs cannot be run properly. In this case, you may use the default config files to replace the corrupted ones.
</li>
<br>
<li>
You have to edit the config files by yourself then upload the file; the upload action will replace the old config file. The file name you upload must be 1. "chineseAI.txt", 2. "ccconfig.txt", or 3. "bqconfig.txt", depending on what you want to configure.
</li>
<br>
<li>
File "chineseAI.txt": for converting simp to trad Chinese only. Each line represents an entry and values of each line is divided by the char "_". The first value of each line is a Chinese typo; while the second value is the typo correction instruction. If the correction instruction contains a number, it must contain 1 Chinese char which will replace the character place of the typo as indicated by the number. The correction can be done by string to string replacement, in this case there will be no number in the 2nd value.  
</li>
<br>
<li>
File "ccconfig.txt" (General): for configuring CopyCat function. Config details are as follow: "COPYCAT_QUERY_LIST" is the whole list of institute codes of libraries to make Z39.50 query to. The Z39.50 server details associated with the institute code must be set before, in bqconfig.txt, for this setup to work. "EXISTTAG_FOR_MATCHING" defines the MARC tags that only if the return records contain such tags, CopyCAT program will include the records on reports; the first 3 chars is a MARC tag value, the fourth is a subfield or control field indicator "_" which follows a control number. "TOT_DIGIT_RECORDID" defines total number of the ILS record ID, and add "0" to the left if ILS record IDs don't have enough digits (to be used with the function "con001"). "CORRUPT_RECORD_STRING" defines strings which determine if a returned record, which contains the string(s), is corrupted. 
</li>
<br>
<li>
File "ccconfig.txt" (Function): for configuring to enable or disable MARC post-processing functions ("YES" means enable "NO" means disable). Config details are as follow:"conNRm880": convert 880 CJK titles to the original field, and remove all 880 lines. "remove0XXexcept008": remove all the tags start with 00X except 008. "rm9XX": remove all tags start with 9. "rm856": remove 856 tag. "con001": add 001 tag which contains your ILS's record ID. "simpChiToTradChin": convert Chinese from simp to trad. "removeLeader": remove leader. 
</li>
<br>
<li>
File "bqconfig.txt": for configure server addresses, ports, databases for Z39.50 query. "Z3950_SERVER_[institute code], "Z3950_PORT_[institute code]", and "Z3950_BASE_[institute code]" must be defined in order to query a Z39.50 server. You may assign an institute code which will be used in "COPYCAT_QUERY_LIST" of ccconfig.txt. "EACC_SYSTEM_[institution code]" is optional, for the ILS contain EACC/CCCII characters. 
</li>
</ol>

<hr>

To generate reports:

<ol>
<li>
The CopyCAT program only acccepts Excel (.xlxs) file format. The Excel columns  must be in the following order: Bib#,ISBN,ISBN,Title. Sample: <a href="requests/sample.xlsx"> sample.xlsx </a>
</li>
</ol>

<hr>

To obtain reports:
<ol>
<li>
Reports are on the <a href="report.jsp">  report </a> page, reports start with the date-time of generations in form yyyy-mm-dd-hh-mm-ss. The most noteworthy report is "xxx-outconvert.txt" which is the MARC tags be further processed for importing into an ILS. "xxx-outconvertRaw.txt" (the raw MARC format) is the same as "xxx-outconvert.txt" except it can be imported directly into an ILS. "xxx-summayStat.txt" is a quick summary (stat) of report generation.
</li>
</ol>

</html>
