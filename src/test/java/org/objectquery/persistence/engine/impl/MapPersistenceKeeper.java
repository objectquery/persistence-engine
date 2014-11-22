package org.objectquery.persistence.engine.impl;

import java.util.HashMap;
import java.util.Map;

import org.objectquery.persistence.engine.MapTestDb;
import org.objectquery.persistence.engine.PersistenceKeeperAbstract;

public class MapPersistenceKeeper extends PersistenceKeeperAbstract {

	private Object id;
	private MapTestDb db;
	private Map<String, Object> values;

	public MapPersistenceKeeper(Object id2, MapTestDb db) {
		this.id = id2;
		this.db = db;
	}

	@Override
	protected Object loadField(String field) {
		loadRecord();
		return values.get(field);
	}

	private void loadRecord() {
		if (values == null) {
			values = db.getById(id);
			if (values == null) {
				values = new HashMap<String, Object>();
				db.setById(id, values);
			}
		}
	}

	protected void storeField(String filed, Object prev, Object newValue) {
		loadRecord();
		values.put(filed, newValue);
	}
}
