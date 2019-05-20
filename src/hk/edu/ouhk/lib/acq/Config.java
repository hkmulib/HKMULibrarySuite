package hk.edu.ouhk.lib.acq;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;


/**
 * Reading the configuration from budgetCodes.txt and config.txt in to static variables for later use.
 * @author Wai-yan NG
 * @version 1.0
 * @since Jan 29, 2019
 */
public final class Config {

	public static Map<String, String> VALUES = new HashMap<String, String>();
	public static Map<String, Object[]> BUDGET_CODES = new HashMap<String, Object[]>();

	public static void init() {
		BufferedReader br = null;

		try {
			URL url = Config.class.getResource("config.txt");
			br = new BufferedReader(new FileReader(url.getPath()));
			String line;
			while ((line = br.readLine()) != null) {
				String[] para = line.split("=");
				VALUES.put(para[0], para[1]);
			} // end while

			url = Config.class.getResource("budgetCodes.txt");
			br = new BufferedReader(new FileReader(url.getPath()));
			while ((line = br.readLine()) != null) {
				String[] para = line.split("=");
				String[] sch = para[0].split("@");
				String[] codes = para[1].split(",");
				Object[] values = {sch[1], codes, para[2]};
				BUDGET_CODES.put(sch[0], values);
			} // end while
			
			url = Config.class.getResource("config.txt");
			br = new BufferedReader(new FileReader(url.getPath()));
			while ((line = br.readLine()) != null) {
				String[] para = line.split("=");
				VALUES.put(para[0], para[1]);
			}

		} // end try

		catch (IOException e) {
			e.printStackTrace();
		} // end catch
	} // end updateConfig()

} // end class Config