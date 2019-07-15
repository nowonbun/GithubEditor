package compile;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import common.FactoryDao;
import common.PropertyMap;
import common.Util;
import dao.AttachmentDao;
import dao.CategoryDao;
import model.Attachment;
import model.Category;
import model.Post;

public class TemplateManager extends AbstractManager {
	private String postTemp;
	private String searchTemp;
	private String listTemp;
	private String indexTemp;

	private String title;
	private String menu;

	private String rssWebMaster;
	private String hostname;

	private String imageurl;

	private String twitter;

	public TemplateManager() {
		super();
		
		this.postTemp = PropertyMap.getInstance().getTemplateFile("post");
		this.searchTemp = PropertyMap.getInstance().getTemplateFile("search");
		this.listTemp = PropertyMap.getInstance().getTemplateFile("list");
		this.indexTemp = PropertyMap.getInstance().getTemplateFile("index");

		this.title = PropertyMap.getInstance().getProperty("config", "title");
		this.menu = createMenu();

		this.rssWebMaster = PropertyMap.getInstance().getProperty("config", "rss_webMaster");
		this.hostname = PropertyMap.getInstance().getProperty("config", "host_name");

		this.imageurl = PropertyMap.getInstance().getProperty("config", "imageurl");
		this.twitter = PropertyMap.getInstance().getProperty("config", "twitter");
	}

	public String createListTemp(Category category) {
		String temp = this.listTemp;
		temp = replaceTagForTemplate(temp, "TITLE", this.title + ":: " + getCategoryName(category));
		temp = replaceTagForTemplate(temp, "MENU", this.menu);
		temp = replaceTagForTemplate(temp, "AUTHOR", this.rssWebMaster);
		temp = replaceTagForTemplate(temp, "CANONICAL", this.hostname + "/" + category.getUniqcode() + ".html");
		temp = replaceTagForTemplate(temp, "IMAGEURL", this.imageurl);
		temp = replaceTagForTemplate(temp, "SEARCHTITLE", getCategoryName(category));
		temp = replaceTagForTemplate(temp, "TWITTER", this.twitter);
		temp = replaceTagForTemplate(temp, "SITENAME", this.title);

		StringBuffer sb = new StringBuffer();
		List<Post> posts = new LinkedList<>();
		for (Post post : category.getPosts()) {
			posts.add(0, post);
		}
		for (Post post : posts) {
			if (post.getIsdeleted()) {
				continue;
			}
			sb = createSearchItem(sb, post);
		}
		temp = replaceTagForTemplate(temp, "SEARCHCOUNT", String.valueOf(category.getPosts().size()));
		temp = replaceTagForTemplate(temp, "LIST", sb.toString());
		return temp;
	}

	public String createSearchTemp(List<Post> posts) {
		String temp = this.searchTemp;
		temp = replaceTagForTemplate(temp, "TITLE", this.title);
		temp = replaceTagForTemplate(temp, "MENU", this.menu);
		temp = replaceTagForTemplate(temp, "AUTHOR", this.rssWebMaster);
		temp = replaceTagForTemplate(temp, "CANONICAL", this.hostname + "/search.html");
		temp = replaceTagForTemplate(temp, "IMAGEURL", this.imageurl);
		temp = replaceTagForTemplate(temp, "TWITTER", this.twitter);
		temp = replaceTagForTemplate(temp, "SITENAME", this.title);

		StringBuffer sb = new StringBuffer();
		for (Post post : posts) {
			sb = createSearchItem(sb, post);
		}
		temp = replaceTagForTemplate(temp, "LIST", sb.toString());
		return temp;
	}

	public String createIndexTemp(List<Post> posts) {
		String temp = this.indexTemp;
		temp = replaceTagForTemplate(temp, "TITLE", this.title);
		temp = replaceTagForTemplate(temp, "MENU", this.menu);
		temp = replaceTagForTemplate(temp, "AUTHOR", this.rssWebMaster);
		temp = replaceTagForTemplate(temp, "CANONICAL", this.hostname + "/index.html");
		temp = replaceTagForTemplate(temp, "IMAGEURL", this.imageurl);
		temp = replaceTagForTemplate(temp, "TWITTER", this.twitter);
		temp = replaceTagForTemplate(temp, "SITENAME", this.title);

		StringBuffer sb = new StringBuffer();
		for (Post post : posts) {
			sb = createSearchItem(sb, post);
		}
		temp = replaceTagForTemplate(temp, "LIST", sb.toString());
		return temp;
	}

