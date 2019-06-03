package compile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import bean.ListBean;
import bean.MenuBean;
import common.FactoryDao;
import common.PropertyMap;
import common.Util;
import dao.AttachmentDao;
import dao.CategoryDao;
import dao.PostDao;
import model.Attachment;
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

			File attachPath = new File(path + File.separator + "contents");
			if (attachPath.exists()) {
				deleteFiles(attachPath);
			}
			attachPath.mkdir();

			setStatus(CompileStatus.copy, "The Javascript files was copied to git root", 15);
			copyDirectoryToGitRoot("js");
			setStatus(CompileStatus.copy, "The Css files was copied to git root", 20);
			copyDirectoryToGitRoot("css");
			setStatus(CompileStatus.copy, "The Image files was copied to git root", 25);
			copyDirectoryToGitRoot("img");

			String mainTemp = PropertyMap.getInstance().getTemplateFile("main");
			String listTemp = PropertyMap.getInstance().getTemplateFile("list");
			String postTemp = PropertyMap.getInstance().getTemplateFile("post");

			String title = PropertyMap.getInstance().getProperty("config", "title");
			String menu = createMenu();
			mainTemp = replaceTagForTemplate(mainTemp, "TITLE", title);
			mainTemp = replaceTagForTemplate(mainTemp, "MENU", menu);

			// index.html
			createFile(path + File.separator + "index.html", mainTemp);

			// list.html
			List<Category> categorys = FactoryDao.getDao(CategoryDao.class).selectAll();
			categorys.parallelStream().forEach(category -> {
				if (category.getCategories().size() > 0) {
					return;
				}
				String template = replaceCategory(category, listTemp);
				template = replaceTagForTemplate(template, "TITLE", title + " :: " + getCategoryName(category));
				template = replaceTagForTemplate(template, "MENU", menu);
				template = replaceTagForTemplate(template, "CATEGORYNAME", getCategoryName(category));
				template = replaceTagForTemplate(template, "JSONFILE", "./" + category.getUniqcode() + ".json");
				createFile(path + File.separator + category.getUniqcode() + ".html", template);

				List<Post> postsOfCategory = FactoryDao.getDao(PostDao.class).selectByCategoryAll(category);
				List<ListBean> list = new ArrayList<>();
				for (Post post : postsOfCategory) {
					ListBean bean = new ListBean();
					bean.setIdx(post.getIdx());
					bean.setTitle(post.getTitle());
					bean.setTags(post.getTag());
					bean.setSummary(createDescription(post.getContents()));
					bean.setCreateddate(Util.convertDateFormat(post.getCreateddate()));
					bean.setLastupdateddate(Util.convertDateFormat(post.getLastupdateddate()));
					list.add(bean);
				}

				createFile(path + File.separator + category.getUniqcode() + ".json", Util.getGson().toJson(list));
			});

			// post.html
			List<Post> posts = FactoryDao.getDao(PostDao.class).selectAll();
			posts.parallelStream().forEach(post -> {
				File postAttach = new File(attachPath.getAbsolutePath() + File.separator + post.getIdx());
				if (postAttach.exists()) {
					deleteFiles(postAttach);
				}
				postAttach.mkdir();
				for (Attachment attach : post.getAttachments()) {
					try {
						createFile(postAttach.getAbsoluteFile() + File.separator + attach.getIdx() + "_" + URLEncoder.encode(attach.getFilename(), StandardCharsets.UTF_8.toString()),
								attach.getData());
					} catch (Throwable e) {
						throw new RuntimeException(e);
					}
				}
				String template = replacePost(post, postTemp);
				template = replaceTagForTemplate(template, "TITLE", title + " :: " + post.getTitle());
				template = replaceTagForTemplate(template, "MENU", menu);
				template = replaceTagForTemplate(template, "CONTENTS_TITLE", post.getTitle());
				template = replaceTagForTemplate(template, "CATEGORY_LINK", "./" + post.getCategory().getUniqcode() + ".html");
				template = replaceTagForTemplate(template, "CATEGORY_NAME", getCategoryName(post.getCategory()));
				template = replaceTagForTemplate(template, "CREATED_DATE", Util.convertDateFormat(post.getCreateddate()));
				template = replaceTagForTemplate(template, "LAST_UPDATED_DATE", Util.convertDateFormat(post.getLastupdateddate()));
				template = replaceTagForTemplate(template, "CONTENTS", getContetns(post));
				template = replaceTagForTemplate(template, "TAG", post.getTag());
				createFile(path + File.separator + post.getIdx() + ".html", template);
			});

			// rss
			String rss = createRss(posts);
			createFile(path + File.separator + "rss", rss);

			// sitemap
			String sitemap = createSiteMap(posts);
			createFile(path + File.separator + "sitemap.xml", sitemap);

			String httppath = PropertyMap.getInstance().getProperty("config", "httpServer");
			deleteFiles(httppath);
			File http = new File(httppath);
			http.mkdir();
			copyDirectory(path, httppath);

			setStatus(CompileStatus.wait, "This compiler was ready.", 0);
		});
	}

	private String getContetns(Post post) {
		Document doc = Jsoup.parse(post.getContents());
		Elements nodes = doc.select("img[data-filename],a.attachfile[data-filename]");
		for (Element node : nodes) {
			String attr = null;
			if (node.tagName().equals("img")) {
				attr = node.attr("src");
			}
			if (node.tagName().equals("a")) {
				attr = node.attr("href");
			}
			if (!Util.StringIsEmptyOrNull(attr)) {
				String idx = attr.replace("./getAttachFile.ajax?idx=", "");
				idx = idx.trim();
				try {
					int id = Integer.parseInt(idx);
					Attachment attachment = FactoryDao.getDao(AttachmentDao.class).select(id);
					if (attachment != null) {
						attr = "./contents/" + attachment.getPost().getIdx() + "/" + attachment.getIdx() + "_" + URLEncoder.encode(attachment.getFilename(), StandardCharsets.UTF_8.toString());
					} else {
						attr = "";
					}
				} catch (Throwable e) {
					attr = "";
				}
				if (node.tagName().equals("img")) {
					node.attr("src", attr);
				}
				if (node.tagName().equals("a")) {
					node.attr("href", attr);
				}
			}
		}
		return doc.html();
	}

	private String replaceTagForTemplate(String template, String tagName, String data) {
		return template.replace("#####" + tagName + "#####", data);
	}

	private void createFile(String path, byte[] data) {
		try (FileOutputStream stream = new FileOutputStream(path)) {
			stream.write(data, 0, data.length);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	private String createSiteMap(List<Post> posts) {
		// http://www.nowonbun.com/sitemap.xml
		StringBuffer xml = new StringBuffer();
		xml.append("<?xml version=\\\"1.0\\\" encoding=\\\"UTF-8\\\"?>");
		xml.append("<urlset xmlns=\\\"http://www.sitemaps.org/schemas/sitemap/0.9\\\">");
		for (Post post : posts) {
			xml.append(createTag("url", () -> {
				StringBuffer url = new StringBuffer();
				url.append(createTag("loc", PropertyMap.getInstance().getProperty("config", "host_name") + "/" + post.getIdx() + ".html"));
				url.append(createTag("lastmod", Util.convertGMT2DateFormat(post.getLastupdateddate())));
				url.append(createTag("changefred", PropertyMap.getInstance().getProperty("config", "sitemap_changefred")));
				url.append(createTag("priority", PropertyMap.getInstance().getProperty("config", "sitemap_priority")));
				return url.toString();
			}));
		}
		xml.append("</urlset>");
		return xml.toString();
	}

	private String createRss(List<Post> posts) {
		// file:///home/nowonbun/Downloads/rss
		StringBuffer xml = new StringBuffer();
		xml.append("<?xml version=\\\"1.0\\\" encoding=\\\"UTF-8\\\"?>");
		xml.append(
				"<rss version=\"2.0\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:taxo=\"http://purl.org/rss/1.0/modules/taxonomy/\" xmlns:activity=\"http://activitystrea.ms/spec/1.0/\" >");
		xml.append(createTag("channel", () -> {
			StringBuffer channel = new StringBuffer();
			channel.append(createTag("title", PropertyMap.getInstance().getProperty("config", "rss_title")));
			channel.append(createTag("link", PropertyMap.getInstance().getProperty("config", "rss_link")));
			channel.append(createTag("description", PropertyMap.getInstance().getProperty("config", "rss_description")));
			channel.append(createTag("language", PropertyMap.getInstance().getProperty("config", "rss_language")));
			channel.append(createTag("pubDate", Util.convertGMTDateFormat(new Date())));
			channel.append(createTag("generator", PropertyMap.getInstance().getProperty("config", "rss_generator")));
			channel.append(createTag("managingEditor", PropertyMap.getInstance().getProperty("config", "rss_managingEditor")));
			channel.append(createTag("webMaster", PropertyMap.getInstance().getProperty("config", "rss_webMaster")));
			for (Post post : posts) {
				channel.append(createTag("item", () -> {
					String link = PropertyMap.getInstance().getProperty("config", "host_name") + "/" + post.getIdx() + ".html";
					StringBuffer item = new StringBuffer();
					item.append(createTag("title", post.getTitle()));
					item.append(createTag("link", link));
					item.append(createTag("description", createDescription(post.getContents())));
					item.append(createTag("category", getCategoryName(post.getCategory())));
					item.append(createTag("author", PropertyMap.getInstance().getProperty("config", "rss_author")));
					item.append(createTag("guid", link));
					item.append(createTag("pubDate", Util.convertGMTDateFormat(post.getLastupdateddate())));
					return item.toString();
				}));
			}
			return channel.toString();
		}));
		xml.append("</rss>");
		return xml.toString();
	}

	private String getCategoryName(Category category) {
		String name = "";
		if (category.getCategory() != null) {
			name += getCategoryName(category.getCategory()) + " / ";
		}
		name += category.getName();
		return name;
	}

	private String createDescription(String contents) {
		contents = contents.toLowerCase();
		int pos = contents.indexOf("<pre");
		while (pos > -1) {
			int epos = contents.indexOf("</pre>", pos);
			if (epos < 0) {
				break;
			}
			epos += 6;
			String pre = contents.substring(0, pos);
			String after = contents.substring(epos, contents.length());
			contents = pre + System.lineSeparator() + after;
			pos = contents.indexOf("<pre");
		}
		// return "<![CDATA[" + contents.replaceAll("<[^>]*>", "").replace("&nbsp;", "")
		// + "]]>";
		String ret = contents.replaceAll("<[^>]*>", "").replace("&nbsp;", "");
		if (ret.length() > 1020) {
			return ret.substring(0, 1020);
		}
		return ret;
	}

	private String createTag(String tagName, Callable<String> func) {
		try {
			return createTag(tagName, func.call());
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	private String createTag(String tagName, String data) {
		return "<" + tagName + ">" + data + "</" + tagName + ">";
	}

	private void copyDirectory(String src, String dest) {
		File source = new File(src);
		File destination = new File(dest);
		if (source.isDirectory()) {
			if (!destination.exists()) {
				destination.mkdir();
			}
			File[] files = source.listFiles();
			for (File file : files) {
				copyDirectory(src + File.separator + file.getName(), dest + File.separator + file.getName());
			}
		}
		if (source.isFile()) {
			try {
				copyFile(src, dest);
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}

	private void copyDirectoryToGitRoot(String dirName) {
		String path = PropertyMap.getInstance().getProperty("config", "gitRoot");
		List<File> files = getFiles(PropertyMap.getInstance().getWebRootPath() + File.separator + dirName);
		File newDir = new File(path + File.separator + dirName);
		if (newDir.exists()) {
			deleteFiles(newDir);
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

	private void deleteFiles(File file) {
		deleteFiles(file.getAbsolutePath());
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

	private String createMenu() {
		StringBuffer sb = new StringBuffer();
		try {
			List<Category> categorylist = FactoryDao.getDao(CategoryDao.class).selectAll();
			List<Category> pList = categorylist.stream().filter(x -> x.getCategory() == null).sorted((x, y) -> Integer.compare(x.getSeq(), y.getSeq())).collect(Collectors.toList());
			for (Category c : pList) {
				sb.append("<li class=\"\">");
				List<Category> sublist = categorylist.stream().filter(x -> x.getCategory() == c).sorted((x, y) -> Integer.compare(x.getSeq(), y.getSeq())).collect(Collectors.toList());
				if (sublist.size() > 0) {
					sb.append("<a class=\"link_item link-item-collapse\" href=\"javascript:void(0)\">");
					sb.append(c.getName());
					sb.append("<span class=\"fa fa-chevron-down pull-right\"></span></a>");
					sb.append("<ul class=\"sub_category_list off\">");
					for (Category sub : sublist) {
						sb.append("<li class=\"\"><a class=\"link_sub_item\" href=\"");
						sb.append(sub.getUniqcode() + ".html");
						sb.append("\">");
						sb.append(sub.getName());
						sb.append("</a></li>");
					}
					sb.append("</ul>");
				} else {
					sb.append("<a class=\"link_item link-item-collapse\" href=\"");
					sb.append(c.getUniqcode() + ".html");
					sb.append("\">");
					sb.append(c.getName());
					sb.append("</a>");
				}
				MenuBean bean = new MenuBean();
				bean.setUrl("list.html?category=" + c.getCode());
				bean.setText(c.getName());
				sb.append("</li>");
			}
			return sb.toString();
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}
}
