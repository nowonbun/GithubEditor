package common;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import com.google.gson.Gson;

import common.IF.LambdaExpression;

public class Util {

	private final static DateFormat yyyyMMddFormat = new SimpleDateFormat("yyyy/MM/dd");
	private final static DateFormat dateFormat2 = new SimpleDateFormat("yyyyMMddHHmmss");
	private final static DateFormat javascriptDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	private final static DateFormat GMTDateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
	private final static DateFormat GMTDateFormat2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
	private static Gson gson = null;

	public static boolean StringEquals(String val1, String val2) {
		if (val1 == null) {
			return false;
		}
		if (val2 == null) {
			return false;
		}
		return val1.equals(val2);
	}

	public static boolean StringEqualsUpper(String val1, String val2) {
		if (val1 == null) {
			return false;
		}
		if (val2 == null) {
			return false;
		}
		return val1.toUpperCase().equals(val2.toUpperCase());
	}

	public static boolean StringIsEmptyOrNull(String val) {
		if (val == null) {
			return true;
		}
		if (val.trim().length() == 0) {
			return true;
		}
		return false;
	}

	public static Date getDateFromString(String pDate) {
		try {
			return yyyyMMddFormat.parse(pDate);
		} catch (ParseException e) {
			return null;
		}
	}

	public static Date getNow() {
		return Calendar.getInstance().getTime();
	}

	public static int getYear(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.YEAR);
	}

	public static int getMonth(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.MONTH) + 1;
	}

	public static int getDay(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.DAY_OF_MONTH);
	}

	public static String createCookieKey() {
		String key = UUID.randomUUID().toString();
		return key.replace("-", "") + dateFormat2.format(new Date());
	}

	public static String getTimeUnique() {
		return dateFormat2.format(new Date());
	}

	public static int getCookieExpire() {
		return 60 * 60 * 24 * PropertyMap.getInstance().getPropertyInt("config", "cookie_expire");
	}

	public static String getCookiePath() {
		return PropertyMap.getInstance().getProperty("config", "cookie_path");
	}

	public static <T> T searchArray(T[] array, LambdaExpression<T, Boolean> condition) {
		if (array == null) {
			return null;
		}
		for (T node : array) {
			if (condition.run(node)) {
				return node;
			}
		}
		return null;
	}

	public static String convertDateFormat(Date date) {
		if (date == null) {
			return null;
		}
		return javascriptDateFormat.format(date);
	}

	public static String convertGMTDateFormat(Date date) {
		if (date == null) {
			return null;
		}
		return GMTDateFormat.format(date);
	}

	public static String convertGMT2DateFormat(Date date) {
		if (date == null) {
			return null;
		}
		return GMTDateFormat2.format(date);
	}

	public static Gson getGson() {
		if (Util.gson == null) {
			Util.gson = new Gson();
		}
		return Util.gson;
	}

	public static void copyFile(String src, String dest) throws FileNotFoundException, IOException {
		try (InputStream inputStream = new FileInputStream(src)) {
			try (OutputStream outputStream = new FileOutputStream(dest)) {
				byte[] buffer = new byte[1024];
				int length = 0;
				while ((length = inputStream.read(buffer)) > 0) {
					outputStream.write(buffer, 0, length);
				}
			}
		}
	}
}