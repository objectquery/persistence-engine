package org.objectquery.persistence.engine;

import java.util.Iterator;
import java.util.ServiceLoader;

import org.objectquery.persistence.engine.impl.JavassistClassFactory;

public class PersistenceEngineFactory {

	public PersistenceEngine createEngine() {
		ServiceLoader<PersistenceEngineFactory> loader = ServiceLoader.load(PersistenceEngineFactory.class);
		Iterator<PersistenceEngineFactory> factories = loader.iterator();
		if (factories == null) {
			throw new PersistenceException("Inpossible to find any persistence engine implementation in the classpath");
		}
		return factories.next().newEngine();
	}

	public void close() {

	}

	protected ClassFactory newClassFactory() {
		return new JavassistClassFactory();
	}

	protected PersistenceEngine newEngine() {
		return null;
	}

}
