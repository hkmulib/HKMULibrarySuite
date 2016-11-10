import hk.edu.csids.bookquery.*;

public class TestZ3950earchByNonISBN {
	public static void main(String[] args) {
		try {
			String inst = "HKBU";
			String author = "De Bono, Edward";
			String title = "Lateral thinking : a textbook of creativity.";
			String publisher = "";
			String year = "1977";
			String edition = "";
			String vol = "";

			Z3950Query q = new Z3950QueryByNonISBN(author, title, publisher, year, edition, vol, inst);
			System.out.println("QUERY: " + author + ", " + title  + ", " + publisher  + ", " + year  + ", " + edition  + ", " + vol  + ", " + inst);
			if (q.match()) {
				System.out.println("MATCH: ");
				q.queryBk.getTitle();
				System.out.println(q.getQueryStr());
				System.out.println(q.getQueryBase());
				System.out.println(q.bk.marc.getMarcTag());
				System.out.println(q.bk.getHoldingText());
				System.out.println("AVA:" + q.isAva());
				System.out.println("BIB:" + q.getBib_no());
				System.out.println("EXT ITM:" + q.getExt_itm_no());
				System.out.println("AVA ITM:" + q.getAva_itm_no());
				System.out.println("DEBUG:" + q.debug);
			} else {
				System.out.println("NOT MATCH: ");
				System.out.println(q.getQueryStr());
				System.out.println(q.getQueryBase());
				System.out.println("--- ");
				System.out.println(q.bk.marc.getMarcTag());
				System.out.println("--- ");
				System.out.println("DEBUG:" + q.debug);
				System.out.println(q.bk.getHoldingText());
				System.out.println("AVA:" + q.isAva());
				System.out.println("BIB:" + q.getBib_no());
				System.out.println("EXT ITM:" + q.getExt_itm_no());
				System.out.println("AVA ITM:" + q.getAva_itm_no());
				System.out.println("DEBUG:" + q.debug);
			} // end if
		} // end try
		catch (Exception e) {
			e.printStackTrace();
		}
	} // end main()
} // end class Test