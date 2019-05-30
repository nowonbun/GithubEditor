package test;

import java.io.File;

public class StrRemove {
	public static void main(String[] args) {
		String contents = "test<pre>codeing</pre> aaaaaa<pre>coding</pre>asdasads";
		int pos = contents.indexOf("<pre");
		while (pos > -1) {
			int epos = contents.indexOf("</pre>", pos);
			if (epos < 0) {
				break;
			}
			epos += 6;
			String pre = contents.substring(0, pos);
			String after = contents.substring(epos, contents.length());
			contents = pre + System.lineSeparator() +after;
			pos = contents.indexOf("<pre");
		}
		System.out.println(contents);
	}
}
