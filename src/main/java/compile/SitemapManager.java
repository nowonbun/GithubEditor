package compile;

import java.util.List;

import common.PropertyMap;
import common.Util;
import model.Post;

public class SitemapManager extends AbstractManager {
	private List<Post> posts;
	private String locRoot;
	private String changefred;
	private String priority;

	public SitemapManager(List<Post> posts) {
		this.posts = posts;
		this.locRoot = PropertyMap.getInstance().getProperty("config", "host_name");
		this.changefred = PropertyMap.getInstance().getProperty("config", "sitemap_changefred");
		this.priority = PropertyMap.getInstance().getProperty("config", "sitemap_priority");
	}

	public String build() {
		// http://www.nowonbun.com/sitemap.xml
		StringBuffer xml = new StringBuffer();
		xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		xml.append("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">");
		for (Post post : posts) {
			xml.append(createTag("url", () -> {
				StringBuffer url = new StringBuffer();
				url.append(createTag("loc", this.locRoot + "/" + post.getIdx() + ".html"));
				url.append(createTag("lastmod", Util.convertGMT2DateFormat(post.getLastupdateddate())));
				url.append(createTag("changefred", this.changefred));
				url.append(createTag("priority", this.priority));
				return url.toString();
			}));
		}
		xml.append("</urlset>");
		return xml.toString();
	}

}
