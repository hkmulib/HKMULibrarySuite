import hk.edu.csids.bookquery.*;

public class TestZ3950earchByNonISBN{
	public static void main(String[] args){
		try{
		String inst = "USTL";
		String author = " 村上春樹";
		String title = "尋羊冒險記.";
		String publisher = "時報文化出版企業股份有限公司,";
		String year = "1995";
		String edition = "";
		String vol = "";
		
		Z3950Query q = new Z3950QueryByNonISBN(author, title, publisher, year, edition, vol, inst);
		if(q.match()){
			System.out.println("MATCH: ");
			System.out.println(q.getQueryStr());
			System.out.println(q.getQueryBase());
			System.out.println(q.bk.marc.getMarcTag());
			System.out.println(q.bk.getHoldingText());
			System.out.println("AVA:" + q.isAva());
			System.out.println("BIB:" + q.getBib_no());
			System.out.println("EXT ITM:" + q.getExt_itm_no());
			System.out.println("AVA ITM:" + q.getAva_itm_no());
		} else {
			System.out.println("NOT MATCH: ");
			System.out.println(q.getQueryStr());
			System.out.println(q.getQueryBase());
			System.out.println("--- ");
			System.out.println(q.getResult());
			System.out.println("--- ");
		} //end if
		} //end try
		catch(Exception e){e.printStackTrace();}
	} //end main()
} //end class Test