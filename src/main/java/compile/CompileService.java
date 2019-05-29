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
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

import common.FactoryDao;
import common.PropertyMap;
import common.IF.LambdaExpression;
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
			setStatus(CompileStatus.init, "The git root files will be  all deleted", 5);
			deleteFiles(path);
			setStatus(CompileStatus.init, "The git root files will be initialize", 10);
			File file = new File(path);
			file.mkdir();

			setStatus(CompileStatus.copy, "The Javascript files was copied to git root", 15);
			copyDirectory("js");
			setStatus(CompileStatus.copy, "The Css files was copied to git root", 20);
			copyDirectory("css");
			setStatus(CompileStatus.copy, "The Image files was copied to git root", 25);
			copyDirectory("img");

			String mainTemp = PropertyMap.getInstance().getTemplateFile("main");
			String listTemp = PropertyMap.getInstance().getTemplateFile("list");
			String postTemp = PropertyMap.getInstance().getTemplateFile("post");
			// file create
			createFile(path + File.separator + "index.html", mainTemp);

			List<Category> categorys = FactoryDao.getDao(CategoryDao.class).selectAll();
			categorys.parallelStream().forEach(category -> {
				String template = replaceCategory(category, listTemp);
				createFile(path + File.separator + category.getUniqcode() + ".html", template);
			});

			List<Post> posts = FactoryDao.getDao(PostDao.class).selectAll();
			posts.parallelStream().forEach(post -> {
				String template = replacePost(post, postTemp);
				createFile(path + File.separator + post.getIdx() + ".html", template);
			});
			String rss = createRss(posts);
			createFile(path + File.separator + "rss", rss);
			// rss - xml
			// sitemap - xml

			setStatus(CompileStatus.wait, "This compiler was ready.", 0);
		});
	}
	
	private String createRss(List<Post> posts)
    {
		//file:///home/nowonbun/Downloads/rss
		StringBuffer xml = new StringBuffer();
		xml.append("<?xml version=\\\"1.0\\\" encoding=\\\"UTF-8\\\"?>");
		xml.append("<rss version=\"2.0\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:taxo=\"http://purl.org/rss/1.0/modules/taxonomy/\" xmlns:activity=\"http://activitystrea.ms/spec/1.0/\" >");
		xml.append(createTag("channel", ()->{
			StringBuffer channel = new StringBuffer();
			channel.append(createTag("title", ""));
			channel.append(createTag("link",""));
			channel.append(createTag("description",""));
			channel.append(createTag("language",""));
			// format "ddd, dd MMM yyyy HH:mm:ss 'GMT'"
			channel.append(createTag("pubDate",""));
			channel.append(createTag("generator",""));
			channel.append(createTag("managingEditor", ""));
			channel.append(createTag("webMaster", ""));
			for(Post post: posts) {
				channel.append(createTag("item", () ->{
					StringBuffer item = new StringBuffer();
					item.append(createTag("title", ""));
					item.append(createTag("link", ""));
					item.append(createTag("description", ""));
					item.append(createTag("category", ""));
					item.append(createTag("guid", ""));
					item.append(createTag("pubDate", ""));
					return item.toString();
				}));
			}
			return channel.toString();
		}));
		xml.append("</rss>");
		return xml.toString();
    }
	
	 private String CreateDescription(String contents)
     {
		 //return "";
         contents = contents.toLowerCase();
         int pos = contents.indexOf("<pre");
         while (pos > -1)
         {
             int epos = contents.indexOf("</pre>", pos);
             if (epos < 0)
             {
                 break;
             }
             //contents = contents.Remove(pos, epos - pos);
             //contents.subSequence(beginIndex, endIndex)
             pos = contents.indexOf("<pre");
         }
         return "<![CDATA[" + contents.replaceAll("<[^>]*>", "").replace("&nbsp;", "") + "]]>";
         //return "<![CDATA[" + Regex.Replace(contents, "<[^>]*>", "").Replace("&nbsp;", "") + "]]>";
     }
	
	private String createTag(String tagName, Callable<String> func) {
		try {
			return createTag(tagName, func.call());
		}catch(Throwable e) {
			throw new RuntimeException(e);
		}
	}


	private String createTag(String tagName, String data) {
		return "<" + tagName + ">" + data + "</" + tagName + ">";
	}

	private void copyDirectory(String dirName) {
		String path = PropertyMap.getInstance().getProperty("config", "gitRoot");
		List<File> files = getFiles(PropertyMap.getInstance().getWebRootPath() + File.separator + dirName);
		File newDir = new File(path + File.separator + dirName);
		if (newDir.exists()) {
			deleteFiles(newDir.getAbsolutePath());
		}
		newDir.mkdir();
		files.parallelStream().forEach(f -> {
			try {
				copyFile(f.getAbsolutePath(), newDir.getAbsolutePath() + File.separator + f.getName());
			} catch (Throwable e) {
				e.printStackTrace();
			}
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

	private List<File> getFiles(String path) {
		return getFiles(path, null);
	}

	private List<File> getFiles(String path, List<File> list) {
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

	private void deleteFiles(String path) {
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
