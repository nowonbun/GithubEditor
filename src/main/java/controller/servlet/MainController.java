package controller.servlet;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import bean.PostBean;
import bean.SelectBean;
import common.AbstractController;
import common.FactoryDao;
import common.Util;
import dao.CategoryDao;
import dao.PostDao;
import model.Category;
import model.Post;

@Controller
public class MainController extends AbstractController {
	@RequestMapping(value = "/main.html", method = RequestMethod.GET)
	public String main(ModelMap modelmap, HttpSession session, HttpServletRequest req, HttpServletResponse res) {
		super.getLogger().info("main.html");
		try {
			setMenu(modelmap);
			return "main";
		} catch (Throwable e) {
			super.getLogger().error(e);
			return error();
		}
	}

	private List<SelectBean> getCategorySelectList() {
		List<Category> categorylist = FactoryDao.getDao(CategoryDao.class).selectAll();
		List<Category> pList = categorylist.stream().filter(x -> x.getCategory() == null).sorted((x, y) -> Integer.compare(x.getSeq(), y.getSeq())).collect(Collectors.toList());
		List<SelectBean> selectList = new ArrayList<>();
		for (Category c : pList) {
			List<Category> sublist = categorylist.stream().filter(x -> x.getCategory() == c).sorted((x, y) -> Integer.compare(x.getSeq(), y.getSeq())).collect(Collectors.toList());
			if (sublist.size() > 0) {
				for (Category s : sublist) {
					SelectBean bean = new SelectBean();
					bean.setText(c.getName() + " / " + s.getName());
					bean.setValue(s.getCode());
					selectList.add(bean);
				}
			} else {
				SelectBean bean = new SelectBean();
				bean.setText(c.getName());
				bean.setValue(c.getCode());
				selectList.add(bean);
			}
		}
		return selectList;
	}

	@RequestMapping(value = "/write.html", method = RequestMethod.GET)
	public String write(ModelMap modelmap, HttpSession session, HttpServletRequest req, HttpServletResponse res) {
		super.getLogger().info("write.html");
		try {
			setMenu(modelmap);
			modelmap.addAttribute("categorylist", getCategorySelectList());
			return "write";
		} catch (Throwable e) {
			super.getLogger().error(e);
			return error();
		}
	}

	@RequestMapping(value = "/list.html", method = RequestMethod.GET)
	public String list(ModelMap modelmap, HttpSession session, HttpServletRequest req, HttpServletResponse res) {
		super.getLogger().info("list.html");
		try {
			setMenu(modelmap);
			String code = req.getParameter("category");
			if (code == null) {
				super.getLogger().warn("The parameter of category is null.");
				throw new RuntimeException();
			}
			Category category = FactoryDao.getDao(CategoryDao.class).select(code);
			if (category == null) {
				super.getLogger().warn("The category is null by that get the code.");
				throw new RuntimeException();
			}
			String title = getCategoryName(category);
			long count = FactoryDao.getDao(PostDao.class).getCountByCategory(category);

			modelmap.addAttribute("title", title);
			modelmap.addAttribute("count", count);
			modelmap.addAttribute("pageMax", (int) (count / 30) + 1);
			modelmap.addAttribute("category", category.getCode());
			return "list";
		} catch (Throwable e) {
			super.getLogger().error(e);
			return error();
		}
	}
	@RequestMapping(value = "/search.html", method = RequestMethod.GET)
	public String search(ModelMap modelmap, HttpSession session, HttpServletRequest req, HttpServletResponse res) {
		super.getLogger().info("search.html");
		try {
			setMenu(modelmap);
			String query = req.getParameter("query");
			if (query == null) {
				super.getLogger().warn("The parameter of query is null.");
				throw new RuntimeException();
			}
			String title = query;
			long count = FactoryDao.getDao(PostDao.class).getCountByTitleLike(query);
			

			modelmap.addAttribute("title", title);
			modelmap.addAttribute("count", count);
			modelmap.addAttribute("pageMax", (int) (count / 30) + 1);
			modelmap.addAttribute("query", query);
			return "list";
		} catch (Throwable e) {
			super.getLogger().error(e);
			return error();
		}
	}

	@RequestMapping(value = "/post.html", method = RequestMethod.GET)
	public String post(ModelMap modelmap, HttpSession session, HttpServletRequest req, HttpServletResponse res) {
		super.getLogger().info("post.html");
		try {
			setMenu(modelmap);
			String idx = req.getParameter("idx");
			if (idx == null) {
				super.getLogger().warn("The parameter of idx is null.");
				throw new RuntimeException();
			}
			int id = Integer.parseInt(idx);
			Post post = FactoryDao.getDao(PostDao.class).select(id);
			if (post == null) {
				super.getLogger().warn("The post is null by that get the id.");
				throw new RuntimeException();
			}
			PostBean bean = new PostBean();
			bean.setIdx(post.getIdx());
			// TODO: Null Eception was occurrd.
			bean.setCategoryCode(post.getCategory().getCode());
			bean.setCategoryUrl("./list.html?category=" + post.getCategory().getCode());
			bean.setCategoryName(getCategoryName(post.getCategory()));
			bean.setCreateDate(Util.convertDateFormat(post.getCreateddate()));
			if (post.getLastupdateddate() != null) {
				bean.setLastUpdateDate(Util.convertDateFormat(post.getLastupdateddate()));
			}
			bean.setTitle(post.getTitle());
			
			if(!Util.StringIsEmptyOrNull(post.getTag())) {
				StringBuffer sb = new StringBuffer();
				String[] tags = post.getTag().split(",");
				for(String tag : tags) {
					if(sb.length() > 0) {
						sb.append(",");
					}
					if(tag.indexOf("#") == 0) {
						sb.append("<a href=./search.html?query="+URLEncoder.encode(tag.substring(1), "UTF-8") +">");
						sb.append(tag);
						sb.append("</a>");
					} else {
						sb.append(tag);
					}
				}
				bean.setTags(sb.toString());
			}
			
			//bean.setTags(post.getTag());
			bean.setContents(post.getContents());
			modelmap.addAttribute("post", bean);
			modelmap.addAttribute("data", getGson().toJson(bean));
			modelmap.addAttribute("categorylist", getCategorySelectList());
			return "post";
		} catch (Throwable e) {
			super.getLogger().error(e);
			return error();
		}
	}

	private String getCategoryName(Category category) {
		String name = "";
		if (category.getCategory() != null) {
			name += getCategoryName(category.getCategory()) + " / ";
		}
		name += category.getName();
		return name;
	}
}
