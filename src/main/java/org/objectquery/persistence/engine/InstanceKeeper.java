package org.objectquery.persistence.engine;

public class InstanceKeeper {

	private WeakValueHashMap objects = new WeakValueHashMap();

	public Object getInstanceById(Object id) {
		return objects.get(id);
	}

	public Object addInstanceIfNeeded(Object id, Object value) {
		Object val = objects.get(id);
		if (val == null) {
			synchronized (objects) {
				val = objects.get(id);
				if (val == null) {
					objects.put(id, value);
					val = value;
				}
			}
		}
		return val;
	}
}
