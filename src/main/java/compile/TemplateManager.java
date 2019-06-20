package compile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import bean.MenuBean;
import common.FactoryDao;
import common.PropertyMap;
import common.Util;
import dao.AttachmentDao;
import dao.CategoryDao;
import model.Attachment;
import model.Category;
import model.Post;

public class TemplateManager extends AbstractManager {
	private String mainTemp;
	private String postTemp;

	private String title;
	private String menu;

	public TemplateManager() {
		super();
		this.mainTemp = PropertyMap.getInstance().getTemplateFile("main");
		this.postTemp = PropertyMap.getInstance().getTemplateFile("post");

		this.title = PropertyMap.getInstance().getProperty("config", "title");
		this.menu = createMenu();

	}

	public String createMainTemp() {
		String temp = this.mainTemp;
		temp = replaceTagForTemplate(temp, "TITLE", this.title);
		temp = replaceTagForTemplate(temp, "MENU", this.menu);
		return temp;
	}

	public String createPostTemp(Post post) {
		String temp = this.postTemp;
		temp = replaceTagForTemplate(temp, "TITLE", title + " :: " + post.getTitle());
		temp = replaceTagForTemplate(temp, "MENU", menu);
		temp = replaceTagForTemplate(temp, "CONTENTS_TITLE", post.getTitle());
		//temp = replaceTagForTemplate(temp, "CATEGORY_LINK", "./" + post.getCategory().getUniqcode() + ".html");
		temp = replaceTagForTemplate(temp, "CATEGORY_LINK", "./?category=" + post.getCategory().getCode());
		temp = replaceTagForTemplate(temp, "CATEGORY_NAME", getCategoryName(post.getCategory()));
		temp = replaceTagForTemplate(temp, "CREATED_DATE", Util.convertDateFormat(post.getCreateddate()));
		temp = replaceTagForTemplate(temp, "LAST_UPDATED_DATE", Util.convertDateFormat(post.getLastupdateddate()));
		temp = replaceTagForTemplate(temp, "CONTENTS", getContetns(post));
		temp = replaceTagForTemplate(temp, "TAG", post.getTag());
		return temp;
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
					sb.append("<span class=\"fa fa-chevron-down pull-right\"></span></a>");
					sb.append("<ul class=\"sub_category_list off\">");
					for (Category sub : sublist) {
						sb.append("<li class=\"\"><a class=\"link_sub_item category-item\" data-code=\"");
						sb.append(sub.getCode());
						sb.append("\" href=\"");
						// sb.append(sub.getUniqcode() + ".html");
						sb.append("./?category=" + sub.getCode());
						sb.append("\">");
						sb.append(sub.getName());
						sb.append("</a></li>");
					}
					sb.append("</ul>");
				} else {
					sb.append("<a class=\"link_item link-item-collapse category-item\" data-code=\"");
					sb.append(c.getCode());
					sb.append("\" href=\"");
					// sb.append(c.getUniqcode() + ".html");
					sb.append("./?category=" + c.getCode());
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
			getLogger().error(e);
			throw new RuntimeException(e);
		}
	}
}
