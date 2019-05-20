
import java.io.File;

import hk.edu.ouhk.lib.cat.*;

public class TestCopyCatSFX {

	static public void main(String args[]){
		File file = new File(
				"d:/list.xml");
		
		String writePath = "D:\\";
		CopyCatSFX cc;
		cc = new CopyCatSFX(null, writePath, "2018-12-12-09-10-02");
	} //end main()
}