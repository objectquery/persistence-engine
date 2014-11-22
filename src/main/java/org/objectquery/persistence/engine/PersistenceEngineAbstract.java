package org.objectquery.persistence.engine;

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
		T instance;
		Class<?> clazz = classFactory.getRealClass(class1);
		try {
			instance = (T) clazz.getConstructor(PersistenceKeeper.class).newInstance(newRecord(id));
		} catch (Exception e) {
			throw new PersistenceException(e);
		}
		if (id != null)
			instance = (T) keeper.addInstanceIfNeeded(class1, id, instance);
		return instance;
	}

	public <T> T get(Class<T> class1, Object id) {
		Object instance = keeper.getInstanceById(class1, id);
		if (instance == null)
			instance = keeper.addInstanceIfNeeded(class1, id, loadRecord(id));
		return (T) instance;
	}

}
