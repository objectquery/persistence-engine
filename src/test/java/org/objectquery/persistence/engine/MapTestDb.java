package org.objectquery.persistence.engine;

import java.util.HashMap;
import java.util.Map;

public class MapTestDb {
	private Map<Object, Map<String, Object>> values = new HashMap<Object, Map<String, Object>>();

	public Map<String, Object> getById(Object id) {
		return values.get(id);
	}

	public void setById(Object id, Map<String, Object> value) {
		values.put(id, value);
	}
}
