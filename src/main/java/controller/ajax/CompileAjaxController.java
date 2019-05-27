package controller.ajax;

import java.util.Date;
import java.util.concurrent.Executors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import bean.StatusBean;
import common.AbstractController;
import common.Util;

@Controller
public class CompileAjaxController extends AbstractController {

	enum CompileStatus {
		wait, start
	}

	@RequestMapping(value = "/status.ajax")
	public void status(ModelMap modelmap, HttpSession session, HttpServletRequest req, HttpServletResponse res) {
		StatusBean bean = new StatusBean();
		bean.setStatus(getInstance().getStatusToInt());
		bean.setMessage(getInstance().getMessage());
		bean.setTime(Util.convertDateFormat(new Date()));
		bean.setProgress(getInstance().getProgress());
		returnJson(res, bean);
	}

	@RequestMapping(value = "/compile.ajax")
	public void compile(ModelMap modelmap, HttpSession session, HttpServletRequest req, HttpServletResponse res) {
		getInstance().start();
		OKAjax(res);
	}

	private static CompileAjaxController singleton = null;
	private CompileStatus status;
	private String message;
	private int progress;

	public static CompileAjaxController getInstance() {
		if (singleton == null) {
			singleton = new CompileAjaxController();
			singleton.status = CompileStatus.wait;
			singleton.progress = 0;
			singleton.message = "This compiler was ready.";
		}
		return singleton;
	}

	public CompileStatus getStatus() {
		return this.status;
	}

	public String getMessage() {
		return message;
	}

	public int getStatusToInt() {
		return getStatus().ordinal();
	}

	public int getProgress() {
		return progress;
	}

	private void setStatus(CompileStatus status, String message, int progress) {
		synchronized (getInstance()) {
			getInstance().status = status;
			getInstance().message = message;
			getInstance().progress = progress;
		}
	}

	public void start() {
		Executors.newSingleThreadExecutor().execute(() -> {
			setStatus(CompileStatus.start, "The compiler was start.", 1);
			
			
			setStatus(CompileStatus.start, "The compiler was start.", 1);
		});
	}
}
