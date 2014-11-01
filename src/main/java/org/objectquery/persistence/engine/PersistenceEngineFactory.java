package org.objectquery.persistence.engine;

public class PersistenceEngineFactory {

	public PersistenceEngine createEngine() {
		return new PersistenceEngineImpl(new JavassistClassFactory());
	}

	public void close() {

	}

}
