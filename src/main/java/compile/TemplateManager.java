package compile;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import bean.ApplicationJson;
import bean.ApplicationJson2;
import common.FactoryDao;
import common.PropertyMap;
import common.Util;
import dao.AttachmentDao;
import dao.CategoryDao;
import dao.PostDao;
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
	private int categorycount;

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
		this.categorycount = PropertyMap.getInstance().getPropertyInt("config", "categorycount");
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
			if (post.getIsdeleted()) {
				continue;
			}
			if (post.getIsreservation()) {
				continue;
			}
			posts.add(0, post);
		}
		int position = 1;
		for (Post post : posts) {
			sb = createSearchItem(sb, post, position++);
		}
		temp = replaceTagForTemplate(temp, "SEARCHCOUNT", String.valueOf(posts.size()));
		temp = replaceTagForTemplate(temp, "LIST", sb.toString());
		ApplicationJson2 aj = new ApplicationJson2(posts);
		String json = Util.getGson().toJson(aj);
		String unicode = Util.convertUnicode(json);
		temp = replaceTagForTemplate(temp, "APPLICATIONJSON", unicode.replace("/", "\\/"));
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
		int position = 1;
		for (Post post : posts) {
			sb = createSearchItem(sb, post, position++);
		}
		temp = replaceTagForTemplate(temp, "LIST", sb.toString());
		ApplicationJson2 aj = new ApplicationJson2(posts);
		String json = Util.getGson().toJson(aj);
		String unicode = Util.convertUnicode(json);
		temp = replaceTagForTemplate(temp, "APPLICATIONJSON", unicode.replace("/", "\\/"));
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
		int position = 1;
		for (Post post : posts) {
			sb = createSearchItem(sb, post, position++);
		}
		temp = replaceTagForTemplate(temp, "LIST", sb.toString());
		ApplicationJson2 aj = new ApplicationJson2(posts);
		String json = Util.getGson().toJson(aj);
		String unicode = Util.convertUnicode(json);
		temp = replaceTagForTemplate(temp, "APPLICATIONJSON", unicode.replace("/", "\\/"));
		return temp;
	}

	public String createPostTemp(Post post, List<Post> posts) {
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
		List<Post> sortedPosts = posts.stream().filter(x -> x.getCategory().getCode().equals(post.getCategory().getCode())).collect(Collectors.toList());
		temp = replaceTagForTemplate(temp, "OTHERCATEGORY", getOtherCategory(post, sortedPosts));
		temp = replaceTagForTemplate(temp, "PRE_POST_ARTICLE", getPrePostArticle(post, sortedPosts));
		
		temp = replaceTagForTemplate(temp, "RECENTLYCATEGORY", getRecentlyPost(posts));
		temp = replaceTagForTemplate(temp, "UNIQIDENTIFIER", "NOWONBUN" + String.format("%05d", post.getIdx()));
		ApplicationJson aj = new ApplicationJson(this.hostname + "/" + post.getIdx() + ".html", title + " :: " + post.getTitle(), createDescription(post.getContents()), post.getCreateddate(), post.getLastupdateddate());
		String json = Util.getGson().toJson(aj);
		String unicode = Util.convertUnicode(json);
		temp = replaceTagForTemplate(temp, "APPLICATIONJSON", unicode.replace("/", "\\/"));
		return temp;
	}
	
	private String getPrePostArticle(Post post, List<Post> posts) {
		if (posts.size() < 2) {
			return "";
		}
		int index = 0;
		for (; index < posts.size(); index++) {
			if (posts.get(index) == post) {
				break;
			}
		}
		StringBuffer sb = new StringBuffer();
		sb.append("<div class=\"pre-post-category\">");
		if (index != posts.size() - 1) {
			sb.append("<div class=\"pre-category\">");
			sb.append("<span>◀前の投稿</span>");
			sb.append("<a href=\"" + this.hostname + "/" + posts.get(index + 1).getIdx() + ".html\" target=\"_blank\">" + posts.get(index + 1).getTitle() + "</a>");
			sb.append("</div>");
		}
		if (index != 0) {
			sb.append("<div class=\"post-category\">");
			sb.append("<span>次の投稿▶</span>");
			sb.append("<a href=\"" + this.hostname + "/" + posts.get(index - 1).getIdx() + ".html\" target=\"_blank\">" + posts.get(index - 1).getTitle() + "</a>");
			sb.append("</div>");
		}
		sb.append("</div>");
		return sb.toString();
	}

	private String getOtherCategory(Post post, List<Post> posts) {
		int index = 0;
		for (; index < posts.size(); index++) {
			if (posts.get(index) == post) {
				break;
			}
		}
		int sindex = 0;
		int eindex = 0;
		if (index - (this.categorycount / 2) > 0) {
			sindex = index - (this.categorycount / 2);
		}
		if (index + (this.categorycount / 2) >= posts.size()) {
			eindex = posts.size() - 1;
			sindex = eindex - this.categorycount -1;
			if(sindex < 0) {
				sindex = 0;
			}
		}
		List<Post> sortedPosts = new ArrayList<>();
		for (int i = sindex; i < posts.size() && i <= eindex; i++) {
			sortedPosts.add(posts.get(i));
		}
		return getRecentlyPost(sortedPosts);
	}

	private String getRecentlyPost(List<Post> posts) {
		StringBuffer sb = new StringBuffer();
		sb.append("<ul>");
		for (int i = 0; i < posts.size() && i < this.categorycount; i++) {
			Post post = posts.get(i);
			sb.append("<li>");
			sb.append("<a href='");
			sb.append(this.hostname + "/" + post.getIdx() + ".html");
			sb.append("'>").append(post.getTitle()).append("</a>");
			sb.append("<span>").append(Util.convertDateFormat(post.getCreateddate())).append("</span>");
			sb.append("</li>");
		}
		sb.append("</ul>");
		return sb.toString();
	}

	private StringBuffer insertTabStringBuffer(StringBuffer sb, int count) {
		for (int i = 0; i < count; i++) {
			sb.append("\t");
		}
		return sb;
	}

	private StringBuffer createSearchItem(StringBuffer sb, Post post, int position) {
		sb.append("<article class=\"list-item\" data-category-code=\"" + post.getCategory().getCode() + "\" property=\"itemListElement\" typeof=\"ListItem\">").append("\r\n");
		insertTabStringBuffer(sb,5).append("<div class=\"list-row pos-right ratio-fixed ratio-4by3 crop-center lts-narrow fouc clearfix searchListEntity\">").append("\r\n");
		insertTabStringBuffer(sb,6).append("<div class=\"list-body\" style=\"width: 100%;\">").append("\r\n");
		insertTabStringBuffer(sb,7).append("<div class=\"flexbox\">").append("\r\n");
		insertTabStringBuffer(sb,8).append("<span property=\"position\" style=\"display:none;\">" + position + "</span>").append("\r\n");
		insertTabStringBuffer(sb,8).append("<a class=\"list-link\" href=\"./" + post.getIdx() + ".html\" property=\"item\" typeof=\"WebPage\">").append("\r\n");
		insertTabStringBuffer(sb,9).append("<h5 class=\"list-head ie-nanum ci-link\" property=\"name\">" + post.getTitle() + "</h5>").append("\r\n");
		insertTabStringBuffer(sb,9).append("<p class=\"list-summary\">" + createDescription(post.getContents()) + "</p>").append("\r\n");
		insertTabStringBuffer(sb,8).append("</a>").append("\r\n");
		insertTabStringBuffer(sb,8).append("<div class=\"list-meta ie-dotum\">").append("\r\n");
		insertTabStringBuffer(sb,9).append("<p>").append("\r\n");
		insertTabStringBuffer(sb,10).append("<a href=\"./search.html?category=" + post.getCategory().getCode() + "\" class=\"p-category ci-color\">" + super.getCategoryName(post.getCategory()) + "</a>").append("\r\n");
		insertTabStringBuffer(sb,9).append("</p>").append("\r\n");
		insertTabStringBuffer(sb,9).append("<p>").append("\r\n");
		insertTabStringBuffer(sb,10).append("<span class=\"timeago ff-h dt-published tag-column\">" + convertTag(post.getTag()) + "</span>").append("\r\n");
		insertTabStringBuffer(sb,9).append("</p>").append("\r\n");
		insertTabStringBuffer(sb,9).append("<p>").append("\r\n");
		insertTabStringBuffer(sb,10).append("<span class=\"data-column-label\">作成日付 :</span>").append("\r\n");
		insertTabStringBuffer(sb,10).append("<span class=\"timeago ff-h dt-published date-column create-date\">" + Util.convertDateFormat(post.getCreateddate()) + "</span>").append("\r\n");
		insertTabStringBuffer(sb,10).append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; ").append("\r\n");
		insertTabStringBuffer(sb,10).append("<span class=\"data-column-label\">修正日付	:</span>").append("\r\n");
		insertTabStringBuffer(sb,10).append("<span class=\"timeago ff-h dt-published date-column update-date\">" + Util.convertDateFormat(post.getLastupdateddate()) + "</span>").append("\r\n");
		insertTabStringBuffer(sb,9).append("</p>").append("\r\n");
		insertTabStringBuffer(sb,8).append("</div>").append("\r\n");
		insertTabStringBuffer(sb,7).append("</div>").append("\r\n");
		insertTabStringBuffer(sb,6).append("</div>").append("\r\n");
		insertTabStringBuffer(sb,5).append("</div>").append("\r\n");
		insertTabStringBuffer(sb,4).append("</article>").append("\r\n");
		sb.append("\r\n");
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
					tag = tag.trim();
					if (tag.indexOf("#") == 0) {
						sb.append("<a href=./search.html?query=" + URLEncoder.encode(tag.substring(1), "UTF-8") + ">");
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
		String html = doc.html();
		html = html.replace("<html>", "");
		html = html.replace("<head>", "");
		html = html.replace("</head>", "");
		html = html.replace("<body>", "");
		html = html.replace("</body>", "");
		html = html.replace("</html>", "");
		return html;
		// return doc.html().replace("<html>\r\n <head></head>\r\n <body>",
		// "").replace("</body>\r\n</html>", "");
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
					int categoryCount = 0;
					for (Category sub : sublist) {
						categoryCount += (int) FactoryDao.getDao(PostDao.class).getCountByCategoryNotReservation(sub);
					}
					sb.append("<span class=\"category-item-count\">(");
					sb.append(categoryCount + (int) FactoryDao.getDao(PostDao.class).getCountByCategoryNotReservation(c));
					sb.append(")</span>");
					sb.append("<span class=\"fa fa-chevron-up pull-right\"></span></a>");
					sb.append("<ul class=\"sub_category_list\">");
					for (Category sub : sublist) {
						sb.append("<li class=\"\"><a class=\"link_sub_item category-item\" data-code=\"");
						sb.append(sub.getCode());
						sb.append("\" href=\"");
						sb.append(sub.getUniqcode() + ".html");
						sb.append("\">");
						sb.append(sub.getName());
						sb.append("<span class=\"category-item-count\">(");
						sb.append((int) FactoryDao.getDao(PostDao.class).getCountByCategoryNotReservation(sub));
						sb.append(")</span>");
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
					sb.append("<span class=\"category-item-count\">(");
					sb.append((int) FactoryDao.getDao(PostDao.class).getCountByCategoryNotReservation(c));
					sb.append(")</span>");
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
