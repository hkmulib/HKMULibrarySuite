import hk.edu.csids.bookquery.*;

public class TestPrimoSearchByISBN{
	public static void main(String[] args){
		String inst = "CIHE";
		PrimoQueryByISBN q = new PrimoQueryByISBN("7533911695", inst);
		if(q.match()){
			System.out.println("Query Setting: " + q.getQuerySetting());
			System.out.println("-- MATCHED --");
			System.out.println("TITLE: " + q.bk.getTitle());
			System.out.println("CREATOR: " + q.bk.getCreator());
			System.out.println("CONTRIBUTOR: " + q.bk.getContributor());
			System.out.println("PUBLISHER: " + q.bk.getPublisher());
			System.out.println("EDITION: " + q.bk.getEdition());
			System.out.println("TITLE: " + q.bk.getTitle());
			System.out.println("BOOKTYPE: " + q.bk.getBookType());
			System.out.println("Fulltext Urls: " + q.bk.getFulltextUrls());
			System.out.println("PUBLISH YEAR: " + q.bk.getPublishYear());
			System.out.println("Subject: " + q.bk.getSubject());
			System.out.println("ISBN ORI " + q.bk.isbn.getOriIsbn());
			System.out.println("ISBN13: " + q.bk.isbn.getIsbn13());
			System.out.println("Primo Link: " + q.bk.getPrimoLink());
			System.out.println("Holding Info: " + q.bk.getHoldingText());
			System.out.println("ILSID: " + q.bk.getIlsId());
			System.out.println("AVA: " + q.isAva() );
			System.out.println("Item count: " + q.getExt_itm_no() );
			System.out.println("AVA Item count: " + q.getAva_itm_no() );
			System.out.println("ERR MSG: " + q.getErrMsg() );
			for(int i=0; i<q.bk.getSubjects().size(); i++){
				System.out.println("Subject " + i + ": " + q.bk.getSubjects().get(i));	
			}//endfor
			for(int i=0; i<q.bk.getCreators().size(); i++){
				System.out.println("Authors " + i + ": " + q.bk.getCreators().get(i));	
			}//endfor
		} else {
			System.out.println("NOT MATCHED. ISBN:"  + q.bk.isbn.getOriIsbn() + ".\n"+ q.getErrMsg());
			System.out.println("Primo X-service Base: " + hk.edu.csids.bookquery.Config.PRIMO_X_BASE);
			System.out.println("ISBN ORI " + q.bk.isbn.getOriIsbn());
			System.out.println("ISBN10 " + q.bk.isbn.getIsbn10());
			System.out.println("ISBN13: " + q.bk.isbn.getIsbn13());
		} //end if
	}
} //end class Test