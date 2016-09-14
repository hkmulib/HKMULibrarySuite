import hk.edu.csids.bookquery.*;

public class TestZ3950earchProxy{
	public static void main(String[] args){
		
		String inst = "OXFORD";
		String query = "@attr 1=7 9789571365091";
		try{
			if(args[0]!=null){inst=args[0];}
		}
		catch (Exception e){System.out.println("No inst Argument, using default. Syntax: [ccommand] [inst] [query]");}
		try{
			if(args[1]!=null){query=args[1];}
		}
		catch (Exception e){System.out.println("No ISBN Argument, using default. Syntax: [ccommand] [inst] [query]");}
		Z3950Query q = new Z3950QueryProxy(query, inst);
		//System.out.println(q.getResult());
		System.out.println(q.bk.getHoldingText());
		System.out.println("AVA:" + q.isAva());
		System.out.println("BIB:" + q.getBib_no());
		System.out.println("EXT ITM:" + q.getExt_itm_no());
		System.out.println("AVA ITM:" + q.getAva_itm_no());
		System.out.println(q.queryBk.isbn.getIsbn13());

	} //end main()
} //end class Test