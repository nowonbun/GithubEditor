    
package controller.ajax;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class TestController {
	@RequestMapping(value = "/test.ajax")
	public void test(ModelMap modelmap, HttpSession session, HttpServletRequest req, HttpServletResponse res) {
		try {
			System.out.println("hello wrold");
			res.getWriter().println("hello world");
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}