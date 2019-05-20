
import java.io.PrintWriter;

import hk.edu.ouhk.lib.acq.*;

public class TestAcqReportBySch {

	public static void main(String argvs[]) {
		Config.init();
		/*
		 * for(String schCode : Config.BUDGET_CODES.keySet()) {
		 * FetchReportBySchoolAndDates acqRpt = new FetchReportBySchoolAndDates(schCode,
		 * "20180821", "20180822", "D:\\"); acqRpt.fetchReport(); }
		 */
		PrintWriter out = new PrintWriter(System.out, true);
		FetchReportBySchoolAndDates acqRpt = new FetchReportBySchoolAndDates("E&L", "20190101", "20190228", "D:\\", null);
		acqRpt.fetchReport();
		acqRpt.reportLastMonth();
		acqRpt.setSchCode("E&L");
		acqRpt.reportLastMonth();
	}
}
