package hk.edu.csids.acq;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public final class Config{
	
	/*This class serves as a collection of constants containing system specific settings.
	 *This class reads config.txt at the same directory of the package "hk.edu.csids.bookquery"
	 */
	
	public static Map<String, String> VALUES = new HashMap<String, String>();
	
	public static void init(){
		BufferedReader br = null;
		
		try{
			URL url = Config.class.getResource("config.txt");
			br = new BufferedReader(new FileReader(url.getPath()));
			String line;
			while ((line = br.readLine()) != null) {
				String[] para = line.split(" ");
				VALUES.put(para[0], para[1]);
			} //end while
		} //end try
			
		catch (IOException e) {
			e.printStackTrace();
		} //end catch
	} //end updateConfig()
	
} //end class Config