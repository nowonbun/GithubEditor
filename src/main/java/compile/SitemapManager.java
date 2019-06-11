package compile;

import java.util.List;

import common.PropertyMap;
import common.Util;
import model.Post;

public class SitemapManager extends AbstractManager {
	private List<Post> posts;

	public SitemapManager(List<Post> posts) {
		this.posts = posts;
	}

	public String build() {
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

}
