import hk.edu.csids.bookquery.*;

public class TestZ3950earchByNonISBN {
	public static void main(String[] args) {
		try {
			String inst = "CUHK";
			String author = "毛澤東";
			String title = " 毛澤東詩詞全集.";
			String publisher = "香港崑崙製作公司,";
			String year = "1990.";
			String edition = "再版";
			String vol = "";

			Z3950Query q = new Z3950QueryByNonISBN(author, title, publisher, year, edition, vol, inst);
			System.out.println("QUERY: " + author + ", " + title  + ", " + publisher  + ", " + year  + ", " + edition  + ", " + vol  + ", " + inst);
			if (q.match()) {
				
				System.out.println("MATCH: ");
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