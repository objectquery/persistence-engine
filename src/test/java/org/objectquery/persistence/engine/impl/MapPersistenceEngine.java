package org.objectquery.persistence.engine.impl;

import java.util.HashMap;
import java.util.Map;

import org.objectquery.persistence.engine.ClassFactory;
import org.objectquery.persistence.engine.MapTestDb;
import org.objectquery.persistence.engine.PersistenceEngineAbstract;
import org.objectquery.persistence.engine.PersistenceKeeper;

public class MapPersistenceEngine extends PersistenceEngineAbstract {

	private MapTestDb db;

	public MapPersistenceEngine(ClassFactory classFactory) {
		super(classFactory);
	}

	@Override
	protected PersistenceKeeper loadRecord(Object id) {
		return new MapPersistenceKeeper(id, db);
	}

	@Override
	protected PersistenceKeeper newRecord(Object id) {
		return new MapPersistenceKeeper(id, db);
	}
}
