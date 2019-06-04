package test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.concurrent.Executors;

public class ProcessTest {
	public static void main(String[] args) {
		ProcessBuilder processBuilder = new ProcessBuilder();
		processBuilder.command("bash");
		try {

			Process process = processBuilder.start();
			Executors.newCachedThreadPool().submit(() -> {
				String line;
				try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
					while ((line = reader.readLine()) != null) {
						System.out.println(line + "\n");
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			});
			Executors.newCachedThreadPool().submit(() -> {
				try (OutputStreamWriter writer = new OutputStreamWriter(process.getOutputStream())) {
					writer.append("pwd");
					writer.flush();
					
					writer.append("cd /homw/nowonbun/git/BlogTest");
					writer.flush();
					
					writer.append("ls -al");
					writer.flush();
					
					//Thread.sleep(5000);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			});

			int exitVal = process.waitFor();
			if (exitVal == 0) {
				System.out.println("Success!");
				System.exit(0);
			} else {
				// abnormal...
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
