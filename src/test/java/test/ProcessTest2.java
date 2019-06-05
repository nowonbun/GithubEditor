package test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Scanner;
import java.util.concurrent.Executors;

public class ProcessTest2 {
	public static void main(String[] args) {
		try {
			//https://stackoverflow.com/questions/3643939/java-process-with-input-output-stream
			Process process = Runtime.getRuntime().exec("/bin/bash");
			OutputStream stdin = process.getOutputStream();
			InputStream stderr = process.getErrorStream();
			InputStream stdout = process.getInputStream();

			Executors.newCachedThreadPool().execute(() -> {
				try (BufferedReader reader = new BufferedReader(new InputStreamReader(stdout))) {
					String line;
					while ((line = reader.readLine()) != null) {
						System.out.println(line);
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			});

			Executors.newCachedThreadPool().execute(() -> {
				try (BufferedReader reader = new BufferedReader(new InputStreamReader(stderr))) {
					String line;
					while ((line = reader.readLine()) != null) {
						System.out.println("err " + line);
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			});

			Executors.newCachedThreadPool().execute(() -> {
				try (Scanner scan = new Scanner(System.in)) {
					try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stdin))) {
						while (true) {
							String input = scan.nextLine();
							input += "\n";
							writer.write(input);
							writer.flush();
							if ("exit\n".equals(input)) {
								System.exit(0);
							}
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});

		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
