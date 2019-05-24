
package controller.ajax;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import common.AbstractController;
import common.FactoryDao;
import common.Util;
import dao.AttachmentDao;
import dao.CategoryDao;
import dao.PostDao;
import model.Attachment;
import model.Post;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

@Controller
public class PostAjaxController extends AbstractController {
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
			post.setCategory(FactoryDao.getDao(CategoryDao.class).select(category));

			// Document doc = Jsoup.connect("http://jobc.tistory.com/").get();
			Document doc = Jsoup.parse(contents);
			Elements nodes = doc.select("img[data-filename],a.attachfile[data-filename]");
			post.setAttachments(new ArrayList<>());
			for (Element node : nodes) {
				String attr = null;
				if (node.tagName().equals("img")) {
					attr = node.attr("src");
				}
				if (node.tagName().equals("a")) {
					attr = node.attr("href");
				}
				if (!Util.StringIsEmptyOrNull(attr)) {
					String idx = attr.replace("./getAttachFile.ajax?idx=", "");
					try {
						int id = Integer.parseInt(idx);
						Attachment attachment = FactoryDao.getDao(AttachmentDao.class).select(id);
						post.getAttachments().add(attachment);
						attachment.setPost(post);
					} catch (NumberFormatException e) {

					}
				}
			}
			post.setIsdeleted(false);
			post.setCreateddate(new Date());
			post.setTag(tags);
			post.setContents(contents);
			post.setLastupdateddate(new Date());
			FactoryDao.getDao(PostDao.class).update(post);

			OKAjax(res, "list.html?category=" + post.getCategory().getCode());
			// https://jsoup.org/cookbook/extracting-data/attributes-text-html

			//
			// https://heekim0719.tistory.com/162
			// "<img[^>]*src=[\"']?([^>\"']+)[\"']?[^>]*>"
			// (\<div[^>]+[\>])([^<]*)(\<\/div\>)

		} catch (Throwable e) {
			res.setStatus(406);
		}
	}

	@RequestMapping(value = "/addAttachFile.ajax")
	public void addAttachFile(ModelMap modelmap, HttpSession session, HttpServletRequest req, HttpServletResponse res) {
		try {
			String filename = req.getParameter("filename");
			String type = req.getParameter("type");
			String data = req.getParameter("data");
			if (Util.StringIsEmptyOrNull(data)) {
				throw new RuntimeException();
			}

			Attachment attachment = new Attachment();
			attachment.setType(type);
			attachment.setFilename(filename);
			attachment.setIsdeleted(false);
			attachment.setLastupdateddate(new Date());
			attachment.setData(Base64.getDecoder().decode(data));
			FactoryDao.getDao(AttachmentDao.class).create(attachment);

			OKAjax(res, "./getAttachFile.ajax?idx=" + attachment.getIdx());
		} catch (Throwable e) {
			res.setStatus(406);
		}
	}

	@RequestMapping(value = "/getAttachFile.ajax")
	public void getAttachFile(ModelMap modelmap, HttpSession session, HttpServletRequest req, HttpServletResponse res) {
		try {
			String idx = req.getParameter("idx");
			if (Util.StringIsEmptyOrNull(idx)) {
				throw new RuntimeException();
			}
			int id = Integer.parseInt(idx);
			Attachment attachment = FactoryDao.getDao(AttachmentDao.class).select(id);
			if (attachment != null) {
				if (attachment.getType() != null) {
					String contentType = attachment.getType().replace("data:", "");
					res.setContentType("content-type: " + contentType + "; charset=utf-8");
				}
				res.getOutputStream().write(attachment.getData(), 0, attachment.getData().length);
			}
		} catch (Throwable e) {
			res.setStatus(406);
		}
	}

	@RequestMapping(value = "/modifyPost.ajax")
	public void modifyPost(ModelMap modelmap, HttpSession session, HttpServletRequest req, HttpServletResponse res) {
		try {
			String idx = req.getParameter("idx");
			String title = req.getParameter("title");
			String category = req.getParameter("category");
			String contents = req.getParameter("contents");
			String tags = req.getParameter("tags");
			if (Util.StringIsEmptyOrNull(idx) || Util.StringIsEmptyOrNull(title) || Util.StringIsEmptyOrNull(category)) {
				throw new RuntimeException();
			}
			int id = Integer.parseInt(idx);
			Post post = FactoryDao.getDao(PostDao.class).select(id);
			post.setTitle(title);
			post.setCategory(FactoryDao.getDao(CategoryDao.class).select(category));

			// Document doc = Jsoup.connect("http://jobc.tistory.com/").get();
			Document doc = Jsoup.parse(contents);
			Elements nodes = doc.select("img[data-filename],a.attachfile[data-filename]");
			post.setAttachments(new ArrayList<>());
			for (Element node : nodes) {
				String attr = null;
				if (node.tagName().equals("img")) {
					attr = node.attr("src");
				}
				if (node.tagName().equals("a")) {
					attr = node.attr("href");
				}
				if (!Util.StringIsEmptyOrNull(attr)) {
					String aIdx = attr.replace("./getAttachFile.ajax?idx=", "");
					try {
						int aId = Integer.parseInt(aIdx);
						Attachment attachment = FactoryDao.getDao(AttachmentDao.class).select(aId);
						post.getAttachments().add(attachment);
						attachment.setPost(post);
					} catch (NumberFormatException e) {

					}
				}
			}
			post.setIsdeleted(false);
			post.setTag(tags);
			post.setContents(contents);
			post.setLastupdateddate(new Date());
			FactoryDao.getDao(PostDao.class).update(post);

			OKAjax(res, "list.html?category=" + post.getCategory().getCode());
		} catch (Throwable e) {
			res.setStatus(406);
		}
	}
}