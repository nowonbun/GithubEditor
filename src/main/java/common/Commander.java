package common;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Commander {
	public static String exec(String cmd) {
		Process process = null;
		try {
			process = Runtime.getRuntime().exec(cmd);
			try (InputStreamReader input = new InputStreamReader(process.getInputStream())) {
				try (BufferedReader reader = new BufferedReader(input)) {
					String line = null;
					while ((line = reader.readLine()) != null) {
						//reader
						//https://hojak99.tistory.com/338
						//https://stackoverflow.com/questions/3403226/how-to-run-linux-commands-in-java
						//https://jeong-pro.tistory.com/85
					}
				}
			}
return "";
		} catch (Throwable e) {
			throw new RuntimeException(e);
		} finally {
			if (process != null) {
				process.destroy();
			}
		}
	}
}
