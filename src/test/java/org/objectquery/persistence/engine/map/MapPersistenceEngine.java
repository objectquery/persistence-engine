package org.objectquery.persistence.engine.map;

import org.objectquery.persistence.engine.ClassFactory;
import org.objectquery.persistence.engine.PersistenceKeeper;
import org.objectquery.persistence.engine.impl.MetaClass;
import org.objectquery.persistence.engine.impl.PersistenceEngineAbstract;

public class MapPersistenceEngine extends PersistenceEngineAbstract {

	private MapTestDb db = new MapTestDb();

	public MapPersistenceEngine(ClassFactory classFactory) {
		super(classFactory);
	}

	@Override
	protected PersistenceKeeper loadRecord(MetaClass metaClass, Object id) {
		return new MapPersistenceKeeper(this, metaClass, id, db);
	}

	@Override
	protected PersistenceKeeper newRecord(MetaClass metaClass, Object id) {
		return new MapPersistenceKeeper(this, metaClass, id, db);
	}

	public MapTestDb getDb() {
		return db;
	}

	@Override
	public void close() {
	}
}
