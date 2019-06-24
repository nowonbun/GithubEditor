package compile;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import org.apache.log4j.Logger;
import bean.ListBean;
import common.FactoryDao;
import common.LoggerManager;
import common.PropertyMap;
import common.Util;
import dao.PostDao;
import model.Post;

public class CompileService extends AbstractManager {
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
				/*filemanager.createFile("index.html", tempmanager.createMainTemp());
				List<ListBean> totalList = new LinkedList<>();
				posts.forEach(post -> {
					totalList.add(convertPost(post));
				});
				filemanager.createFile("list.json", Util.getGson().toJson(totalList));*/
				filemanager.createFile("index.html", tempmanager.createSearchTemp(posts));
				//filemanager.createFile("search.html", tempmanager.createSearchTemp(posts));

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
				String filepermission = PropertyMap.getInstance().getProperty("config", "httpFilePermission");
				String dirpermission = PropertyMap.getInstance().getProperty("config", "httpDirPermisstion");
				filemanager.copyToHttpRoot(httppath, groupName, filepermission, dirpermission);

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

	private ListBean convertPost(Post post) {
		ListBean bean = new ListBean();
		bean.setIdx(post.getIdx());
		bean.setTitle(post.getTitle());
		bean.setTags(post.getTag());
		bean.setSummary(createDescription(post.getContents()));
		bean.setCreateddate(Util.convertDateFormat(post.getCreateddate()));
		bean.setLastupdateddate(Util.convertDateFormat(post.getLastupdateddate()));
		bean.setCategoryCode(post.getCategory().getCode());
		bean.setCategoryName(super.getCategoryName(post.getCategory()));

		return bean;
	}
}
