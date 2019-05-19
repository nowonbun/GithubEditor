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
public class ErrorController extends AbstractController {
	@RequestMapping(value = "/error.html", method = RequestMethod.GET)
	public String error(ModelMap modelmap, HttpSession session, HttpServletRequest req, HttpServletResponse res) {
		modelmap.addAttribute("message", "에러가 발생했습니다.<br />관리자에게 문의해 주십시오.");
		return "error";
	}
}
