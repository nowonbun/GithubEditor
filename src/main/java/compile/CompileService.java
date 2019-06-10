package compile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import bean.ListBean;
import bean.MenuBean;
import common.FactoryDao;
import common.LoggerManager;
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
	private final Logger logger;

	public static CompileService getInstance() {
		if (instance == null) {
			instance = new CompileService();
		}
		return instance;
	}

	private CompileService() {
		logger = LoggerManager.getLogger(CompileService.class);
		logger.info("The compileService class was created.");
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
			logger.info("status: " + status.toString() + " message : " + message + " progress : " + progress);
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
		if (this.parameter.getCompileStatus() != CompileStatus.wait) {
			logger.info("The compile was already start!");
			return;
		}
		logger.info("The compile is start!");
		Executors.newSingleThreadExecutor().execute(() -> {
			try {
				FileManager filemanager = new FileManager();
				TemplateManager tempmanager = new TemplateManager();
				setStatus(CompileStatus.start, "The compiler will be start.", 1);
				setStatus(CompileStatus.init, "The git root files will be  all deleted", 5);
				filemanager.initGitDirectory();
				setStatus(CompileStatus.init, "The git root files will be initialize", 10);

				setStatus(CompileStatus.copy, "The Javascript files was copied to git root", 15);
				filemanager.copyDirectoryToGitRoot("js");
				setStatus(CompileStatus.copy, "The Css files was copied to git root", 20);
				filemanager.copyDirectoryToGitRoot("css");
				setStatus(CompileStatus.copy, "The Image files was copied to git root", 25);
				filemanager.copyDirectoryToGitRoot("img");

				String mainTemp = PropertyMap.getInstance().getTemplateFile("main");
				String listTemp = PropertyMap.getInstance().getTemplateFile("list");
				String postTemp = PropertyMap.getInstance().getTemplateFile("post");
				String searchTemp = PropertyMap.getInstance().getTemplateFile("search");

				String title = PropertyMap.getInstance().getProperty("config", "title");
				String menu = createMenu();
				mainTemp = replaceTagForTemplate(mainTemp, "TITLE", title);
				mainTemp = replaceTagForTemplate(mainTemp, "MENU", menu);

				searchTemp = replaceTagForTemplate(searchTemp, "TITLE", title);
				searchTemp = replaceTagForTemplate(searchTemp, "MENU", menu);

				// index.html
				filemanager.createFile("index.html", mainTemp);

				// search.html
				filemanager.createFile("search.html", searchTemp);

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
					filemanager.createFile(category.getUniqcode() + ".html", template);

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

					filemanager.createFile(category.getUniqcode() + ".json", Util.getGson().toJson(list));
				});

				// post.html
				List<Post> posts = FactoryDao.getDao(PostDao.class).selectAll();
				posts.parallelStream().forEach(post -> {
					filemanager.createAttachfiles(post.getIdx(), post.getAttachments());
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
					filemanager.createFile(post.getIdx() + ".html", template);
				});

				// rss
				String rss = createRss(posts);
				filemanager.createFile("rss", rss);

				// sitemap
				String sitemap = createSiteMap(posts);
				filemanager.createFile("sitemap.xml", sitemap);

				String httppath = PropertyMap.getInstance().getProperty("config", "httpServer");
				String groupName = PropertyMap.getInstance().getProperty("config", "httpGroup");
				String permission = PropertyMap.getInstance().getProperty("config", "httpPermission");
				filemanager.copyToHttpRoot(httppath, groupName, permission);

				setStatus(CompileStatus.finish, "This compiler was completed.", 100);
				new Thread(() -> {
					try {
						Thread.sleep(1000 * 10);
					} catch (Throwable e) {
						logger.error(e);
					}
					setStatus(CompileStatus.wait, "This compiler was ready.", 0);
				}).start();
			} catch (Throwable e) {
				logger.error(e);
			}
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

	private String createSiteMap(List<Post> posts) {
		// http://www.nowonbun.com/sitemap.xml
		StringBuffer xml = new StringBuffer();
		xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		xml.append("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">");
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
		xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
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
			logger.error(e);
			throw new RuntimeException(e);
		}
	}

	private String createTag(String tagName, String data) {
		return "<" + tagName + ">" + data + "</" + tagName + ">";
	}

	private String replaceCategory(Category category, String template) {
		return template;
	}

	private String replacePost(Post post, String template) {
		return template;
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
			logger.error(e);
			throw new RuntimeException(e);
		}
	}
}
