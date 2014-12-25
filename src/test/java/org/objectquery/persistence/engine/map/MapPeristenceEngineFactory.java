package org.objectquery.persistence.engine.map;

import org.objectquery.persistence.engine.PersistenceEngine;
import org.objectquery.persistence.engine.PersistenceEngineFactory;

public class MapPeristenceEngineFactory extends PersistenceEngineFactory {

	@Override
	protected PersistenceEngine newEngine() {
		return new MapPersistenceEngine(super.newClassFactory());
	}
}
