package common;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;


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
		LoggerContext context = (org.apache.logging.log4j.core.LoggerContext) LogManager.getContext(false);
		context.setConfigLocation(file.toURI());
		//try (InputStream stream = new FileInputStream(file)) {
		//	PropertyConfigurator.configure(stream);
		//} catch (Throwable e) {
		//	throw new RuntimeException(e);
		//}
	}

	public Logger get(Class<?> clazz) {
		if (!flyweight.containsKey(clazz)) {
			flyweight.put(clazz, LogManager.getLogger(clazz));
		}
		return flyweight.get(clazz);
	}
}
