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

	public <T> T newInstance(Class<T> class1) {
		Class<?> clazz = classFactory.getRealClass(class1);
		try {
			return (T) clazz.newInstance();
		} catch (Exception e) {
			throw new PersistenceException(e);
		}
	}

	public <T> T newInstance(Class<T> class1, Object id) {
		T instance = newInstance(class1);
		((PersistentEntity) instance).__set__id(id);
		instance = (T) keeper.addInstanceIfNeeded(id, instance);
		return instance;
	}

	public <T> T get(Class<T> class1, Object id) {
		return (T) keeper.getInstanceById(id);
	}

}
