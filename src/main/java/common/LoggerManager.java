package common;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class LoggerManager {
	private static LoggerManager instance = null;
	private Map<Class<?>, Logger> flyweight = null;

	public static Logger getLogger(Class<?> clazz) {
		if (instance == null) {
			instance = new LoggerManager();
		}
		return instance.get(clazz);
	}

	private LoggerManager() {
		flyweight = new HashMap<>();
		File file = new File(LocalPaths.getClassPath() + File.separator + "log4j.xml");
		try (InputStream stream = new FileInputStream(file)) {
			PropertyConfigurator.configure(stream);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	public Logger get(Class<?> clazz) {
		if (!flyweight.containsKey(clazz)) {
			flyweight.put(clazz, Logger.getLogger(clazz));
		}
		return flyweight.get(clazz);
	}
}
