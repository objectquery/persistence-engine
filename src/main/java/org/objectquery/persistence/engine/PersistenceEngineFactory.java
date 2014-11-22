package org.objectquery.persistence.engine;

import java.util.Iterator;

import javax.imageio.spi.ServiceRegistry;

public class PersistenceEngineFactory {

	public PersistenceEngine createEngine() {
		Iterator<PersistenceEngineFactory> factories = ServiceRegistry.lookupProviders(PersistenceEngineFactory.class);
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
