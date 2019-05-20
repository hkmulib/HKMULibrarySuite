

import hk.edu.ouhk.lib.acq.*;

public class TestAcqEmail {

	public static void main(String args[]) {
		String reportFile = "d:\\BA-20181101-20181131-(Created-2019-01-10-12-13-23).xlsx";
		EmailReportBySubject em = new EmailReportBySubject(reportFile);
		System.out.println(em.getReportFileStr());

	}
}
