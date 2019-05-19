package controller.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import common.AbstractController;

@Controller
public class MainController extends AbstractController {
	@RequestMapping(value = "/main.html", method = RequestMethod.GET)
	public String main(ModelMap modelmap, HttpSession session, HttpServletRequest req, HttpServletResponse res) {
		try {
			return "main";
		} catch (Throwable e) {
			return error();
		}
	}

	@RequestMapping(value = "/write.html", method = RequestMethod.GET)
	public String write(ModelMap modelmap, HttpSession session, HttpServletRequest req, HttpServletResponse res) {
		try {
			return "write";
		} catch (Throwable e) {
			return error();
		}
	}

}
