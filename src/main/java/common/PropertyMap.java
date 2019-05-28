package common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;

public class PropertyMap {
	private static PropertyMap singleton = null;
	private String webContentPath = null;
	private Map<String, String> flyweight = new HashMap<>();

	public static PropertyMap getInstance() {
		if (singleton == null) {
			singleton = new PropertyMap();
		}
		return singleton;
	}

	private Map<String, Properties> map = new Hashtable<>();

	private PropertyMap() {
	}

	public String getProperty(String session, String key) {
		try {
			if (!map.containsKey(session)) {
				Properties pro = new Properties();
				map.put(session, pro);
				ClassLoader cl = Thread.currentThread().getContextClassLoader();
				URL url = cl.getResource(session + ".properties");
				try (InputStream straem = url.openStream()) {
					pro.load(straem);
				}
			}
			Properties property = map.get(session);
			return property.getProperty(key);
		} catch (IOException e) {
			return null;
		}
	}

	public int getPropertyInt(String session, String key) {
		String data = getProperty(session, key);
		try {
			return Integer.parseInt(data);
		} catch (Throwable e) {
			return 0;
		}
	}

	public String getTemplateFile(String templatefileName) {
		try {
			File file = null;
			// TODO: why null Exception???
			synchronized (flyweight) {
				if (!flyweight.containsKey(templatefileName)) {
					ClassLoader cl = Thread.currentThread().getContextClassLoader();
					URL url = cl.getResource(templatefileName + ".tpl.html");
					flyweight.put(templatefileName, url.getFile());
				}
				file = new File(flyweight.get(templatefileName));
			}
			byte[] data = new byte[(int) file.length()];
			try (InputStream straem = new FileInputStream(file)) {
				straem.read(data, 0, data.length);
				return new String(data);
			}
		} catch (Throwable e) {
			e.printStackTrace();
			return null;
		}
	}

	public void setWebRootPath(String path) {
		this.webContentPath = path;
	}

	public String getWebRootPath() {
		return this.webContentPath;
	}
}