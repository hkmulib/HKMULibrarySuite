package hk.edu.csids;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.*;
import java.util.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.spreada.utils.chinese.ZHConverter;

public class CJKStringHandling {

	private String ori_str;
	private String con_str;
	private static HashMap<String, String> EACCMap = null;
	private static HashMap<String, String> ToEACCMap = null;
	private static HashMap<String, String> VariantChineseMap = null;
	private static HashMap<String, String> ChineseAI = null;

	public CJKStringHandling() {
		clear();
	} // end ChineseHandling

	public CJKStringHandling(String str) {
		clear();
		getOriginalString(str);
	} // end ChineseHandling()

	public void clear() {
		ori_str = null;
		con_str = null;
	} // end clear()

	public boolean isCJK() {
		if (ori_str != null) {
			return true;
		} // end if
		return false;
	} // end isChinese()

	public String getResultString() {
		return con_str;
	} // end getResultString()

	public String getOriginalString() {
		return ori_str;
	} // end getResultString()

	public void getOriginalString(String str) {
		if (isCJKString(str)) {
			ori_str = str;
		} else {
			ori_str = null;
		} // end if
	} // end setOriStr()

	public boolean isCJKString(String str) {
		if (str == null) {
			return false;
		} // end if
		for (int i = 0; i < str.length(); i++) {
			char ch = str.charAt(i);
			boolean retbool = isCJKChar(ch);
			if (retbool) {
				return true;
			} // end if
		} // end for
		return false;
	} // end checkCJK()

	public static boolean isCJKChar(Character ch) {
		Character.UnicodeBlock block = Character.UnicodeBlock.of(ch);
		if (!Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS.equals(block)
				&& !Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS.equals(block)
				&& !Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A.equals(block)
				&& !Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B.equals(block)
				&& !Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION.equals(block)
				&& !Character.UnicodeBlock.KATAKANA.equals(block)) {
			return false;
		} // end if
		return true;
	} // end check()

	public String convertToSimpChinese() {
		return convertToSimpChinese(ori_str);
	} // end public String convertToSimpChinese()

	public String convertArabicToChineseNumber(String str) {
		str = str.replace("1", "一");
		str = str.replace("2", "二");
		str = str.replace("3", "三");
		str = str.replace("4", "四");
		str = str.replace("5", "五");
		str = str.replace("6", "六");
		str = str.replace("7", "七");
		str = str.replace("8", "八");
		str = str.replace("9", "九");
		str = str.replace("0", "零");
		return str;
	} // end convertArabicToChineseNumber()

	public String convertToSimpChinese(String str) {
		ori_str = str;
		if (ori_str != null) {
			ZHConverter converter = ZHConverter.getInstance(ZHConverter.SIMPLIFIED);

			if (con_str == null) {
				con_str = converter.convert(ori_str);
			} else {
				con_str = converter.convert(con_str);
			} // end if
			return con_str;
		} // end if
		return "";
	} // end convertToSimpChinese()

	public String convertToTradChinese(String str) {
		str = standardizeVariantChinese(str);
		str = tradChineseAICorrection(str);
		return str;
	} // end convertToTradChinese()

	public String tradChineseAICorrection(String str) {

		if (ChineseAI == null) {
			ChineseAI = new HashMap<String, String>();
			InputStream is = null;
			is = getClass().getResourceAsStream("chineseAI.txt");
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String line;
			try {
				while ((line = br.readLine()) != null) {
					line = line.trim();
					if(line != ""){
						String[] strArry = line.split("_");
						if(strArry.length>1 && strArry[1]!=null)
							ChineseAI.put(strArry[0], strArry[1]);
					} //end if
				} // end while
			} // end try
			catch (Exception e) {
				StringWriter errors = new StringWriter();
				e.printStackTrace(new PrintWriter(errors));
				String errStr = "CJKStringHandling:convertToTradChinese():" + errors.toString();
				System.out.println(errStr);
			} // end catch
		} // end if

		Iterator it = ChineseAI.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			String key = pair.getKey() + "";
			String value = pair.getValue() + "";
			if (str.contains(key)) {
				int replacePlace = -1;
				String pos = "";
				if(value.length()==2)
					pos = value.charAt(1) + "";
				if (pos.matches("\\d")) {
					replacePlace = Integer.parseInt(value.charAt(1) + "");
					replacePlace -= 1;
				} // end if
				if (replacePlace < 0 && value.length() > 0) {
					key = GenStringHandling.escapeRegExpReservedChars(key);
					str = str.replaceAll(key, value);
				} // end if

				if (replacePlace >= 0) {
					str = str.replaceAll(key.charAt(replacePlace) + "", value.charAt(0) + "");
				} // if
			} // end if
		} // end while

