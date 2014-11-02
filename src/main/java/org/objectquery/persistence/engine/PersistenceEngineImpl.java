package org.objectquery.persistence.engine;

import java.util.Map;
import java.util.Map.Entry;
import java.util.WeakHashMap;

public class PersistenceEngineImpl implements PersistenceEngine {

	private ClassFactory classFactory;
	private InstanceKeeper keeper = new InstanceKeeper();

	public PersistenceEngineImpl(ClassFactory classFactory) {
		this.classFactory = classFactory;
	}

	public void close() {

	}

	protected PersistenceKeeper newRecord(Object id) {
		return new PersistenceKeeperImpl(id);
	}

	public <T> T newInstance(Class<T> class1) {
		return newInstance(class1, null);
	}

	public <T> T newInstance(Class<T> class1, Object id) {
		T instance;
		Class<?> clazz = classFactory.getRealClass(class1);
		try {
			instance = (T) clazz.getConstructor(PersistenceKeeper.class).newInstance(newRecord(id));
		} catch (Exception e) {
			throw new PersistenceException(e);
		}
		// ((PersistentEntity) instance).__set__id(id);
		if (id != null)
			instance = (T) keeper.addInstanceIfNeeded(class1, id, instance);
		return instance;
	}

	public <T> T get(Class<T> class1, Object id) {
		return (T) keeper.getInstanceById(class1, id);
	}

}
