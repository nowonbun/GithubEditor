package compile;

import java.util.Date;
import java.util.List;
import common.PropertyMap;
import common.Util;
import model.Post;

public class RssManager extends AbstractManager {

	private List<Post> posts;
	private String rssTitle;
	private String rssLink;
	private String rssDescription;
	private String rssLanguage;
	private String rssGenerator;
	private String rssManagingEditor;
	private String rssWebMaster;
	private String hostName;
	private String rssAuthor;

	public RssManager(List<Post> posts) {
		super();
		this.posts = posts;
		this.rssTitle = PropertyMap.getInstance().getProperty("config", "rss_title");
		this.rssLink = PropertyMap.getInstance().getProperty("config", "rss_link");
		this.rssDescription = PropertyMap.getInstance().getProperty("config", "rss_description");
		this.rssLanguage = PropertyMap.getInstance().getProperty("config", "rss_language");
		this.rssGenerator = PropertyMap.getInstance().getProperty("config", "rss_generator");
		this.rssManagingEditor = PropertyMap.getInstance().getProperty("config", "rss_managingEditor");
		this.rssWebMaster = PropertyMap.getInstance().getProperty("config", "rss_webMaster");
		this.hostName = PropertyMap.getInstance().getProperty("config", "host_name");
		this.rssAuthor = PropertyMap.getInstance().getProperty("config", "rss_author");
	}

	public String build() {
		StringBuffer xml = new StringBuffer();
		xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		xml.append("<rss version=\"2.0\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:taxo=\"http://purl.org/rss/1.0/modules/taxonomy/\" xmlns:activity=\"http://activitystrea.ms/spec/1.0/\" >");
		xml.append(createTag("channel", () -> {
			StringBuffer channel = new StringBuffer();
			channel.append(createTag("title", this.rssTitle));
			channel.append(createTag("link", this.rssLink));
			channel.append(createTag("description", this.rssDescription));
			channel.append(createTag("language", this.rssLanguage));
			channel.append(createTag("pubDate", Util.convertGMTDateFormat(new Date())));
			channel.append(createTag("generator", this.rssGenerator));
			channel.append(createTag("managingEditor", this.rssManagingEditor));
			channel.append(createTag("webMaster", this.rssWebMaster));
			for (Post post : this.posts) {
				channel.append(createTag("item", () -> {
					String link = this.hostName + "/" + post.getIdx() + ".html";
					StringBuffer item = new StringBuffer();
					item.append(createTag("title", post.getTitle()));
					item.append(createTag("link", link));
					item.append(createTag("description", "<![CDATA[" + createDescription(post.getContents()) + "]]>"));
					item.append(createTag("category", getCategoryName(post.getCategory())));
					item.append(createTag("author", this.rssAuthor));
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
