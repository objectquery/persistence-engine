package org.objectquery.persistence.engine.impl;

import org.objectquery.persistence.engine.ClassFactory;
import org.objectquery.persistence.engine.InstanceKeeper;
import org.objectquery.persistence.engine.PersistenceException;
import org.objectquery.persistence.engine.PersistenceKeeper;

public abstract class PersistenceEngineAbstract implements PersistenceEngineInternal {

	private ClassFactory classFactory;
	private InstanceKeeper keeper = new InstanceKeeper();
	private TransactionEngine transactions = new TransactionEngine();

	public PersistenceEngineAbstract(ClassFactory classFactory) {
		this.classFactory = classFactory;
	}

	protected abstract PersistenceKeeper newRecord(MetaClass metaClass, Object id);

	protected abstract PersistenceKeeper loadRecord(MetaClass metaClass, Object id);

	public <T> T newInstance(Class<T> class1) {
		return newInstance(class1, null);
	}

	@SuppressWarnings("unchecked")
	public <T> T newInstance(Class<T> classInter, Object id) {
		Class<? extends T> realClass = (Class<? extends T>) classFactory.getRealClass(classInter);
		T instance = createInstance(realClass, newRecord(getMetaClass(classInter), id));
		if (id != null)
			instance = (T) keeper.addInstanceIfNeeded(classInter, id, instance);
		return instance;
	}

	private <T> T createInstance(Class<T> clazz, PersistenceKeeper keeper) {
		try {
			T instance = (T) clazz.getConstructor(PersistenceKeeper.class).newInstance(keeper);
			keeper.setInstance(instance);
			return instance;
		} catch (Exception e) {
			throw new PersistenceException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T get(Class<T> classInter, Object id) {
		Class<? extends T> realClass = (Class<? extends T>) classFactory.getRealClass(classInter);
		Object instance = keeper.getInstanceById(classInter, id);
		if (instance == null)
			instance = keeper.addInstanceIfNeeded(classInter, id, createInstance(realClass, loadRecord(getMetaClass(classInter), id)));
		return (T) instance;
	}

	protected MetaClass getMetaClass(Class<?> type) {
		return classFactory.getClassMetadata(type);
	}

	@Override
	public void begin() {
		transactions.begin(this);
	}

	@Override
	public void commit() {
		transactions.commit(this);
	}

	@Override
	public void restore() {
		transactions.restore(this);
	}

	@Override
	public void rollback() {
		transactions.rollback(this);
	}

	@Override
	public void suspend() {
		transactions.suspend(this);

	}

	@Override
	public Transaction current() {
		return transactions.current(this);
	}

	public Transaction newTransaction() {
		return new TransactionImpl();
	}

}
