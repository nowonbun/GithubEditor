package test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternTest {
	public static void main(String[] args) {
		String a = "index.tpl.html";
		String b = "main.tpl.html";
		String c = "log4j.xml";
		
		Pattern p = Pattern.compile("\\.(tpl)\\.(html)$");
		Matcher m = p.matcher(c);
		System.out.println(m.find());
	}
}
