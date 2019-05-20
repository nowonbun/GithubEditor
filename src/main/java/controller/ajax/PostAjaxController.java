    
package controller.ajax;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import common.AbstractController;
import common.FactoryDao;
import common.Util;
import dao.CategoryDao;
import model.Post;

@Controller
public class PostAjaxController extends AbstractController{
	@RequestMapping(value = "/createPost.ajax")
	public void createPost(ModelMap modelmap, HttpSession session, HttpServletRequest req, HttpServletResponse res) {
		try {
			String title = req.getParameter("title");
			String category = req.getParameter("category");
			String contents = req.getParameter("contents");
			String tags = req.getParameter("tags");
			if (Util.StringIsEmptyOrNull(title) || Util.StringIsEmptyOrNull(category)) {
				throw new RuntimeException();
			}
			Post post = new Post();
			post.setTitle(title);
			post.setCategory(FactoryDao.getDao(CategoryDao.class).findOne(category));
			//(\<div[^>]+[\>])([^<]*)(\<\/div\>)
			//https://heekim0719.tistory.com/162
			String regex = "<img[^>]*src=[\"']?([^>\"']+)[\"']?[^>]*>";
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(contents);     
			
	        while(matcher.find()){
	        	contents.replaceAll(regex, "<img src='test'>");
	        }

			System.out.println(contents);
			
			OKAjax(res);
			
		} catch (Throwable e) {
			res.setStatus(406);
		}
	}
}