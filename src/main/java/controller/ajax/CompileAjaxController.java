package controller.ajax;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import bean.StatusBean;
import common.AbstractController;
import common.PropertyMap;
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
			setStatus(CompileStatus.start, "The compiler will be start.", 1);
			String path = PropertyMap.getInstance().getProperty("config", "gitRoot");
			setStatus(CompileStatus.start, "The git root files will be  all deleted", 5);
			deleteFiles(path);
			setStatus(CompileStatus.start, "The git root files will be initialize", 10);
			File file = new File(path);
			file.mkdir();

			// file create
			createFile(path + "\\index.html", PropertyMap.getInstance().getTemplateFile("main"));

			setStatus(CompileStatus.start, "The compiler was start.", 1);
		});
	}

	private void createFile(String filename, String data) {
		File file = new File(filename);
		byte[] binary = data.getBytes(StandardCharsets.UTF_8);
		try (FileOutputStream output = new FileOutputStream(file)) {
			output.write(binary, 0, binary.length);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	private static List<String> getFiles(String path, List<String> list) {
		if (list == null) {
			list = new ArrayList<>();
		}
		File file = new File(path);

		if (file.isDirectory()) {
			for (File f : file.listFiles()) {
				getFiles(f.getAbsolutePath(), list);
			}
		}
		if (file.isFile()) {
			list.add(file.getAbsolutePath());
		}
		return list;
	}

	private static void deleteFiles(String path) {
		File file = new File(path);

		if (file.isDirectory()) {
			for (File f : file.listFiles()) {
				deleteFiles(f.getAbsolutePath());
			}
			file.delete();
		}
		if (file.isFile()) {
			file.delete();
		}
	}
}
