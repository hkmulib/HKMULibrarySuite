package hk.edu.csids.bookquery;

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
	
	static {
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
	} //end static
	
	private static String PRIMO_QUERY_BASE = "/PrimoWebServices/xservice/search/brief?"+
												   "&indx=" + VALUES.get("INDX") +
												   "&bulkSize=" + VALUES.get("BULK_SIZE") +
												   "&institution=" + VALUES.get("INST_CODE") +
												   "&loc=local,scope:(" + VALUES.get("LOCAL_SCOPE") + ")";
	
	public static String PRIMO_X_BASE = "http://" + VALUES.get("PRIMO_BASE") + PRIMO_QUERY_BASE; 
											
	public static String QUERY_SETTING = VALUES.get("PRIMO_BASE") + "(Institution Code: " + VALUES.get("INST_CODE") +
												"; Source ID: " + VALUES.get("SOURCE_ID") + "; Search Scope: " + VALUES.get("LOCAL_SCOPE") + ").";
	
	public static void init(String inst){
		VALUES.put("INST_CODE", inst);
		VALUES.put("SOURCE_ID", Config.VALUES.get("SOURCE_ID_" + inst));
		VALUES.put("LOCAL_SCOPE", Config.VALUES.get("LOCAL_SCOPE_" + inst));
		VALUES.put("ILS_AVA_BASE", Config.VALUES.get("ILS_AVA_BASE_" + inst));
		VALUES.put("Z3950_SERVER", Config.VALUES.get("Z3950_SERVER_" + inst));
		VALUES.put("Z3950_PORT", Config.VALUES.get("Z3950_PORT_" + inst));
		VALUES.put("Z3950_BASE", Config.VALUES.get("Z3950_BASE_" + inst));
		VALUES.put("Z3950_CCCII", Config.VALUES.get("Z3950_CCCII_" + inst));
		
		PRIMO_QUERY_BASE = "/PrimoWebServices/xservice/search/brief?"+
				   "&indx=" + VALUES.get("INDX") +
				   "&bulkSize=" + VALUES.get("BULK_SIZE") +
				   "&institution=" + VALUES.get("INST_CODE") +
				   "&loc=local,scope:(" + VALUES.get("LOCAL_SCOPE") + ")";
		
		PRIMO_X_BASE = "http://" + VALUES.get("PRIMO_BASE") + PRIMO_QUERY_BASE;
		
		QUERY_SETTING = VALUES.get("PRIMO_BASE") + "(Institution Code: " + VALUES.get("INST_CODE") +
				"; Source ID: " + VALUES.get("SOURCE_ID") + "; Search Scope: " + VALUES.get("LOCAL_SCOPE") + ").";
		
	} //end init()
	
} //end class Config