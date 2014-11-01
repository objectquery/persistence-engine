package org.objectquery.persistence.engine;

import java.util.HashMap;
import java.util.Map;

public class InstanceKeeper {

	private Map<Class<?>, WeakValueHashMap> objects = new HashMap<Class<?>, WeakValueHashMap>();

	// private WeakValueHashMap objects = new WeakValueHashMap();

	public Object getInstanceById(Class<?> clazz, Object id) {
		WeakValueHashMap map = getByClass(clazz);
		return map.get(id);
	}

	private WeakValueHashMap getByClass(Class<?> clazz) {
		WeakValueHashMap val = objects.get(clazz);
		if (val == null) {
			synchronized (objects) {
				val = objects.get(clazz);
				if (val == null) {
					val = new WeakValueHashMap();
					objects.put(clazz, val);
				}
			}
		}
		return val;
	}

	public Object addInstanceIfNeeded(Class<?> clazz, Object id, Object value) {
		WeakValueHashMap map = getByClass(clazz);
		Object val = map.get(id);
		if (val == null) {
			synchronized (map) {
				val = map.get(id);
				if (val == null) {
					map.put(id, value);
					val = value;
				}
			}
		}
		return val;
	}
}
