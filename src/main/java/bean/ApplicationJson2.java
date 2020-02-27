package bean;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.SerializedName;
import model.Post;

@SuppressWarnings("unused")
public class ApplicationJson2 {
	@SerializedName("@context")
	private String context = "http://schema.org";
	@SerializedName("@type")
	private String type = "BreadcrumbList";
	private List<ApplicationJson_itemListElement> itemListElement = new ArrayList<>();

	class ApplicationJson_item {
		@SerializedName("@id")
		private String id;
		private String name;

		public ApplicationJson_item(String id, String name) {
			this.id = id;
			this.name = name;
		}
	}

	class ApplicationJson_itemListElement {
		@SerializedName("@type")
		private String type = "ListItem";
		private int position;
		private ApplicationJson_item item;

		public ApplicationJson_itemListElement(String url, String title, int position) {
			this.position = position;
			item = new ApplicationJson_item(url, title);
		}
	}

	public ApplicationJson2(List<Post> posts) {
		int position = 1;
		for (Post post : posts) {
			itemListElement.add(
					new ApplicationJson_itemListElement("./" + post.getIdx() + ".html", post.getTitle(), position++));
		}
	}
}
