
import java.io.File;

import hk.edu.hkmu.lib.cat.*;

public class TestCopyCatSFX {

	static public void main(String args[]){
		File file = new File(
				"d:/list.xml");
		
		String writePath = "D:\\";
		CopyCatSFX cc;
		cc = new CopyCatSFX(file, writePath);
	} //end main()
}