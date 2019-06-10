package compile;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import bean.MenuBean;
import common.FactoryDao;
import common.LoggerManager;
import common.PropertyMap;
import dao.CategoryDao;
import model.Category;

public class TemplateManager {
	private String mainTemp;
	private String listTemp;
	private String postTemp;
	private String searchTemp;

	private String title;
	private String menu;
	private final Logger logger;

	public TemplateManager() {
		this.mainTemp = PropertyMap.getInstance().getTemplateFile("main");
		this.listTemp = PropertyMap.getInstance().getTemplateFile("list");
		this.postTemp = PropertyMap.getInstance().getTemplateFile("post");
		this.searchTemp = PropertyMap.getInstance().getTemplateFile("search");

		this.title = PropertyMap.getInstance().getProperty("config", "title");
		this.menu = createMenu();
		logger = LoggerManager.getLogger(TemplateManager.class);
	}

	public String getMain() {
		String temp = this.mainTemp;
		temp = replaceTagForTemplate(temp, "TITLE", this.title);
		temp = replaceTagForTemplate(temp, "MENU", this.menu);
		return temp;
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
