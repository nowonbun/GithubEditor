package controller.ajax;

import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import bean.StatusBean;
import common.AbstractController;
import common.Util;
import compile.CompileService;

@Controller
public class CompileAjaxController extends AbstractController {

	@RequestMapping(value = "/status.ajax")
	public void status(ModelMap modelmap, HttpSession session, HttpServletRequest req, HttpServletResponse res) {
		StatusBean bean = new StatusBean();
		bean.setStatus(CompileService.getInstance().getStatusToInt());
		bean.setMessage(CompileService.getInstance().getMessage());
		bean.setTime(Util.convertDateFormat(new Date()));
		bean.setProgress(CompileService.getInstance().getProgress());
		returnJson(res, bean);
	}

	@RequestMapping(value = "/compile.ajax")
	public void compile(ModelMap modelmap, HttpSession session, HttpServletRequest req, HttpServletResponse res) {
		CompileService.getInstance().start();
		OKAjax(res);
	}

}
