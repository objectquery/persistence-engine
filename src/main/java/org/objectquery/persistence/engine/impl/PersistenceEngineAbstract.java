package org.objectquery.persistence.engine.impl;

import org.objectquery.persistence.engine.ClassFactory;
import org.objectquery.persistence.engine.InstanceKeeper;
import org.objectquery.persistence.engine.PersistenceEngine;
import org.objectquery.persistence.engine.PersistenceException;
import org.objectquery.persistence.engine.PersistenceKeeper;

public abstract class PersistenceEngineAbstract implements PersistenceEngine {

	private ClassFactory classFactory;
	private InstanceKeeper keeper = new InstanceKeeper();

	public PersistenceEngineAbstract(ClassFactory classFactory) {
		this.classFactory = classFactory;
	}

	public void close() {

	}

	protected abstract PersistenceKeeper newRecord(Object id);

	protected abstract PersistenceKeeper loadRecord(Object id);

	public <T> T newInstance(Class<T> class1) {
		return newInstance(class1, null);
	}

	public <T> T newInstance(Class<T> class1, Object id) {
		T instance = createInstance(class1, newRecord(id));
		if (id != null)
			instance = (T) keeper.addInstanceIfNeeded(class1, id, instance);
		return instance;
	}

	private <T> T createInstance(Class<T> class1, PersistenceKeeper keeper) {
		Class<?> clazz = classFactory.getRealClass(class1);
		try {
			T instance = (T) clazz.getConstructor(PersistenceKeeper.class).newInstance(keeper);
			keeper.setInstance(instance);
			return instance;
		} catch (Exception e) {
			throw new PersistenceException(e);
		}
	}

	public <T> T get(Class<T> class1, Object id) {
		Object instance = keeper.getInstanceById(class1, id);
		if (instance == null)
			instance = keeper.addInstanceIfNeeded(class1, id, createInstance(class1, loadRecord(id)));
		return (T) instance;
	}

}
