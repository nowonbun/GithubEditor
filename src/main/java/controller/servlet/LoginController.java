package controller.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import bean.UserBean;
import common.AbstractController;
import common.Define;
import common.PropertyMap;
import common.Util;

@Controller
public class LoginController extends AbstractController {
	@RequestMapping(value = "/index.html")
	public String index(ModelMap modelmap, HttpSession session, HttpServletRequest req, HttpServletResponse res) {
		try {
			String isLogin = PropertyMap.getInstance().getProperty("config", "login");
			if (!Util.StringIsEmptyOrNull(isLogin) && "false".equals(isLogin.toLowerCase())) {
				session.setAttribute(Define.USER_SESSION_NAME, new UserBean());
			}
			if (super.getCurrentUser(session) != null) {
				return redirect("main.html");
			}
			return "index";
		} catch (Throwable e) {
			return error();
		}
	}

	@RequestMapping(value = "/login.html", method = RequestMethod.POST)
	public String login(ModelMap modelmap, HttpSession session, HttpServletRequest req, HttpServletResponse res) {
		try {
			String pId = req.getParameter("id");
			String pEmail = req.getParameter("email");
			String id = PropertyMap.getInstance().getProperty("config", "id");
			String email = PropertyMap.getInstance().getProperty("config", "email");
			if ((Util.StringEquals(id, pId) && Util.StringEquals(email, pEmail)) || (Util.StringIsEmptyOrNull(id) && Util.StringIsEmptyOrNull(email))) {
				UserBean user = new UserBean();
				user.setId(pId);
				user.setEmail(pEmail);
				session.setAttribute(Define.USER_SESSION_NAME, user);
				return redirect("main.html");
			} else {
			}
			return redirect("loginfailed.html");
		} catch (Throwable e) {
			return error();
		}
	}
	
	@RequestMapping(value = "/logout.html", method = RequestMethod.GET)
	public String logout(ModelMap modelmap, HttpSession session, HttpServletRequest req, HttpServletResponse res) {
		try {
			session.setAttribute(Define.USER_SESSION_NAME, null);
			return redirect("index.html");
		} catch (Throwable e) {
			return error();
		}
	}

	@RequestMapping(value = "/loginfailed.html", method = RequestMethod.GET)
	public String loginfailed(ModelMap modelmap, HttpSession session, HttpServletRequest req, HttpServletResponse res) {
		try {
			modelmap.addAttribute("message", "로그인에 실패하였습니다.");
			return redirect("error.html");
		} catch (Throwable e) {
			return error();
		}
	}
}