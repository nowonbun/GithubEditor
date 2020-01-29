package compile;

import java.util.concurrent.Callable;

import org.apache.log4j.Logger;

import common.LoggerManager;
import model.Category;

public class AbstractManager {
	private final Logger logger;

	protected AbstractManager() {
		this.logger = LoggerManager.getLogger(this.getClass());
	}

	protected Logger getLogger() {
		return this.logger;
	}

	protected String createDescription(String contents) {
		return createDescription(contents, false);
	}

	protected String createDescription(String contents, boolean isRss) {
		contents = contents.toLowerCase();
		int pos = contents.indexOf("<pre");
		while (pos > -1) {
			int epos = contents.indexOf("</pre>", pos);
			if (epos < 0) {
				break;
			}
			epos += 6;
			String pre = contents.substring(0, pos);
			String after = contents.substring(epos, contents.length());
			contents = pre + System.lineSeparator() + after;
			pos = contents.indexOf("<pre");
		}
		String html;
		if (!isRss) {
			html = contents.replaceAll("<[^>]*>", "");
			html = html.replace("&", "&amp;");
		} else {
			html = contents.replace("&", "&amp;");
		}
		html = html.replace("&nbsp;", "");
		html = html.replace("\n", "");
		html = html.replace("\"", "&quot;");
		html = html.replace("'", "&apos;");
		html = html.replace("<", "&lt;");
		html = html.replace(">", "&gt;");

		String ret = html;
		if (ret.length() > 1020) {
			return ret.substring(0, 1020);
		}
		return ret;
	}

	protected String getCategoryName(Category category) {
		String name = "";
		if (category.getCategory() != null) {
			name += getCategoryName(category.getCategory()) + " / ";
		}
		name += category.getName();
		return name;
	}

	protected String createTag(String tagName, Callable<String> func) {
		try {
			return createTag(tagName, func.call());
		} catch (Throwable e) {
			getLogger().error(e);
			throw new RuntimeException(e);
		}
	}

	protected String createTag(String tagName, String data) {
		return "<" + tagName + ">" + data + "</" + tagName + ">";
	}
}
