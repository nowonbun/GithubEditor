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
import gitsync.GitSyncService;

@Controller
public class CompileAjaxController extends AbstractController {

	@RequestMapping(value = "/status.ajax")
	public void status(ModelMap modelmap, HttpSession session, HttpServletRequest req, HttpServletResponse res) {
		super.getLogger().info("status.ajax");
		try {
			StatusBean bean = new StatusBean();
			bean.setStatus(CompileService.getInstance().getStatusToInt());
			bean.setMessage(CompileService.getInstance().getMessage());
			bean.setTime(Util.convertDateFormat(new Date()));
			bean.setProgress(CompileService.getInstance().getProgress());
			returnJson(res, bean);
		} catch (Throwable e) {
			super.getLogger().error(e);
			res.setStatus(406);
		}
	}

	@RequestMapping(value = "/compile.ajax")
	public void compile(ModelMap modelmap, HttpSession session, HttpServletRequest req, HttpServletResponse res) {
		super.getLogger().info("compile.ajax");
		try {
			CompileService.getInstance().start();
			OKAjax(res);
		} catch (Throwable e) {
			super.getLogger().error(e);
			res.setStatus(406);
		}
	}

	@RequestMapping(value = "/gitstatus.ajax")
	public void gitstatus(ModelMap modelmap, HttpSession session, HttpServletRequest req, HttpServletResponse res) {
		super.getLogger().info("gitstatus.ajax");
		try {
			StatusBean bean = new StatusBean();
			bean.setMessage(GitSyncService.getInstance().getMessage());
			bean.setTime(Util.convertDateFormat(new Date()));
			bean.setStatus(GitSyncService.getInstance().isStart() ? 1 : 0);
			returnJson(res, bean);
		} catch (Throwable e) {
			super.getLogger().error(e);
			res.setStatus(406);
		}
	}

	@RequestMapping(value = "/gitsync.ajax")
	public void gitsync(ModelMap modelmap, HttpSession session, HttpServletRequest req, HttpServletResponse res) {
		super.getLogger().info("gitsync.ajax");
		try {
			GitSyncService.getInstance().start();
			OKAjax(res);
		} catch (Throwable e) {
			super.getLogger().error(e);
			res.setStatus(406);
		}
	}
}
