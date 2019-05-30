package test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormatTest {
	private final static DateFormat GMTDateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
	private final static DateFormat GMTDateFormat2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
			//yyyy-MM-ddTHH:mm:ss.fffffffzzz
	public static void main(String[] args) {
		//format "ddd, dd MMM yyyy HH:mm:ss 'GMT'"
		//Wed, 24 Apr 2019 00:13:30 GMT
		//https://docs.oracle.com/javase/jp/8/docs/api/java/text/SimpleDateFormat.html
		//2019-04-24T00:18:49.0000000+09:00
		String ret = GMTDateFormat.format(new Date());
		System.out.println(ret);
		
	}
}