	public String createPostTemp(Post post) {
		String temp = this.postTemp;
		temp = replaceTagForTemplate(temp, "TITLE", title + " :: " + post.getTitle());
		temp = replaceTagForTemplate(temp, "MENU", menu);
		temp = replaceTagForTemplate(temp, "CONTENTS_TITLE", post.getTitle());
		// temp = replaceTagForTemplate(temp, "CATEGORY_LINK", "./" +
		// post.getCategory().getUniqcode() + ".html");
		temp = replaceTagForTemplate(temp, "CATEGORY_LINK", "./?category=" + post.getCategory().getCode());
		temp = replaceTagForTemplate(temp, "CATEGORY_NAME", getCategoryName(post.getCategory()));
		temp = replaceTagForTemplate(temp, "CREATED_DATE", Util.convertDateFormat(post.getCreateddate()));
		temp = replaceTagForTemplate(temp, "LAST_UPDATED_DATE", Util.convertDateFormat(post.getLastupdateddate()));
		temp = replaceTagForTemplate(temp, "CONTENTS", getContetns(post));
		temp = replaceTagForTemplate(temp, "TAG", convertTag(post.getTag()));

		temp = replaceTagForTemplate(temp, "DESCRIPTION", "<![CDATA[" + createDescription(post.getContents()) + "]]>");
		temp = replaceTagForTemplate(temp, "AUTHOR", this.rssWebMaster);
		temp = replaceTagForTemplate(temp, "CANONICAL", this.hostname + "/" + post.getIdx() + ".html");
		temp = replaceTagForTemplate(temp, "IMAGEURL", this.imageurl);
		temp = replaceTagForTemplate(temp, "TWITTER", this.twitter);
		temp = replaceTagForTemplate(temp, "SITENAME", this.title);
		return temp;
	}

	private StringBuffer createSearchItem(StringBuffer sb, Post post) {
		sb.append("<article class=\"list-item\" data-category-code=\"" + post.getCategory().getCode() + "\">");
		sb.append("<div class=\"list-row pos-right ratio-fixed ratio-4by3 crop-center lts-narrow fouc clearfix searchListEntity\">");
		sb.append("<div class=\"list-body\" style=\"width: 100%;\">");
		sb.append("<div class=\"flexbox\">");
		sb.append("<a class=\"list-link\" href=\"./" + post.getIdx() + ".html\">");
		sb.append("<h5 class=\"list-head ie-nanum ci-link\">" + post.getTitle() + "</h5>");
		sb.append("<p class=\"list-summary\">" + createDescription(post.getContents()) + "</p>");
		sb.append("</a>");
		sb.append("<div class=\"list-meta ie-dotum\">");
		sb.append("<p>");
		sb.append("<a href=\"./search.html?category=" + post.getCategory().getCode() + "\" class=\"p-category ci-color\">" + super.getCategoryName(post.getCategory()) + "</a>");
		sb.append("</p>");
		sb.append("<p>");
		sb.append("<span class=\"timeago ff-h dt-published tag-column\">" + convertTag(post.getTag()) + "</span>");
		sb.append("</p>");
		sb.append("<p>");
		sb.append("<span class=\"data-column-label\">作成日付 :</span>");
		sb.append("<span class=\"timeago ff-h dt-published date-column create-date\">" + Util.convertDateFormat(post.getCreateddate()) + "</span>");
		sb.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; ");
		sb.append("<span class=\"data-column-label\">修正日付	:</span>");
		sb.append("<span class=\"timeago ff-h dt-published date-column update-date\">" + Util.convertDateFormat(post.getLastupdateddate()) + "</span>");
		sb.append("</p>");
		sb.append("</div>");
		sb.append("</div>");
		sb.append("</div>");
		sb.append("</div>");
		sb.append("</article>");
		return sb;
	}

	private String convertTag(String tagcode) {
		try {
			if (!Util.StringIsEmptyOrNull(tagcode)) {
				StringBuffer sb = new StringBuffer();
				String[] tags = tagcode.split(",");
				for (String tag : tags) {
					if (sb.length() > 0) {
						sb.append(",");
					}
					if (tag.indexOf("#") == 0) {
						sb.append("<a href=./?query=" + URLEncoder.encode(tag.substring(1), "UTF-8") + ">");
						sb.append(tag);
						sb.append("</a>");
					} else {
						sb.append(tag);
					}
				}
				return sb.toString();
			} else {
				return "";
			}
		} catch (UnsupportedEncodingException e) {
			getLogger().error(e);
			return "";
		}
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
		return doc.html().replace("<html>\r\n <head></head>\r\n <body>", "").replace("</body>\r\n</html>", "");
	}

	private String replaceTagForTemplate(String template, String tagName, String data) {
		return template.replace("#####" + tagName + "#####", data);
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
					sb.append("<a class=\"link_item link-item-collapse category-item\" href=\"javascript:void(0)\">");
					sb.append(c.getName());
					sb.append("<span class=\"fa fa-chevron-up pull-right\"></span></a>");
					sb.append("<ul class=\"sub_category_list\">");
					for (Category sub : sublist) {
						sb.append("<li class=\"\"><a class=\"link_sub_item category-item\" data-code=\"");
						sb.append(sub.getCode());
						sb.append("\" href=\"");
						sb.append(sub.getUniqcode() + ".html");
						sb.append("\">");
						sb.append(sub.getName());
						sb.append("</a></li>");
					}
					sb.append("</ul>");
				} else {
					sb.append("<a class=\"link_item link-item-collapse category-item\" data-code=\"");
					sb.append(c.getCode());
					sb.append("\" href=\"");
					sb.append(c.getUniqcode() + ".html");
					sb.append("\">");
					sb.append(c.getName());
					sb.append("</a>");
				}
				sb.append("</li>");
			}
			return sb.toString();
		} catch (Throwable e) {
			getLogger().error(e);
			throw new RuntimeException(e);
		}
	}
}
