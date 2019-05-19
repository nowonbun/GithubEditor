package common;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import bean.UserBean;

public class AbstractController {

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
}
