package org.objectquery.persistence.engine.map;

import java.util.HashMap;
import java.util.Map;

import org.objectquery.persistence.engine.impl.MetaClass;
import org.objectquery.persistence.engine.impl.PersistenceKeeperAbstract;

public class MapPersistenceKeeper extends PersistenceKeeperAbstract {

	private MapTestDb db;
	private Map<String, Object> values;

	public MapPersistenceKeeper(MapPersistenceEngine engine, MetaClass metaClass, Object id2, MapTestDb db) {
		super(engine, metaClass, id2);
		this.db = db;
		values = db.getById(id2);
		if (values == null) {
			values = new HashMap<String, Object>();
			db.setById(id2, values);
		}
	}

	@Override
	protected Object loadField(String field) {
		return values.get(field);
	}

	protected void storeField(String filed, Object prev, Object newValue) {
		values.put(filed, newValue);
	}
}
