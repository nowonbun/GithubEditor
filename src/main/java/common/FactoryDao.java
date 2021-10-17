package common;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

public class FactoryDao {
	private static FactoryDao instance = null;
	private Map<Class<?>, AbstractDao<?>> flyweight = null;

	@SuppressWarnings("unchecked")
	public static <T> T getDao(Class<T> clz) {
		try {
			Logger.getLogger("debugger").error("q11");
			if (instance == null) {
				instance = new FactoryDao();
			}
			Logger.getLogger("debugger").error("q12");
			if (instance.flyweight == null) {
				instance.flyweight = new HashMap<Class<?>, AbstractDao<?>>();
			}
			Logger.getLogger("debugger").error("q13");
			if (!instance.flyweight.containsKey(clz)) {
				Logger.getLogger("debugger").error("q14");
				Constructor<T> constructor = clz.getDeclaredConstructor();
				Logger.getLogger("debugger").error("q15");
				constructor.setAccessible(true);
				Logger.getLogger("debugger").error("q16");
				instance.flyweight.put(clz, (AbstractDao<?>) constructor.newInstance());
				Logger.getLogger("debugger").error("q17");
			}
			Logger.getLogger("debugger").error("q18");
			return (T) instance.flyweight.get(clz);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	public static void initializeMaster() {
		resetMaster();
	}

	public static void resetMaster() {

	}
}