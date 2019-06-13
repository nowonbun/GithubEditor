package bean;

import java.util.List;

public class MenuBean {
	private String url;
	private String text;
	private String categoryCode;
	private List<MenuBean> subMenu;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public List<MenuBean> getSubMenu() {
		return subMenu;
	}

	public void setSubMenu(List<MenuBean> subMenu) {
		this.subMenu = subMenu;
	}

	public String getCategoryCode() {
		return categoryCode;
	}

	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}

}
