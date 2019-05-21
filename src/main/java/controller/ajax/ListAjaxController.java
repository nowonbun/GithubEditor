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
		try {
			String page = req.getParameter("page");
			String code = req.getParameter("category");
			if (Util.StringIsEmptyOrNull(page) || Util.StringIsEmptyOrNull(code)) {
				throw new RuntimeException();
			}
			int pagenumber = Integer.parseInt(page);
			Category category = FactoryDao.getDao(CategoryDao.class).select(code);
			if (category == null) {
				throw new RuntimeException();
			}
			List<Post> posts = FactoryDao.getDao(PostDao.class).selectByCategory(category, pagenumber * 30, 30);
			List<ListBean> ret = new ArrayList<>();
			for (Post post : posts) {
				ListBean bean = new ListBean();
				bean.setIdx(post.getIdx());
				bean.setTitle(post.getTitle());
				bean.setTags(post.getTag());
				bean.setCreateddate(Util.convertDateFormat(post.getCreateddate()));
				bean.setLastupdateddate(Util.convertDateFormat(post.getLastupdateddate()));
				ret.add(bean);
			}
			//https://stackoverflow.com/questions/240546/remove-html-tags-from-a-string
			//summary

			returnJson(res, ret);
		} catch (Throwable e) {
			res.setStatus(406);
		}
	}
}
