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
				List<Post> posts = FactoryDao.getDao(PostDao.class).selectAll();

				FileManager filemanager = new FileManager();
				TemplateManager tempmanager = new TemplateManager();
				RssManager rssmanager = new RssManager(posts);
				SitemapManager sitemapmanager = new SitemapManager(posts);
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

				// index.html
				filemanager.createFile("index.html", tempmanager.createMainTemp());

				// search.html
				filemanager.createFile("search.html", tempmanager.createSearchTemp());

				// list.html
				List<Category> categorys = FactoryDao.getDao(CategoryDao.class).selectAll();
				categorys.parallelStream().forEach(category -> {
					if (category.getCategories().size() > 0) {
						return;
					}
					filemanager.createFile(category.getUniqcode() + ".html", tempmanager.createListTemp(category));

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
				posts.parallelStream().forEach(post -> {
					filemanager.createAttachfiles(post.getIdx(), post.getAttachments());
					filemanager.createFile(post.getIdx() + ".html", tempmanager.createPostTemp(post));
				});

				// rss
				filemanager.createFile("rss", rssmanager.build());

				// sitemap
				filemanager.createFile("sitemap.xml", sitemapmanager.build());

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

	private String replaceTagForTemplate(String template, String tagName, String data) {
		return template.replace("#####" + tagName + "#####", data);
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
