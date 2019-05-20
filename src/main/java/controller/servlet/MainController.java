package controller.servlet;

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

import bean.SelectBean;
import common.AbstractController;
import common.FactoryDao;
import dao.CategoryDao;
import model.Category;

@Controller
public class MainController extends AbstractController {
	@RequestMapping(value = "/main.html", method = RequestMethod.GET)
	public String main(ModelMap modelmap, HttpSession session, HttpServletRequest req, HttpServletResponse res) {
		try {
			setMenu(modelmap);
			return "main";
		} catch (Throwable e) {
			return error();
		}
	}

	@RequestMapping(value = "/write.html", method = RequestMethod.GET)
	public String write(ModelMap modelmap, HttpSession session, HttpServletRequest req, HttpServletResponse res) {
		try {
			setMenu(modelmap);
			List<Category> categorylist = FactoryDao.getDao(CategoryDao.class).selectAll();
			List<Category> pList = categorylist.stream().filter(x -> x.getCategory() == null).sorted((x, y) -> Integer.compare(x.getSeq(), y.getSeq())).collect(Collectors.toList());
			List<SelectBean> selectList = new ArrayList<>();
			for (Category c : pList) {
				List<Category> sublist = categorylist.stream().filter(x -> x.getCategory() == c).sorted((x, y) -> Integer.compare(x.getSeq(), y.getSeq())).collect(Collectors.toList());
				if (sublist.size() > 0) {
					for (Category s : sublist) {
						SelectBean bean = new SelectBean();
						bean.setText(c.getName() +" / "+ s.getName());
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
			modelmap.addAttribute("categorylist", selectList);
			return "write";
		} catch (Throwable e) {
			System.out.println(e);
			return error();
		}
	}

}
