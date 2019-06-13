package controller.ajax;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import bean.ListBean;
import common.AbstractController;
import common.FactoryDao;
import common.Util;
import dao.CategoryDao;
import dao.PostDao;
import model.Category;
import model.Post;

@Controller
public class ListAjaxController extends AbstractController {
	@RequestMapping(value = "/list.ajax")
	public void list(ModelMap modelmap, HttpSession session, HttpServletRequest req, HttpServletResponse res) {
		super.getLogger().info("list.ajax");
		try {
			String page = req.getParameter("page");
			String code = req.getParameter("category");
			if (Util.StringIsEmptyOrNull(page) || Util.StringIsEmptyOrNull(code)) {
				super.getLogger().warn("The parameter is null.");
				throw new RuntimeException();
			}
			int pagenumber = Integer.parseInt(page);
			Category category = FactoryDao.getDao(CategoryDao.class).select(code);
			if (category == null) {
				super.getLogger().warn("The category is null.");
				throw new RuntimeException();
			}
			List<Post> posts = FactoryDao.getDao(PostDao.class).selectByCategory(category, pagenumber * 30, 30);
			List<ListBean> ret = new ArrayList<>();
			for (Post post : posts) {
				ListBean bean = new ListBean();
				bean.setIdx(post.getIdx());
				bean.setTitle(post.getTitle());
				bean.setTags(post.getTag());
				bean.setCategoryCode(post.getCategory().getCode());
				bean.setCategoryName(getCategoryName(post.getCategory()));
				// bean.setSummary(Jsoup.parse(post.getContents()).text());
				bean.setSummary(createDescription(post.getContents()));
				bean.setCreateddate(Util.convertDateFormat(post.getCreateddate()));
				bean.setLastupdateddate(Util.convertDateFormat(post.getLastupdateddate()));
				ret.add(bean);
			}
			// https://stackoverflow.com/questions/240546/remove-html-tags-from-a-string
			// summary

			returnJson(res, ret);
		} catch (Throwable e) {
			super.getLogger().error(e);
			res.setStatus(406);
		}
	}

	private String createDescription(String contents) {
		contents = contents.toLowerCase();
		int pos = contents.indexOf("<pre");
		while (pos > -1) {
			int epos = contents.indexOf("</pre>", pos);
			if (epos < 0) {
				break;
			}
			epos += 6;
			String pre = contents.substring(0, pos);
			String after = contents.substring(epos, contents.length());
			contents = pre + System.lineSeparator() + after;
			pos = contents.indexOf("<pre");
		}
		String ret = contents.replaceAll("<[^>]*>", "").replace("&nbsp;", "");
		if (ret.length() > 1020) {
			return ret.substring(0, 1020);
		}
		return ret;
	}

	protected String getCategoryName(Category category) {
		String name = "";
		if (category.getCategory() != null) {
			name += getCategoryName(category.getCategory()) + " / ";
		}
		name += category.getName();
		return name;
	}
}
