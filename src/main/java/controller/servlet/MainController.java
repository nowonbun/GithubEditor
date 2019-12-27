package controller.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
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
import common.LocalPaths;
import common.PropertyMap;
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
		List<Category> pList = categorylist.stream().filter(x -> x.getCategory() == null)
				.sorted((x, y) -> Integer.compare(x.getSeq(), y.getSeq())).collect(Collectors.toList());
		List<SelectBean> selectList = new ArrayList<>();
		for (Category c : pList) {
			List<Category> sublist = categorylist.stream().filter(x -> x.getCategory() == c)
					.sorted((x, y) -> Integer.compare(x.getSeq(), y.getSeq())).collect(Collectors.toList());
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

			if (!Util.StringIsEmptyOrNull(post.getTag())) {
				StringBuffer sb = new StringBuffer();
				String[] tags = post.getTag().split(",");
				for (String tag : tags) {
					if (sb.length() > 0) {
						sb.append(",");
					}
					if (tag.indexOf("#") == 0) {
						sb.append("<a href=./search.html?query=" + URLEncoder.encode(tag.substring(1), "UTF-8") + ">");
						sb.append(tag);
						sb.append("</a>");
					} else {
						sb.append(tag);
					}
				}
				bean.setTags(sb.toString());
			}

			bean.setContents(post.getContents());
			bean.setIsReservation(post.getIsreservation());
			bean.setReservationDate(Util.convertDatepicker(post.getCreateddate()));
			modelmap.addAttribute("post", bean);
			modelmap.addAttribute("categorylist", getCategorySelectList());
			return "post";
		} catch (Throwable e) {
			super.getLogger().error(e);
			return error();
		}
	}
	
	@RequestMapping(value = "/modify.html", method = RequestMethod.POST)
	public String modify(ModelMap modelmap, HttpSession session, HttpServletRequest req, HttpServletResponse res) {
		super.getLogger().info("modify.html");
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
			bean.setTags(post.getTag());
			bean.setContents(post.getContents());
			bean.setIsReservation(post.getIsreservation());
			bean.setReservationDate(Util.convertDatepicker(post.getCreateddate()));
			modelmap.addAttribute("post", bean);
			modelmap.addAttribute("categorylist", getCategorySelectList());
			return "modify";
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

	Pattern pattern = Pattern.compile("\\.(tpl)\\.(html)$");

	@RequestMapping(value = "/template.html", method = RequestMethod.GET)
	public String template(ModelMap modelmap, HttpSession session, HttpServletRequest req, HttpServletResponse res) {
		super.getLogger().info("template.html");
		try {
			setMenu(modelmap);
			String templatePath = PropertyMap.getInstance().getProperty("config", "templatepath");
			if (Util.StringIsEmptyOrNull(templatePath)) {
				templatePath = LocalPaths.getClassPath();
			}
			File dir = new File(templatePath);
			File[] list = dir.listFiles();
			List<String> templateList = new ArrayList<>();
			for (File file : list) {
				if (pattern.matcher(file.getName()).find()) {
					templateList.add(file.getName());
				}
			}
			modelmap.addAttribute("templateList", templateList);
			return "template";
		} catch (Throwable e) {
			super.getLogger().error(e);
			return error();
		}
	}

	@RequestMapping(value = "/templateDetail.html", method = RequestMethod.GET)
	public String templateDetail(ModelMap modelmap, HttpSession session, HttpServletRequest req,
			HttpServletResponse res) {
		super.getLogger().info("templateDetail.html");
		try {
			setMenu(modelmap);
			String temp = req.getParameter("temp");
			if (temp == null) {
				super.getLogger().warn("The parameter of temp is null.");
				throw new RuntimeException();
			}
			String templatePath = PropertyMap.getInstance().getProperty("config", "templatepath");
			if (Util.StringIsEmptyOrNull(templatePath)) {
				templatePath = LocalPaths.getClassPath();
			}
			File file = new File(templatePath + File.separator + temp);
			if (!file.exists()) {
				super.getLogger().warn("The template file is nothing!");
				throw new RuntimeException();
			}
			byte[] data = new byte[(int) file.length()];
			try (FileInputStream stream = new FileInputStream(file)) {
				stream.read(data);
			}
			modelmap.addAttribute("data", new String(data, "UTF-8"));
			modelmap.addAttribute("templateName", temp);
			return "templateDetail";
		} catch (Throwable e) {
			super.getLogger().error(e);
			return error();
		}
	}

	@RequestMapping(value = "/templateModify.html", method = RequestMethod.POST)
	public String templateModify(ModelMap modelmap, HttpSession session, HttpServletRequest req,
			HttpServletResponse res) {
		super.getLogger().info("templateModify.html");
		try {
			String templateName = req.getParameter("templateName");
			if (templateName == null) {
				super.getLogger().warn("The parameter of templateName is null.");
				throw new RuntimeException();
			}
			String templateData = req.getParameter("templateData");
			if (templateData == null) {
				super.getLogger().warn("The parameter of templateData is null.");
				throw new RuntimeException();
			}
			String templatePath = PropertyMap.getInstance().getProperty("config", "templatepath");
			if (Util.StringIsEmptyOrNull(templatePath)) {
				templatePath = LocalPaths.getClassPath();
			}
			File file = new File(templatePath + File.separator + templateName);
			if (!file.exists()) {
				super.getLogger().warn("The template file is nothing!");
				throw new RuntimeException();
			}
			String backFileName = file.getAbsolutePath() + "." + Util.getTimeUnique();
			Util.copyFile(file.getAbsolutePath(), backFileName);
			try (OutputStream outputStream = new FileOutputStream(file)) {
				byte[] buffer = templateData.getBytes();
				outputStream.write(buffer, 0, buffer.length);
			}
			if (!Util.StringEquals(templatePath, LocalPaths.getClassPath())) {
				Util.copyFile(file.getAbsolutePath(), LocalPaths.getClassPath() + File.separator + templateName);
			}
			return redirect("template.html");
		} catch (Throwable e) {
			super.getLogger().error(e);
			return error();
		}
	}
}
