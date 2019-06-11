package compile;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import common.PropertyMap;
import common.Util;
import model.Category;
import model.Post;

public class RssManager extends AbstractManager {

	private List<Post> posts;

	public RssManager(List<Post> posts) {
		super();
		this.posts = posts;
	}

	public String build() {
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
			for (Post post : this.posts) {
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
}
