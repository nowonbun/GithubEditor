package controller.servlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.log4j.Logger;

import common.LocalPaths;
import common.LoggerManager;

public class InitController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Logger logger = null;

	public InitController() {
		super();
	}

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		String path = getServletContext().getRealPath("/");
		LocalPaths.getInstance().setWebRootPath(path);
		if (logger == null) {
			logger = LoggerManager.getLogger(InitController.class);
		}
		logger.info("The program is start.");

	}
}