		return str;
	} // end

	public String removeNonCJKChars() {
		if (ori_str != null) {
			con_str = removeNonCJKChars(ori_str);
		} // end if
		return "";
	} // end removeNonCJKChars()

	public String removeNonCJKChars(String str) {
		String out = "";
		if (str != null) {
			for (int i = 0; i < str.length(); i++) {
				char ch = str.charAt(i);
				boolean retbool = isCJKChar(ch);
				if (retbool) {
					out += ch;
				} // end if
			} // end for
			con_str = out;
		} // end if
		return con_str;
	} // end removeNonCJKChars()

	public String standardizeVariantChineseFast() {
		return standardizeVariantChineseFast(ori_str);
	} // standardizeVariantChineseFast()

	public String standardizeVariantChineseFast(String str) {
		ori_str = str;
		if (ori_str != null) {
			if (con_str == null) {
				con_str = ori_str;
			} // end if
			String[] specChars = { "檯", "枱", "臺", "台", "峰 ", "峰", "鑑", "鍳", "研", "硏", "羨", "羡", "清", "淸", "群", "羣", "羣",
					"床", "裡", "裏", "啟", "啓", "敎", "教" };

			for (int i = 0; i < specChars.length; i += 2) {
				con_str = con_str.replaceAll(specChars[i], specChars[i + 1]);
			} // end for
			return con_str;
		} // end if
		return "";
	} // end standardizeVariantChineseFast()

	public String standardizeVariantChineseForLooseMaching() {
		return standardizeVariantChineseForLooseMaching(ori_str);
	} // end public String standardizeVariantChineseForLooseMaching()

	public String standardizeVariantChineseForLooseMaching(String str) {
		ori_str = str;
		if (ori_str != null) {
			if (con_str == null) {
				con_str = ori_str;
			} // end if
			String[] specChars = { "营", "行", "词", "字", "詞", "字", "用", "下", "界", "介", "识", "慧", "識", "慧", "劃", "畫", "划",
					"畫", "の", "之", "と", "與", "的", "之", "络", "路" };

			for (int i = 0; i < specChars.length; i += 2) {
				con_str = con_str.replaceAll(specChars[i], specChars[i + 1]);
			} // end for
			return con_str;
		} // end if
		return "";
	} // end standardizeVariantChineseForLooseMaching()

	public String standardizeVariantChinese() {
		return standardizeVariantChinese(ori_str);
	} // end standardizeVariantChinese()

	public String standardizeVariantChinese(String str) {
		ori_str = str;
		if (ori_str != null) {
			con_str = ori_str;
			loadVariantChineseMap();
			try {
				for (int k = 0; k < con_str.length(); k++) {
					char ch = con_str.charAt(k);
					String tempStr = Integer.toHexString(ch | 0x10000).substring(1);
					tempStr = tempStr.toUpperCase();

					String conStr = VariantChineseMap.get(tempStr);
					if (conStr != null) {
						int i = Integer.parseInt(conStr, 16);
						char ch2 = (char) i;
						con_str = con_str.replace(con_str.charAt(k), ch2);
					} // end if
				} // end for
			} // end try
			catch (Exception e) {
				
				StringWriter errors = new StringWriter();
				e.printStackTrace(new PrintWriter(errors));
				String errStr = "CJKStringHandling:standardizeVariantChinese():" + errors.toString();
				System.out.println(errStr);

			} // end catch
			return con_str;
		} // end if
		return "";
	} // standardizeVariantChinese()

	public boolean isBig5(byte[] bytes) {
		String str = new String(bytes, Charset.forName("Big5_HKSCS"));

		int matchCount = 0;
		int mostersCount = 0;
		char ch = ' ';
		str = str.replaceAll("\n", "");
		for (int i = 0; i < str.length(); i++) {
			ch = str.charAt(i);
			if (ch == '�') {
				mostersCount++;
				if (mostersCount > 3)
					return false;
			} // end if

			if (isCJKChar(ch)) {
				matchCount++;
				if (matchCount > 15)
					return true;
			} // end if
		} // end for
		return false;
	} // end isBig5

	private int checkEACCScheme(byte[] bytes) {
		if (bytes == null) {
			return 0;
		} // end if
		byte[] ori_strBytes = bytes;
		for (int k = 0; k < ori_strBytes.length; k++) {

			byte b1 = ori_strBytes[k];
			byte b2 = 0;
			byte b3 = 0;
			byte b8 = 0;
			if (k + 1 < ori_strBytes.length)
				b2 = ori_strBytes[k + 1];
			if (k + 3 < ori_strBytes.length)
				b3 = ori_strBytes[k + 2];
			if (k + 8 < ori_strBytes.length)
				b8 = ori_strBytes[k + 7];
			if (b1 == 27) {
				if (b2 == 36 && b3 == 49) {
					return 1;
				} // end if

			} // end if
			if (b1 == 123 && b8 == 125) {
				return 2;
			} // end if
		} // end for

		return 0;
	} // end checkScheme

	public boolean isEACC(byte[] bytes) {
		if (checkEACCScheme(bytes) < 1)
			return false;
		return true;
	} // end isEACC()

	public byte[] removeControl(byte[] bytes) {
		for (int i = 0; i < bytes.length; i++) {
			if (bytes[i] == 0x1E || bytes[i] == 0x1f || bytes[i] == 0x1d) {
				bytes[i] = 32;
			} // end if
		} // end for
		return bytes;
	} // end removeControl

	public byte[] EACCtoUnicode(byte[] bytes) {

		for (int i = 0; i < bytes.length; i++)
			if (bytes[i] == 0x00)
				bytes[i] = 0x5f;

		byte[] con_strBytes = bytes;
		byte[] ori_strBytes = bytes;
		int scheme = checkEACCScheme(con_strBytes);
		if (scheme == 1) {

			boolean start = false;
			boolean end = true;
			int startIndex = 0;
			int endIndex = 0;
			int lastEndIndex = 0;
			String convertedSegment = "";

			for (int k = 0; k < ori_strBytes.length; k++) {
				byte b1 = ori_strBytes[k];
				byte b2 = 0;
				byte b3 = 0;

				if (b1 == 27) {
					b2 = ori_strBytes[k + 1];
					b3 = ori_strBytes[k + 2];

					if (b2 == 36 && b3 == 49) {
						start = true;
						end = false;
						startIndex = k;
						byte bc1 = ori_strBytes[k + 3];
						byte bc2 = ori_strBytes[k + 4];
						byte bc3 = ori_strBytes[k + 5];

						byte[] tb = { bc1, bc2, bc3 };

						String con = EACCtoUnicodeChar(tb);
						convertedSegment = con;
					} // end if

					if (start && !end) {

						for (int j = k + 6; j < ori_strBytes.length; j++) {
							if (j + 2 < ori_strBytes.length) {
								byte bc1 = ori_strBytes[j];
								byte bc2 = ori_strBytes[j + 1];
								byte bc3 = ori_strBytes[j + 2];
								if (bc1 == 27 && bc2 == 40 && bc3 == 66) {
									end = true;
									endIndex = j + 3;
									k = j + 3;
									break;
								} else {
									byte[] tb = { bc1, bc2, bc3 };
									j = j + 2;
									String con = EACCtoUnicodeChar(tb);
									convertedSegment += con;
								} // end if
							} // end if
						} // end for

						if (lastEndIndex == 0) {
							for (int m = 0; m < startIndex; m++) {
								con_strBytes[m] = ori_strBytes[m];
							} // end for
							byte[] b = convertedSegment.getBytes();
							for (int i = startIndex, h = 0; i < startIndex + b.length; i++, h++) {
								con_strBytes[i] = b[h];
							} // end for
							lastEndIndex = endIndex;
						} else {
							for (int i = lastEndIndex; i < startIndex; i++) {
								con_strBytes[i] = ori_strBytes[i];
							} // end for

							byte[] b = convertedSegment.getBytes();
							for (int i = startIndex, h = 0; i < startIndex + b.length; i++, h++) {
								con_strBytes[i] = b[h];
							} // end for

							lastEndIndex = endIndex;
						} // end if
					} // end if
				} // end if
			} // end for

			for (int i = lastEndIndex; i < ori_strBytes.length; i++) {
				con_strBytes[i] = ori_strBytes[i];
			} // end for

		} else if (scheme == 2) {
			
			for (int k = 0; k < ori_strBytes.length; k++) {
				byte b1 = ori_strBytes[k];
				byte b8 = 0;

				if (k + 7 < ori_strBytes.length)
					b8 = ori_strBytes[k + 7];
				
				if (b1 == 123 && b8 == 125) {
					String conStr = "";
					String oriStr = "";
					byte[] bch = new byte[3];

					for (int l = k + 1; l < k + 8; l++) {
						oriStr += (char) ori_strBytes[l];
						con_strBytes[l] = 0x5f;
					} // end for

					oriStr = oriStr.toUpperCase().trim();

					for (int o = 0, z = 0; o < 6; o++) {
						String tmp = "";
						if (o > 0 && (o + 1) % 2 == 0) {
							tmp = oriStr.charAt(o - 1) + "" + oriStr.charAt(o);
							int a = Integer.parseInt(tmp, 16);
							tmp = tmp.trim();
							bch[z] = (byte) a;
							z++;
						} // end if
					} // end for
					
					conStr = EACCtoUnicodeChar(bch);
					byte[] conB = conStr.getBytes();

					if (conB.length > 0)
						con_strBytes[k] = conB[0];

					if (k + 1 < con_strBytes.length && conB.length > 1)
						con_strBytes[k + 1] = conB[1];

					if (k + 2 < con_strBytes.length && conB.length > 2)
						con_strBytes[k + 2] = conB[2];

					if (k + 7 < con_strBytes.length)
						k += 7;
				} else {
					con_strBytes[k] = ori_strBytes[k];
				} // end if

				for (int l = con_strBytes.length - 1; l > con_strBytes.length - 30; l--) {
					if (con_strBytes.length < l && ori_strBytes.length < l)
						con_strBytes[l] = ori_strBytes[l];
				} // end for
			} // end for
			
		} // end if
		
		bytes = new byte[con_strBytes.length + 100];

		boolean notCJK = false;
		int notCJKNo = 0;
		
		for (int i = 0, j = 0; i < con_strBytes.length; i++) {
			if (con_strBytes[i] != 0x5f && i < bytes.length) {

				byte[] bs = new byte[1];
				bs[0] = ori_strBytes[i];
				String str = "";
				str = new String(bs, Charset.forName("ISO-8859-1"));
				bs = str.getBytes();

				byte[] bs2 = new byte[3];
				if (i + 2 < ori_strBytes.length) {
					bs2[0] = ori_strBytes[i];
					bs2[1] = ori_strBytes[i + 1];
					bs2[2] = ori_strBytes[i + 2];
				} // end if

				String str2 = new String(bs2);

				if (isCJKString(str2))
					notCJK = true;

				if (notCJK)
					notCJKNo += 1;

				if (bs.length == 2 && !(notCJKNo > 0)) {
					bytes[j] = bs[0];
					bytes[j + 1] = bs[1];
					j += 1;
				} else {
					bytes[j] = con_strBytes[i];
					if (notCJKNo == 3) {
						notCJK=false;
						notCJKNo = 0;
					} // end if
				} // end if

				j++;
			} // end if
		} // end for
		
		return bytes;
	} // EACCtoUnicode()()

	public String EACCtoUnicodeChar(byte[] bytes) {
		if (bytes.length != 3) {
			return "";
		} // end if

		int i = bytes[0];
		int i2 = bytes[1];
		int i3 = bytes[2];

		String ch = "";
		ch = Integer.toHexString(i);
		ch += Integer.toHexString(i2);
		ch += Integer.toHexString(i3);
		ch = ch.toUpperCase();

		String result = "";
		try {
			if (EACCMap == null) {
				loadEACCMap();
			} // end if

			result = EACCMap.get(ch);
			if (result != null) {
				result = (char) Integer.parseInt(result, 16) + "";
			} else {
				result = "";
			} // end if

			return result;
		} // end try
		catch (Exception e) {
			
			
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			String errStr = "CJKStringHandling:EACCtoUnicodeChar():" + errors.toString();
			System.out.println(errStr);

		}
		return "";
	} // EACCtoUnicodeChar()

	public String UnicodeCharToEACCNumber(char ch) {
		String hex = String.format("%04x", (int) ch);
		String EACC = "";
		hex = hex.toUpperCase();
		loadToEACCMap();
		EACC = ToEACCMap.get(hex);
		return EACC;
	} // UnicodeCharToEACCNumber()

	private void loadEACCMap() {
		try {
			if (EACCMap == null) {
				EACCMap = new HashMap<String, String>();
				InputStream is = null;
				is = getClass().getResourceAsStream("EACC_Unicode.xlsx");
				XSSFWorkbook wb = new XSSFWorkbook(is);
				XSSFSheet sheet = wb.getSheetAt(0);
				Iterator<org.apache.poi.ss.usermodel.Row> rowIterator = sheet.iterator();

				while (rowIterator.hasNext()) {
					org.apache.poi.ss.usermodel.Row row = rowIterator.next();
					// For each row, iterate through all the columns
					String ci = "";
					String cu = "";
					Iterator<Cell> cellIterator = row.cellIterator();
					while (cellIterator.hasNext()) {
						Cell cell = cellIterator.next();
						// Check the cell type and format accordingly

						if (cell.getColumnIndex() == 1) {
							String tempStr = "";
							switch (cell.getCellType()) {
							case Cell.CELL_TYPE_NUMERIC:
								Double d = cell.getNumericCellValue();
								tempStr = d.longValue() + "";
								break;
							case Cell.CELL_TYPE_STRING:
								tempStr = cell.getStringCellValue();
								break;
							} // switch
							ci = tempStr;
						} // end if

						if (cell.getColumnIndex() == 2) {
							String tempStr = "";
							switch (cell.getCellType()) {
							case Cell.CELL_TYPE_NUMERIC:
								Double d = cell.getNumericCellValue();
								tempStr = d.longValue() + "";
								break;
							case Cell.CELL_TYPE_STRING:
								tempStr = cell.getStringCellValue();
								break;
							} // switch
							cu = tempStr;
						} // end if
					} // end while
					EACCMap.put(ci, cu);
				} // end whiles
				wb.close();
			} // end if
		} // end try
		catch (Exception e) {
			
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			String errStr = "CJKStringHandling:loadEACCMap():" + errors.toString();
			System.out.println(errStr);

		} // end catch
	} // loadEACCMap()

	private void loadToEACCMap() {
		try {
			if (ToEACCMap == null) {
				ToEACCMap = new HashMap<String, String>();
				InputStream is = null;
				is = getClass().getResourceAsStream("EACC_Unicode.xlsx");
				XSSFWorkbook wb = new XSSFWorkbook(is);
				XSSFSheet sheet = wb.getSheetAt(0);
				Iterator<org.apache.poi.ss.usermodel.Row> rowIterator = sheet.iterator();

				while (rowIterator.hasNext()) {
					org.apache.poi.ss.usermodel.Row row = rowIterator.next();
					// For each row, iterate through all the columns
					String ci = "";
					String cu = "";
					Iterator<Cell> cellIterator = row.cellIterator();
					while (cellIterator.hasNext()) {
						Cell cell = cellIterator.next();
						// Check the cell type and format accordingly

						if (cell.getColumnIndex() == 1) {
							String tempStr = "";
							switch (cell.getCellType()) {
							case Cell.CELL_TYPE_NUMERIC:
								Double d = cell.getNumericCellValue();
								tempStr = d.longValue() + "";
								break;
							case Cell.CELL_TYPE_STRING:
								tempStr = cell.getStringCellValue();
								break;
							} // switch
							ci = tempStr;
						} // end if

						if (cell.getColumnIndex() == 2) {
							String tempStr = "";
							switch (cell.getCellType()) {
							case Cell.CELL_TYPE_NUMERIC:
								Double d = cell.getNumericCellValue();
								tempStr = d.longValue() + "";
								break;
							case Cell.CELL_TYPE_STRING:
								tempStr = cell.getStringCellValue();
								break;
							} // switch
							cu = tempStr;
						} // end if
					} // end while
					ToEACCMap.put(cu, ci);
				} // end whiles
				wb.close();
			} // end if
		} // end try
		catch (Exception e) {
			
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			String errStr = "CJKStringHandling:loadEACCMap():" + errors.toString();
			System.out.println(errStr);
			
		} // end catch
	} // loadToEACCMap()

	public void loadVariantChineseMap() {
		try {
			if (VariantChineseMap == null) {
				VariantChineseMap = new HashMap<String, String>();
				InputStream is = null;
				is = getClass().getResourceAsStream("HKIUGTSVCC_modified.xlsx");
				XSSFWorkbook wb = new XSSFWorkbook(is);
				XSSFSheet sheet = wb.getSheetAt(0);
				Iterator<org.apache.poi.ss.usermodel.Row> rowIterator = sheet.iterator();
				String masterCode = "";
				String valueCode = "";
				while (rowIterator.hasNext()) {
					org.apache.poi.ss.usermodel.Row row = rowIterator.next();
					// For each row, iterate through all the columns
					Iterator<Cell> cellIterator = row.cellIterator();
					while (cellIterator.hasNext()) {
						Cell cell = cellIterator.next();
						if ((cell.getColumnIndex() != 0) && (cell.getColumnIndex() % 2) == 0) {
							String tempStr = "";
							switch (cell.getCellType()) {
							case Cell.CELL_TYPE_NUMERIC:
								Double d = cell.getNumericCellValue();
								tempStr = d.longValue() + "";
								break;
							case Cell.CELL_TYPE_STRING:
								tempStr = cell.getStringCellValue();
								break;
							} // switch

							if (!valueCode.matches("^#Sequence")) {
								valueCode = tempStr;
								valueCode = valueCode.replaceAll("^U\\+", "");
							} // end if

							Cell masterCell = sheet.getRow(cell.getRowIndex()).getCell(0);
							switch (masterCell.getCellType()) {
							case Cell.CELL_TYPE_NUMERIC:
								Double d = masterCell.getNumericCellValue();
								tempStr = d.longValue() + "";
								break;
							case Cell.CELL_TYPE_STRING:
								tempStr = masterCell.getStringCellValue();
								break;
							} // switch
							masterCode = tempStr;
							masterCode = masterCode.replaceAll("^U\\+", "");

							if (valueCode != null) {
								VariantChineseMap.put(valueCode, masterCode);
							} // end if
						} // end if
					} // end whiles
					wb.close();
				} // while
			} // end if

		} // end try

		catch (IOException e) {
			
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			String errStr = "CJKStringHandling:loadVariantChineseMap():" + errors.toString();
			System.out.println(errStr);

		} // end catch
	} // loadVariantChineseMap()

	public void setOriginalString(String str) {
		if (isCJKString(str)) {
			ori_str = str;
		} else {
			ori_str = null;
		} // end if
		con_str = null;
	} // end setOriginalString()

} // end class