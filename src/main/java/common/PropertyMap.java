package common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;

public class PropertyMap {
	private static PropertyMap singleton = null;

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
				File file = new File(LocalPaths.getClassPath() + File.separator + session + ".properties");
				try (InputStream straem = new FileInputStream(file)) {
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
			File file = new File(LocalPaths.getClassPath() + File.separator + templatefileName + ".tpl.html");
			byte[] data = new byte[(int) file.length()];
			try (InputStream straem = new FileInputStream(file)) {
				straem.read(data, 0, data.length);
				return new String(data, "UTF-8");
			}
		} catch (Throwable e) {
			e.printStackTrace();
			return null;
		}
	}

}