package controller.servlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import common.PropertyMap;

public class InitController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public InitController() {
		super();
	}

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		String path = getServletContext().getRealPath("/");
		//System.out.println(path);
		PropertyMap.getInstance().setWebRootPath(path);
	}
}
