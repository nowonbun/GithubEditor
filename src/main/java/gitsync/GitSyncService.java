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
import org.apache.log4j.Logger;
import common.LoggerManager;
import common.PropertyMap;
import common.Util;

public class GitSyncService {

	private static GitSyncService instance = null;
	private final Logger logger;

	public static GitSyncService getInstance() {
		if (instance == null) {
			instance = new GitSyncService();
		}
		return instance;
	}

	private final GitSyncParameter parameter;

	private GitSyncService() {
		logger = LoggerManager.getLogger(GitSyncService.class);
		this.parameter = new GitSyncParameter();
		this.parameter.setStart(false);
	}

	public String getMessage() {
		return this.parameter.getMessage();
	}

	public boolean isStart() {
		return this.parameter.isStart();
	}

	public void start() {
		// https://hojak99.tistory.com/338
		// https://www.mkyong.com/java/how-to-execute-shell-command-from-java/
		if (this.parameter.isStart()) {
			logger.info("The GitSync was already start!");
			return;
		}
		this.parameter.setStart(true);
		logger.info("The GitSync is start!");
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
							line += "\n";
							this.parameter.addMessage("$ " + line);
							logger.info("Bash ] " + line);
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						logger.error(e);
					}
				});

				Executors.newCachedThreadPool().execute(() -> {
					try (BufferedReader reader = new BufferedReader(new InputStreamReader(stderr))) {
						String line;
						while ((line = reader.readLine()) != null) {
							line += "\n";
							this.parameter.addMessage("$ " + line);
							logger.info("Bash Error] " + line);
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						logger.error(e);
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
					logger.error(e);
				}
				new Thread(() -> {
					try {
						Thread.sleep(1000 * 10);
						stdin.close();
						stderr.close();
						stdout.close();
						process.destroy();
						this.parameter.setStart(false);
						this.parameter.clearMessage();
					} catch (Throwable e) {
						logger.error(e);
					}
				}).start();
			} catch (Throwable e) {
				logger.error(e);
			}
		});
	}
}
