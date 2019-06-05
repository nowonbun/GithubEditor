package gitsync;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.concurrent.Executors;

import common.PropertyMap;
import common.Util;

public class GitSyncService {

	private static GitSyncService instance = null;

	public static GitSyncService getInstance() {
		if (instance == null) {
			instance = new GitSyncService();
		}
		return instance;
	}

	private final GitSyncParameter parameter;

	private GitSyncService() {
		this.parameter = new GitSyncParameter();
		this.parameter.setStart(false);
	}

	public void start() {
		// https://hojak99.tistory.com/338
		// https://www.mkyong.com/java/how-to-execute-shell-command-from-java/
		if (this.parameter.isStart()) {
			return;
		}
		Executors.newSingleThreadExecutor().execute(() -> {
			try {
				Process process = Runtime.getRuntime().exec("/bin/bash");
				OutputStream stdin = process.getOutputStream();
				InputStream stderr = process.getErrorStream();
				InputStream stdout = process.getInputStream();

				Executors.newCachedThreadPool().execute(() -> {
					try (BufferedReader reader = new BufferedReader(new InputStreamReader(stdout))) {
						String line;
						while ((line = reader.readLine()) != null) {
							this.parameter.addMessage(line);
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
							this.parameter.addMessage("Error : " + line);
							System.out.println("Error : " + line);
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				});

				try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stdin))) {
					writer.write("cd " + PropertyMap.getInstance().getProperty("config", "gitRoot"));
					writer.write("\n");
					writer.flush();
					writer.write("git add .");
					writer.write("\n");
					writer.flush();
					writer.write("git commit -a -m '" + Util.convertDateFormat(new Date()) + "'");
					writer.write("\n");
					writer.flush();
					writer.write("git push origin master");
					writer.write("\n");
					writer.flush();

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
	}
}
