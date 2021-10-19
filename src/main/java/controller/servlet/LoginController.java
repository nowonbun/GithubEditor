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
	@RequestMapping(value = { "/", "/index.html" })
	public String index(ModelMap modelmap, HttpSession session, HttpServletRequest req, HttpServletResponse res) {
		super.getLogger().info("index.html");
		try {
			String isLogin = PropertyMap.getInstance().getProperty("config", "login");
			if (!Util.StringIsEmptyOrNull(isLogin) && "false".equals(isLogin.toLowerCase())) {
				super.getLogger().info("The login is pass!");
				session.setAttribute(Define.USER_SESSION_NAME, new UserBean());
			}
			if (super.getCurrentUser(session) != null) {
				super.getLogger().info("This user was already login.");
				return redirect("main.html");
			}
			return "index";
		} catch (Throwable e) {
			super.getLogger().error(e);
			return error();
		}
	}

	@RequestMapping(value = "/login.html", method = RequestMethod.POST)
	public String login(ModelMap modelmap, HttpSession session, HttpServletRequest req, HttpServletResponse res) {
		super.getLogger().info("login.html");
		try {
			String pId = req.getParameter("id");
			String pEmail = req.getParameter("email");
			String id = PropertyMap.getInstance().getProperty("config", "id");
			String email = PropertyMap.getInstance().getProperty("config", "email");
			if ((Util.StringEquals(id, pId) && Util.StringEquals(email, pEmail))
					|| (Util.StringIsEmptyOrNull(id) && Util.StringIsEmptyOrNull(email))) {
				UserBean user = new UserBean();
				user.setId(pId);
				user.setEmail(pEmail);
				super.getLogger().warn("The login was OK.");
				session.setAttribute(Define.USER_SESSION_NAME, user);
				return redirect("main.html");
			}
			super.getLogger().warn("The login was failt.");
			return redirect("loginfailed.html");
		} catch (Throwable e) {
			super.getLogger().error(e);
			return error();
		}
	}

	@RequestMapping(value = "/logout.html", method = RequestMethod.GET)
	public String logout(ModelMap modelmap, HttpSession session, HttpServletRequest req, HttpServletResponse res) {
		super.getLogger().info("logout.html");
		try {
			session.setAttribute(Define.USER_SESSION_NAME, null);
			super.getLogger().info("The user was logout.!");
			return redirect("index.html");
		} catch (Throwable e) {
			super.getLogger().error(e);
			return error();
		}
	}

	@RequestMapping(value = "/loginfailed.html", method = RequestMethod.GET)
	public String loginfailed(ModelMap modelmap, HttpSession session, HttpServletRequest req, HttpServletResponse res) {
		super.getLogger().info("loginfailed.html");
		try {
			modelmap.addAttribute("message", "ログインに失敗しました。");
			return redirect("error.html");
		} catch (Throwable e) {
			super.getLogger().error(e);
			return error();
		}
	}
}