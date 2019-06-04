package controller.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import common.AbstractController;
import common.FactoryDao;
import dao.PostDao;

@Controller
public class CompileController extends AbstractController {
	@RequestMapping(value = "/compile.html")
	public String compile(ModelMap modelmap, HttpSession session, HttpServletRequest req, HttpServletResponse res) {
		try {
			setMenu(modelmap);
			modelmap.addAttribute("postCount", FactoryDao.getDao(PostDao.class).getCount());
			return "compile";
		} catch (Throwable e) {
			return error();
		}
	}
	
	@RequestMapping(value = "/gitsync.html")
	public String gitsync(ModelMap modelmap, HttpSession session, HttpServletRequest req, HttpServletResponse res) {
		try {
			setMenu(modelmap);
			return "gitsync";
		} catch (Throwable e) {
			return error();
		}
	}
}
