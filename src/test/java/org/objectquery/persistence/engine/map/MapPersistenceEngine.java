package org.objectquery.persistence.engine.map;

import org.objectquery.persistence.engine.ClassFactory;
import org.objectquery.persistence.engine.PersistenceKeeper;
import org.objectquery.persistence.engine.impl.PersistenceEngineAbstract;

public class MapPersistenceEngine extends PersistenceEngineAbstract {

	private MapTestDb db = new MapTestDb();

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
