package bean;

import java.util.Date;
import com.google.gson.annotations.SerializedName;

import common.PropertyMap;
import common.Util;

@SuppressWarnings("unused")
public class ApplicationJson {
	class ApplicationJson_mainEntityOfPage {
		@SerializedName("@id")
		private String id;

		public ApplicationJson_mainEntityOfPage(String id) {
			this.id = id;
		}
	}

	class ApplicationJson_author {
		@SerializedName("@type")
		private String type = "Person";
		private String name;

		public ApplicationJson_author() {
			this.name = PropertyMap.getInstance().getProperty("config", "rss_webMaster");
		}
	}

	class ApplicationJson_image {
		@SerializedName("@type")
		private String type = "ImageObject";
		private String url;
		private String width;
		private String height;

		public ApplicationJson_image() {
			this.url = PropertyMap.getInstance().getProperty("config", "imageurl");
			this.width = PropertyMap.getInstance().getProperty("config", "image_width");
			this.height = PropertyMap.getInstance().getProperty("config", "image_height");
		}
	}

	class ApplicationJson_publisher {
		@SerializedName("@type")
		private String type = "Organization";
		private String name;
		private ApplicationJson_image logo = new ApplicationJson_image();

		public ApplicationJson_publisher() {
			this.name = PropertyMap.getInstance().getProperty("config", "rss_webMaster");
		}
	}

	@SerializedName("@context")
	private String context = "http://schema.org";
	@SerializedName("@type")
	private String type = "BlogPosting";
	private ApplicationJson_mainEntityOfPage mainEntityOfPage;
	private String url;
	private String headline;
	private String description;
	private ApplicationJson_author author = new ApplicationJson_author();
	private String datePublished;
	private String dateModified;
	private ApplicationJson_publisher publisher = new ApplicationJson_publisher();

	public ApplicationJson(String url, String headline, String description, Date createdate, Date modifiedDate) {
		mainEntityOfPage = new ApplicationJson_mainEntityOfPage(url);
		this.url = url;
		this.headline = headline;
		this.description = description;
		this.datePublished = Util.convertGMT2DateFormat(createdate);
		this.dateModified = Util.convertGMT2DateFormat(modifiedDate);
	}
}
