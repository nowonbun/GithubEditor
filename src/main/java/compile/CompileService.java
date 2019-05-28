package compile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import common.FactoryDao;
import common.PropertyMap;
import dao.CategoryDao;
import dao.PostDao;
import model.Category;
import model.Post;

public class CompileService {
	private static CompileService instance = null;

	public static CompileService getInstance() {
		if (instance == null) {
			instance = new CompileService();
		}
		return instance;
	}

	private CompileService() {
		parameter = new CompileParameter();
		parameter.setCompileStatus(CompileStatus.wait);
		parameter.setProgress(0);
		parameter.setMessage("This compiler was ready.");
	}

	private final CompileParameter parameter;

	private void setStatus(CompileStatus status, String message, int progress) {
		synchronized (this.parameter) {
			this.parameter.setCompileStatus(status);
			this.parameter.setMessage(message);
			this.parameter.setProgress(progress);
		}
	}

	public int getStatusToInt() {
		return this.parameter.getCompileStatusToInt();
	}

	public String getMessage() {
		return this.parameter.getMessage();
	}

	public int getProgress() {
		return this.parameter.getProgress();
	}

	public void start() {
		Executors.newSingleThreadExecutor().execute(() -> {
			setStatus(CompileStatus.start, "The compiler will be start.", 1);
			String path = PropertyMap.getInstance().getProperty("config", "gitRoot");
			setStatus(CompileStatus.start, "The git root files will be  all deleted", 5);
			deleteFiles(path);
			setStatus(CompileStatus.start, "The git root files will be initialize", 10);
			File file = new File(path);
			file.mkdir();

			List<File> jsFiles = getFiles(PropertyMap.getInstance().getWebRootPath() + "\\js", null);
			File jsDir = new File(path + File.pathSeparator);
			jsDir.mkdir();
			jsFiles.parallelStream().forEach(f -> {
				try {
					copyFile(f.getAbsolutePath(), path + File.pathSeparator + f.getName());
				} catch (Throwable e) {
					e.printStackTrace();
				}
			});

			String main = PropertyMap.getInstance().getTemplateFile("main");
			String list = PropertyMap.getInstance().getTemplateFile("list");
			// file create
			createFile(path + File.pathSeparator + "index.html", main);

			List<Category> categorys = FactoryDao.getDao(CategoryDao.class).selectAll();
			categorys.parallelStream().forEach(category -> {
				String template = list;
				template = replaceCategory(category, template);
				createFile(path + File.pathSeparator + category.getUniqcode() + ".html", template);
			});

			List<Post> posts = FactoryDao.getDao(PostDao.class).selectAll();
			posts.parallelStream().forEach(post -> {
				String template = PropertyMap.getInstance().getTemplateFile("post");
				template = replacePost(post, template);
				createFile(path + File.pathSeparator + post.getIdx() + ".html", template);
			});

			// rss - xml
			// sitemap - xml

			setStatus(CompileStatus.wait, "This compiler was ready.", 0);
		});
	}

	private void copyFile(String src, String dest) throws FileNotFoundException, IOException {
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

	private String replaceCategory(Category category, String template) {
		return template;
	}

	private String replacePost(Post post, String template) {
		return template;
	}

	private void createFile(String filename, String data) {
		File file = new File(filename);
		byte[] binary = data.getBytes(StandardCharsets.UTF_8);
		try (FileOutputStream output = new FileOutputStream(file)) {
			output.write(binary, 0, binary.length);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	private static List<File> getFiles(String path, List<File> list) {
		if (list == null) {
			list = new ArrayList<>();
		}
		File file = new File(path);

		if (file.isDirectory()) {
			for (File f : file.listFiles()) {
				getFiles(f.getAbsolutePath(), list);
			}
		}
		if (file.isFile()) {
			list.add(file);
		}
		return list;
	}

	private static void deleteFiles(String path) {
		File file = new File(path);

		if (file.isDirectory()) {
			for (File f : file.listFiles()) {
				deleteFiles(f.getAbsolutePath());
			}
			file.delete();
		}
		if (file.isFile()) {
			file.delete();
		}
	}
}
