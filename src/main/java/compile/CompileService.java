package compile;

import java.util.List;
import java.util.concurrent.Executors;
import org.apache.log4j.Logger;
import common.FactoryDao;
import common.LoggerManager;
import common.PropertyMap;
import common.Util;
import dao.CategoryDao;
import dao.PostDao;
import gitsync.GitSyncService;
import model.Post;
import model.Category;

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

	public void start(boolean isGitupdate) {
		if (this.parameter.getCompileStatus() != CompileStatus.wait) {
			logger.info("The compile was already start!");
			return;
		}
		logger.info("The compile is start!");
		Executors.newSingleThreadExecutor().execute(() -> {
			try {
				List<Category> categorys = FactoryDao.getDao(CategoryDao.class).selectAll();
				List<Post> posts = FactoryDao.getDao(PostDao.class).selectAllNotReservation();
				List<Post> posts2 = FactoryDao.getDao(PostDao.class).selectAllNotReservationOrderUpdatedate();
				FileManager filemanager = new FileManager();
				TemplateManager tempmanager = new TemplateManager();
				RssManager rssmanager = new RssManager(categorys, posts2);
				SitemapManager sitemapmanager = new SitemapManager(categorys, posts2);
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
				// main
				filemanager.createFile("index.html", tempmanager.createIndexTemp(posts));

				filemanager.createFile("search.html", tempmanager.createSearchTemp(posts));

				// list
				categorys.parallelStream().forEach(category -> {
					filemanager.createFile(category.getUniqcode() + ".html", tempmanager.createListTemp(category));
				});

				// post.html
				posts.parallelStream().forEach(post -> {
					filemanager.createAttachfiles(post.getIdx(), post.getAttachments());
					filemanager.createFile(post.getIdx() + ".html", tempmanager.createPostTemp(post, posts));
				});

				// rss
				filemanager.createFile("rss", rssmanager.build());

				// sitemap
				filemanager.createFile("sitemap.xml", sitemapmanager.build());
				filemanager.createFile("CNAME", "www.nowonbun.com");
				filemanager.createFile("ads.txt", "google.com, pub-6300064900388375, DIRECT, f08c47fec0942fa0");

				// robots.txt
				StringBuffer sb = new StringBuffer();
				sb.append("User-agent: *");
				sb.append("\r\n");
				sb.append("Allow: /");
				// sb.append("\r\n");
				// sb.append("\r\n");

				/*
				 * String hostname = PropertyMap.getInstance().getProperty("config",
				 * "host_name"); sb.append("Sitemap: " + hostname + "/sitemap.xml");
				 */

				filemanager.createFile("robots.txt", sb.toString());
				filemanager.createFile("robot.txt", sb.toString());
				filemanager.createFile("ads.txt","google.com, pub-6300064900388375, DIRECT, f08c47fec0942fa0");
				String gitroot = PropertyMap.getInstance().getProperty("config", "gitRoot");
				String httppath = PropertyMap.getInstance().getProperty("config", "httpServer");
				String groupName = PropertyMap.getInstance().getProperty("config", "httpGroup");
				String filepermission = PropertyMap.getInstance().getProperty("config", "httpFilePermission");
				String dirpermission = PropertyMap.getInstance().getProperty("config", "httpDirPermisstion");
				if (!Util.StringIsEmptyOrNull(gitroot) && !Util.StringEquals(httppath, gitroot)) {
					filemanager.copyToHttpRoot(gitroot, groupName, filepermission, dirpermission);
				}
				setStatus(CompileStatus.finish, "This compiler was completed.", 100);
				new Thread(() -> {
					try {
						Thread.sleep(1000 * 10);
					} catch (Throwable e) {
						logger.error(e);
					}
					setStatus(CompileStatus.wait, "This compiler was ready.", 0);
				}).start();
				if (isGitupdate) {
					GitSyncService.getInstance().start();
				}
			} catch (Throwable e) {
				logger.error(e);
			}
		});
	}
}
