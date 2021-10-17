package common;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.Logger;
import org.springframework.ui.ModelMap;

import com.google.gson.Gson;

import bean.AjaxBean;
import bean.MenuBean;
import bean.UserBean;
import dao.CategoryDao;
import dao.PostDao;
import model.Category;

public class AbstractController {

	private Gson GSON = null;
	private Logger logger = null;

	protected Logger getLogger() {
		if (logger == null) {
			logger = LoggerManager.getLogger(this.getClass());
		}
		return logger;
	}

	protected Cookie[] getCookies(HttpServletRequest request) {
		return request.getCookies();
	}

	protected Cookie getCookie(HttpServletRequest request, String name) {
		return Util.searchArray(getCookies(request), (node) -> {
			return Util.StringEquals(name, node.getName());
		});
	}

	protected UserBean getCurrentUser(HttpSession session) {
		return (UserBean) session.getAttribute(Define.USER_SESSION_NAME);
	}

	protected String redirect(String url) {
		return "redirect:" + url;
	}

	protected String error() {
		return redirect("./error.html");
	}

	protected void returnString(HttpServletResponse res, Object data) {
		try {
			res.setContentType("content-type: application/text; charset=utf-8");
			res.getWriter().println(data.toString());
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	protected Gson getGson() {
		if (GSON == null) {
			GSON = new Gson();
		}
		return GSON;
	}

	protected void returnJson(HttpServletResponse res, Object data) {
		try {
			res.setContentType("content-type: application/json; charset=utf-8");
			res.getWriter().println(getGson().toJson(data));
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	protected void OKAjax(HttpServletResponse res) {
		AjaxBean bean = new AjaxBean();
		bean.setRet(true);
		returnJson(res, bean);
	}

	protected void NGAjax(HttpServletResponse res) {
		AjaxBean bean = new AjaxBean();
		bean.setRet(false);
		returnJson(res, bean);
	}

	protected void OKAjax(HttpServletResponse res, String message) {
		AjaxBean bean = new AjaxBean();
		bean.setRet(true);
		bean.setMessage(message);
		returnJson(res, bean);
	}

	protected void NGAjax(HttpServletResponse res, String message) {
		AjaxBean bean = new AjaxBean();
		bean.setRet(false);
		bean.setMessage(message);
		returnJson(res, bean);
	}

	protected void setMenu(ModelMap modelmap) {
		try {
			logger.error("test1");
			List<Category> categorylist = FactoryDao.getDao(CategoryDao.class).selectAll();
			logger.error("test2");
			List<Category> pList = categorylist.stream().filter(x -> x.getCategory() == null).sorted((x, y) -> Integer.compare(x.getSeq(), y.getSeq())).collect(Collectors.toList());
			logger.error("test3");
			List<MenuBean> selectList = new ArrayList<>();
			logger.error("test4");
			for (Category c : pList) {
				MenuBean bean = new MenuBean();
				bean.setUrl("list.html?category=" + c.getCode());
				bean.setText(c.getName());
				bean.setCategoryCode(c.getCode());
				int categoryCount = 0;
				List<Category> sublist = categorylist.stream().filter(x -> x.getCategory() == c).sorted((x, y) -> Integer.compare(x.getSeq(), y.getSeq())).collect(Collectors.toList());
				if (sublist.size() > 0) {
					bean.setSubMenu(new ArrayList<>());
					for (Category sub : sublist) {
						MenuBean subBean = new MenuBean();
						subBean.setUrl("list.html?category=" + sub.getCode());
						subBean.setText(sub.getName());
						subBean.setCategoryCode(sub.getCode());
						bean.getSubMenu().add(subBean);
						int count = (int)FactoryDao.getDao(PostDao.class).getCountByCategory(sub);
						categoryCount += count;
						subBean.setCount(count);
					}
					
				}
				bean.setCount(categoryCount+ (int)FactoryDao.getDao(PostDao.class).getCountByCategory(c));
				selectList.add(bean);
			}
			logger.error("test5");
			modelmap.addAttribute("menulist", selectList);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}
}
